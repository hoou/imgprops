package cz.vutbr.fit.zpo.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXRadioButton;
import cz.vutbr.fit.zpo.dto.FileInformation;
import cz.vutbr.fit.zpo.dto.ImageInformation;
import cz.vutbr.fit.zpo.utils.ImageUtils;
import cz.vutbr.fit.zpo.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MasterRegionController extends Controller {
    /* ***********************************************************************
     *                              FXML nodes                               *
     *********************************************************************** */

    /* File information pane */
    @FXML
    public BorderPane fileInformationPane;
    @FXML
    public Label nameText;
    @FXML
    public Label mimeTypeText;
    @FXML
    public Label fileSizeText;

    /* Image information pane */
    @FXML
    public BorderPane imageInformationPane;
    @FXML
    public Text bitDepthText;
    @FXML
    public Text channelsText;
    @FXML
    public Text sizeText;
    @FXML
    public Text uniqueColorsText;

    /* Pixel information pane */
    @FXML
    public BorderPane pixelInformationPane;
    @FXML
    public Text xText;
    @FXML
    public Text yText;
    @FXML
    public Rectangle colorBox;
    @FXML
    public HBox pixelColorHbox;

    /* Channels pane */
    @FXML
    public BorderPane channelsPane;
    @FXML
    public JFXCheckBox redChannelCheckbox;
    @FXML
    public JFXCheckBox greenChannelCheckbox;
    @FXML
    public JFXCheckBox blueChannelCheckbox;

    /* Histogram pane */
    @FXML
    public BorderPane histogramPane;

    /* Brightness profile pane */
    @FXML
    public BorderPane brightnessProfilePane;
    @FXML
    public ToggleGroup brightnessProfileSelect;
    @FXML
    public JFXCheckBox brightnessProfileCheckbox;
    @FXML
    public JFXRadioButton rowProfileToggle;
    @FXML
    public JFXRadioButton columnProfileToggle;
    @FXML
    public AreaChart<Number, Number> brightnessProfileAreaChart;
    @FXML
    public NumberAxis brightnessValuesAxis;
    @FXML
    public NumberAxis profileLineLengthAxis;

    /* Brightness profile row average pane */
    @FXML
    public BorderPane brightnessProfileRowAveragePane;
    @FXML
    public AreaChart<Number, Number> brightnessProfileRowAverageAreaChart;
    @FXML
    public NumberAxis profileRowAverageLengthAxis;
    @FXML
    public NumberAxis brightnessValuesRowAverageAxis;

    /* Brightness profile column average pane */
    @FXML
    public BorderPane brightnessProfileColumnAveragePane;
    @FXML
    public AreaChart<Number, Number> brightnessProfileColumnAverageAreaChart;
    @FXML
    public NumberAxis profileColumnAverageLengthAxis;
    @FXML
    public NumberAxis brightnessValuesColumnAverageAxis;

    /* Brightness profile row average differentiated pane */
    @FXML
    public BorderPane brightnessProfileRowAverageDiffPane;
    @FXML
    public AreaChart<Number, Number> brightnessProfileRowAverageDiffAreaChart;
    @FXML
    public NumberAxis profileRowAverageDiffLengthAxis;
    @FXML
    public NumberAxis brightnessValuesRowAverageDiffAxis;

    /* Brightness profile column average differentiated pane */
    @FXML
    public BorderPane brightnessProfileColumnAverageDiffPane;
    @FXML
    public AreaChart<Number, Number> brightnessProfileColumnAverageDiffAreaChart;
    @FXML
    public NumberAxis profileColumnAverageDiffLengthAxis;
    @FXML
    public NumberAxis brightnessValuesColumnAverageDiffAxis;

    /* Brightness profile row average differentiated - second method pane */
    @FXML
    public BorderPane brightnessProfileRowAverageDiffSecondPane;
    @FXML
    public AreaChart<Number, Number> brightnessProfileRowAverageDiffSecondAreaChart;
    @FXML
    public NumberAxis profileRowAverageDiffSecondLengthAxis;
    @FXML
    public NumberAxis brightnessValuesRowAverageDiffSecondAxis;

    /* Brightness profile column average differentiated - second method pane */
    @FXML
    public BorderPane brightnessProfileColumnAverageDiffSecondPane;
    @FXML
    public AreaChart<Number, Number> brightnessProfileColumnAverageDiffSecondAreaChart;
    @FXML
    public NumberAxis profileColumnAverageDiffSecondLengthAxis;
    @FXML
    public NumberAxis brightnessValuesColumnAverageDiffSecondAxis;

    ObservableList<XYChart.Series<Number, Number>> currentProfileValues = FXCollections.observableArrayList();
    ObservableList<Double> brightnessProfileRowAverageData = FXCollections.observableArrayList();
    ObservableList<Double> brightnessProfileColumnAverageData = FXCollections.observableArrayList();
    ObservableList<Double> brightnessProfileRowAverageDiffData = FXCollections.observableArrayList();
    ObservableList<Double> brightnessProfileColumnAverageDiffData = FXCollections.observableArrayList();
    ObservableList<Double> brightnessProfileRowAverageDiffSecondData = FXCollections.observableArrayList();
    ObservableList<Double> brightnessProfileColumnAverageDiffSecondData = FXCollections.observableArrayList();
    private ObservableList<XYChart.Series<Number, Number>> rowAverageProfileValues = FXCollections.observableArrayList();
    private ObservableList<XYChart.Series<Number, Number>> columnAverageProfileValues = FXCollections.observableArrayList();
    private ObservableList<XYChart.Series<Number, Number>> rowAverageDiffProfileValues = FXCollections.observableArrayList();
    private ObservableList<XYChart.Series<Number, Number>> columnAverageDiffProfileValues = FXCollections.observableArrayList();
    private ObservableList<XYChart.Series<Number, Number>> rowAverageDiffSecondProfileValues = FXCollections.observableArrayList();
    private ObservableList<XYChart.Series<Number, Number>> columnAverageDiffSecondProfileValues = FXCollections.observableArrayList();

    /* File information pane private members */
    private Tooltip nameTooltip = new Tooltip();
    @FXML
    private Text widthText;
    @FXML
    private Text heightText;
    /* ***********************************************************************
     *                              Member fields                            *
     *********************************************************************** */
    private Map<String, List<Integer>> allHistogramValues = new LinkedHashMap<>();
    private ObservableList<XYChart.Series<Number, Number>> shownHistogramValues = FXCollections.observableArrayList();
    private List<Text> pixelColorTexts = new ArrayList<>();

    /* ***********************************************************************
     *                                 Methods                               *
     *********************************************************************** */
    @Override
    public void onStart() {
        nameTooltip.textProperty().bind(nameText.textProperty());
        nameText.setTooltip(nameTooltip);

        channelsPane.setVisible(false);
        channelsPane.setManaged(false);

        AreaChart<Number, Number> areaChart = createAreaChart();
        areaChart.setData(shownHistogramValues);
        histogramPane.setCenter(areaChart);

        brightnessProfileAreaChart.setData(currentProfileValues);
        brightnessProfileAreaChart.setLegendVisible(false);
        brightnessValuesAxis.setAutoRanging(false);
        brightnessValuesAxis.setLowerBound(0);
        brightnessValuesAxis.setUpperBound(255);
        brightnessValuesAxis.setTickUnit(20);

        addListenerToChangeSeriesOnChangeAverageProfileData(brightnessProfileRowAverageData, rowAverageProfileValues);
        addListenerToChangeSeriesOnChangeAverageProfileData(brightnessProfileColumnAverageData, columnAverageProfileValues);
        addListenerToChangeSeriesOnChangeAverageProfileData(brightnessProfileRowAverageDiffData, rowAverageDiffProfileValues);
        addListenerToChangeSeriesOnChangeAverageProfileData(brightnessProfileColumnAverageDiffData, columnAverageDiffProfileValues);
        addListenerToChangeSeriesOnChangeAverageProfileData(brightnessProfileRowAverageDiffSecondData, rowAverageDiffSecondProfileValues);
        addListenerToChangeSeriesOnChangeAverageProfileData(brightnessProfileColumnAverageDiffSecondData, columnAverageDiffSecondProfileValues);

        brightnessProfileRowAverageAreaChart.setData(rowAverageProfileValues);
        brightnessProfileRowAverageAreaChart.setLegendVisible(false);
        brightnessValuesRowAverageAxis.setAutoRanging(false);
        brightnessValuesRowAverageAxis.setLowerBound(0);
        brightnessValuesRowAverageAxis.setUpperBound(255);
        brightnessValuesRowAverageAxis.setTickUnit(20);

        brightnessProfileColumnAverageAreaChart.setData(columnAverageProfileValues);
        brightnessProfileColumnAverageAreaChart.setLegendVisible(false);
        brightnessValuesColumnAverageAxis.setAutoRanging(false);
        brightnessValuesColumnAverageAxis.setLowerBound(0);
        brightnessValuesColumnAverageAxis.setUpperBound(255);
        brightnessValuesColumnAverageAxis.setTickUnit(20);

        brightnessProfileRowAverageDiffAreaChart.setData(rowAverageDiffProfileValues);
        brightnessProfileRowAverageDiffAreaChart.setLegendVisible(false);
        brightnessValuesRowAverageDiffAxis.setAutoRanging(false);
        brightnessValuesRowAverageDiffAxis.setLowerBound(0);
        brightnessValuesRowAverageDiffAxis.setUpperBound(255);
        brightnessValuesRowAverageDiffAxis.setTickUnit(20);

        brightnessProfileColumnAverageDiffAreaChart.setData(columnAverageDiffProfileValues);
        brightnessProfileColumnAverageDiffAreaChart.setLegendVisible(false);
        brightnessValuesColumnAverageDiffAxis.setAutoRanging(false);
        brightnessValuesColumnAverageDiffAxis.setLowerBound(0);
        brightnessValuesColumnAverageDiffAxis.setUpperBound(255);
        brightnessValuesColumnAverageDiffAxis.setTickUnit(20);

        brightnessProfileRowAverageDiffSecondAreaChart.setData(rowAverageDiffSecondProfileValues);
        brightnessProfileRowAverageDiffSecondAreaChart.setLegendVisible(false);

        brightnessProfileColumnAverageDiffSecondAreaChart.setData(columnAverageDiffSecondProfileValues);
        brightnessProfileColumnAverageDiffSecondAreaChart.setLegendVisible(false);

        /* Clear selection when disabled */
        brightnessProfileCheckbox.disableProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                brightnessProfileCheckbox.setSelected(false);
            }
        });
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }

    private void addListenerToChangeSeriesOnChangeAverageProfileData(ObservableList<Double> data, ObservableList<XYChart.Series<Number, Number>> listOfSeries) {
        data.addListener((ListChangeListener<Double>) c -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    listOfSeries.clear();
                }
                if (c.wasAdded()) {
                    List<Number> tmp = new ArrayList<>();
                    tmp.addAll(c.getAddedSubList());
                    listOfSeries.add(createSeries(tmp, "brightness"));
                }
            }
        });
    }


    /* ********************   File information pane   ********************** */


    /**
     * Set texts for file information pane.
     *
     * @param fileInformation If null, all texts will be erased
     */
    void setFileInformation(FileInformation fileInformation) {
        if (fileInformation == null) {
            nameText.setText("");
            mimeTypeText.setText("");
            fileSizeText.setText("");
        } else {
            nameText.setText(fileInformation.getName());
            mimeTypeText.setText(fileInformation.getMimeType());
            fileSizeText.setText(Utils.humanReadableByteCount(fileInformation.getSize(), true));
        }
    }


    /* ********************   Image information pane   ********************* */


    /**
     * Set texts for image information pane.
     *
     * @param imageInformation If null, all texts will be erased
     */
    void setImageInformation(ImageInformation imageInformation) {
        if (imageInformation == null) {
            widthText.setText("");
            heightText.setText("");
            bitDepthText.setText("");
            channelsText.setText("");
            sizeText.setText("");
            uniqueColorsText.setText("");
        } else {
            widthText.setText(String.valueOf(imageInformation.getWidth()) + " px");
            heightText.setText(String.valueOf(imageInformation.getHeight()) + " px");
            bitDepthText.setText(String.valueOf(imageInformation.getBitDepth()));
            channelsText.setText(String.valueOf(imageInformation.getChannelsCount()));
            sizeText.setText(String.valueOf((int) imageInformation.getSize()));
            uniqueColorsText.setText(String.valueOf(imageInformation.getUniqueColors()));
        }
    }


    /* ********************   Pixel information pane   ********************* */


    /**
     * Create Text nodes. If number of channels of image is 1, create only one Text with Intensity label.
     * If number of channels of image is 3 or 4, create 3 Text nodes with R,G,B labels.
     *
     * @param numberOfChannels number of channels of image
     * @return list of Text nodes
     */
    List<Text> createTextsForPixelColor(int numberOfChannels) {
        pixelColorTexts = new ArrayList<>();
        GridPane gridPane = new GridPane();

        if (numberOfChannels == 1) {
            Label label = new Label("Intensity:");
            Text text = new Text();

            gridPane.addRow(0);

            ColumnConstraints col1 = new ColumnConstraints(60, 60, 60);
            ColumnConstraints col2 = new ColumnConstraints(30, 30, 30);

            gridPane.getColumnConstraints().addAll(col1, col2);
            gridPane.add(label, 0, 0);
            gridPane.add(text, 1, 0);

            pixelColorTexts.add(text);
        } else if (numberOfChannels == 3 || numberOfChannels == 4) {
            Label redLabel = new Label("Red:");
            redLabel.setTextFill(Color.RED);
            Label greenLabel = new Label("Green:");
            greenLabel.setTextFill(Color.GREEN);
            Label blueLabel = new Label("Blue:");
            blueLabel.setTextFill(Color.BLUE);
            Text redText = new Text();
            Text greenText = new Text();
            Text blueText = new Text();

            gridPane.addRow(0);
            gridPane.addRow(1);
            gridPane.addRow(2);

            ColumnConstraints col1 = new ColumnConstraints(50, 50, 50);
            ColumnConstraints col2 = new ColumnConstraints(30, 30, 30);

            gridPane.getColumnConstraints().addAll(col1, col2);
            gridPane.add(redLabel, 0, 0);
            gridPane.add(redText, 1, 0);
            gridPane.add(greenLabel, 0, 1);
            gridPane.add(greenText, 1, 1);
            gridPane.add(blueLabel, 0, 2);
            gridPane.add(blueText, 1, 2);

            pixelColorTexts.add(redText);
            pixelColorTexts.add(greenText);
            pixelColorTexts.add(blueText);
        }

        // Remove old Text nodes, if any.
        if (pixelColorHbox.getChildren().size() > 1) {
            pixelColorHbox.getChildren().remove(0);
        }

        pixelColorHbox.getChildren().add(0, gridPane);

        return pixelColorTexts;
    }

    void removeTextsForPixelColor() {
        if (pixelColorHbox.getChildren().size() > 1) {
            pixelColorHbox.getChildren().remove(0);
        }
        pixelColorTexts = new ArrayList<>();
    }


    /* ************************   Channels pane   ************************** */


    void setAllChannelCheckboxes() {
        redChannelCheckbox.setSelected(true);
        greenChannelCheckbox.setSelected(true);
        blueChannelCheckbox.setSelected(true);
    }


    /* ************************   Histogram pane   ************************* */


    private AreaChart<Number, Number> createAreaChart() {
        final NumberAxis xAxis = new NumberAxis(0, 255, 20);
        final NumberAxis yAxis = new NumberAxis();
        AreaChart<Number, Number> areaChart = new AreaChart<>(xAxis, yAxis);
        areaChart.setLegendVisible(false);
        return areaChart;
    }

    XYChart.Series<Number, Number> createSeries(List<Number> data, String name) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < data.size(); i++) {
            series.getData().add(new XYChart.Data<>(i, data.get(i)));
        }
        return series;
    }

    void createHistogram(Mat image) {
        allHistogramValues.clear();

        List<List<Integer>> data = ImageUtils.computeHistogram(image);
        //TODO normalize data maybe

        //TODO make alpha channel visible
        if (data.size() == 3 || data.size() == 4) {
            allHistogramValues.put("Red", data.get(2));
            allHistogramValues.put("Green", data.get(1));
            allHistogramValues.put("Blue", data.get(0));
        } else if (data.size() == 1) {
            allHistogramValues.put("Grey", data.get(0));
        } else {
            Utils.showErrorDialog("Cannot create histogram");
        }
    }

    /**
     * Create histogram checkboxes when size of histogram list is more than 1.
     * There is no need to hide or show 1 series of histogram.
     */
    void createHistogramCheckboxes() {
        removeHistogramCheckboxes();

        if (allHistogramValues.size() <= 1) {
            return;
        }

        GridPane gridPane = new GridPane();
        gridPane.addRow(0);
        gridPane.setAlignment(Pos.CENTER);

        List<String> keys = new ArrayList<>(allHistogramValues.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String name = keys.get(i);
            JFXCheckBox checkBox = new JFXCheckBox(name);
            checkBox.setSelected(true);
            checkBox.setCheckedColor(Color.web(name));
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> setHistogramSeriesVisible(name, newValue));
            ColumnConstraints ithCol = new ColumnConstraints();
            ithCol.setMaxWidth(70);
            ithCol.setMinWidth(70);
            gridPane.getColumnConstraints().add(ithCol);
            gridPane.add(checkBox, i, 0);
        }

        histogramPane.setBottom(gridPane);
    }

    void removeHistogramCheckboxes() {
        histogramPane.setBottom(null);
    }

    private void setHistogramSeriesVisible(String name, boolean visible) {
        shownHistogramValues.removeIf(numberNumberSeries -> numberNumberSeries.getName().equals(name));
        if (!visible) {
            shownHistogramValues.add(createSeries(new ArrayList<>(), name));
        } else {
            shownHistogramValues.add(createSeries(new ArrayList<>(allHistogramValues.get(name)), name));
        }
    }

    void clearAllHistogramSeries() {
        shownHistogramValues.clear();
    }

    void showAllHistogramSeries() {
        allHistogramValues.forEach((name, seriesData) -> setHistogramSeriesVisible(name, true));
    }
}
