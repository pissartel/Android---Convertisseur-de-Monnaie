package com.example.tp1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.COLUMN_NAME_DEVISE;
import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.COLUMN_NAME_RATE;
import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.TABLE_NAME;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {


    Boolean statusNetwork;
    private AsyncTaskDATA rateTask;
    Button convertButton = null;
    Button parameterButton = null;
    TextInputEditText deviseInput = null;
    TextView deviseOutput = null;
    public Spinner deviseSpinnerIn = null;
    public Spinner deviseSpinnerOut = null;

    double val = 0;

    URL myUrl;
    DocumentBuilderFactory factory =null;
    DocumentBuilder docBuildder = null;
    Document document = null;
    HashMap<String, Double> dataXML = null;

    Intent intentRate = null;

    private RateSQL rateSQL;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "First onCreate() calls", Toast.LENGTH_SHORT).show();
        dataXML = new HashMap<>();
        dataXML.put("EUR", 1.0);
        rateSQL = new RateSQL(getApplicationContext());
        db = rateSQL.getWritableDatabase();
        rateTask = new AsyncTaskDATA(this);
        rateTask.execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "Démarrage appli", Toast.LENGTH_LONG).show();
        Log.e("onStart", "Start app");
        convertButton = findViewById(R.id.button1);
        deviseInput = findViewById(R.id.Entree);
        deviseOutput = findViewById(R.id.Dollarsprint);
        deviseSpinnerIn = (Spinner)findViewById(R.id.spinner4);
        deviseSpinnerOut = (Spinner)findViewById(R.id.spinner3);
        parameterButton = findViewById(R.id.param);


        intentRate = new Intent(MainActivity.this,ViewRate.class);

        convertButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if ((android.text.TextUtils.isDigitsOnly(deviseInput.getText())) ) {
            if (android.text.TextUtils.isEmpty(deviseInput.getText()))
                Log.e("chaine vide", "chaine vide");
            else {
                val = Double.parseDouble(deviseInput.getText().toString())/
                        dataXML.get(deviseSpinnerIn.getSelectedItem());
                 val *= dataXML.get(deviseSpinnerOut.getSelectedItem());

                deviseOutput.setText(String.valueOf(val));
            }

        }
        else
            Log.e("mauvaise saisie","auvaise saisie");
    }});


        parameterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentRate = new Intent( MainActivity.this, ViewRate.class );
                startActivityForResult(intentRate, 0);
            }});
    }

    private class AsyncTaskDATA extends AsyncTask {

        private Activity activity = null;
        public AsyncTaskDATA(Activity mainActivity){
            this.activity = mainActivity;
        }
        @SuppressLint("WrongThread")
        @Override
        protected Object doInBackground(Object[] objects) {
            statusNetwork = this.isOnline() ;
            Log.e("network state", Boolean.toString(statusNetwork) );

            if(statusNetwork)
            {
                try {
                    myUrl = new URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
                    URLConnection connection = myUrl.openConnection();
                    connection.setConnectTimeout(1000);
                    connection.connect();

                    factory = DocumentBuilderFactory.newInstance();
                    docBuildder = factory.newDocumentBuilder();
                    document = docBuildder.parse((myUrl.openStream()));
                    NodeList NodelistDoc = document.getElementsByTagName("Cube");
                    for (int i = 0; i < NodelistDoc.getLength(); i++) {
                        Element ele = (Element) NodelistDoc.item(i);
                        if (!ele.getAttribute("currency").isEmpty()) {
                            Log.d("element", ele.getAttribute("currency"));
                            Log.d("rate", ele.getAttribute("rate"));
                            Log.d("i:", String.valueOf(i));
                            dataXML.put(ele.getAttribute("currency"), Double.parseDouble(ele.getAttribute("rate")));
                            Log.d("hashmap", dataXML.toString());
                        }

                    }


                } catch (ParserConfigurationException | IOException | SAXException   e) {

                    Log.e("except1", e.getMessage());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (statusNetwork) {
                Log.d("hashmap", dataXML.toString());
                List<String> keys = new ArrayList<>(dataXML.keySet());
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.activity, android.R.layout.simple_spinner_item, keys);
                adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                deviseSpinnerIn.setAdapter(adapter2);
                deviseSpinnerOut.setAdapter(adapter2);

                // MAJ SQL
                Iterator dataIterator = dataXML.entrySet().iterator();

                while (dataIterator.hasNext()) {
                    Map.Entry mapElement = (Map.Entry)dataIterator.next();

                    ContentValues values = new ContentValues();
                    values.put(COLUMN_NAME_DEVISE, (String)mapElement.getKey());
                    values.put(COLUMN_NAME_RATE, (Double)mapElement.getValue());
                    db.insert(TABLE_NAME, null,values);
                    long newRowId  = db.insert(TABLE_NAME, null,values);
                Log.e("DataBase", Long.toString(newRowId));

                }
            }
            else {Log.e("Network", "error");}
        }

        public boolean isOnline() {
            boolean connected = false;
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                connected = networkInfo != null && networkInfo.isAvailable() &&
                        networkInfo.isConnected();
                return connected;


            } catch (Exception e) {
                System.out.println("CheckConnectivity Exception: " + e.getMessage());
                Log.v("connectivity", e.toString());
            }
            return connected;
        }
    }


}

