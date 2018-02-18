package ru.nt202.chessrobotandroidapplication.logic;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Button;

import java.lang.ref.WeakReference;

import ru.nt202.chessrobotandroidapplication.view.MainActivity;

public class ButtonsLockerTask extends AsyncTask<Void, Button, Void> {
    private WeakReference<Activity> activityReference;
    private boolean move, start;

    public ButtonsLockerTask(Activity context, boolean start, boolean move) {
        activityReference = new WeakReference<>(context);
        this.start = start;
        this.move = move;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        publishProgress();
        return null;
    }

    @Override
    protected void onProgressUpdate(Button... values) {
        super.onProgressUpdate(values);
        MainActivity activity = (MainActivity) activityReference.get();
        if (move) {
            activity.unlockMoveButton();
            activity.stopProgressBar();
        } else {
            if (start) {
                activity.lockMoveBtn();
                activity.startProgressBar();
            } else {
                activity.unlockPieceButtons();
                activity.unlockPlayButton();
                activity.stopProgressBar();
            }
        }

    }
}
