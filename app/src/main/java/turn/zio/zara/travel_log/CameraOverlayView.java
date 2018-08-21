package turn.zio.zara.travel_log;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by 하루마다 on 2017-04-29.
 */

public class CameraOverlayView extends View implements SensorEventListener {

    private static float mXCompassDegree;
    private static float mYCompassDegree;
    private CameraActivity mContext;
    private SensorManager mSensorManager;
    private Sensor mOriSensor;
    private double mlongitude;
    private double mlatitude;
    public static double mVisibleDistance = 0.25;
    private int mWidth;
    private int mHeight;
    private int mShadowXMargin;
    private Paint mbackgroundPaint;
    private int mShadowYMargin;
    private Paint mPaint;
    private Paint mShadowPaint;
    private int mTouchedItem;
    private List<PointF> mPointFList = null;
    private List<int[]> PointHashMap = null;
    private List<int[]> boxsize = null;
    public boolean mTouched = false;

    DataBaseUrl dataurl = new DataBaseUrl();
    String s = null;
    private int item_code;
    public static boolean DBselect = true;
    public static String order_DB = "1";
    public static String hashTag;
    public static boolean drawtext;

    public CameraOverlayView(Context context) {
        super(context);
        mContext = (CameraActivity) context;


        hashTag = "없음";
        DBselect = true;
        drawtext = true;

        // 비트맵, 센서, 페인트, DB 핸들러 초기화
        initSensor(context);
        initPaints();

        arData task = new arData();

        try {
            s = task.execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("dd",s);
    }

    public void onDraw(Canvas canvas) {
        canvas.save();

        // DB의 레코드를 읽어들이고, 그림

        interpretDB(canvas);
        // 회전된 카메라를 원상복귀함
        canvas.restore();
        // 아이템이 터치된 상태일때 팝업을 그림
        if (mTouched == true) {
            drawPopup();
        }
    }

    public void arData(String s){
        this.s = s;
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
            Log.d("ardata",s);
        }
    }

