package com.example.tp1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RateSQL extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "deviserate";
    public static final String KEY_DEVISE="devise";
    public static final String KEY_RATE="rate";
    public static final String CREATE_TABLE_DEVISE_RATE = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_DEVISE+" INTEGER primary key," +
            " "+KEY_RATE+" TEXT" +
            ");";
    private static final String DATABASE_NAME = "db.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static RateSQL instance;
    private SQLiteDatabase db;
    private String DATABASE_PATH;
    private final Context mycontext;

    public RateSQL(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mycontext=context;
        String filesDir = context.getFilesDir().getPath(); // /data/data/com.package.nom/files/
        DATABASE_PATH = filesDir.substring(0, filesDir.lastIndexOf("/")) + "/databases/"; // /data/data/com.package.nom/databases/

        // Si la bdd n'existe pas dans le dossier de l'app
        if (!checkDatabase()) {
            // copy db de 'assets' vers DATABASE_PATH
            copyDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       // db.execSQL(CREATE_TABLE_DEVISE_RATE); // création table

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion){
            //Log.d("debug", "onUpgrade() : oldVersion=" + oldVersion + ",newVersion=" + newVersion);
            mycontext.deleteDatabase(DATABASE_NAME);
            copyDatabase();
        }
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static synchronized RateSQL getInstance(Context context) {
        if (instance == null) { instance = new RateSQL(context); }
        return instance;
    }

    private boolean checkDatabase() {
        File dbfile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbfile.exists();
    }

    private void copyDatabase() {

        final String outFileName = DATABASE_PATH + DATABASE_NAME;

        InputStream myInput;
        try {
            myInput = mycontext.getAssets().open(DATABASE_NAME);

            // dossier de destination
            File pathFile = new File(DATABASE_PATH);
            if(!pathFile.exists()) {
                if(!pathFile.mkdirs()) {
                    Log.e("DATABASE", "copyDatabase: ERROR");
                    return;
                }
            }

            // Ouverture du fichier bdd
            OutputStream myOutput = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Fermeture du fichier
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("DATABASE", "copyDatabase: ERROR");
        }

        // Numero de version
        try{
            SQLiteDatabase checkdb = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
            checkdb.setVersion(DATABASE_VERSION);
        }
        catch(SQLiteException e) {
            // bdd n'existe pas
        }

    }

}

