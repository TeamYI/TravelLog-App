package turn.zio.zara.travel_log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

public class LoginMenuActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button CustomloginButton;

    SharedPreferences login;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화 (setContentView 보다 먼저 실행되어야합니다. 안그럼 에러납니다.)
        setContentView(R.layout.activity_login_menu);

        ImageView logo = (ImageView)findViewById(R.id.main_logo_icon);
        logo.setImageDrawable(getResources().getDrawable(R.drawable.logo));
        //로그인시  유지
        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();

        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        String userkeep = user.getString("user_id", "0");

        if (!userkeep.equals("0")) {
            viewMove();
        }
         /*페이스북 로그인연동*/

        callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자

        /*CustomloginButton = (Button) findViewById(R.id.facebook_loagin);
        CustomloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LoginManager - 요청된 읽기 또는 게시 권한으로 로그인 절차를 시작합니다.
                LoginManager.getInstance().logInWithReadPermissions(LoginMenuActivity.this,
                        Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                Log.e("onSuccess", "onSuccess");
                            }

                            @Override
                            public void onCancel() {
                                Log.e("onCancel", "onCancel");
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                Log.e("onError", "onError " + exception.getLocalizedMessage());
                            }
                        });
            }
        });
        *//*페이스북 로그인연동*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void Travel_login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void viewMove() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("dd","Dd");

        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onSaveInstanceState(outState);
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
