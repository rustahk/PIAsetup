package backend.devices;

import jssc.SerialPortException;

import java.io.IOException;

public class Engine
{
    private static Connection connection;

    public static void init(Connection engine)
    {
        connection=engine;
    }

    public static int sendCommand(int[] command) throws IOException, SerialPortException
    {
        int[] reply;
        connection.sendMessage(command);
        reply = connection.getByteResponce();
        if (!EngineByteCommands.commandStatus(command, reply)) throw new IOException();
        return EngineByteCommands.getValue(reply);
    }

    public static boolean waitMoving()
    {
        while (true)
        {
            try
            {
                if (sendCommand(EngineByteCommands.getSpeed()) == 0) break;
                Thread.sleep(100);
            }
            catch (Exception e)
            {
                return false;
            }
        }
        return true;
    }

    public static Connection getConnection() {
        return connection;
    }
}
