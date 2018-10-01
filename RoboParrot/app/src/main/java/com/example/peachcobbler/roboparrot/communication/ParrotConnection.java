package com.example.peachcobbler.roboparrot.communication;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

abstract class ParrotConnection {
    AppCompatActivity main;
    Handler handler;

    ParrotConnection(AppCompatActivity m, Handler h) {
        main = m;
        handler = h;
    }

    abstract void setup();

    abstract void close();
}
