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

import java.io.IOException;

public class ParrotBluetoothConnection extends ParrotConnection {
    static BluetoothSocket mmSocket;

    private final String DEVICE_ADDRESS = ""; //TODO this
    private final int REQUEST_ENABLE_BT = 901;
    private BluetoothAdapter mBluetoothAdapter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getAddress().equals(DEVICE_ADDRESS)) {
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

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        main.registerReceiver(mReceiver, filter);
    }

    void setup() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            main.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    void close() {
        main.unregisterReceiver(mReceiver);
    }
}
