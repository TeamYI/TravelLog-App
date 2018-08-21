package turn.zio.zara.travel_log;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/*
 * Created by 하루마다 on 2017-06-13.
 */
class MaterialApter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inf;
    String[] text;
    int[] metrial_code;
    String[] check;
    int[] metrial_check;
    String group_code;

    DataBaseUrl dataurl = new DataBaseUrl();
    public MaterialApter(Context context, int layout, String[] text, int[] metrial_check, int[] metrial_code, String group_code) {
        this.context = context;
        this.layout = layout;
        this.text = text;
        this.metrial_check = metrial_check;
        check = new String[text.length];
        this.metrial_code = metrial_code;
        this.group_code = group_code;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int position) {
        return text[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inf.inflate(layout, null);

        TextView tv = (TextView) convertView.findViewById(R.id.material_name);
        tv.setText(text[position]);
        final CheckBox ck = (CheckBox) convertView.findViewById(R.id.material_check);
        if(metrial_check[position] == 1){
            ck.setChecked(true);
            check[position] = "true";
        }else{
            ck.setChecked(false);
            check[position] = "false";
        }

        ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(check[position].equals("true")){
                    ck.setChecked(false);
                    check[position] = "false";
                }else{
                    ck.setChecked(true);
                    check[position] = "true";
                }
                Materialcheck(position+"");
                Log.d("dasd",check[position]);
            }
        });
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check[position].equals("true")){
                    ck.setChecked(false);
                    check[position] = "false";
                }else{
                    ck.setChecked(true);
                    check[position] = "true";
                }
                Materialcheck(position+"");
                Log.d("dasd",check[position]);

            }
        });

        return convertView;
    }

    private void Materialcheck(final String position) {
        class Material extends AsyncTask<String, Void, String> {
            ProgressDialog loading;


            @Override
            protected String doInBackground(String... params) {
                try {
                    String DBserver = null;
                    Map<String, String> seldata = new HashMap<String, String>();
                    int position = Integer.parseInt((String) params[0]);

                    seldata.put("group_code", group_code);
                    seldata.put("material_code", metrial_code[position]+"");

                    if (check[position].equals("true")) {
                        DBserver = "Material_on";
                    } else {
                        DBserver = "Material_off";
                    }

                    String link = dataurl.getServerUrl() + DBserver; //92.168.25.25
                    Log.d("dasd", link);
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

        Material task = new Material();
        task.execute(position);
    }
}
