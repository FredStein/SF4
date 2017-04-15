package com.fred.sn4;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.TextView;

import static android.content.Context.SENSOR_SERVICE;
import static android.os.SystemClock.elapsedRealtimeNanos;
import static java.lang.System.currentTimeMillis;

/**
 * Created by Fred Stein on 14/04/2017.
 */

class AccelerometerThread implements Runnable {
    private int sensorType;
    private Context mContext;
    int bin = 50000;                //:TODO This is microseconds. Timestamp is nanoseconds
    long tick = 0;
    long tock;
    int i = 0;
    double ax = 0;
    double ay = 0;
    double az = 0;

    AccelerometerThread(int sensorType, Context mContext) {
        this.sensorType = sensorType;
        this.mContext = mContext;
    }
    @Override
    public void run() {
        SensorManager mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(sensorType);
        HandlerThread mHandlerThread = new HandlerThread("SensorListener");
        mHandlerThread.start();
        Handler handler = new Handler(mHandlerThread.getLooper());

        SensorEventListener mListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (tick == 0) {
                    tick = event.timestamp;                                     //initialise bin start time
                    tock = tick + (long) bin * 1000;                            //initialise bin end time
                } else if (event.timestamp < tock) {                            // event within bin
                    ax += event.values[0];                                      // Start value accumulation and increment counter
                    ay += event.values[1];
                    az += event.values[2];
                    i += 1;
                } else if (event.timestamp > tock) {
                    tick = tock;                                                //Reset bin start / end
                    tock = tick + (long) bin * 1000;
                    /*:TODO Send average values + timestamp (for middle of bin) to handler
                      :TODO -> write to SensorActivity
                      :TODO -> Aggregate XML
                      :TODO -> send UDP
                    */
                    ax = event.values[0];                                       //Recommence value accumulation
                    ay = event.values[0];
                    az = event.values[0];
                    i = 1;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // do nothing
            }
        };

        mSensorManager.registerListener(mListener, sensor, bin ,handler);        // bin default = 50ms
    }

//    public void cleanThread(){
//        //Unregister the listener
//        if(mSensorManager != null) {
//            mSensorManager.unregisterListener(mListener);
//        }
//
//        if(mHandlerThread.isAlive())
//            mHandlerThread.quitSafely();
//    }
}
