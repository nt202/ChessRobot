package ru.nt202.chessrobotwebserver.imageprocessing;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;

public class FacadeImageProcessing {
    private BufferedImage picture = null;
    private static BufferedImage[] previousSquares = null;

    public FacadeImageProcessing(InputStream board) {
        try {
            picture = ImageIO.read(board); // original
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage[] process() {
        picture = new DistortionRectifier().rectify(picture); // without distortion

        new CornersFinder().findCorners(picture);

        picture = new BoardHomographCropper().rectify(picture); // board

        // To check illumination:
        savePicture(picture, "board");

        BufferedImage[] squares = new SquaresCropper().crop(picture);


        BufferedImage[] newSquares = getMovedSquares(squares, previousSquares);

//        // Save squares (not necessary):
//        for (int i = 0; i < 64; i++) {
//            savePicture(squares[i], String.valueOf(i));
//        }
        previousSquares = Arrays.copyOf(squares, squares.length);

        return newSquares;
    }

    // TODO: расспараллелить
    private BufferedImage[] getMovedSquares(final BufferedImage[] squares, BufferedImage[] previousSquares) {
        if (previousSquares == null) {
            return squares;
        }
        BufferedImage[] locSquares = Arrays.copyOf(squares, squares.length);
        BufferedImage im1, im2;
        int sum = 0;
        for (int n = 0; n < 64; n++) {
            im1 = squares[n];
            im2 = previousSquares[n];
            for (int i = 0; i < 224; i+=2) {
                for (int j = 0; j < 224; j+=2) {
                    Color c1 = new Color(im1.getRGB(j, i));
                    Color c2 = new Color(im2.getRGB(j, i));
                    if (Math.abs(c1.getRed() - c2.getRed()) > 10) {
                        sum++;
                    }
                }
            }
            if (sum < 1200) {
                locSquares[n] = null;
            }
            sum = 0;
        }
        return locSquares;
    }

    private void savePicture(BufferedImage picture, String name) {
        ImageWriter writer = null;
        try {
            Iterator iterator = ImageIO.getImageWritersByFormatName("jpeg");
            writer = (ImageWriter) iterator.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(1);   // an integer of quality between 0 and 1

            String filename = name + ".jpg";
            File file = new File("./session", filename);

            FileImageOutputStream output = new FileImageOutputStream(file);
            writer.setOutput(output);
            IIOImage image = new IIOImage(picture, null, null);
            writer.write(null, image, iwp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.dispose();
            }
        }
    }
}
