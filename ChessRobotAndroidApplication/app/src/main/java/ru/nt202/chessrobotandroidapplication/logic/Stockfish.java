package ru.nt202.chessrobotandroidapplication.logic;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Stockfish {

    private Process engineProcess;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    private final String SDCARD = Environment.getExternalStorageDirectory().toString();
    private final String ENGINE_NAME = "stockfish-8-armeabi-v7a";
    private final String PATH = SDCARD + "/" + ENGINE_NAME;

    public boolean startEngine() {
        try {
            engineProcess = Runtime.getRuntime().exec(new String[]{"su", "-c", PATH});
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(engineProcess.getOutputStream());
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Takes in any valid UCI command and executes it
     *
     * @param command
     */
    public void sendCommand(String command) {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is generally called right after 'sendCommand' for getting the raw
     * output from Stockfish
     *
     * @param waitTime Time in milliseconds for which the function waits before
     *                 reading the output. Useful when a long running command is
     *                 executed
     * @return Raw output from Stockfish
     */
    public String getOutput(int waitTime) {
        StringBuffer buffer = new StringBuffer();
        try {
            Thread.sleep(waitTime);
            sendCommand("isready");
            while (true) {
                String text = processReader.readLine();
                if (text.equals("readyok"))
                    break;
                else
                    buffer.append(text + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * This function returns the best move for a given position after
     * calculating for 'waitTime' ms
     *
     * @param fen      Position string
     * @param waitTime in milliseconds
     * @return Best Move in PGN format
     */
    public String[] getBestMove(String fen, int waitTime) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        String outputs;
        while (true) {
            try {
                String line = processReader.readLine();
                try {
                    outputs = line.split("bestmove ")[1].split(" ")[0];
                    break;
                } catch (Exception e) {
                    // NOPE
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String[] bestMove = new String[2];
        bestMove[0] = outputs.substring(0, 2);
        bestMove[1] = outputs.substring(2, 4);
        return bestMove;
    }

    /**
     * Stops Stockfish and cleans up before closing it
     */
    public void stopEngine() {
        try {
            sendCommand("quit");
            processReader.close();
            processWriter.close();
        } catch (IOException e) {
        }
    }

    /**
     * Get a set of all legal moves from the given position
     *
     * @param fen Position string
     * @return Map of all possibles moves
     */
    public Map<String, HashSet<String>> getLegalMoves(String fen) {
        Map<String, HashSet<String>> legalMovesMap = new HashMap<>(32);
        sendCommand("position fen " + fen + " w");
        sendCommand("perft 1");
        String[] outputs = getOutput(0).split("\n");
        String squareName;
        for (String output : outputs) {
            squareName = output.substring(0, 2);
            if (legalMovesMap.get(squareName) == null) {
                HashSet<String> set = new HashSet<>();
                set.add(output.substring(2, 4));
                legalMovesMap.put(squareName, set);
            } else {
                legalMovesMap.get(squareName).add(output.substring(2, 4));
            }
        }
        return legalMovesMap;
    }

    /**
     * Draws the current state of the chess board
     *
     * @param fen Position string
     */
    public void drawBoard(String fen) {
        sendCommand("position fen " + fen);
        sendCommand("d");

        String[] rows = getOutput(0).split("\n");

        for (int i = 1; i < 18; i++) {
            System.out.println(rows[i]);
        }
    }

    /**
     * Get the evaluation score of a given board position
     *
     * @param fen      Position string
     * @param waitTime in milliseconds
     * @return evalScore
     */
    public float getEvalScore(String fen, int waitTime) {
        sendCommand("position fen " + fen);
        sendCommand("go movetime " + waitTime);

        float evalScore = 0.0f;
        String[] dump = getOutput(waitTime + 20).split("\n");
        for (int i = dump.length - 1; i >= 0; i--) {
            if (dump[i].startsWith("info depth ")) {
                try {
                    evalScore = Float.parseFloat(dump[i].split("score cp ")[1]
                            .split(" nodes")[0]);
                } catch (Exception e) {
                    evalScore = Float.parseFloat(dump[i].split("score cp ")[1]
                            .split(" upperbound nodes")[0]);
                }
            }
        }
        return evalScore / 100;
    }
}