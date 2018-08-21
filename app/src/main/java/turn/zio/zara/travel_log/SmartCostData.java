package turn.zio.zara.travel_log;

/**
 * Created by 하루마다 on 2017-08-30.
 */

public class SmartCostData {
    static String user_id;
    static String group_code;

    public static String getUser_id() {
        return user_id;
    }

    public static void setUser_id(String user_id) {
        SmartCostData.user_id = user_id;
    }

    public static String getGroup_code() {
        return group_code;
    }

    public static void setGroup_code(String group_code) {
        SmartCostData.group_code = group_code;
    }
}
