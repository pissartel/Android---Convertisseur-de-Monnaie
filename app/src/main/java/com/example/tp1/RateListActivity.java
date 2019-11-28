package com.example.tp1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
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
        db.close();

        // Init listview
        List<DeviseRate> deviseRates = genererDeviseRates(dataRates);
        RateAdapter adapter = new RateAdapter(RateListActivity.this, deviseRates);
        listView.setAdapter(adapter);

        // Listener for ratemanager
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub

                Log.v("long clicked","pos: " + pos);
                Bundle b = new Bundle();
                String key = (String) dataRates.keySet().toArray()[pos];
                Log.d("Selected devise", key);
                Log.d("Selected rate", Double.toString(dataRates.get(key)));
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
        Toast.makeText(getApplicationContext(), "Appuyez longuement pour modifier le rate", Toast.LENGTH_LONG).show();
    }

    private List<DeviseRate> genererDeviseRates(HashMap<String, Double> deviseRatesMap){
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

    /* Class representant l'affichage des donnees */
    class RateViewHolder{
        public TextView devise;
        public TextView rate;
    }


    /* Class representant les donnees */
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

    /* class ADAPTER */
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
