package cz.vutbr.fit.zpo.controller;

import cz.vutbr.fit.zpo.Main;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Abstract controller which handle application lifecycle
 *
 * @author Martin Matejcik
 */
public abstract class Controller implements Initializable {
    static {
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.registerController(this);
        onStart();
        onRefresh();
    }

    /**
     * start method is called when controller is initializing
     */
    public abstract void onStart();

    /**
     * Refresh can be handled from {@link Main}
     */
    public abstract void onRefresh();

    /**
     * When application is being terminated, from {@link Main#stop()} invoke this
     */
    public abstract void onStop();
}
