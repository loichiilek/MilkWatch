package com.example.chiilek.milkwatch.job_scheduler;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.chiilek.milkwatch.ProgressValueConverter;
import com.example.chiilek.milkwatch.R;
import com.example.chiilek.milkwatch.activity_classes.HomeActivity;
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

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static java.lang.String.format;


/**
 * Created by chiilek on 4/4/2018.
 */

public class NotificationJobService extends JobService{

    private static final String TAG = "mqtt";
    private static final String USERNAME = "gliqwcrb";
    private static final String PASSWORD = "4h-WUUkbI3uN";
    private static final String URL = "tcp://m12.cloudmqtt.com:18446";
    private static final String TOPIC = "milktest";

    MqttAndroidClient client;
    MqttConnectOptions options;


    Boolean MQTTBoolean = false;
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("Scheduler", "WORKS");
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
                    MsgParser msgParser = new MsgParser(new String(message.getPayload()));

                    Log.d("Message Payload", "from notification, pH: " + msgParser.getpH_value());
                    Log.d("Message Payload", "from notification, capacitance: " + msgParser.getCapacitance());

                    MQTTBoolean = true;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        if(MQTTBoolean){
            Intent openApp = new Intent(this, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openApp, PendingIntent.FLAG_UPDATE_CURRENT);

            // To send the notification here.
            @SuppressWarnings("deprecation") NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
            notification.setSmallIcon(R.drawable.cow_black);
            notification.setTicker("This is the ticker");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("Your milk is expiring soon!");
            notification.setContentText("MQTT working");
            notification.setContentIntent(pendingIntent);
            notification.setPriority(NotificationCompat.PRIORITY_HIGH);
            notification.setDefaults(NotificationCompat.DEFAULT_ALL);


            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(1, notification.build());
        }


        if(checkIfBelowThreshold(params)){
            Intent openApp = new Intent(this, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openApp, PendingIntent.FLAG_UPDATE_CURRENT);

            // To send the notification here.
            @SuppressWarnings("deprecation") NotificationCompat.Builder notification = new NotificationCompat.Builder(this);
            notification.setSmallIcon(R.drawable.cow_black);
            notification.setTicker("This is the ticker");
            notification.setWhen(System.currentTimeMillis());
            notification.setContentTitle("Your milk is expiring soon!");
            notification.setContentText("Finish your milk.");
            notification.setContentIntent(pendingIntent);
            notification.setPriority(NotificationCompat.PRIORITY_HIGH);
            notification.setDefaults(NotificationCompat.DEFAULT_ALL);


            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(1, notification.build());
        }

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }


    private boolean checkIfBelowThreshold(JobParameters params){
        float pHCalibratedVal = (float) params.getExtras().getDouble("pHCalibratedVal");
        float capacitanceCalibratedVal = (float) params.getExtras().getDouble("capacitanceCalibratedVal");
        float pHVal =(float) params.getExtras().getDouble("pHVal");
        float capacitanceVal = (float) params.getExtras().getDouble("capacitanceVal");

        Log.d("Scheduler pHCalibratedVal", Double.toString(params.getExtras().getDouble("pHCalibratedVal")));
        Log.d("Scheduler capacitanceCalibratedVal", Double.toString(params.getExtras().getDouble("capacitanceCalibratedVal")));
        Log.d("Scheduler pHVal", Double.toString(params.getExtras().getDouble("pHVal")));
        Log.d("Scheduler capacitanceVal", Double.toString(params.getExtras().getDouble("capacitanceVal")));


        if (pHVal < 0.9 * pHCalibratedVal || capacitanceVal < 0.9 * capacitanceCalibratedVal){
            Log.d("Scheduler", "Threshold Exceeded");
            return true;
        }
        else {
            return false;
        }
    }

    private void setSubscription(){
        try {
            client.subscribe(TOPIC, 0);
        } catch (MqttException e){
            e.printStackTrace();
        }
    }
}
