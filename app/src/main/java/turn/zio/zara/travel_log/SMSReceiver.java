package turn.zio.zara.travel_log;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.StringTokenizer;


/**
 * Created by Hoonhoon94 on 2017-06-12.
 */

public class SMSReceiver extends BroadcastReceiver {

    SharedPreferences smartCost;


    @Override
    public void onReceive(Context context, Intent intent) {

        String ReSwitch_Stat = InsertCoinSmsActivity.Switch_Stat;
        int ReJoinCode = TravelListActivity.joinCode;

        // SMS 문자 수신에 대한 이벤트는 문자열 Action으로 전달되며 intent에서 확인가능
            Bundle bundle = intent.getExtras();
            Object[] messages = (Object[]) bundle.get("pdus");

            SmsMessage[] smsMessage = new SmsMessage[messages.length];
            String[] token_arr = new String[6]; // 토큰의 값을 담을 배열 숫자 바꿔줘야함중요**************************************************************************/.ㅣㅔㅔ'/
            int tokencount = 0; // 토큰의 수
            String receive_data = ""; // 문자 수신정보
            String receive_money = ""; // 문자 내용(사용금액)
            String receive_content = ""; // 문자 내용(사용처)

            for (int i = 0; i < messages.length; i++) {
                smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
            }

            String message = smsMessage[0].getMessageBody().toString();
            String sender = smsMessage[0].getOriginatingAddress(); // 문자 발신처
            Log.d("onReceive: ", sender);
            if (sender.equals("01052700558")) { // 1588-1688 국민은행 01063340247
                StringTokenizer s = new StringTokenizer(message, "\n");
                tokencount = s.countTokens(); // 토큰사이즈 변수에 적용

                for (int i = 0; i < tokencount; i++) {
                    token_arr[i] = s.nextToken();
                    // Log.d(TAG, token_arr[i]);
                }

                // token_arr의 값을 각 변수에 저장
//                receive_data = token_arr[3].replace("/","").replace(" ","").replace(":",""); // 시간
                receive_money = token_arr[4].replace(",", "").replace("원", ""); // 금액
                receive_content = token_arr[5]; // 내용

//                Log.d(TAG, receive_data);
                Log.d("TAG", receive_money);
                Log.d("TAG", receive_content);

                Toast.makeText(context, "SMS문자 :" + message, Toast.LENGTH_LONG).show();
                insertToDatabase(receive_money, receive_content, TravelListActivity.select_group_Code, TravelListActivity.login_user_id); // 디비 실행
            }

    }

    // 디비입력
    private void insertToDatabase(String sc_coin, String sc_content, String groupCode, String userkeep) {
        SmartCostData smartdata = new SmartCostData();
        InsertMoney task = new  InsertMoney();
        task.execute(sc_coin, sc_content, smartdata.getGroup_code(), smartdata.getUser_id());
    }


}