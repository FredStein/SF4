package com.fred.sn4;

/**
 * Created by Fred Stein on 14/04/2017.
 */

class MessageXML {
    public String NodeId;
    public double[] Accelerometer = new double[3];
    public double[] Gyroscope = new double[3];
    public double[] Gravity = new double[3];
    public double[] LinearAcceleration = new double[3];
    public double[] RotationVector = new double[4];
    public long TimeStamp;
}
