package com.example.peachcobbler.roboparrot.location.direction;

import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.example.peachcobbler.roboparrot.location.ParrotLocationManager;
import com.mapbox.directions.DirectionsCriteria;
import com.mapbox.directions.MapboxDirections;
import com.mapbox.directions.service.models.DirectionsResponse;
import com.mapbox.directions.service.models.RouteStep;
import com.mapbox.directions.service.models.Waypoint;
import com.mapbox.geocoder.MapboxGeocoder;
import com.mapbox.geocoder.service.models.GeocoderResponse;

import java.io.IOException;
import java.util.List;

import retrofit.Response;

public class DirectionManager extends HandlerThread {
    private final String TOKEN = "pk.eyJ1IjoicGVhY2hjb2JibGVyIiwiYSI6ImNqbWxrZmM1YzA5Ymgzd255Ymo5dTl2YmcifQ.tsHcUIG0WCYLCMqBvpP5qw";

    public static final int NEW = 151;
    public static final int UPDATE = 152;

    private Handler handler;
    private DirectionListener listener;

    private String[] currList = new String[0];
    private int currInd = 0;

    public DirectionManager(String name) {
        super(name);
    }

    public void startNavigation(String destination) throws Exception {
        Message msg = new Message();
        msg.what = NEW;
        msg.obj = destination;
        handler.sendMessage(msg);
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case NEW:
                        String destination = (String) msg.obj;
                        currList = directionList(destination);
                        currInd = 0;
                        Message newMsg = new Message();
                        newMsg.what = UPDATE;
                        handler.sendMessage(newMsg);
                        break;
                    case UPDATE:
                        if (currInd < currList.length) {
                            listener.onDirection(currList[currInd]);
                            currInd++;
                        }
                        if (currInd >= currList.length) {
                            currList = new String[0];
                            currInd = 0;
                            listener.onDirection("");
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private String[] directionList(String destination) {
        String[] ret = new String[0];
        Waypoint wayCurrent;
        if (ParrotLocationManager.current != null) {
            wayCurrent = new Waypoint(ParrotLocationManager.current.getLongitude(),
                    ParrotLocationManager.current.getLatitude());
        }
        else {
            wayCurrent = new Waypoint(ParrotLocationManager.defaultLocation.getLongitude(),
                    ParrotLocationManager.defaultLocation.getLatitude());
        }

        Waypoint wayDestination = getWaypoint(wayCurrent, destination);

        MapboxDirections client = (new MapboxDirections.Builder())
                .setAccessToken(TOKEN)
                .setOrigin(wayCurrent)
                .setDestination(wayDestination)
                .setProfile(DirectionsCriteria.PROFILE_WALKING)
                .setSteps(true)
                .build();

        Log.d("LOCATION CURRENT: ", String.format("%f %f", wayCurrent.getLatitude(), wayCurrent.getLongitude()));
        Log.d("LOCATION DEST: ", String.format("%f %f", wayDestination.getLatitude(), wayDestination.getLongitude()));

        try {
            DirectionsResponse body = client.execute().body();
            Log.d("IS NULL? ", String.valueOf(body == null));
            if (body != null) {
                List<RouteStep> instructions = body.getRoutes().get(0).getSteps();
                ret = new String[instructions.size()];
                Log.d("NUMINSTRUCTIONS: ", String.valueOf(instructions.size()));
                for (int i = 0; i < instructions.size(); i++) {
                    ret[i] = instructions.get(i).getManeuver().getInstruction();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private Waypoint getWaypoint(Waypoint near, String destination) {
        Waypoint ret = null;
        MapboxGeocoder geoClient = new MapboxGeocoder.Builder()
                .setAccessToken(TOKEN)
                .setProximity(near.getLongitude(), near.getLatitude())
                .setLocation(destination)
                .build();
        try {
            GeocoderResponse body = geoClient.execute().body();
            if (body != null) {
                List<Double> point = body.getFeatures().get(0).getCenter();
                ret = new Waypoint(point.get(0), point.get(1));
                Log.d("DIRECTION: ", "Got waypoint for location " + ret.toString());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void reset() {
        currList = new String[0];
        currInd = 0;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setDirectionListener(DirectionListener l) {
        listener = l;
    }

    public interface DirectionListener {
        void onDirection(String direction);
    }
}
