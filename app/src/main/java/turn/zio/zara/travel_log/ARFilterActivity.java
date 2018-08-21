package turn.zio.zara.travel_log;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

public class ARFilterActivity extends AppCompatActivity {

    private RadioButton latest_order_check;
    private RadioButton like_order_check;
    private Spinner AR_view_filter;

    private EditText hash_Text;

    private LinearLayout view_moode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arfilter);

        latest_order_check = (RadioButton) findViewById(R.id.latest_order_check);
        like_order_check = (RadioButton) findViewById(R.id.like_order_check);
        hash_Text = (EditText) findViewById(R.id.hash_Text);

        view_moode = (LinearLayout) findViewById(R.id.view_moode);

        latest_order_check.setChecked(true);

        AR_view_filter = (Spinner) findViewById(R.id.place_view_mode);
        ArrayAdapter filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.view_mode, android.R.layout.simple_spinner_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AR_view_filter.setAdapter(filterAdapter);

    }

    public void like_order_check(View v) {
        like_order_check.setChecked(true);
        latest_order_check.setChecked(false);
    }

    public void latest_order_check(View v) {
        like_order_check.setChecked(false);
        latest_order_check.setChecked(true);
    }

    public void bakcMain(View v) {
        CameraOverlayView.DBselect = true;
        finish();
    }


    public void filter_submit(View v) {
        String visible = AR_view_filter.getSelectedItem().toString();
        String hashtext = hash_Text.getText().toString();

        if (hashtext.equals("")) {
            CameraOverlayView.hashTag = "없음";
        } else {
            CameraOverlayView.hashTag = hashtext;
        }

        if (visible.equals("250m")) {
            CameraOverlayView.mVisibleDistance = 0.25;
        } else if (visible.equals("500m")) {
            CameraOverlayView.mVisibleDistance = 0.5;
        } else if (visible.equals("1km")) {
            CameraOverlayView.mVisibleDistance = 1;
        } else if (visible.equals("3km")) {
            CameraOverlayView.mVisibleDistance = 3;
        } else if (visible.equals("5km")) {
            CameraOverlayView.mVisibleDistance = 5;
        }

        if (like_order_check.isChecked()) {
            CameraOverlayView.order_DB = "2";
        } else if (latest_order_check.isChecked()) {
            CameraOverlayView.order_DB = "1";
        }
        CameraOverlayView.DBselect = true;
        finish();
    }

    @Override
    public void onBackPressed() {
        CameraOverlayView.DBselect = true;
        finish();
    }
}
