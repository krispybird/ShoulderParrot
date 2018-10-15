package com.example.peachcobbler.roboparrot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peachcobbler.roboparrot.location.ParrotLocationManager;
import com.example.peachcobbler.roboparrot.location.direction.DirectionManager;
import com.example.peachcobbler.roboparrot.movement.Movement;
import com.example.peachcobbler.roboparrot.parsing.Parser;
import com.example.peachcobbler.roboparrot.parsing.PhraseBook;
import com.example.peachcobbler.roboparrot.sound.input.ParrotSpeechRecognizer;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationEntryFragment.LocationEntryListener {
    private ParrotSpeechRecognizer psr;
    private Parser p;   //TODO temporary
    private ParrotLocationManager lm;
    private Handler handler;
    private boolean started = false;
    private boolean muted = false;

    private final int TTS_RESPONSE = 99;

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
                    put(Manifest.permission.ACCESS_NETWORK_STATE, 109);
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
        Log.d("MAIN HANDLER", "Handle message");
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

        Movement m = new Movement("move");
        m.start();
        handler = new MainHandler(this);

        setContentView(R.layout.activity_main);
    }

    public void beginFunctioning(View v) {
        lm = new ParrotLocationManager(this);
        //p = new Parser(this);
        psr = new ParrotSpeechRecognizer("ParrotSpeechRecognizer", this, handler);
        psr.start();
        ((ViewGroup) v.getParent()).removeView(v);
        started = true;
    }

    public void mute(View v) {
        if (notStarted())
            return;

        muted = !muted;
        if (muted) {
            psr.getRecognizer().stop();
            ((Button) findViewById(R.id.muteButton)).setText("Unmute");
        }
        else {
            psr.getRecognizer().startListening(ParrotSpeechRecognizer.KWS_SEARCH);
            ((Button) findViewById(R.id.muteButton)).setText("Mute");
        }
    }

    public void move(View v) {
        if (notStarted())
            return;

        Movement.execute(Movement.GRAB, psr.getParser().getCommunicator());
    }

    public void fact(View v) {
        if (notStarted())
            return;

        Message msg = new Message();
        if (ParrotLocationManager.current == null) {
            msg.obj = ParrotLocationManager.defaultLocation;
        }
        else {
            msg.obj = ParrotLocationManager.current;
        }
        psr.getParser().getCommunicator().getPOIFinder().getHandler().sendMessage(msg);
    }

    public void direct(View v) {
        if (notStarted())
            return;

        FragmentManager fm = getSupportFragmentManager();
        LocationEntryFragment editNameDialogFragment = LocationEntryFragment.newInstance();
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    public void next(View v) {
        Message msg = new Message();
        msg.what = DirectionManager.UPDATE;
        psr.getParser().getDirectionManager().getHandler().sendMessage(msg);
    }

    public void cancel(View v) {
        psr.getParser().getDirectionManager().reset();
        findViewById(R.id.nextButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.cancelButton).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        try {
            psr.getParser().getDirectionManager().startNavigation(inputText);
            findViewById(R.id.cancelButton).setVisibility(View.VISIBLE);
            findViewById(R.id.nextButton).setVisibility(View.VISIBLE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean notStarted() {
        if (!started) {
            Toast.makeText(this, "Please press start button first.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void requestAllPermissions() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, TTS_RESPONSE);

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
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        if (requestCode == TTS_RESPONSE) {
            if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
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
        if (psr != null)
            psr.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (psr != null)
            psr.pauseConnection();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (psr != null)
            psr.resumeConnection();
    }
}
