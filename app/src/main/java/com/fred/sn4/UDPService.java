package com.fred.sn4;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

public class UDPService extends Service {
    //tag for logging
    private static final String TAG = SensorActivity.class.getSimpleName();
    //flag for logging
    private static boolean mLogging = true;

    public LinkedBlockingQueue q1;
    public UDPService() {
    //constructor
        MessageXML xml = new MessageXML();
    }

    @Override
    public void onCreate(){
        LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();
        qReader readQ = new qReader(queue);
        this.q1 = queue;
        //starting consumer to consume messages from queue
        new Thread(readQ).start();
        if (mLogging){
            String logString = "Debug :" +"Read queue started";
            Log.v(TAG, logString);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public class qReader implements Runnable{
        private LinkedBlockingQueue<Message> queue;

        public qReader(LinkedBlockingQueue<Message> q){
            this.queue=q;
        }

        @Override
        public void run() {
            Message sData;
            try{                //Sequentially reads sensor message queues & places on UDP queue
                                //consuming messages until exit message is received
                while((sData = queue.take()).what != 0){
                    // Place msg into XML? Will need to be aware of queue identity
                    xmlLoader(sData.getData());
                }
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class qWriter implements Runnable {

        private LinkedBlockingQueue<Message> queue;

        public qWriter(LinkedBlockingQueue<Message> q){
            this.queue=q;
        }
                                            //Something funky here
        @Override
        public void run() {
           //Write to queue
           if (mLogging){
               String logString = "Debug :" + "put ok";
               Log.v(TAG, logString);
           }
        }
    }

    private void xmlLoader (Bundle sData){
        if (mLogging){
            String logString = "Debug :" + sData.getFloatArray("data");
            Log.v(TAG, logString);
        }

    }
}
