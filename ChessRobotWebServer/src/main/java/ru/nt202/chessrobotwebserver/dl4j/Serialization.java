package ru.nt202.chessrobotwebserver.dl4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.HashMap;
import java.util.Map;

class Serialization {

    private static final String[] pieces = {
            "bb", "bk", "bn", "bp", "bq", "br",
            "em",
            "wb", "wk", "wn", "wp", "wq", "wr"};

    private Map<String, Map<String, Double>> squares; // Shouldn't be static otherwise it'll not show up!

    Serialization() {
        squares = new HashMap<>(64);
    }

    void put(String squareName, INDArray probabilities) {
        Map<String, Double> probabilityMap = new HashMap<>(13);
        for (int i = 0; i < 13; i++) {
            probabilityMap.put(pieces[i], probabilities.getDouble(i));
        }
        squares.put(squareName, probabilityMap);
    }

    String getResult() {
        Gson GSON = new GsonBuilder().setPrettyPrinting().create();
        return GSON.toJson(this);
    }
}
