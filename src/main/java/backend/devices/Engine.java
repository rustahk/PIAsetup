package backend.devices;

import backend.core.ErrorProcessor;
import jssc.SerialPortException;

import java.io.IOException;

public class Engine implements Connectable {

    private static Engine engine;

    private Connection connection;

    public Engine(Connection connection) {
        this.connection = connection;
        engine = this;
    }

    public static synchronized int sendCommand(int[] command) throws SerialPortException, IOException, InterruptedException {
        int[] reply;
        try {
            engine.connection.sendMessage(command);
        } catch (SerialPortException e) {
            ErrorProcessor.standartError("Engine connection problem", e);
            throw e;
        }
        reply = engine.connection.getByteResponce();
        if (!EngineCommands.commandStatus(command, reply)) {
            IOException e = new IOException("Engine wrong responce");
            ErrorProcessor.standartError("Engine responce problem", e);
            throw e;
        }
        return EngineCommands.getValue(reply);
    }

    public void cleanInputBuffer() {
        connection.cleanInputBuffer();
    }

    @Override
    public void connect() throws SerialPortException{
        connection.connect();
    }

    @Override
    public void reconnect(Connection connection) throws SerialPortException{
        if(this.connection!=null && this.connection.isOpened()) this.connection.disconnect();
        this.connection = connection;
        this.connection.connect();
    }

    public static Engine getEngine() {
        return engine;
    }

    public Connection getConnection() {
        return connection;
    }
}
