/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algohmk3;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dubielsw
 */
public class Seam {
    
    private static double B_ENERGY = Double.MAX_VALUE;

    // could change this to BigDecimal for greater accuracy
    public static void verticalSeamShrink(UWECImage image) {
        
        findSeam(findImageEnergy(image), image, false);

    }

    public static void horizontalSeamShrink(UWECImage image) {
        
        image.im = image.transpose().im;
        findSeam(findImageEnergy(image), image, true);


    }

    private static void findSeam(double[][] pixelEnergy, UWECImage image, boolean hor) {

        double pixelSums[][] = new double[image.getWidth()][image.getHeight()];
        int traceBack[][] = new int[image.getWidth()][image.getHeight()];

        for (int x = 1; x < image.getWidth()-1; x++) {
            for (int y = 1; y < image.getHeight()-1; y++) {

                //base
                if (y == 1) {
                    pixelSums[x][y] = pixelEnergy[x][y];
                    traceBack[x][y] = -3;
                } else {
                    if (x == 1) {
                        pixelSums[x][y] = Math.min(pixelEnergy[x][y - 1], pixelEnergy[x + 1][y - 1]);
                        traceBack[x][y] = (pixelEnergy[x][y - 1] == pixelSums[x][y]) ? 0 : 1;

                    }
                    if (x == image.getWidth() - 2) {
                        pixelSums[x][y] = Math.min(pixelEnergy[x - 1][y - 1], pixelEnergy[x][y - 1]);
                        traceBack[x][y] = (pixelEnergy[x - 1][y - 1] == pixelSums[x][y]) ? -1 : 0;

                    } else {
                        pixelSums[x][y] = Math.min(pixelEnergy[x - 1][y - 1], Math.min(pixelEnergy[x][y - 1], pixelEnergy[x + 1][y - 1]));
                        if (pixelSums[x][y] == pixelEnergy[x - 1][y - 1]) {
                            traceBack[x][y] = -1;
                        } else if (pixelSums[x][y] == pixelEnergy[x][y - 1]) {
                            traceBack[x][y] = 0;
                        } else {
                            traceBack[x][y] = 1;
                        }
                    }
                }

            }
        }

        removeSeam(pixelSums, traceBack, image, hor);

    }

    private static void removeSeam(double[][] pixelSums, int[][] traceBack, UWECImage image, boolean hor) {

        int minIndex = 1;
        for (int i = 2; i < image.getWidth() - 1; i++) {
            if (pixelSums[i][image.getHeight() - 2] < pixelSums[minIndex][image.getHeight() - 2]) {
                minIndex = i;
            }
        }
        //start at lowest engery in last row and find way up
        int x = minIndex;
        int[] seam = new int[image.getHeight()];
        for (int y = image.getHeight() - 2; y > 0; y--) {
            image.setRGB(x, y, 255, 0, 0);
            seam[y] = x;
            x = x + traceBack[x][y];

        }

        if (hor) {
            image.im = image.transpose().im;
            image.repaintCurrentDisplayWindow();
            image.im = image.transpose().im;
        } else {
            image.repaintCurrentDisplayWindow();

        }

        x = minIndex;


        UWECImage newImage = new UWECImage(image.getWidth() - 1, image.getHeight());
        //System.out.println("SEAM " + seam.length + " im height " + image.getHeight());

        for (int j = 0; j < image.getHeight() - 1; j++) {
            for (int i = 0; i < image.getWidth() - 1; i++) {

                if (i >= seam[j]) {
                    newImage.setRGB(i, j, image.getRed(i + 1, j), image.getGreen(i + 1, j), image.getBlue(i + 1, j));

                } else {
                    newImage.setRGB(i, j, image.getRed(i, j), image.getGreen(i, j), image.getBlue(i, j));
                }

            }
        }

        if (hor) {

            image.switchImage(newImage);
            image.im = image.transpose().im;
            

        } else {

            image.switchImage(newImage);

        }
        image.repaintCurrentDisplayWindow();

    }

    private static double[][] findImageEnergy(UWECImage image) {
        double pixelEnergy[][] = new double[image.getWidth()][image.getHeight()];
        //System.out.println("Image height width "+image.getHeight() +" "+image.getWidth() );

        for (int r = 0; r < image.getWidth(); r++) {
            for (int c = 0; c < image.getHeight(); c++) {
                pixelEnergy[r][c] = getPixelEnergy(r, c, image);
            }

        }
        return pixelEnergy;

    }

    private static double getPixelEnergy(int r, int c, UWECImage image) {
        int outw = image.getWidth();
        int outh = image.getHeight();
        //TODO make sure this is right. Changes border to max energy
        if (c == 0 || c == image.getHeight() - 1 || r == 0 || r == image.getWidth() - 1) {
            return B_ENERGY;
        }
        if ((r < 0 || r >= outw) || (c < 0 || c >= outh)) {
            throw new IndexOutOfBoundsException();
        }

        return Gradient(r, c, image);

    }

    private static double Gradient(int x, int y, UWECImage image) {

        //y gradient
        double b = Math.abs(image.getBlue(x, y - 1) - image.getBlue(x, y + 1));

        double g = Math.abs(image.getGreen(x, y - 1) - image.getGreen(x, y + 1));

        double r = Math.abs(image.getRed(x, y - 1) - image.getRed(x, y + 1));

        double yGradient = r * r + g * g + b * b;

        //x gradient
        b = Math.abs(image.getBlue(x - 1, y) - image.getBlue(x + 1, y));

        g = Math.abs(image.getGreen(x - 1, y) - image.getGreen(x + 1, y));

        r = Math.abs(image.getRed(x - 1, y) - image.getRed(x + 1, y));

        double xGradient = r * r + g * g + b * b;

        return xGradient + yGradient;

    }



}
