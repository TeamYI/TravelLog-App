package turn.zio.zara.travel_log;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static turn.zio.zara.travel_log.R.id.gender;

/**
 * Created by 하루마다 on 2017-04-14.
 */

public class RegisterActivity extends AppCompatActivity {
    private EditText editid;
    private EditText editpass;
    private EditText editcpass;
    private EditText editemail;
    private EditText editphone;
    private EditText editbrith;
    private RadioGroup rg;

    DataBaseUrl dataurl = new DataBaseUrl();
    private int action; // 0이면 아이디체크 1이면 회원가입

    private boolean regicheck = false; // 회원가입버튼 클릭시 공백체크
    private boolean usercheck = false; // userid 페이드아웃시 db연결
    private boolean regicheck2 = false; // 회원가입시 이메일, 전화번호 정규식 체크

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_register);

        editid = (EditText) findViewById(R.id.user_regi_id);
        editpass = (EditText) findViewById(R.id.user_regi_pass);
        editcpass = (EditText) findViewById(R.id.pass_check);
        editemail = (EditText) findViewById(R.id.user_regi_email);
        editphone = (EditText) findViewById(R.id.user_regi_phone);
        editbrith = (EditText) findViewById(R.id.user_regi_birth);

        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        ImageView back_main_icon = (ImageView)findViewById(R.id.back_main_icon);
        back_main_icon.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        rg = (RadioGroup) findViewById(gender);

        editid.setOnFocusChangeListener(new View.OnFocusChangeListener() { // 포커스를 얻으면
            @Override
            public void onFocusChange(View v, boolean hasFocus) { // 포커스가 한뷰에서 다른뷰로 바뀔때
                if (hasFocus == false) {
                    idCheck();
                }
            }
        });
        editbrith.setOnFocusChangeListener(new View.OnFocusChangeListener() { // 포커스를 얻으면
            @Override
            public void onFocusChange(View v, boolean hasFocus) { // 포커스가 한뷰에서 다른뷰로 바뀔때
                if (hasFocus) {
                    date();
                }
            }
        });

    }

    public void memregister(View view) {
        String user_id = editid.getText().toString();
        String user_pass = editpass.getText().toString();
        String user_cpass = editcpass.getText().toString();
        String user_email = editemail.getText().toString();
        String user_phone = editphone.getText().toString();
        String user_birth = editbrith.getText().toString();
        String user_gender;

        int radio = rg.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(radio);
        String rbString = rb.getText().toString();
        if (rbString.equals("남자")) {
            user_gender = "0";
        } else {
            user_gender = "1";
        }

        spacechekc(user_id, user_pass, user_cpass, user_email, user_phone, user_birth);
        if (regicheck) {
            checkrole(user_email, user_phone);
        }
        if (regicheck && regicheck2 && usercheck) {
            insertToDatabase(user_id, user_pass, user_email, user_phone, user_birth, user_gender);
        }
    }

    public void date() {
        //현재 날짜
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                editbrith.setText(String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this, listener, cyear, cmonth, cday);
        dialog.show();
    }

    // 회원가입시 공백 체크후 포커스
    public void spacechekc(String id, String pass, String cpass, String email, String phone, String birth) {
        regicheck = false;
        if (id.isEmpty()) {
            Toast.makeText(getApplicationContext(), "아이디를 입력해주세요.", Toast.LENGTH_LONG).show();
            editid.requestFocus();
        } else if (pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "패스워드를 입력해주세요.", Toast.LENGTH_LONG).show();
            editpass.requestFocus();
        } else if (cpass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "패스워드확인을 입력해주세요.", Toast.LENGTH_LONG).show();
            editcpass.requestFocus();
        } else if (!pass.equals(cpass)) {
            Toast.makeText(getApplicationContext(), "패스워드가 일치하지 않습니다", Toast.LENGTH_LONG).show();
            editcpass.requestFocus();
        } else if (email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "이메일을 입력해주세요.", Toast.LENGTH_LONG).show();
            editemail.requestFocus();
        } else if (phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "전화번호를 입력해주세요.", Toast.LENGTH_LONG).show();
            editphone.requestFocus();
        } else if (birth.isEmpty()) {
            Toast.makeText(getApplicationContext(), "생년월일 입력해주세요.", Toast.LENGTH_LONG).show();
            editbrith.requestFocus();
        } else {
            regicheck = true;
        }
    }

    //정규식 체크
    public void checkrole(String email, String phone) {
        regicheck2 = false;
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), "이메일형식이 아닙니다.", Toast.LENGTH_LONG).show();
            editemail.requestFocus();
        } else if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phone)) {
            Toast.makeText(getApplicationContext(), "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_LONG).show();
            editphone.requestFocus();
        } else {
            regicheck2 = true;
        }
    }

    //ID 페이스 아웃시 아파치에 있는 PHP 접속 구문으로
    public void idCheck() {
        String user_id = editid.getText().toString();
        action = 0; // 0 : 아이디 중복확인
        InsertData task = new InsertData();
        task.execute(user_id);
        ;
    }

    // 회원가입 버튼 클릭시 아파치에 있는 PHP접속 구문으로
    private void insertToDatabase(String user_id, String user_pass, String user_email, String user_phone, String user_birth, String user_gender) {
        InsertData task = new InsertData();
        action = 1;
        task.execute(user_id, user_pass, user_email, user_phone, user_birth, user_gender);
    }

    //AsyncTask 라는 스레드를 시작 시켜 DB연결
    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        //처음 execute시 실행되는 메소드
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(RegisterActivity.this, "Please Wait", null, true, true);
            if (action == 0) {
                usercheck = false;
            }
        }

        //doInBackGround가 종료후 실행되는 메소드
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("result LOGINCHECK : ", s);

            loading.dismiss();

            if (action == 0) {
                if (s.equals("1")) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 아이디 입니다.", Toast.LENGTH_LONG).show();
                    editid.requestFocus();
                } else {
                    usercheck = true;
                }
            } else if (action == 1) {
                finish();
                Toast.makeText(getApplicationContext(), "회원가입 성공하였습니다", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String link = "";
                String data = "";
                link = dataurl.getServerUrl(); //192.168.25.25


                Map<String, String> insertParam = new HashMap<String, String>();

                SimpleDateFormat dt1;
                if (action == 0) { //아이디체크
                    String user_id = (String) params[0];

                    link += "idCheck";
                    insertParam.put("user_id", user_id);
                } else if (action == 1) { //insert
                    String user_id = (String) params[0];
                    String user_pass = (String) params[1];
                    String user_email = (String) params[2];
                    String user_phone = (String) params[3];
                    String user_birth = (String) params[4];
                    String user_gender = (String) params[5];

                    link += "register";

                    insertParam.put("user_id", user_id);
                    insertParam.put("user_pass", user_pass);
                    insertParam.put("user_email", user_email);
                    insertParam.put("user_phone", user_phone);
                    insertParam.put("user_birth", user_birth);
                    insertParam.put("user_gender", user_gender);


                }
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

    public void bakcMain(View view) {
        finish();
    }
}
