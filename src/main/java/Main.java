import backend.core.ErrorProcessor;
import backend.core.Initializer;
import console.*;
import jssc.SerialPortException;

public class Main {

    private static Initializer setup_init;

    public static void main(String[] args) {
        //Start console output to see errors
        ConsoleOutput output = new ConsoleOutput();
        try {
            setup_init = new Initializer();
            //Start console menu
            ConsoleProcessor.mainmenu(output);
        } catch (SerialPortException ex_port) {
            ErrorProcessor.standartError("Start failed: Connection problem ", ex_port);
        } catch (Exception ex_1) {
            ErrorProcessor.standartError("Start failed: ", ex_1);

        } finally {
            try {
                setup_init.close();
            }
            catch (Exception ex_2) {
                ErrorProcessor.standartError("Fail to close connection: ", ex_2);
            }
        }


    }

    public static void stop() //Main exit point
    {
        setup_init.close();
    }
}
