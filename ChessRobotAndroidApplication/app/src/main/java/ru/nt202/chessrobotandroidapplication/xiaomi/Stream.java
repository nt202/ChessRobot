package ru.nt202.chessrobotandroidapplication.xiaomi;

import android.annotation.SuppressLint;
import org.json.JSONObject;

public class Stream extends Camera {

    private static final String COMMAND_TO_START_STREAM = "{\"msg_id\":259,\"token\":%d,\"param\":\"none_force\"}\r\n";
    private static final String COMMAND_TO_STOP_STREAM = "{\"msg_id\":260,\"token\":%d,\"param\":\"none_force\"}\r\n";

    @SuppressLint("DefaultLocale")
    public synchronized boolean startStream() {
        try {
            openAllConnections();
            getToken();
            bw.write(String.format(COMMAND_TO_START_STREAM, token));
            bw.flush();
            JSONObject response = getResponse(2);
            System.out.println(response);
            System.out.println("Stream started successfully");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("DefaultLocale")
    public synchronized void stopStream() {
        try {
            bw.write(String.format(COMMAND_TO_STOP_STREAM, token));
            bw.flush();
            JSONObject response = getResponse(2);
            System.out.println(response);
            System.out.println("Stream is stopped");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeAllConnections();
        }
    }
}