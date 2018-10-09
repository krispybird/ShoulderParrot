package com.example.peachcobbler.roboparrot.parsing;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class PhraseBook implements TextToSpeech.OnInitListener {
    public static final int DIRECTION = 65;
    public static final int MANIPULATION = 67;
    public static final int CONVERSATION = 71;

    TextToSpeech mTts;

    private Random random;
    private final String READY = "What do you want?";

    private static List<String> CONVERSE = Collections.unmodifiableList(new ArrayList<String>() {
        {
            add("I don't want to talk right now.");
            add("Sorry, I don't like you.");
            add("Excuse me, could you please stop talking.");
            add("Stop.");
            add("No, go away.");
        }
    });

    PhraseBook(AppCompatActivity main) {
        mTts = new TextToSpeech(main, this);
        random = new Random(System.currentTimeMillis());
    }

    void respond(int type, String query) {
        switch (type) {
            case DIRECTION:
                break;
            case MANIPULATION:
                break;
            case CONVERSATION:
                int index = random.nextInt(CONVERSE.size());
                String response = CONVERSE.get(index);
                mTts.speak(response, TextToSpeech.QUEUE_ADD, null, String.valueOf(Math.random()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onInit(int i) {
        mTts.setLanguage(Locale.CANADA);
        mTts.speak(READY, TextToSpeech.QUEUE_ADD, null, String.valueOf(Math.random()));
    }
}
