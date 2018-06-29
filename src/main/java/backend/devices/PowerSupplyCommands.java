package backend.devices;

public class PowerSupplyCommands {

    public static String lockPanel()
    {
        return "LOCK1";
    }
    public static String unlockPanel()
    {
        return "LOCK0";
    }
    public static String setCurrent(int channel, double current)
    {
        return "ISET"+channel+":"+floatFormat(current);
    }
    public static String getCurrentParameter(int channel)
    {
        return "ISET"+channel+"?";
    }
    public static String setVoltage(int channel, double voltage)
    {
        return "VSET"+channel+":"+floatFormat(voltage);
    }
    public static String getVoltageParameter(int channel)
    {
        return "VSET"+channel+"?";
    }
    public static String getCurrentValue(int channel)
    {
        return "IOUT"+channel+"?";
    }
    public static String getVoltageValue(int channel)
    {
        return "VOUT"+channel+"?";
    }
    public static String switchOn()
    {
        return "OUT1";
    }
    public static String switchOff()
    {
        return "OUT0";
    }
    public static String getStatusByte()
    {
        return "STATUS?";
    }
    public static String getID()
    {
        return "*IDN?";
    }
    public static String setIndepChannels()
    {
        return "TRACK0";
    }
    public static String setCurrentProtection(int channel, double current)
    {
        return "OCPSTE"+channel+":"+floatFormat(current);
    }
    public static String setVoltageProtection(int channel, double voltage)
    {
        return "OVPSTE"+channel+":"+floatFormat(voltage);
    }
    private static String floatFormat(double value)
    {
        return String.format("%2.2f", value);
    }
}