    private void initPaints() {
        // TODO Auto-generated method stub
        mShadowXMargin = 2;
        mShadowYMargin = 2;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.rgb(238, 229, 222));
        mPaint.setTextSize(40);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.BLACK);
        mShadowPaint.setTextSize(40);

        mbackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mbackgroundPaint.setColor(Color.rgb(32, 178, 170));

    }


    // 센서 초기화
    // TYPE_ORIENTATION 사용할수 있게 설정
    private void initSensor(Context context) {
        // TODO Auto-generated method stub
        mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mOriSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensorManager.registerListener(this, mOriSensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

            mXCompassDegree = event.values[0];
            mYCompassDegree = event.values[1];

            this.invalidate();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        // 화면이 회전되었기에 좌표도 변환함
        float convertedX, convertedY, temp;
        convertedX = event.getX();
        convertedY = event.getY();
        convertedX = convertedX - mWidth / 2;
        convertedY = convertedY - mHeight / 2;
        Log.d("convertedX", convertedX + "");
        Log.d("convertedY", convertedY + "");

        mTouched = false;


        PointF tPoint = new PointF();

        Iterator<int[]> iterator = PointHashMap.iterator();
        Iterator<int[]> sizeiterator = boxsize.iterator();
        Iterator<PointF> pointIterator = mPointFList.iterator();
        for (int i = 0; i < mPointFList.size(); i++) {
            tPoint = pointIterator.next();

            int[] box = sizeiterator.next();
            int item_key = box[0];
            int widthSize = box[1];

            int[] item = iterator.next();
            item_code = item[1];


            if (convertedX > tPoint.x - (widthSize / 2)
                    && convertedX < tPoint.x + (widthSize / 2)
                    && convertedY > tPoint.y - (100 / 2)
                    && convertedY < tPoint.y + (100 / 2)) {
                mTouched = true;
                mTouchedItem = item_code;


            }
        }
        return super.onTouchEvent(event);
    }

    private void drawPopup() {
        // TODO Auto-generated method stub
        int touch_board_Code = mTouchedItem;
        Log.d("이거 클릭", touch_board_Code + "");
        String[][] parsedata = new String[0][9];

        JSONArray json = null;
        try {
            json = new JSONArray(s);
            parsedata = new String[json.length()][11];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("board_code");
                parsedata[i][1] = jobject.getString("board_title");
                parsedata[i][2] = jobject.getString("board_content");
                parsedata[i][3] = jobject.getString("log_longtitude");
                parsedata[i][4] = jobject.getString("log_latitude");
                parsedata[i][5] = jobject.getString("randomViewY");
                parsedata[i][6] = jobject.getString("user_id");
                parsedata[i][7] = jobject.getString("board_date");
                parsedata[i][8] = jobject.getString("write_type");
                parsedata[i][9] = jobject.getString("user_profile");
                if (json.getJSONObject(i).isNull("step_log_code") == false) {
                    parsedata[i][10] = jobject.getString("step_log_code");
                } else {
                    parsedata[i][10] = "-1";
                }
                int sel_board_Code = Integer.parseInt(parsedata[i][0]);
                if (touch_board_Code == sel_board_Code) {

                    Intent intent = new Intent(mContext, LifeLogViewActivity.class);
                    intent.putExtra("board_Code", parsedata[i][0]);
                    intent.putExtra("board_Title", parsedata[i][1]);
                    intent.putExtra("board_Content", parsedata[i][2]);
                    intent.putExtra("log_longtitude", parsedata[i][3]);
                    intent.putExtra("log_latitude", parsedata[i][4]);
                    intent.putExtra("board_Date", parsedata[i][7]);
                    intent.putExtra("write_type", parsedata[i][8]);
                    intent.putExtra("profile_picture", parsedata[i][9]);
                    intent.putExtra("step_log_code", parsedata[i][10]);
                    Log.d("click", parsedata[i][9]);

                    mContext.startActivity(intent);
                    DBselect = false;
                    mTouched = false;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getJsonData() {
        return s;
    }

    public Double getLong() {
        return mlongitude;
    }

    public Double getLat() {
        return mlatitude;
    }

    private void drawGrid(double tAx, double tAy, double tBx, double tBy,
                          Canvas pCanvas, Paint pPaint, String title, String content, int placeY, int item_code, int i) {
        // TODO Auto-generated method stub

        // 현재 위치와 데이터의 위치를 계산하는 공식
        double mXDegree = (double) (Math.atan((double) (tBy - tAy) / (double) (tBx - tAx)) * 180.0 / Math.PI);
        float mYDegree = mYCompassDegree; // 기기의 기울임각도
        // 4/4분면을 고려하여 0~360도가 나오게 설정
        if (tBx > tAx && tBy > tAy) {
            ;
        } else if (tBx < tAx && tBy > tAy) {
            mXDegree += 180;
        } else if (tBx < tAx && tBy < tAy) {
            mXDegree += 180;
        } else if (tBx > tAx && tBy < tAy) {
            mXDegree += 360;
        }

        // 두 위치간의 각도에 현재 스마트폰이 동쪽기준 바라보고 있는 방향 만큼 더해줌
        // 360도(한바퀴)가 넘었으면 한바퀴 회전한것이기에 360를 빼줌
        if (mXDegree + mXCompassDegree < 360) {
            mXDegree += mXCompassDegree;
        } else if (mXDegree + mXCompassDegree >= 360) {
            mXDegree = mXDegree + mXCompassDegree - 360;
        }


        // 계산된 각도 만큼 기기 정중앙 화면 기준 어디에 나타날지 계산함
        // 정중앙은 90도, 시야각은 30도로 75 ~ 105 사이일때만 화면에 나타남
        float mX = 0;
        float mY = 0;

        if (mXDegree > 75 && mXDegree < 105) {
            if (mYDegree > -180 && mYDegree < 0) {

                mX = (float) mWidth
                        - (float) ((mXDegree - 75) * ((float) mWidth / 30));

                mYDegree = -(mYDegree);

                mY = (float) (mYDegree * ((float) mHeight / 180));

                // 두 위치간의 거리를 계산함
                Location locationA = new Location("Point A");
                Location locationB = new Location("Point B");

                locationA.setLongitude(tAx);
                locationA.setLatitude(tAy);

                locationB.setLongitude(tBx);
                locationB.setLatitude(tBy);

                int distance = (int) locationA.distanceTo(locationB);

                Paint tPaint = mbackgroundPaint;
                // 데이터에 거리에 들어오면 데이터 보임
                if (distance <= mVisibleDistance * 1000) {
                    if (distance < 1000) {
                        pPaint.setColor(Color.WHITE);
                        pPaint.setTextSize(50);
                        mShadowPaint.setTextSize(50);
                        tPaint.setColor(Color.BLACK);
                        tPaint.setAlpha(125);
                        RectF r = new RectF(mX - pPaint.measureText(title) / 2 - 60, mY + placeY - 70, mX + pPaint.measureText(title) / 2 + 60, mY + placeY + 30);

                        pCanvas.drawRoundRect(r, 20, 20, tPaint);
                        pCanvas.drawText(title, mX - pPaint.measureText(title) / 2
                                + mShadowXMargin, mY + placeY
                                + mShadowYMargin, mShadowPaint);
                        pCanvas.drawText(title, mX - pPaint.measureText(title) / 2, mY + placeY, pPaint);

                        int widthbox = (int) (r.right - r.left);
                        int heightbox = (int) (r.bottom - r.top);

                        boxsize.add(new int[]{item_code, widthbox, heightbox});

                        PointF tPoint = new PointF();
                        tPoint.set(mX - mWidth / 2, mY - mHeight / 2 + placeY);

                        mPointFList.add(tPoint);
                        PointHashMap.add(new int[]{i, item_code});
                    }
                }
            }
        }
        // 현재의 회전되기전의 좌표를 회전된 좌표로 변환한후 반환함
    }

    private void interpretDB(Canvas pCanvas) {

        // TODO Auto-generated method stub
        double tAx, tAy, tBx, tBy;


        PointF tPoint;

        mPointFList = new ArrayList<PointF>();
        PointHashMap = new ArrayList<int[]>();
        boxsize = new ArrayList<int[]>();

        tAx = mlongitude;
        tAy = mlatitude;


        String[][] parsedata = new String[0][9];

        String title;
        String content;
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][9];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("board_code");
                parsedata[i][1] = jobject.getString("board_title");
                parsedata[i][3] = jobject.getString("log_longtitude");
                parsedata[i][4] = jobject.getString("log_latitude");
                parsedata[i][5] = jobject.getString("randomViewY");

                int item_code = Integer.parseInt(parsedata[i][0]);
                title = parsedata[i][1];
                content = parsedata[i][2];
                tBx = Double.parseDouble(parsedata[i][3]);
                tBy = Double.parseDouble(parsedata[i][4]);

                int placeY = Integer.parseInt(parsedata[i][5]);

                if (drawtext == true) {
                    drawGrid(tAx, tAy, tBx, tBy, pCanvas, mPaint, title, content, placeY, item_code, i);
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // 카메라 액티비티에서 오버레이 화면 크기를 설정함
    public void setOverlaySize(int width, int height) {
        // TODO Auto-generated method stub
        mWidth = width;
        mHeight = height;

    }

    //핸재위치 갱신
    public void setCurrentPoint(double longitude, double latitude) {

        mlongitude = longitude;
        mlatitude = latitude;
    }

    public void viewDestory() {
        mSensorManager.unregisterListener(this);

    }

}