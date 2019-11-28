package com.example.tp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import static com.example.tp1.RateSQL.DeviseRateManager.DeviseRateEntry.TABLE_NAME;

public class RateManagerActivity extends AppCompatActivity {

    Button saveButton = null;
    TextInputEditText rateInput = null;
    TextView deviseTextView = null;
    private RateSQL rateSQL;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_manager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        saveButton = findViewById(R.id.saveButton);
        rateInput = findViewById(R.id.rateEntree);
        deviseTextView = findViewById(R.id.devise);

        // Get values passed by ratelist activity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final String deviseStr = bundle.getString("devise");
        final int id = bundle.getInt("id");
        Log.d("RateManager id : ", Integer.toString(id));

        // Init
        deviseTextView.setText(deviseStr);
        rateInput.setText( String.valueOf(bundle.getDouble("rate")));

        // Init SQL Database
        rateSQL = new RateSQL(getApplicationContext());
        db = rateSQL.getWritableDatabase();

        // Set Button Action
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();
                cv.put("devise",deviseStr); //These Fields should be your String values of actual column names
                cv.put("rate", String.valueOf(rateInput.getText()));
                int i = db.update(TABLE_NAME, cv, "_id="+id, null);
                Log.d("RateManager i : ", Integer.toString(i));
                db.close();
                onBackPressed();
            }
        });
    }
}