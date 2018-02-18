package ru.nt202.chessrobotwebserver.imageprocessing;

import java.awt.*;
import java.awt.image.BufferedImage;

class CornersFinder {
    void findCorners(final BufferedImage picture) {
        try {
            QuadrantHandler.annulCounter(); // necessary

            QuadrantHandler quadrant0 = new QuadrantHandler(picture, 2, 1);
            QuadrantHandler quadrant1 = new QuadrantHandler(picture, 5, 1);
            QuadrantHandler quadrant2 = new QuadrantHandler(picture, 5, 4);
            QuadrantHandler quadrant3 = new QuadrantHandler(picture, 2, 4);

            quadrant0.join();
            quadrant1.join();
            quadrant2.join();
            quadrant3.join();

//            System.out.println("Qudrant1: x=" + (quadrant0.getXCorner() - 816) + ", y=" + (quadrant0.getYCorner() - 408));
//            System.out.println("Qudrant2: x=" + (quadrant1.getXCorner() - 2040) + ", y=" + (quadrant1.getYCorner() - 408));
//            System.out.println("Qudrant3: x=" + (quadrant2.getXCorner() - 2040) + ", y=" + (quadrant2.getYCorner() - 1632));
//            System.out.println("Qudrant4: x=" + (quadrant3.getXCorner() - 816) + ", y=" + (quadrant3.getYCorner() - 1632));

            // NOTE1: Finding corners is produced after the distortion rectification.
            // NOTE2: Shifts were added because of the specific diode layout.
            new BoardCoordinates(quadrant0.getXCorner() + 14, quadrant0.getYCorner() + 3,
                    quadrant1.getXCorner() - 14, quadrant1.getYCorner() - 4,
                    quadrant2.getXCorner() - 8, quadrant2.getYCorner() + 3,
                    quadrant3.getXCorner() + 12, quadrant3.getYCorner() + 2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class QuadrantHandler extends Thread {
    private int xCorner = 0, yCorner = 0;

    private static int QUADRANT_SIZE;
    private static volatile int quadrantCounter = -1;
    private int quadrantNumber;
    private BufferedImage origin = null;
    private BufferedImage quadrant = null;
    private int m, n;

    static void annulCounter() {
        quadrantCounter = -1;
    }

    QuadrantHandler(BufferedImage origin, int x, int y) {
        this.origin = origin;
        QUADRANT_SIZE = origin.getHeight() / 6;
        this.n = x;
        this.m = y;
        setQuadrantNumber();
        start();
    }

    private synchronized void setQuadrantNumber() {
        quadrantCounter++;
        quadrantNumber = quadrantCounter;
    }

    int getXCorner() {
        return xCorner + QUADRANT_SIZE * n;
    }

    int getYCorner() {
        return yCorner + QUADRANT_SIZE * m;
    }

    @Override
    public void run() {
        quadrant = origin.getSubimage(QUADRANT_SIZE * n, QUADRANT_SIZE * m, QUADRANT_SIZE, QUADRANT_SIZE);
        int xBegin = 0, xStep = 0, xEnd = 0, yBegin = 0, yStep = 0, yEnd = 0;

        if (quadrantNumber == 0) {
            xBegin = QUADRANT_SIZE - 2;
            xStep = -2;
            xEnd = 2;
            yBegin = QUADRANT_SIZE - 2;
            yStep = -2;
            yEnd = 2;
        }
        if (quadrantNumber == 1) {
            xBegin = 2;
            xStep = 2;
            xEnd = QUADRANT_SIZE - 2;
            yBegin = QUADRANT_SIZE - 2;
            yStep = -2;
            yEnd = 2;
        }
        if (quadrantNumber == 2) {
            xBegin = 2;
            xStep = 2;
            xEnd = QUADRANT_SIZE - 2;
            yBegin = 2;
            yStep = 2;
            yEnd = QUADRANT_SIZE - 2;
        }
        if (quadrantNumber == 3) {
            xBegin = QUADRANT_SIZE - 2;
            xStep = -2;
            xEnd = 2;
            yBegin = 2;
            yStep = 2;
            yEnd = QUADRANT_SIZE - 2;
        }


        findCorners(xBegin, xStep, xEnd, yBegin, yStep, yEnd);
    }

    private void findCorners(int xBegin, int xStep, int xEnd, int yBegin, int yStep, int yEnd) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (int j = xBegin; j != xEnd; j += xStep) {
            for (int i = yBegin; i != yEnd; i += yStep) {
                r = new Color(quadrant.getRGB(j, i)).getRed();
                g = new Color(quadrant.getRGB(j, i)).getGreen();
                b = new Color(quadrant.getRGB(j, i)).getBlue();
                if (r == 255 && g > 204 && b > 204) {
                    yCorner = i;
                    xCorner = j;
                    return;
                }
            }
        }
    }
}