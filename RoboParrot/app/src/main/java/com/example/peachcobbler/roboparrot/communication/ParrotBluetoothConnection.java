package com.example.peachcobbler.roboparrot.communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class ParrotBluetoothConnection extends ParrotConnection {
    static BluetoothSocket mmSocket;

    private final String DEVICE_MAC_ADDRESS = "00:1B:10:41:05:D7"; //TODO this
    private final int REQUEST_ENABLE_BT = 901;
    private BluetoothAdapter mBluetoothAdapter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("ACTION: ", action);
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                try {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int pin=intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, 0);
                    //the pin in case you need to accept for an specific pin
                    Log.d("PIN", " " + intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY,0));
                    //maybe you look for a name or address
                    Log.d("Bonded", device.getName());
                    byte[] pinBytes;
                    pinBytes = (""+pin).getBytes("UTF-8");
                    device.setPin(pinBytes);
                    //setPairing confirmation if neeeded
                    //device.setPairingConfirmation(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("BLUETOOTH SCAN: ", String.format("Name: %s MAC: %s", device.getName(), device.getAddress()));
                if (device.getAddress().equals(DEVICE_MAC_ADDRESS)) {
                    mBluetoothAdapter.cancelDiscovery();
                    handler.post(new ParrotBluetoothConnectionRequestor(device));
                }
            }
        }
    };

    ParrotBluetoothConnection(AppCompatActivity m, Handler h) throws IOException {
        super(m, h);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            throw new IOException("Bluetooth not supported.");
        }

        setup();

        Set<BluetoothDevice> paired = mBluetoothAdapter.getBondedDevices();
        Log.d("BLUETOOTH: ", String.format("Paired length: %d", paired.size()));
        if (paired.size() > 0) {
            for (BluetoothDevice device : paired) {
                if (device.getAddress().equals(DEVICE_MAC_ADDRESS)) {
                    Log.d("BLUETOOTH: ", "Device already paired");
                    mBluetoothAdapter.cancelDiscovery();
                    handler.post(new ParrotBluetoothConnectionRequestor(device));
                }
            }
        }
        else {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            main.registerReceiver(mReceiver, filter);
            filter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
            main.registerReceiver(mReceiver, filter);
            mBluetoothAdapter.startDiscovery();
        }
    }

    void setup() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            main.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    void send(ParrotMessage msg) {
        //InputStream inp;
        OutputStream out;
        Log.d("BLUETOOTH: ", "Sending message: " + msg.toString());
        try {
            //inp = mmSocket.getInputStream();
            out = mmSocket.getOutputStream();
            out.write(msg.format());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void close() {
        main.unregisterReceiver(mReceiver);
        try {
            mmSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void cleanup() {
        try {
            mmSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        try {
            main.unregisterReceiver(mReceiver);
        }
        catch (IllegalArgumentException e) {
            Log.d("RECEIVER: ", "No receiver registered");
        }
    }

    public void resume() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        main.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        main.registerReceiver(mReceiver, filter);
    }
}
