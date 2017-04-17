package com.fred.sn4;
/*
 * Created by Fred Stein on 14/04/2017.
 * Creates a thread to read a sensorType of TYPE_? and return binned data
 * This iteration averages data received during the bin length (Default: 50 ms)
 * SensorThread explicitly sets the sensor listener hint rather than using an android constant (Default: 20 ms)
 * bin and listenHint are available for future use as parameters to the SensorThread constructor
 * :param  int sensorType:      any android sensor
   :param  Context mContext:    the context starting the thread
   :param  Handler sHandler:    the display handler receiving the binned sensor readings
   :param  Queue uQueue:        the queue holding each binned sensor reading (taken off async and built to XML)
 */

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;

import java.util.Queue;

import static android.content.Context.SENSOR_SERVICE;
import static android.os.SystemClock.elapsedRealtimeNanos;
import static java.lang.System.currentTimeMillis;

class SensorThread implements Runnable {
    //tag for logging
    private static final String TAG = SensorThread.class.getSimpleName();
    //flag for logging
    private boolean mLogging = false;

    private int sensorType;
    private Context mContext;
    private Handler sHandler;
    private Queue uQeue;
    private int listenHint = 20000;         //Hint to system for listener frequency in microseconds (20 ms)
    private int bin = 50000000;             //bin length in nanoseconds (50 ms)
    private long halfTick = bin/2;
    private long tick = 0;
    private long tock;
    private int i = 1;
    private float acc[];

    SensorThread(int sensorType, Context mContext, Handler sHandler) {
        this.sensorType = sensorType;
        this.mContext = mContext;
        this.sHandler = sHandler;
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
                    tock = tick + bin;                                          //initialise bin end time
                    acc = new float[event.values.length];
                    for (int j = 0; j < event.values.length; j++){              //Initialise accumulator registers
                        acc[j] = event.values[j];
                    }
                } else if (event.timestamp < tock) {                            // event within bin
                    for (int j = 0; j < event.values.length; j++){              // Add next value to registers, increment counter
                        acc[j] += event.values[j];
                    }
                    i += 1;
                } else if (event.timestamp > tock) {
                    publishEpoch(formatter());                                  //Capture average values + timestamp (for middle of bin) and send to SensorActivity write handler
                    /*:TODO -> Aggregate XML
                      :TODO -> send UDP
                    */
                    tick = tock;                                                //Reset bin start time
                    tock = tick + bin;

                    for (int j = 0; j < event.values.length; j++){               //Reset accumulator registers and counter
                        acc[j] = event.values[j];
                    }
                    i = 1;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // do nothing
            }
        };

        mSensorManager.registerListener(mListener, sensor, listenHint ,handler);                    // listenHint set above. default = 50ms
    }

    private String[] formatter(){
        long ts = (currentTimeMillis() + (tock - elapsedRealtimeNanos ()+ halfTick) / 1000000L);   //Convert ns to ms

        String[] avData = new String[acc.length+1];
        for (int j = 0; j < avData.length-1; j++){
            avData[j] = String.format ("%.3f", acc[j] / i);
        }
        avData[3] = String.format ("%d", ts);

        return avData;
    }

    private void publishEpoch(String[] sData) {
        sHandler.obtainMessage(sensorType,sData).sendToTarget();
        //TODO Place each package on uQueue
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
