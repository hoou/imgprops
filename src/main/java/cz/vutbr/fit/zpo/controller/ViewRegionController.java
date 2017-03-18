package cz.vutbr.fit.zpo.controller;

import cz.vutbr.fit.zpo.dto.PixelInformation;
import cz.vutbr.fit.zpo.utils.ImageUtils;
import cz.vutbr.fit.zpo.utils.OpenCvUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.*;

public class ViewRegionController extends Controller {
    @FXML
    public ImageView imageView;
    @FXML
    public BorderPane clickableOverlay;

    private Mat imageMat;
    private List<Mat> channelPlanes;

    private PixelInformation lastPixelInformation;

    @Override
    public void onStart() {
        lastPixelInformation = new PixelInformation();
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }

    public Mat getImageMat() {
        return imageMat;
    }

    public boolean setImage(File file) {
        imageMat = imread(file.getAbsolutePath(), CV_LOAD_IMAGE_ANYCOLOR | CV_LOAD_IMAGE_ANYDEPTH);

        if (imageMat.empty())
            return false;

        channelPlanes = ImageUtils.splitImageByChannels(imageMat);

        setImageView();

        return true;
    }

    private void setImageView() {
        Image image = OpenCvUtils.mat2Image(imageMat);

        imageView.setFitWidth(imageMat.cols());
        imageView.setFitHeight(imageMat.rows());
        imageView.setImage(image);
    }

    public void handleOnMouseMove(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getSource();
        if (imageView.getImage() == null) {
            return;
        }

        lastPixelInformation.setxPos(mouseEvent.getX());
        lastPixelInformation.setyPos(mouseEvent.getY());
        lastPixelInformation.setColor(ImageUtils.getColor(imageMat, (int) mouseEvent.getX(), (int) mouseEvent.getY()));
    }

    public PixelInformation getLastPixelInformation() {
        return lastPixelInformation;
    }

    public void toggleChannels(boolean red, boolean green, boolean blue) {
        List<Mat> channelsToShow = new ArrayList<>();

        if (blue) {
            channelsToShow.add(channelPlanes.get(0));
        } else {
            channelsToShow.add(Mat.zeros(channelPlanes.get(0).size(), channelPlanes.get(0).type()));
        }

        if (green) {
            channelsToShow.add(channelPlanes.get(1));
        } else {
            channelsToShow.add(Mat.zeros(channelPlanes.get(1).size(), channelPlanes.get(1).type()));
        }

        if (red) {
            channelsToShow.add(channelPlanes.get(2));
        } else {
            channelsToShow.add(Mat.zeros(channelPlanes.get(2).size(), channelPlanes.get(2).type()));
        }

        Mat mergedChannels = new Mat();
        Core.merge(channelsToShow, mergedChannels);

        imageMat = mergedChannels;
        setImageView();
    }
}
