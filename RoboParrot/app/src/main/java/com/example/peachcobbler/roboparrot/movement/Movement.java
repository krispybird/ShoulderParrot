package com.example.peachcobbler.roboparrot.movement;

import com.example.peachcobbler.roboparrot.communication.Communicator;

import java.nio.ByteBuffer;

public class Movement {
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

    public static void execute(int command, Communicator c) throws InterruptedException {
        switch (command) {
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

    private static String rotate(short deg) {
        return "mb" + String.valueOf(deg) + "\n";
    }
}
