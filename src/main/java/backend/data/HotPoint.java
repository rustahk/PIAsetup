package backend.data;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class HotPoint
{
    private DoubleProperty x;
    private DoubleProperty y;
    private IntegerProperty n; //flag for listeners (and number of scaned points)

    public HotPoint()
    {
        this.x = new SimpleDoubleProperty();
        this.y = new SimpleDoubleProperty();
        this.n = new SimpleIntegerProperty();
        n.set(0);
    }

    public void setXY(double x, double y)
    {
        setX(x);
        setY(y);
        n.set(n.get()+1);
    }

    public void setN(int n) {
        this.n.set(n);
    }

    public int getN() {
        return n.get();
    }

    public IntegerProperty nProperty() {
        return n;
    }

    public double getX() {
        return x.get();
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public double getY() {
        return y.get();
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public static void updatePoint(final HotPoint hotpoint, final double x, final double y)
    {
        Platform.runLater(new Runnable() {
            public void run() {
                hotpoint.setXY(x, y);
            }
        });
    }
}
