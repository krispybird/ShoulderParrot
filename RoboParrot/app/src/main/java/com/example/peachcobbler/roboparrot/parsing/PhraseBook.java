package com.example.peachcobbler.roboparrot.parsing;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PhraseBook {

    private static Set<String> OBJECTS = Collections.unmodifiableSet(new HashSet<String>() {
        {
            add("bag");
            add("backpack");
            add("pack");
            add("bottle");
            add("wallet");
        }
    });

    private static HashMap<String, Method> VERBS;

    public PhraseBook() {
        try {
            VERBS = new HashMap<String, Method>(){
                {
                    put("put",Parser.class.getMethod("takeCommand",String.class,String.class));
                    put("get",Parser.class.getMethod("fetchCommand",String.class,String.class));
                }
            };
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
