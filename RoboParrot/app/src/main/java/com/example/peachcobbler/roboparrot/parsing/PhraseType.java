package com.example.peachcobbler.roboparrot.parsing;

public enum PhraseType {
    KEY     (0),
    VERB    (1),
    OBJECT  (2);

    int type;

    PhraseType(int t) {
        this.type = t;
    }
}
