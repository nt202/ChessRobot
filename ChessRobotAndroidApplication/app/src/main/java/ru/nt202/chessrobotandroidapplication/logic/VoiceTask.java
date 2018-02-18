package ru.nt202.chessrobotandroidapplication.logic;

import android.app.Activity;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import ru.nt202.chessrobotandroidapplication.view.MainActivity;

public class VoiceTask extends AsyncTask<Void, Void, Void> {
    private WeakReference<Activity> activityReference;
    private String[] sounds;
    private MediaPlayer player;

    public VoiceTask(Activity context, String[] sounds) {
        activityReference = new WeakReference<>(context);
        this.sounds = sounds;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MainActivity activity = (MainActivity) activityReference.get();
        Resources resources = activity.getResources();
        int resourceId;
        int duration;

        for (String sound : sounds) {
            resourceId = resources.getIdentifier(sound, "raw", activity.getPackageName());
            player = MediaPlayer.create(activity, resourceId);
            duration = player.getDuration();
            player.start();
            try {
                Thread.sleep(duration - 100); // because player.stop() has delay
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
