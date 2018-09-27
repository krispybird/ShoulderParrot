package com.example.peachcobbler.roboparrot;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.peachcobbler.roboparrot.location.ParrotLocationListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final Map<String, Integer> PERMISSIONS =
            Collections.unmodifiableMap(new HashMap<String, Integer>() {
                {
            /* --- MASTER PERMISSIONS LIST ---
               Just add new permissions here as needed. Simply provide permission name from
               Manifest.permission along with some integer that represents its response code. Also
               remember to add permission to manifest.
             */
                    put(Manifest.permission.ACCESS_FINE_LOCATION, 100);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllPermissions();

        setContentView(R.layout.activity_main);
    }

    public void beginFunctioning(View v) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION UPDATE: ", "Initializing location listening");
            LocationManager locationManager =
                    (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new ParrotLocationListener();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0, locationListener);
        }
        else {
            buildAlertDialog("Permission Error",
                    "You must grant us full control of your device to use this app.");
        }
    }

    public void requestAllPermissions() {
        for (Map.Entry<String, Integer> permission : PERMISSIONS.entrySet()) {
            if (ContextCompat.checkSelfPermission(this, permission.getKey())
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permission.getKey())) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{permission.getKey()},
                            permission.getValue());
                }
            }
            else {
                Log.d("PERMISSION: ",
                        String.format("Already have permission %s", permission.getKey()));
            }
        }
    }

    private void buildAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,
                android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            })
            .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        for (Map.Entry<String, Integer> permission : PERMISSIONS.entrySet()) {
            if (requestCode == permission.getValue()) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Log.d("PERMISSION: ", String.format("%s granted", permission.getKey()));
                else
                    Log.d("PERMISSION: ", String.format("%s denied", permission.getKey()));
            }
        }
    }
}
