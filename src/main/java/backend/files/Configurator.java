package backend.files;

import backend.core.ErrorProcessor;
import backend.core.ServiceProcessor;
import backend.devices.*;

import java.io.*;
import java.util.Properties;

/*
Thiss class realize loading of the configuration file
 */

public class Configurator {

    private Properties props;
    private FileInputStream configin;
    private FileOutputStream configout;
    private File configfile;
    private Connection engine_connection;
    private Connection lockin_connection;
    private Connection power_connection;
    private Connection laser_connection;
    private Calibration calibration;
    private PowerChannel lamp;
    private PowerChannel chopper;

    public void loadConfig() {
        configfile = new File(FileManager.mainfile.getAbsolutePath() + "\\config.ini");
        props = new Properties();
        try {
            configin = new FileInputStream(configfile);
            props.load(configin);
            configin.close();
        } catch (FileNotFoundException e) {
            ErrorProcessor.standartError("Config file not found", e);
            loadDefaultValues();
        } catch (IOException e) {
            ErrorProcessor.standartError("Config file reading error ", e);
            loadDefaultValues();
        }
        if (!readConfigValues())//If config has been damaged, it's loading default and try again
        {
            loadDefaultValues();
            rewriteConfigFile();
            readConfigValues();
        }
    }

    public boolean readConfigValues() {
        try {
            calibration = loadCalibrConfig();
            engine_connection = loadDeviceConfig("engine");
            lockin_connection = loadDeviceConfig("lockin");
            power_connection = loadDeviceConfig("power");
            laser_connection = loadDeviceConfig("laser");
            lamp = loadPowerChannelConfig("lamp");
            chopper = loadPowerChannelConfig("chopper");
        } catch (NullPointerException e) {
            ErrorProcessor.standartError("Config file is not correct ", e);
            return false;
        } catch (NumberFormatException e) {
            ErrorProcessor.standartError("Config file is not correct ", e);
            return false;
        }
        return true;
    }

    private PowerChannel loadPowerChannelConfig(String device_name) {
        return new PowerChannel(
                getIntegerProp(device_name + "_channel"),
                getDoubleProp(device_name + "_v_min"),
                getDoubleProp(device_name + "_v_max"),
                getDoubleProp(device_name + "_i_min"),
                getDoubleProp(device_name + "_i_max")
        );
    }

    private Calibration loadCalibrConfig() {
        return new Calibration(
                getIntegerProp("min_work_step"),
                getIntegerProp("max_work_step"),
                getDoubleProp("min_work_wave"),
                getDoubleProp("max_work_wave"),
                getIntegerProp("min_safe_step"),
                getIntegerProp("max_safe_step"),
                getDoubleProp("min_safe_wave"),
                getDoubleProp("max_safe_wave"));
    }

    private Connection loadDeviceConfig(String device_name) throws NullPointerException, NumberFormatException {
        return new Connection(
                getStringProp(device_name + "_port"),
                getIntegerProp(device_name + "_baud"),
                getIntegerProp(device_name + "_databits"),
                getIntegerProp(device_name + "_stopbit"),
                getIntegerProp(device_name + "_parity"),
                getIntegerProp(device_name + "_msgsize"),
                getBooleanProp(device_name + "_stringResponce"),
                getIntegerProp(device_name + "_delay")
        );
    }

    private int getIntegerProp(String key) throws NullPointerException, NumberFormatException {
        try {
            return Integer.valueOf(props.getProperty(key));
        } catch (NullPointerException e) {
            ErrorProcessor.standartError("Parameter " + key + " not found", e);
            throw new NullPointerException("Parameter " + key + " is null");
        } catch (NumberFormatException e) {
            ErrorProcessor.standartError("Parameter " + key + " is not correct", e);
            throw new NullPointerException("Parameter " + key + " wrong");
        }
    }

    private double getDoubleProp(String key) throws NullPointerException, NumberFormatException {
        try {
            return Double.valueOf(props.getProperty(key));
        } catch (NullPointerException e) {
            ErrorProcessor.standartError("Parameter " + key + " not found", e);
            throw new NullPointerException("Parameter " + key + " is null");
        } catch (NumberFormatException e) {
            ErrorProcessor.standartError("Parameter " + key + " is not correct", e);
            throw new NullPointerException("Parameter " + key + " wrong");
        }
    }

    private String getStringProp(String key) throws NullPointerException {
        try {
            return props.getProperty(key);
        } catch (NullPointerException e) {
            ErrorProcessor.standartError("Parameter " + key + " not found", e);
            throw new NullPointerException("Parameter " + key + " is null");
        }
    }

