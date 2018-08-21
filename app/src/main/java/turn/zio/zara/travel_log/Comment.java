package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Comment extends AppCompatActivity {

    EditText comentText;
    int board_code;
    String user_id;

    DataBaseUrl dataurl = new DataBaseUrl();
    private String[][] parsedata;
    private CommentAdapter adapter;

    GridView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        comentText = (EditText) findViewById(R.id.commentText);
        list = (GridView) findViewById(R.id.list);

        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        Intent intent = getIntent();
        board_code = Integer.parseInt(intent.getExtras().getString("board_Code"));
        user_id = intent.getExtras().getString("user_id");
        commentDB();

    }

    public void commentDB() {
        replyList task = new replyList();
        String result = null;
        try {
            result = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("result", result);
        if (!result.equals("[]")) {
            jsonParse(result);
            serpic setimage = new serpic();
            setimage.execute();
        }
    }

    public void Apeter(Bitmap[] images) {

        String[] writeuser_id = new String[parsedata.length];
        String[] date = new String[parsedata.length];
        String[] Content = new String[parsedata.length];
        int[] board_codes = new int[parsedata.length];

        for (int i = 0; i < parsedata.length; i++) {
            board_codes[i] = Integer.parseInt(parsedata[i][0]);
            Content[i] = parsedata[i][1];
            date[i] = parsedata[i][2];
            writeuser_id[i] = parsedata[i][3];
        }

        adapter = new CommentAdapter(
                Comment.this,
                R.layout.commentview, board_codes, Content, date, writeuser_id       // GridView 항목의 레이아웃 row.xml
        );
        adapter.pimage(images);
        GridView gv = (GridView) findViewById(R.id.list);
        gv.setAdapter(adapter);

    }

    public void bakcMain(View view) {
        finish();
    }

    public void commentWrite(View view) {
        String board_Content = comentText.getText().toString();
        if (board_Content.isEmpty()) {
            Toast.makeText(getApplicationContext(), "댓글 내용을 입력하세요 .", Toast.LENGTH_LONG).show();
            return;
        }
        commentWrite(board_Content);
        commentDB();
    }

    class replyList extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(Comment.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("like 결과", s);
            loading.dismiss();
            comentText.setText(null);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            if (!s.equals("[]")) {
                Handler mHandler = new Handler();
                mHandler.postDelayed(sbottom, 2000);
            }
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Map<String, String> loginParam = new HashMap<String, String>();

                loginParam.put("reply_code", board_code + "");


                String link = dataurl.getServerUrl() + "writeReplyList"; //92.168.25.25
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

    public void jsonParse(String s) {
        Log.d("json", s);
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][5];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("board_code");
                parsedata[i][1] = jobject.getString("board_content");
                parsedata[i][2] = jobject.getString("board_date");
                parsedata[i][3] = jobject.getString("user_id");
                parsedata[i][4] = jobject.getString("user_profile");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class serpic extends AsyncTask<String, Void, Bitmap[]> {
        ProgressDialog loading;

        @Override
        protected Bitmap[] doInBackground(String... params) {
            Bitmap[] images = new Bitmap[parsedata.length];
            try {
                for (int i = 0; i < parsedata.length; i++) {

                    String url = dataurl.getProfile() + parsedata[i][4];
                    InputStream is = (InputStream) new URL(url).getContent();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = false;
                    Bitmap resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                    images[i] = resizedBitmap;
                }
                return images;

                // Read Server Response

            } catch (Exception e) {
                images = null;
                return images;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(Comment.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(Bitmap[] s) {
            super.onPostExecute(s);
            loading.dismiss();
            Apeter(s);
            this.cancel(true);
        }
    }

    private void commentWrite(final String board_content) {

        class write extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("like 결과", s);
                comentText.setText(null);

            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);
                    loginParam.put("reply_code", board_code + "");
                    loginParam.put("board_content", board_content);


                    String link = dataurl.getServerUrl() + "writeComment"; //92.168.25.25
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

        write task = new write();
        task.execute(board_content);
    }

    Runnable sbottom = new Runnable() {
        @Override
        public void run() {
            list.setSelection(parsedata.length - 1);
        }
    };

}
