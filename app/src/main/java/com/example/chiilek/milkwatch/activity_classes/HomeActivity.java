package com.example.chiilek.milkwatch.activity_classes;

import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chiilek.milkwatch.job_scheduler.NotificationJobService;
import com.example.chiilek.milkwatch.R;

public class HomeActivity extends AppCompatActivity {

    private static final int JOB_ID = 101;
    private android.app.job.JobScheduler jobScheduler;
    private JobInfo jobInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPref = this.getSharedPreferences("values", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        float pHCalibratedVal = sharedPref.getFloat("pHCalibratedVal", 1.0f);
        float capacitanceCalibratedVal = sharedPref.getFloat("capacitanceCalibratedVal", 1.0f);
        float pHVal = sharedPref.getFloat("pHVal", 0.0f);
        float capacitanceVal = sharedPref.getFloat("capacitanceVal", 0.0f);

        Log.d("onCreate HOME", "pH" + Float.toString(pHCalibratedVal));
        Log.d("onCreate HOME", "cap" + Float.toString(capacitanceCalibratedVal));

        PersistableBundle bundle = new PersistableBundle();
        bundle.putDouble("pHCalibratedVal", pHCalibratedVal);
        bundle.putDouble("capacitanceCalibratedVal", capacitanceCalibratedVal);
        bundle.putDouble("pHVal", pHVal);
        bundle.putDouble("capacitanceVal", capacitanceVal);

        ComponentName serviceName = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                .setMinimumLatency(20000)
          //      .setPeriodic(1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(bundle)
                .build();
        jobScheduler = (android.app.job.JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

//        MessageReceiver receiver = new MessageReceiver(new Message());
//
//        // To start the background intent whenever this activity is created.
//        Intent backgroundIntent = new Intent(this, BackgroundService.class);
//        backgroundIntent.putExtra("timer", 10);
//        backgroundIntent.putExtra("receiver", receiver);
//        startService(backgroundIntent);
        jobScheduler.schedule(jobInfo);
    }

    public void fetch_data(View view) {

        Log.d("Scheduler", "Started Schedule");
        Intent intent = new Intent(this, CircularProgressBarActivity.class);
        startActivity(intent);
    }

    // To pass data back to UI.
    public class Message {
        public void displayMessage(int resultCode, Bundle resultData){
            String message = resultData.getString("message");
            Toast.makeText(HomeActivity.this, message + "", Toast.LENGTH_SHORT).show();
        }
    }

}
