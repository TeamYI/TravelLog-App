package turn.zio.zara.travel_log;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static turn.zio.zara.travel_log.TravelListActivity.joinCode;

public class mypageActivity extends AppCompatActivity {

    TextView user_place;
    TextView user_main_id;
    LocationManager lm;

    private ListViewDialog mDialog;

    SharedPreferences login;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editor2;
    SharedPreferences.Editor editor3;
    SharedPreferences.Editor editor4;
    private SharedPreferences alram;
    SharedPreferences travelStory;
    SharedPreferences smartCost;
    ListViewAdapter Tadapter;

    SharedPreferences stepkeep;
    public String select_group_Code = "";

    LinearLayout mainPage;
    LinearLayout searchPage;
    LinearLayout myPage;
    LinearLayout serch_view;
    LinearLayout mylogvisible;
    LinearLayout travelvisible;

    TextView search_Text_view;
    TextView friendsCount;
    TextView logCount;
    EditText search_Text;
    String sc_Division;

    static String group_code;

    private ImageView step_log_pic;
    private ImageView profile;
    ListView listview;

    private BackPressCloseHandler backPressCloseHandler;
    private String hashTagText;

    private ArrayList<LocationInfo> steparr;
    int placeTime;

    String steplogkeep;
    private String[][] parsedata;
    MyAdapter adapter;
    MainAdapter mainapter;
    String user_id;

    DataBaseUrl dataurl = new DataBaseUrl();
    private String prifile_pict;

    Bitmap[] pImage;
    private ImageView my_page_profile_picture;
    Notification.Builder builder;
    PendingIntent pendingNotificationIntent;
    NotificationManager notificationManager;
    private ImageView mypages;
    private ImageView hompage;
    private ImageView write;
    private ImageView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        notificationManager = (NotificationManager) mypageActivity.this.getSystemService(mypageActivity.this.NOTIFICATION_SERVICE);
        placeTime = 1000;
        backPressCloseHandler = new BackPressCloseHandler(this);

