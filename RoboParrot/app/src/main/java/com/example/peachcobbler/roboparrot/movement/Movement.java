package com.example.peachcobbler.roboparrot.movement;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.example.peachcobbler.roboparrot.communication.Communicator;

import java.nio.ByteBuffer;

public class Movement extends HandlerThread {
    public static final int CALIBRATE = 9999;
    public static final int GRAB = 10000;

    private static final int WAIT_BUFFER = 2000;

    private static final String LOW = "p0\n";
    private static final String MLOW = "p1\n";
    private static final String MED = "p2\n";
    private static final String MHI = "p3\n";
    private static final String HI = "p4\n";
    private static final String OPEN = "go\n";
    private static final String CLOSE = "gc\n";
    private static final String CALIB = "xxx\n";

    private static Handler handler;

    public Movement(String name) {
        super(name);
    }

    @Override
    public void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Communicator c = (Communicator) msg.obj;
                try {
                    switch (msg.what) {
                        case CALIBRATE:
                            c.sendMessage(CALIB);
                            break;
                        case GRAB:
                            c.sendMessage(HI);
                            Thread.sleep(WAIT_BUFFER);
                            c.sendMessage(OPEN);
                            Thread.sleep(WAIT_BUFFER);
                            c.sendMessage(rotate((short) 10));
                            Thread.sleep(WAIT_BUFFER + 600);
                            c.sendMessage(LOW);
                            Thread.sleep(WAIT_BUFFER);
                            c.sendMessage(CLOSE);
                            Thread.sleep(WAIT_BUFFER);
                            Thread.sleep(WAIT_BUFFER);
                            Thread.sleep(WAIT_BUFFER);
                            c.sendMessage(HI);
                            Thread.sleep(WAIT_BUFFER);
                            c.sendMessage(rotate((short) 1023));
                            Thread.sleep(WAIT_BUFFER);
                            c.sendMessage(MED);
                            Thread.sleep(WAIT_BUFFER);
                            c.sendMessage(OPEN);
                            break;
                        default:
                            break;
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static void execute(int command, Communicator c) {
        Message msg = new Message();
        msg.what = command;
        msg.obj = c;
        handler.sendMessage(msg);
    }

    private static String rotate(short deg) {
        return "mb" + String.valueOf(deg) + "\n";
    }
}
