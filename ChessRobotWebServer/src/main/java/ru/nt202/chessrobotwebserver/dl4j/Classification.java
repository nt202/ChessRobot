package ru.nt202.chessrobotwebserver.dl4j;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Classification {
    private static final String[][] squareNames = {
            {"a8", "a7", "a6", "a5", "a4", "a3", "a2", "a1"},
            {"b8", "b7", "b6", "b5", "b4", "b3", "b2", "b1"},
            {"c8", "c7", "c6", "c5", "c4", "c3", "c2", "c1"},
            {"d8", "d7", "d6", "d5", "d4", "d3", "d2", "d1"},
            {"e8", "e7", "e6", "e5", "e4", "e3", "e2", "e1"},
            {"f8", "f7", "f6", "f5", "f4", "f3", "f2", "f1"},
            {"g8", "g7", "g6", "g5", "g4", "g3", "g2", "g1"},
            {"h8", "h7", "h6", "h5", "h4", "h3", "h2", "h1"}};

    private static ComputationGraph graph = null;
    private static NativeImageLoader loader = null;
    private static INDArray[] probabilities = null;
    private static INDArray image;

    private Serialization serialization;

    private static Classification classification = null;

    public static Classification getInstance() {
        if (classification != null) {
            return classification;
        } else {
            classification = new Classification();
            return classification;
        }
    }

    private Classification() {
        int imageHeight = 224;
        int imageWidth = 224;
        int nChannels = 3;

        InputStream locationToSave = null;
        try {
            locationToSave = Classification.class.getClass().getResourceAsStream("/model.zip");
            graph = ModelSerializer.restoreComputationGraph(locationToSave);
            loader = new NativeImageLoader(imageHeight, imageWidth, nChannels);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (locationToSave != null) {
                try {
                    locationToSave.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String getResult(BufferedImage[] squares) {
        serialization = new Serialization();
        int counter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                try {
                    if (squares[counter] != null) {
                        image = loader.asMatrix(convert(squares[counter]));
                        probabilities = graph.output(false, image);
                        serialization.put(squareNames[i][j], probabilities[0]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                counter++;
            }
        }
        return serialization.getResult();
    }

    private InputStream convert(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }
}
