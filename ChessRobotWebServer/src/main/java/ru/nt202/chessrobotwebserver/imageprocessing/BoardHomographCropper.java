package ru.nt202.chessrobotwebserver.imageprocessing;

import java.awt.image.BufferedImage;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.inverse.InvertMatrix;

//http://www.corrmap.com/features/homography_transformation.php
//https://stackoverflow.com/questions/35296284/homography-transformation-of-image

// A*C=B

public class BoardHomographCropper {
    // NOTE: was calculated
    private static final int BOARD_SIZE = 1288;

    private static INDArray matrixA = null;
    private static INDArray matrixB = null;
    private static INDArray matrixC = null;

    BufferedImage rectify(final BufferedImage picture) {

        BufferedImage board = new BufferedImage(BOARD_SIZE, BOARD_SIZE, BufferedImage.TYPE_INT_RGB);

        float X1 = BoardCoordinates.getX0();
        float Y1 = BoardCoordinates.getY0();
        float X2 = BoardCoordinates.getX1();
        float Y2 = BoardCoordinates.getY1();
        float X3 = BoardCoordinates.getX2();
        float Y3 = BoardCoordinates.getY2();
        float X4 = BoardCoordinates.getX3();
        float Y4 = BoardCoordinates.getY3();
        float x1 = 0;
        float y1 = 0;
        float x2 = BOARD_SIZE - 1;
        float y2 = 0;
        float x3 = BOARD_SIZE - 1;
        float y3 = BOARD_SIZE - 1;
        float x4 = 0;
        float y4 = BOARD_SIZE - 1;

        matrixA = Nd4j.create(new float[]{
                x1, y1, 1, 0, 0, 0, -x1 * X1, -y1 * X1,
                x2, y2, 1, 0, 0, 0, -x2 * X2, -y2 * X2,
                x3, y3, 1, 0, 0, 0, -x3 * X3, -y3 * X3,
                x4, y4, 1, 0, 0, 0, -x4 * X4, -y4 * X4,
                0, 0, 0, x1, y1, 1, -x1 * Y1, -y1 * Y1,
                0, 0, 0, x2, y2, 1, -x2 * Y2, -y2 * Y2,
                0, 0, 0, x3, y3, 1, -x3 * Y3, -y3 * Y3,
                0, 0, 0, x4, y4, 1, -x4 * Y4, -y4 * Y4}, new int[]{8, 8});


        matrixB = Nd4j.create(new float[]{
                X1, X2, X3, X4, Y1, Y2, Y3, Y4}, new int[]{8, 1});

        matrixC = InvertMatrix.invert(matrixA, false).mmul(matrixB);

        double a = matrixC.getDouble(0);
        double b = matrixC.getDouble(1);
        double c = matrixC.getDouble(2);
        double d = matrixC.getDouble(3);
        double e = matrixC.getDouble(4);
        double f = matrixC.getDouble(5);
        double g = matrixC.getDouble(6);
        double h = matrixC.getDouble(7);

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int x = (int) (((a * i) + (b * j) + c) / ((g * i) + (h * j) + 1));
                int y = (int) (((d * i) + (e * j) + f) / ((g * i) + (h * j) + 1));
                int p = picture.getRGB(x, y);
                board.setRGB(i, j, p);
            }
        }
        return board;
    }
}
