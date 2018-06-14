package backend.data;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class HotUpdater
{
    public static void updatePoint(final HotPoint hotpoint, final double x, final double y)
    {
        Platform.runLater(new Runnable() {
             public void run() {
                hotpoint.setXY(x, y);
            }
        });
    }
}
