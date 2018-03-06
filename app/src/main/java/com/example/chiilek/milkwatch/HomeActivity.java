package com.example.chiilek.milkwatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void fetch_data(View view) {
        Intent intent = new Intent(this, InformationDisplay.class);
        startActivity(intent);
    }
}
