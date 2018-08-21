package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static turn.zio.zara.travel_log.R.id.imageView2;

/**
 * Created by Hoonhoon94 on 2017-06-08.
 */

public class TravelGroupActivity extends AppCompatActivity {
    DataBaseUrl dataurl = new DataBaseUrl();
    SharedPreferences smartCost;
    String sc_Division;
    SharedPreferences travelStory;
    SharedPreferences.Editor editor;
    String group_code;
    private String[][] parsedata;
    Bitmap[] images;

    LinearLayout userinfo;
    LinearLayout userinfos;
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_travel_group);

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
        userinfo = (LinearLayout) findViewById(R.id.group_aceptlist);
        userinfos = (LinearLayout) findViewById(R.id.group_aceptinglist);
        TravelGroup group = new TravelGroup();
        String result = null;

        try {
            result = group.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        jsonParse(result);

        serpic sel = new serpic();
        sel.execute();

    }

    public void jsonParse(String s) {
        Log.d("json", s);
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][12];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("user_id");
                parsedata[i][1] = jobject.getString("group_apply");
                if (json.getJSONObject(i).isNull("user_profile") == false) {
                    parsedata[i][2] = jobject.getString("user_profile");
                } else {
                    parsedata[i][2] = "0";
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class serpic extends AsyncTask<String, Void, Bitmap[]> {
        ProgressDialog loading;

        ArrayList<String> location = new ArrayList<String>();
        private InputStream is = null;

        @Override
        protected Bitmap[] doInBackground(String... params) {
            images = new Bitmap[parsedata.length];
            try {
                for (int i = 0; i < parsedata.length; i++) {
                    String url = dataurl.getProfile() + parsedata[i][2];
                    Log.d("URLs", url + " / " + i);
                    InputStream is = (InputStream) new URL(url).getContent();
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    options.inJustDecodeBounds = false;
                    Bitmap resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                    images[i] = resizedBitmap;

                }
                return images;

                // Read Server Response

            } catch (Exception e) {
                return images;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap[] s) {
            super.onPostExecute(s);
            this.cancel(true);

            int[] count = new int[2];
            for(int k = 0; k < parsedata.length; k++){
                count[Integer.parseInt(parsedata[k][1])]++;
            }
            if(count[0]==0){
                userinfos.setVisibility(View.GONE);
            }

            int[] num = new int[2];
            for(int i = 0; i < parsedata.length; i++){
                Log.d("username",parsedata[i][0]);
                TextView username = new TextView(TravelGroupActivity.this);
                ImageView user_profile  = new ImageView(TravelGroupActivity.this);
                int hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                int weight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
                user_profile.setLayoutParams(new LinearLayout.LayoutParams(weight, hight));
                user_profile.setImageBitmap(s[i]);
                hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
                username.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hight));
                username.setTextColor(Color.parseColor("#000000"));
                username.setTextSize(20);
                username.setText(parsedata[i][0]);
                username.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                View line = new View(TravelGroupActivity.this);
                hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
                line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, hight));
                line.setBackgroundColor(Color.parseColor("#e6e6e6"));

                user_profile.setBackground(new ShapeDrawable(new OvalShape()));
                user_profile.setClipToOutline(true);

                LinearLayout user_data = new LinearLayout(TravelGroupActivity.this);

                LinearLayout margin = new LinearLayout(TravelGroupActivity.this);
                hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
                margin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hight));


                LinearLayout margin2 = new LinearLayout(TravelGroupActivity.this);
                hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
                margin2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hight));


                username.setGravity(View.TEXT_ALIGNMENT_CENTER);
                user_data.addView(user_profile);
                user_data.addView(username);
                if(parsedata[i][1].equals("1")) {
                    userinfo.addView(user_data);
                    if(num[0] != count[1]-1) {
                        userinfo.addView(margin);
                        userinfo.addView(line);
                        userinfo.addView(margin2);
                        num[0]++;
                    }
                }else{
                    userinfos.addView(user_data);
                    if(num[1] != count[0]-1) {
                        userinfos.addView(margin);
                        userinfos.addView(line);
                        userinfos.addView(margin2);
                        num[1]++;
                    }
                }

            }
        }
    }

    class TravelGroup extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("group_code", group_code);
                DBserver = "Travel_group";

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

    public void travel_Supply(View view) { // 준비물 액티비티로 이동
        Intent intent = new Intent(this, TravelSupplyActivity.class);
        startActivity(intent);
    }

    public void travel_Story(View view) { // 스토리 액티비티로 이동
        Intent intent = new Intent(this, TravelStoryActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), TravelMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
