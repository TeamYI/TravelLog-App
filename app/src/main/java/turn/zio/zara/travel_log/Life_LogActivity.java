package turn.zio.zara.travel_log;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Life_LogActivity extends AppCompatActivity {

    private String mImgPath = null;
    private String mImgTitle = null;
    private String mImgOri = null;
    String voiceData = null;
    private String user_id;

    FileInputStream mFileInputStream;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";

    private EditText log_Content;
    private EditText log_Title;
    private ImageView image;
    private ListViewDialog mDialog;
    private TextView place_info;
    private TextView hash_text;
    private TextView shareAllText;
    private TextView shareGroupText;
    private TextView shareMeText;
    private EditText hash_write;
    private LinearLayout viewvisible;
    private LinearLayout background;
    private RadioButton shareAll;
    private RadioButton shareGroup;
    private RadioButton shareMe;

    private double latitude;
    private double longitude;
    private String share;
    List<Object> hash = new ArrayList<Object>();

    LocationManager lm;
    DataBaseUrl dataurl = new DataBaseUrl();

    SharedPreferences login;
    SharedPreferences.Editor editor;
    private String arr;
    private String stepLogCode;
    private String pushalram;
    private LinearLayout group;

    MyLocation myLocation = new MyLocation();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_log);

        log_Content = (EditText) findViewById(R.id.view_Travel_logTxt);
        log_Title = (EditText) findViewById(R.id.view_Travel_logTitle);
        place_info = (TextView) findViewById(R.id.user_place_info);
        image = (ImageView) findViewById(R.id.addFile);
        hash_text = (TextView) findViewById(R.id.hash_test_view);
        hash_write = (EditText) findViewById(R.id.hash_text_write);
        shareAll = (RadioButton) findViewById(R.id.share_all_button);
        shareGroup = (RadioButton) findViewById(R.id.share_group_button);
        shareMe = (RadioButton) findViewById(R.id.share_me_button);
        shareAllText = (TextView) findViewById(R.id.share_all);
        shareGroupText = (TextView) findViewById(R.id.share_group);
        shareMeText = (TextView) findViewById(R.id.share_me);
        background = (LinearLayout) findViewById(R.id.background_logWrite_view);
        viewvisible = (LinearLayout) findViewById(R.id.viewvisible);
        group = (LinearLayout) findViewById(R.id.group);

        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        Drawable drawable = getResources().getDrawable(R.drawable.addfile);
        image.setImageDrawable(drawable);

        group.setVisibility(View.GONE);

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();

        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        user_id = user.getString("user_id", "0");
        pushalram = user.getString("pushAlram", "0");

        Log.d("puah",pushalram);


        Intent intent = getIntent();
        String step_Log = intent.getStringExtra("stepLog");

        if (step_Log.equals("1")) {
            StepInsert(user_id);
            viewvisible.setVisibility(View.GONE);
            shareAll.setChecked(true);
        } else {
            stepLogCode = "0";
            viewvisible.setVisibility(View.VISIBLE);
        }

        searchTravel serch = new searchTravel();
        serch.execute();

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);

        hash_write.setOnFocusChangeListener(new View.OnFocusChangeListener() { // 포커스를 얻으면
            @Override
            public void onFocusChange(View v, boolean hasFocus) { // 포커스가 한뷰에서 다른뷰로 바뀔때
                if (hasFocus == false) {
                    String hashhint = hash_write.getHint().toString();
                    String hashtest = hash_write.getText().toString();
                    hash_write.setVisibility(View.GONE);
                    hash_text.setVisibility(View.VISIBLE);
                    if (!hashtest.equals("")) {
                        hash_text.setText(hashtest);
                    } else {
                        hash_text.setText(hashhint);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
    }

    class searchTravel extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("user_id", user_id);
                DBserver = "search_travel";

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
            Log.d("share", s);
            if(s.equals("1")){
                group.setVisibility(View.VISIBLE);
            }
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

    /*스탭로그 켜져있을시 스탭로그의 code*/
    private void StepInsert(String user_id) {

        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Life_LogActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s + "출력");
                if (!s.equals("")) {
                    stepLogCode = s;
                }
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);

                    String link = dataurl.getServerUrl() + "stepCodeSelect"; //92.168.25.25
                    HttpClient.Builder http = new HttpClient.Builder("POST", link);

                    http.addAllParameters(loginParam);

                    // HTTP 요청 전송
                    HttpClient post = http.create();
                    post.request();
                    // 응답 상태코드 가져오기
                    int statusCode = post.getHttpStatusCode();
                    // 응답 본문 가져오기
                    String body = post.getBody();
                    return body;

                    // Read Server Response

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }
        insertData task = new insertData();
        task.execute(user_id);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            MyLocation myLocation = new MyLocation();

            double longitude = myLocation.getLongitude(); //경도
            double latitude = myLocation.getLatitude();   //위도

            place_info.setText(getAddress(latitude, longitude));
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    public void modeWrite(View view) {
        String hashtest = (String) hash_text.getText();
        hash_text.setVisibility(view.GONE);
        hash_write.setVisibility(view.VISIBLE);
        if (hashtest.equals("해시태그 입력")) {
            hash_write.setHint(hashtest);
        } else {
            hash_write.setText(hashtest);
        }
        hash_write.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

    }

    public void Travel_log_submit(View view) {
        String Title = log_Title.getText().toString();
        String Content = log_Content.getText().toString();
        String hashtag = hash_write.getText().toString();

        if (shareAll.isChecked()) {
            share = "1";
        } else if (shareGroup.isChecked()) {
            share = "2";
        } else if (shareMe.isChecked()) {
            share = "3";
        }

        Travel_log_Write(Title, Content, hashtag);
    }

    public void addFile(View v) {
        switch (v.getId()) {
            case R.id.addFile:
                Log.d("TAG", "click button list dialog.......");
                showListDialog();
                break;
        }
    }

    private void Travel_log_Write(final String Title, String Contnet, String hashtag) {

        class insertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Life_LogActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                if (s.equals("success")) {
                    Toast.makeText(getApplicationContext(), "업로드 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    editor.putString("pushAlram", "0");
                    editor.commit();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                }
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {
                HttpURLConnection conn = null;
                StringBuilder sb = new StringBuilder();

                String file_Type = "0";
                try {
                    String log_Title = params[0];
                    String log_Content = params[1];
                    String hash_tag = params[2];

                    if (mImgPath != null) {
                        file_Type = "1";
                        File file = new File(mImgPath);
                        mFileInputStream = new FileInputStream(file);
                    } else if (voiceData != null) {
                        File file = new File(voiceData);
                        file_Type = "2";
                        mFileInputStream = new FileInputStream(file);
                    } else {

                    }

                    URL url = new URL(dataurl.getServerUrl() + "insertLog"); //요청 URL을 입력
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
                    dos.writeBytes("Content-Disposition: form-data; name=\"log_Title\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(log_Title.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"log_Content\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(log_Content.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"hash_tag\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(hash_tag.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"log_longtitude\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    String longti = Double.toString(longitude);
                    dos.write(longti.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"log_latitude\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    String lati = Double.toString(latitude);
                    dos.write(lati.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"share_type\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(share.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file_Type\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(file_Type.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"board_type\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    String board_type = "1";
                    dos.write(board_type.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"user_id\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(user_id.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    if (!stepLogCode.equals("0")) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"step_log\""
                                + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.write(stepLogCode.getBytes("EUC_KR"));
                        dos.writeBytes(lineEnd);
                    }
                    if (!pushalram.equals("0")) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"pushalram\""
                                + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.write(FirebaseMessagingService.push_board_code.getBytes("EUC_KR"));
                        dos.writeBytes(lineEnd);
                    }
                    if (mImgPath != null) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        Log.d("dd", mImgPath);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + mImgPath + "\"" + lineEnd);
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
                    } else if (voiceData != null) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + voiceData + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);


                        int bytesAvailable = mFileInputStream.available();
                        int maxBufferSize = 1 * 1024 * 1024;
                        int bufferSize = Math.min(bytesAvailable, maxBufferSize);

                        bytesAvailable = mFileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        byte[] buffer = new byte[bufferSize];

                        // Read file
                        int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = mFileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                        }
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    } else {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"null\";filename=\"" + null + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    }

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
                }

                return sb.toString();
            }
        }

        insertData task = new insertData();
        task.execute(Title, Contnet, hashtag);
    }

    private void showListDialog() {

        String[] item = getResources().getStringArray(R.array.list_dialog_list_item);

        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String>(listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);
        mDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDialog.onOnSetItemClickListener(new ListViewDialog.ListViewDialogSelectListener() {


            @Override

            public void onSetOnItemClickListener(int position) {
                // TODO Auto-generated method stub

                if (position == 0) {
                    Intent intent = new Intent(getApplicationContext(), AlbumSelectActivity.class);
                    startActivityForResult(intent, position);
                } else if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), VoiceRecording.class);
                    startActivityForResult(intent, position);
                } else if (position == 2) {
                    image.setImageBitmap(null);
                    mImgPath = null;
                    voiceData = null;
                    mDialog.dismiss();
                }
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }


    /**
     * 위도와 경도 기반으로 주소를 리턴하는 메서드
     */
    public String getAddress(double lat, double lng) {
        String address = null;

        //위치정보를 활용하기 위한 구글 API 객체
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        //주소 목록을 담기 위한 HashMap
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(35.896533, 128.620363, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) {
            Log.e("getAddress", "주소 데이터 얻기 실패");
            return null;
        }
        if (list.size() > 0) {
            Address addr = list.get(0);
            address = addr.getLocality() + " "
                    + addr.getThoroughfare() + " ";
        }
        return address;
    }

    public void bakcMain(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                try {

                    voiceData = null;
                    arr = data.getExtras().getString("data");

                    File imgFile = new File(arr);
                    ExifInterface exif = null;
                    Matrix matrix = null;
                    Log.d("그거", arr);
                    try {
                        exif = new ExifInterface(arr);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                        matrix = new Matrix();
                        mImgPath = arr;
                        Log.d("이름", mImgPath);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    int width = myBitmap.getWidth();
                    int height = myBitmap.getHeight();
                    Bitmap b2 = Bitmap.createBitmap(myBitmap, 0, 0, width, height, matrix, true);

                    image.setImageBitmap(b2);
                    image.setScaleType(ImageView.ScaleType.FIT_XY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                mImgPath = null;
                voiceData = data.getStringExtra("VoicePath");
                Drawable drawable = getResources().getDrawable(R.drawable.voice);
                image.setImageDrawable(drawable);

            }
        }

    }
}
