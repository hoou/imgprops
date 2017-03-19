package cz.vutbr.fit.zpo.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import cz.vutbr.fit.zpo.Main;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public abstract class ConfirmationDialog {
    private JFXDialog dialog;

    public ConfirmationDialog(String heading, String message) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text(heading));
        content.setBody(new Text(message));

        JFXButton yesButton = new JFXButton("Yes");
        yesButton.setStyle("-fx-background-color: #72ff6a; -fx-min-width: 80px");

        JFXButton noButton = new JFXButton("No");
        noButton.setStyle("-fx-background-color: #ff9387; -fx-min-width: 80px");

        content.setActions(noButton, yesButton);

        dialog = new JFXDialog((StackPane) Main.window.getScene().getRoot(), content, JFXDialog.DialogTransition.CENTER);

        yesButton.setOnAction(event -> {
            onConfirm();
            dialog.close();
        });

        noButton.setOnAction(event -> dialog.close());
    }

    public void show() {
        dialog.show();
    }

    public abstract void onConfirm();
}
