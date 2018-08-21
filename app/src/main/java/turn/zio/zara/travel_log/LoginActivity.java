package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private EditText userid;
    private EditText userpass;

    private boolean logincheck = false;

    SharedPreferences login;
    SharedPreferences.Editor editor;

    DataBaseUrl dataurl = new DataBaseUrl();
    private String[] parsedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        ImageView logo = (ImageView)findViewById(R.id.back_main_icon);
        logo.setImageDrawable(getResources().getDrawable(R.drawable.logo));

        //로그인시  유지
        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();


        userid = (EditText) findViewById(R.id.user_login_id);
        userpass = (EditText) findViewById(R.id.user_login_pass);


    }

    //회원가입 텍스트뷰 클릭시 회원가입뷰로 이동
    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //로그인 버튼 클릭시 실행되는 메서드
    public void login(View view) {
        String user_login_id = userid.getText().toString();
        String user_login_pass = userpass.getText().toString();

        //공백 체크
        spacecheck(user_login_id, user_login_pass);
        //공백이 없으면 실행
        if (logincheck) {
            LogininputDatabase(user_login_id, user_login_pass);
        }
    }

    private void LogininputDatabase(final String user_id, String user_pass) {

        class loginData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);

                loading.dismiss();
                if (!s.equals("null")) { // 아이디와 비밀번호가 같은 유저 존재시
                    try {
                        parsedata = new String[2];
                        JsonParser jsonParser = new JsonParser();
                        JsonElement element = jsonParser.parse(s);

                        parsedata[0] = element.getAsJsonObject().get("user_id").getAsString();
                        parsedata[1] = element.getAsJsonObject().get("user_profile").getAsString();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    editor.putString("user_id", parsedata[0]);
                    editor.putString("prifile_picture", parsedata[1]);
                    editor.commit();
                    viewMove(); // 뷰페이지를 이동시키는 메서드
                } else { // 없으면
                    Toast.makeText(getApplicationContext(), "아이디가 존재하지 않거나 비밀번호가 틀렸습니다", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];
                    String user_pass = (String) params[1];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);
                    loginParam.put("user_pass", user_pass);


                    String link = dataurl.getServerUrl() + "login"; //92.168.25.25
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

        loginData task = new loginData();
        task.execute(user_id, user_pass);
    }

    public void viewMove() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void spacecheck(String id, String pass) {
        logincheck = false;
        if (id.isEmpty()) {
            Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_LONG).show();
            userid.requestFocus();
        } else if (pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "패스워드를 입력해주세요.", Toast.LENGTH_LONG).show();
            userid.requestFocus();
        } else {
            logincheck = true;
        }
    }

    public void bakcMain(View view) {
        finish();
    }

    public void findpass(View view) {
        Intent intent = new Intent(this, FindPassActivity.class);
        startActivity(intent);
    }
}