        Intent intent1 = new Intent(mypageActivity.this.getApplicationContext(), mypageActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        builder = new Notification.Builder(getApplicationContext());
        pendingNotificationIntent = PendingIntent.getActivity(mypageActivity.this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);


        user_place = (TextView) findViewById(R.id.user_place_info);
        user_main_id = (TextView) findViewById(R.id.main_user_id);
        TextView my_page_user_id = (TextView) findViewById(R.id.my_page_user_id);

        mainPage = (LinearLayout) findViewById(R.id.main_page);
        searchPage = (LinearLayout) findViewById(R.id.search_page);
        myPage = (LinearLayout) findViewById(R.id.my_page);
        serch_view = (LinearLayout) findViewById(R.id.serch_view);
        mylogvisible = (LinearLayout) findViewById(R.id.mylogvisible);
        travelvisible = (LinearLayout) findViewById(R.id.travelvisible);

        logCount = (TextView) findViewById(R.id.logCount);
        friendsCount = (TextView) findViewById(R.id.friendsCount);
        my_page_profile_picture = (ImageView) findViewById(R.id.my_page_profile_picture);
        hompage = (ImageView) findViewById(R.id.view_home_icon);
        search = (ImageView) findViewById(R.id.view_search_icon);
        write = (ImageView) findViewById(R.id.view_logWrite_icon);
        mypages = (ImageView) findViewById(R.id.view_mypage_icon);

        ImageView travel = (ImageView)findViewById(R.id.travel);
        ImageView mypage_option = (ImageView)findViewById(R.id.mypage_option);
        ImageView my_logs = (ImageView)findViewById(R.id.my_logs);

        hompage.setImageDrawable(getResources().getDrawable(R.drawable.homepage_off));
        search.setImageDrawable(getResources().getDrawable(R.drawable.search_off));
        mypages.setImageDrawable(getResources().getDrawable(R.drawable.mypage_on));
        write.setImageDrawable(getResources().getDrawable(R.drawable.write_off));
        travel.setImageDrawable(getResources().getDrawable(R.drawable.timeline_off));
        mypage_option.setImageDrawable(getResources().getDrawable(R.drawable.mypage_option));
        my_logs.setImageDrawable(getResources().getDrawable(R.drawable.mypage_icon));

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();
        user_id = login.getString("user_id", "0");
        prifile_pict = login.getString("prifile_picture", "default.png");

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

        my_page_user_id.setText(user_id);
        search_Text = (EditText) findViewById(R.id.search_Text);
        search_Text_view = (TextView) findViewById(R.id.search_Text_view);

        profile = (ImageView) findViewById(R.id.profile_picture);
        profile_count task = new profile_count();
        task.execute();
        if (!prifile_pict.equals("0")) {
            profile_pic();
        }
        Drawable d;

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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        GridView mygv = (GridView) findViewById(R.id.mypage_list);
        /*list에 뿌려진 로그 클릭시*/
        mygv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getApplicationContext(), LifeLogViewActivity2.class);
                intent.putExtra("board_Code", parsedata[position][0]);
                intent.putExtra("board_Title", parsedata[position][1]);
                intent.putExtra("board_Content", parsedata[position][2]);
                intent.putExtra("log_longtitude", parsedata[position][3]);
                intent.putExtra("log_latitude", parsedata[position][4]);
                intent.putExtra("board_Date", parsedata[position][5]);
                intent.putExtra("write_user_id", parsedata[position][6]);
                intent.putExtra("user_id", user_id);
                intent.putExtra("file_Type", parsedata[position][7]);
                intent.putExtra("file_Content", parsedata[position][8]);
                if (parsedata[position][7].equals("3")) {
                    intent.putExtra("step_log_code", parsedata[position][9]);
                }
                intent.putExtra("write_type", parsedata[position][10]);
                Log.d("profile_pic", parsedata[position][11]);
                intent.putExtra("profile_picture", parsedata[position][11]);
                startActivity(intent);
            }
        });
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


    /*검색 gridview에 뿌리기*/
    public void Apeter(Bitmap[] images) {
        String[] text = new String[parsedata.length];
        String[] file_type = new String[parsedata.length];
        for (int i = 0; i < parsedata.length; i++) {
            if (parsedata[i][7].equals("0")) {
                text[i] = parsedata[i][1];
            } else {
                text[i] = parsedata[i][8];
            }
            file_type[i] = parsedata[i][7];
        }
        adapter = new MyAdapter(
                mypageActivity.this,
                R.layout.pop_view_list,       // GridView 항목의 레이아웃 row.xml
                text, file_type);
        adapter.image(images);
        GridView gv = null;

        gv = (GridView) findViewById(R.id.mypage_list);

        gv.setAdapter(adapter);

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
            loading = ProgressDialog.show(mypageActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(Bitmap[] s) {
            super.onPostExecute(s);

            Apeter(s);

            this.cancel(true);
            loading.dismiss();
        }
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
                DBserver = "myLog";

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
        class write extends AsyncTask<String, Void, Bitmap> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Bitmap s) {
                super.onPostExecute(s);
                this.cancel(true);
                my_page_profile_picture.setScaleType(ImageView.ScaleType.FIT_XY);
                my_page_profile_picture.setImageBitmap(s);
                my_page_profile_picture.setBackground(new ShapeDrawable(new OvalShape()));
                my_page_profile_picture.setClipToOutline(true);
            }

            @Override
            protected Bitmap doInBackground(String... params) {

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
                    Bitmap resizedBitmaps = BitmapFactory.decodeStream(is, null, options);
                    return resizedBitmaps;
                } catch (Exception e) {
                    return null;
                }

            }
        }

        write task = new write();
        task.execute();
    }

    class profile_count extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {

                Map<String, String> seldata = new HashMap<String, String>();
                seldata.put("user_id", user_id);

                String link = dataurl.getServerUrl() + "count_profile"; //92.168.25.25
                HttpClient.Builder http = new HttpClient.Builder("GET", link);

                http.addAllParameters(seldata);

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("카운트 결과값", s);
            String lCount = s.substring(0, s.indexOf(","));
            String fCount = s.substring(s.indexOf(",") + 1);

            friendsCount.setText(fCount);
            logCount.setText(lCount);
            this.cancel(true);
        }
    }


    //AsyncTask 라는 스레드를 시작 시켜 DB연결
    class Travel extends AsyncTask<String, Void, String> {

        //doInBackGround가 종료후 실행되는 메서드
        @Override
        protected void onPostExecute(String s) { // 웹 -> 앱으로 받는값
            super.onPostExecute(s);
            Log.d("onPostExecute: ", s);

            liston(s);
        }

        @Override
        protected String doInBackground(String... params) { // 실행 메서드

            try {
                String link = "";
                String data = "";
                link = dataurl.getServerUrl(); // 집 : 192.168.1.123, 학교 : 172.20.10.203, 에이타운 : 192.168.0.14

                Map<String, String> insertParam = new HashMap<String, String>(); // key, value

                String user_id = (String) params[0];
                insertParam.put("user_id", user_id);

                Log.d("스프링으로 보내는 값", insertParam.get("user_id").toString());

                link += "/titleSearch";

                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(insertParam); // 앱 -> 스프링으로 데이터보냄, 매개값을

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
    }

    public void liston(String title) {
        // Adapter 생성
        Tadapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(Tadapter);

        JsonArray json = (JsonArray) new JsonParser().parse(title);

////        Log.d("TAG", "onCreate: "+title);
        for (int i = 0; i < json.size(); i++) {
            Log.d("TAG Object", json.get(i).toString());
            JsonObject obj = json.get(i).getAsJsonObject(); // 오브젝트
//
            Log.d("TAG", "" + obj.get("travel_title"));
            Tadapter.addItem(ContextCompat.getDrawable(this, R.drawable.story), obj.get("travel_title").toString().replaceAll("\"", ""),
                    obj.get("start_date").toString().replaceAll("\"", ""), obj.get("end_date").toString().replaceAll("\"", ""),
                    obj.get("group_Code").toString().replaceAll("\"", ""));
//
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListViewItem listViewItem = (ListViewItem) adapterView.getItemAtPosition(i);
                travelStory = getSharedPreferences("title", MODE_PRIVATE);
                editor4 = travelStory.edit();
                editor4.putString("selectTitle", listViewItem.getTitle()); // 선택한 제목
                editor4.putString("selectgroupCode", listViewItem.getGcode()); // 선택한 코드

                editor4.commit();

                Log.d("디비시작전", listViewItem.getGcode());
                SearhToDatabase(listViewItem.getGcode());

                Intent intent = new Intent(mypageActivity.this, TravelMapActivity.class);
                startActivity(intent);

            }
        });

        // 현재날짜와 회원이 참여한 그룹의 날짜들을 비교하여 상태를 지정
        java.util.Date mDate = new java.util.Date(); // 현재날짜 구하기
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd"); // 형식지정
        String tempDate = mFormat.format(mDate); // 현재날짜 형식에 맞춰 초기화
        java.util.Date nowDate = java.sql.Date.valueOf(tempDate); //현재날짜


        for (int i = 0; i < json.size(); i++) { // 현재날짜와 그룹의 날짜를 비교
            JsonObject obj = json.get(i).getAsJsonObject(); // json 오브젝트
            String startDate = obj.get("start_date").toString().replaceAll("\"", "");
            String endDate = obj.get("end_date").toString().replaceAll("\"", "");

            java.util.Date st_Date = java.sql.Date.valueOf(startDate);
            java.util.Date en_Date = java.sql.Date.valueOf(endDate);

            Log.d("날짜 ", "제목:" + obj.get("travel_title") + ", 현재:" + String.valueOf(nowDate) + ", 시작:" + String.valueOf(st_Date) + ", 종료:" + String.valueOf(en_Date));
            if (nowDate.compareTo(st_Date) != -1 && nowDate.compareTo(en_Date) != 1) { // 현재날짜가 여행중인 날짜이면 joinCode를 1로 세팅
                joinCode = 1;
                select_group_Code = obj.get("group_Code").toString().replaceAll("\"", "");
//                Log.d("여행 중", String.valueOf(joinCode));
            } else {
                if (joinCode == 1) {
//                    Log.d("여행 중 아닌데 이미 여행중인 날짜가 존재", String.valueOf(joinCode));
                    break;
                }
                joinCode = 0;
//                Log.d("여행 중 아님", String.valueOf(joinCode));
            }
        }
        Log.d("최종 joinCode값", String.valueOf(joinCode));
        smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
        editor4 = smartCost.edit();
        editor4.putString("joinCode", String.valueOf(joinCode)); // 선택한 제목
        editor4.commit();
    }

    private void SearhToDatabase(String group_Code) {
        SearhData task = new SearhData();
        Log.d("최지훈디비전송", group_Code);
        task.execute(group_Code); // 메소드를 실행한당
    }

    //AsyncTask 라는 스레드를 시작 시켜 DB연결
    class SearhData extends AsyncTask<String, Void, String> {

        //doInBackGround가 종료후 실행되는 메서드
        @Override
        protected void onPostExecute(String s) { // 웹 -> 앱으로 받는값

            super.onPostExecute(s);

            JsonObject obj = (JsonObject) new JsonParser().parse(s);

            smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
            editor4 = smartCost.edit();
            editor4.putString("coin_Limit", obj.get("coin_Limit").toString().replaceAll("\"", "")); // 선택한 일정의 한도
            editor4.putString("sc_Division", obj.get("sc_Division").toString().replaceAll("\"", "")); // 선택한 일정의 스마트 코스트 구분
            editor4.commit();
        }

        @Override
        protected String doInBackground(String... params) { // 실행 메서드

            try {
                String link = "";
                String data = "";
                link = dataurl.getServerUrl();
                ; // 집 : 192.168.1.123, 학교 : 172.20.10.203, 에이타운 : 192.168.0.14

                Map<String, String> insertParam = new HashMap<String, String>(); // key, value

                String group_Code = (String) params[0];
                insertParam.put("group_Code", group_Code);

                link += "/scDivisionSearch";

                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(insertParam); // 앱 -> 스프링으로 데이터보냄, 매개값을

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
    }


    public void log_Write(View view) {
        LogWriteDialog();
    }

    public void option(View v) {
        Intent intent = new Intent(getApplicationContext(), option.class);
        startActivity(intent);
    }

    public void profile_edit(View v) {
        Intent intent = new Intent(getApplicationContext(), profileEditActivity.class);
        startActivity(intent);
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

    private void StepInsert(String user_id) {

        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(mypageActivity.this, "Please Wait", null, true, true);
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
    protected void onResume(){
        super.onResume();
    }

    public void myLogList(View view) {
        mylogvisible.setVisibility(View.VISIBLE);
        travelvisible.setVisibility(View.GONE);
        DBinput();
    }

    public void myTravel(View view) {
        mylogvisible.setVisibility(View.GONE);
        travelvisible.setVisibility(View.VISIBLE);
        Travel task = new Travel();
        task.execute(user_id); // 메소드를 실행한당
    }
    @Override
    protected void onDestroy() {
        Log.d("OOMTEST", "onDestroy");
        for(int i = 0; i < pImage.length; i++){
            if(pImage[i] != null) {
                pImage[i].recycle();
            }
        }

        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
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


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
