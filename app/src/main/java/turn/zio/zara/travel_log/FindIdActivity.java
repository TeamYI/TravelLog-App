package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FindIdActivity extends AppCompatActivity {

    private EditText user_email;
    private EditText user_phone;

    private boolean textcheck = false;
    private boolean regicheck2 = false;
    DataBaseUrl dataurl = new DataBaseUrl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostidfind);

        user_email = (EditText) findViewById(R.id.user_email);
        user_phone = (EditText) findViewById(R.id.user_phone);
    }

    public void findId(View view) {
        String user_find_phone = user_phone.getText().toString();
        String user_find_email = user_email.getText().toString();

        //공백 체크
        spacecheck(user_find_email, user_find_phone);
        //공백이 없으면 실행
        if (textcheck) {
            checkrole(user_find_email, user_find_phone);
            if (regicheck2) {
                FindIDDataBase(user_find_email, user_find_phone);
            }
        }
    }

    //정규식 체크
    public void checkrole(String email, String phone) {
        regicheck2 = false;
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "이메일형식이 아닙니다.", Toast.LENGTH_LONG).show();
            user_email.requestFocus();
        } else if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phone)) {
            Toast.makeText(getApplicationContext(), "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_LONG).show();
            user_phone.requestFocus();
        } else {
            regicheck2 = true;
        }
    }

    public void spacecheck(String user_find_email, String user_find_phone) {
        textcheck = false;
        if (user_find_email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
            user_email.requestFocus();
        } else if (user_find_phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "전화번호를 입력해주세요.", Toast.LENGTH_LONG).show();
            user_phone.requestFocus();
        } else {
            textcheck = true;
        }
    }

    private void FindIDDataBase(String user_email, String user_phone) {

        class idFind extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(FindIdActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                loading.dismiss();

                if (!s.equals("NOT FOUND")) {
                    viewMove(s);
                } else {
                    Toast.makeText(getApplicationContext(), "가입하신 아이디 없습니다.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_email = (String) params[0];
                    String user_phone = (String) params[1];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_email", user_email);
                    loginParam.put("user_phone", user_phone);


                    String link = dataurl.getServerUrl() + "findId"; //92.168.25.25
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

        idFind task = new idFind();
        task.execute(user_email, user_phone);
    }

    public void viewMove(String user_id) {
        Intent intent = new Intent(this, FindSuccessActivity.class);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }

    public void bakcMain(View view) {
        finish();
    }

    public void findpass(View view) {
        Intent intent = new Intent(this, FindPassActivity.class);
        startActivity(intent);
    }
}
