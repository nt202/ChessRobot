package ru.nt202.chessrobotandroidapplication.web;

import android.os.AsyncTask;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.nt202.chessrobotandroidapplication.logic.Constants;
import ru.nt202.chessrobotandroidapplication.xiaomi.Capture;

public class WebInteractionTask extends AsyncTask<Void, Void, Report> {

    // TODO: Retrieve from SettingsActivity
//    private final String webURL = "http://192.168.42.3:8080/result";
    private final String webURL;
    private Report report;
    private Gson GSON;

    public WebInteractionTask() {
        report = new Report();
        GSON = new GsonBuilder().setPrettyPrinting().create();
        webURL = "http://" + Constants.ip + ":"
                + Constants.port + "/result";
    }

    @Override
    protected Report doInBackground(Void... voids) {
        InputStream is = null;
        try {
            HttpRequest request = HttpRequest.post(webURL);
            Capture capture = new Capture();
            URL pictureURL = capture.receivePictureURL();
            is = getInputStreamFromURL(pictureURL);
            request.part("board", is);
            String response = request.body();
            report = GSON.fromJson(response, report.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return report;
    }

    private InputStream getInputStreamFromURL(URL url) {
        HttpURLConnection connection;
        InputStream is;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            is = connection.getInputStream();
            return is;
        } catch (IOException e) {
            return null;
        }
    }
}