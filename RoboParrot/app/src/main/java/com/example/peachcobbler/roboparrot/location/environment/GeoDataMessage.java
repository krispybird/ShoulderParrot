package com.example.peachcobbler.roboparrot.location.environment;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class GeoDataMessage {
    private Location location;
    private int radius;
    private int limit;

    GeoDataMessage(Location loc, int rad, int lim) {
        location = loc; radius = rad; limit = lim;
    }

    String send(WikiRequest wiki) {
        Map<String, String> getParams = new HashMap<String, String>() {
            {
                put("action", "query");
                put("format", "json");
                put("list", "geosearch");
                put("gscoord", String.format("%f|%f", location.getLatitude(), location.getLongitude()));
                put("gsradius", Integer.toString(radius));
                put("gslimit", Integer.toString(limit));
            }
        };
        String response = "";
        String data = wiki.makeRequest(getParams);
        try {
            JSONObject page = new JSONObject(data).getJSONObject("query");
            JSONArray pts = page.getJSONArray("geosearch");
            if (pts.length() > 0) {
                int id = pts.getJSONObject(0).getInt("pageid");
                Map<String, String> idParams = new HashMap<String, String>() {
                    {
                        put("action", "query");
                        put("format", "json");
                        put("pageids", String.valueOf(id));
                        put("prop", "extracts");
                        put("exintro", "");
                        put("explaintext", "");
                        put("redirects", "1");
                    }
                };
                response = new JSONObject(wiki.makeRequest(idParams))
                        .getJSONObject("query")
                        .getJSONObject("pages")
                        .getJSONObject(String.valueOf(id))
                        .getString("extract");

            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }
}
