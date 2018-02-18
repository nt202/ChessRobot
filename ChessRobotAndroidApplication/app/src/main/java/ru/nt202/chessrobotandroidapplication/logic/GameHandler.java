package ru.nt202.chessrobotandroidapplication.logic;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import ru.nt202.chessrobotandroidapplication.videoprocessing.MoveDetectionTask;
import ru.nt202.chessrobotandroidapplication.web.Report;
import ru.nt202.chessrobotandroidapplication.web.WebInteractionTask;

import static java.lang.Thread.sleep;

public class GameHandler {
    private static GameHandler handler;
    private WeakReference<Activity> activityWeakReference;
    private Report report;
    private WebInteractionTask webInteraction;
    private Stockfish stockfish;
    private BoardDrawerTask drawer;
    private ButtonsLockerTask locker;
    private BoardHandler boardHandler;
    private AnalyserTask analyser;
    private VoiceTask voice;
    private String fen;
    private MoveDetectionTask moveDetectionTask;

    private GameHandler(Activity context) {
        activityWeakReference = new WeakReference<>(context);
    }

    public static GameHandler getInstance(Activity context) {
        if (handler == null) {
            handler = new GameHandler(context);
            return handler;
        } else {
            return handler;
        }
    }

    public void recognize() {
        new Thread(() -> {
            try {
                webInteraction = new WebInteractionTask();
                webInteraction.execute();
                report = webInteraction.get();
                boardHandler = new BoardHandler(report);
                Activity activity = activityWeakReference.get();
                drawer = new BoardDrawerTask(activity, null);
                drawer.execute(boardHandler);
                locker = new ButtonsLockerTask(activity, false, false);
                locker.execute();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void refreshSquare(String squareName) {
        boardHandler.refreshSquare(squareName);
        Activity activity = activityWeakReference.get();
        drawer = new BoardDrawerTask(activity, squareName);
        drawer.execute(boardHandler);
    }

    public void play() {
        // Здесь можно спросить чей ход первый.
        new Thread(() -> {
            try {
                analyser = new AnalyserTask();
                analyser.execute(boardHandler);
                fen = analyser.get();
                stockfish.getOutput(0); // flush TODO: try delete
//                String[] bestMove = stockfish.getBestMove(fen, 1000); // TODO: from settings activity
//                Activity activity = activityWeakReference.get();
//                voice = new VoiceTask(activity, bestMove);
//                voice.execute();
                if (Constants.detection) {
                    moveDetectionTask = new MoveDetectionTask();
                    moveDetectionTask.execute();
                    if (moveDetectionTask.get()) {
                        move();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void move() { // happened by me
        new Thread(() -> {
            try {
                Activity activity = activityWeakReference.get();
                locker = new ButtonsLockerTask(activity, true, false);
                locker.execute();
                webInteraction = new WebInteractionTask();
                webInteraction.execute();
                report = webInteraction.get();
                Map<String, HashSet<String>> legalMoves = stockfish.getLegalMoves(fen);
                boardHandler.figureOut(report, legalMoves);
                locker = new ButtonsLockerTask(activity, false, true);
                locker.execute();
                drawer = new BoardDrawerTask(activity, null);
                drawer.execute(boardHandler);
                analyser = new AnalyserTask();
                analyser.execute(boardHandler);
                fen = analyser.get();
                String[] bestMove = stockfish.getBestMove(fen, Constants.strength); // TODO: from settings activity
                voice = new VoiceTask(activity, bestMove);
                voice.execute();
                boardHandler.updateBoard(bestMove);
                drawer = new BoardDrawerTask(activity, null);
                drawer.execute(boardHandler);
                if (Constants.detection) {
                    sleep(Constants.duration * 1000 + 1500);
                    voice = new VoiceTask(activity, new String[]{"ready"});
                    voice.execute();
                    moveDetectionTask = new MoveDetectionTask();
                    moveDetectionTask.execute();
                    if (moveDetectionTask.get()) {
                        move();
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void startStockfish() {
        stockfish = new Stockfish();
        stockfish.startEngine();
    }

    public void closeStockfish() {
        if (stockfish != null) {
            stockfish.stopEngine();
            System.out.println("Stockfish closed!"); // TODO: do LOG
        }
    }
}
