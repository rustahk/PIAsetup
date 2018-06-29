package backend.devices;

import backend.core.ErrorProcessor;
import jssc.SerialPortException;

import java.io.IOException;

public class Power implements Connectable {

    private static Power power;
    private Connection connection;

    public Power(Connection connection) {
        this.connection = connection;
        power = this;
    }

    public static synchronized String sendCommand(String command) throws SerialPortException, IOException, InterruptedException //return String of Lockin reply
    {
        try {
            power.connection.sendMessage(command);
        } catch (SerialPortException e) {
            ErrorProcessor.standartError("Power connection problem", e);
            throw e;
        }

        return power.connection.getStringResponce();
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

    public static Power getPower() {
        return power;
    }

    public Connection getConnection() {
        return connection;
    }
}
