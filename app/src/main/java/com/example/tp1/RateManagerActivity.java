package com.example.tp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class RateManagerActivity extends AppCompatActivity {

    Button saveButton = null;
    TextInputEditText rateInput = null;
    TextView deviseTextView = null;

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
        Bundle extras = intent.getExtras();

        // Init
        deviseTextView.setText( extras.getString("devise"));
        rateInput.setText( String.valueOf(extras.getDouble("rate")));
    }

}
