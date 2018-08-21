package turn.zio.zara.travel_log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;


/**
 * Created by Hoonhoon94 on 2017-06-21.
 */

public class InsertCoinSmsActivity extends AppCompatActivity {
    public static String Switch_Stat;
    SharedPreferences smartCost;
    SharedPreferences.Editor editor;
    Switch optionSwitch;
    SharedPreferences login;

    SharedPreferences travelStory;

    String user_id;
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_insert_coin_sms);
        optionSwitch = (Switch) findViewById(R.id.SmsOptionSwitch);
        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        smartCost = getSharedPreferences("switchOP", MODE_PRIVATE);
        Switch_Stat = smartCost.getString("switchOP", "0");

        travelStory = getSharedPreferences("title", MODE_PRIVATE);
        final String group_code = travelStory.getString("selectgroupCode", "-1");
        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();
        user_id = login.getString("user_id", "0");

      final SmartCostData smartdata = new SmartCostData();
        if (Switch_Stat.equals("true")) {
            optionSwitch.setChecked(true);
        } else {
            optionSwitch.setChecked(false);
        }

        // 스위치의 체크 이벤트를 위한 리스너 등록
        optionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    Log.d("group_code",group_code);
                    Log.d("user_id",user_id);
                    editor.putString("group_code", group_code);
                    smartdata.setGroup_code(group_code);
                    smartdata.setUser_id(user_id);
                    Switch_Stat = "true";
                } else {
                    Switch_Stat = "false";
                }
            }
        });
    }

    public void sms_option_submit(View view) {
        smartCost = getSharedPreferences("switchOP", MODE_PRIVATE);
        editor = smartCost.edit();
        editor.putString("switchOP", Switch_Stat);
        editor.commit();

        Intent intent = new Intent(this, SmartCostAddActivity.class);
        startActivity(intent);
    }

    public void bakcMain(View view) {
        finish();
    }
}