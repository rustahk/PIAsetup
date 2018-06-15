import backend.core.ErrorProcessor;
import backend.core.Initializer;
import console.*;
import gui.MainMenu;
import javafx.application.Application;
import jssc.SerialPortException;

public class Main {

    private static Initializer setup_init;

    public static void main(String[] args) {
    //args = new String[1];

        if (args.length == 0) {
            startGUI(); //default GUI interface
        } else {
            startConsole(); //console interface
        }
    }

    private static void startGUI()
    {
        Application.launch(MainMenu.class, null);
    }

    private static void startConsole() {
        //Start console output to see errors
        ConsoleOutput output = new ConsoleOutput();
        try {
            if (!Initializer.fullInit()) throw new Exception("Init fail");
            ConsoleProcessor.mainmenu(output);//Start console menu
        } catch (Exception ex_1) {
            ErrorProcessor.standartError("Start failed: ", ex_1);

        } finally {
            try {
                setup_init.fullClose();
            } catch (Exception ex_2) {
                ErrorProcessor.standartError("Fail to close connection: ", ex_2);
            }
        }
    }
}
