package com.example.tp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.COLUMN_NAME_DEVISE;
import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.COLUMN_NAME_RATE;
import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.TABLE_NAME;

public class ViewRate extends AppCompatActivity {

    ListView listView;
    private RateSQL rateSQL;
    private SQLiteDatabase db;
    HashMap<String, Double> dataRates = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rate);

        listView = (ListView) findViewById(R.id.listView);
        rateSQL = new RateSQL(getApplicationContext());

        // Reading database
        db = rateSQL.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +TABLE_NAME, null);
        Log.d("cursor", cursor.toString());

        if(cursor.moveToFirst()) {
            do {
                // you are creating map here but not adding this map to list
                dataRates.put(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVISE)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_RATE)));

            } while (cursor.moveToNext());
        }
        db.close();

        Log.d("hashmap", dataRates.toString());
        List<String> devises = new ArrayList<>(dataRates.keySet());
        List<String> rates = new ArrayList<>();

        for (Double d:dataRates.values())
            rates.add(d.toString());

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewRate.this,
                android.R.layout.simple_list_item_1, devises);


        listView.setAdapter(adapter);


    }




}
