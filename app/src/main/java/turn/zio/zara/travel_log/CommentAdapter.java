package turn.zio.zara.travel_log;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Created by 하루마다 on 2017-06-13.
 */
class CommentAdapter extends BaseAdapter {
    Context context;
    int layout;
    LayoutInflater inf;
    Drawable drawable;
    int[] board_code;
    String[] board_content;
    String[] date;
    String[] user_id;

    private Bitmap[] images;

    public CommentAdapter(Context context, int layout, int[] board_code, String[] board_content, String[] date, String[] user_id) {
        this.context = context;
        this.layout = layout;
        this.board_code = board_code;
        this.board_content = board_content;
        this.date = date;
        this.user_id = user_id;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }


    public void pimage(Bitmap[] images) {
        this.images = images;
    }

    @Override
    public int getCount() {
        return board_code.length;
    }

    @Override
    public Object getItem(int position) {
        return board_code[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inf.inflate(layout, null);

        ImageView iv = (ImageView) convertView.findViewById(R.id.profile_picture);
        TextView user_idView = (TextView) convertView.findViewById(R.id.user_id);
        TextView contentView = (TextView) convertView.findViewById(R.id.contenttext);
        TextView dateView = (TextView) convertView.findViewById(R.id.datetext);

        iv.setBackground(new ShapeDrawable(new OvalShape()));
        iv.setClipToOutline(true);
        user_idView.setText(user_id[position]);
        contentView.setText(board_content[position]);
        dateView.setText(date[position]);
        iv.setImageBitmap(images[position]);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        return convertView;
    }
}
