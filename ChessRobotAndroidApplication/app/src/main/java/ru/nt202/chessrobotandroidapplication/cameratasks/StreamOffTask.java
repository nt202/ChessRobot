package ru.nt202.chessrobotandroidapplication.cameratasks;

import android.os.AsyncTask;
import ru.nt202.chessrobotandroidapplication.xiaomi.Stream;

public class StreamOffTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] objects) {
        Stream stream = new Stream();
        stream.stopStream();
        return null;
    }
}
