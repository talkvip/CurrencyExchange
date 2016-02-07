package id.dekz.code.ce.pojo;

/**
 * Created by DEKZ on 2/7/2016.
 */
public class Rate {
    private String base,symbol,date,updatedOn;
    private int amount;

    public Rate(String base, String symbol, String date, int amount, String updatedOn) {
        this.base = base;
        this.symbol = symbol;
        this.date = date;
        this.amount = amount;
        this.updatedOn = updatedOn;
    }

    public Rate(String base, String symbol){
        this.base = base;
        this.symbol = symbol;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
