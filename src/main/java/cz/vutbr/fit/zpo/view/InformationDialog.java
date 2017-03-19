package cz.vutbr.fit.zpo.view;

import com.jfoenix.controls.JFXButton;

public class InformationDialog extends Dialog {
    public InformationDialog(String heading, String message) {
        super(heading, message);
    }

    @Override
    protected void setActions() {
        JFXButton closeButton = new JFXButton("Close");
        closeButton.setStyle("-fx-background-color: #aeaeae; -fx-min-width: 80px");

        content.setActions(closeButton);

        closeButton.setOnAction(event -> close());
    }
}
