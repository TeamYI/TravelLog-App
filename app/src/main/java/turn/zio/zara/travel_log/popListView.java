package turn.zio.zara.travel_log;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static turn.zio.zara.travel_log.CameraOverlayView.mVisibleDistance;


public class popListView extends Activity {

    private ArrayList<ListItem> arItem;
    private String jsondata;
    private popListView mContext;
    private String[][] parsedata;


    public static boolean touch = true;
    JSONArray json = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_log_listview);
        getWindow().setLayout(android.view.WindowManager.LayoutParams.MATCH_PARENT, android.view.WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        touch = true;

        Intent intent = getIntent();
        jsondata = intent.getExtras().getString("jsonData");
        double mlongitude = intent.getExtras().getDouble("mlongitude");
        double mlatitude = intent.getExtras().getDouble("mlatitude");
        Log.d("Dd", jsondata);

        Location locationA = new Location("Point A");
        Location locationB = new Location("Point B");

        locationA.setLongitude(mlongitude);
        locationA.setLatitude(mlatitude);


        parsedata = new String[0][11];

        arItem = new ArrayList<ListItem>();

        try {
            json = new JSONArray(jsondata);
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

                Double tBx = Double.parseDouble(parsedata[i][3]);
                Double tBy = Double.parseDouble(parsedata[i][4]);

                locationB.setLongitude(tBx);
                locationB.setLatitude(tBy);

                int distance = (int) locationA.distanceTo(locationB);

                if (distance <= mVisibleDistance * 1000) {
                    if (distance < 1000) {
                        String board_Code = parsedata[i][0];
                        String title = parsedata[i][1];
                        String user_id = parsedata[i][6];
                        arItem.add(new ListItem(board_Code, title, user_id));
                        Log.d("Dd", arItem.size() + "");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MultiAdapter MyAdapter = new MultiAdapter(this, arItem);

        //리스트뷰를 만들고
        GridView MyList;
        MyList = (GridView) findViewById(R.id.list);
        MyList.setAdapter(MyAdapter);//이어줍니다.

        MyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sel_boardCode = arItem.get(position).board_Code();
                try {
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
                        if (sel_boardCode.equals(parsedata[i][0]) && touch == true) {
                            Intent intent = new Intent(getApplicationContext(), LifeLogViewActivity.class);
                            touch = false;
                            intent.putExtra("board_Code", parsedata[i][0]);
                            intent.putExtra("board_Title", parsedata[i][1]);
                            intent.putExtra("board_Content", parsedata[i][2]);
                            intent.putExtra("log_longtitude", parsedata[i][3]);
                            intent.putExtra("log_latitude", parsedata[i][4]);
                            intent.putExtra("user_id", parsedata[i][6]);
                            intent.putExtra("board_Date", parsedata[i][7]);
                            intent.putExtra("write_type", parsedata[i][8]);
                            intent.putExtra("profile_picture", parsedata[i][9]);
                            intent.putExtra("step_log_code",parsedata[i][10]);

                            startActivity(intent);
                            CameraOverlayView.DBselect = false;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void backAR(View view) {
        CameraOverlayView.drawtext = true;
        finish();
    }

    @Override
    public void onBackPressed() {
        CameraOverlayView.drawtext = true;
        finish();
    }

}

