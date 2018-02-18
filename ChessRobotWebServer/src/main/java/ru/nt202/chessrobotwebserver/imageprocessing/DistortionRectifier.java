package ru.nt202.chessrobotwebserver.imageprocessing;
//https://mipav.cit.nih.gov/pubwiki/index.php/Barrel_Distortion_Correction

import java.awt.image.BufferedImage;


class DistortionRectifier {
    private static final double A = 0.075; // affects only the outermost pixels of the image
    private static final double B = 0.01; // most cases only require b optimization
    private static final double C = 0.0; // most uniform correction
    private static final double D = 1.0 - A - B - C; // describes the linear scaling of the image

    BufferedImage rectify(final BufferedImage picture) {
        int width = picture.getWidth();
        int height = picture.getHeight();
        BufferedImage output = new BufferedImage(width, height, picture.getType());
        double centerX = (width - 1) / 2.0;
        double centerY = (height - 1) / 2.0;
        int radius = Math.min(width, height) / 2;    // radius of the circle
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double deltaX = (x - centerX) / radius;
                double deltaY = (y - centerY) / radius;
                double dstR = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                double srcR = (A * dstR * dstR * dstR + B * dstR * dstR + C * dstR + D) * dstR;
                double factor = Math.abs(dstR / srcR);
                double srcXd = centerX + (deltaX * factor * radius);
                double srcYd = centerY + (deltaY * factor * radius);
                int srcX = (int) srcXd;
                int srcY = (int) srcYd;
                if (srcX >= 0 && srcY >= 0 && srcX < width && srcY < height) {
                    output.setRGB(x, y, picture.getRGB(srcX, srcY));
                }
            }
        }
        return output;
    }
}
