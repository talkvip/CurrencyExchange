package id.dekz.code.ce.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.dekz.code.ce.pojo.Rate;

/**
 * Created by DEKZ on 2/7/2016.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CE_DATABASE";

    //rates table structure
    private static final String TABLE_RATES = "rates";
    private static final String FIELD_RATES_ID = "rates_id";
    private static final String FIELD_RATES_DATE = "rates_date";
    private static final String FIELD_RATES_BASE = "rates_base";
    private static final String FIELD_RATES_SYMBOL = "rates_symbol";
    private static final String FIELD_RATES_AMOUNT = "rates_amount";
    private static final String FIELD_RATES_UPDATED_ON = "rates_updated_on";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_RATES = "CREATE TABLE "+TABLE_RATES+"("
                                    +FIELD_RATES_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                                    +FIELD_RATES_DATE+" TEXT,"
                                    +FIELD_RATES_BASE+" TEXT,"
                                    +FIELD_RATES_SYMBOL+" TEXT,"
                                    +FIELD_RATES_AMOUNT+" INTEGER,"
                                    +FIELD_RATES_UPDATED_ON+" TEXT)";
        db.execSQL(CREATE_TABLE_RATES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATES);
        onCreate(db);
    }

    //insert data
    public long saveData(Rate rate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_RATES_DATE, rate.getDate());
        values.put(FIELD_RATES_BASE, rate.getBase());
        values.put(FIELD_RATES_SYMBOL, rate.getSymbol());
        values.put(FIELD_RATES_AMOUNT, rate.getAmount());
        values.put(FIELD_RATES_UPDATED_ON, rate.getUpdatedOn());

        return db.insert(TABLE_RATES, null, values);
    }

    //update data
    public int updateData(Rate rate){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIELD_RATES_DATE, rate.getDate());
        values.put(FIELD_RATES_AMOUNT, rate.getAmount());
        values.put(FIELD_RATES_UPDATED_ON, rate.getUpdatedOn());

        return db.update(TABLE_RATES,
                values,
                FIELD_RATES_BASE + " = ? AND " + FIELD_RATES_SYMBOL + " = ?",
                new String[]{rate.getBase(), rate.getSymbol()});
    }

    //check if data already in db
    public int checkData(Rate rate){
        String queryCekData = "SELECT * FROM "+TABLE_RATES+" WHERE "
                +FIELD_RATES_BASE+"='"+rate.getBase()+"' AND "
                +FIELD_RATES_SYMBOL+"='"+rate.getSymbol()+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryCekData, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //get data date to check if data older than one day
    public String getDataDate(Rate rate){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_RATES,
                new String[]{FIELD_RATES_UPDATED_ON},
                FIELD_RATES_BASE + " = ? AND " + FIELD_RATES_SYMBOL + " = ?",
                new String[]{rate.getBase(), rate.getSymbol()},
                null, null, null, null);

        if (cursor!=null){
            cursor.moveToFirst();
        }

        return cursor.getString(0);
    }

    //get single rate data
    public Rate getSingleRate(Rate rate){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_RATES,
                new String[]{FIELD_RATES_BASE, FIELD_RATES_SYMBOL, FIELD_RATES_DATE, FIELD_RATES_AMOUNT, FIELD_RATES_UPDATED_ON},
                FIELD_RATES_BASE + " = ? AND " + FIELD_RATES_SYMBOL + " = ?",
                new String[]{rate.getBase(), rate.getSymbol()},
                null, null, null, null);

        if (cursor!=null){
            cursor.moveToFirst();
        }

        Rate resultRate = new Rate(cursor.getString(0),
                                   cursor.getString(1),
                                   cursor.getString(2),
                                   cursor.getInt(3),
                                   cursor.getString(4));

        return resultRate;
    }
}
