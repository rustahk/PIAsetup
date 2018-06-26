package backend.devices;

import backend.core.ErrorProcessor;
import jssc.SerialPortException;

import java.io.IOException;

public class Engine {
    private static Connection connection;

    public static void init(Connection engine) {
        connection = engine;
    }

    public static synchronized int sendCommand(int[] command) throws SerialPortException, IOException, InterruptedException {
        int[] reply;
        try {
            connection.sendMessage(command);
        } catch (SerialPortException e) {
            ErrorProcessor.standartError("Engine connection problem", e);
            throw e;
        }
        reply = connection.getByteResponce();
        if (!EngineByteCommands.commandStatus(command, reply)) {
            IOException e = new IOException("Engine wrong responce");
            ErrorProcessor.standartError("Engine responce problem", e);
            throw e;
        }
        return EngineByteCommands.getValue(reply);
    }

    public static synchronized void waitMoving() throws SerialPortException, IOException, InterruptedException{
        while (true) {
            if (sendCommand(EngineByteCommands.getSpeed()) == 0) break;
            Thread.sleep(100);
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
