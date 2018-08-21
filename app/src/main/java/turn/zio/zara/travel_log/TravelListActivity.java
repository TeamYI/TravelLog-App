package turn.zio.zara.travel_log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hoonhoon94 on 2017-06-15.
 */

public class TravelListActivity extends AppCompatActivity {

    public static int joinCode = 0; // 오늘이 여행중인지 구분코드
    public static String login_user_id = "";
    public static String select_group_Code = "";

    SharedPreferences travelStory;
    SharedPreferences smartCost;
    String sc_Division;
    SharedPreferences.Editor editor;
    ListView listview;
    ListViewAdapter adapter;

    DataBaseUrl dataUrl = new DataBaseUrl();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_list);

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

        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        login_user_id = user.getString("user_id", "0");

        smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
        sc_Division = smartCost.getString("sc_Division", "0");

        Log.d("TAG0", "아이디 : " + login_user_id);
        insertToDatabase(login_user_id);
    }

    // 아이콘 클릭시 디비시작
    private void insertToDatabase(String user_id) {
        InsertData task = new InsertData();
        Log.d("스프링으로 데이터 보내기 전", user_id);
        task.execute(user_id); // 메소드를 실행한당
    }

    //AsyncTask 라는 스레드를 시작 시켜 DB연결
    class InsertData extends AsyncTask<String, Void, String> {

        //doInBackGround가 종료후 실행되는 메서드
        @Override
        protected void onPostExecute(String s) { // 웹 -> 앱으로 받는값
            super.onPostExecute(s);
            Log.d("onPostExecute: ", s);

            liston(s);
        }

        @Override
        protected String doInBackground(String... params) { // 실행 메서드

            try {
                String link = "";
                String data = "";
                link = dataUrl.getServerUrl(); // 집 : 192.168.1.123, 학교 : 172.20.10.203, 에이타운 : 192.168.0.14

                Map<String, String> insertParam = new HashMap<String, String>(); // key, value

                String user_id = (String) params[0];
                insertParam.put("user_id", user_id);

                Log.d("스프링으로 보내는 값", insertParam.get("user_id").toString());

                link += "/titleSearch";

                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(insertParam); // 앱 -> 스프링으로 데이터보냄, 매개값을

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

    public void liston(String title) {
        // Adapter 생성
        adapter = new ListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        JsonArray json = (JsonArray) new JsonParser().parse(title);

////        Log.d("TAG", "onCreate: "+title);
        for (int i = 0; i < json.size(); i++) {
            Log.d("TAG Object", json.get(i).toString());
            JsonObject obj = json.get(i).getAsJsonObject(); // 오브젝트
//
            Log.d("TAG", "" + obj.get("travel_title"));
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.story), obj.get("travel_title").toString().replaceAll("\"", ""),
                    obj.get("start_date").toString().replaceAll("\"", ""), obj.get("end_date").toString().replaceAll("\"", ""),
                    obj.get("group_Code").toString().replaceAll("\"", ""));
//
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListViewItem listViewItem = (ListViewItem) adapterView.getItemAtPosition(i);
                travelStory = getSharedPreferences("title", MODE_PRIVATE);
                editor = travelStory.edit();
                editor.putString("selectTitle", listViewItem.getTitle()); // 선택한 제목
                editor.putString("selectgroupCode", listViewItem.getGcode()); // 선택한 코드
                editor.commit();

                Log.d("디비시작전", listViewItem.getGcode());
                SearhToDatabase(listViewItem.getGcode());

                if (sc_Division.equals("차감")) {
                    Intent intent = new Intent(TravelListActivity.this, SmartCostSubActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(TravelListActivity.this, SmartCostAddActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 현재날짜와 회원이 참여한 그룹의 날짜들을 비교하여 상태를 지정
        java.util.Date mDate = new java.util.Date(); // 현재날짜 구하기
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd"); // 형식지정
        String tempDate = mFormat.format(mDate); // 현재날짜 형식에 맞춰 초기화
        java.util.Date nowDate = java.sql.Date.valueOf(tempDate); //현재날짜


        for (int i = 0; i < json.size(); i++) { // 현재날짜와 그룹의 날짜를 비교
            JsonObject obj = json.get(i).getAsJsonObject(); // json 오브젝트
            String startDate = obj.get("start_date").toString().replaceAll("\"", "");
            String endDate = obj.get("end_date").toString().replaceAll("\"", "");

            java.util.Date st_Date = java.sql.Date.valueOf(startDate);
            java.util.Date en_Date = java.sql.Date.valueOf(endDate);

            Log.d("날짜 ", "제목:" + obj.get("travel_title") + ", 현재:" + String.valueOf(nowDate) + ", 시작:" + String.valueOf(st_Date) + ", 종료:" + String.valueOf(en_Date));
            if (nowDate.compareTo(st_Date) != -1 && nowDate.compareTo(en_Date) != 1) { // 현재날짜가 여행중인 날짜이면 joinCode를 1로 세팅
                joinCode = 1;
                select_group_Code = obj.get("group_Code").toString().replaceAll("\"", "");
//                Log.d("여행 중", String.valueOf(joinCode));
            } else {
                if (joinCode == 1) {
//                    Log.d("여행 중 아닌데 이미 여행중인 날짜가 존재", String.valueOf(joinCode));
                    break;
                }
                joinCode = 0;
//                Log.d("여행 중 아님", String.valueOf(joinCode));
            }
        }
        Log.d("최종 joinCode값", String.valueOf(joinCode));
        smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
        editor = smartCost.edit();
        editor.putString("joinCode", String.valueOf(joinCode)); // 선택한 제목
        editor.commit();
    }

    private void SearhToDatabase(String group_Code) {
        SearhData task = new SearhData();
        Log.d("최지훈디비전송", group_Code);
        task.execute(group_Code); // 메소드를 실행한당
    }

    //AsyncTask 라는 스레드를 시작 시켜 DB연결
    class SearhData extends AsyncTask<String, Void, String> {

        //doInBackGround가 종료후 실행되는 메서드
        @Override
        protected void onPostExecute(String s) { // 웹 -> 앱으로 받는값

            super.onPostExecute(s);

            JsonObject obj = (JsonObject) new JsonParser().parse(s);

            smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
            editor = smartCost.edit();
            editor.putString("coin_Limit", obj.get("coin_Limit").toString().replaceAll("\"", "")); // 선택한 일정의 한도
            editor.putString("sc_Division", obj.get("sc_Division").toString().replaceAll("\"", "")); // 선택한 일정의 스마트 코스트 구분
            editor.commit();
        }

        @Override
        protected String doInBackground(String... params) { // 실행 메서드

            try {
                String link = "";
                String data = "";
                link = dataUrl.getServerUrl();; // 집 : 192.168.1.123, 학교 : 172.20.10.203, 에이타운 : 192.168.0.14

                Map<String, String> insertParam = new HashMap<String, String>(); // key, value

                String group_Code = (String) params[0];
                insertParam.put("group_Code", group_Code);

                link += "/scDivisionSearch";

                HttpClient.Builder http = new HttpClient.Builder("POST", link);

                http.addAllParameters(insertParam); // 앱 -> 스프링으로 데이터보냄, 매개값을

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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), TravelMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}