package backend.devices;

public class LockinStringCommands {
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
}
