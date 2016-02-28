package id.dekz.code.ce.util;

import java.util.ArrayList;
import java.util.List;

import id.dekz.code.ce.pojo.Currency;

/**
 * Created by DEKZ on 2/27/2016.
 */
public class GenerateCurrencyList {

    private static final String AUD = "AUD";
    private static final String AUD_name = "Australian Dollar";
    private static final String BGN = "BGN";
    private static final String BGN_name = "Bulgarian Lev";
    private static final String BRL = "BRL";
    private static final String BRL_name = "Brazilian Real";
    private static final String CAD = "CAD";
    private static final String CAD_name = "Canadian Dollar";
    private static final String CHF = "CHF";
    private static final String CHF_name = "Swiss Franc";
    private static final String CNY = "CNY";
    private static final String CNY_name = "Chinese Yuan Renminbi";
    private static final String CZK = "CZK";
    private static final String CZK_name = "Czech Koruna";
    private static final String DKK = "DKK";
    private static final String DKK_name = "Danish Krone";
    private static final String EUR = "EUR";
    private static final String EUR_name = "Euro";
    private static final String GBP = "GBP";
    private static final String GBP_name = "British Pound";
    private static final String HKD = "HKD";
    private static final String HKD_name = "Hong Kong Dollar";
    private static final String HRK = "HRK";
    private static final String HRK_name = "Croatian Kuna";
    private static final String HUF = "HUF";
    private static final String HUF_name = "Hungarian Forint";
    private static final String IDR = "IDR";
    private static final String IDR_name = "Indonesian Rupiah";
    private static final String ILS = "ILS";
    private static final String ILS_name = "Israeli Shekel";
    private static final String INR = "INR";
    private static final String INR_name = "Indian Rupee";
    private static final String JPY = "JPY";
    private static final String JPY_name = "Japanese Yen";
    private static final String KRW = "KRW";
    private static final String KRW_name = "South Korean Won";
    private static final String MXN = "MXN";
    private static final String MXN_name = "Mexican Peso";
    private static final String MYR = "MYR";
    private static final String MYR_name = "Malaysian Ringgit";
    private static final String NOK = "NOK";
    private static final String NOK_name = "Norwegian Krone";
    private static final String NZD = "NZD";
    private static final String NZD_name = "New Zealand Dollar";
    private static final String PHP = "PHP";
    private static final String PHP_name = "Philippine Peso";
    private static final String PLN = "PLN";
    private static final String PLN_name = "Polish Zloty";
    private static final String RON = "RON";
    private static final String RON_name = "Romanian New Leu";
    private static final String RUB = "RUB";
    private static final String RUB_name = "Russian Ruble";
    private static final String SEK = "SEK";
    private static final String SEK_name = "Swedish Krona";
    private static final String SGD = "SGD";
    private static final String SGD_name = "Singapore Dollar";
    private static final String THB = "THB";
    private static final String THB_name = "Thai Baht";
    private static final String USD = "USD";
    private static final String USD_name = "US Dollar";
    private static final String ZAR = "ZAR";
    private static final String ZAR_name = "South African Rand";

    private List<Currency> currencies = new ArrayList<Currency>();

    public GenerateCurrencyList() {
    }

    public List<Currency> getListCurrency(){
        currencies.clear();
        currencies.add(new Currency(AUD, AUD_name));
        currencies.add(new Currency(BGN, BGN_name));
        currencies.add(new Currency(BRL, BRL_name));
        currencies.add(new Currency(CAD, CAD_name));
        currencies.add(new Currency(CHF, CHF_name));
        currencies.add(new Currency(CNY, CNY_name));
        currencies.add(new Currency(CZK, CZK_name));
        currencies.add(new Currency(DKK, DKK_name));
        currencies.add(new Currency(EUR, EUR_name));
        currencies.add(new Currency(GBP, GBP_name));
        currencies.add(new Currency(HKD, HKD_name));
        currencies.add(new Currency(HRK, HRK_name));
        currencies.add(new Currency(HUF, HUF_name));
        currencies.add(new Currency(IDR, IDR_name));
        currencies.add(new Currency(ILS, ILS_name));
        currencies.add(new Currency(INR, INR_name));
        currencies.add(new Currency(JPY, JPY_name));
        currencies.add(new Currency(KRW, KRW_name));
        currencies.add(new Currency(MXN, MXN_name));
        currencies.add(new Currency(MYR, MYR_name));
        currencies.add(new Currency(NOK, NOK_name));
        currencies.add(new Currency(NZD, NZD_name));
        currencies.add(new Currency(PHP, PHP_name));
        currencies.add(new Currency(PLN, PLN_name));
        currencies.add(new Currency(RON, RON_name));
        currencies.add(new Currency(RUB, RUB_name));
        currencies.add(new Currency(SEK, SEK_name));
        currencies.add(new Currency(SGD, SGD_name));
        currencies.add(new Currency(THB, THB_name));
        currencies.add(new Currency(USD, USD_name));
        currencies.add(new Currency(ZAR, ZAR_name));
        return currencies;
    }
}
