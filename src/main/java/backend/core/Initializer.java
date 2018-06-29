package backend.core;

import backend.devices.*;
import backend.files.Configurator;
import backend.files.FileManager;
import backend.files.Logger;
import gui.MainMenu;

import java.util.Date;

public class Initializer {

    private static Configurator config;

    public static void fullInit() throws Exception {
        initLogger();
        initConfig();
        initCalibr();
        initEngine();
        initLockin();
        initPower();
        initLaser();
        initLamp();
        initChopper();
    }

    public static void fullClose() {
        //$
        /*
        try {
            Laser.sendCommand(LaserCommands.closeShutter());
        } catch (Exception e) {
            ErrorProcessor.standartError("Fail to close laser shutter", e);
        }
        try {
            Laser.sendCommand(LaserCommands.switchOff());
        } catch (Exception e) {
            ErrorProcessor.standartError("Fail to activate laser stand-by mode", e);
        }
        */
        try {
            Power.sendCommand(PowerSupplyCommands.switchOff());
        } catch (Exception e) {
            ErrorProcessor.standartError("Fail to switch off power output", e);
        }
        Connection.cleanConnections();
        ServiceProcessor.serviceMessage(FileManager.getDateTimeStamp(new Date()) + " #Session finish");
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
            new Engine(config.getEngine_connection());
            Engine.getEngine().connect();
        } catch (Exception e) {
            ErrorProcessor.standartError("Engine: FAIL", e);
            MainMenu.errorMessage("Connection", "Engine connection failed. Try to connect manually", e.toString(), e);
            if (MainMenu.getConnectionMenu().restartEngineConnection()) {
                //Nothing
            } else throw e;
        }
        ServiceProcessor.serviceMessage("Engine: OK");
    }

    public static void initLockin() throws Exception {

        try {
            new Lockin(config.getLockin_connection());
            Lockin.getLockin().connect();
            Lockin.sendCommand(LockinCommands.setOutInterface());
        } catch (Exception e) {
            MainMenu.errorMessage("Connection", "Lockin connection failed. Try to connect manually", e.toString(), e);
            ErrorProcessor.standartError("Lockin: FAIL", e);
            if (MainMenu.getConnectionMenu().restartLockinConnection()) {
                //Nothing
            } else throw e;
        }
        ServiceProcessor.serviceMessage("Lockin: OK");
    }

    public static void initPower() throws Exception {

        try {
            new Power(config.getPower_connection());
            Power.getPower().connect();
        } catch (Exception e) {
            MainMenu.errorMessage("Connection", "Power connection failed. Try to connect manually", e.toString(), e);
            ErrorProcessor.standartError("Power: FAIL", e);
            if (MainMenu.getConnectionMenu().restartPowerConnection()) {
                //Nothing
            } else throw e;
        }
        ServiceProcessor.serviceMessage("Power: OK");
    }

    public static void initLaser() throws Exception {

        try {
            new Laser(config.getLaser_connection());
            Laser.getLaser().connect();
        } catch (Exception e) {
            MainMenu.errorMessage("Connection", "Laser connection failed. Try to connect manually", e.toString(), e);
            ErrorProcessor.standartError("Laser: FAIL", e);
            if (MainMenu.getConnectionMenu().restartLaserConnection()) {
                //Nothing
            } else throw e;
        }
        ServiceProcessor.serviceMessage("Laser: OK");
    }

    public static void initCalibr() throws Exception {
        try
        {
            Calibration.init(config.getCalibration());
            ServiceProcessor.serviceMessage("Limits: OK");
        }
        catch (Exception e)
        {
            ErrorProcessor.standartError("Limits: FAIL", e);
        }
    }

    public static void initLamp() throws Exception {

        try {
            new Lamp(config.getLamp());
        } catch (Exception e) {
            MainMenu.errorMessage("Connection", "Lamp connection failed", e.toString(), e);
            ErrorProcessor.standartError("Lamp: FAIL", e);
            if (MainMenu.getConnectionMenu().restartLockinConnection()) {
                //Nothing
            } else throw e;
        }
        ServiceProcessor.serviceMessage("Lamp: OK");
    }

    public static void initChopper() throws Exception {

        try {
            new Chopper(config.getChopper());
        } catch (Exception e) {
            MainMenu.errorMessage("Connection", "Chopper connection failed", e.toString(), e);
            ErrorProcessor.standartError("Chopper: FAIL", e);
            if (MainMenu.getConnectionMenu().restartLockinConnection()) {
                //Nothing
            } else throw e;
        }
        ServiceProcessor.serviceMessage("Chopper: OK");
    }

    public static Configurator getConfig() {
        return config;
    }
}
