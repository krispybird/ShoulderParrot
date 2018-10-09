package com.example.peachcobbler.roboparrot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peachcobbler.roboparrot.location.ParrotLocationManager;
import com.example.peachcobbler.roboparrot.parsing.Parser;
import com.example.peachcobbler.roboparrot.sound.input.ParrotSpeechRecognizer;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ParrotSpeechRecognizer psr;
    private Parser p;   //TODO temporary
    private ParrotLocationManager lm;
    private Handler handler;

    private final Map<String, Integer> PERMISSIONS =
            Collections.unmodifiableMap(new HashMap<String, Integer>() {
                {
            /* --- MASTER PERMISSIONS LIST ---
               Just add new permissions here as needed. Simply provide permission name from
               Manifest.permission along with some integer that represents its response code. Also
               remember to add permission to manifest.
             */
                    put(Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    put(Manifest.permission.BLUETOOTH, 105);
                    put(Manifest.permission.BLUETOOTH_ADMIN, 106);
                    put(Manifest.permission.WRITE_EXTERNAL_STORAGE, 107);
                    put(Manifest.permission.RECORD_AUDIO, 108);
                }
            });

    static class MainHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        MainHandler(MainActivity ma) {
            mActivity = new WeakReference<>(ma);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity main = mActivity.get();
            if (main != null) {
                main.handleMessage(msg);
            }
        }
    }

    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ParrotSpeechRecognizer.MAKE_TOAST:
                Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                break;
            case ParrotSpeechRecognizer.CHANGE_TEXT:
                ParrotSpeechRecognizer.TextFieldChange theMsg = (ParrotSpeechRecognizer.TextFieldChange) msg.obj;
                ((TextView) findViewById(theMsg.getFieldId())).setText(theMsg.getContents());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAllPermissions();

        handler = new MainHandler(this);

        setContentView(R.layout.activity_main);
    }

    public void beginFunctioning(View v) {
        //lm = new ParrotLocationManager(this);
        //p = new Parser(this);
        psr = new ParrotSpeechRecognizer("ParrotSpeechRecognizer", this, handler);
        psr.start();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (p != null)
            p.cleanup();
        if (psr != null)
            psr.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (p != null)
            p.pauseConnection();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (p != null)
            p.resumeConnection();
    }
}
