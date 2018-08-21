package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class FindPassActivity extends AppCompatActivity {

    private EditText user_id;
    private EditText user_email;

    private boolean textcheck = false;
    private boolean regicheck2 = false;

    DataBaseUrl dataurl = new DataBaseUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostpassfind);

        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        ImageView logo = (ImageView)findViewById(R.id.back_main_icon);
        logo.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        user_email = (EditText) findViewById(R.id.user_email);
        user_id = (EditText) findViewById(R.id.user_id);
    }

    public void findpass(View view) {
        String user_find_id = user_id.getText().toString();
        String user_find_email = user_email.getText().toString();

        //공백 체크
        spacecheck(user_find_email, user_find_id);
        //공백이 없으면 실행
        if (textcheck) {
            checkrole(user_find_email);
            if (regicheck2) {
                FindPassDataBase(user_find_email, user_find_id);
            }
        }
    }

    //정규식 체크
    public void checkrole(String email) {
        textcheck = false;
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "이메일형식이 아닙니다.", Toast.LENGTH_LONG).show();
            user_email.requestFocus();
        } else {
            regicheck2 = true;
        }
    }

    public void spacecheck(String user_find_email, String user_find_id) {
        textcheck = false;
        if (user_find_email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_LONG).show();
            user_id.requestFocus();
        } else if (user_find_id.isEmpty()) {
            Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
            user_email.requestFocus();
        } else {
            textcheck = true;
        }
    }

    private void FindPassDataBase(String user_email, String user_id) {

        class passFind extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FindPassActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                loading.dismiss();

                if (!s.equals("FAILED")) {
                    viewMove();
                } else {
                    Toast.makeText(getApplicationContext(), "가입하신 아이디, 일치하는 이메일이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_email = (String) params[0];
                    String user_id = (String) params[1];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_email", user_email);
                    loginParam.put("user_id", user_id);


                    String link = dataurl.getServerUrl() + "findPass"; //92.168.25.25
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

        passFind task = new passFind();
        task.execute(user_email, user_id);
    }

    public void viewMove() {
        Intent intent = new Intent(this, emailSubmitActivity.class);
        startActivity(intent);
    }

    public void bakcMain(View view) {
        finish();
    }

    public void findid(View view) {
        Intent intent = new Intent(this, FindIdActivity.class);
        startActivity(intent);
    }
}
