import jssc.SerialPortException;

public class Main {

    public static void main(String[] args)
    {
        DataStorage storage = new DataStorage("C:\\PIAsetup");
        ConsoleOutput.setLogging(storage);
        Connection engine = new Connection("COM4", 9600, 8, 1, 0, 9, false, 100);
        Connection lockin = new Connection("COM5", 9600, 8,1,0, 0, true, 100);
        EngineCommands engineCommands = new EngineCommands();
        LockInCommands lockInCommands = new LockInCommands();
        Calibration calibration = new Calibration();
        ConsoleCommands consoleCommands = new ConsoleCommands(engine, lockin, engineCommands, lockInCommands, calibration);
        try
        {

            Console console = new Console(consoleCommands, storage);
            engine.connect();
            lockin.connect();
            lockin.sendMessage(lockInCommands.setOutInterface());
            console.startMode();
        } catch (Exception e) {
            ConsoleOutput.errorMessage(e.toString());
        }
        finally {
            try {
                engine.disconnect();
                lockin.disconnect();
            }
            catch (SerialPortException e)
            {
                ConsoleOutput.errorMessage(e.toString());
            }
        }
        ConsoleOutput.serviceMessage("Programm closed");
    }


}
