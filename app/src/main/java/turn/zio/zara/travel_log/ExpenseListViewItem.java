package turn.zio.zara.travel_log;

/**
 * Created by Hoonhoon94 on 2017-06-26.
 */

public class ExpenseListViewItem {
    private String idStr;
    private String expense_ContentStr;
    private String expense_CostStr;

    public void setIdStr(String id) {
        idStr = id;
    }

    public void setExpense_ContentStr(String expense_Content) {
        expense_ContentStr = expense_Content;
    }

    public void setExpense_CostStr(String expense_Cost) {
        expense_CostStr = expense_Cost;
    }

    public String getIdStr() {
        return this.idStr;
    }

    public String getExpense_ContentStr() {
        return this.expense_ContentStr;
    }

    public String getExpense_CostStr() {
        return this.expense_CostStr;
    }

}
