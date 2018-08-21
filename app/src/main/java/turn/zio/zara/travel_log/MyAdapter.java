package turn.zio.zara.travel_log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Created by 하루마다 on 2017-06-13.
 */
class MyAdapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inf;
    String[] text;
    String[] file_type;

    Drawable drawable;

    private Bitmap[] images;

    public MyAdapter(Context context, int layout, String[] text, String[] file_type) {
        this.context = context;
        this.layout = layout;
        this.text = text;
        this.file_type = file_type;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    public void image(Bitmap[] images) {
        this.images = images;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inf.inflate(layout, null);
        ImageView iv = (ImageView) convertView.findViewById(R.id.log_image);
        TextView tv = (TextView) convertView.findViewById(R.id.log_text);
        tv.setBackgroundColor(Color.GRAY);
        if (file_type[position].equals("0")) {
            tv.setVisibility(View.VISIBLE);
            iv.setVisibility(View.GONE);
            /*텍스트 일경우*/
            tv.setText(text[position]);
        } else {
            tv.setVisibility(View.GONE);
            iv.setVisibility(View.VISIBLE);
            if (file_type[position].equals("1")) {
                Log.d("position", position+"position");
                iv.setImageBitmap(images[position]);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
            } else if (file_type[position].equals("2")) {
                drawable = context.getResources().getDrawable(R.drawable.voice);
                iv.setImageDrawable(drawable);
            } else if (file_type[position].equals("3")) {
                iv.setImageBitmap(images[position]);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
        return convertView;
    }
}
