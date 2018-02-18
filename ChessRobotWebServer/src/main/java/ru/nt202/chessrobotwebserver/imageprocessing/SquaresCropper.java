package ru.nt202.chessrobotwebserver.imageprocessing;

import java.awt.image.BufferedImage;

class SquaresCropper {
    private static final int SQUARE_SIZE = 224;
    private static final int S = (int) (SQUARE_SIZE / 1.6);

    BufferedImage[] crop(final BufferedImage board) {
        BufferedImage[] squares = new BufferedImage[64];
        int counter = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[counter] = board.getSubimage(
                        i * S + i * 12,
                        j * S + j * 12,
                        SQUARE_SIZE, SQUARE_SIZE);
                counter++;
            }
        }
        return squares;
    }
}