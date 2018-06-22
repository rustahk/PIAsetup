import backend.core.ErrorProcessor;
import backend.core.Initializer;
import backend.files.Loader;
import console.*;
import gui.MainMenu;
import gui.NewMenu;
import javafx.application.Application;
import jssc.SerialPortException;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        //args = new String[1];

        if (args.length == 0) {
            startGUI(); //default GUI interface
            //$ Loader.loadDataset(new File("C:\\pia test data\\2018.06.22 155701 testname Scan.txt"));
        } else {
            startConsole(); //console interface
        }
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

        } finally {
            Initializer.fullClose();
        }
    }
}
