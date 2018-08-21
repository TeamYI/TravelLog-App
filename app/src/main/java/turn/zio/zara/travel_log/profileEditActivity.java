package turn.zio.zara.travel_log;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public class profileEditActivity extends AppCompatActivity {
    private ImageView profile_Picutre;
    private TextView change_user_id;
    private TextView change_email;
    private TextView change_number;
    DataBaseUrl dataurl = new DataBaseUrl();

    Spinner userGender;

    String profile_img;
    String profile_ori;

    private boolean regicheck2;

    SharedPreferences login;
    SharedPreferences.Editor editor;
    private String user_id;
    private String prifile_pict;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        profile_Picutre = (ImageView) findViewById(R.id.profile_picture);
        change_user_id = (TextView) findViewById(R.id.change_user_id);
        change_email = (TextView) findViewById(R.id.change_email);
        change_number = (TextView) findViewById(R.id.change_number);

        userGender = (Spinner) findViewById(R.id.user_gender);
        ArrayAdapter userAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_gender, android.R.layout.simple_spinner_item);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGender.setAdapter(userAdapter);
        profile_Picutre.setBackground(new ShapeDrawable(new OvalShape()));
        profile_Picutre.setClipToOutline(true);


        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        ImageView profile_picture = (ImageView)findViewById(R.id.profile_picture);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.addfile));
        ImageView change_user_id_icon = (ImageView)findViewById(R.id.change_user_id_icon);
        change_user_id_icon.setImageDrawable(getResources().getDrawable(R.drawable.pass_update_lock));
        ImageView change_email_icon = (ImageView)findViewById(R.id.change_email_icon);
        change_email_icon.setImageDrawable(getResources().getDrawable(R.drawable.pass_update_lock));
        ImageView change_number_icon = (ImageView)findViewById(R.id.change_number_icon);
        change_number_icon.setImageDrawable(getResources().getDrawable(R.drawable.pass_update_lock));
        ImageView change_gender_icon = (ImageView)findViewById(R.id.change_gender_icon);
        change_gender_icon.setImageDrawable(getResources().getDrawable(R.drawable.pass_update_lock));

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();
        user_id = login.getString("user_id", "0");
        prifile_pict = login.getString("prifile_picture", "default.png");
        profile_pic();
    }

    /*프로필 사진*/
    private void profile_pic() {
        final Bitmap[] resizedBitmaps = new Bitmap[1];
        class write extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                this.cancel(true);
                profile_Picutre.setScaleType(ImageView.ScaleType.FIT_XY);
                profile_Picutre.setImageBitmap(resizedBitmaps[0]);
            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    String url = dataurl.getProfile() + prifile_pict;
                    Log.d("profile", url);
                    InputStream is = (InputStream) new URL(url).getContent();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = false;
                    resizedBitmaps[0] = BitmapFactory.decodeStream(is, null, options);
                    return "success";
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }
        write task = new write();
        task.execute();
    }

    public void bakcMain(View view) {
        finish();
    }

    public void profile_picture_chagne(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    public void profile_submit(View view) {
        String c_user_id = change_user_id.getText().toString();
        String c_email = change_email.getText().toString();
        String c_number = change_number.getText().toString();
        String c_user_gencer = userGender.getSelectedItem().toString();
        Log.d("gender", c_user_gencer);
        if (c_user_gencer.equals("남자")) {
            c_user_gencer = "0";
        } else {
            c_user_gencer = "1";
        }
        Log.d("gender", c_user_gencer);
        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();


        //SharedPreferences값이 있으면 유저아이디를 없으면 널값을
        SharedPreferences user = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        String this_user = user.getString("user_id", "0");


        if (c_email.equals(" ")) {
            regicheck2 = true;
        } else {
            checkrole(c_email, c_number);
        }
        if (c_number.equals(" ")) {
            regicheck2 = true;
        } else {
            checkrole(c_email, c_number);
        }

        if (c_user_id.isEmpty()) {
            regicheck2 = false;
            Toast.makeText(getApplicationContext(), "변경할 아이디를 입력해주세요.", Toast.LENGTH_LONG).show();
            change_user_id.requestFocus();
        } else if (this_user.equals(c_user_id)) {
            regicheck2 = false;
            Toast.makeText(getApplicationContext(), "현재 아이디 입니다.", Toast.LENGTH_LONG).show();
            change_user_id.requestFocus();
        }
        if (regicheck2 == true) {
            Change_user_profile(this_user, c_user_id, c_email, c_number, c_user_gencer);
        }
    }

    private void Change_user_profile(String this_id, String c_user_id, String c_user_email, String c_user_phone, String c_user_gencer) {

        class Change_user extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            HttpURLConnection conn = null;
            StringBuilder sb = new StringBuilder();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(profileEditActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("result", s);
                if (s.equals("idcheck")) {
                    Toast.makeText(getApplicationContext(), "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                    change_user_id.requestFocus();
                } else if (s.equals("success")) {
                    Toast.makeText(getApplicationContext(), "회원정보 업데이트 완료 다시 로그인 해주세요.", Toast.LENGTH_SHORT).show();
                    Intent intent = getIntent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "오류 발생.", Toast.LENGTH_SHORT).show();
                }
                loading.dismiss();

            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String this_id = (String) params[0];
                    String c_user_id = (String) params[1];
                    String c_user_email = (String) params[2];
                    String c_user_phone = (String) params[3];
                    String c_user_gencer = (String) params[4];

                    FileInputStream mFileInputStream = null;
                    if(profile_img!=null) {
                        File file = new File(profile_img);
                        mFileInputStream = new FileInputStream(file);
                    }else {
                        String testStr = "ABCDEFGHIJK...";
                        File savefile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/profile.txt");
                        FileOutputStream fos = new FileOutputStream(savefile);
                        fos.write(testStr.getBytes());
                        fos.close();
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/profile.txt");
                        mFileInputStream = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/travelLog/profile.txt");
                    }
                        URL url = new URL(dataurl.getServerUrl() + "change_profile"); //요청 URL을 입력

                    conn = (HttpURLConnection) url.openConnection();

                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    //conn.setConnectTimeout(60); //타임아웃 시간 설정 (default : 무한대기)
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"this_id\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(this_id.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);

                    if(!c_user_id.equals("")) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"c_user_id\""
                                + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.write(c_user_id.getBytes("EUC_KR"));
                        dos.writeBytes(lineEnd);
                        Log.d("아이디", c_user_id+"유저아이디");
                    }

                    if(!c_user_email.equals("")) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"c_user_email\""
                                + lineEnd);
                        dos.writeBytes(lineEnd);
                        String board_type = "1";
                        dos.write(c_user_email.getBytes("EUC_KR"));
                        dos.writeBytes(lineEnd);
                        Log.d("이메일주소", c_user_email+"이메일주소");
                    }

                    if(!c_user_phone.equals("")) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"c_user_phone\""
                                + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.write(c_user_phone.getBytes("EUC_KR"));
                        dos.writeBytes(lineEnd);
                        Log.d("전화번호", c_user_phone+"전화번호");
                    }

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"c_user_gencer\""
                            + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.write(c_user_gencer.getBytes("EUC_KR"));
                    dos.writeBytes(lineEnd);
                    Log.d("성별", c_user_gencer+"성별");


                    if (profile_img != null) {
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        Log.d("dd", profile_img);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + profile_img + "\"" + lineEnd);
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
                    }else {
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
                    } else if(status == HttpURLConnection.HTTP_CREATED){
                        return "idcheck";
                    }else{
                        return "failed";
                    }
                    // Read Server Response

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        Change_user task = new Change_user();
        task.execute(this_id, c_user_id, c_user_email, c_user_phone, c_user_gencer);
    }

    public void checkrole(String email, String phone) {
        regicheck2 = false;
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty()) {
            Toast.makeText(getApplicationContext(), "이메일형식이 아닙니다.", Toast.LENGTH_LONG).show();
            change_email.requestFocus();
        } else if (!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", phone) && !phone.isEmpty()) {
            Toast.makeText(getApplicationContext(), "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_LONG).show();
            change_number.requestFocus();
        } else {
            regicheck2 = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    //이미지 데이터를 비트맵으로 받아온다.
                    //전송할 데이터
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    int width = image_bitmap.getWidth();
                    int height = image_bitmap.getHeight();

                    getImageName(data.getData());
                    Log.d("Dd", profile_ori);
                    Matrix matrix = new Matrix();
                    if (profile_ori.equals("180")) {
                        matrix.postRotate(180);
                    } else if (profile_ori.equals("270")) {
                        matrix.postRotate(270);
                    } else if (profile_ori.equals("90")) {
                        matrix.postRotate(90);
                    } else {
                        matrix.postRotate(0);
                    }
                    //배치해놓은 ImageView에 set

                    //화면에 표시할 데이터

                    Bitmap resizedBitmap = Bitmap.createBitmap(image_bitmap, 0, 0, width, height, matrix, true);

                    profile_Picutre.setImageBitmap(resizedBitmap);
                    profile_Picutre.setScaleType(ImageView.ScaleType.FIT_XY);


                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getImageName(Uri data) {
        Log.d("Dd", data + "");
        String[] proj = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.ORIENTATION,
        };
        Cursor cursor = this.getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();

        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        int column_title = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
        int column_ori = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);

        profile_img = cursor.getString(column_data);
        profile_ori = cursor.getString(column_ori);
    }

}
