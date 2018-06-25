import backend.core.ErrorProcessor;
import backend.core.Initializer;
import console.*;
import gui.MainMenu;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        //args = new String[1];

        if (args.length == 0) {
            startGUI(); //default GUI interface
        } else {
            startConsole(); //console interface
        }
        Initializer.fullClose();
    }

    private static void startGUI() {
        Application.launch(MainMenu.class, null);
    }

    private static void startConsole() {
        //Start console output to see errors
        ConsoleOutput output = new ConsoleOutput();
        try {
            Initializer.fullInit();
            ConsoleProcessor.mainmenu(output);//Start console menu
        } catch (Exception ex_1) {
            ErrorProcessor.standartError("Start failed: ", ex_1);
        }
    }
}
