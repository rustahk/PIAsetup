package backend.devices;

public class LaserCommands {
    private static String floatFormat(double value) {
        return String.format("%2.2f", value);
    }

    public static String switchOn() {
        return "ON";
    }

    public static String switchOff() {
        return "OFF";
    }

    public static String setPower(double power) {
        return "P:" + floatFormat(power);
    }

    public static String openShutter() {
        return "SHUTTER:1";
    }

    public static String closeShutter() {
        return "SHUTTER:0";
    }

    public static String getWarmup() {
        return "?WARMUP%";
    }

    public static String getID() {
        return "?IDN";
    }

    public static String getOptimizationStatus() {
        return "OPTIMO?";
    }

    public static String getPower() {
        return "?P";
    }

    public static String getShutterStatus() {
        return "?SHUTTER";
    }

    public static String getStatusByte() {
        return "?STB";
    }
}
