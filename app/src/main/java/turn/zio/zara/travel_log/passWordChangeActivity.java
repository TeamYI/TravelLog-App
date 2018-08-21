package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class passWordChangeActivity extends AppCompatActivity {

    private TextView this_password;
    private TextView change_password;
    private TextView change_confirm_password;
    private boolean insertcheck;
    private boolean passwordcheck;

    SharedPreferences login;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_word_change);

        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        ImageView this_password_icon = (ImageView)findViewById(R.id.this_password_icon);
        this_password_icon.setImageDrawable(getResources().getDrawable(R.drawable.pass_update_lock));
        ImageView change_password_icon = (ImageView)findViewById(R.id.change_password_icon);
        change_password_icon.setImageDrawable(getResources().getDrawable(R.drawable.pass_update_lock));
        ImageView change_confirm_password_icon = (ImageView)findViewById(R.id.change_confirm_password_icon);
        change_confirm_password_icon.setImageDrawable(getResources().getDrawable(R.drawable.pass_update_lock));

        this_password = (TextView) findViewById(R.id.this_password);
        change_password = (TextView) findViewById(R.id.change_password);
        change_confirm_password = (TextView) findViewById(R.id.change_confirm_password);

    }

    public void bakcMain(View view) {
        finish();
    }

    public void passChangesubmit(View view) {
        String this_pass = this_password.getText().toString();
        String new_pass = change_password.getText().toString();
        String new_confirm_pass = change_confirm_password.getText().toString();

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();


        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        String user_id = user.getString("user_id", "0");


        spacecheck(this_pass, new_pass, new_confirm_pass);
        if (insertcheck == true) {
            if (this_pass.equals(new_pass)) {
                Toast.makeText(getApplicationContext(), "현재 비밀번호와 같습니다.", Toast.LENGTH_SHORT).show();
                passwordcheck = false;
            } else if (!new_pass.equals(new_confirm_pass)) {
                passwordcheck = false;
            } else {
                passwordcheck = true;
            }
        }
        if (passwordcheck == true) {
            Change_user_profile(user_id, new_pass);
        }
    }

    public void spacecheck(String this_pass, String new_pass, String new_confirm_pass) {
        insertcheck = false;
        if (this_pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            this_password.requestFocus();
        } else if (new_pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "새 패스워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            change_password.requestFocus();
        } else if (new_confirm_pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "새 패스워드 확인을 입력해주세요.", Toast.LENGTH_SHORT).show();
            change_confirm_password.requestFocus();
        } else {
            insertcheck = true;
        }
    }

    private void Change_user_profile(String user_id, String user_pass) {

        class Change_user extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(passWordChangeActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                if (s.equals("success")) {
                    Toast.makeText(getApplicationContext(), "비밀번호 변경 완료 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "오류 발생.", Toast.LENGTH_SHORT).show();
                }
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];
                    String new_pass = (String) params[1];


                    Map<String, String> changeParame = new HashMap<String, String>();

                    changeParame.put("user_id", user_id);
                    changeParame.put("user_pass", new_pass);


                    String link = "http://211.211.213.218:8084/android/change_pass"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(changeParame);

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

        Change_user task = new Change_user();
        task.execute(user_id, user_pass);
    }
}
