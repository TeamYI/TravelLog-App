package turn.zio.zara.travel_log;

/**
 * Created by 하루마다 on 2017-06-15.
 */

class LocationInfo {
    private double longitude;
    private double latitude;

    public LocationInfo(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        latitude = latitude;
    }
}
