package turn.zio.zara.travel_log;

/**
 * Created by 하루마다 on 2017-07-12.
 */

public class DataBaseUrl {
    /*
    * 서버 http://211.202.32.52:8084 114.201.41.248
    * 노트북 http://127.19.1.81:8088 192.168.43.109:8087
    *
    */
    private String url = "http://114.201.41.248:8084";
    private String tumnailUrl = url + "/turn/resources/upload/logs";
    private String serverUrl = url + "/android/";
    private String dataUrl = url + "/turn/resources/upload/logs/";
    private String profile = url + "/turn/resources/upload/profile";
    private String stepUrl = url + "/turn/resources/upload/step_Log/";
    private String placeUrl = url + "/turn/resources/upload";

    public String getTumnailUrl() {
        return tumnailUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public String getStepUrl() {
        return stepUrl;
    }

    public String getProfile() {
        return profile;
    }

    public String getPlaceUrl() {
        return placeUrl;
    }
}
