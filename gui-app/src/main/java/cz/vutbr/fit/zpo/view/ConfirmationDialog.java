package cz.vutbr.fit.zpo.view;

import com.jfoenix.controls.JFXButton;

public abstract class ConfirmationDialog extends Dialog {
    public ConfirmationDialog(String heading, String message) {
        super(heading, message);
    }

    @Override
    protected void setActions() {
        JFXButton yesButton = new JFXButton("Yes");
        yesButton.setStyle("-fx-background-color: #72ff6a; -fx-min-width: 80px");

        JFXButton noButton = new JFXButton("No");
        noButton.setStyle("-fx-background-color: #ff9387; -fx-min-width: 80px");

        yesButton.setOnAction(event -> {
            onConfirm();
            close();
        });

        noButton.setOnAction(event -> close());

        content.setActions(noButton, yesButton);
    }

    public abstract void onConfirm();
}
