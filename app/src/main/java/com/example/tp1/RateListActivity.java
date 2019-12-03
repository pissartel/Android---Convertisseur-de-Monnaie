package com.example.tp1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.COLUMN_NAME_DEVISE;
import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.COLUMN_NAME_RATE;
import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.TABLE_NAME;

public class RateListActivity extends AppCompatActivity {

    ListView listView;
    private RateSQL rateSQL;
    private SQLiteDatabase db;
    private Intent intentRateManager = null;
    private HashMap<String, Double> dataRates = null;
    private boolean isOpenning = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_list);

        listView = (ListView) findViewById(R.id.listView);
        rateSQL = new RateSQL(getApplicationContext());
        dataRates = new HashMap<>();

        // Init intent
        intentRateManager = new Intent(RateListActivity.this, RateManagerActivity.class);

        // Reading database
        db = rateSQL.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +TABLE_NAME, null);
        Log.d("cursor view rate", cursor.toString());

        if(cursor.moveToFirst()) {
            do {
                // you are creating map here but not adding this map to list
                dataRates.put(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVISE)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_RATE)));

            } while (cursor.moveToNext());
        }

        // Init listview
        List<DeviseRate> deviseRates = generateDeviseRates(dataRates);
        RateAdapter adapter = new RateAdapter(RateListActivity.this, deviseRates);
        listView.setAdapter(adapter);


        // Listener for ratemanager
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                db.close();
                Log.v("long clicked","pos: " + pos);
                Bundle b = new Bundle();
                String key = (String) dataRates.keySet().toArray()[pos];
                Log.d("Selected devise", key);
                Log.d("Selected rate", Double.toString(dataRates.get(key)));
                b.putInt("id", pos);
                b.putString("devise", key);
                b.putDouble("rate", dataRates.get(key));
                intentRateManager.putExtras(b);
                startActivityForResult(intentRateManager, 0);
                return true;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Reading database
        db = rateSQL.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +TABLE_NAME, null);
        Log.d("cursor view rate", cursor.toString());

        if(cursor.moveToFirst()) {
            do {
                // you are creating map here but not adding this map to list
                dataRates.put(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVISE)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_RATE)));

            } while (cursor.moveToNext());
        }
        db.close();
        if (isOpenning) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Appuyez longuement sur une devise pour modifier son taux");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        isOpenning = false;


    }

    @Override
    protected void onResume() {
        super.onResume();
        dataRates.clear();
        db = rateSQL.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +TABLE_NAME, null);
        Log.d("cursor view rate", cursor.toString());

        if(cursor.moveToFirst()) {
            do {
                // you are creating map here but not adding this map to list
                dataRates.put(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_DEVISE)),
                        cursor.getDouble(cursor.getColumnIndex(COLUMN_NAME_RATE)));

            } while (cursor.moveToNext());
        }

        List<DeviseRate> deviseRates = generateDeviseRates(dataRates);
        RateAdapter adapter = new RateAdapter(RateListActivity.this, deviseRates);
        listView.setAdapter(adapter);

    }


    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

    private List<DeviseRate> generateDeviseRates(HashMap<String, Double> deviseRatesMap){
        List<DeviseRate> deviseRates = new ArrayList<DeviseRate>();

        for (Map.Entry<String, Double> entry : deviseRatesMap.entrySet()) {
            deviseRates.add( new  DeviseRate(entry.getKey(), entry.getValue()));
        }
        return deviseRates;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.devise_rate,parent, false);
        }

        RateViewHolder viewHolder = (RateViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new RateViewHolder();
            viewHolder.devise = (TextView) convertView.findViewById(R.id.devise);
            viewHolder.rate = (TextView) convertView.findViewById(R.id.rate);
            convertView.setTag(viewHolder);
        }

        return convertView;
    }

    /* Classe pour l'affichage des donnees */
    class RateViewHolder{
        public TextView devise;
        public TextView rate;
    }


    /* Classe representant les donnees */
    public class DeviseRate {

        private String devise;
        private Double rate;

        public DeviseRate(String devise, Double rate) {
            this.devise = devise;
            this.rate = rate;
        }

        public String getDevise() {
            return devise;
        }

        public Double getRate() {
            return rate;
        }

        public void setDevise(String devise) {
            this.devise = devise;
        }

        public void setRate(Double rate) {
            this.rate = rate;
        }
    }

    /* classe ADAPTER */
    public class RateAdapter extends ArrayAdapter<DeviseRate> {

        public RateAdapter(Context context, List<DeviseRate> deviserates) {
            super(context, 0, deviserates);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.devise_rate,parent, false);
            }

            RateViewHolder viewHolder = (RateViewHolder) convertView.getTag();
            if(viewHolder == null){
                viewHolder = new RateViewHolder();
                viewHolder.devise = (TextView) convertView.findViewById(R.id.devise);
                viewHolder.rate = (TextView) convertView.findViewById(R.id.rate);
                convertView.setTag(viewHolder);
            }

            DeviseRate deviserate = getItem(position);
            viewHolder.devise.setText(deviserate.getDevise());
            viewHolder.rate.setText( Double.toString(deviserate.getRate()));

            return convertView;
        }

    }






}
