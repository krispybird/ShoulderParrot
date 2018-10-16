package com.example.peachcobbler.roboparrot.parsing;

import android.content.Context;
import android.location.Location;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.peachcobbler.roboparrot.R;
import com.example.peachcobbler.roboparrot.communication.Communicator;
import com.example.peachcobbler.roboparrot.location.ParrotLocationManager;
import com.example.peachcobbler.roboparrot.location.direction.DirectionManager;
import com.example.peachcobbler.roboparrot.movement.Movement;

public class Parser {
    private DirectionManager director;
    private Communicator comm;
    private PhraseBook pb;

    public Parser(AppCompatActivity main) {
        director = new DirectionManager(main, "dmthread");
        director.start();
        director.setDirectionListener(new DirectionManager.DirectionListener() {
            @Override
            public void onDirection(String direction) {
                if (direction.length() > 0) {
                    PhraseBook.mTts.speak(direction, TextToSpeech.QUEUE_FLUSH, null, String.valueOf(Math.random()));
                }
                else {
                    ((Button) main.findViewById(R.id.cancelButton)).setVisibility(View.INVISIBLE);
                    ((Button) main.findViewById(R.id.nextButton)).setVisibility(View.INVISIBLE);
                }
            }
        });
        comm = new Communicator("Communicator", main);
        comm.start();
        pb = new PhraseBook(main);
    }

    public Communicator getCommunicator() {
        return comm;
    }

    public DirectionManager getDirectionManager() {
        return director;
    }

    public void cleanup() {
        comm.cleanup();
    }

    public void pauseConnection() {
        comm.pauseConnection();
    }

    public void resumeConnection() {
        comm.resumeConnection();
    }

    public int guessType(String command) {
        String[] words = command.split(" ");

        if (command.equals(PhraseBook.KEYPHRASE)) {
            return PhraseBook.KEY;
        }
        else if (words[0].equals("next")) {
            return PhraseBook.NEXT;
        }
        else if (words[words.length - 1].equals("fact")) {
            return PhraseBook.FUN_FACT;
        }
        // Direction start command
        else if (command.contains("direction")) {
            return PhraseBook.DIRECTION_START;
        }
        // Grab command
        else if (words[0].equals("grab") || words[0].equals("pick") || words[0].equals("fetch")) {
            return PhraseBook.MANIPULATION;
        }
        // Conversation command
        else {
            return PhraseBook.CONVERSATION;
        }
    }

    public void fetchCommand(String dirObject) {
        Movement.execute(Movement.GRAB, comm);
    }
}
