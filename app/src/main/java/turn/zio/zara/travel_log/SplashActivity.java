package turn.zio.zara.travel_log;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by 하루마다 on 2017-04-14.
 * 스플래시화면 3초 호출
 */

public class SplashActivity extends Activity {
    Intent intent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        ImageView splash = (ImageView)findViewById(R.id.imageView);
        splash.setImageDrawable(getResources().getDrawable(R.drawable.splash));
        splashload();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("dd","Dd");
        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        super.onSaveInstanceState(outState);
    }
    private void splashload() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                intent = new Intent(getApplicationContext(), LoginMenuActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
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
