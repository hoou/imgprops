package cz.vutbr.fit.zpo.utils;

import cz.vutbr.fit.zpo.dto.ImageInformation;
import javafx.scene.paint.Color;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ImageUtils {
    /**
     * Get color of image. If number of channels of image is 3 or 4, return RGB color.
     * If number of channels of image is 1, return greyscale color. Otherwise return #000000.
     *
     * @param image image to get color of
     * @param col x position of pixel to get color of
     * @param row y position of pixel to get color of
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

    public static ImageInformation getImageInformation(Mat image) {
        int width = image.width();
        int height = image.height();
        int channelsCount = image.channels();
        int bitDepth = CvType.ELEM_SIZE(image.depth()) * 8 * channelsCount;
        double size = image.size().area();
        return new ImageInformation(width, height, bitDepth, channelsCount, size);
    }

    public static List<Mat> splitImageByChannels(Mat image) {
        List<Mat> planes = new ArrayList<>();
        Core.split(image, planes);
        return planes;
    }
}
