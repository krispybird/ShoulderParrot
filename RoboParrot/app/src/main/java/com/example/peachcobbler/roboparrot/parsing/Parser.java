package com.example.peachcobbler.roboparrot.parsing;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;

import com.example.peachcobbler.roboparrot.communication.Communicator;
import com.example.peachcobbler.roboparrot.location.ParrotLocationManager;
import com.example.peachcobbler.roboparrot.location.direction.DirectionManager;

public class Parser {
    private DirectionManager director;
    private Communicator comm;
    private PhraseBook pb;

    public Parser(AppCompatActivity main) {
        director = new DirectionManager("dmthread");
        director.start();
        director.setDirectionListener(new DirectionManager.DirectionListener() {
            @Override
            public void onDirection(String direction) {
                return;
            }
        });
        comm = new Communicator("Communicator", main);
        comm.start();
        pb = new PhraseBook(main);

        /*Location uni = new Location("");
        uni.setLatitude(51.079102);
        uni.setLongitude(-114.135778);

        try {
            director.startNavigation(uni);
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
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

    public void process(String command) {
        String[] words = command.split(" ");

        // Directions command
        if (words[0].equals("go") || words[0].equals("take")) {

        }
        // Grab command
        else if (words[0].equals("grab") || words[0].equals("pick") || words[0].equals("fetch")) {

        }
        // Conversation command
        else {
            pb.respond(PhraseBook.CONVERSATION, command);
        }
    }

    static void fetchCommand(String dirObject) {

    }
}
