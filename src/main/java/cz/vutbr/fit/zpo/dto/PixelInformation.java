package cz.vutbr.fit.zpo.dto;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class PixelInformation {
    public PixelInformation() {
        setxPos(0);
        setyPos(0);
        setColor(new Color(0,0,0,0));
    }

    private DoubleProperty xPos = new SimpleDoubleProperty();
    private DoubleProperty yPos = new SimpleDoubleProperty();
    private ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public double getxPos() {
        return xPos.get();
    }

    public DoubleProperty xPosProperty() {
        return xPos;
    }

    public void setxPos(double xPos) {
        this.xPos.set(xPos);
    }

    public double getyPos() {
        return yPos.get();
    }

    public DoubleProperty yPosProperty() {
        return yPos;
    }

    public void setyPos(double yPos) {
        this.yPos.set(yPos);
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void setColor(Color color) {
        this.color.set(color);
    }
}
