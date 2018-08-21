package turn.zio.zara.travel_log;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StepLogActivity extends Activity {

    private RadioButton shareAll;
    private EditText log_Title;
    private RadioButton shareGroup;
    private RadioButton shareMe;

    private ArrayList<LocationInfo> steparr;
    private String user_id;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    DataBaseUrl dataurl = new DataBaseUrl();
    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/kml/";
    private String pathName;

    SharedPreferences login;
    SharedPreferences.Editor editor;
    private SharedPreferences stepkeep;
    private String steplogkeep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_log);
        
        stepkeep = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = stepkeep.edit();
        steplogkeep = stepkeep.getString("steplogkeep", "0");
        
        log_Title = (EditText) findViewById(R.id.view_Travel_logTitle);
        shareAll = (RadioButton) findViewById(R.id.share_all_button);
        shareGroup = (RadioButton) findViewById(R.id.share_group_button);
        shareMe = (RadioButton) findViewById(R.id.share_me_button);

        steparr = new ArrayList<LocationInfo>();

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        int stepsize = intent.getIntExtra("stepsize", 0);
        double[] latitude = intent.getDoubleArrayExtra("latitude");
        double[] longitude = intent.getDoubleArrayExtra("longitude");

        for (int i = 0; i < stepsize; i++) {
            steparr.add(new LocationInfo(latitude[i], longitude[i]));
        }

    }

    public void share_all(View view) {
        shareGroup.setChecked(false);
        shareAll.setChecked(true);
        shareMe.setChecked(false);
    }

    public void share_group(View view) {
        shareAll.setChecked(false);
        shareGroup.setChecked(true);
        shareMe.setChecked(false);
    }

    public void share_me(View view) {
        shareGroup.setChecked(false);
        shareMe.setChecked(true);
        shareAll.setChecked(false);
    }

    public void step_submit(View view) {
        String Title = log_Title.getText().toString();
        String share = null;

        CreateKMLFile createKml = new CreateKMLFile();
        pathName = createKml.createKML(steparr, user_id);

        if (shareAll.isChecked()) {
            share = "1";
        } else if (shareGroup.isChecked()) {
            share = "2";
        } else if (shareMe.isChecked()) {
            share = "3";
        }
        Step_log_Write(Title, share, user_id);

    }

    public void step_finish(View view) {
        step_delte(user_id);
        finish();
    }

    private void step_delte(String user_id) {

        class deleteDate extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                steplogkeep = "0";
                editor.putString("steplogkeep", steplogkeep);
                editor.commit();
                NotificationManager nm =
                        (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                // 등록된 notification 을 제거 한다.
                nm.cancel(1);
            }

            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection conn = null;
                StringBuilder sb = new StringBuilder();

                try {

                    String user_id = params[0];

                    Map<String, String> insertParam = new HashMap<String, String>();

                    insertParam.put("user_id", user_id);

                    String link = dataurl.getServerUrl() + "stepdelete"; //92.168.25.25
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
                    e.printStackTrace();
                }
                return sb.toString();
            }
        }

        deleteDate task = new deleteDate();
        task.execute(user_id);
    }

    private void Step_log_Write(final String Title, String share_Type, String user_id) {

        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(StepLogActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                if (s.equals("success")) {
                    steplogkeep = "0";
                    editor.putString("steplogkeep", steplogkeep);
                    editor.commit();
                    NotificationManager nm =
                            (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

                    // 등록된 notification 을 제거 한다.
                    nm.cancel(1);

                    Toast.makeText(getApplicationContext(), "업로드 성공", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                }
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection conn = null;
                StringBuilder sb = new StringBuilder();

                try {
                    String log_Title = params[0];
                    String share_Type = params[1];
                    String user_id = params[2];

                    File file = new File(path + pathName);
                    FileInputStream mFileInputStream = new FileInputStream(file);

                    URL url = new URL(dataurl.getServerUrl() + "stepUpdate"); //요청 URL을 입력
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    //conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                    ;

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"step_Title\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(log_Title.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"share_type\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(share_Type.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"user_id\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(user_id.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    Log.d("dd", path + pathName);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + path + pathName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    int bytesAvailable = mFileInputStream.available();
                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                    byte[] buffer = new byte[bufferSize];
                    int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = mFileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                    conn.connect();
                    if (mFileInputStream != null) {
                        mFileInputStream.close();
                    }

                    dos.flush(); // finish upload...
                    dos.close();

                    int ch;
                    InputStream is;
                    int status = conn.getResponseCode();

                    if (status == HttpURLConnection.HTTP_OK) {
                        is = conn.getInputStream();
                        return "success";
                    } else {
                        return "filed";
                    }
                    /*
                    BufferedReader rd = null;
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC_KR"));
                    String line = null;
                    while ((line = rd.readLine()) != null) {
                        Log.d("BufferedReader: ", line);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }

                return sb.toString();
            }
        }

        insertData task = new insertData();
        task.execute(Title, share_Type, user_id);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
