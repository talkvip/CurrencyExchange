package id.dekz.code.ce.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DEKZ on 2/9/2016.
 */
public class PreferencesManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    public static final String IS_OFFLINE = "IsOffline";
    public static final String KEY_ASK_TO_OFFLINE = "askToOffline";
    public static final String KEY_LAST_CURRENCY_FROM = "lastCurrencyFrom";
    public static final String KEY_LAST_CURRENCY_TO = "lastCurrencyTo";

    //constructor
    public PreferencesManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences("session",0);
        editor = pref.edit();
    }

    public String getKeyLastCurrencyFrom(){
        return pref.getString(KEY_LAST_CURRENCY_FROM,"USD");
    }

    public String getKeyLastCurrencyTo(){
        return pref.getString(KEY_LAST_CURRENCY_TO,"EUR");
    }

    public void setKeyLastCurrencyTo(String currencyTo){
        editor.putString(KEY_LAST_CURRENCY_TO,currencyTo);
        editor.commit();
    }

    public void setKeyLastCurrencyFrom(String currencyFrom){
        editor.putString(KEY_LAST_CURRENCY_FROM, currencyFrom);
        editor.commit();
    }

    public void dontShowOfflineDialog(){
        editor.putBoolean(KEY_ASK_TO_OFFLINE,false);
        editor.commit();
    }

    public void showOfflineDialog(){
        editor.putBoolean(KEY_ASK_TO_OFFLINE,true);
        editor.commit();
    }

    public boolean isOfflineDialogCanAppear(){
        return pref.getBoolean(KEY_ASK_TO_OFFLINE,true);
    }

    public boolean isUsingAppOffline(){
        return pref.getBoolean(IS_OFFLINE,false);
    }

    public void setUsingAppOffline(){
        editor.putBoolean(IS_OFFLINE,true);
        editor.commit();
    }

    public void setUsingAppOnline(){
        editor.putBoolean(IS_OFFLINE,false);
        editor.commit();
    }
}
