package com.example.peachcobbler.roboparrot.location.environment;

import android.location.Location;

import org.wikipedia.Wiki;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeoDataMessage {
    private Location location;
    private int radius;
    private int limit;

    public GeoDataMessage(Location loc, int rad, int lim) {
        location = loc; radius = rad; limit = lim;
    }

    public String send(Wiki wiki, String caller) throws IOException {
        Map<String, String> getParams = new HashMap<String, String>() {
            {
                put("action", "query");
                put("list", "geosearch");
                put("gscoord", String.format("%f|%f", location.getLatitude(), location.getLongitude()));
                put("gsradius", Integer.toString(radius));
                put("gslimit", Integer.toString(limit));
            }
        };
        return wiki.makeApiCall(getParams, null, caller);
    }
}
