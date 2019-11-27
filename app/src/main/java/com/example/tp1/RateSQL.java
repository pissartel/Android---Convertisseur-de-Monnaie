package com.example.tp1;

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

public class RateSQL extends SQLiteOpenHelper {

    private final Context mycontext;
    private static RateSQL sInstance;
    public static final int DATABASE_VERSION = 1;
    private String DATABASE_PATH;
    public static final String DATABASE_NAME = "DeviseRate.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DeviseRateManager.DeviseRateEntry.TABLE_NAME + " (" +
                    DeviseRateManager.DeviseRateEntry._ID + " INTEGER PRIMARY KEY," +
                    DeviseRateManager.DeviseRateEntry.COLUMN_NAME_DEVISE + " TEXT," +
                    DeviseRateManager.DeviseRateEntry.COLUMN_NAME_RATE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DeviseRateManager.DeviseRateEntry.TABLE_NAME;


    public RateSQL(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext=context;
        String filesDir = context.getFilesDir().getPath(); // /data/data/com.package.nom/files/
        DATABASE_PATH = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/"; // /data/data/com.package.nom/databases/

        // Si la bdd n'existe pas dans le dossier de l'app
        if (!checkDatabase()) {
            // copy db de 'assets' vers DATABASE_PATH
            Log.d("SQL Database", "creation ");
            copyDatabase();
        }
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

    private boolean checkDatabase() {
        // retourne true/false si la bdd existe dans le dossier de l'app
        File dbfile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbfile.exists();
    }

    private void copyDatabase() {

        final String outFileName = DATABASE_PATH + DATABASE_NAME;

        InputStream myInput;
        try {
            // Ouvre la bdd de 'assets' en lecture
            myInput = mycontext.getAssets().open(DATABASE_NAME);

            // dossier de destination
            File pathFile = new File(DATABASE_PATH);
            if(!pathFile.exists()) {
                if(!pathFile.mkdirs()) {
                    Log.e("SQL DataBase copy", "Erreur : pathFile.mkdirs() ");
                    return;
                }
            }

            // Ouverture en écriture du fichier bdd de destination
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfert de inputfile vers outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Fermeture
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("SQL DataBase copy", "Erreur ");
        }

        // on greffe le numéro de version
        try{
            SQLiteDatabase checkdb = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            checkdb.setVersion(DATABASE_VERSION);
        }
        catch(SQLiteException e) {
            Log.e("SQL DataBase copy", "Databse doesn't exist ");
        }

    } // copydatabase()

    public static synchronized RateSQL getInstance(Context context) {
        if (sInstance == null) {
            Log.d("SQL Instance", "database not instanced yet");
            sInstance = new RateSQL(context);
        }
        else Log.d("SQL Instance", "Already an instance of database");

        return sInstance;
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

