package turn.zio.zara.travel_log;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LifeLogViewActivity2 extends AppCompatActivity implements OnMapReadyCallback {

    private TextView log_title;
    private TextView log_Content;
    private TextView log_Place;
    private TextView log_date;
    private TextView profile_user_id;

    private LinearLayout picutre_Linear;
    private LinearLayout text;
    private LinearLayout goomap;

    private ImageView image;
    private ImageView bakcMain_icon;
    private Drawable drawable;

    Bitmap resizedBitmap;
    Bitmap bmImg;

    String step_log_code;
    String file_Content;
    MediaPlayer player;
    ArrayList<String> location = new ArrayList<String>();

    DataBaseUrl dataurl = new DataBaseUrl();

    LinearLayout MapContainer;
    MapFragment mMapFragment;
    private LinearLayout mLayout;
    private GoogleMap mMap;
    private int board_code;
    private String user_id;
    public static boolean oneView = true;
    private String file_Type;
    int like_ture = 0;
    private ImageView like;
    private ImageView user_profile;
    private String profile_picture;
    private ImageView option;

    private ListViewDialog mDialog;
    private double log_latitude;
    private double log_longtitude;
    private String write_type;
    private String boder_Title;
    private String board_Content;
    private String String_Date;
    private String write_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_life_log_view);

        log_title = (TextView) findViewById(R.id.log_title);
        log_Content = (TextView) findViewById(R.id.log_cotennt);
        log_Place = (TextView) findViewById(R.id.log_place);
        log_date = (TextView) findViewById(R.id.log_date);
        profile_user_id = (TextView) findViewById(R.id.user_id);

        image = (ImageView) findViewById(R.id.log_picture);
        picutre_Linear = (LinearLayout) findViewById(R.id.log_picture_Linear);
        text = (LinearLayout) findViewById(R.id.text);
        goomap = (LinearLayout) findViewById(R.id.MapContainer);
        like = (ImageView) findViewById(R.id.log_Likes);
        user_profile = (ImageView) findViewById(R.id.profile_picture);
        option = (ImageView) findViewById(R.id.option);

        user_profile.setBackground(new ShapeDrawable(new OvalShape()));
        user_profile.setClipToOutline(true);

        ImageView log_Comments = (ImageView) findViewById(R.id.log_Comments);
        ImageView bakcMain = (ImageView) findViewById(R.id.bakcMain);
        ImageView view_mainlogo_icon = (ImageView) findViewById(R.id.view_mainlogo_icon);

        log_Comments.setImageDrawable(getResources().getDrawable(R.drawable.comment));
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        view_mainlogo_icon.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        image.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        option.setImageDrawable(getResources().getDrawable(R.drawable.homepage_option));

        log_title = (TextView) findViewById(R.id.log_title);
        log_Content = (TextView) findViewById(R.id.log_cotennt);
        log_Place = (TextView) findViewById(R.id.log_place);
        log_date = (TextView) findViewById(R.id.log_date);
        profile_user_id = (TextView) findViewById(R.id.user_id);

        Intent intent = getIntent();
        board_code = Integer.parseInt(intent.getExtras().getString("board_Code"));
        boder_Title = intent.getExtras().getString("board_Title");
        board_Content = intent.getExtras().getString("board_Content");
        user_id = intent.getExtras().getString("user_id");
        write_user_id = intent.getExtras().getString("write_user_id");
        String_Date = intent.getExtras().getString("board_Date");
        file_Type = intent.getExtras().getString("file_Type");
        file_Content = intent.getExtras().getString("file_Content");
        write_type = intent.getExtras().getString("write_type");
        profile_picture = intent.getExtras().getString("profile_picture");
        profle pro = new profle();
        pro.execute();
        if (!file_Type.equals("3")) {
            log_longtitude = Double.parseDouble(intent.getExtras().getString("log_longtitude"));
            log_latitude = Double.parseDouble(intent.getExtras().getString("log_latitude"));
        }
        if (file_Type.equals("3")) {
            step_log_code = intent.getExtras().getString("step_log_code");
            Log.d("dd", step_log_code);
        }
        //좋아요 눌렀는지 여부

        if (user_id.equals(write_user_id)) {
            option.setVisibility(View.VISIBLE);
        }

        LikeTure(user_id, board_code + "");
        String address = "0";
        if (file_Type.equals("1")) {
            Log.d("이미지", "이미지");
            picutre_Linear.setVisibility(View.VISIBLE);
            address = getAddress(log_latitude, log_longtitude);
            serpic webserver = new serpic();
            webserver.execute();
        } else if (file_Type.equals("2")) {
            Log.d("뷰", file_Content);
            picutre_Linear.setVisibility(View.VISIBLE);
            address = getAddress(log_latitude, log_longtitude);
            final String url = dataurl.getDataUrl() + file_Content;
            drawable = getResources().getDrawable(R.drawable.voice);
            image.setImageDrawable(drawable);
            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (v.getId() == R.id.log_picture) {
                        try {
                            player = new MediaPlayer();
                            player.setDataSource(url);
                            player.prepare();
                            player.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else if (file_Type.equals("3")) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            mMapFragment = MapFragment.newInstance();
            fragmentTransaction.add(R.id.MapContainer, mMapFragment);
            fragmentTransaction.commit();
            mMapFragment.getMapAsync(this);
    }
        log_title.setText(boder_Title);
        Log.d("dsada",write_type);
        if (!file_Type.equals("3")) {
            log_Place.setText(address);
            /*웹으로쓴 글일때*/
            if (write_type.equals("0")) {

                String regex = "<img [^<>]*>";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(board_Content);
                Log.d("dsadsa",board_Content);
                while (matcher.find()) {
                    String temp = matcher.replaceAll("");
                    Log.d("dsadsa",temp);
                    log_Content.setText(Html.fromHtml(temp));
                }

            }/*앱이면*/ else {
                log_Content.setText(board_Content);
            }
        } else {
            goomap.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
        }
        profile_user_id.setText(write_user_id);
        log_date.setText(String_Date);
    }

    public static String replaceLast(String str, String regex, String replacement) {
        int regexIndexOf = str.lastIndexOf(regex);
        if (regexIndexOf == -1) {
            return str;
        } else {
            return str.substring(0, regexIndexOf) + replacement + str.substring(regexIndexOf + regex.length());
        }
    }

    class profle extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog loading;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {

                String url = dataurl.getProfile() + profile_picture;
                Log.d("url", url);
                InputStream is = (InputStream) new URL(url).getContent();

                Bitmap bmImg2 = BitmapFactory.decodeStream(is);


                return bmImg2;

                // Read Server Response

            } catch (Exception e) {
                resizedBitmap = null;
                return resizedBitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            this.cancel(true);
            user_profile.setScaleType(ImageView.ScaleType.FIT_XY);
            user_profile.setImageBitmap(s);
        }
    }

    /**
     * 위도와 경도 기반으로 주소를 리턴하는 메서드
     */
    public String getAddress(double lat, double lng) {
        String address = null;

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 1);
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

    public void bakcMain(View view) {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        finish();
    }

    class serpic extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog loading;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {

                String url = dataurl.getTumnailUrl() + file_Content;
                Log.d("url", url);
                InputStream is = (InputStream) new URL(url).getContent();

                bmImg = BitmapFactory.decodeStream(is);
                int width = bmImg.getWidth();
                int height = bmImg.getHeight();
                //화면에 표시할 데이터
                Matrix matrix = new Matrix();
                resizedBitmap = Bitmap.createBitmap(bmImg, 0, 0, width, height, matrix, true);


                return resizedBitmap;

                // Read Server Response

            } catch (Exception e) {
                resizedBitmap = null;
                return resizedBitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(LifeLogViewActivity2.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            this.cancel(true);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            image.setImageBitmap(resizedBitmap);
            loading.dismiss();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        selFile();
    }

    private void selFile() {

        class loginData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            private InputStream is = null;
            KmlLayer layer = null;
            private String[][] parsedata;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                Log.d("result", s);
                JSONArray json = null;
                try {
                    json = new JSONArray(s);
                    parsedata = new String[json.length()][10];
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
                        parsedata[i][9] = jobject.getString("write_type");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (mMap != null) {


                    String[] coo = location.get(((location.size() - 1) / 2)).toString().split(",");
                    Log.d("size", location.get(((location.size() - 1) / 2)).toString().trim());
                    LatLng startPoint = new LatLng(Double.parseDouble(coo[1]), Double.parseDouble(coo[0]));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15));

                    PolylineOptions option = new PolylineOptions();
                    option.width(4);
                    option.color(Color.BLACK);
                    for (int i = 0; i < location.size(); i++) {
                        String[] coos = location.get(i).toString().split(",");
                        Log.d("draw", location.get(i).toString());
                        LatLng point = new LatLng(Double.parseDouble(coos[1]), Double.parseDouble(coos[0]));
                        option.add(point);
                    }
                    mMap.addPolyline(option);

                    MarkerOptions markerOption = new MarkerOptions();
                    for (int i = 0; i < parsedata.length; i++) {
                        markerOption.position(new LatLng(Double.parseDouble(parsedata[i][4]), Double.parseDouble(parsedata[i][3])));
                        markerOption.title(parsedata[i][1]);
                        if (parsedata[i][7].equals("1")) {
                            markerOption.snippet("사진");
                        } else if (parsedata.equals("2")) {
                            markerOption.snippet("음성");
                        } else {
                            markerOption.snippet("글");
                        }
                        mMap.addMarker(markerOption);

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                for (int i = 0; i < parsedata.length; i++) {

                                    if (marker.getPosition().latitude == Double.parseDouble(parsedata[i][4])
                                            && marker.getPosition().longitude == Double.parseDouble(parsedata[i][3])
                                            && oneView) {
                                        oneView = false;
                                        Intent intent = new Intent(getApplicationContext(), LifeLogViewActivity.class);
                                        intent.putExtra("board_Code", parsedata[i][0]);
                                        intent.putExtra("board_Title", parsedata[i][1]);
                                        intent.putExtra("board_Content", parsedata[i][2]);
                                        intent.putExtra("log_longtitude", parsedata[i][3]);
                                        intent.putExtra("log_latitude", parsedata[i][4]);
                                        intent.putExtra("user_id", parsedata[i][6]);
                                        intent.putExtra("board_Date", parsedata[i][5]);
                                        intent.putExtra("write_type", parsedata[i][9]);
                                        intent.putExtra("step_log_code",step_log_code);
                                        startActivity(intent);
                                    }
                                }


                                return false;
                            }
                        });
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("step_log_code", board_code + "");


                    String link = dataurl.getServerUrl() + "step_log_select"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(loginParam);

                    // HTTP 요청 전송
                    HttpClient post = http.create();
                    post.request();
                    // 응답 상태코드 가져오기
                    int statusCode = post.getHttpStatusCode();
                    // 응답 본문 가져오기
                    String body = post.getBody();


                    String urltext = dataurl.getStepUrl() + file_Content;
                    Log.d("url", urltext);
                    URL url = new URL(urltext);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    is = urlConnection.getInputStream();

                    InputStreamReader inputReader = new InputStreamReader(is);

                    String column = null;
                    BufferedReader br = new BufferedReader(inputReader);
                    boolean flag = false;
                    while ((column = br.readLine()) != null) {
                        int coordin = column.indexOf("<coordinates>");

                        if (coordin != -1 || flag) {
                            int i = 0;
                            flag = true;
                            String tmpCoordin = column;
                            tmpCoordin = tmpCoordin.replaceAll("<coordinates>", "");
                            tmpCoordin = tmpCoordin.replaceAll("</coordinates>", "");
                            Log.d("tomCoordib", tmpCoordin.trim());
                            if (tmpCoordin.trim().equals("</LineString>")) {
                                break;
                            }
                            location.add(tmpCoordin);
                        }

                    }

                    return body;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "failed";
            }
        }

        loginData task = new loginData();
        task.execute();
    }

    /*좋아요 여부*/
    private void LikeTure(final String user_id, final String board_code) {

        class tureData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("like 결과", s);
                like_ture = Integer.parseInt(s);
                if (like_ture == 1) {
                    like.setImageDrawable(getResources().getDrawable(R.drawable.like_on));
                } else {
                    like.setImageDrawable(getResources().getDrawable(R.drawable.like_off));
                }

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];
                    String board_code = (String) params[1];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);
                    loginParam.put("board_code", board_code);


                    String link = dataurl.getServerUrl() + "liketure"; //92.168.25.25
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

        tureData task = new tureData();
        task.execute(user_id, board_code);
    }

    public void likeclick(View v) {
        Log.d("like", like_ture + "");
        if (like_ture == 1) {
            like.setImageDrawable(getResources().getDrawable(R.drawable.like_off));
            like_ture = -1;
        } else {
            like.setImageDrawable(getResources().getDrawable(R.drawable.like_on));
            like_ture = 1;
        }
        LikeonOff(user_id, board_code + "");
    }

    private void LikeonOff(final String user_id, final String board_code) {

        class tureData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("like 결과", s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];
                    String board_code = (String) params[1];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);
                    loginParam.put("board_code", board_code);

                    String dbselect = null;

                    if (like_ture == 1) {
                        dbselect = "like";
                    } else {
                        dbselect = "likeDelete";
                    }

                    Log.d("db", dbselect);
                    String link = dataurl.getServerUrl() + dbselect; //92.168.25.25
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

        tureData task = new tureData();
        task.execute(user_id, board_code);
    }

    public void log_option(View v) {
        switch (v.getId()) {
            case R.id.option:
                Log.d("TAG", "click button list dialog.......");
                showListDialog();
                break;
        }
    }

    private void showListDialog() {

        String[] item = getResources().getStringArray(R.array.list_dialog_option_item);

        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String>(listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.onOnSetItemClickListener(new ListViewDialog.ListViewDialogSelectListener() {


            @Override

            public void onSetOnItemClickListener(int position) {
                // TODO Auto-generated method stub
                profle pro = new profle();
                pro.execute();
                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), Life_LogModifyActivity.class);
                    Log.d("board_code", board_code + "/board_code");
                    intent.putExtra("board_code", board_code + "");
                    intent.putExtra("board_Title", boder_Title);
                    intent.putExtra("board_Content", board_Content);
                    intent.putExtra("file_Type", file_Type);
                    intent.putExtra("file_Content", file_Content);
                    startActivity(intent);
                } else if (position == 1) {
                    deleteBoard(user_id, board_code + "");
                } else if (position == 2) {
                    mDialog.dismiss();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();

    }

    private void deleteBoard(final String user_id, final String board_code) {

        class delete extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d(" 결과", s);
                Intent intent = new Intent(getApplicationContext(), searchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];
                    String board_code = (String) params[1];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);
                    loginParam.put("board_code", board_code);


                    String link = dataurl.getServerUrl() + "deleteBoard"; //92.168.25.25
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

        delete task = new delete();
        task.execute(user_id, board_code);
    }

    public void userinfo(View v){
        if(user_id.equals(write_user_id)){
            RecycleUtils RecursiveUtils = new RecycleUtils();
            RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
            System.gc();
            Intent intent = new Intent(getApplicationContext(), mypageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
            intent.putExtra("profile_picture", profile_picture);
            intent.putExtra("write_user_id", write_user_id);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        }
    }

    public void commentView(View v) {
        Intent intent = new Intent(getApplicationContext(), Comment.class);
        intent.putExtra("board_Code", board_code + "");
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onDestroy();
    }
}
