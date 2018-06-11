import jssc.SerialPortException;

public class Main {

    public static void main(String[] args) {
        String maindir = "C:\\PIAsetup";
        DataStorage storage = new DataStorage(maindir);
        ConsoleOutput.setLogging(storage);
        Config config = new Config(maindir);
        config.loadConfig();
        Connection engine = new Connection(config.getEngine_port(), config.getEngine_baud(), config.getEngine_databits(), config.getEngine_stopbit(), config.getEngine_parity(), 9, false, config.getEngine_delay());
        Connection lockin = new Connection(config.getLockin_port(), config.getLockin_baud(), config.getLockin_databits(), config.getLockin_stopbit(), config.getLockin_parity(), 0, true, config.getLockin_delay());
        EngineCommands engineCommands = new EngineCommands();
        LockInCommands lockInCommands = new LockInCommands();
        Calibration calibration = new Calibration();
        ConsoleCommands consoleCommands = new ConsoleCommands(engine, lockin, engineCommands, lockInCommands, calibration, storage);
        try {
            Console console = new Console(consoleCommands, storage);
            engine.connect();
            lockin.connect();
            lockin.sendMessage(lockInCommands.setOutInterface());
            console.startMode();
        } catch (Exception e) {
            ConsoleOutput.errorMessage(e.toString());
        } finally {
            try {
                engine.disconnect();
                lockin.disconnect();
            } catch (SerialPortException e) {
                ConsoleOutput.errorMessage(e.toString());
            }
        }
        ConsoleOutput.serviceMessage("Programm closed");
    }
}
