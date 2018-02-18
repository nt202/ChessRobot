package ru.nt202.chessrobotandroidapplication.logic;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.Map;

import ru.nt202.chessrobotandroidapplication.view.MainActivity;

public class BoardDrawerTask extends AsyncTask<BoardHandler, Map<String, String>, Void> {

    private WeakReference<Activity> activityReference;
    private String squareName;

    public BoardDrawerTask(Activity context, String squareName) {
        activityReference = new WeakReference<>(context);
        this.squareName = squareName;
    }

    @Override
    protected Void doInBackground(BoardHandler... boardHandlers) {
        publishProgress(boardHandlers[0].getBoard()); // onProgressUpdate(Bitmap... values)
        return null;
    }

    @Override
    protected void onProgressUpdate(Map<String, String>... boards) {
        super.onProgressUpdate(boards);
        MainActivity activity = (MainActivity) activityReference.get();
        Map<String, String> board = boards[0];
        if (squareName == null) {
            activity.visualisePieces(board);
        } else {
            activity.visualisePiece(squareName, board.get(squareName));
        }
    }

}
