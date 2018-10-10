package com.example.peachcobbler.roboparrot.location.environment;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.peachcobbler.roboparrot.parsing.PhraseBook;

public class POIFinder extends HandlerThread {
    private final String USERNAME = "RobotParrotBot";
    private final String SUPER_SECURE_PASSWORD = "YarrMateyShiverMeTimbers";
    private final String ROBOT_NAME = "RoboParrotBot@RoboParrotBotAgent";
    private final String ROBOT_PASSWORD = "24106njna0mv087eqf5i3f2do9q5n3rn";
    private final String USER_AGENT = "RoboParrotUIST/0.0";

    private final long INTERVAL = 60000;
    private final int RADIUS = 1000;
    private final int LIMIT = 10;

    private Handler handler;
    private WikiRequest wiki;
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
                    //TODO Set a default location
                    String response = (new GeoDataMessage((Location) msg.obj, RADIUS, LIMIT))
                            .send(wiki);
                    Log.d("POI DESCRIPTION: ", response.toString());
                    PhraseBook.respond(PhraseBook.FUN_FACT, response);
                    //PhraseBook.mTts.speak(response, TextToSpeech.QUEUE_ADD, null, String.valueOf(Math.random()));
                }
            }
        };

        wiki = new WikiRequest();

        timer = new POITimer(INTERVAL, handler);
        timer.start();
    }
}