    private boolean getBooleanProp(String key) throws NullPointerException {
        try {
            return Boolean.valueOf(props.getProperty(key));
        } catch (NullPointerException e) {
            ErrorProcessor.standartError("Parameter " + key + " not found", e);
            throw new NullPointerException("Parameter " + key + " is null");
        }
    }

    private void loadDefaultValues() {
        ServiceProcessor.serviceMessage("Using default config");
        defaultEngine();
        defaultLockin();
        defaultPower();
        defaultLaser();
        defaultCalibration();
        defaultPowerChannelConfig();
        saveConfigFile("DEFAULT CONFIG");
    }

    private void defaultLockin() {
        defaultConnection("lockin", 19200, 8, 1, 0, 0, true, 75);
    }

    private void defaultEngine() {
        defaultConnection("engine", 9600, 8, 1, 0, 9, false, 25);
    }

    private void defaultPower() {
        defaultConnection("power");
    }

    private void defaultLaser() {
        defaultConnection("laser");
    }

    private void defaultConnection(String name) {
        props.setProperty(name + "_port", "COM0"); //Must be configed by user
        props.setProperty(name + "_baud", "9600");
        props.setProperty(name + "_databits", "8");
        props.setProperty(name + "_stopbit", "1");
        props.setProperty(name + "_parity", "0");
        props.setProperty(name + "_delay", "100");
        props.setProperty(name + "_msgsize", "0");
        props.setProperty(name + "_stringResponce", "true");
    }

    private void defaultConnection(String name, int baud, int databits, int stopbit, int parity, int msgsize, boolean stringResponce, int delay) {
        props.setProperty(name + "_port", "COM0"); //Must be configed by user
        props.setProperty(name + "_baud", "" + baud);
        props.setProperty(name + "_databits", "" + databits);
        props.setProperty(name + "_stopbit", "" + stopbit);
        props.setProperty(name + "_parity", "" + parity);
        props.setProperty(name + "_delay", "" + delay);
        props.setProperty(name + "_msgsize", "" + msgsize);
        props.setProperty(name + "_stringResponce", "" + stringResponce);
    }

    private void defaultCalibration() {
        props.setProperty("min_work_step", "0");
        props.setProperty("max_work_step", "12799250");
        props.setProperty("max_work_wave", "1000");
        props.setProperty("min_work_wave", "0");
        props.setProperty("min_safe_step", "-1035257");
        props.setProperty("max_safe_step", "13598380");
        props.setProperty("max_safe_wave", "1081.6");
        props.setProperty("min_safe_wave", "-6.1");
    }

    private void defaultPowerChannelConfig() {
        //
        props.setProperty("chopper_channel", "2");
        props.setProperty("chopper_v_min", "0");
        props.setProperty("chopper_v_max", "15");
        props.setProperty("chopper_i_min", "0");
        props.setProperty("chopper_i_max", "0.05");
        //
        props.setProperty("lamp_channel", "1");
        props.setProperty("lamp_v_min", "0");
        props.setProperty("lamp_v_max", "2.5");
        props.setProperty("lamp_i_min", "0");
        props.setProperty("lamp_i_max", "2.5");
    }

    public void updateDeviceConfig(String name, String port, int baud_rate, int data_bits, int stopbit, int parity, int delay) throws FileNotFoundException, IOException {
        props.setProperty(name + "_port", port);
        props.setProperty(name + "_baud", "" + baud_rate);
        props.setProperty(name + "_databits", "" + data_bits);
        props.setProperty(name + "_stopbit", "" + stopbit);
        props.setProperty(name + "_parity", "" + parity);
        props.setProperty(name + "_delay", "" + delay);
        saveConfigFile("USER CONFIG");
    }

    private void rewriteConfigFile() {
        try {
            configin.close();
            configfile.delete();
            configfile.createNewFile();
            loadDefaultValues();
        } catch (IOException e) {
            ErrorProcessor.standartError("fail to delete old config", e);
        }
    }

    private void saveConfigFile(String comment) {
        try {
            configout = new FileOutputStream(configfile);
            props.store(configout, comment);
            configout.close();
            ServiceProcessor.serviceMessage("New config file is saved");
        } catch (Exception e) {
            ErrorProcessor.standartError("fail to create/update config file ", e);
        }
    }

    public Connection getEngine_connection() {
        return engine_connection;
    }

    public Connection getLockin_connection() {
        return lockin_connection;
    }

    public Connection getPower_connection() {
        return power_connection;
    }

    public Connection getLaser_connection() {
        return laser_connection;
    }

    public Calibration getCalibration() {
        return calibration;
    }

    public PowerChannel getLamp() {
        return lamp;
    }

    public PowerChannel getChopper() {
        return chopper;
    }
}
