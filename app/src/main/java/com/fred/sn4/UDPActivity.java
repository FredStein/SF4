package com.fred.sn4;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.ref.WeakReference;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;

public class UDPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp);
    }

    private static class udpHandler extends Handler {
        private final WeakReference<UDPActivity> mActivity;
        public udpHandler(UDPActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            //:Param msg .obj element is array of formatted strings
            switch (msg.what) {
                case TYPE_ACCELEROMETER:{
                    String[] sData = (String[]) msg.obj;
                    //TODO Insert code to read uQueue for each sensor here
                }break;
            }
        }
    }

}
