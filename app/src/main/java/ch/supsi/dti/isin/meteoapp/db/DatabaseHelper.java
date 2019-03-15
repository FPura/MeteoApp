package ch.supsi.dti.isin.meteoapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "locations.db"; // nome del database
    private static final int VERSION = 1; // versione
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DbSchema.Table.NAME + "("
                + " _id integer primary key autoincrement, "
                + DbSchema.Table.Cols.UUID
                + ", "
                + DbSchema.Table.Cols.NAME
                + ")"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}