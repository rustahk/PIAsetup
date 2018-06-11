public class LockInCommands {
/*
This class realize ASCII strings to Lock-In commands

There is no checksum!

*/

    public String setOutInterface() {
        return "OUTX 0";
    }

    public String autoGain() {
        return "AGAN";
    }

    public String autoPhase() {
        return "APHS";
    }

    public String getOutputX() {
        return getOutputValue() + "1";
    }

    private String getOutputValue() {
        return "OUTP ? ";
    }
}
