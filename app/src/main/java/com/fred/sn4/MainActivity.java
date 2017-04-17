package com.fred.sn4;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //tag for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    //flag for logging
    private boolean mLogging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        TextView tv = (TextView) findViewById(R.id.tv);

        List<Sensor> mList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor item : mList) {
            tv.append( "\n" + item.getStringType());
        }
    }

    /** Called when the user taps the Sensors button */
    public void displaySensors(View view) {
        startActivity(new Intent(this, SensorActivity.class));
    }

}
