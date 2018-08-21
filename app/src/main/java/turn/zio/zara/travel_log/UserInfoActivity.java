package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static turn.zio.zara.travel_log.R.id.storyDisplay;

public class UserInfoActivity extends AppCompatActivity {

    private String write_user_id;
    private ImageView user_profile;
    private String profile_picture;
    private Bitmap resizedBitmap;
    DataBaseUrl dataurl = new DataBaseUrl();
    private TextView logCount;
    private TextView friendsCount;
    private TextView friendadd;
    private String[][] parsedata;
    Bitmap[] pImage;
    MyAdapter adapter;
    private TextView my_page_user_id;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        Intent intent = getIntent();
        write_user_id = intent.getExtras().getString("write_user_id");
        user_id = intent.getExtras().getString("user_id");
        profile_picture = intent.getExtras().getString("profile_picture");
        logCount = (TextView) findViewById(R.id.logCount);
        friendsCount = (TextView) findViewById(R.id.friendsCount);
        friendadd = (TextView) findViewById(R.id.profile_edit);
        my_page_user_id = (TextView) findViewById(R.id.my_page_user_id);
        user_profile = (ImageView) findViewById(R.id.my_page_profile_picture);
        user_profile.setBackground(new ShapeDrawable(new OvalShape()));
        user_profile.setClipToOutline(true);
        my_page_user_id.setText(write_user_id);


        ImageView bakcMain = (ImageView) findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        ImageView my_logs = (ImageView) findViewById(R.id.my_logs);
        my_logs.setImageDrawable(getResources().getDrawable(R.drawable.mypage_icon));
        profle pro = new profle();
        pro.execute();
        profile_count profile_count = new profile_count();
        profile_count.execute();
        friendState friendState = new friendState();
        friendState.execute();
    }
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

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class mainlistAll extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("user_id", write_user_id);
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
                        //FileInputStream is =new FileInputStream(file);
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

                        options.inJustDecodeBounds = false;
                        Bitmap resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                        images[i] = resizedBitmap;
                    }
                    else if (parsedata[i][7].equals("3")) {
                        Log.d("url", parsedata[i][8]);
                        InputStream is = (InputStream) new URL(KMlurl).getContent();
                        BitmapFactory.Options options = new BitmapFactory.Options();

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
            loading = ProgressDialog.show(UserInfoActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(Bitmap[] s) {
            super.onPostExecute(s);

            Apeter(s);

            this.cancel(true);
            loading.dismiss();
        }
    }

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
                UserInfoActivity.this,
                R.layout.pop_view_list,       // GridView 항목의 레이아웃 row.xml
                text, file_type);
        adapter.image(images);
        GridView gv = null;
        gv = (GridView) findViewById(R.id.mypage_list);
        gv.setAdapter(adapter);

    }

    public void myLogList(View view) {
        DBinput();
    }
    class profile_count extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {

                Map<String, String> seldata = new HashMap<String, String>();
                seldata.put("user_id", write_user_id);

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
            DBinput();
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

    public void bakcMain(View view) {
        finish();
    }

    public void friend(View v){
        String friendtext =friendadd.getText().toString();
        friend friend = new friend();
        if(friendtext.equals("친구신청")){
            friendadd.setText("친구신청중");
        }else if(friendtext.equals("친구신청중")){
            friendadd.setText("친구신청");
        }else if(friendtext.equals("친구하기")){
            friendadd.setText("친구");
        }else if(friendtext.equals("친구")){
            friendadd.setText("친구신청");
        }

        friend.execute(friendtext);
    }
    class friendState extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("friend_id", write_user_id);
                seldata.put("user_id", user_id);
                DBserver = "firend";

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
            Log.d("result",s);
            if(s.equals("null")){
                friendadd.setText("친구신청");
            }else{
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(s);
                String f_user_id = element.getAsJsonObject().get("user_id").getAsString();
                String f_friend_id = element.getAsJsonObject().get("friend_id").getAsString();
                String f_state = element.getAsJsonObject().get("friend_accept").getAsString();
                if(f_user_id.equals(user_id) && f_state.equals("0")){
                    friendadd.setText("친구신청중");
                }else if(f_state.equals("1")){
                    friendadd.setText("친구");
                }else if(f_friend_id.equals(user_id) && f_state.equals("0")){
                    friendadd.setText("친구하기");
                }
            }
        }
    }
    class friend extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                String friendtext = params[0];
                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("friend_id", write_user_id);
                seldata.put("user_id", user_id);
                seldata.put("friend_State", friendtext);
                DBserver = "firendADD";

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

    @Override
    protected void onDestroy() {
        Log.d("OOMTEST", "onDestroy");
        recycleBitmap(user_profile);

        super.onDestroy();
    }

    private static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
    }
}
