package com.example.peachcobbler.roboparrot.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ParrotBluetoothConnectionRequestor implements Runnable {
    private final String NAME = "RoboParrot";
    //private final String MY_UUID = "ee6dfe94-3093-4b35-8f6f-3846f9b4ddf3";
    private final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    //private final String MY_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private BluetoothDevice device;

    ParrotBluetoothConnectionRequestor(BluetoothDevice d) {
        BluetoothSocket tmp = null;
        device = d;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch (IOException e) {
            Log.d("BLUETOOTH CONNECTION: ", "Socket's create() method failed");
        }
        ParrotBluetoothConnection.mmSocket = tmp;
    }

    private BluetoothSocket createBluetoothSocket()
            throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, UUID.fromString(MY_UUID));
            } catch (Exception e) {
                Log.d("BLUETOOTH: ", "Could not create Insecure RFComm Connection",e);
            }
        }
        return device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
    }

    @Override
    public void run() {
        Log.d("BLUETOOTH: ", "Running connection request...");
        try {
            Thread.sleep(1000);
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            Object[] params = new Object[] {Integer.valueOf(1)};
            ParrotBluetoothConnection.mmSocket = (BluetoothSocket)m.invoke(device, params);
            ParrotBluetoothConnection.mmSocket.connect();
            Log.d("BLUETOOTH: ", "Connection succeeded, opening channel!");
        }
        catch (InterruptedException | IllegalAccessException | InvocationTargetException |
                NoSuchMethodException | IOException backupException) {
            Log.d("BLUETOOTH: ", "Could not connect");
            backupException.printStackTrace();
            try {
                ParrotBluetoothConnection.mmSocket.close();
            } catch (IOException closeException) {
                Log.e("BLUETOOTH CONNECTION: ", "Could not close the client socket");
            }
        }
    }
}
