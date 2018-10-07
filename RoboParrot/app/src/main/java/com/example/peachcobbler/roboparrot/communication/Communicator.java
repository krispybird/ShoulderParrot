package com.example.peachcobbler.roboparrot.communication;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.peachcobbler.roboparrot.location.environment.POIFinder;

import java.io.IOException;

public class Communicator extends HandlerThread {
    private Handler handler;
    private ParrotConnection connection;
    private POIFinder poif;
    private AppCompatActivity main;

    public Communicator(String name, AppCompatActivity m) {
        super(name);
        main = m;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };

        try {
            connection = new ParrotBluetoothConnection(main, handler);
            poif = new POIFinder("POIFinder");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
