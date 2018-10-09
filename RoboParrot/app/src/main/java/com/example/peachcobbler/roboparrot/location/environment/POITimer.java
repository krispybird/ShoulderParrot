package com.example.peachcobbler.roboparrot.location.environment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.peachcobbler.roboparrot.location.ParrotLocationManager;

public class POITimer extends Thread {
    private long interval;
    private long last;
    private Handler bin;

    POITimer(long inter, Handler b) {
        interval = inter; bin = b;
        last = System.currentTimeMillis();
    }

    @Override
    public void run() {
        while (true) {
            long curr = System.currentTimeMillis();
            while (curr - last < interval) {
                curr = System.currentTimeMillis();
            }
            Message msg = new Message();
            msg.obj = ParrotLocationManager.current;
            Log.d("TIME UPDATE: ", "Send new POI update");
            bin.sendMessage(msg);
            last = System.currentTimeMillis();
        }
    }
}
