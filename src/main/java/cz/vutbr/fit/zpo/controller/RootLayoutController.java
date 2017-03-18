package cz.vutbr.fit.zpo.controller;

import cz.vutbr.fit.zpo.Main;
import cz.vutbr.fit.zpo.utils.FileUtils;
import cz.vutbr.fit.zpo.utils.ImageUtils;
import cz.vutbr.fit.zpo.utils.Utils;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class RootLayoutController extends Controller {
    @FXML
    public Label leftStatus;
    @FXML
    public CheckMenuItem showFileInformationCheckMenuItem;
    @FXML
    public CheckMenuItem showImageInformationCheckMenuItem;
    @FXML
    public CheckMenuItem showPixelInformationCheckMenuItem;
    @FXML
    public CheckMenuItem showChannelsCheckMenuItem;
    @FXML
    public CheckMenuItem showHistogramCheckMenuItem;
    @FXML
    private MasterRegionController masterRegionController;
    @FXML
    private ViewRegionController viewRegionController;

    @Override
    public void onStart() {
        showChannelsCheckMenuItem.setSelected(false);
        showChannelsCheckMenuItem.setDisable(true);

        masterRegionController.fileInformationPane.visibleProperty().bind(showFileInformationCheckMenuItem.selectedProperty());
        masterRegionController.fileInformationPane.managedProperty().bind(showFileInformationCheckMenuItem.selectedProperty());
        masterRegionController.imageInformationPane.visibleProperty().bind(showImageInformationCheckMenuItem.selectedProperty());
        masterRegionController.imageInformationPane.managedProperty().bind(showImageInformationCheckMenuItem.selectedProperty());
        masterRegionController.pixelInformationPane.visibleProperty().bind(showPixelInformationCheckMenuItem.selectedProperty());
        masterRegionController.pixelInformationPane.managedProperty().bind(showPixelInformationCheckMenuItem.selectedProperty());
        masterRegionController.channelsPane.visibleProperty().bind(showChannelsCheckMenuItem.selectedProperty());
        masterRegionController.channelsPane.managedProperty().bind(showChannelsCheckMenuItem.selectedProperty());
        masterRegionController.histogramPane.visibleProperty().bind(showHistogramCheckMenuItem.selectedProperty());
        masterRegionController.histogramPane.managedProperty().bind(showHistogramCheckMenuItem.selectedProperty());

        masterRegionController.xText.textProperty().bind(viewRegionController.getLastPixelInformation().xPosProperty().asString("%.0f"));
        masterRegionController.yText.textProperty().bind(viewRegionController.getLastPixelInformation().yPosProperty().asString("%.0f"));
        masterRegionController.colorBox.fillProperty().bind(viewRegionController.getLastPixelInformation().colorProperty());

        masterRegionController.redChannelCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> toggleChannels());
        masterRegionController.greenChannelCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> toggleChannels());
        masterRegionController.blueChannelCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> toggleChannels());

        viewRegionController.viewPane.setOnMouseClicked(event -> openImage());
    }

    private void toggleChannels() {
        viewRegionController.toggleChannels(
                masterRegionController.redChannelCheckbox.isSelected(),
                masterRegionController.greenChannelCheckbox.isSelected(),
                masterRegionController.blueChannelCheckbox.isSelected()
        );
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }

    public void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose picture...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", ".jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
        File file = fileChooser.showOpenDialog(Main.window);
        if (file == null)
            return;

        viewRegionController.viewPane.setCenter(null);
        Utils.showSpinner();

        ImageLoadingService imageLoadingService = new ImageLoadingService(file);
        leftStatus.textProperty().bind(imageLoadingService.messageProperty());
        imageLoadingService.start();

        imageLoadingService.setOnFailed(event -> Utils.hideSpinner());
        imageLoadingService.setOnSucceeded(event -> Utils.hideSpinner());
        imageLoadingService.setOnCancelled(event -> Utils.hideSpinner());
    }

    private class ImageLoadingService extends Service<Void> {

        File file;

        ImageLoadingService(File file) {
            this.file = file;
        }

        @Override
        protected Task<Void> createTask() {
            return new LoadImageTask(file);
        }
    }

    private class LoadImageTask extends Task<Void> {

        File file;

        LoadImageTask(File file) {
            this.file = file;
        }

        @Override
        protected Void call() {
            updateMessage("Checking file type");
            if (!FileUtils.isFileImage(file)) {
                String message = "Loaded file is not an image";
                updateMessage(message);
                throw new IllegalArgumentException(message);
            }

            updateMessage("Loading image");
            boolean wasImageSet = viewRegionController.setImage(file);
            if (!wasImageSet) {
                String message = "Image format not supported";
                updateMessage(message);
                throw new IllegalArgumentException(message);
            }

            updateMessage("Loading histogram");
            masterRegionController.createHistogram(viewRegionController.getImageMat());

            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            new SetupGuiTask(file).run();
            updateMessage("Done");
        }

        @Override
        protected void failed() {
            super.failed();
            Utils.showErrorDialog(leftStatus.getText());
        }
    }

    private class SetupGuiTask extends Task<Void> {

        File file;

        SetupGuiTask(File file) {
            this.file = file;
        }

        @Override
        protected Void call() throws Exception {
            Platform.runLater(() -> {
                masterRegionController.setFileInformation(FileUtils.getFileInformation(file));

                masterRegionController.setImageInformation(ImageUtils.getImageInformation(viewRegionController.getImageMat()));

                masterRegionController.clearAllHistogramSeries();
                masterRegionController.showAllHistogramSeries();
                masterRegionController.createHistogramCheckboxes();

                List<Text> list = masterRegionController.createTextsForPixelColor(viewRegionController.getImageMat().channels());
                viewRegionController.getLastPixelInformation().colorProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        list.forEach(text -> text.setText(""));
                        return;
                    }

                    if (list.size() == 1) {
                        list.get(0).setText(String.valueOf((int) (newValue.getRed() * 255)));
                    } else if (list.size() == 3) {
                        list.get(0).setText(String.valueOf((int) (newValue.getRed() * 255)));
                        list.get(1).setText(String.valueOf((int) (newValue.getGreen() * 255)));
                        list.get(2).setText(String.valueOf((int) (newValue.getBlue() * 255)));
                    }
                });

                viewRegionController.setImageView();

                if (viewRegionController.getImageMat().channels() == 3 || viewRegionController.getImageMat().channels() == 4) {
                    showChannelsCheckMenuItem.setDisable(false);
                    showChannelsCheckMenuItem.setSelected(true);
                    masterRegionController.setAllChannelCheckboxes();
                } else {
                    showChannelsCheckMenuItem.setSelected(false);
                    showChannelsCheckMenuItem.setDisable(true);
                }
            });
            return null;
        }
    }
}
