package com.example.peachcobbler.roboparrot.parsing;

import android.content.Context;
import android.location.Location;

import com.example.peachcobbler.roboparrot.location.ParrotLocationManager;
import com.example.peachcobbler.roboparrot.location.direction.DirectionManager;

public class Parser {
    private DirectionManager director;

    public Parser(Context main) {
        director = new DirectionManager("dmthread");
        director.start();
        director.setDirectionListener(new DirectionManager.DirectionListener() {
            @Override
            public void onDirection(String direction) {
                return;
            }
        });

        /*Location uni = new Location("");
        uni.setLatitude(51.079102);
        uni.setLongitude(-114.135778);

        try {
            director.startNavigation(uni);
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    static void fetchCommand(String indObject, String dirObject) {

    }

    static void takeCommand(String indObject, String dirObject) {

    }
}
