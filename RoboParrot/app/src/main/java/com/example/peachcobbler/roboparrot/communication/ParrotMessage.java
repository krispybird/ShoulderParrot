package com.example.peachcobbler.roboparrot.communication;

class ParrotMessage {
    private String contents;

    ParrotMessage(String test) {
        contents = test;
    }

    byte[] format() {
        return contents.getBytes();
    }

    @Override
    public String toString() {
        return contents;
    }
}
