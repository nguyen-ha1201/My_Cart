package hanu.a2_1801040055.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME= "cart.db";
    public static final int DB_VERSION = 1;


    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DbSchema.OrderTable.NAME + "(" +
                        DbSchema.OrderTable.Cols.PRODUCT_ID +" INTEGER PRIMARY KEY, " +
                        DbSchema.OrderTable.Cols.PRODUCT_NAME + " TEXT, " +
                        DbSchema.OrderTable.Cols.THUMBNAIL + " TEXT," +
                        DbSchema.OrderTable.Cols.UNIT_PRICE + " DOUBLE, "+
                        DbSchema.OrderTable.Cols.QUANTITY + " INTEGER" + ")"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop existing table
        Log.w("My Order", "My Order: upgrading DB; dropping/recreating tables.");
        db.execSQL("DROP TABLE IF EXISTS " + DbSchema.OrderTable.NAME);

        //other table here

        //onCreate again
        onCreate(db);
    }
}
