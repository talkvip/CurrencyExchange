package id.dekz.code.ce.util;

import android.content.Context;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DEKZ on 2/7/2016.
 */
public class StringToDate {

    private String dbDate,nowDate;
    private SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
    private SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.UK);
    private SimpleDateFormat sdfDate = new SimpleDateFormat("MMM, dd yyyy", Locale.UK);
    private Context context;

    public StringToDate(Context context, String dbDate, String nowDate) {
        this.context = context;
        this.dbDate = dbDate;
        this.nowDate = nowDate;
    }

    public String convertDate(String strDate){
        try {
            Date date = formatDate.parse(strDate);
            String newDate = sdfDate.format(date);
            return newDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "null";
        }
    }

    public String difference(String dbDate,String nowDate){
        try {
            Date datedb = formatDate.parse(dbDate);
            Date datenow = formatDate.parse(nowDate);
            if(datedb.before(datenow)){
                return "outdated";
            }else{
                return "ok";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getDbDate() {
        return dbDate;
    }

    public void setDbDate(String dbDate) {
        this.dbDate = dbDate;
    }

    public String getNowDate() {
        return nowDate;
    }

    public void setNowDate(String nowDate) {
        this.nowDate = nowDate;
    }
}
