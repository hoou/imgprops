package cz.vutbr.fit.zpo.controller;

import com.jfoenix.controls.JFXCheckBox;
import cz.vutbr.fit.zpo.dto.FileInformation;
import cz.vutbr.fit.zpo.dto.ImageInformation;
import cz.vutbr.fit.zpo.utils.ImageUtils;
import cz.vutbr.fit.zpo.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MasterRegionController extends Controller {
    @FXML
    public Text xText;
    @FXML
    public Text yText;
    @FXML
    public Rectangle colorBox;
    @FXML
    public BorderPane histogramPane;
    @FXML
    public Text bitDepthText;
    @FXML
    public Text channelsText;
    @FXML
    public Text sizeText;
    @FXML
    public Label nameText;
    @FXML
    public Label mimeTypeText;
    @FXML
    public Label fileSizeText;
    @FXML
    public HBox pixelColorHbox;
    @FXML
    private Text widthText;
    @FXML
    private Text heightText;

    private Map<String, List<Integer>> allHistogramValues = new LinkedHashMap<>();
    private ObservableList<XYChart.Series<Number, Number>> shownHistogramValues = FXCollections.observableArrayList();
    private AreaChart<Number, Number> areaChart;
    private Tooltip nameTooltip = new Tooltip();

    @Override
    public void onStart() {
        areaChart = createAreaChart();
        areaChart.setData(shownHistogramValues);
        histogramPane.setCenter(areaChart);

        nameTooltip.textProperty().bind(nameText.textProperty());
        nameText.setTooltip(nameTooltip);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }

    public void setFileInformation(FileInformation fileInformation) {
        nameText.setText(fileInformation.getName());
        mimeTypeText.setText(fileInformation.getMimeType());
        fileSizeText.setText(Utils.humanReadableByteCount(fileInformation.getSize(), true));
    }

    public void setImageInformation(ImageInformation imageInformation) {
        widthText.setText(String.valueOf(imageInformation.getWidth()) + " px");
        heightText.setText(String.valueOf(imageInformation.getHeight()) + " px");
        bitDepthText.setText(String.valueOf(imageInformation.getBitDepth()));
        channelsText.setText(String.valueOf(imageInformation.getChannelsCount()));
        sizeText.setText(String.valueOf((int) imageInformation.getSize()));
    }

    private AreaChart<Number, Number> createAreaChart() {
        final NumberAxis xAxis = new NumberAxis(0, 255, 20);
        final NumberAxis yAxis = new NumberAxis();
        AreaChart<Number, Number> areaChart = new AreaChart<>(xAxis, yAxis);
        areaChart.setLegendVisible(false);
        return areaChart;
    }

    private XYChart.Series<Number, Number> createSeries(List<Integer> data, String name) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < data.size(); i++) {
            series.getData().add(new XYChart.Data<>(i, data.get(i)));
        }
        return series;
    }

    public void createHistogram(Mat image) {
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
    public void createHistogramCheckboxes() {
        histogramPane.setBottom(null);

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

    /**
     * Create Text nodes. If number of channels of image is 1, create only one Text with Intensity label.
     * If number of channels of image is 3 or 4, create 3 Text nodes with R,G,B labels.
     *
     * @param numberOfChannels number of channels of image
     * @return list of Text nodes
     */
    public List<Text> createTextsForPixelColor(int numberOfChannels) {
        List<Text> list = new ArrayList<>();
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

            list.add(text);
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

            list.add(redText);
            list.add(greenText);
            list.add(blueText);
        }

        // Remove old Text nodes, if any.
        if (pixelColorHbox.getChildren().size() > 1) {
            pixelColorHbox.getChildren().remove(0);
        }

        pixelColorHbox.getChildren().add(0, gridPane);

        return list;
    }

    private void setHistogramSeriesVisible(String name, boolean visible) {
        shownHistogramValues.removeIf(numberNumberSeries -> numberNumberSeries.getName().equals(name));
        if (!visible) {
            shownHistogramValues.add(createSeries(new ArrayList<>(), name));
        } else {
            shownHistogramValues.add(createSeries(allHistogramValues.get(name), name));
        }
    }

    public void clearAllHistogramSeries() {
        shownHistogramValues.clear();
    }

    public void showAllHistogramSeries() {
        allHistogramValues.forEach((name, seriesData) -> setHistogramSeriesVisible(name, true));
    }
}
