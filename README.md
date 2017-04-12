# SF4
This is a project to create an Android app that fuses sensor data from the device with data from a usb attached sensor and sends the resultant data packet (as XML) over udp via wifi to a target client for real time processing.

The project will evolve in a number of steps.

Obtain data from device sensors. Achieved for sensors of primary interest: Accelerometer Gyroscope Linear Acceleration Gravity Rotation Vector Sensor selection will eventually be user configurable however the above 5 are the sensors in which I am presently interested & so are hard coded at this time. TODO: Add timestamp

Acquire (serial) data from usb attached external sensor. In Progress: Device based on FDTI UART Serial port settings will eventually be user configurable. Until this element is working reliably these will be hardcoded to suit the test device configuration. 19200/8/1/None

Write device and external data to XML string TODO

Transmit via UDP IP to be user configurable Port to be user configurable TODO
