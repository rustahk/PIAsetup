package backend.devices;

import backend.core.ErrorProcessor;
import jssc.SerialPortException;

import java.io.IOException;

public class Lockin {

    private static Connection connection;

    public static void init(Connection lockin) {
        connection = lockin;
    }

    public static String sendCommand(String command) throws  SerialPortException, IOException, InterruptedException //return String of Lockin reply
    {
        try
        {
        connection.sendMessage(command);
        }
        catch (SerialPortException e)
        {
            ErrorProcessor.standartError("Lockin connection problem", e);
            throw e;
        }
        try
        {
            return connection.getStringResponce();
        }
        catch (IOException e)
        {
            ErrorProcessor.standartError("Lockin reply problem", e);
            throw e;
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
