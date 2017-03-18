package cz.vutbr.fit.zpo.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXSpinner;
import cz.vutbr.fit.zpo.Main;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class Utils {
    private static Pane root;
    private static Node spinnerParent;
    private static boolean isSpinnerShown;

    static {
        root = (Pane) Main.window.getScene().getRoot().lookup("#viewStackPane");
        createSpinner();
        isSpinnerShown = false;
    }

    private static void createSpinner() {
        JFXSpinner loading = new JFXSpinner();
        spinnerParent = new BorderPane(loading);
    }

    public static void showErrorDialog(String message) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Error"));
        content.setBody(new Text(message));

        JFXButton button = new JFXButton("Ok");
        button.setStyle("-fx-background-color: darkgray; -fx-min-width: 80px");
        content.setActions(button);

        JFXDialog dialog = new JFXDialog((StackPane) Main.window.getScene().getRoot(), content, JFXDialog.DialogTransition.CENTER);
        dialog.show();

        button.setOnAction(event -> dialog.close());
    }

    /**
     * <a href="http://programming.guide/java/formatting-byte-size-to-human-readable-format.html">source</a>
     *
     * @param bytes number of bytes
     * @param si    use SI prefix
     * @return formatted number of bytes
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static void showSpinner() {
        if (!isSpinnerShown) {
            root.getChildren().add(spinnerParent);
            isSpinnerShown = true;
        }
    }

    public static void hideSpinner() {
        if (isSpinnerShown) {
            root.getChildren().remove(spinnerParent);
            isSpinnerShown = false;
        }
    }
}
