package backend.core;

import backend.devices.Connection;
import backend.devices.Engine;
import backend.devices.Lockin;
import backend.devices.LockinStringCommands;
import backend.files.Configurator;
import backend.files.FileManager;
import backend.files.Logger;
import jssc.SerialPortException;

public class Initializer {
    private static Connection engine_connection;
    private static Connection lockin_connection;
    private static Configurator config;

    public static void fullInit() throws Exception {
        initLogger();
        initConfig();
        initEngine();
        initLockin();
    }

    public static void fullClose() {

        if(engine_connection!=null){
            try {
                engine_connection.disconnect();
            } catch (Exception e) {
                ErrorProcessor.standartError(engine_connection.getPortname() + " not closed", e);
            }
        }
        if(lockin_connection!=null){
            try {
                lockin_connection.disconnect();
            } catch (Exception e) {
                ErrorProcessor.standartError(lockin_connection.getPortname() + " not closed", e);
            }
        }

    }

    public static void initLogger() throws Exception {
        try {
            FileManager.init();
            Logger.init();
        } catch (Exception e) {
            ErrorProcessor.standartError("Logger: FAIL", e);
            throw e;
        }
        ServiceProcessor.serviceMessage("Logger: OK");
    }

    public static void initConfig() throws Exception {
        try {
            config = new Configurator();
            config.loadConfig();
        } catch (Exception e) {
            ErrorProcessor.standartError("Config: FAIL", e);
            throw e;
        }
        ServiceProcessor.serviceMessage("Config: OK");
    }

    public static void initEngine() throws Exception {
        try {
            engine_connection = new Connection(config.getEngine_port(), config.getEngine_baud(), config.getEngine_databits(), config.getEngine_stopbit(), config.getEngine_parity(), 9, false, config.getEngine_delay());
            Engine.init(engine_connection);
            engine_connection.connect();
        } catch (Exception e) {
            ErrorProcessor.standartError("Engine: FAIL", e);
            throw e;
        }
        ServiceProcessor.serviceMessage("Engine: OK");
    }

    public static void initLockin() throws Exception {

        try {
            lockin_connection = new Connection(config.getLockin_port(), config.getLockin_baud(), config.getLockin_databits(), config.getLockin_stopbit(), config.getLockin_parity(), 0, true, config.getLockin_delay());
            Lockin.init(lockin_connection);
            lockin_connection.connect();
            Lockin.sendCommand(LockinStringCommands.setOutInterface());
        } catch (Exception e) {
            ErrorProcessor.standartError("Lockin: FAIL", e);
            throw e;
        }
        ServiceProcessor.serviceMessage("Lockin: OK");
    }
}
