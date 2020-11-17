package app.easygames.falsum;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper  extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "configurationsDb";
    public static final String TABLE_CONFIGURATIONS = "configurations";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_BALANCE = "balance";
    public static final String KEY_BILL = "bill";
    public static final String KEY_BILL_DATE = "bill_date";
    public static final String KEY_BILL_TIME = "bill_time";
    public static final String KEY_REWARD = "reward";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CONFIGURATIONS + "(" + KEY_ID
                + " integer primary key,"  + KEY_NAME + " text," +  KEY_BALANCE
                + " text," + KEY_BILL + " text," + KEY_BILL_DATE + " text," + KEY_BILL_TIME + " text," + KEY_REWARD
                + " text," + KEY_DATE + " text," + KEY_TIME + " text" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_CONFIGURATIONS);

        onCreate(db);

    }
}
