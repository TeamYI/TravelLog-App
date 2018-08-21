package turn.zio.zara.travel_log;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import static turn.zio.zara.travel_log.CameraOverlayView.hashTag;
import static turn.zio.zara.travel_log.CameraOverlayView.order_DB;

public class CameraActivity extends AppCompatActivity {

    FrameLayout previewFrame;
    private CameraOverlayView mOverlayView = null;
    LocationManager lm;
    DataBaseUrl dataurl = new DataBaseUrl();

    String s;
    MyLocation myLocation = new MyLocation();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);

        ImageView home_icon = (ImageView) findViewById(R.id.home_icon);
        ImageView mode_change = (ImageView) findViewById(R.id.mode_change);
        ImageView filrer_icon = (ImageView) findViewById(R.id.filrer_icon);

        home_icon.setImageDrawable(getResources().getDrawable(R.drawable.ar_homepage));
        mode_change.setImageDrawable(getResources().getDrawable(R.drawable.search_on));
        filrer_icon.setImageDrawable(getResources().getDrawable(R.drawable.ar_fillter));

        //카메라 화면 보여주기
        final CameraSurfaceView cameraView = new CameraSurfaceView(getApplicationContext());

        //AR 데이터 보여주기
        mOverlayView = new CameraOverlayView(this);


        FrameLayout previewFrame = (FrameLayout) findViewById(R.id.previewFrame);
        previewFrame.addView(cameraView);

        //폰의 디스플레이를 구하기
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        //폰의 디스플레이에 따라 AR 화면의 디스플레이 셋팅
        mOverlayView.setOverlaySize(dm.widthPixels, dm.heightPixels);
        addContentView(mOverlayView, new LayoutParams(dm.widthPixels, dm.heightPixels));

        MyLocation myLocation = new MyLocation();

        double longitude = myLocation.getLongitude(); //경도
        double latitude = myLocation.getLatitude();   //위도
        //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
        //Network 위치제공자에 의한 위치변화
        //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
        //AR에 내 위도 경도 셋팅
        mOverlayView.setCurrentPoint(longitude, latitude);


        //위치정보 획득
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);

        //checkDangerousPermissions();
    }

    class arData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {

                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("order_DB", order_DB);
                seldata.put("hashTag", hashTag);

                String link = dataurl.getServerUrl() + "boardList"; //92.168.25.25
                HttpClient.Builder http = new HttpClient.Builder("GET", link);

                http.addAllParameters(seldata);

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
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    //권한 체크
    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);

            MyLocation myLocation = new MyLocation();

            double longitude = myLocation.getLongitude(); //경도
            double latitude = myLocation.getLatitude();   //위도
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            //AR에 내 위도 경도 셋팅

            arData task = new arData();

            try {
                s = task.execute().get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mOverlayView.arData(s);
            mOverlayView.setCurrentPoint(longitude, latitude);
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

    // 액티비티가 소멸될때 위치 리스너와 오버레이 뷰의 자원을 해제해줌
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        mOverlayView.viewDestory();
        lm.removeUpdates(mLocationListener);
        super.onDestroy();
    }

    public void onBackPressed() {

        RecycleUtils RecursiveUtils = new RecycleUtils();
        RecursiveUtils.recursiveRecycle(getWindow().getDecorView());
        System.gc();
        this.finish();
        overridePendingTransition(0, android.R.anim.slide_out_right);
    }

    public void Filter(View view) {
        CameraOverlayView.DBselect = false;
        Intent intent = new Intent(this, ARFilterActivity.class);
        startActivity(intent);
    }

    public void backmain(View view) {
        finish();
    }

    public void modechange(View view) {
        if(mOverlayView.getJsonData() != null && !mOverlayView.getJsonData().equals("")) {
            mOverlayView.drawtext = false;
            Intent intent = new Intent(this, popListView.class);
            intent.putExtra("jsonData", mOverlayView.getJsonData());
            intent.putExtra("mlongitude", mOverlayView.getLong());
            intent.putExtra("mlatitude", mOverlayView.getLat());
            mOverlayView.mTouched = false;
            startActivity(intent);
        }
    }

    @Override
    public void onResume(){
        arData task = new arData();

        try {
            s = task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mOverlayView.arData(s);
        super.onResume();
    }
}
