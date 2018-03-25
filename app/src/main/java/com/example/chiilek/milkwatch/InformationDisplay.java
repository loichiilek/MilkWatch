package com.example.chiilek.milkwatch;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import static java.lang.String.format;

public class InformationDisplay extends AppCompatActivity {

    private static final int BAC_MAX_THRESHOLD = 100;
    private static final int BAC_MIN_THRESHOLD = 0;

    private static final int PH_MAX_THRESHOLD = 14;
    private static final int PH_MIN_THRESHOLD = 1;

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_display);

        // Initialize preferences.
        SharedPreferences sharedPref = getPreferences(0);
        final SharedPreferences.Editor editor = sharedPref.edit();

        // To set the values of both thresholds to the saved values.
        final TextView bacThreshold = findViewById(R.id.bacteria_threshold);
        bacThreshold.setText(Integer.toString(sharedPref.getInt("bacteriaThreshold", 50)));
        final TextView pHThreshold = findViewById(R.id.pH_threshold);
        pHThreshold.setText(String.format("%.1f", sharedPref.getFloat("pHThreshold", 7)));


        // To set seekBar values to the saved values.
        SeekBar bacSeekBar = findViewById(R.id.bacteria_seek_bar);
        bacSeekBar.setProgress(sharedPref.getInt("bacteriaThreshold", 50));
        SeekBar pHSeekBar = findViewById(R.id.pH_seek_bar);
        float pHVal = sharedPref.getFloat("pHThreshold", 7) / (PH_MAX_THRESHOLD - PH_MIN_THRESHOLD) * 100;
        pHSeekBar.setProgress((int) pHVal);


        // To set notification's colouring to the white/grey, based on saved preferences.
        final TextView notifText = findViewById(R.id.notification_text);
        Switch notifSwitch = findViewById(R.id.notification_switch);
        if (sharedPref.getBoolean("notification", true)) {
            notifText.setTextColor(getColor(R.color.colorAccent));
            notifSwitch.setChecked(true);
            Notification.setNotif(InformationDisplay.this, AlarmReceiver.class);
        } else {
            notifText.setTextColor(getColor(R.color.colorSpaceGreyLight));
            notifSwitch.setChecked(false);
        }

        // Bacteria seek bar's implementation
        bacSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int val = BAC_MIN_THRESHOLD + progress * BAC_MAX_THRESHOLD / 100;
                bacThreshold.setText(format("%d", val));
                editor.putInt("bacteriaThreshold", val);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.apply();
            }
        });

        // pH seek bar's implementation
        pHSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                double ratio = (double) progress / 100;
                double val = PH_MIN_THRESHOLD + ratio * (PH_MAX_THRESHOLD - PH_MIN_THRESHOLD);
                pHThreshold.setText(format("%.1f", val));
                editor.putFloat("pHThreshold", (float) val);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.apply();
            }
        });

        // Toggle switch's implementation
        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notifText.setTextColor(getColor(R.color.colorAccent));
                    editor.putBoolean("notification", true);
                    editor.apply();
                } else {
                    notifText.setTextColor(getColor(R.color.colorSpaceGreyLight));
                    editor.putBoolean("notification", false);
                    editor.apply();
                }
            }
        });
    }
}
