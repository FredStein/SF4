package com.fred.sn4;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;

public class SensorService extends Service{
    //tag for logging
    private static final String TAG = SensorService.class.getSimpleName();
    //flag for logging
    private boolean mLogging = false;
    //keep track of the previous value
    private static float previousValue;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AccelerometerThread a = new AccelerometerThread(TYPE_ACCELEROMETER, this);
        new Thread(a).start();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // ignore this since not linked to an activity
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();      //May be double acting here - check OnDestroy
        stopSelf();
    }
}
