package cz.vutbr.fit.zpo.controller;

import cz.vutbr.fit.zpo.dto.PixelInformation;
import cz.vutbr.fit.zpo.utils.ImageUtils;
import cz.vutbr.fit.zpo.utils.OpenCvUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgcodecs.Imgcodecs.*;

public class ViewRegionController extends Controller {
    @FXML
    public BorderPane viewPane;
    @FXML
    public Text clickHereText;
    @FXML
    public StackPane viewStackPane;


    public ImageView imageView = new ImageView();
    private Mat imageMat;
    private List<Mat> channelPlanes;
    private PixelInformation lastPixelInformation;

    @Override
    public void onStart() {
        lastPixelInformation = new PixelInformation();
        imageView.setOnMouseMoved(this::handleOnMouseMove);
        imageView.setOnMouseExited(this::handleOnMouseExited);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }

    Mat getImageMat() {
        return imageMat;
    }

    boolean setImage(File file) {
        imageMat = imread(file.getAbsolutePath(), CV_LOAD_IMAGE_ANYCOLOR | CV_LOAD_IMAGE_ANYDEPTH);

        if (imageMat.empty())
            return false;

        channelPlanes = ImageUtils.splitImageByChannels(imageMat);

        return true;
    }

    void setImageView(Image image) {
        imageView.setFitWidth(imageMat.cols());
        imageView.setFitHeight(imageMat.rows());
        imageView.setImage(image);
        viewPane.getChildren().remove(clickHereText);
        viewPane.setCenter(imageView);
    }

    private void handleOnMouseMove(MouseEvent mouseEvent) {
        ImageView imageView = (ImageView) mouseEvent.getSource();
        if (imageView.getImage() == null) {
            return;
        }

        lastPixelInformation.setxPos(mouseEvent.getX());
        lastPixelInformation.setyPos(mouseEvent.getY());
        lastPixelInformation.setColor(ImageUtils.getColor(imageMat, (int) mouseEvent.getX(), (int) mouseEvent.getY()));
    }

    private void handleOnMouseExited(MouseEvent mouseEvent) {
        lastPixelInformation.setColor(null);
        lastPixelInformation.setyPos(0);
        lastPixelInformation.setxPos(0);
    }

    PixelInformation getLastPixelInformation() {
        return lastPixelInformation;
    }

    void toggleChannels(boolean red, boolean green, boolean blue) {
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
        setImageView(OpenCvUtils.mat2Image(imageMat));
    }

    void drawRows() {
        System.out.println("Drawing rows");
    }

    void drawColumns() {
        System.out.println("Drawing columns");
    }
}
