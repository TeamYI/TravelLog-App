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
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.util.concurrent.ExecutionException;

public class Life_LogModifyActivity extends AppCompatActivity {

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
    private String board_code;
    private String[][] parsedata;

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

        shareGroup.setChecked(false);
        shareAll.setChecked(true);
        shareMe.setChecked(false);

        Intent intent = getIntent();
        board_code = intent.getExtras().getString("board_code");
        String boder_Title = intent.getExtras().getString("board_Title");
        String board_Content = intent.getExtras().getString("board_Content");
        String file_Type = intent.getExtras().getString("file_Type");
        String file_Content = intent.getExtras().getString("file_Content");

        log_Title.setHint(boder_Title);
        log_Content.setHint(board_Content);

        listAll hashTag = new listAll();
        String json = null;
        try {
            json = hashTag.execute(board_code).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("json", json);
        String hashTagText = "";

        try {
            JSONArray jsons = new JSONArray(json);
            parsedata = new String[jsons.length()][2];
            for (int i = 0; i < jsons.length(); i++) {
                JSONObject jobject = jsons.getJSONObject(i);

                parsedata[i][0] = jobject.getString("hash_tag_content");
                hashTagText += parsedata[i][0].replace("#", "") + " ";
                Log.d("hashTagText", hashTagText);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hash_text.setText(hashTagText);

        if (file_Type.equals("0")) {
            Drawable drawable = getResources().getDrawable(R.drawable.addfile);
            image.setImageDrawable(drawable);
        } else if (file_Type.equals("1")) {
            addFilepic add = new addFilepic();
            add.execute(file_Content);
        } else if (file_Type.equals("2")) {
            Drawable drawable = getResources().getDrawable(R.drawable.voice);
            image.setImageDrawable(drawable);
        }

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();

        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        user_id = user.getString("user_id", "0");


        hash_write.setOnFocusChangeListener(new View.OnFocusChangeListener() { // 포커스를 얻으면
            @Override
            public void onFocusChange(View v, boolean hasFocus) { // 포커스가 한뷰에서 다른뷰로 바뀔때
                if (hasFocus == false) {
                    String hashtest = hash_write.getText().toString();
                    hash_write.setVisibility(View.GONE);
                    hash_text.setVisibility(View.VISIBLE);
                    if (!hashtest.equals("")) {
                        hash_text.setText(hashtest);
                    } else {
                        hash_text.setText("해시태그 입력");
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
            }
        });
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

    class addFilepic extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog loading;
        private Bitmap bmImg2;


        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String file_Content = (String) params[0];
                String url = dataurl.getTumnailUrl() + file_Content;
                Log.d("url", url);
                InputStream is = (InputStream) new URL(url).getContent();

                bmImg2 = BitmapFactory.decodeStream(is);


                return bmImg2;

                // Read Server Response

            } catch (Exception e) {
                bmImg2 = null;
                return bmImg2;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            image.setImageBitmap(s);
            this.cancel(true);
        }
    }

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
                loading = ProgressDialog.show(Life_LogModifyActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                if (s.equals("success")) {
                    Toast.makeText(getApplicationContext(), "수정 성공", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "수정 실패", Toast.LENGTH_SHORT).show();
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
                        String testStr = "ABCDEFGHIJK...";
                        File savefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/log.txt");
                        FileOutputStream fos = new FileOutputStream(savefile);
                        fos.write(testStr.getBytes());
                        fos.close();
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/log.txt");
                        mFileInputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/log.txt");
                    }

                    URL url = new URL(dataurl.getServerUrl() + "updateBoard"); //요청 URL을 입력
                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    //conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());


                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"board_code\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write((board_code + "").getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);
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
                } finally {
                    conn.disconnect();
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
            list = geocoder.getFromLocation(lat, lng, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list == null) {
            Log.e("getAddress", "주소 데이터 얻기 실패");
            return null;
        }
        if (list.size() > 0) {
            Address addr = list.get(0);
            address = addr.getAdminArea() + " "
                    + addr.getLocality() + " "
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

    class listAll extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String board_code = (String) params[0];

                Map<String, String> loginParam = new HashMap<String, String>();

                loginParam.put("board_code", board_code);

                String link = dataurl.getServerUrl() + "hashTagList"; //92.168.25.25
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(Life_LogModifyActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            this.cancel(true);
            loading.dismiss();
        }
    }
}
