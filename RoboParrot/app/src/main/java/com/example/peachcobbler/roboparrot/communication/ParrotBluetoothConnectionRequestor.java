package com.example.peachcobbler.roboparrot.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ParrotBluetoothConnectionRequestor implements Runnable {
    private final String NAME = "RoboParrot";
    private final String MY_UUID = "ee6dfe94-3093-4b35-8f6f-3846f9b4ddf3";

    ParrotBluetoothConnectionRequestor(BluetoothDevice device) {
        BluetoothSocket tmp = null;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
        } catch (IOException e) {
            Log.d("BLUETOOTH CONNECTION: ", "Socket's create() method failed");
        }
        ParrotBluetoothConnection.mmSocket = tmp;
    }

    @Override
    public void run() {
        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            ParrotBluetoothConnection.mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                ParrotBluetoothConnection.mmSocket.close();
            } catch (IOException closeException) {
                Log.e("BLUETOOTH CONNECTION: ", "Could not close the client socket");
            }
        }
    }
}
