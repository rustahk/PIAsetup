package backend.devices;

import jssc.SerialPortException;

public interface Connectable {

    void cleanInputBuffer();

    void connect() throws SerialPortException;

    void reconnect(Connection connection) throws SerialPortException;

}
