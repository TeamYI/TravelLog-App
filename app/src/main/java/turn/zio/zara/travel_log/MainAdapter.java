package turn.zio.zara.travel_log;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static turn.zio.zara.travel_log.R.id.profile_picture;

/**
 * Created by 하루마다 on 2017-06-14.
 */

class MainAdapter extends BaseAdapter {

    private final int layout;
    Context context;

    LayoutInflater mInflater;
    String[] title;
    LayoutInflater inf;
    String[] Content;
    String[] date;
    String[] writeuser_id;
    String[] file_type;
    String[] adress;
    String[] file_Content;
    String[] board_code;

    Drawable drawable;

    private Bitmap[] images;
    private GoogleMap mMap;
    private String kmlFile;
    private InputStream is;
    String[] step_log_code;

    String board_codetext;
    String titletext;
    String Contenttext;
    String datetext;
    String writeuser_idtext;
    String file_typetext;
    String adresstext;
    String step_log_codetext;
    String[] write_type;
    String[] coo;
    String[] user_profile;

    DataBaseUrl dataurl = new DataBaseUrl();
    ArrayList<String> location = new ArrayList<String>();
    private boolean flag2 = true;
    private FragmentTransaction fragmentTransaction;
    MapFragment mMapFragment;
    private int mode;
    private int like_ture = -1;
    String mainuser_id;
    private Bitmap[] pimages;

    private List<WeakReference<View>> mRecycleList = new ArrayList<WeakReference<View>>();

    //생성자
    public MainAdapter(Context context, int layout, String[] board_code, String[] title, String[] Content, String[] date,
                       String[] writeuser_id, String[] file_type, String[] adress, String[] file_Content, String[] step_log_code,
                       String[] write_type, String user_id, String[] user_profile) {
        //인플레이트 준비를 합니다.
        this.context = context;
        this.layout = layout;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.board_code = board_code;
        this.title = title;
        this.Content = Content;
        this.date = date;
        this.writeuser_id = writeuser_id;
        this.file_type = file_type;
        this.adress = adress;
        this.file_Content = file_Content;
        this.step_log_code = step_log_code;
        this.write_type = write_type;
        this.mainuser_id = user_id;
        this.user_profile = user_profile;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return title[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void image(Bitmap[] images, int i) {
        this.images = images;
        this.mode = i;
    }

    public void pimage(Bitmap[] pimages, int i) {
        this.pimages = pimages;
        this.mode = i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //최초 호출이면 항목 뷰를 생성한다.
        //타입별로 뷰를 다르게 디자인 할 수 있으며 높이가 달라도 상관없다.\
        if (convertView == null)
            //인플레이트합니다. 즉 화면에 뿌립니다.
            convertView = inf.inflate(layout, null);

            TextView titles = (TextView) convertView.findViewById(R.id.log_title);
            titles.setText(title[position]);
            TextView Contents = (TextView) convertView.findViewById(R.id.log_cotennt);
            TextView log_place = (TextView) convertView.findViewById(R.id.log_place);
            log_place.setText(adress[position]);
            TextView log_date = (TextView) convertView.findViewById(R.id.log_date);
            log_date.setText(date[position]);
            final TextView user_id = (TextView) convertView.findViewById(R.id.user_id);
            user_id.setText(writeuser_id[position]);

            final ImageView like = (ImageView) convertView.findViewById(R.id.log_Likes);
            ImageView Comment = (ImageView) convertView.findViewById(R.id.log_Comments);
            ImageView iv = (ImageView) convertView.findViewById(R.id.log_picture);
            ImageView map_picture = (ImageView) convertView.findViewById(R.id.map_picture);
            ImageView profile_pic = (ImageView) convertView.findViewById(profile_picture);
            ImageView option = (ImageView) convertView.findViewById(R.id.option);
            LinearLayout picView = (LinearLayout) convertView.findViewById(R.id.log_picture_Linear);
            LinearLayout map = (LinearLayout) convertView.findViewById(R.id.MapContainer);
            LinearLayout text = (LinearLayout) convertView.findViewById(R.id.text);
            //화면에 뿌린뒤 여기서 각항목에 해당하는 값을 바꿔주는 부분입니다.

            option.setImageDrawable(convertView.getResources().getDrawable(R.drawable.homepage_option));
            Comment.setImageDrawable(convertView.getResources().getDrawable(R.drawable.comment));

            profile_pic.setBackground(new ShapeDrawable(new OvalShape()));
            profile_pic.setClipToOutline(true);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("click", "라이크 클릭");
                    if (like_ture == 1) {
                        like.setImageDrawable(context.getResources().getDrawable(R.drawable.like_off));
                        like_ture = -1;
                    } else {
                        like.setImageDrawable(context.getResources().getDrawable(R.drawable.like_on));
                        like_ture = 1;
                    }
                    LikeonOff(mainuser_id, board_code[position] + "");
                }
            });

            Comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Comment.class);
                    intent.putExtra("board_Code", board_code[position] + "");
                    intent.putExtra("user_id", mainuser_id);
                    context.startActivity(intent);
                }
            });

            profile_pic.setImageBitmap(pimages[position]);
            profile_pic.setScaleType(ImageView.ScaleType.FIT_XY);
            if (file_type[position].equals("0")) {
                picView.setVisibility(View.GONE);
                text.setVisibility(View.VISIBLE);
                map.setVisibility(View.GONE);
                flag2 = true;
            } else if (file_type[position].equals("1")) {
                picView.setVisibility(View.VISIBLE);
                map.setVisibility(View.GONE);
                iv.setImageBitmap(images[position]);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                flag2 = true;
            } else if (file_type[position].equals("2")) {
                picView.setVisibility(View.VISIBLE);
                map.setVisibility(View.GONE);
                drawable = context.getResources().getDrawable(R.drawable.voice);
                iv.setImageDrawable(drawable);
                flag2 = true;
            } else if (file_type[position].equals("3")) {
                map.setVisibility(View.VISIBLE);
                picView.setVisibility(View.GONE);
                text.setVisibility(View.GONE);


                map_picture.setImageBitmap(images[position]);
                map_picture.setScaleType(ImageView.ScaleType.FIT_XY);
            /*i
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Log.d("dd", Double.parseDouble(coo[0]) + "/" + Double.parseDouble(coo[1]));


            }*/
            }
            if (like_ture == -1) {
                LikeTure(mainuser_id, board_code[position], like);
            }
            Log.d("like", like_ture + "ddd" + board_code[position] + "/" + Content[position]);
        /*웹으로쓴 글일때*/
            if (write_type[position].equals("0")) {


                String regex = "<img [^<>]*>";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(Content[position]);
                while (matcher.find()) {
                    String temp = matcher.replaceAll("");
                    Contents.setText(Html.fromHtml(temp));
                }
            }/*앱이면*/ else {
                Contents.setText(Content[position]);
            }
            map_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, LifeLogViewActivity2.class);
                    intent.putExtra("board_Code", board_code[position]);
                    intent.putExtra("board_Title", title[position]);
                    intent.putExtra("board_Content", Content[position]);
                    intent.putExtra("board_Date", date[position]);
                    intent.putExtra("user_id", writeuser_id[position]);
                    intent.putExtra("file_Type", file_type[position]);
                    intent.putExtra("file_Content", file_Content[position]);
                    intent.putExtra("step_log_code", step_log_code[position]);
                    intent.putExtra("profile_picture", user_profile[position]);

                    context.startActivity(intent);
                }
            });


        return convertView;//getCount만큼 반복한다고 했죠?
        //리스트의 갯수만큼 반복하게 됩니다.
    }


    public static String replaceLast(String str, String regex, String replacement) {
        int regexIndexOf = str.lastIndexOf(regex);
        if (regexIndexOf == -1) {
            return str;
        } else {
            return str.substring(0, regexIndexOf) + replacement + str.substring(regexIndexOf + regex.length());
        }
    }


    /*좋아요 여부*/
    private void LikeTure(final String user_id, final String board_code, final ImageView like) {

        class tureData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("like 결과", s);
                Log.d("like 결과", board_code + "");
                like_ture = Integer.parseInt(s);
                if (like_ture == 1) {
                    like.setImageDrawable(context.getResources().getDrawable(R.drawable.like_on));
                } else {
                    like.setImageDrawable(context.getResources().getDrawable(R.drawable.like_off));
                }
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];
                    String board_code = (String) params[1];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);
                    loginParam.put("board_code", board_code);


                    String link = dataurl.getServerUrl() + "liketure"; //92.168.25.25
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

        tureData task = new tureData();
        task.execute(user_id, board_code);
    }

    private void LikeonOff(final String user_id, final String board_code) {

        class tureData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("like 결과", s);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String user_id = (String) params[0];
                    String board_code = (String) params[1];

                    Map<String, String> loginParam = new HashMap<String, String>();

                    loginParam.put("user_id", user_id);
                    loginParam.put("board_code", board_code);

                    String dbselect = null;

                    if (like_ture == 1) {
                        dbselect = "like";
                    } else {
                        dbselect = "likeDelete";
                    }

                    Log.d("db", dbselect);
                    String link = dataurl.getServerUrl() + dbselect; //92.168.25.25
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

        tureData task = new tureData();
        task.execute(user_id, board_code);
    }


}
