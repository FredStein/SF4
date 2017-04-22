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
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.concurrent.LinkedBlockingQueue;

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
    private int listenHint = 20000;         //Hint to system for listener frequency in microseconds (20 ms)
    private int bin = 50000000;             //bin length in nanoseconds (50 ms)
    private long halfTick = bin/2;
    private long tick = 0;
    private long tock;
    private int i = 1;
    private float acc[];
    private UDPService udpService;
    private LinkedBlockingQueue<Message> wQ;
    private Thread wThread;

    SensorThread(int sensorType, Context mContext, Handler sHandler,UDPService udp) {
        this.sensorType = sensorType;
        this.mContext = mContext;
        this.sHandler = sHandler;
        this.udpService = udp;
    }

    @Override
    public void run() {
        SensorManager mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(sensorType);
        HandlerThread writeToUIThread = new HandlerThread("UIWriter" + String.format ("%d", sensorType));
        writeToUIThread.start();
        Handler writeToUI = new Handler(writeToUIThread.getLooper());
        this.wQ = udpService.q1;
        UDPService.qWriter writeQ;
        writeQ = udpService.new udpService.qWriter(wQ);
        //starting producer to place messages on queue
        this.wThread = new Thread(writeQ);
        wThread.start();


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
        mSensorManager.registerListener(mListener, sensor, listenHint ,writeToUI);                    // listenHint set above. default = 50ms
    }

    private String[] formatter(){
        long ts = (currentTimeMillis() + (tock - elapsedRealtimeNanos ()+ halfTick) / 1000000L);   //Convert ns to ms

        String[] avData = new String[acc.length+1];
        for (int j = 0; j < avData.length-1; j++){
            avData[j] = String.format ("%.3f", acc[j] / i);
        }
        avData[acc.length] = String.format ("%d", ts);
        //Create Message
        return avData;
    }

    private void publishEpoch(String[] sData) {
        sHandler.obtainMessage(sensorType,sData).sendToTarget();
        Bundle outArr = new Bundle();
        outArr.putFloatArray("data",acc);
        Message outPkt = new Message();
        outPkt.what = sensorType;
        outPkt.setData(outArr);

        if (sData[0] != "exit"){            //Modify Test - place 0 in outPkt.what to signal no more data from sensor (close thread)
            try {
                wQ.put(outPkt);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            //set a flag in here to kill the thread when no more to write.
        }
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
