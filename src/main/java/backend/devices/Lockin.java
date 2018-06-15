package backend.devices;

public class Lockin {

    private static Connection connection;

    public static void init(Connection lockin)
    {
        connection=lockin;
    }

    public static String sendCommand(String command) { //return String of Lockin reply
        try {
            connection.sendMessage(command);
            return connection.getStringResponce();
        } catch (Exception e) {
            return null;
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
