import backend.core.LogicCommands;
import gui.CalcMenu;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Test;

public class NormalizationTest extends Application{

    @Test
    public void testNormalization()
    {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        new CalcMenu(primaryStage).openWindow();
    }
}
