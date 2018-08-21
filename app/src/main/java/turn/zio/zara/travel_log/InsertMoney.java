package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 하루마다 on 2017-06-26.
 */

public class InsertMoney extends AsyncTask<String, String, String> {
    ProgressDialog loading;

    DataBaseUrl dataurl = new DataBaseUrl();

    //처음 execute시 실행되는 메소드
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("InsertCoinActivity", "금액입력 정보 전송 전");
    }

    //doInBackGround가 종료후 실행되는 메소드
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d("InsertCoinActivity", "금액입력 정보 전송 후");
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            String link = "";
            String data = "";
            link = dataurl.getServerUrl(); // 집 : 192.168.1.123, 학교 : 172.20.10.203

            Map<String, String> insertParam = new HashMap<String, String>();

            String sc_coin = (String) params[0];
            String sc_content = (String) params[1];
            String groupCode = (String) params[2];
            String userkeep = (String) params[3];

            link += "expensive";

            insertParam.put("expense_Cost", sc_coin);
            insertParam.put("expense_Content", sc_content);
            insertParam.put("group_Code", groupCode);
            insertParam.put("user_id", userkeep);

            HttpClient.Builder http = new HttpClient.Builder("POST", link);

            http.addAllParameters(insertParam);

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

