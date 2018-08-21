package turn.zio.zara.travel_log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Hoonhoon94 on 2017-06-26.
 */

public class ExpenseListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ExpenseListViewItem> listViewItemList = new ArrayList<ExpenseListViewItem>();

    // ListViewAdapter의 생성자
    public ExpenseListViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expenselistview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView idstrTextView = (TextView) convertView.findViewById(R.id.textView1); // 아이디
        TextView expensecontentstrTextView = (TextView) convertView.findViewById(R.id.textView2); // 지출내역
        TextView expensecoststrTextView = (TextView) convertView.findViewById(R.id.textView3); /// 지출금액

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ExpenseListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        idstrTextView.setText(listViewItem.getIdStr());
        expensecontentstrTextView.setText(listViewItem.getExpense_ContentStr());
        expensecoststrTextView.setText(listViewItem.getExpense_CostStr());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String id, String expense_Content, String expense_Cost) {
        ExpenseListViewItem item = new ExpenseListViewItem();

        item.setIdStr(id);
        item.setExpense_ContentStr(expense_Content);
        item.setExpense_CostStr(expense_Cost);

        listViewItemList.add(item);
    }
}