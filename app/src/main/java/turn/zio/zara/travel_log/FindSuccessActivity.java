package turn.zio.zara.travel_log;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FindSuccessActivity extends AppCompatActivity {
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_success);
        Intent intent = getIntent();
        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        ImageView logo = (ImageView)findViewById(R.id.logo_image);
        logo.setImageDrawable(getResources().getDrawable(R.drawable.logo));

        user_id = intent.getExtras().getString("user_id");

        TextView tv = (TextView) findViewById(R.id.successview);

        tv.setText("아이디는 " + user_id + " 입니다.");
    }

    public void bakcMain(View view) {
        finish();
    }

    public void movelogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }
}
