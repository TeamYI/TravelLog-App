package turn.zio.zara.travel_log;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by YunSungHyun on 2017-05-26.
 */

public class ImageModel implements Parcelable {

    String name, url;

    public ImageModel() {

    }

    protected ImageModel(Parcel in) {
        name = in.readString();
        url = in.readString();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel source) {
            return new ImageModel(source);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }


    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }
}

