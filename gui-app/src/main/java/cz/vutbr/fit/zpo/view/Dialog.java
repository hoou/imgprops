package cz.vutbr.fit.zpo.view;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import cz.vutbr.fit.zpo.Main;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public abstract class Dialog {
    protected JFXDialog dialog;
    protected JFXDialogLayout content;

    public Dialog(String heading, String message) {
        content = new JFXDialogLayout();
        content.setHeading(new Text(heading));
        content.setBody(new Text(message));
        content.getBody().get(0).getParent().setStyle("-fx-alignment: center-left");

        setActions();

        dialog = new JFXDialog((StackPane) Main.window.getScene().getRoot(), content, JFXDialog.DialogTransition.CENTER);
    }

    public void show() {
        dialog.show();
    }

    public void close() {
        dialog.close();
    }

    protected abstract void setActions();
}
