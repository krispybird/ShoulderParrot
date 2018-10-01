package com.example.peachcobbler.roboparrot.communication;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class Communicator extends HandlerThread {
    Handler handler;

    public Communicator(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {


            }
        };
    }
}
