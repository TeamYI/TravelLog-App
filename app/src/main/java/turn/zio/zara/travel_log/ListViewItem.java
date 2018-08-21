package turn.zio.zara.travel_log;

import android.graphics.drawable.Drawable;

/**
 * Created by Hoonhoon94 on 2017-06-16.
 */

public class ListViewItem {
    private Drawable iconDrawable;
    private String titleStr;
    private String StartStr;
    private String EndStr;
    private String GcodeStr;

    public void setIcon(Drawable icon) {
        iconDrawable = icon;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setStart(String start) {
        StartStr = start;
    }

    public void setEnd(String end) {
        EndStr = end;
    }

    public void setGcode(String Gcode) {
        GcodeStr = Gcode;
    }

    public Drawable getIcon() {
        return this.iconDrawable;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getStart() {
        return this.StartStr;
    }

    public String getEnd() {
        return this.EndStr;
    }

    public String getGcode() {
        return this.GcodeStr;
    }
}