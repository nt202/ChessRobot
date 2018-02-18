package ru.nt202.chessrobotandroidapplication.videoprocessing;

import android.os.AsyncTask;

import ru.nt202.decoder.MoveDetection;

public class MoveDetectionTask extends AsyncTask<Void, Void, Boolean> {

    private final String TAG = "MoveDetectionTask";

    private final String mediaURL = "rtsp://192.168.42.1/live";

    private MoveDetection moveDetection;

    public MoveDetectionTask() {
        moveDetection = new MoveDetection();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (moveDetection.moveHappened(mediaURL)) {
            System.out.println("MOVE");
            System.out.println("MOVE");
            System.out.println("MOVE");
            System.out.println("MOVE");
            System.out.println("MOVE");
            return true;
        } else {
            System.out.println("NULL's");
            System.out.println("NULL's");
            System.out.println("NULL's");
            System.out.println("NULL's");
            System.out.println("NULL's");
            return false;
        }
    }
}