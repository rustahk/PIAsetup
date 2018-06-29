package backend.devices;

public class LockinCommands {
/*
This class realize ASCII strings to Lock-In commands

There is no checksum!

*/

    public static String setOutInterface() {
        return "OUTX 0";
    }

    public static String autoGain() {
        return "AGAN";
    }

    public static String autoPhase() {
        return "APHS";
    }

    public static String getOutputX() {
        return getOutputValue() + "1";
    }

    private static String getOutputValue() {
        return "OUTP ? ";
    }

    public static String getOutputXY() {
        return "SNAP?1,2";
    }

    public static String reset() {
        return "*RST";
    }

    public static String getID() {
        return "*IDN?";
    }

    public static String lockPanel() {
        return "LOCL 1";
    }

    public static String unlockPanel() {
        return "LOCL 0";
    }

    public static String getRefFreq() {
        return "SNAP?9";
    }
}
