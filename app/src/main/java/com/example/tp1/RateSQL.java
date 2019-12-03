package com.example.tp1;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;

public class RateSQL extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "deviserate.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + DeviseRateManager.DeviseRateEntry.TABLE_NAME + " (" +
                    DeviseRateManager.DeviseRateEntry._ID + " INTEGER PRIMARY KEY," +
                    DeviseRateManager.DeviseRateEntry.COLUMN_NAME_DEVISE + " TEXT," +
                    DeviseRateManager.DeviseRateEntry.COLUMN_NAME_RATE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DeviseRateManager.DeviseRateEntry.TABLE_NAME;


    public RateSQL(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public final class DeviseRateManager {
        // To prevent someone from accidentally instantiating the contract class,
        // make the constructor private.
        private DeviseRateManager() {}

        /* Inner class that defines the table contents */
        public class DeviseRateEntry implements BaseColumns {
            public static final String TABLE_NAME = "deviserate";
            public static final String COLUMN_NAME_DEVISE = "devise";
            public static final String COLUMN_NAME_RATE = "rate";
        }
    }
}

