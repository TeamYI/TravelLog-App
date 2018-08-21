package turn.zio.zara.travel_log;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.nhn.android.data.g.i;

/**
 * Created by Hoonhoon94 on 2017-06-08.
 */

public class TravelMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    SharedPreferences smartCost;
    String sc_Division;
    MapFragment mMapFragment;
    LinearLayout MapContainer;
    LinearLayout place_info;
    private GoogleMap mMap;
    SharedPreferences travelStory;
    SharedPreferences.Editor editor;
    String group_code;
    private String[][] parsedata;
    LinearLayout Day;
    LinearLayout dateicon;
    LinearLayout addday;
    ImageView place_pic;
    TextView place_name;
    TextView address;
    TextView place_priority;

    int listCount = 0;

    DataBaseUrl dataurl = new DataBaseUrl();
    Bitmap[] images;
    int datemenu;
    int daylist;
    int daydiff;
    int onmenu;

    int[] daylistcount;
    private int startCount;
    static long diffDays;
    static String begin;
    static Date beginDate;
    int[] imagecount;
    SharedPreferences login;
    String user_id;

    @Override
    public void onCreate(Bundle saveInstanceState) {

        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_travel_map);

        ImageView storyDisplay = (ImageView) findViewById(R.id.storyDisplay);
        ImageView moneyDisplay = (ImageView) findViewById(R.id.moneyDisplay);
        ImageView mapDisplay = (ImageView) findViewById(R.id.mapDisplay);
        ImageView supplyDisplay = (ImageView) findViewById(R.id.supplyDisplay);
        ImageView groupDisplay = (ImageView) findViewById(R.id.groupDisplay);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        ImageView prev = (ImageView) findViewById(R.id.pre);
        ImageView next = (ImageView) findViewById(R.id.next);

        storyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.story));
        moneyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.money1));
        mapDisplay.setImageDrawable(getResources().getDrawable(R.drawable.map));
        supplyDisplay.setImageDrawable(getResources().getDrawable(R.drawable.supply1));
        groupDisplay.setImageDrawable(getResources().getDrawable(R.drawable.group1));
        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.date));
        prev.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        next.setImageDrawable(getResources().getDrawable(R.drawable.next));

        datemenu = 0;
        daylist = 0;
        onmenu = 1;
        Day = (LinearLayout) findViewById(R.id.Day);
        dateicon = (LinearLayout) findViewById(R.id.Dateicon);
        addday = (LinearLayout) findViewById(R.id.addday);
        place_info = (LinearLayout) findViewById(R.id.place_info);
        place_pic = (ImageView) findViewById(R.id.place_pic);
        place_name = (TextView) findViewById(R.id.place_name);
        address = (TextView) findViewById(R.id.address);
        place_priority = (TextView) findViewById(R.id.place_priority);
        TextView travelTitleText = (TextView) findViewById(R.id.travelTitleText);

        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        user_id = login.getString("user_id", "0");

        smartCost = getSharedPreferences("joinCode", MODE_PRIVATE);
        sc_Division = smartCost.getString("sc_Division", "0");

        MapContainer = (LinearLayout) findViewById(R.id.MapContainer);

        travelStory = getSharedPreferences("title", MODE_PRIVATE);
        group_code = travelStory.getString("selectgroupCode", "-1");
        String selectTitle = travelStory.getString("selectTitle", "0");

        travelTitleText.setText(selectTitle);
        Log.d("group_code", group_code);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        mMapFragment = MapFragment.newInstance();
        fragmentTransaction.add(R.id.MapContainer, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
        TravelList travel = new TravelList();
        String result = null;
        TravelDate date = new TravelDate();
        String resultDate = null;
        try {
            result = travel.execute().get();
            resultDate = date.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("result", result);
        Log.d("resultDate", resultDate);
        jsonParse(result);

        serpic sel = new serpic();
        sel.execute();
        place_name.setText(parsedata[0][1]);
        address.setText(parsedata[0][2]);

        final JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(resultDate);
        String startDate = element.getAsJsonObject().get("start_Date").getAsString();
        String endDate = element.getAsJsonObject().get("end_Date").getAsString();


        begin = (startDate.substring(startDate.indexOf(",") + 1).trim());
        if ((Integer.parseInt(startDate.substring(0, startDate.indexOf("월")))) <= 10) {
            begin += 0;
        }
        begin += startDate.substring(0, startDate.indexOf("월"));
        begin += (startDate.substring(startDate.indexOf("월") + 1, startDate.indexOf(",")).trim());

        String end = (endDate.substring(endDate.indexOf(",") + 1).trim());
        if ((Integer.parseInt(endDate.substring(0, endDate.indexOf("월")))) <= 10) {
            end += 0;
        }
        end += endDate.substring(0, endDate.indexOf("월"));
        end += (endDate.substring(endDate.indexOf("월") + 1, endDate.indexOf(",")).trim());

        SimpleDateFormat formmat = new SimpleDateFormat("yyyyMMdd");
        beginDate = null;
        Date endDates = null;
        try {
            beginDate = formmat.parse(begin);
            endDates = formmat.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diff = endDates.getTime() - beginDate.getTime();
        diffDays = diff / (24 * 60 * 60 * 1000);

        Log.d("startDate", begin);
        Log.d("endDate", end);
        Log.d("endDate", diffDays + "");
        daydiff = (int) diffDays + 1;
        daylistcount = new int[daydiff];

        for (int i = 0; i < diffDays + 1; i++) {
            TextView days = new TextView(TravelMapActivity.this);
            int hight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
            days.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, hight));
            days.setTextColor(Color.parseColor("#FFFFFF"));
            days.setBackgroundColor(Color.parseColor("#000000"));
            days.setTextSize(30);
            days.setGravity(Gravity.CENTER);
            days.setText((i + 1) + "일차");
            LinearLayout daysadd = new LinearLayout(this);
            daysadd.addView(days);
            addday.addView(daysadd);
            final int position = i;
            days.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("클릭한 일차", (position + 1) + "일차");
                    daylist = position;
                    Day.setVisibility(View.GONE);
                    dateicon.setVisibility(View.VISIBLE);
                    place_info.setVisibility(View.VISIBLE);
                    PolylineOptions option = new PolylineOptions();
                    option.width(13);
                    option.color(Color.BLACK);
                    MarkerOptions markerOption = new MarkerOptions();
                    int count = 0;
                    mMap.clear();
                    for (int i = 0; i < parsedata.length; i++) {
                        Log.d("count", i + "dd" + position + " / " + parsedata[i][9]);
                        if (parsedata[i][9].equals(position + "")) {
                            if (count == 0) {
                                startCount = i;
                                datemenu = startCount;
                                LatLng startPoint = new LatLng(Double.parseDouble(parsedata[i][3]), Double.parseDouble(parsedata[i][4]));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15));
                                place_name.setText(parsedata[i][1]);
                                address.setText(parsedata[i][2]);
                                serpic sel = new serpic();
                                sel.execute();
                                place_pic.setImageBitmap(images[i]);
                                place_priority.setText(parsedata[i][10] + " " + parsedata[i][6] + "번째");
                            }
                            count++;
                            LatLng point = new LatLng(Double.parseDouble(parsedata[i][3]), Double.parseDouble(parsedata[i][4]));
                            option.add(point);

                            Log.d("memo", parsedata[i][11]);
                            markerOption.position(new LatLng(Double.parseDouble(parsedata[i][3]), Double.parseDouble(parsedata[i][4])));
                            markerOption.title(parsedata[i][1]);
                            if(!parsedata[i][11].equals("없음")) {
                                markerOption.snippet(parsedata[i][11]);
                            }else{
                                markerOption.snippet("");
                            }
                            Log.d("marker", parsedata[i][1]);
                            mMap.addMarker(markerOption);
                        }
                        mMap.addPolyline(option);
                    }
                }
            });

        }


    }

    public void dateSelect(View v) {
        if (onmenu == 1) {
            onmenu = 0;
            Day.setVisibility(View.VISIBLE);
            dateicon.setVisibility(View.GONE);
            place_info.setVisibility(View.GONE);
        } else {
            onmenu = 1;
            Day.setVisibility(View.GONE);
            dateicon.setVisibility(View.VISIBLE);
            place_info.setVisibility(View.VISIBLE);
        }

    }

    class serpic extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog loading;

        ArrayList<String> location = new ArrayList<String>();
        private InputStream is = null;
        private String KMlurl;
        Bitmap resizedBitmap;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                String url = dataurl.getPlaceUrl() + parsedata[datemenu][8];
                Log.d("URLs", url + " / " + i);
                InputStream is = (InputStream) new URL(url).getContent();
                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inJustDecodeBounds = false;
                resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                imagecount[datemenu]++;


                return resizedBitmap;

                // Read Server Response

            } catch (Exception e) {
                return resizedBitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            this.cancel(true);
            place_pic.setImageBitmap(s);
            images[datemenu] = s;
        }
    }

    public void jsonParse(String s) {
        Log.d("json", s);
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][12];
            imagecount = new int[parsedata.length];
            images = new Bitmap[parsedata.length];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("place_code");
                parsedata[i][1] = jobject.getString("place_name");
                parsedata[i][2] = jobject.getString("place_address");
                parsedata[i][3] = jobject.getString("place_lat");
                parsedata[i][4] = jobject.getString("place_lng");
                parsedata[i][5] = jobject.getString("place_type");
                parsedata[i][6] = jobject.getString("travel_Priority");
                parsedata[i][7] = jobject.getString("travel_Date");
                if (json.getJSONObject(i).isNull("place_img") == false) {
                    parsedata[i][8] = jobject.getString("place_img");
                    parsedata[i][8] = parsedata[i][8].replace("/", "/s_");
                } else {
                    parsedata[i][8] = "0";
                }
                if (json.getJSONObject(i).isNull("travel_Memo") == false) {
                    parsedata[i][11] = jobject.getString("travel_Memo");
                } else {
                    parsedata[i][11] = "없음";
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    class TravelList extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("group_code", group_code);
                DBserver = "Travel_place";

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
        }
    }

    class TravelDate extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();

                seldata.put("group_code", group_code);
                DBserver = "Travel_Date";

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
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            LatLng startPoint = new LatLng(Double.parseDouble(parsedata[0][3]), Double.parseDouble(parsedata[0][4]));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15));

            PolylineOptions option = new PolylineOptions();
            option.width(13);
            option.color(Color.BLACK);
            MarkerOptions markerOption = new MarkerOptions();
            for (int i = 0; i < parsedata.length; i++) {
                SimpleDateFormat formmat = new SimpleDateFormat("yyyyMMdd");
                String travelDate = parsedata[i][7].replaceAll("-", "");
                Date TravelDay = null;
                try {
                    TravelDay = formmat.parse(travelDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long diff = TravelDay.getTime() - beginDate.getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000);
                parsedata[i][9] = diffDays + "";
                parsedata[i][10] = "Day" + (Integer.parseInt(parsedata[i][9]) + 1);

                if (diff == 0) {
                    LatLng point = new LatLng(Double.parseDouble(parsedata[i][3]), Double.parseDouble(parsedata[i][4]));
                    option.add(point);

                    Log.d("memo", parsedata[i][11]);
                    markerOption.position(new LatLng(Double.parseDouble(parsedata[i][3]), Double.parseDouble(parsedata[i][4])));
                    markerOption.title(parsedata[i][1]);
                    if(!parsedata[i][11].equals("없음")) {
                        markerOption.snippet(parsedata[i][11]);
                    }else{
                        markerOption.snippet("");
                    }
                    Log.d("marker", parsedata[i][1]);
                    mMap.addMarker(markerOption);
                }
                mMap.addPolyline(option);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        for (int i = 0; i < parsedata.length; i++) {

                            if (marker.getPosition().latitude == Double.parseDouble(parsedata[i][3])
                                    && marker.getPosition().longitude == Double.parseDouble(parsedata[i][4])) {
                                datemenu = i;
                                LatLng startPoint = new LatLng(Double.parseDouble(parsedata[i][3]), Double.parseDouble(parsedata[i][4]));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15));

                                place_name.setText(parsedata[i][1]);
                                address.setText(parsedata[i][2]);
                                place_priority.setText(parsedata[i][10] + " " + parsedata[i][6] + "번째");
                                place_pic.setImageBitmap(images[i]);
                            }
                        }


                        return false;
                    }
                });
            }
            int j = 0;
            for (int i = 0; i < daylistcount.length; i++) {
                for (; j < parsedata.length; j++) {
                    daylistcount[Integer.parseInt(parsedata[j][9])]++;
                }
                Log.d("daycount", daylistcount[i] + " /");
            }
        }
        place_priority.setText(parsedata[0][10] + " " + parsedata[0][6] + "번째");
    }

    // 액티비티 전환시 애니메이션 제거
    public void onResume() {
        this.overridePendingTransition(0, 0);
        super.onResume();
    }

    public void next(View v) {
        datemenu++;
        int num = 0;
        for (int i = 0; i <= daylist; i++) {
            num += daylistcount[i];
        }
        if (datemenu == num) {
            datemenu = datemenu - daylistcount[daylist];
        }
        Log.d("next", datemenu + "");
        Log.d("num", num + "");
        Log.d("text", parsedata[datemenu][1]);
        LatLng startPoint = new LatLng(Double.parseDouble(parsedata[datemenu][3]), Double.parseDouble(parsedata[datemenu][4]));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15));

        place_name.setText(parsedata[datemenu][1]);
        address.setText(parsedata[datemenu][2]);
        place_priority.setText(parsedata[datemenu][10] + " " + parsedata[datemenu][6] + "번째");

        if (imagecount[datemenu] == 0) {
            serpic sel = new serpic();
            sel.execute();
        } else {
            place_pic.setImageBitmap(images[datemenu]);
        }

    }

    public void prev(View v) {
        datemenu--;
        int num = 0;
        for (int i = 0; i <= daylist; i++) {
            num += daylistcount[i];
        }
        if (datemenu < startCount) {
            datemenu = num - 1;
        }
        Log.d("prev", datemenu + "");
        Log.d("num", num + "");
        LatLng startPoint = new LatLng(Double.parseDouble(parsedata[datemenu][3]), Double.parseDouble(parsedata[datemenu][4]));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15));
        place_name.setText(parsedata[datemenu][1]);
        address.setText(parsedata[datemenu][2]);
        place_priority.setText(parsedata[datemenu][10] + " " + parsedata[datemenu][6] + "번째");

        if (imagecount[datemenu] == 0) {
            serpic sel = new serpic();
            sel.execute();
        } else {
            place_pic.setImageBitmap(images[datemenu]);
        }
    }

    public void smart_Cost(View view) { // 여비관리 액티비티로 이동
        if (sc_Division.equals("차감")) {
            Intent intent = new Intent(this, SmartCostSubActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SmartCostAddActivity.class);
            startActivity(intent);
        }
    }

    public void travel_Story(View view) { // 스토리 액티비티로 이동
        Intent intent = new Intent(this, TravelStoryActivity.class);
        startActivity(intent);
    }

    public void travel_Supply(View view) { // 준비물 액티비티로 이동
        Intent intent = new Intent(this, TravelSupplyActivity.class);
        startActivity(intent);
    }

    public void travel_Group(View view) { // 그룹 액티비티로 이동
        Intent intent = new Intent(this, TravelGroupActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, mypageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }

    @Override
    public void onDestroy() {
        if (!(images.length+"").equals("null")) {
            for (int i = 0; i < images.length; i++) {
                if (images[i] != null) {
                    images[i].recycle();
                }
            }
        }
        images = null;

        super.onDestroy();
    }
}
