import gui.CalcMenu;
import gui.ConnectionMenu;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;

public class ConnectionMenuTest  extends Application {

    @Test
    public void testConnectionMenu()
    {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new ConnectionMenu(primaryStage).openWindow();
    }
}