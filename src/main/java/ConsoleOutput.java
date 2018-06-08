public class ConsoleOutput
{
    private static DataStorage storage;

    public  static void setLogging(DataStorage storage)
    {
        ConsoleOutput.storage = storage;
    }

    public static synchronized void errorMessage(String error) {
        sendMessage("[ERROR]: " + error, true);
    }

    public static synchronized void serviceMessage(String msg) {
        sendMessage("[SYSTEM]: " + msg, true);
    }
    public static synchronized void userCommand(String command) {
        storage.addtoLog("[USER]: " + command);
    }

    public static synchronized void unloggedMessage(String msg) {
        sendMessage(msg, false);
    }
    private static synchronized void sendMessage(String msg, boolean logit)
    {
        System.out.println(msg);
        try {
            if (logit) storage.addtoLog(msg);
        }
        catch (NullPointerException e)
        {
            System.out.println("[UNLOGGED ERROR]: " + e + " " + msg);
        }
    }
}
