package turn.zio.zara.travel_log;

/**
 * Created by 하루마다 on 2017-05-24.
 */

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ListViewDialog extends Dialog {

    // using debug
    private static final String TAG = "ListViewDialog";
    private ListViewDialogSelectListener mListener;
    private Context mContext;
    private ArrayList<String> mStrListItem;
    private ListView mListView;

    public ListViewDialog(Context context, String title, ArrayList<String> item) {
        super(context);
// TODO Auto-generated constructor stub
        setContentView(R.layout.list_view_dialog);
        mContext = context;
        findViews();
        setTitle(title);
//	mStrListItem = new ArrayList<String>(Arrays.asList(item));
        mStrListItem = item;
        createListViewDialog();
    }

    private void findViews() {
        mListView = (ListView) findViewById(R.id.list_view_dialog);
    }

    private void createListViewDialog() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, mStrListItem);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
// TODO Auto-generated method stub
                Log.d(TAG, "list index = [ " + arg2 + " ] ");
                mListener.onSetOnItemClickListener(arg2);
            }
        });
    }

    /**
     * listener 함수..
     */
    public void onOnSetItemClickListener(ListViewDialogSelectListener listener) {
        mListener = listener;
    }

    /**
     * interface...
     */
    public interface ListViewDialogSelectListener {
        public void onSetOnItemClickListener(int position);
    }
}
