package cz.vutbr.fit.zpo.controller;

import cz.vutbr.fit.zpo.dto.PixelInformation;
import cz.vutbr.fit.zpo.utils.ImageUtils;
import cz.vutbr.fit.zpo.utils.OpenCvUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
    @FXML
    public StackPane viewImageStackPane;
    ObservableList<Integer> profileData = FXCollections.observableArrayList();
    private ImageView imageView;
    private AnchorPane drawingPane;
    private Mat imageMat;
    private List<Mat> channelPlanes;
    private PixelInformation lastPixelInformation;
    private Rectangle profileRect;

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
        imageView = new ImageView();
        imageView.setOnMouseMoved(this::handleOnMouseMove);
        imageView.setOnMouseExited(this::handleOnMouseExited);
        imageView.setFitWidth(imageMat.cols());
        imageView.setFitHeight(imageMat.rows());
        imageView.setImage(image);
        viewImageStackPane.getChildren().add(imageView);
    }

    void removeImageView() {
        viewImageStackPane.getChildren().remove(imageView);
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

    void startDrawingRows() {
        /* Remove old drawing pane */
        viewImageStackPane.getChildren().remove(drawingPane);

        drawingPane = new AnchorPane();
        int imageCols = imageMat.cols();
        int imageRows = imageMat.rows();

        drawingPane.setMinWidth(imageCols);
        drawingPane.setMaxWidth(imageCols);
        drawingPane.setMinHeight(imageRows);
        drawingPane.setMaxHeight(imageRows);
        drawingPane.setOnMouseMoved(event -> {
            if (event.getY() >= imageMat.rows())
                return;

            drawingPane.getChildren().remove(profileRect);
            profileRect = new Rectangle(0, event.getY(), imageCols, 1);
            profileRect.setFill(Color.RED);
            drawingPane.getChildren().add(profileRect);

            Mat selectedRow = imageMat.row((int) event.getY());
            drawProfile(selectedRow);
        });
        drawingPane.setOnMouseExited(event -> {
            drawingPane.getChildren().remove(profileRect);
            profileData.clear();
        });
        viewImageStackPane.getChildren().add(drawingPane);
    }

    void startDrawingColumns() {
        /* Remove old drawing pane */
        viewImageStackPane.getChildren().remove(drawingPane);

        drawingPane = new AnchorPane();
        int imageCols = imageMat.cols();
        int imageRows = imageMat.rows();

        drawingPane.setMinWidth(imageCols);
        drawingPane.setMaxWidth(imageCols);
        drawingPane.setMinHeight(imageRows);
        drawingPane.setMaxHeight(imageRows);
        drawingPane.setOnMouseMoved(event -> {
            if (event.getX() >= imageMat.cols())
                return;

            drawingPane.getChildren().remove(profileRect);
            profileRect = new Rectangle(event.getX(), 0, 1, imageRows);
            profileRect.setFill(Color.RED);
            drawingPane.getChildren().add(profileRect);

            Mat selectedColumn = imageMat.col((int) event.getX());
            drawProfile(selectedColumn);
        });
        drawingPane.setOnMouseExited(event -> {
            drawingPane.getChildren().remove(profileRect);
            profileData.clear();
        });
        viewImageStackPane.getChildren().add(drawingPane);
    }

    void stopDrawingRowsAndCols() {
        drawingPane.setOnMouseMoved(null);
        viewImageStackPane.getChildren().remove(drawingPane);
    }

    private void drawProfile(Mat line) {
        Mat greyscale;

        if (line.channels() != 1) {
            greyscale = ImageUtils.toGreyscale(line);
        } else {
            greyscale = line;
        }

        profileData.clear();
        if (greyscale.rows() == 1) {
            List<Integer> tmp = new ArrayList<>();
            for (int i = 0; i < greyscale.cols(); i++) {
                tmp.add((int) greyscale.get(0, i)[0]);
            }
            profileData.addAll(tmp);
        } else {
            List<Integer> tmp = new ArrayList<>();
            for (int i = 0; i < greyscale.rows(); i++) {
                tmp.add((int) greyscale.get(i, 0)[0]);
            }
            profileData.addAll(tmp);
        }
    }
}
