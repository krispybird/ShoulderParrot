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
    public static final int KEY = 61;
    public static final int DIRECTION_START = 65;
    public static final int DIRECTION = 66;
    public static final int MANIPULATION = 67;
    public static final int CONVERSATION = 71;
    public static final int FUN_FACT = 77;
    public static final int NEXT = 79;

    public static TextToSpeech mTts;

    public static final String KEYPHRASE = "hey polly";

    private static List<String> READY = Collections.unmodifiableList(new ArrayList<String>() {
        {
            add("Hello, I am Polly.");
            add("What's up? I'm Polly.");
            add("Howdy y'all, I'm Polly.");
        }
    });
    private static List<String> LISTEN = Collections.unmodifiableList(new ArrayList<String>() {
        {
            add("What is it?");
            add("Yes?");
            add("What?");
            add("What do you want?");
            add("What now?");
        }
    });
    private static  List<String> DIRECTION_QUERY = Collections.unmodifiableList(new ArrayList<String>() {
        {
            add("Where to?");
            add("Where would you like to go?");
            add("Ok, where?");
            add("Where are we going?");
        }
    });
    private static  List<String> DIRECTION_GO = Collections.unmodifiableList(new ArrayList<String>() {
        {
            add("Ok, let's go!");
            add("Fine, here we go.");
            add("Let's get going.");
        }
    });
    private static List<String> CONVERSE = Collections.unmodifiableList(new ArrayList<String>() {
        {
            add("I don't want to talk right now.");
            add("No, go away.");
            add("Fine.");
        }
    });
    private static List<String> FACT_INTRO = Collections.unmodifiableList(new ArrayList<String>() {
        {
            add("Here's a good one...");
            add("Guess what?");
            add("Here's what's hip and happenin' near you.");
            add("You might want to check this place out if you have time...");
            add("Ooo! Let's go here!");
        }
    });

    PhraseBook(AppCompatActivity main) {
        mTts = new TextToSpeech(main, this);
    }

    public static void respond(int type, String query) {
        Random random = new Random(System.currentTimeMillis());
        int index;
        String response;
        switch (type) {
            case KEY:
                index = random.nextInt(LISTEN.size());
                response = LISTEN.get(index);
                mTts.speak(response, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(Math.random()));
                break;
            case DIRECTION_START:
                index = random.nextInt(DIRECTION_QUERY.size());
                response = DIRECTION_QUERY.get(index);
                mTts.speak(response, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(Math.random()));
                break;
            case DIRECTION:
                index = random.nextInt(DIRECTION_GO.size());
                response = DIRECTION_GO.get(index);
                mTts.speak(response, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(Math.random()));
            case MANIPULATION:
                break;
            case CONVERSATION:
                index = random.nextInt(CONVERSE.size());
                response = CONVERSE.get(index);
                mTts.speak(response, TextToSpeech.QUEUE_ADD, null, String.valueOf(Math.random()));
                break;
            case FUN_FACT:
                index = random.nextInt(FACT_INTRO.size());
                response = FACT_INTRO.get(index);
                mTts.speak(response + query, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(Math.random()));
                break;
            default:
                break;
        }
    }

    @Override
    public void onInit(int i) {
        Random random = new Random(System.currentTimeMillis());
        mTts.setLanguage(Locale.CANADA);
        int index = random.nextInt(READY.size());
        mTts.speak(READY.get(index), TextToSpeech.QUEUE_ADD, null, String.valueOf(Math.random()));
    }
}
