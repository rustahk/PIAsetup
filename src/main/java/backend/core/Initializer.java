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

    public static boolean fullInit() throws Exception {
        if(!initLogger()) return false;
        if(!initConfig()) return false;
        if(!initEngine()) return false;
        if(!initLockin()) return false;
        return true;
    }

    public static boolean fullClose() {
        try {
            engine_connection.disconnect();
            lockin_connection.disconnect();
            return true;
        } catch (SerialPortException e) {
            ErrorProcessor.standartError("fullClose ", e);
            return false;
        }
    }

    public static boolean initLogger() {
        try {
            FileManager.init();
            Logger.init();
        } catch (Exception e) {
            ErrorProcessor.standartError("Logger: FAIL", e);
            return false;
        }
        ServiceProcessor.serviceMessage("Logger: OK");
        return true;
    }

    public static boolean initConfig() {
        try {
            config = new Configurator();
            config.loadConfig();
        } catch (Exception e) {
            ErrorProcessor.standartError("Config: FAIL", e);
            return false;
        }
        ServiceProcessor.serviceMessage("Config: OK");
        return true;
    }

    public static boolean initEngine() throws SerialPortException {
        try {
            engine_connection = new Connection(config.getEngine_port(), config.getEngine_baud(), config.getEngine_databits(), config.getEngine_stopbit(), config.getEngine_parity(), 9, false, config.getEngine_delay());
            engine_connection.connect();
            Engine.init(engine_connection);
        } catch (Exception e) {
            ErrorProcessor.standartError("Engine: FAIL", e);
            return false;
        }
        ServiceProcessor.serviceMessage("Engine: OK");
        return true;

    }

    public static boolean initLockin() throws SerialPortException, NullPointerException {

        try {
            lockin_connection = new Connection(config.getLockin_port(), config.getLockin_baud(), config.getLockin_databits(), config.getLockin_stopbit(), config.getLockin_parity(), 0, true, config.getLockin_delay());
            lockin_connection.connect();
            Lockin.init(lockin_connection);
            Lockin.sendCommand(LockinStringCommands.setOutInterface());
        } catch (Exception e) {
            ErrorProcessor.standartError("Lockin: FAIL", e);
            return false;
        }
        ServiceProcessor.serviceMessage("Lockin: OK");
        return true;
    }
}
