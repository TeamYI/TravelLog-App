package turn.zio.zara.travel_log;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView user_place;
    TextView user_main_id;
    LocationManager lm;

    private ListViewDialog mDialog;

    SharedPreferences login;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor2;
    SharedPreferences.Editor editor3;
    private SharedPreferences alram;
    SharedPreferences travelStory;
    SharedPreferences smartCost;

    SharedPreferences stepkeep;

    LinearLayout mainPage;
    LinearLayout searchPage;
    LinearLayout myPage;
    LinearLayout serch_view;

    TextView search_Text_view;
    EditText search_Text;
    String sc_Division;


    private ImageView step_log_pic;
    private ImageView profile;

    private BackPressCloseHandler backPressCloseHandler;


    private ArrayList<LocationInfo> steparr;

    String steplogkeep;
    private String[][] parsedata;
    MyAdapter adapter;
    MainAdapter mainapter;
    String user_id;

    DataBaseUrl dataurl = new DataBaseUrl();
    private String prifile_pict;

    Bitmap[] pImage;
    Notification.Builder builder;
    PendingIntent pendingNotificationIntent;
    NotificationManager notificationManager;
    private ImageView mypages;
    private ImageView hompage;
    private ImageView write;
    private ImageView search;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*처음 DB 실행*/

        notificationManager = (NotificationManager) MainActivity.this.getSystemService(MainActivity.this.NOTIFICATION_SERVICE);

        Intent intent1 = new Intent(MainActivity.this.getApplicationContext(), MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


        builder = new Notification.Builder(getApplicationContext());
        pendingNotificationIntent = PendingIntent.getActivity(MainActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        backPressCloseHandler = new BackPressCloseHandler(this);

        user_place = (TextView) findViewById(R.id.user_place_info);
        user_main_id = (TextView) findViewById(R.id.main_user_id);

        mainPage = (LinearLayout) findViewById(R.id.main_page);
        searchPage = (LinearLayout) findViewById(R.id.search_page);
        myPage = (LinearLayout) findViewById(R.id.my_page);
        serch_view = (LinearLayout) findViewById(R.id.serch_view);

        hompage = (ImageView) findViewById(R.id.view_home_icon);
        search = (ImageView) findViewById(R.id.view_search_icon);
        write = (ImageView) findViewById(R.id.view_logWrite_icon);
        mypages = (ImageView) findViewById(R.id.view_mypage_icon);

        ImageView cameraicon = (ImageView) findViewById(R.id.Camera_sel_pop);
        ImageView mainlogo = (ImageView) findViewById(R.id.view_mainlogo_icon);

        /* MainView의 이미지 아이콘 셋팅 */
        cameraicon.setImageDrawable(getResources().getDrawable(R.drawable.camera_off));
        mainlogo.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        hompage.setImageDrawable(getResources().getDrawable(R.drawable.homepage_on));
        search.setImageDrawable(getResources().getDrawable(R.drawable.search_off));
        mypages.setImageDrawable(getResources().getDrawable(R.drawable.mypage_off));
        write.setImageDrawable(getResources().getDrawable(R.drawable.write_off));

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();
        user_id = login.getString("user_id", "0");
        prifile_pict = login.getString("prifile_picture", "default.png");
        String group_code = login.getString("group_code", "0");

        final SmartCostData smartdata = new SmartCostData();

        smartdata.setGroup_code(group_code);
        smartdata.setUser_id(user_id);

        editor.putString("pushAlram", "0");
        editor.commit();

        stepkeep = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor2 = stepkeep.edit();
        steplogkeep = stepkeep.getString("steplogkeep", "0");
        int stepsize = stepkeep.getInt("stepdatasize", 0);

        alram = getSharedPreferences("pushAlram", MODE_PRIVATE);
        editor3 = alram.edit();

        smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
        sc_Division = smartCost.getString("sc_Division", "0");

        search_Text = (EditText) findViewById(R.id.search_Text);
        search_Text_view = (TextView) findViewById(R.id.search_Text_view);

        profile = (ImageView) findViewById(R.id.profile_picture);

        if (!prifile_pict.equals("0")) {
            profile_pic();
        }

        /* 스탭로그가 작성중이면 푸시알림을 주고 위치정보를 배열에 저장.*/
        if (steplogkeep.equals("1")) {
            steparr = new ArrayList<LocationInfo>();
            builder.setSmallIcon(R.drawable.foot).setTicker("StepLog").setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle("Step Log").setContentText("Step Log 작성중...").setOngoing(true)
                    .setContentIntent(pendingNotificationIntent);
            notificationManager.notify(1, builder.build());
            for (int i = 0; i < stepsize; i++) {
                Double latitude = Double.parseDouble(stepkeep.getString("latitude" + i, "0"));
                Double longitude = Double.parseDouble(stepkeep.getString("longitude" + i, "0"));

                steparr.add(new LocationInfo(latitude, longitude));
            }
        } else {
            steparr = new ArrayList<LocationInfo>();
        }

        Log.d("step_log", steplogkeep);
        DBinput();

        user_main_id.setText(user_id);

        /* 위치정보를 100miliSecond마다 읽는다.*/
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    1000, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        } else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    1000, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        }



    }

    /* 서버에 접속하여 내 위치 반경에 예전에 쓴 로그가 있는지 확인.*/
    private void pushalram(String token, double longitude, double latitude) {

        final double Mylongitude = longitude;
        final double Mylatitude = latitude;
        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("와아아아아아", s+"뭘까요");
                if(!s.equals("") && !s.equals("없음")) {
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(s);
                    FirebaseMessagingService.push_text = element.getAsJsonObject().get("question_content").getAsString();
                    FirebaseMessagingService.push_board_code = element.getAsJsonObject().get("board_code").getAsString();
                    FirebaseMessagingService.push_Type = "1";
                    super.onPostExecute(s);

                    Log.d("result", s);
                }
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String token = (String) params[0];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("userDeviceIdKey", token);
                    loginParam.put("longitude", Mylongitude + "");
                    loginParam.put("latitude", Mylatitude + "");
                    loginParam.put("user_id", user_id);

                    String link = dataurl.getServerUrl() + "push_alram"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(loginParam);


                    // HTTP 요청 전송
                    HttpClient post = http.create();
                    post.request();
                    // 응답 상태코드 가져오기
                    int statusCode = post.getHttpStatusCode();
                    // 응답 본문 가져오기
                    String body = post.getBody();
                    return body;

                    // Read Server Res

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }
        insertData task = new insertData();
        task.execute(token);
    }


    /*StepLog Insert*/
    private void StepInsert(String user_id) {

        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);

                    String link = dataurl.getServerUrl() + "stepinsert"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(loginParam);

                    // HTTP 요청 전송
                    HttpClient post = http.create();
                    post.request();
                    // 응답 상태코드 가져오기
                    int statusCode = post.getHttpStatusCode();
                    // 응답 본문 가져오기
                    String body = post.getBody();
                    return body;
                    // Read Server Response

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }
        insertData task = new insertData();
        task.execute(user_id);
    }

    /*메인 db 연결시도*/
    public void DBinput() {
        mainlistAll task = new mainlistAll();
        String result = null;
        try {
            result = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        jsonParse(result);
        serpic setimage = new serpic();
        setimage.execute();
    }

    /*result JSon Parese*/
    public void jsonParse(String s) {
        Log.d("json", s);
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][12];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("board_code");
                parsedata[i][1] = jobject.getString("board_title");
                parsedata[i][2] = jobject.getString("board_content");
                parsedata[i][3] = jobject.getString("log_longtitude");
                parsedata[i][4] = jobject.getString("log_latitude");
                parsedata[i][5] = jobject.getString("board_date");
                parsedata[i][6] = jobject.getString("user_id");
                if (json.getJSONObject(i).isNull("file_content") == false) {
                    parsedata[i][7] = jobject.getString("file_type");
                    parsedata[i][8] = jobject.getString("file_content");
                } else {
                    parsedata[i][7] = "0";
                    parsedata[i][8] = "1";
                }
                if (parsedata[i][7].equals("3")) {
                    parsedata[i][9] = jobject.getString("step_log_code");
                }
                parsedata[i][10] = jobject.getString("write_type");
                if (json.getJSONObject(i).isNull("user_profile") == false) {
                    parsedata[i][11] = jobject.getString("user_profile");
                } else {
                    parsedata[i][11] = prifile_pict;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*메인 gridview에 뿌리기*/
    public void mainApeter(Bitmap[] images) {
        String[] board_code = new String[parsedata.length];
        String[] title = new String[parsedata.length];
        String[] Content = new String[parsedata.length];
        String[] date = new String[parsedata.length];
        String[] writeuser_id = new String[parsedata.length];
        String[] file_type = new String[parsedata.length];
        String[] adress = new String[parsedata.length];
        String[] file_Content = new String[parsedata.length];
        String[] step_log_code = new String[parsedata.length];
        String[] write_type = new String[parsedata.length];
        String[] user_profile = new String[parsedata.length];

        for (int i = 0; i < parsedata.length; i++) {
            board_code[i] = parsedata[i][0];
            title[i] = parsedata[i][1];
            Content[i] = parsedata[i][2];
            date[i] = parsedata[i][5];
            writeuser_id[i] = parsedata[i][6];
            file_type[i] = parsedata[i][7];
            file_Content[i] = parsedata[i][8];
            if (file_type.equals("3")) {
                step_log_code[i] = parsedata[i][9];
            } else {
                step_log_code[i] = "0";
            }
            write_type[i] = parsedata[i][10];
            user_profile[i] = parsedata[i][11];
        }
        Log.d("image", images + "");
        mainapter = new MainAdapter(
                MainActivity.this,
                R.layout.main_log_view, board_code,       // GridView 항목의 레이아웃 row.xml
                title, Content, date, writeuser_id, file_type, adress, file_Content, step_log_code, write_type, user_id, user_profile);
        mainapter.image(images, 1);
        mainapter.pimage(pImage, 1);
        GridView gv = (GridView) findViewById(R.id.main_list);
        gv.setAdapter(mainapter);

    }



    /*gridView 웹서버 이미지 뿌리기*/
    class serpic extends AsyncTask<String, Void, Bitmap[]> {
        ProgressDialog loading;

        ArrayList<String> location = new ArrayList<String>();
        private InputStream is = null;
        private String KMlurl;

        @Override
        protected Bitmap[] doInBackground(String... params) {
            Bitmap[] images = new Bitmap[parsedata.length];
            pImage = new Bitmap[parsedata.length];
            try {
                for (int i = 0; i < parsedata.length; i++) {

                    String purl = dataurl.getProfile() + parsedata[i][11];
                    InputStream iss = (InputStream) new URL(purl).getContent();
                    BitmapFactory.Options optionss = new BitmapFactory.Options();
                    optionss.inSampleSize = 1;
                    optionss.inJustDecodeBounds = false;
                    Bitmap resizedBitmaps = BitmapFactory.decodeStream(iss, null, optionss);
                    pImage[i] = resizedBitmaps;
                    if (parsedata[i][7].equals("3")) {
                        String urltext = dataurl.getStepUrl() + parsedata[i][8];
                        Log.d("KMLurl", urltext);
                        URL url = new URL(urltext);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.connect();
                        File file = new File(urltext);
                        is = urlConnection.getInputStream();

                        InputStreamReader inputReader = new InputStreamReader(is);

                        String column = null;
                        BufferedReader br = new BufferedReader(inputReader);
                        boolean flag = false;
                        while ((column = br.readLine()) != null) {
                            int coordin = column.indexOf("<coordinates>");

                            if (coordin != -1 || flag) {
                                Log.d("폴리라인 그림", "걸러내는중");
                                int j = 0;
                                flag = true;
                                String tmpCoordin = column;
                                tmpCoordin = tmpCoordin.replaceAll("<coordinates>", "");
                                tmpCoordin = tmpCoordin.replaceAll("</coordinates>", "");
                                if (tmpCoordin.trim().equals("</LineString>")) {
                                    break;
                                }
                                location.add(tmpCoordin.trim());
                            }


                        }

                        Log.d("size", location.size() + "");
                        KMlurl = "";
                        KMlurl = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&path=";
                        for (int k = 0; k < location.size(); k++) {
                            String[] coos = location.get(k).toString().split(",");
                            KMlurl += coos[1] + "," + coos[0];
                            if (k != location.size() - 1) {
                                KMlurl += "|";
                            } else {
                                KMlurl += "&sensor=false"; //&key=AIzaSyAF8iTF3JtdLLhprWyASWE8APl6RM6BGBQ
                            }
                            Log.d("dd", coos[1] + "," + coos[0]);
                        }

                        Log.d("result", KMlurl);
                    }
                    if (parsedata[i][7].equals("1")) {
                        String url = dataurl.getTumnailUrl() + parsedata[i][8];
                        Log.d("URLs", url + " / " + i);
                        InputStream is = (InputStream) new URL(url).getContent();
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inSampleSize = 1;
                        options.inJustDecodeBounds = true;
                        final int height = options.outHeight;
                        final int width = options.outWidth;
                        int inSampleSize = 1;

                        if (height > 100 || width > 100) {

                            final int halfHeight = height / 2;
                            final int halfWidth = width / 2;

                            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                            // height and width larger than the requested height and width.
                            while ((halfHeight / inSampleSize) > 100
                                    && (halfWidth / inSampleSize) > 100) {
                                inSampleSize *= 2;
                            }
                        }
                        options.inSampleSize = inSampleSize;
                        options.inJustDecodeBounds = false;

                        Bitmap resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                        images[i] = resizedBitmap;
                    }
                    if (parsedata[i][7].equals("3")) {
                        Log.d("url", parsedata[i][8]);
                        InputStream is = (InputStream) new URL(KMlurl).getContent();
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inSampleSize = 1;
                        options.inJustDecodeBounds = true;
                        final int height = options.outHeight;
                        final int width = options.outWidth;
                        int inSampleSize = 1;

                        if (height > 100 || width > 100) {

                            final int halfHeight = height / 2;
                            final int halfWidth = width / 2;

                            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                            // height and width larger than the requested height and width.
                            while ((halfHeight / inSampleSize) > 100
                                    && (halfWidth / inSampleSize) > 100) {
                                inSampleSize *= 2;
                            }
                        }
                        options.inSampleSize = inSampleSize;
                        options.inJustDecodeBounds = false;

                        Bitmap resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                        images[i] = resizedBitmap;
                    }
                }
                return images;

                // Read Server Response

            } catch (Exception e) {
                return images;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MainActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(Bitmap[] s) {
            super.onPostExecute(s);
                mainApeter(s);

            this.cancel(true);
            loading.dismiss();
        }
    }

    public void backView(View view) {
        search_Text.clearFocus();
    }


    /*메인 클릭시 db시*/
    class mainlistAll extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();
                seldata.put("user_id", user_id);
                DBserver = "main_View_DB";

                String link = dataurl.getServerUrl() + DBserver; //92.168.25.25
                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(seldata);

                // HTTP 요청 전송
                HttpClient post = http.create();
                post.request();
                // 응답 상태코드 가져오기
                int statusCode = post.getHttpStatusCode();
                // 응답 본문 가져오기
                String body = post.getBody();
                return body;

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.cancel(true);
        }
    }

    /*프로필 사진*/
    private void profile_pic() {
        final Bitmap[] resizedBitmaps = new Bitmap[1];
        class write extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                this.cancel(true);
                profile.setImageBitmap(resizedBitmaps[0]);
                profile.setBackground(new ShapeDrawable(new OvalShape()));
                profile.setClipToOutline(true);
            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    String url = dataurl.getProfile() + prifile_pict;
                    Log.d("profile", url);
                    InputStream is = (InputStream) new URL(url).getContent();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = true;
                    final int height = options.outHeight;
                    final int width = options.outWidth;
                    int inSampleSize = 1;

                    if (height > 100 || width > 100) {

                        final int halfHeight = height / 2;
                        final int halfWidth = width / 2;

                        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                        // height and width larger than the requested height and width.
                        while ((halfHeight / inSampleSize) > 100
                                && (halfWidth / inSampleSize) > 100) {
                            inSampleSize *= 2;
                        }
                    }
                    options.inSampleSize = inSampleSize;
                    options.inJustDecodeBounds = false;
                    resizedBitmaps[0] = BitmapFactory.decodeStream(is, null, options);
                    return "success";
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        write task = new write();
        task.execute();
    }

    /* 서버에 접속하여 내 위치 반경에 예전에 쓴 로그가 있는지 확인.*/
    private void travelpush(String token, double longitude, double latitude) {

        final double Mylongitude = longitude;
        final double Mylatitude = latitude;
        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("와아아아아아", s+"뭘까요");
                if(!s.equals("") && !s.equals("없음")) {
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(s);
                    FirebaseMessagingService.push_text = s;
                    FirebaseMessagingService.push_Type = "2";
                    super.onPostExecute(s);

                    Log.d("result", s);
                }
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String token = (String) params[0];
                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("userDeviceIdKey", token);
                    loginParam.put("longitude", Mylongitude + "");
                    loginParam.put("latitude", Mylatitude + "");
                    loginParam.put("user_id", user_id);

                    String link = dataurl.getServerUrl() + "travel_push"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(loginParam);


                    // HTTP 요청 전송
                    HttpClient post = http.create();
                    post.request();
                    // 응답 상태코드 가져오기
                    int statusCode = post.getHttpStatusCode();
                    // 응답 본문 가져오기
                    String body = post.getBody();
                    return body;

                    // Read Server Res

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }
        insertData task = new insertData();
        task.execute(token);
    }

    /*폰의 위치정보를 받아오는 메소드*/
    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
            FirebaseMessaging.getInstance().subscribeToTopic("notice");
            String token = FirebaseInstanceId.getInstance().getToken();

            Log.d("test", "onLocationChanged, location:" + location);

            double longitude = 126.981579; //location.getLongitude(); //경도
            double latitude = 37.568228; //location.getLongitude();   //위도

            MyLocation myLocation = new MyLocation();
            myLocation.setLatitude(latitude);
            myLocation.setLongitude(longitude);
            pushalram(token, longitude, latitude);
            travelpush(token, longitude, latitude);
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            user_place.setText("서울특별시 다동70");
            if (steplogkeep.equals("1")) {
                steparr.add(new LocationInfo(latitude, longitude));
                Log.d("dd", steparr.size() + "");
                Log.d("dd", "dd");
            }
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    /* 위치정보를 주소로 변환 */
    public String getAddress(double lat, double lng) {
        String address = null;

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(35.896533, 128.620363, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) {
            Log.e("getAddress", "주소 데이터 얻기 실패");
            return null;
        }
        if (list.size() > 0) {
            Address addr = list.get(0);
            address = addr.getLocality() + " "
                    + addr.getThoroughfare() + " ";
        }
        return address;
    }

    public void log_Write(View view) {
        LogWriteDialog();
    }



    /*카메라 종류 선택 dialog*/
    public void PictureSel(View v) {
        switch (v.getId()) {
            case R.id.Camera_sel_pop:
                Log.d("TAG", "click button list dialog.......");
                showListDialog();
                break;
        }
    }

    /*글쓰기 버튼 클릿*/
    private void LogWriteDialog() {

        String[] item = getResources().getStringArray(R.array.log_wrtie_item);

        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String>(listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.onOnSetItemClickListener(new ListViewDialog.ListViewDialogSelectListener() {


            @Override

            public void onSetOnItemClickListener(int position) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), Life_LogActivity.class);
                    intent.putExtra("stepLog", steplogkeep);
                    startActivity(intent);
                } else if (position == 1) {
                    stepkeep = getSharedPreferences("LoginKeep", MODE_PRIVATE);
                    editor = stepkeep.edit();
                    steplogkeep = stepkeep.getString("steplogkeep", "0");
                    Log.d("steplog", steplogkeep);
                    if (steplogkeep.equals("0")) {
                        steplogkeep = "1";
                        editor2.putString("steplogkeep", steplogkeep);
                        editor2.commit();
                        StepInsert(user_id);
                        builder.setSmallIcon(R.drawable.foot).setTicker("StepLog").setWhen(System.currentTimeMillis())
                                .setNumber(1).setContentTitle("Step Log").setContentText("Step Log 작성중...").setOngoing(true)
                                .setContentIntent(pendingNotificationIntent);


                        notificationManager.notify(1, builder.build());

                    } else {
                        Intent intent = new Intent(getApplicationContext(), StepLogActivity.class);

                        double[] latitude = new double[steparr.size()];
                        double[] longitude = new double[steparr.size()];
                        for (int i = 0; i < steparr.size(); i++) {
                            latitude[i] = steparr.get(i).getLatitude();
                            longitude[i] = steparr.get(i).getLongitude();
                        }

                        intent.putExtra("user_id", user_id);
                        intent.putExtra("stepsize", steparr.size());
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);

                        startActivityForResult(intent, 3);
                    }
                    Log.d("step_log", steplogkeep);
                } else if (position == 2) {
                    mDialog.dismiss();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }

    /*카메라 버튼 클릭*/
    private void showListDialog() {

        String[] item = getResources().getStringArray(R.array.list_dialog_main_item);

        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String>(listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.onOnSetItemClickListener(new ListViewDialog.ListViewDialogSelectListener() {


            @Override

            public void onSetOnItemClickListener(int position) {
                // TODO Auto-generated method stub

                if (position == 0) {

                    Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(intent);

                } else if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), TravelCameraActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    mDialog.dismiss();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) // 액티비티가 정상적으로 종료되었을 경우
        {
            if (requestCode == 1 || requestCode == 2) // requestCode==1 로 호출한 경우에만 처리.
            {
                editor.clear();
                editor.commit();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("OOMTEST", "onDestroy");
        for(int i = 0; i < pImage.length; i++){
            if(pImage[i] != null) {
                pImage[i].recycle();
            }
        }

        RecycleUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }

    public void viewhomeChange(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }
    public void viewsearchChange(View v){
        Intent intent = new Intent(this, searchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    
    public void viewMypageChange(View v){

        Intent intent = new Intent(this, mypageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /*뒤로가기를 눌렀을때*/
    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
        if (steplogkeep.equals("1")) {

            editor.putInt("stepdatasize", steparr.size()); /*sKey is an array*/
            editor.putString("pushAlram", "0");
            Log.d("step_log", steparr.size() + "");
            for (int i = 0; i < steparr.size(); i++) {
                editor.putString("latitude" + i, steparr.get(i).getLatitude() + "");
                editor.putString("longitude" + i, steparr.get(i).getLongitude() + "");
            }

            editor.commit();
        }
    }
}
