package backend.files;

import backend.core.ErrorProcessor;
import backend.core.ServiceProcessor;

import java.io.*;
import java.util.Properties;

/*
Thiss class realize loading of the configuration file
 */

public class Configurator {
    private String engine_port;
    private Integer engine_baud;
    private Integer engine_databits;
    private Integer engine_stopbit;
    private Integer engine_parity;
    private Integer engine_delay;
    private String lockin_port;
    private Integer lockin_baud;
    private Integer lockin_databits;
    private Integer lockin_stopbit;
    private Integer lockin_parity;
    private Integer lockin_delay;

    private Properties props;
    private FileInputStream configin;
    private FileOutputStream configout;
    private File configfile;

    public void loadConfig() {
        configfile = new File(FileManager.mainfile.getAbsolutePath() + "\\config.ini");
        props = new Properties();
        try {
            configin = new FileInputStream(configfile);
            props.load(configin);
        } catch (FileNotFoundException e) {
            ErrorProcessor.standartError("Config file not found", e);
            loadDefaultValues();
            saveConfigFile();
        } catch (IOException e) {
            ErrorProcessor.standartError("Config file reading error ", e);
            loadDefaultValues();
            saveConfigFile();
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
            engine_port = props.getProperty("engine_port");
            engine_baud = Integer.valueOf(props.getProperty("engine_baud"));
            engine_databits = Integer.valueOf(props.getProperty("engine_databits"));
            engine_stopbit = Integer.valueOf(props.getProperty("engine_stopbit"));
            engine_parity = Integer.valueOf(props.getProperty("engine_parity"));
            engine_delay = Integer.valueOf(props.getProperty("engine_delay"));
            lockin_port = props.getProperty("lockin_port");
            lockin_baud = Integer.valueOf(props.getProperty("lockin_baud"));
            lockin_databits = Integer.valueOf(props.getProperty("lockin_databits"));
            lockin_stopbit = Integer.valueOf(props.getProperty("lockin_stopbit"));
            lockin_parity = Integer.valueOf(props.getProperty("lockin_parity"));
            lockin_delay = Integer.valueOf(props.getProperty("lockin_delay"));
        } catch (NullPointerException e) {
            ErrorProcessor.standartError("Config file is not correct", e);
            return false;
        } catch (NumberFormatException e) {
            ErrorProcessor.standartError("Config file is not correct", e);
            return false;
        }
        ServiceProcessor.serviceMessage("Config file has been loaded");
        return true;
    }

    private void loadDefaultValues() {
        ServiceProcessor.serviceMessage("Using default config");
        props.setProperty("engine_port", "COM4");
        props.setProperty("engine_baud", "9600");
        props.setProperty("engine_databits", "8");
        props.setProperty("engine_stopbit", "1");
        props.setProperty("engine_parity", "0");
        props.setProperty("engine_delay", "100");
        props.setProperty("lockin_port", "COM5");
        props.setProperty("lockin_baud", "9600");
        props.setProperty("lockin_databits", "8");
        props.setProperty("lockin_stopbit", "1");
        props.setProperty("lockin_parity", "0");
        props.setProperty("lockin_delay", "100");
    }

    private void rewriteConfigFile() {
        try {
            configin.close();
            configfile.delete();
            configfile.createNewFile();
            saveConfigFile();
        } catch (IOException e) {
            ErrorProcessor.standartError("fail to delete old config", e);
        }
    }

    private void saveConfigFile() {
        try {
            configout = new FileOutputStream(configfile);
            props.store(configout, "DEFAULT CONFIG");
            configout.close();
            ServiceProcessor.serviceMessage("New config file is saved");
        }  catch (Exception e) {
            ErrorProcessor.standartError("fail to create default config file ", e);
        }
    }

    public String getEngine_port() {
        return engine_port;
    }

    public Integer getEngine_baud() {
        return engine_baud;
    }

    public Integer getEngine_databits() {
        return engine_databits;
    }

    public Integer getEngine_stopbit() {
        return engine_stopbit;
    }

    public Integer getEngine_parity() {
        return engine_parity;
    }

    public Integer getEngine_delay() {
        return engine_delay;
    }

    public String getLockin_port() {
        return lockin_port;
    }

    public Integer getLockin_baud() {
        return lockin_baud;
    }

    public Integer getLockin_databits() {
        return lockin_databits;
    }

    public Integer getLockin_stopbit() {
        return lockin_stopbit;
    }

    public Integer getLockin_parity() {
        return lockin_parity;
    }

    public Integer getLockin_delay() {
        return lockin_delay;
    }
}
