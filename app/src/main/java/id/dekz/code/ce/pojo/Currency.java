package id.dekz.code.ce.pojo;

/**
 * Created by DEKZ on 2/27/2016.
 */
public class Currency {
    private String CurrencyName,CurrencyCountry,CurrencyValue;

    public Currency() {
    }

    public Currency(String currencyName, String currencyCountry) {
        CurrencyName = currencyName;
        CurrencyCountry = currencyCountry;
    }

    public Currency(String currencyName, String currencyCountry, String currencyValue) {
        CurrencyName = currencyName;
        CurrencyCountry = currencyCountry;
        CurrencyValue = currencyValue;
    }

    public String getCurrencyName() {
        return CurrencyName;
    }

    public void setCurrencyName(String currencyName) {
        CurrencyName = currencyName;
    }

    public String getCurrencyCountry() {
        return CurrencyCountry;
    }

    public void setCurrencyCountry(String currencyCountry) {
        CurrencyCountry = currencyCountry;
    }

    public String getCurrencyValue() {
        return CurrencyValue;
    }

    public void setCurrencyValue(String currencyValue) {
        CurrencyValue = currencyValue;
    }
}
