package com.example.peachcobbler.roboparrot.sound.input;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peachcobbler.roboparrot.R;
import com.example.peachcobbler.roboparrot.parsing.Parser;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;

import static android.widget.Toast.makeText;

public class ParrotSpeechRecognizer extends HandlerThread implements RecognitionListener {
    Handler bin;
    AppCompatActivity main;
    Parser parser;

    public static final int MAKE_TOAST = 1001;
    public static final int CHANGE_TEXT = 1002;

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    private static final String MENU_SEARCH = "menu";

    /* Keyword we are looking for to activate menu */
    private static final String KEYPHRASE = "oh polly";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    private Message constructMessage(int type, int field, String contents) {
        Log.d("CONSTRUCT MESSAGE: ", contents);
        Message msg = new Message();
        msg.what = type;
        switch (type) {
            case MAKE_TOAST:
                msg.obj = contents;
                break;
            case CHANGE_TEXT:
                msg.obj = new TextFieldChange(field, contents);
                break;
            default:
                break;
        }
        return msg;
    }

    public ParrotSpeechRecognizer(String name, AppCompatActivity m,  Handler b) {
        super(name);
        main = m; bin = b;
        parser = new Parser(m);

        captions = new HashMap<>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);

        bin.sendMessage(constructMessage(CHANGE_TEXT, R.id.caption_text, "Loading recognizer..."));
    }

    @Override
    protected void onLooperPrepared() {
        Handler handler = new Handler(getLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Assets assets = new Assets(main);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                switchSearch(KWS_SEARCH);
            }
        });
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;
        Log.d("PARTIAL RESULT: ", hypothesis.getHypstr());

        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE))
            switchSearch(MENU_SEARCH);
        else
            bin.sendMessage(constructMessage(CHANGE_TEXT, R.id.result_text, text));
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        bin.sendMessage(constructMessage(CHANGE_TEXT, R.id.result_text, ""));
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            bin.sendMessage(constructMessage(MAKE_TOAST, 0, text));
            if (!text.equals(KEYPHRASE))
                parser.process(text);
        }
    }

    @Override
    public void onError(Exception e) {
        bin.sendMessage(constructMessage(CHANGE_TEXT, R.id.caption_text, e.getMessage()));
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

        String caption = main.getResources().getString(captions.get(searchName));
        bin.sendMessage(constructMessage(CHANGE_TEXT, R.id.caption_text, caption));
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them

        recognizer = SpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                .setRawLogDir(assetsDir) // To disable logging of raw audio comment out this call (takes a lot of space on the device)

                .getRecognizer();
        recognizer.addListener(this);

        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */

        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);

        // Create grammar-based search for selection between demos
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        bin.sendMessage(constructMessage(CHANGE_TEXT, R.id.caption_text, "Recognizer loaded."));
    }

    public void close() {
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    public class TextFieldChange {
        private int fieldId;
        private String contents;

        TextFieldChange(int fid, String c) {
            fieldId = fid; contents = c;
        }

        public int getFieldId() {
            return fieldId;
        }

        public String getContents() {
            return contents;
        }
    }
}
