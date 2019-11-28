package com.example.tp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.COLUMN_NAME_DEVISE;
import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.COLUMN_NAME_RATE;
import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.TABLE_NAME;
import static java.security.AccessController.getContext;

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
        dataRates = new HashMap<>();

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

        Log.d("hashmap view rate", dataRates.toString());
       // List<String> devises = new ArrayList<>(dataRates.keySet());
       // List<String> rates = new ArrayList<>();

        //for (Double d:dataRates.values())
         //   rates.add(d.toString());

       //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewRate.this,
        //        android.R.layout.simple_list_item_1, devises);
        

        List<DeviseRate> deviseRates = genererDeviseRates(dataRates);

        RateAdapter adapter = new RateAdapter(ViewRate.this, deviseRates);
        listView.setAdapter(adapter);
    }

    private List<DeviseRate> genererDeviseRates(HashMap<String, Double> deviseRatesMap){
        List<DeviseRate> deviseRates = new ArrayList<DeviseRate>();

        for (Map.Entry<String, Double> entry : deviseRatesMap.entrySet()) {
            deviseRates.add( new  DeviseRate(entry.getKey(), entry.getValue()));
        }
        return deviseRates;
    }

    //convertView est notre vue recyclée
    public View getView(int position, View convertView, ViewGroup parent) {

        //Android nous fournit un convertView null lorsqu'il nous demande de la créer
        //dans le cas contraire, cela veux dire qu'il nous fournit une vue recyclée
        if(convertView == null){
            //Nous récupérons notre row_tweet via un LayoutInflater,
            //qui va charger un layout xml dans un objet View
            convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.devise_rate,parent, false);
        }

        RateViewHolder viewHolder = (RateViewHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new RateViewHolder();
            viewHolder.devise = (TextView) convertView.findViewById(R.id.devise);
            viewHolder.rate = (TextView) convertView.findViewById(R.id.rate);
            convertView.setTag(viewHolder);
        }

        //nous renvoyons notre vue à l'adapter, afin qu'il l'affiche
        //et qu'il puisse la mettre à recycler lorsqu'elle sera sortie de l'écran
        return convertView;
    }

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

    /* ADAPTER */
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

            //getItem(position) va récupérer l'item [position] de la List<Tweet> tweets
            DeviseRate deviserate = getItem(position);

            //il ne reste plus qu'à remplir notre vue
            viewHolder.devise.setText(deviserate.getDevise());
            viewHolder.rate.setText( Double.toString(deviserate.getRate()));

            return convertView;
        }

    }






}
