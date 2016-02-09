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

    //constructor
    public PreferencesManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences("session",0);
        editor = pref.edit();
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
