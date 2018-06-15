import backend.core.ErrorProcessor;
import backend.core.Initializer;
import console.*;
import gui.InterfaceManager;
import jssc.SerialPortException;

public class Main {

    private static Initializer setup_init;

    public static void main(String[] args) {

        if (args.length == 0) {
            startGUI(); //default GUI interface
        } else {
            startConsole(); //console interface
        }
    }

    private static void startGUI()
    {
        InterfaceManager.startTerminal();
        Initializer.loadConfig();
        try {
            Initializer.configConnection();
        }
        catch (SerialPortException e)
        {
            ErrorProcessor.standartError("Connection config: ", e);
        }
        try {
            Initializer.openConnection();
        }
        catch (SerialPortException e)
        {
            ErrorProcessor.standartError("Connection open: ", e);
        }

    }

    private static void startConsole() {
        //Start console output to see errors
        ConsoleOutput output = new ConsoleOutput();
        try {
            Initializer.fullInit();
            //Start console menu
            ConsoleProcessor.mainmenu(output);
        } catch (SerialPortException ex_port) {
            ErrorProcessor.standartError("Start failed: Connection problem ", ex_port);
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
