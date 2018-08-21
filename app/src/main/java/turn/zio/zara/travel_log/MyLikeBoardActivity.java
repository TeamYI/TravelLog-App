package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MyLikeBoardActivity extends AppCompatActivity {

    private String[][] parsedata;
    DataBaseUrl dataurl = new DataBaseUrl();
    SharedPreferences login;
    SharedPreferences.Editor editor;
    private String user_id;
    private MyAdapter adapter;

    ArrayList<String> location = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_like_board);

        ImageView bakcMain = (ImageView)findViewById(R.id.bakcMain);
        bakcMain.setImageDrawable(getResources().getDrawable(R.drawable.backbutton2));
        login = getSharedPreferences("LoginKeep", MODE_PRIVATE);
        editor = login.edit();
        user_id = login.getString("user_id", "0");
        DBinput();
        GridView gv = (GridView) findViewById(R.id.list);
        /*list에 뿌려진 로그 클릭시*/
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Intent intent = new Intent(getApplicationContext(), LifeLogViewActivity2.class);
                intent.putExtra("board_Code", parsedata[position][0]);
                intent.putExtra("board_Title", parsedata[position][1]);
                intent.putExtra("board_Content", parsedata[position][2]);
                intent.putExtra("log_longtitude", parsedata[position][3]);
                intent.putExtra("log_latitude", parsedata[position][4]);
                intent.putExtra("board_Date", parsedata[position][5]);
                intent.putExtra("write_user_id", parsedata[position][6]);
                intent.putExtra("user_id", user_id);
                intent.putExtra("file_Type", parsedata[position][7]);
                intent.putExtra("file_Content", parsedata[position][8]);
                if (parsedata[position][7].equals("3")) {
                    intent.putExtra("step_log_code", parsedata[position][9]);
                }
                intent.putExtra("write_type", parsedata[position][10]);
                startActivity(intent);
            }
        });
    }

    /*메인 db 연결시도*/
    public void DBinput() {
        mainlistAll task = new mainlistAll();
        String result = null;
        try {
            result = task.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        jsonParse(result);
        serpic setimage = new serpic();
        setimage.execute();
    }
    class mainlistAll extends AsyncTask<String, Void, String> {
        ProgressDialog loading;

        @Override
        protected String doInBackground(String... params) {
            try {
                String DBserver = null;
                Map<String, String> seldata = new HashMap<String, String>();
                seldata.put("user_id", user_id);
                DBserver = "myLikeBoard";

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

    /*result JSon Parese*/
    public void jsonParse(String s) {
        Log.d("json", s);
        try {
            JSONArray json = new JSONArray(s);
            parsedata = new String[json.length()][12];
            for (int i = 0; i < json.length(); i++) {
                JSONObject jobject = json.getJSONObject(i);

                parsedata[i][0] = jobject.getString("board_code");
                parsedata[i][1] = jobject.getString("board_title");
                parsedata[i][2] = jobject.getString("board_content");
                parsedata[i][3] = jobject.getString("log_longtitude");
                parsedata[i][4] = jobject.getString("log_latitude");
                parsedata[i][5] = jobject.getString("board_date");
                parsedata[i][6] = jobject.getString("user_id");
                if (json.getJSONObject(i).isNull("file_content") == false) {
                    parsedata[i][7] = jobject.getString("file_type");
                    parsedata[i][8] = jobject.getString("file_content");
                } else {
                    parsedata[i][7] = "0";
                    parsedata[i][8] = "1";
                }
                if (parsedata[i][7].equals("3")) {
                    parsedata[i][9] = jobject.getString("step_log_code");
                }
                parsedata[i][10] = jobject.getString("write_type");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /*gridView 웹서버 이미지 뿌리기*/
    class serpic extends AsyncTask<String, Void, Bitmap[]> {
        ProgressDialog loading;
        private String KMlurl;
        private InputStream is = null;

        @Override
        protected Bitmap[] doInBackground(String... params) {
            Bitmap[] images = new Bitmap[parsedata.length];
            try {
                for (int i = 0; i < parsedata.length; i++) {

                    if (parsedata[i][7].equals("3")) {
                        String urltext = dataurl.getStepUrl() + parsedata[i][8];
                        Log.d("KMLurl", urltext);
                        URL url = new URL(urltext);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.connect();
                        File file = new File(urltext);
                        //FileInputStream is =new FileInputStream(file);
                        is = urlConnection.getInputStream();

                        InputStreamReader inputReader = new InputStreamReader(is);

                        String column = null;
                        BufferedReader br = new BufferedReader(inputReader);
                        boolean flag = false;
                        while ((column = br.readLine()) != null) {
                            int coordin = column.indexOf("<coordinates>");

                            if (coordin != -1 || flag) {
                                Log.d("폴리라인 그림", "걸러내는중");
                                int j = 0;
                                flag = true;
                                String tmpCoordin = column;
                                tmpCoordin = tmpCoordin.replaceAll("<coordinates>", "");
                                tmpCoordin = tmpCoordin.replaceAll("</coordinates>", "");
                                if (tmpCoordin.trim().equals("</LineString>")) {
                                    break;
                                }
                                location.add(tmpCoordin.trim());
                            }


                        }

                        Log.d("size", location.size()+"");
                        KMlurl = "";
                        KMlurl = "http://maps.googleapis.com/maps/api/staticmap?size=400x400&path=";
                        for (int k = 0; k < location.size(); k++) {
                            String[] coos = location.get(k).toString().split(",");
                            KMlurl += coos[1] + "," + coos[0];
                            if (k != location.size() - 1) {
                                KMlurl += "|";
                            }else {
                                KMlurl += "&sensor=false";
                            }
                            Log.d("dd", coos[1] + "," + coos[0]);
                            Log.d("result", parsedata[i][8]);
                        }

                    }
                    if (parsedata[i][7].equals("1")) {
                        String url = dataurl.getTumnailUrl() + parsedata[i][8];
                        Log.d("URL", url);
                        InputStream is = (InputStream) new URL(url).getContent();
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inJustDecodeBounds = false;
                        Bitmap resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                        images[i] = resizedBitmap;
                    }else if(parsedata[i][7].equals("3")){
                        Log.d("url", parsedata[i][8]);
                        InputStream is = (InputStream) new URL(KMlurl).getContent();
                        BitmapFactory.Options options = new BitmapFactory.Options();

                        options.inJustDecodeBounds = false;
                        Bitmap resizedBitmap = BitmapFactory.decodeStream(is, null, options);
                        images[i] = resizedBitmap;
                    }
                }
                return images;

                // Read Server Response

            } catch (Exception e) {
                images = null;
                return images;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(MyLikeBoardActivity.this, "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(Bitmap[] s) {
            super.onPostExecute(s);

            Apeter(s);

            this.cancel(true);
            loading.dismiss();
        }
    }
    public void Apeter(Bitmap[] images) {
        String[] text = new String[parsedata.length];
        String[] file_type = new String[parsedata.length];
        for (int i = 0; i < parsedata.length; i++) {
            if (parsedata[i][7].equals("0")) {
                text[i] = parsedata[i][1];
            } else {
                text[i] = parsedata[i][8];
            }
            file_type[i] = parsedata[i][7];
        }
        adapter = new MyAdapter(
                MyLikeBoardActivity.this,
                R.layout.pop_view_list,       // GridView 항목의 레이아웃 row.xml
                text, file_type);
        adapter.image(images);
        GridView gv = null;
            gv = (GridView) findViewById(R.id.list);
        
        gv.setAdapter(adapter);

    }

    public void bakcMain(View view) {
        finish();
    }
}
