package backend.devices;

import backend.core.ErrorProcessor;
import jssc.SerialPortException;

import java.io.IOException;

public class Lockin implements Connectable {
    private static Lockin lockin;
    private Connection connection;

    public Lockin(Connection connection) {
        this.connection = connection;
        lockin = this;
    }

    public static synchronized String sendCommand(String command) throws SerialPortException, IOException, InterruptedException //return String of Lockin reply
    {
        try {
            lockin.connection.sendMessage(command);
        } catch (SerialPortException e) {
            ErrorProcessor.standartError("Lockin connection problem", e);
            throw e;
        }
        return lockin.connection.getStringResponce();
    }

    public void cleanInputBuffer() {
        connection.cleanInputBuffer();
    }

    @Override
    public void connect() throws SerialPortException {
        connection.connect();
    }

    @Override
    public void reconnect(Connection connection) throws SerialPortException {
        if (this.connection != null && this.connection.isOpened()) this.connection.disconnect();
        this.connection = connection;
        this.connection.connect();
    }

    public static Lockin getLockin() {
        return lockin;
    }

    public Connection getConnection() {
        return connection;
    }
}
