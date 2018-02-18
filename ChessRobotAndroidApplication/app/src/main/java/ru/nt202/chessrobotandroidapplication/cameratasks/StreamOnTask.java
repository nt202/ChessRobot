package ru.nt202.chessrobotandroidapplication.cameratasks;

import android.os.AsyncTask;
import ru.nt202.chessrobotandroidapplication.xiaomi.Stream;

public class StreamOnTask extends AsyncTask<Void, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Void... voids) {
        Stream stream = new Stream();
        return stream.startStream();
    }
}