package turn.zio.zara.travel_log;

/**
 * Created by 하루마다 on 2017-06-14.
 */

class ListItem {
    String title;
    String user_id;
    String board_Code;

    ListItem(String board_Code, String title, String user_id) {
        this.board_Code = board_Code;
        this.title = title;
        this.user_id = user_id;
    }


    public String board_Code() {
        return this.board_Code;
    }
}
