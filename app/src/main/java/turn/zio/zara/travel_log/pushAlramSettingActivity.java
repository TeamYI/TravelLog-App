package turn.zio.zara.travel_log;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class pushAlramSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_alram_setting);
        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
    }
}
