package com.fred.sn4;

import android.support.v7.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_GRAVITY;
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static android.hardware.Sensor.TYPE_LINEAR_ACCELERATION;
import static android.hardware.Sensor.TYPE_ROTATION_VECTOR;
import static android.os.SystemClock.elapsedRealtimeNanos;
import static java.lang.System.currentTimeMillis;

//SENSOR_TYPE_ACCELEROMETER m/s^2                                A      1
//SENSOR_TYPE_GYROSCOPE r/s                                      G      4
//SENSOR_TYPE_LINEAR_ACCELERATION m/s^2(?) (Device coordinates?) LA     10
//SENSOR_TYPE_GRAVITY r, theta m/s^2 (Device coordinates?)       Grav   9
//SENSOR_TYPE_ROTATION_VECTOR ( unitless World coordinates?)     R      11

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private List<Sensor> mSensorL = new ArrayList<>();
    private int[] sList = {TYPE_ACCELEROMETER,TYPE_GYROSCOPE,TYPE_GRAVITY,
                            TYPE_LINEAR_ACCELERATION,TYPE_ROTATION_VECTOR};
    private String[] sNames = {"Accelerometer","Gyroscope","Gravity Sensor", "Linear Accelerometer",
                            "Rotation Vector"};
    private String[] prefixList = {"A","G","LA","Grav","R"};
    //TODO  Integrate sList and sNames into a map
    private Map<Integer,String> sPrefix = new HashMap<>();
    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_activity);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        int i = 0;
        for (int item : sList){
        // TODO Amend so Sensor only displayed if present (Set Header and values to visible?)
            if (mSensorManager.getDefaultSensor(item) != null) {
                // We have this sensor
                mSensorL.add(i,mSensorManager.getDefaultSensor(item));
                sPrefix.put(item,prefixList[i]);
                mSensorManager.registerListener(this, mSensorL.get(i),
                        SensorManager.SENSOR_DELAY_UI);                 //~62.5ms _NORMAl ~190 ms _FASTEST ~5ms
            } else {
                Toast.makeText(getApplicationContext(), sNames[i] + getResources().getString(R.string.absent), Toast.LENGTH_LONG).show();
            }
            i++;
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case TYPE_ACCELEROMETER:{
                TextView x;
                TextView y;
                TextView z;
                TextView t;

                x = (TextView) findViewById(R.id.Ax);
                y = (TextView) findViewById(R.id.Ay);
                z = (TextView) findViewById(R.id.Az);
                t = (TextView) findViewById(R.id.At);

                float xVal = event.values[0];
                float yVal = event.values[1];
                float zVal = event.values[2];
                long ts = (currentTimeMillis() + (event.timestamp - elapsedRealtimeNanos ()) / 1000000L);   //Milliseconds

                String sx = String.format ("%.3f", xVal);
                String sy = String.format ("%.3f", yVal);
                String sz = String.format ("%.3f", zVal);
                String st = String.format ("%d", ts%100000);

                timestamp = ts;

                x.setText(sx);
                y.setText(sy);
                z.setText(sz);
                t.setText(st);
            }break;

            case TYPE_GYROSCOPE:{
                TextView x;
                TextView y;
                TextView z;
                TextView t;

                x = (TextView) findViewById(R.id.Gx);
                y = (TextView) findViewById(R.id.Gy);
                z = (TextView) findViewById(R.id.Gz);
                t = (TextView) findViewById(R.id.Gt);

                float xVal = event.values[0];
                float yVal = event.values[1];
                float zVal = event.values[2];
                long ts = (currentTimeMillis() + (event.timestamp - elapsedRealtimeNanos ()) / 1000000L);   //Milliseconds

                String sx = String.format ("%.3f", xVal);
                String sy = String.format ("%.3f", yVal);
                String sz = String.format ("%.3f", zVal);
                String st = String.format ("%d", ts%100000);

                x.setText(sx);
                y.setText(sy);
                z.setText(sz);
                t.setText(st);
            }break;
            case TYPE_LINEAR_ACCELERATION:{
                TextView x;
                TextView y;
                TextView z;
                TextView t;

                x = (TextView) findViewById(R.id.LAx);
                y = (TextView) findViewById(R.id.LAy);
                z = (TextView) findViewById(R.id.LAz);
                t = (TextView) findViewById(R.id.LAt);

                float xVal = event.values[0];
                float yVal = event.values[1];
                float zVal = event.values[2];
                long ts = (currentTimeMillis() + (event.timestamp - elapsedRealtimeNanos ()) / 1000000L);   //Milliseconds

                String sx = String.format ("%.3f", xVal);
                String sy = String.format ("%.3f", yVal);
                String sz = String.format ("%.3f", zVal);
                String st = String.format ("%d", ts%100000);

                x.setText(sx);
                y.setText(sy);
                z.setText(sz);
                t.setText(st);
            }break;
            case TYPE_GRAVITY:{
                TextView x;
                TextView y;
                TextView z;
                TextView t;

                x = (TextView) findViewById(R.id.Gravx);
                y = (TextView) findViewById(R.id.Gravy);
                z = (TextView) findViewById(R.id.Gravz);
                t = (TextView) findViewById(R.id.Gravt);

                float xVal = event.values[0];
                float yVal = event.values[1];
                float zVal = event.values[2];
                long ts = (currentTimeMillis() + (event.timestamp - elapsedRealtimeNanos ()) / 1000000L);   //Milliseconds

                String sx = String.format ("%.3f", xVal);
                String sy = String.format ("%.3f", yVal);
                String sz = String.format ("%.3f", zVal);
                String sdt = String.format ("%d", ts%100000);

                x.setText(sx);
                y.setText(sy);
                z.setText(sz);
                t.setText(sdt);
            }break;
            case TYPE_ROTATION_VECTOR:{
                TextView x;
                TextView y;
                TextView z;
                TextView r;
                TextView t;

                x = (TextView) findViewById(R.id.Rx);
                y = (TextView) findViewById(R.id.Ry);
                z = (TextView) findViewById(R.id.Rz);
                r = (TextView) findViewById(R.id.Rr);
                t = (TextView) findViewById(R.id.Rt);

                float xVal = event.values[0];
                float yVal = event.values[1];
                float zVal = event.values[2];
                float rVal = event.values[3];
                long ts = (currentTimeMillis() + (event.timestamp - elapsedRealtimeNanos ()) / 1000000L);   //Milliseconds

                String sx = String.format ("%.3f", xVal);
                String sy = String.format ("%.3f", yVal);
                String sz = String.format ("%.3f", zVal);
                String sr = String.format ("%.3f", rVal);
                String st = String.format ("%d", ts%100000);

                x.setText(sx);
                y.setText(sy);
                z.setText(sz);
                r.setText(sr);
                t.setText(st);
            }break;

            default: break;
        }
    }

    protected void onResume() {
        super.onResume();
        int i = 0;
        for (int item : sList){
            if (mSensorManager.getDefaultSensor(item) != null) {
                mSensorManager.registerListener(this, mSensorL.get(i), SensorManager.SENSOR_DELAY_NORMAL);
            i++;
            }
        }
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
