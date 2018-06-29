package backend.devices;

import backend.core.ErrorProcessor;
import jssc.SerialPortException;

import java.io.IOException;

public class Laser implements Connectable {

    private static Laser laser;
    private Connection connection;

    public Laser(Connection connection) {
        this.connection = connection;
        laser = this;
    }

    public static synchronized String sendCommand(String command) throws SerialPortException, IOException, InterruptedException //return String of Lockin reply
    {
        try {
            laser.connection.sendMessage(command);
        } catch (SerialPortException e) {
            ErrorProcessor.standartError("Laser connection problem", e);
            throw e;
        }
        return laser.connection.getStringResponce();
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

    public static Laser getLaser() {
        return laser;
    }

    public Connection getConnection() {
        return connection;
    }

    public static String askID() throws Exception {
        int[] i = new int[5];
        i[0] = 0x3f;
        i[1] = 0x49;
        i[2] = 0x44;
        i[3] = 0x4e;
        i[4] = 0x0d;
        laser.connection.sendMessage(i);
        return laser.connection.getStringResponce();
    }
}