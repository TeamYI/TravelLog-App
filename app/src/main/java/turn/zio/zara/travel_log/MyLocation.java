package turn.zio.zara.travel_log;

/**
 * Created by 하루마다 on 2017-08-23.
 */

public class MyLocation {
    public static double  longitude; //경도
    public static double latitude;
    MyLocation(){

    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        MyLocation.longitude = longitude;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        MyLocation.latitude = latitude;
    }
}
