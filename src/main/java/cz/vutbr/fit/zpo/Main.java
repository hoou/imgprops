package cz.vutbr.fit.zpo;

import cz.vutbr.fit.zpo.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    /**
     * Controller references in main windows to proper handle app lifecycle
     */
    private static List<Controller> controllers = new ArrayList<>();

    public static Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Main.window = primaryStage;
        Main.window.setTitle("imgprops");

        initRootLayout();

        Main.window.setMaximized(true);

//        showOverview();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controllers.forEach(Controller::onStop);
    }

    private void initRootLayout() throws IOException {
        // Load root layout from fxml
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
        Parent rootLayout = loader.load();

        // Show the scene containing the root layout
        Scene scene = new Scene(rootLayout);
        scene.getStylesheets().add(Main.class.getResource("view/areaChart.css").toExternalForm());
        window.setScene(scene);
        window.show();
    }

    public static void registerController(Controller controller) {
        controllers.add(controller);
    }
}
