package turn.zio.zara.travel_log;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hoonhoon94 on 2017-06-08.
 */

public class SmartCostAddActivity extends AppCompatActivity {
    private ListViewDialog mDialog;
    SharedPreferences travelStory;
    String selectGroupCode;
    ListView listview;
    ExpenseListViewAdapter exadapter;

    DataBaseUrl dataurl = new DataBaseUrl();

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_smart_cost_sub);

        ImageView storyDisplay = (ImageView) findViewById(R.id.storyDisplay) ;
        ImageView moneyDisplay = (ImageView) findViewById(R.id.moneyDisplay) ;
        ImageView mapDisplay = (ImageView) findViewById(R.id.mapDisplay) ;
        ImageView supplyDisplay = (ImageView) findViewById(R.id.supplyDisplay) ;
        ImageView groupDisplay = (ImageView) findViewById(R.id.groupDisplay) ;
        ImageView selectInsert = (ImageView) findViewById(R.id.selectInsert) ;

        storyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.story));
        moneyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.money1));
        mapDisplay.setImageDrawable(getResources().getDrawable(R.drawable.map));
        supplyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.supply1));
        groupDisplay.setImageDrawable(getResources().getDrawable(R.drawable.group1));
        selectInsert.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));


        travelStory = getSharedPreferences("title", MODE_PRIVATE);
        selectGroupCode = travelStory.getString("selectgroupCode", "0");
        String selectTitle = travelStory.getString("selectTitle", "0");

        TextView travelName = (TextView) findViewById(R.id.travelTitle); // 텍스트뷰 객체 선언

        insertToDatabase(selectGroupCode);
    }

    // 아이콘 클릭시 디비시작
    private void insertToDatabase(String selectGroupCode) {
        InsertData task = new InsertData();
        Log.d("디비시작한다이제 준비하셈", "시작한다!!");
        task.execute(selectGroupCode); // 메소드를 실행한당
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
                link = dataurl.getServerUrl(); // 집 : 192.168.1.123, 학교 : 172.20.10.203, 에이타운 : 192.168.0.14

                Map<String, String> insertParam = new HashMap<String, String>(); // key, value

                String group_Code = (String) params[0];
                insertParam.put("group_Code", selectGroupCode);

                link += "selectExpense";

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
        exadapter = new ExpenseListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.expenselistview);
        listview.setAdapter(exadapter);

        JsonArray json = (JsonArray) new JsonParser().parse(title);

////        Log.d("TAG", "onCreate: "+title);
        for (int i = 0; i < json.size(); i++) {
            Log.d("TAG Object", json.get(i).toString());
            JsonObject obj = json.get(i).getAsJsonObject(); // 오브젝트

            exadapter.addItem(obj.get("user_id").toString().replaceAll("\"", ""), obj.get("expense_Content").toString().replaceAll("\"", ""), obj.get("expense_Cost").toString().replaceAll("\"", ""));
//
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListViewItem listViewItem = (ListViewItem) adapterView.getItemAtPosition(i);
            }
        });

        // 현재날짜와 회원이 참여한 그룹의 날짜들을 비교하여 상태를 지정
        java.util.Date mDate = new java.util.Date(); // 현재날짜 구하기
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd"); // 형식지정
        String tempDate = mFormat.format(mDate); // 현재날짜 형식에 맞춰 초기화
        java.util.Date nowDate = java.sql.Date.valueOf(tempDate); //현재날짜

    }

    // 액티비티 전환시 애니메이션 제거
    public void onResume() {
        this.overridePendingTransition(0, 0);
        super.onResume();
    }

    public void travel_Story(View view) {
        Intent intent = new Intent(this, TravelStoryActivity.class);
        startActivity(intent);
    }

    public void travel_Map(View view) {
        Intent intent = new Intent(this, TravelMapActivity.class);
        startActivity(intent);
    }

    public void travel_Supply(View view) {
        Intent intent = new Intent(this, TravelSupplyActivity.class);
        startActivity(intent);
    }

    public void travel_Group(View view) {
        Intent intent = new Intent(this, TravelGroupActivity.class);
        startActivity(intent);
    }

    public void selectInsert(View v) {
        switch (v.getId()) {
            case R.id.selectInsert:
                Log.d("TAG", "click button list dialog.......");
                showListDialog();
                break;
        }
    }
    public void trvel_expensive(View v){
        Intent intent = new Intent(getApplicationContext(), SmartCostAddActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    private void showListDialog() {

        String[] item = getResources().getStringArray(R.array.list_dialog_money_item);

        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String>(listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.onOnSetItemClickListener(new ListViewDialog.ListViewDialogSelectListener() {


            @Override

            public void onSetOnItemClickListener(int position) {
                // TODO Auto-generated method stub
                    if (position == 0) {
                        Intent intent = new Intent(getApplicationContext(), InsertCoinSmsActivity.class); // 문자로 입력 작성화면으로 이동
                        startActivityForResult(intent, position);
                    } else if (position == 1) {
                        Intent intent = new Intent(getApplicationContext(), InsertCoinActivity.class); // 직접입력 작성화면으로 이동
                        startActivityForResult(intent, position);
                    } else if (position == 2) {
                        mDialog.dismiss();
                    }

                mDialog.dismiss();
            }
        });
        mDialog.show();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), TravelMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}