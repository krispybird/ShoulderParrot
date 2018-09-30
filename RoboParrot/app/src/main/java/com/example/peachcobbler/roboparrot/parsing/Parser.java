package com.example.peachcobbler.roboparrot.parsing;

import android.content.Context;

import com.example.peachcobbler.roboparrot.location.direction.DirectionManager;

public class Parser {
    private DirectionManager director;

    public Parser(Context main) {
        director = new DirectionManager(main);
    }

    static void fetchCommand(String indObject, String dirObject) {

    }

    static void takeCommand(String indObject, String dirObject) {

    }
}
