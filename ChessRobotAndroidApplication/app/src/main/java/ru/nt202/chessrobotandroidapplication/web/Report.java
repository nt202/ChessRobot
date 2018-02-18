package ru.nt202.chessrobotandroidapplication.web;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Report {
    // Map<squareName, Map<pieceName, probability>>
    private Map<String, Map<String, Double>> squares = new HashMap<>(64);

    public String getPieceName(String squareName) {
        String pieceName = "unknown";
        Map<String, Double> square = squares.get(squareName);
        if (square != null) {
            double max = 0;
            for (Map.Entry<String, Double> entry : square.entrySet()) {
                double value = entry.getValue();
                if (value > max) {
                    max = value;
                    pieceName = entry.getKey();
                }
            }
        }
        return pieceName;
    }

    public Map<Double, String> getPiecesBySquareName(String squareName) {
        Map<String, Double> square = squares.get(squareName);
        if (square != null) {
            Map<Double, String> reverseSquare = new TreeMap<>();
            for (Map.Entry<String, Double> entry : square.entrySet()) {
                reverseSquare.put(entry.getValue(), entry.getKey());
            }
            return reverseSquare;
        } else {
            return null;
        }
    }
}