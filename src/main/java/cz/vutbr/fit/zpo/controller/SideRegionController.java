package cz.vutbr.fit.zpo.controller;

import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class SideRegionController extends Controller {
    @FXML
    public JFXCheckBox redChannelCheckbox;
    @FXML
    public JFXCheckBox greenChannelCheckbox;
    @FXML
    public JFXCheckBox blueChannelCheckbox;
    @FXML
    public BorderPane channelsPane;

    @Override
    public void onStart() {
        channelsPane.setVisible(false);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }

    public void setAllChannelCheckboxes() {
        redChannelCheckbox.setSelected(true);
        greenChannelCheckbox.setSelected(true);
        blueChannelCheckbox.setSelected(true);
    }
}
