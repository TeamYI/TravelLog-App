package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hoonhoon94 on 2017-06-08.
 */

public class TravelSupplyActivity extends AppCompatActivity {
    DataBaseUrl dataurl = new DataBaseUrl();
    SharedPreferences smartCost;
    String sc_Division;
    SharedPreferences travelStory;
    SharedPreferences.Editor editor;
    String group_code;
    private String[][] parsedata;
    MaterialApter adapter;
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_travel_supply);

        ImageView storyDisplay = (ImageView) findViewById(R.id.storyDisplay) ;
        ImageView moneyDisplay = (ImageView) findViewById(R.id.moneyDisplay) ;
        ImageView mapDisplay = (ImageView) findViewById(R.id.mapDisplay) ;
        ImageView supplyDisplay = (ImageView) findViewById(R.id.supplyDisplay) ;
        ImageView groupDisplay = (ImageView) findViewById(R.id.groupDisplay) ;

        storyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.story));
        moneyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.money1));
        mapDisplay.setImageDrawable(getResources().getDrawable(R.drawable.map));
        supplyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.supply1));
        groupDisplay.setImageDrawable(getResources().getDrawable(R.drawable.group1));

        smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
        sc_Division = smartCost.getString("sc_Division", "0");
        travelStory = getSharedPreferences("title", MODE_PRIVATE);
        group_code = travelStory.getString("selectgroupCode", "-1");

        Material meterial = new Material();
        String result = null;

        try {
            result = meterial.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        jsonParse(result);


    }

    public void jsonParse(String s) {
        Log.d("json", s);
        String[] material_name = new String[0];
        int[] metrial_check = new int[0];
        int[] metrial_code = new int[0];
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][12];
            material_name = new String[parsedata.length];
            metrial_check = new int[parsedata.length];
            metrial_code = new int[parsedata.length];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);
                parsedata[i][0] = jobject.getString("material_code");
                parsedata[i][1] = jobject.getString("material_name");
                parsedata[i][2] = jobject.getString("Appmaterial_check");
                material_name[i] = parsedata[i][1];
                metrial_check[i] = Integer.parseInt(parsedata[i][2]);
                metrial_code[i] = Integer.parseInt(parsedata[i][0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new MaterialApter(
                TravelSupplyActivity.this,
                R.layout.materiallist,       // GridView 항목의 레이아웃 row.xml
                material_name, metrial_check, metrial_code, group_code);
        GridView gv = null;
        gv = (GridView) findViewById(R.id.list);
        gv.setAdapter(adapter);

    }

    class Material extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("group_code", group_code);
                DBserver = "Travel_Material";

                String link = dataurl.getServerUrl() + DBserver; //92.168.25.25
                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(seldata);

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.cancel(true);
        }
    }

    // 액티비티 전환시 애니메이션 제거
    public void onResume() {
        this.overridePendingTransition(0, 0);
        super.onResume();
    }

    public void smart_Cost(View view) { // 여비관리 액티비티로 이동
        if (sc_Division.equals("차감")) {
            Intent intent = new Intent(this, SmartCostSubActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SmartCostAddActivity.class);
            startActivity(intent);
        }
    }

    public void travel_Map(View view) { // 지도 액티비티로 이동
        Intent intent = new Intent(this, TravelMapActivity.class);
        startActivity(intent);
    }

    public void travel_Story(View view) { // 스토리 액티비티로 이동
        Intent intent = new Intent(this, TravelStoryActivity.class);
        startActivity(intent);
    }

    public void travel_Group(View view) { // 그룹 액티비티로 이동
        Intent intent = new Intent(this, TravelGroupActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), TravelMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
