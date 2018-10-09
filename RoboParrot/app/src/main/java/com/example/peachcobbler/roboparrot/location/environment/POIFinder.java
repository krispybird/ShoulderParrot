package com.example.peachcobbler.roboparrot.location.environment;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import org.wikipedia.Wiki;

import java.io.IOException;

import javax.security.auth.login.LoginException;

public class POIFinder extends HandlerThread {
    private final String USERNAME = "RobotParrotBot";
    private final String SUPER_SECURE_PASSWORD = "YarrMateyShiverMeTimbers";
    private final String ROBOT_NAME = "RoboParrotBot@RoboParrotBotAgent";
    private final String ROBOT_PASSWORD = "24106njna0mv087eqf5i3f2do9q5n3rn";
    private final String USER_AGENT = "RoboParrotUIST/0.0";

    private final long INTERVAL = 5000;
    private final int RADIUS = 1000;
    private final int LIMIT = 10;

    private Handler handler;
    private Wiki wiki;
    private POITimer timer;

    public POIFinder(String name) {
        super(name);
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof Location) {
                    try {
                        //TODO Set a default location
                        String response = (new GeoDataMessage((Location) msg.obj, RADIUS, LIMIT))
                                .send(wiki, USER_AGENT);
                        //TODO Send to parrot speech handler
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Log.d("WIKI UPDATE: ", "Posting login request to Wikipedia");
        handler.post(new Runnable() {
            @Override
            public void run() {
                wiki = Wiki.createInstance("en.wikipedia.org");
                wiki.setThrottle(5000);
                try {
                    wiki.login(ROBOT_NAME, ROBOT_PASSWORD);
                }
                catch (LoginException | IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                wiki.setUserAgent(USER_AGENT);
                wiki.setAssertionMode(Wiki.ASSERT_USER);
            }
        });

        timer = new POITimer(INTERVAL, handler);
        timer.start();
    }
}
