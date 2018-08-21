package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static turn.zio.zara.travel_log.R.id.bakcMain;
import static turn.zio.zara.travel_log.R.id.log_Comments;
import static turn.zio.zara.travel_log.R.id.user_id;

public class PushAlramActivity extends AppCompatActivity {

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

    private LinearLayout mLayout;
    private String board_code;
    private String file_Content;
    int like_ture = 0;
    private ImageView like;
    private ImageView user_profile;
    private String profile_picture;
    private ImageView option;

    private ListViewDialog mDialog;
    SharedPreferences login;
    SharedPreferences.Editor editor;
    DataBaseUrl dataurl = new DataBaseUrl();
    String userkeep;
    private String prifile_pict;

    private String file_type;
    String boder_Title;
    String board_Content;

    MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_alram);

        log_title = (TextView) findViewById(R.id.log_title);
        log_Content = (TextView) findViewById(R.id.log_cotennt);
        log_Place = (TextView) findViewById(R.id.log_place);
        log_date = (TextView) findViewById(R.id.log_date);
        profile_user_id = (TextView) findViewById(user_id);

        image = (ImageView) findViewById(R.id.log_picture);
        bakcMain_icon = (ImageView) findViewById(R.id.bakcMain_icon);
        picutre_Linear = (LinearLayout) findViewById(R.id.log_picture_Linear);
        text = (LinearLayout) findViewById(R.id.text);
        goomap = (LinearLayout) findViewById(R.id.MapContainer);
        like = (ImageView) findViewById(R.id.log_Likes);
        user_profile = (ImageView) findViewById(R.id.profile_picture);
        option = (ImageView) findViewById(R.id.option);

        ImageView log_Comments = (ImageView) findViewById(R.id.log_Comments);
        ImageView bakcMain = (ImageView) findViewById(R.id.bakcMain);
        ImageView view_mainlogo_icon = (ImageView) findViewById(R.id.view_mainlogo_icon);

        log_Comments.setImageDrawable(getResources().getDrawable(R.drawable.comment));
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        view_mainlogo_icon.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        image.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        option.setImageDrawable(getResources().getDrawable(R.drawable.homepage_option));

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();
        prifile_pict = login.getString("prifile_picture", "default.png");

        option.setVisibility(View.VISIBLE);
        profile_pic();

        user_profile.setBackground(new ShapeDrawable(new OvalShape()));
        user_profile.setClipToOutline(true);

        board_code = FirebaseMessagingService.push_board_code;

        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        userkeep = user.getString("user_id", "0");
        InsertData select = new InsertData();
        select.execute();
    }

    public void bakcMain(View v) {
        finish();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
    @Override
    public void onBackPressed() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
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
            loading = ProgressDialog.show(PushAlramActivity.this, "Please Wait", null, true, true);
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

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //처음 execute시 실행되는 메소드
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //doInBackGround가 종료후 실행되는 메소드
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("result LOGINCHECK : ", s);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(s);
            boder_Title = element.getAsJsonObject().get("board_title").getAsString();
            board_Content = element.getAsJsonObject().get("board_content").getAsString();
            Double longtitude = Double.parseDouble(element.getAsJsonObject().get("log_longtitude").getAsString());
            Double latitude = Double.parseDouble(element.getAsJsonObject().get("log_latitude").getAsString());
            String user_id = element.getAsJsonObject().get("user_id").getAsString();
            String address = getAddress(latitude, longtitude);
            String Date = element.getAsJsonObject().get("board_date").getAsString();
            file_type = element.getAsJsonObject().get("file_type").getAsString();
            file_Content = element.getAsJsonObject().get("file_content").getAsString();
            log_title.setText(boder_Title);
            log_Content.setText(board_Content);
            log_Place.setText(address);
            log_date.setText(Date);
            profile_user_id.setText(user_id);
            LikeTure(user_id, board_code);
            if (file_type.equals("1")) {
                Log.d("이미지", "이미지");
                picutre_Linear.setVisibility(View.VISIBLE);
                serpic webserver = new serpic();
                webserver.execute();
            } else if (file_type.equals("2")) {
                picutre_Linear.setVisibility(View.VISIBLE);
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

            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String link = "";
                String data = "";

                link = dataurl.getServerUrl() + "selectBoard"; //192.168.25.25


                Map<String, String> insertParam = new HashMap<String, String>();
                Log.d("출력보드코드",board_code);
                insertParam.put("board_code", board_code);

                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(insertParam);

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
                user_profile.setScaleType(ImageView.ScaleType.FIT_XY);
                user_profile.setImageBitmap(resizedBitmaps[0]);
            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    String url = dataurl.getProfile() + prifile_pict;
                    Log.d("profile", url);
                    InputStream is = (InputStream) new URL(url).getContent();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
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
        LikeonOff(userkeep, board_code + "");
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
                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), Life_LogModifyActivity.class);
                    Log.d("board_code", board_code + "/board_code");
                    intent.putExtra("board_code", board_code + "");
                    intent.putExtra("board_Title", boder_Title);
                    intent.putExtra("board_Content", board_Content);
                    intent.putExtra("file_Type", file_type);
                    intent.putExtra("file_Content", file_Content);
                    startActivity(intent);
                } else if (position == 1) {
                    deleteBoard(userkeep, board_code + "");
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

    public void commentView(View v) {
        Intent intent = new Intent(getApplicationContext(), Comment.class);
        intent.putExtra("board_Code", board_code + "");
        intent.putExtra("user_id", userkeep);
        startActivity(intent);
    }
}
