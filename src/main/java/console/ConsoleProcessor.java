package console;

import backend.core.ErrorProcessor;
import backend.core.LogicCommands;
import backend.data.Point;
import backend.devices.Calibration;

/*
this class realize console.ConsoleProcessor interface
 */
//Would be good to rewrite
public class ConsoleProcessor {
    private static String bufferline;
    private static ConsoleOutput output;

    public static void mainmenu(ConsoleOutput out) {
        output = out;
        String line;
        welcomemessage();
        calibr();
        try {
            while (true) {
                output.unloggedMessage("[Main menu]");
                line = ConsoleInput.userEnter();
                if (line.equals("quit")) {
                    quit();
                    break;
                } else if (line.equals("help")) {
                    helpmain();
                } else if (line.equals("control")) {
                    control();
                } else if (line.equals("scan")) {
                    scan();
                } else if (line.equals("calibr")) {
                    calibr();
                } else output.unloggedMessage("Unknown command");
            }
        } catch (Exception e) {
            ErrorProcessor.standartError("console", e);
        }
    }

    private static void welcomemessage() {
        output.serviceMessage("PIA setup console is online");
    }

    private static void helpmain() {
        output.unloggedMessage("Commands list:");
        output.unloggedMessage(" 'calibr' to start calibration ");
        output.unloggedMessage(" 'scan' to start scan ");
        output.unloggedMessage(" 'control' to activate manual control");
        output.unloggedMessage(" 'quit' to fullClose the program ");
    }

    private static void helpcontrol() {
        output.unloggedMessage("Manual control commands:");
        output.unloggedMessage(" 'back' to reutrn to main menu ");
    }

    private static void quit() {

    }

    private static void control() {
        boolean nm = true;
        output.unloggedMessage("[Manual control]");
        output.unloggedMessage("default UNIT: nm");
        try {

            while (true) {
                bufferline = ConsoleInput.userEnter();
                if (bufferline.equals("back")) {
                    break;
                } else if (bufferline.equals("unit")) {
                    nm = !nm;
                    if (nm) output.unloggedMessage("UNIT: nm");
                    else {
                        output.unloggedMessage("UNIT: steps");
                    }
                } else if (bufferline.equals("left")) {
                    //consoleCommands.rotateLeft();
                } else if (bufferline.equals("right")) {
                    //consoleCommands.rotateRight();
                } else if (bufferline.equals("stop")) {
                    //consoleCommands.motorStop();
                } else if (bufferline.equals("getpos")) {
                    //ConsoleOutput.unloggedMessage("Position " + consoleCommands.getAbsPosition());
                } else if (bufferline.equals("moveto")) {
                    //consoleCommands.moveToAbsPosition(nm);
                } else if (bufferline.equals("help")) {
                    helpcontrol();
                } else if (bufferline.equals("speed")) {
                    //ConsoleOutput.unloggedMessage("Speed " + consoleCommands.getSpeed());
                } else output.unloggedMessage("Unknown command");
            }
        } catch (Exception e) {
            ErrorProcessor.standartError("console", e);
        }
    }

    private static void scan() {

        output.unloggedMessage("[Scan mode]");
        Point start;
        Point finish;
        int delay;
        int numpoints;
        output.unloggedMessage("From(nm): ");
        start = enterPoint(true);
        output.unloggedMessage("To(nm): ");
        finish = enterPoint(true);
        output.unloggedMessage("Number of points: ");
        numpoints = enterInt();
        output.unloggedMessage("Delay(ms): ");
        delay = enterInt();
        LogicCommands.saveScan(LogicCommands.startScan(start, finish, numpoints, delay));
    }

    private static void calibr()
    {
        LogicCommands.calibratePosition(enterPoint(true));
    }
    private static int enterInt()
    {
        int value;
        while (true) {
            try {
                value = Integer.parseInt(ConsoleInput.userEnter());
                break;
            } catch (NumberFormatException e) {
                output.unloggedMessage("This is not an INT");
            }
        }
        return value;
    } //Enter and check INT value

    private static double enterDouble() {
        double value;
        while (true) {
            try {
                value = Double.parseDouble(ConsoleInput.userEnter());
                break;
            } catch (NumberFormatException e) {
                output.unloggedMessage("This is not a DOUBLE");
            }
        }
        return value;
    } //Enter and check DOUBLE value

    private static Point enterPoint(boolean nm) //Enter and check safe limits of point
    {
        Point point;
        double inDouble;
        int inInt;

        while (true) {
            if (nm) {
                output.unloggedMessage("POSITION (0..1000) nm: ");
                inDouble = enterDouble();
                point = new Point(inDouble);
            } else {
                output.unloggedMessage("POSITION (" + Calibration.getMinwstep() + ".." + Calibration.getMaxwstep() + "): ");
                inInt = enterInt();
                point = new Point(Calibration.positionCalc(inInt),inInt);
            }
            if (Calibration.positionLimit(point.getPosition(), false)) break; //check 0 and 1000nm
            else {
                output.unloggedMessage("Position out of range");
            }
        }
        return point;
    }
}