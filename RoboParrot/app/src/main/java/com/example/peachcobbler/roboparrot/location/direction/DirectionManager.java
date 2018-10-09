package com.example.peachcobbler.roboparrot.location.direction;

import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.example.peachcobbler.roboparrot.location.ParrotLocationManager;
import com.mapbox.directions.DirectionsCriteria;
import com.mapbox.directions.MapboxDirections;
import com.mapbox.directions.service.models.Waypoint;

import java.io.IOException;

import retrofit.Response;

public class DirectionManager extends HandlerThread {
    private final String TOKEN = "pk.eyJ1IjoicGVhY2hjb2JibGVyIiwiYSI6ImNqbWxrZmM1YzA5Ymgzd255Ymo5dTl2YmcifQ.tsHcUIG0WCYLCMqBvpP5qw";

    private Handler handler;
    private DirectionListener listener;

    public DirectionManager(String name) {
        super(name);
    }

    public void startNavigation(Location destination) throws Exception {
        Waypoint wayCurrent = new Waypoint(ParrotLocationManager.current.getLongitude(),
                                          ParrotLocationManager.current.getLatitude());
        Waypoint wayDestination = new Waypoint(destination.getLongitude(),
                                                destination.getLatitude());

        MapboxDirections client = (new MapboxDirections.Builder())
                .setAccessToken(TOKEN)
                .setOrigin(wayCurrent)
                .setDestination(wayDestination)
                .setProfile(DirectionsCriteria.PROFILE_WALKING)
                .setSteps(true)
                .build();

        if (listener == null)
            throw new Exception("No listener defined for DirectionManager.");

        Message msg = new Message();
        msg.obj = client;
        handler.sendMessage(msg);
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                MapboxDirections c = (MapboxDirections) msg.obj;
                Log.d("DIRECTION UPDATE: ", "Directions ready");
                //TODO process directions list and send info to listener as appropriate
            }
        };
    }

    public void setDirectionListener(DirectionListener l) {
        listener = l;
    }

    public interface DirectionListener {
        void onDirection(String direction);
    }
}