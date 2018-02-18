package ru.nt202.chessrobotandroidapplication.logic;

import android.os.AsyncTask;

import java.util.Map;

import static ru.nt202.chessrobotandroidapplication.logic.Constants.squareNames;

public class AnalyserTask extends AsyncTask<BoardHandler, Void, String> {

    @Override
    protected String doInBackground(BoardHandler... boardHandlers) {
        BoardHandler boardHandler = boardHandlers[0];
        return assembleFen(boardHandler.getBoard());
    }

    private String assembleFen(Map<String, String> board) {
        StringBuilder fen = new StringBuilder();
        int emCounter = 0;

        String piece;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                piece = board.get(squareNames[i][j]);
                if (piece.equals("em")) {
                    emCounter++;
                } else {
                    if (emCounter > 0) {
                        fen.append(emCounter);
                        emCounter = 0;
                    }
                    fen.append(convert(piece));
                }
            }
            if (emCounter > 0) fen.append(emCounter);
            if (i != 7) fen.append("/");
            emCounter = 0;
        }
        return fen.toString();
    }

    private String convert(String piece) {
        if (piece.equals("bb")) return "b";
        if (piece.equals("bk")) return "k";
        if (piece.equals("bn")) return "n";
        if (piece.equals("bp")) return "p";
        if (piece.equals("bq")) return "q";
        if (piece.equals("br")) return "r";
        if (piece.equals("wb")) return "B";
        if (piece.equals("wk")) return "K";
        if (piece.equals("wn")) return "N";
        if (piece.equals("wp")) return "P";
        if (piece.equals("wq")) return "Q";
        if (piece.equals("wr")) return "R";
        return null;
    }
}
