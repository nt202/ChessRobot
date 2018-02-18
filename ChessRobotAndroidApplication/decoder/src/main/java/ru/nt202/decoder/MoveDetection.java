package ru.nt202.decoder;

public class MoveDetection {

    static {
        System.loadLibrary("decoder");
    }

    public static native boolean moveHappened(String url);
}
