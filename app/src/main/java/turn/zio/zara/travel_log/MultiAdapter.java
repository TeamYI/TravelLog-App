package turn.zio.zara.travel_log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 하루마다 on 2017-06-14.
 */

class MultiAdapter extends BaseAdapter {


    LayoutInflater mInflater;
    ArrayList<ListItem> arSrc;

    //생성자
    public MultiAdapter(Context context, ArrayList<ListItem> arItem) {
        //인플레이트 준비를 합니다.
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arSrc = arItem;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arSrc.size();
    }

    @Override
    public ListItem getItem(int position) {
        // TODO Auto-generated method stub
        return arSrc.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //최초 호출이면 항목 뷰를 생성한다.
        //타입별로 뷰를 다르게 디자인 할 수 있으며 높이가 달라도 상관없다.
        if (convertView == null)
            //인플레이트합니다. 즉 화면에 뿌립니다.
            convertView = mInflater.inflate(R.layout.pop_log_list, parent, false);

        //화면에 뿌린뒤 여기서 각항목에 해당하는 값을 바꿔주는 부분입니다.
        TextView title = (TextView) convertView.findViewById(R.id.log_title);
        title.setText(arSrc.get(position).title);
        TextView user_id = (TextView) convertView.findViewById(R.id.user_id);
        user_id.setText(arSrc.get(position).user_id);


        return convertView;//getCount만큼 반복한다고 했죠?
        //리스트의 갯수만큼 반복하게 됩니다.
    }


}
