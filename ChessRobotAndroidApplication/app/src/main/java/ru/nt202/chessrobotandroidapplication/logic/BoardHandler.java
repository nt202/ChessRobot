package ru.nt202.chessrobotandroidapplication.logic;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ru.nt202.chessrobotandroidapplication.web.Report;

import static ru.nt202.chessrobotandroidapplication.logic.Constants.blackPieceNames;
import static ru.nt202.chessrobotandroidapplication.logic.Constants.pieceNames;
import static ru.nt202.chessrobotandroidapplication.logic.Constants.squareNames;
import static ru.nt202.chessrobotandroidapplication.logic.Constants.whitePieceNames;

public class BoardHandler {

    private Report report;
    private Map<Double, String> reverseTreeSquare;
    private String squareName = null;
    private int counter = 1;
    private boolean flag = false;

    private static Map<String, String> board = null;

    public Map<String, String> getBoard() {
        return board;
    }

    // TODO: make asynchronous
    public BoardHandler(Report report) {
        this.report = report;
        if (board == null) {
            board = new HashMap<>(64);
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String squareName = squareNames[i][j];
                String pieceName = report.getPieceName(squareName);
                if (!pieceName.equals("unknown")) {
                    board.put(squareName, pieceName);
                }
            }
        }
    }

    public void refreshSquare(String squareName) {
        if (this.squareName != null && this.squareName.equals(squareName)) {
            counter++;
        } else {
            counter = 1;
            flag = false;
            this.squareName = squareName;
        }
        reverseTreeSquare = report.getPiecesBySquareName(squareName);
        if (reverseTreeSquare != null && counter < 3 && !flag) {
            Double key = reverseTreeSquare.keySet().toArray(new Double[reverseTreeSquare.size()])[12 - counter];
            String pieceName = reverseTreeSquare.get(key);
            board.put(squareName, pieceName);
        } else {
            flag = true;
            if (counter > 12) counter = 0;
            board.put(squareName, pieceNames[counter]);
        }
    }

    public void updateBoard(String[] bestMove) {
        String tempPieceName = board.get(bestMove[0]);
        board.put(bestMove[0], "em");
        board.put(bestMove[1], tempPieceName);
    }

    // to figure out which move I made:
    public void figureOut(Report report, Map<String, HashSet<String>> legalMoves) {
        this.report = report;
//        if (doesCastleHappened()) {
//                String movedPieceName = board.get(beginSquareName);
//        }
        String beginSquareName = findEmpty();
        String endSquareName = findFulled();
        String capturedSquareName = findCapture();

        Log.i("1212", "String beginSquareName = " + beginSquareName);
        Log.i("1212", "String endSquareName = " + endSquareName);
        Log.i("1212", "String capturedSquareName = " + capturedSquareName);

//        Log.d("BoardHandler", beginSquareName);
//        Log.d("BoardHandler", endSquareName);

        if (beginSquareName != null) {
            HashSet<String> legalMovesForSquare = legalMoves.get(beginSquareName);

            if (endSquareName != null) {
                if (legalMovesForSquare != null && legalMovesForSquare.contains(endSquareName)) {
                    board.put(endSquareName, board.get(beginSquareName));
                    board.put(beginSquareName, "em");
                } else {
                    Log.i("1212", "ХОД НЕ НАЙДЕН + 1");
                }
            } else {
                if (capturedSquareName != null) {
                    board.put(capturedSquareName, board.get(beginSquareName));
                    board.put(beginSquareName, "em");
                } else {
                    Log.i("1212", "ХОД НЕ НАЙДЕН + 2");
                }
            }
        } else {
            Log.i("1212", "ХОД НЕ НАЙДЕН + 3");
        }
    }

    private String findFulled() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squareName = squareNames[i][j];
                if (board.get(squareName).equals("em")) {
                    String pieceName = report.getPieceName(squareName);
                    if (!pieceName.equals("em") && !pieceName.equals("unknown")) {
                        return squareName;
                    }
                }
            }
        }
        return null;
    }

    private String findCapture() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squareName = squareNames[i][j];
                String reportPieceName = report.getPieceName(squareName);
                String boardPieceName = board.get(squareName);
                if (!reportPieceName.equals("unknown")) {
                    if (isWhite(reportPieceName)) {
                        if (isBlack(boardPieceName)) {
                            return squareName;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isBlack(String pieceName) {
        for (int n = 0; n < 6; n++) {
            if (pieceName.equals(blackPieceNames[n])) {
                return true;
            }
        }
        return false;
    }

    private boolean isWhite(String pieceName) {
        for (int n = 0; n < 6; n++) {
            if (pieceName.equals(whitePieceNames[n])) {
                return true;
            }
        }
        return false;
    }

//    private boolean doesCastleHappened() {
//
//    }

    private String findEmpty() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squareName = squareNames[i][j];
                if (report.getPieceName(squareName).equals("em")) {
                    if (!board.get(squareName).equals("em")) {
                        return squareName;
                    }
                }
            }
        }
        return null;
    }
}
