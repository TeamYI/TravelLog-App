package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.view.View.TEXT_ALIGNMENT_VIEW_END;
import static turn.zio.zara.travel_log.TravelMapActivity.beginDate;

/**
 * Created by Hoonhoon94 on 2017-06-08.
 */

public class TravelStoryActivity extends AppCompatActivity {

    DataBaseUrl dataurl = new DataBaseUrl();
    SharedPreferences smartCost;
    String sc_Division;
    SharedPreferences travelStory;
    SharedPreferences.Editor editor;
    String group_code;
    private String[][] parsedata;
    private String prifile_pict;
    LinearLayout boardinfo;

    SharedPreferences login;
    String user_id;
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_travel_story);

        ImageView storyDisplay = (ImageView) findViewById(R.id.storyDisplay);
        ImageView moneyDisplay = (ImageView) findViewById(R.id.moneyDisplay);
        ImageView mapDisplay = (ImageView) findViewById(R.id.mapDisplay);
        ImageView supplyDisplay = (ImageView) findViewById(R.id.supplyDisplay);
        ImageView groupDisplay = (ImageView) findViewById(R.id.groupDisplay);

        storyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.story));
        moneyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.money1));
        mapDisplay.setImageDrawable(getResources().getDrawable(R.drawable.map));
        supplyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.supply1));
        groupDisplay.setImageDrawable(getResources().getDrawable(R.drawable.group1));

        smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
        sc_Division = smartCost.getString("sc_Division", "0");
        travelStory = getSharedPreferences("title", MODE_PRIVATE);
        group_code = travelStory.getString("selectgroupCode", "-1");

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        user_id = login.getString("user_id", "0");

        Travel_Board board = new Travel_Board();
        String json = null;
        boardinfo = (LinearLayout) findViewById(R.id.boardinfo);
        try {
            json = board.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("json",json);
        if(!json.equals("[]")) {
            jsonParse(json);
        }
    }

    public void jsonParse(String s) {
        Log.d("json", s);
        String[] board_title = new String[0];
        String[] user_id = new String[0];
        String[] board_date = new String[0];
        Date begin = beginDate;
        String beginD = TravelMapActivity.begin;
        Long Alldiff = TravelMapActivity.diffDays;
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][13];
            board_title = new String[parsedata.length];
            user_id = new String[parsedata.length];
            board_date = new String[parsedata.length];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("board_code");
                parsedata[i][1] = jobject.getString("board_title");
                parsedata[i][2] = jobject.getString("board_content");
                parsedata[i][3] = jobject.getString("log_longtitude");
                parsedata[i][4] = jobject.getString("log_latitude");
                parsedata[i][5] = jobject.getString("board_date");
                parsedata[i][6] = jobject.getString("user_id");
                if (json.getJSONObject(i).isNull("file_content") == false) {
                    parsedata[i][7] = jobject.getString("file_type");
                    parsedata[i][8] = jobject.getString("file_content");
                } else {
                    parsedata[i][7] = "0";
                    parsedata[i][8] = "1";
                }
                parsedata[i][10] = jobject.getString("write_type");
                if (json.getJSONObject(i).isNull("user_profile") == false) {
                    parsedata[i][11] = jobject.getString("user_profile");
                } else {
                    parsedata[i][11] = prifile_pict;
                }
                board_title[i] = parsedata[i][1];
                user_id[i] = parsedata[i][6];
                board_date[i] = parsedata[i][5];

                SimpleDateFormat formmat = new SimpleDateFormat("yyyyMMdd");
                String travelDate = parsedata[i][5].substring(parsedata[i][5].indexOf(" "),parsedata[i][5].indexOf(","));
                travelDate = beginD.replace(beginD.substring(6),travelDate.trim());
                Log.d("travelDate", travelDate);
                Date TravelDay = null;
                try {
                    TravelDay = formmat.parse(travelDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = TravelDay.getTime() - beginDate.getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000);
                parsedata[i][12] = diffDays + "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int[][] count = new int[Integer.parseInt(Alldiff+"")][2];
        for (int j = 0; j < Alldiff; j++) {
            count[Integer.parseInt(parsedata[j][12])][0]++;

        }


        int daypage = 0;
        int num = 0;
        for (int i = 0; i < parsedata.length; i++) {
            Log.d("username", parsedata[i][0]);
            TextView username = new TextView(TravelStoryActivity.this);
            TextView boardt = new TextView(TravelStoryActivity.this);
            TextView baordd = new TextView(TravelStoryActivity.this);
            TextView daytext = new TextView(TravelStoryActivity.this);

            daytext.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            daytext.setTextColor(Color.parseColor("#000000"));
            daytext.setTextSize(25);
            daytext.setText("Day "+(daypage+1));

            boardt.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            boardt.setTextColor(Color.parseColor("#000000"));
            boardt.setTextSize(20);
            boardt.setText(board_title[i]);

            username.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            username.setTextColor(Color.parseColor("#000000"));
            username.setTextSize(20);
            username.setText(user_id[i]);
            username.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            baordd.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            baordd.setTextColor(Color.parseColor("#999999"));
            baordd.setTextSize(15);
            baordd.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
            baordd.setText(board_date[i]);

            LinearLayout user_data = new LinearLayout(TravelStoryActivity.this);
            user_data.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            user_data.setOrientation(LinearLayout.VERTICAL);

            LinearLayout vertical = new LinearLayout(TravelStoryActivity.this);
            vertical.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            vertical.setOrientation(LinearLayout.VERTICAL);

            LinearLayout TandU = new LinearLayout(TravelStoryActivity.this);
            TandU.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout Day = new LinearLayout(TravelStoryActivity.this);
            Day.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            LinearLayout margin = new LinearLayout(TravelStoryActivity.this);
            int hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
            margin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hight));

            View line = new View(TravelStoryActivity.this);
            hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
            line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, hight));
            line.setBackgroundColor(Color.parseColor("#e6e6e6"));

            View line2 = new View(TravelStoryActivity.this);
            hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
            line2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, hight));
            line2.setBackgroundColor(Color.parseColor("#e6e6e6"));

            LinearLayout days = new LinearLayout(TravelStoryActivity.this);
            days.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            days.setOrientation(LinearLayout.VERTICAL);

            LinearLayout margin2 = new LinearLayout(TravelStoryActivity.this);
            hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
            margin2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hight));

            LinearLayout margin3 = new LinearLayout(TravelStoryActivity.this);
            hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
            margin3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hight));

            days.setPadding(25,0,25,0);

            days.addView(daytext);
            days.addView(margin3);
            days.addView(line);
            days.addView(margin2);


            if(daypage == Integer.parseInt(parsedata[i][12])){
                daypage++;
                boardinfo.addView(days);
            }
            TandU.setPadding(50,0,0,0);
            user_data.setPadding(50,0,50,0);

            TandU.addView(boardt);
            TandU.addView(username);
            Day.addView(baordd);
            vertical.addView(TandU);
            vertical.addView(Day);
            vertical.setPadding(10, 0, 10, 0);
            vertical.addView(margin);
            user_data.addView(vertical);
            if(i != parsedata.length-1) {
                user_data.addView(line2);
            }
            boardinfo.addView(user_data);
        }

    }

    class Travel_Board extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("group_code", group_code);
                seldata.put("user_id", user_id);
                DBserver = "Travel_board";

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
