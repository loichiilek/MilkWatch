package com.example.chiilek.milkwatch.activity_classes;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.chiilek.milkwatch.CircularProgressBar;
import com.example.chiilek.milkwatch.ProgressValueConverter;
import com.example.chiilek.milkwatch.R;
import com.example.chiilek.milkwatch.mqtt.MsgParser;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.lang.String.format;

/**
 * Created by chiilek on 1/4/2018.
 */

public class CircularProgressBarActivity extends AppCompatActivity {

    private static final String TAG = "mqtt";
    private static final String USERNAME = "gliqwcrb";
    private static final String PASSWORD = "4h-WUUkbI3uN";
    private static final String URL = "tcp://m12.cloudmqtt.com:18446";
    private static final String TOPIC = "milktest";

    TextView subText;

    TextView pHValue;
    TextView capacitanceValue;

    CircularProgressBar pHBar;
    CircularProgressBar capBar;

    MqttAndroidClient client;
    MqttConnectOptions options;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_display2);

        sharedPref = getPreferences(0);
        editor = sharedPref.edit();

        subText = findViewById(R.id.subText);
        pHValue = findViewById(R.id.display2_pHValue);
        capacitanceValue = findViewById(R.id.display2_capacitanceValue);
        pHBar = findViewById(R.id.custom_progressBar_pH);
        capBar = findViewById(R.id.custom_progressBar_capacitance);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), URL, clientId);

        options = new MqttConnectOptions();


        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "Connect Success");
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "Connect Failure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                Log.d("Message Payload", "Entered on messageArrived");

                if (message.getPayload() == null){
                    Log.d("Message Payload", "Unable to get message payload.");
                } else {

                    Log.d("Message Payload", new String(message.getPayload()));
                    subText.setText(new String(message.getPayload()));
                    MsgParser msgParser = new MsgParser(new String(message.getPayload()));
                    pHValue.setText(format("%.1f", msgParser.getpH_value()));
                    capacitanceValue.setText(format("%.1f", msgParser.getCapacitance()));
                    editor.putFloat("pHVal", msgParser.getpH_value());
                    editor.putFloat("capacitanceVal", msgParser.getCapacitance());
                    pHBar.setProgressWithAnimation(40f);
                    capBar.setProgressWithAnimation(50f);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void setSubscription(){
        try {
            client.subscribe(TOPIC, 0);
        } catch (MqttException e){
            e.printStackTrace();
        }
    }

    public void conn(View v){
        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "Connect Success");
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "Connect Failure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void calibrate(View v){
        Log.d("Calibration", "cap: " + capBar.getProgress());
        Log.d("Calibration", "pH: " + pHBar.getProgress());
        capBar.setCalibrationWithAnimation(capBar.getProgress());
        pHBar.setCalibrationWithAnimation(pHBar.getProgress());
        editor.putFloat("pHVal", ProgressValueConverter.pHProgressToValue(pHBar.getProgress()));
        editor.putFloat("capacitanceVal", ProgressValueConverter.capProgressToValue(capBar.getProgress()));
    }
}
