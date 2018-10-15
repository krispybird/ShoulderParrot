package com.example.peachcobbler.roboparrot.location.environment;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.peachcobbler.roboparrot.communication.Communicator;
import com.example.peachcobbler.roboparrot.parsing.PhraseBook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class POIFinder extends HandlerThread {
    private final String USERNAME = "RobotParrotBot";
    private final String SUPER_SECURE_PASSWORD = "YarrMateyShiverMeTimbers";
    private final String ROBOT_NAME = "RoboParrotBot@RoboParrotBotAgent";
    private final String ROBOT_PASSWORD = "24106njna0mv087eqf5i3f2do9q5n3rn";
    private final String USER_AGENT = "RoboParrotUIST/0.0";

    private final long INTERVAL = 60000;
    private final int RADIUS = 1000;
    private final int LIMIT = 10;
    private final int MAX_SENTENCES = 2;

    private static List<String> DEFAULT_FACTS = Collections.unmodifiableList(new ArrayList<String>() {
        {
            add("The Berlin Kulturbrauerei (literally \"Culture Brewery\") is a 25,000 square metres " +
                    "(270,000 sq ft) building complex in Berlin, Germany. Originally built and operated " +
                    "as a brewery, its courtyards and unique architecture have been protected as a " +
                    "monument since 1974 and it is one of the few well-preserved examples of industrial " +
                    "architecture in Berlin dating from the end of the 19th century.");
            add("The Museum in the Kulturbrauerei is museum of contemporary German history. The permanent " +
                    "exhibition focuses on everyday life in the German Democratic Republic. It is located " +
                    "in the Kulturbrauerei building complex in Prenzlauer Berg district (Borough of Pankow) " +
                    "in Berlin, Germany.");
            add("Friedrich-Ludwig-Jahn-Sportpark is a sports stadium in the Prenzlauer Berg district of " +
                    "Berlin. It is bordered on the south by Eberswalder Straße, on the north by the Max " +
                    "Schmeling Halle, on the west by Mauerpark, where part of the Berlin Wall once stood. " +
                    "The complex includes a football and athletics stadium as well as several smaller sports fields.");
            add("Prenzlauer Berg is a locality of Berlin, forming the southerly and most urban " +
                    "part of the district of Pankow. From its founding in 1920 until 2001, Prenzlauer " +
                    "Berg was a district of Berlin in its own right. However, that year it was " +
                    "incorporated (along with the borough of Weißensee) into the greater district of Pankow.");
            add("Mauerpark is a public linear park in Berlin's Prenzlauer Berg district. The name translates " +
                    "to \"Wall Park\", referring to its status as a former part of the Berlin Wall. The park is " +
                    "located at the border of Prenzlauer Berg and Gesundbrunnen district of former West Berlin.");
            add("Open every Sunday since 2004, Flohmarkt am Mauerpark (German for Mauerpark Flea Market) " +
                    "is popular with both locals and tourists alike. While a newcomer to the Berlin flea " +
                    "market scene, it is becoming a quick favorite. The loose grid stalls populate the " +
                    "western side of the park and offer a collection of new and vintage fashions, vinyl " +
                    "records, CDs, GDR memorabilia and antiques, bicycles and other nicknacks.");
            add("The Wasserturm Prenzlauer Berg is Berlin's oldest water tower, completed in 1877 and " +
                    "in use until 1952. The structure was designed by Henry Gill and built by the " +
                    "English Waterworks Company. It is situated between Knaackstraße and Belforter " +
                    "Straße in Kollwitzkiez, in the Prenzlauer Berg locality of Berlin (part of Pankow " +
                    "district) and worked on the principle of using piped water to supply the rapidly " +
                    "growing population of workers.");
            add("The Zeiss Major Planetarium (German Zeiss-Großplanetarium) is a planetarium in " +
                    "Berlin and one of the largest modern stellar theatres in Europe. It was opened " +
                    "in 1987 on the borders of the Ernst-Thälmann-Park housing estates in the Prenzlauer " +
                    "Berg locality of Berlin.");
        }
    });

    private Handler handler;
    private WikiRequest wiki;
    private POITimer timer;
    private AppCompatActivity main;
    private Random random;

    public POIFinder(AppCompatActivity m, String name) {
        super(name);
        main = m;
        random = new Random(System.currentTimeMillis());
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj instanceof Location) {
                    if (Communicator.internetConnected(main)) {
                        String response = (new GeoDataMessage((Location) msg.obj, RADIUS, LIMIT))
                                .send(wiki);
                        String[] sentences = response.split("\\.");
                        String say = "";
                        for (int i = 0; i < sentences.length && i < MAX_SENTENCES; i++) {
                            say += sentences[i] + ".";
                        }
                        Log.d("POI DESCRIPTION: ", response);
                        PhraseBook.respond(PhraseBook.FUN_FACT, say);
                        //PhraseBook.mTts.speak(response, TextToSpeech.QUEUE_ADD, null, String.valueOf(Math.random()));
                    }
                    else {
                        int ind = random.nextInt(DEFAULT_FACTS.size());
                        PhraseBook.respond(PhraseBook.FUN_FACT, DEFAULT_FACTS.get(ind));
                    }
                }
            }
        };

        wiki = new WikiRequest();

        /*timer = new POITimer(INTERVAL, handler);
        timer.start();*/
    }

    public Handler getHandler() {
        return handler;
    }
}
