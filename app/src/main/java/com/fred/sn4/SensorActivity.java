package com.fred.sn4;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    // USB Element Initialisation Start
    // Notifications from UsbService will be received here.
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    private TextView aX;
    private TextView aY;
    private TextView aZ;
    private TextView aT;
    private TextView usbSensVal;
    private TextView usbSensTime;
    private usbHandler mHandler;
    private sensorHandler sHandler;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    // USb Element Initialisation end

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_activity);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        sHandler = new sensorHandler(this);              //Instatiate sensor message handler
        mHandler = new usbHandler(this);                 //Instatiate usb message handler

        aX = (TextView) findViewById(R.id.Ax);
        aY = (TextView) findViewById(R.id.Ay);
        aZ = (TextView) findViewById(R.id.Az);
        aT = (TextView) findViewById(R.id.At);

        usbSensVal = (TextView) findViewById(R.id.d);  //Initialise usb sensor value & timestamp display fields
        usbSensTime = (TextView) findViewById(R.id.Dt);

        int i = 0;
        for (int item : sList){
        // TODO Amend so Sensor only displayed if present (Set Header and values to visible?)
            if (mSensorManager.getDefaultSensor(item) != null) {
                // We have this sensor
                mSensorL.add(i,mSensorManager.getDefaultSensor(item));
                sPrefix.put(item,prefixList[i]);
                mSensorManager.registerListener(this, mSensorL.get(i),
                        SensorManager.SENSOR_DELAY_UI);                 //(67)~62.5ms _NORMAl(200) ~190 ms _FASTEST(0) ~5ms (Or use timing in microseconds)
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
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        unregisterReceiver(mUsbReceiver);           //USB Reciever disconnected
        unbindService(usbConnection);               //USB Service disconnected
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void setFilters() {                     //USB Filter configuration
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }
                                                    //USB Service start
    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private static class usbHandler extends Handler {
        private final WeakReference<SensorActivity> mActivity;

        public usbHandler(SensorActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    data = data.trim();
                    if (data.endsWith("m")){
                        data = data.substring(0,data.length()-1);
                        long ts = currentTimeMillis();
                        mActivity.get().usbSensVal.setText(data);
                        mActivity.get().usbSensTime.setText(String.format ("%d", ts%100000));
                    }
                    break;
            }
        }
    }

    private static class sensorHandler extends Handler {
        private final WeakReference<SensorActivity> mActivity;

        public sensorHandler(SensorActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            //:TODO msg to contain formatted x,y,z,(r),t strings
            Bundle msgBundle = msg.getData();
            switch (msg.what) {
                case TYPE_ACCELEROMETER:{
                    String sx = msgBundle.getString("x");
                    String sy = msgBundle.getString("y");
                    String sz = msgBundle.getString("z");
                    String st = msgBundle.getString("t");

                    mActivity.get().aX.setText(sx);
                    mActivity.get().aY.setText(sy);
                    mActivity.get().aZ.setText(sz);
                    mActivity.get().aT.setText(st);
                }
                    break;
            }
        }
    }

}
