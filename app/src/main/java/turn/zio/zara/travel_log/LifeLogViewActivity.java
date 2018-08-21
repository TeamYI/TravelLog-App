package turn.zio.zara.travel_log;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LifeLogViewActivity extends Activity {

    private TextView log_title;
    private TextView log_Content;
    private TextView log_Place;
    private TextView log_date;
    private TextView profile_user_id;

    private String mImgPath = null;
    private String mImgTitle = null;
    private String mImgOri = null;
    private int liketure = 0;

    private LinearLayout picutre_Linear;

    private ImageView image;
    private Bitmap resizedBitmap;
    private Bitmap bmImg;
    private Drawable drawable;
    private String file_Type;
    private String file_Content;

    SharedPreferences login;
    SharedPreferences.Editor editor;
    MediaPlayer player;

    private ImageView like;
    int board_code;
    String user_id;
    int like_ture = 0;
    DataBaseUrl dataurl = new DataBaseUrl();
    private ImageView user_profile;
    private String profile_picture;
    private ImageView option;
    private ListViewDialog mDialog;
    private double log_latitude;
    private double log_longtitude;
    private String board_Content;
    private String boder_Title;
    private String write_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_life_log_view);
        getWindow().setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        like = (ImageView) findViewById(R.id.log_Likes);
        user_profile = (ImageView) findViewById(R.id.profile_picture);
        ImageView log_Comments = (ImageView) findViewById(R.id.log_Comments);
        ImageView bakcMain = (ImageView) findViewById(R.id.bakcMain);
        user_profile.setBackground(new ShapeDrawable(new OvalShape()));
        user_profile.setClipToOutline(true);


        image = (ImageView) findViewById(R.id.log_picture);
        option = (ImageView) findViewById(R.id.option);
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


        picutre_Linear = (LinearLayout) findViewById(R.id.log_picture_Linear);

        Intent intent = getIntent();
        board_code = Integer.parseInt(intent.getExtras().getString("board_Code"));
        boder_Title = intent.getExtras().getString("board_Title");
        board_Content = intent.getExtras().getString("board_Content");
        log_longtitude = Double.parseDouble(intent.getExtras().getString("log_longtitude"));
        log_latitude = Double.parseDouble(intent.getExtras().getString("log_latitude"));
        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();
        user_id = login.getString("user_id", "0");
        write_user_id = intent.getExtras().getString("write_user_id");
        String String_Date = intent.getExtras().getString("board_Date");
        String write_type = intent.getExtras().getString("write_type");
        Log.d("dd", String_Date);
        String address = getAddress(log_latitude, log_longtitude);
        profile_picture = intent.getExtras().getString("profile_picture");
        profle pro = new profle();
        pro.execute();
        log_title.setText(boder_Title);
        log_Place.setText(address);
        profile_user_id.setText(write_user_id);
        log_date.setText(String_Date);

        String step_code = intent.getExtras().getString("step_log_code");

        if(step_code.equals("-1")){
            log_Comments.setVisibility(View.GONE);
        }else {
            log_Comments.setVisibility(View.VISIBLE);
        }

        //좋아요 눌렀는지 여부
        LikeTure(user_id, board_code + "");

        if (user_id.equals(write_user_id)) {
            option.setVisibility(View.VISIBLE);
        }
        /*웹으로쓴 글일때*/
        if (write_type.equals("0")) {

            String regex = "<img [^<>]*>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(board_Content);
            while (matcher.find()) {
                String temp = matcher.replaceAll("");
                Log.d("이미지태그",temp);
                log_Content.setText(Html.fromHtml(temp));
            }
        }/*앱이면*/ else {
            log_Content.setText(board_Content);
        }
        select_DB(intent.getExtras().getString("board_Code"));
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


    public static String replaceLast(String str, String regex, String replacement) {
        int regexIndexOf = str.lastIndexOf(regex);
        if (regexIndexOf == -1) {
            return str;
        } else {
            return str.substring(0, regexIndexOf) + replacement + str.substring(regexIndexOf + regex.length());
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

    private void select_DB(String board_code) {
        picutre_Linear.setVisibility(View.GONE);
        class selectpic extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LifeLogViewActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                if (!s.equals("failed")) {
                    Log.d("dd", "dd");
                    picutre_Linear.setVisibility(View.VISIBLE);
                    Log.d("image", picutre_Linear.getVisibility() + "");
                    if (file_Type.equals("1")) {
                        image.setImageBitmap(resizedBitmap);
                        image.setScaleType(ImageView.ScaleType.FIT_XY);

                    } else if (file_Type.equals("2")) {
                        final String url = dataurl.getDataUrl() + file_Content;
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
                    }
                }
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String board_code = (String) params[0];

                    Map<String, String> picsel = new HashMap<String, String>();

                    picsel.put("board_code", board_code);


                    String link = dataurl.getServerUrl() + "picture"; //92.168.25.25

                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(picsel);

                    // HTTP 요청 전송
                    HttpClient post = http.create();
                    post.request();
                    // 응답 상태코드 가져오기
                    int statusCode = post.getHttpStatusCode();
                    // 응답 본문 가져오기
                    String body = post.getBody();
                    if (!body.equals("failed")) {
                        Log.d("dd", "dd");
                        JSONArray json = null;
                        String[][] parsedata = new String[0][2];
                        try {
                            json = new JSONArray(body);
                            parsedata = new String[json.length()][2];
                            for (int i = 0; i < json.length(); i++) {
                                JSONObject jobject = json.getJSONObject(i);

                                parsedata[i][0] = jobject.getString("file_content");
                                parsedata[i][1] = jobject.getString("file_type");
                                file_Type = parsedata[i][1];
                                file_Content = parsedata[i][0];
                            }
                            String url = dataurl.getTumnailUrl() + file_Content;
                            if (file_Type.equals("1")) {
                                try {
                                    InputStream is = (InputStream) new URL(url).getContent();

                                    bmImg = BitmapFactory.decodeStream(is);
                                    int width = bmImg.getWidth();
                                    int height = bmImg.getHeight();
                                    //화면에 표시할 데이터
                                    Matrix matrix = new Matrix();
                                    resizedBitmap = Bitmap.createBitmap(bmImg, 0, 0, width, height, matrix, true);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (file_Type.equals("2")) {
                                drawable = getResources().getDrawable(R.drawable.voice);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    return body;

                    // Read Server Response

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        selectpic task = new selectpic();
        task.execute(board_code);

    }
    public void bakcMain(View view) {
        LifeLogViewActivity2.oneView = true;
        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        finish();
    }


    @Override
    public void onBackPressed() {
        LifeLogViewActivity2.oneView = true;
        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }

        finish();
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
                    intent.putExtra("board_code", board_code);
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
