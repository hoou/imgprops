package cz.vutbr.fit.zpo.controller;

import cz.vutbr.fit.zpo.Main;
import cz.vutbr.fit.zpo.dto.FileInformation;
import cz.vutbr.fit.zpo.dto.ImageInformation;
import cz.vutbr.fit.zpo.utils.FileUtils;
import cz.vutbr.fit.zpo.utils.ImageUtils;
import cz.vutbr.fit.zpo.utils.OpenCvUtils;
import cz.vutbr.fit.zpo.utils.Utils;
import cz.vutbr.fit.zpo.view.ConfirmationDialog;
import cz.vutbr.fit.zpo.view.InformationDialog;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RootLayoutController extends Controller {
    private final int maximumRecentFiles = 10;


    @FXML
    public Label leftStatus;
    @FXML
    public Menu openRecentMenu;
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
    public SplitPane splitPane;
    @FXML
    public StackPane rootStackPane;
    @FXML
    private MasterRegionController masterRegionController;
    @FXML
    private ViewRegionController viewRegionController;

    private ChangeListener<Boolean> rowProfileToggleChangeListener;
    private ChangeListener<Boolean> columnProfileToggleChangeListener;

    @Override
    public void onStart() {
        setSplitPaneInitialDividerPosition();

        MenuItem nothing = new MenuItem("no recent images");
        nothing.setDisable(true);
        openRecentMenu.getItems().add(nothing);

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

        rowProfileToggleChangeListener = (observable, oldValue, newValue) -> {
            if (newValue) {
                viewRegionController.drawRows();
            }
        };

        columnProfileToggleChangeListener = (observable, oldValue, newValue) -> {
            if (newValue) {
                viewRegionController.drawColumns();
            }
        };

        masterRegionController.brightnessProfileCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                masterRegionController.rowProfileToggle.selectedProperty().addListener(rowProfileToggleChangeListener);
                masterRegionController.columnProfileToggle.selectedProperty().addListener(columnProfileToggleChangeListener);
                if (masterRegionController.rowProfileToggle.selectedProperty().get()) {
                    viewRegionController.drawRows();
                } else {
                    viewRegionController.drawColumns();
                }
            } else {
                masterRegionController.rowProfileToggle.selectedProperty().removeListener(rowProfileToggleChangeListener);
                masterRegionController.columnProfileToggle.selectedProperty().removeListener(columnProfileToggleChangeListener);
            }
        });

        /* When image loaded, enable checkbox to turn on or off brightness profile */
        viewRegionController.viewPane.centerProperty().addListener(
                (observable, oldValue, newValue) ->
                        masterRegionController.brightnessProfileCheckbox.setDisable(!(newValue instanceof ImageView))
        );

        viewRegionController.viewPane.setOnMouseClicked(event -> openImageHandler());
    }

    private void setSplitPaneInitialDividerPosition() {
        Main.window.showingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    splitPane.setDividerPositions(500 / Main.window.getWidth());
                    observable.removeListener(this);
                }
            }
        });
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

    public void openImageHandler() {
        File file = openFileChooser();
        if (file == null)
            return;

        loadImage(file);
    }

    private File openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose picture...");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", ".jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp")
        );
        return fileChooser.showOpenDialog(Main.window);
    }

    private void loadImage(File file) {
        showChannelsCheckMenuItem.setSelected(false);
        showChannelsCheckMenuItem.setDisable(true);

        masterRegionController.setFileInformation(null);
        masterRegionController.setImageInformation(null);
        masterRegionController.removeTextsForPixelColor();
        masterRegionController.clearAllHistogramSeries();
        masterRegionController.removeHistogramCheckboxes();

        /* Remove old image view */
        viewRegionController.removeImageView();

        Utils.showSpinner();

        ImageLoadingService imageLoadingService = new ImageLoadingService(file);
        leftStatus.textProperty().bind(imageLoadingService.messageProperty());
        imageLoadingService.start();

        imageLoadingService.setOnFailed(event -> Utils.hideSpinner());
        imageLoadingService.setOnSucceeded(event -> {
            addRecentImageMenuItem(file);
            Utils.hideSpinner();
        });
        imageLoadingService.setOnCancelled(event -> Utils.hideSpinner());
    }

    private void addRecentImageMenuItem(File file) {
        // If fake menu item present, remove it. It only says, that no recent images found. Useless at this point.
        if (openRecentMenu.getItems().get(0).isDisable()) {
            openRecentMenu.getItems().remove(0);
        }

        // If menu item with this exactly file already exists, don't make new one, just move this to the top
        Optional<MenuItem> existingMenuItem = openRecentMenu.getItems().stream().filter(
                menuItem -> Objects.equals(((File) menuItem.getUserData()).getAbsolutePath(), file.getAbsolutePath())
        ).findAny();
        if (existingMenuItem.isPresent()) {
            openRecentMenu.getItems().remove(existingMenuItem.get());
            openRecentMenu.getItems().add(0, existingMenuItem.get());
        } else {
            MenuItem recentImageMenuItem = new MenuItem(file.getName());
            recentImageMenuItem.setUserData(file);
            recentImageMenuItem.setOnAction(event -> loadImage((File) recentImageMenuItem.getUserData()));
            openRecentMenu.getItems().add(0, recentImageMenuItem);

            // If maximum reached, remove least recently entry
            if (openRecentMenu.getItems().size() > maximumRecentFiles) {
                openRecentMenu.getItems().remove(openRecentMenu.getItems().size() - 1);
            }
        }
    }

    public void exitApplicationHandler(ActionEvent actionEvent) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog("Confirm Exit", "Are you sure you want to exit imgprops?") {
            @Override
            public void onConfirm() {
                Platform.exit();
            }
        };
        confirmationDialog.show();
    }

    public void aboutHandler(ActionEvent actionEvent) {
        InformationDialog dialog = new InformationDialog("About", "about text...");
        dialog.show();
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
            new Thread(new SetupGuiTask(file)).start();
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
            FileInformation fileInformation;
            ImageInformation imageInformation;
            Image image;

            fileInformation = FileUtils.getFileInformation(file);
            imageInformation = ImageUtils.getImageInformation(viewRegionController.getImageMat());
            image = OpenCvUtils.mat2Image(viewRegionController.getImageMat());

            Platform.runLater(() -> {
                masterRegionController.setFileInformation(fileInformation);

                masterRegionController.setImageInformation(imageInformation);

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

                viewRegionController.setImageView(image);

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
