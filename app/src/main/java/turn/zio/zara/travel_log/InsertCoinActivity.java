package turn.zio.zara.travel_log;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Hoonhoon94 on 2017-06-21.
 */

public class InsertCoinActivity extends AppCompatActivity {

    SharedPreferences travelStory;
    SharedPreferences user;
    private EditText editcoin;
    private EditText editcontent;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_insert_coin);
        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        editcoin = (EditText) findViewById(R.id.sc_coin);
        editcontent = (EditText) findViewById(R.id.sc_content);
    }


    public void scinsertcoin(View view) {
        String sc_coin = editcoin.getText().toString();
        String sc_content = editcontent.getText().toString();

        insertToDatabase(sc_coin, sc_content, TravelListActivity.select_group_Code, TravelListActivity.login_user_id);
    }

    // 디비입력
    private void insertToDatabase(String sc_coin, String sc_content, String groupCode, String userkeep) {
        InsertMoney task = new InsertMoney();
        task.execute(sc_coin, sc_content, groupCode, userkeep);

        finish();
    }

    public void bakcMain(View view) {
        finish();
    }
}