package backend.core;

import backend.devices.Connection;
import backend.devices.Engine;
import backend.devices.Lockin;
import backend.devices.LockinStringCommands;
import backend.files.Configurator;
import backend.files.FileManager;
import backend.files.Logger;
import jssc.SerialPortException;

public class Initializer
{
    private Connection engine_connection;
    private Connection lockin_connection;
    private Configurator config;

    public Initializer() throws Exception
    {
            FileManager.init();
            Logger.init();
            config = new Configurator();
            config.loadConfig();
            engine_connection = new Connection(config.getEngine_port(), config.getEngine_baud(), config.getEngine_databits(), config.getEngine_stopbit(), config.getEngine_parity(), 9, false, config.getEngine_delay());
            lockin_connection = new Connection(config.getLockin_port(), config.getLockin_baud(), config.getLockin_databits(), config.getLockin_stopbit(), config.getLockin_parity(), 0, true, config.getLockin_delay());
            engine_connection.connect();
            lockin_connection.connect();
            Engine.init(engine_connection);
            Lockin.init(lockin_connection);
            Lockin.sendCommand(LockinStringCommands.setOutInterface());
    }

    public boolean close()
    {
        try
        {
            engine_connection.disconnect();
            lockin_connection.disconnect();
            return true;
        }
        catch (SerialPortException e)
        {
            ErrorProcessor.standartError("close ", e);
            return false;
        }
    }
}
