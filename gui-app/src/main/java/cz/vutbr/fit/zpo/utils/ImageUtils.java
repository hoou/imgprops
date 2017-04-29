package cz.vutbr.fit.zpo.utils;

import cz.vutbr.fit.zpo.dto.ImageInformation;
import javafx.scene.paint.Color;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.Core.BORDER_DEFAULT;
import static org.opencv.core.CvType.CV_16S;

public class ImageUtils {
    /**
     * Get color of image. If number of channels of image is 3 or 4, return RGB color.
     * If number of channels of image is 1, return greyscale color. Otherwise return #000000.
     *
     * @param image image to get color of
     * @param col   x position of pixel to get color of
     * @param row   y position of pixel to get color of
     * @return color of pixel on position [col,row]
     */
    public static Color getColor(Mat image, int col, int row) {
        double R, G, B;

        if (row >= image.rows() || col >= image.cols()) {
            return null;
        }

        if (image.channels() == 3 || image.channels() == 4) {
            R = image.get(row, col)[2];
            G = image.get(row, col)[1];
            B = image.get(row, col)[0];
        } else if (image.channels() == 1) {
            R = G = B = image.get(row, col)[0];
        } else {
            R = G = B = 0;
        }

        return new Color(R / 255.0, G / 255.0, B / 255.0, 1);
    }

    /**
     * Compute histogram for each channel individually.
     *
     * @param image input image
     * @return list of data for each histogram
     */
    public static List<List<Integer>> computeHistogram(Mat image) {
        List<Mat> channelPlanes = splitImageByChannels(image);
        List<Mat> histogramMatrices = new ArrayList<>();

        MatOfInt histSize = new MatOfInt(256);

        final MatOfFloat histRange = new MatOfFloat(0f, 256f);

        for (int i = 0; i < channelPlanes.size(); i++) {
            histogramMatrices.add(new Mat());
            Imgproc.calcHist(channelPlanes, new MatOfInt(i), new Mat(), histogramMatrices.get(i), histSize, histRange, false);
        }

        List<List<Integer>> allData = new ArrayList<>();

        histogramMatrices.forEach(mat -> {
            List<Integer> data = new ArrayList<>();
            for (int i = 0; i < mat.rows(); i++) {
                data.add((int) mat.get(i, 0)[0]);
            }
            allData.add(data);
        });

        return allData;
    }

    public static long countUniqueColors(Mat image) {
        List<Color> listOfAllColors = new ArrayList<>();

        for (int i = 0; i < image.rows(); i++) {
            for (int j = 0; j < image.cols(); j++) {
                listOfAllColors.add(getColor(image, j, i));
            }
        }

        return listOfAllColors.stream().distinct().count();
    }

    public static ImageInformation getImageInformation(Mat image) {
        int width = image.width();
        int height = image.height();
        int channelsCount = image.channels();
        int bitDepth = CvType.ELEM_SIZE(image.depth()) * 8 * channelsCount;
        double size = image.size().area();
        long uniqueColors = countUniqueColors(image);
        return new ImageInformation(width, height, bitDepth, channelsCount, size, uniqueColors);
    }

    public static List<Mat> splitImageByChannels(Mat image) {
        List<Mat> planes = new ArrayList<>();
        Core.split(image, planes);
        return planes;
    }

    public static Mat toGreyscale(Mat im) {
        Mat greyscaleIm = new Mat();
        Imgproc.cvtColor(im, greyscaleIm, Imgproc.COLOR_BGR2GRAY);
        return greyscaleIm;
    }

    public static List<Double> calculateBrightnessProfileRowAverage(Mat im) {
        Mat greyscale;
        if (im.channels() != 1) {
            greyscale = toGreyscale(im);
        } else {
            greyscale = im;
        }

        List<Double> result = new ArrayList<>();

        for (int i = 0; i < greyscale.rows(); i++) {
            Mat row = greyscale.row(i);
            Scalar meanScalar = Core.mean(row);
            result.add(meanScalar.val[0]);
        }

        return result;
    }

    public static List<Double> calculateBrightnessProfileColumnAverage(Mat im) {
        Mat greyscale;
        if (im.channels() != 1) {
            greyscale = toGreyscale(im);
        } else {
            greyscale = im;
        }

        List<Double> result = new ArrayList<>();

        for (int i = 0; i < greyscale.cols(); i++) {
            Mat col = greyscale.col(i);
            Scalar meanScalar = Core.mean(col);
            result.add(meanScalar.val[0]);
        }

        return result;
    }

    public static List<Double> calculateBrightnessProfileRowAverageDiff(Mat im) {
        Mat sobel = sobelOperator(im);
        return calculateBrightnessProfileRowAverage(sobel);
    }

    public static List<Double> calculateBrightnessProfileColumnAverageDiff(Mat im) {
        Mat sobel = sobelOperator(im);
        return calculateBrightnessProfileColumnAverage(sobel);
    }

    public static Mat sobelOperator(Mat im) {
        Mat greyscale;
        Mat result = new Mat();
        if (im.channels() != 1) {
            greyscale = toGreyscale(im);
        } else {
            greyscale = im;
        }

        /* http://docs.opencv.org/2.4/doc/tutorials/imgproc/imgtrans/sobel_derivatives/sobel_derivatives.html */
        Mat gradientX = new Mat();
        Mat gradientY = new Mat();
        Mat absGradientX = new Mat();
        Mat absGradientY = new Mat();

        /* Gradient X */
        Imgproc.Sobel(greyscale, gradientX, CV_16S, 1, 0, 3, 1, 0, BORDER_DEFAULT);

        /* Gradient Y */
        Imgproc.Sobel(greyscale, gradientY, CV_16S, 0, 1, 3, 1, 0, BORDER_DEFAULT);

        Core.convertScaleAbs(gradientX, absGradientX);
        Core.convertScaleAbs(gradientY, absGradientY);

        Core.addWeighted(absGradientX, 0.5, absGradientY, 0.5, 0, result);

        return result;
    }
}
