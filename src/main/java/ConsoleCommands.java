import jssc.SerialPortException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ConsoleCommands {
    /*
    This class works like "interface" between low-level commands, calibration and connection;
    Also, it works like console reader;
    */
    private Connection engine;
    private Connection lockin;
    private EngineCommands engineCommands;
    private LockInCommands lockInCommands;
    private Calibration calibration;

    public ConsoleCommands(Connection engine, Connection lockin, EngineCommands engineCommands, LockInCommands lockInCommands, Calibration calibration) {
        this.engine = engine;
        this.engineCommands = engineCommands;
        this.calibration = calibration;
        this.lockin = lockin;
        this.lockInCommands = lockInCommands;
    }

    public void rotateRight() throws IOException {
        sendMotorCommand(engineCommands.rotateRight(rotateMotor()));
    }

    public void rotateLeft() throws IOException {
        sendMotorCommand(engineCommands.rotateLeft(rotateMotor()));
    }

    public void motorStop() {
        sendMotorCommand(engineCommands.motorStop());
    }

    public void moveToAbsPosition(boolean nm) throws IOException {
        sendMotorCommand(engineCommands.moveTo(enterPoint(nm).getPosition()));
    }

    public int getAbsPosition() {
        return sendMotorCommand(engineCommands.getPosition());
    }

    public int getSpeed() {
        return sendMotorCommand(engineCommands.getSpeed());
    }

    private int rotateMotor() throws IOException {
        int speed;
        while (true) {
            ConsoleOutput.unloggedMessage("SPEED (0..2047): ");
            speed = enterInt();
            if (calibration.speedLimit(speed)) return speed;
            else {
                ConsoleOutput.errorMessage("Speed out of range");
            }
        }
    }

    private int enterInt() throws IOException {
        int value;
        while (true) {
            try {
                value = Integer.parseInt(ConsoleInput.userEnter());
                break;
            } catch (NumberFormatException e) {
                ConsoleOutput.errorMessage("This is not an INT number");
            }
        }
        return value;
    } //Enter and check INT value

    private double enterDouble() throws IOException {
        double value;
        while (true) {
            try {
                value = Double.parseDouble(ConsoleInput.userEnter());
                break;
            } catch (NumberFormatException e) {
                ConsoleOutput.errorMessage("This is not an DOUBLE number");
            }
        }
        return value;
    } //Enter and check DOUBLE value

    private Point enterPoint(boolean nm) throws IOException //Enter new point
    {
        Point point;
        double inDouble;
        int inInt;

        while (true) {
            if (nm) {
                ConsoleOutput.unloggedMessage("POSITION (0..1000) nm: ");
                inDouble = enterDouble();
                point = new Point(calibration.positionCalc(inDouble), inDouble);
            } else {
                ConsoleOutput.unloggedMessage("POSITION (" + calibration.getMinwstep() + ".." + calibration.getMaxwstep() + "): ");
                inInt = enterInt();
                point = new Point(inInt, calibration.wavelenghtCalc(inInt));
            }
            if (calibration.positionLimit(point.getPosition(), false)) break; //check 0 and 1000nm
            else {
                ConsoleOutput.errorMessage("Position out of range");
            }
        }
        return point;
    }

    public Dataset standartScan() {
        int direction = 1;
        double scanstep;
        int numpoints;
        int delay; //delay in each point before measuring
        Point[] points;
        Point start;
        Point finish;
        try {
            //Enter parameters
            ConsoleOutput.unloggedMessage("From(nm): ");
            start = enterPoint(true);
            ConsoleOutput.unloggedMessage("To(nm): ");
            finish = enterPoint(true);
            ConsoleOutput.unloggedMessage("Number of points: ");
            numpoints = enterInt();
            points = new Point[numpoints+1];
            ConsoleOutput.unloggedMessage("Delay(ms): ");
            delay = enterInt();
            if (start.getWavelenght() > finish.getWavelenght()) direction = -1;
            scanstep = Math.abs(finish.getWavelenght() - start.getWavelenght()) / numpoints;
            Dataset scan = new Dataset(points, new Date(), delay, scanstep);
            for (int i = 0; i <= numpoints; i++) {
                points[i]=scanPoint(start.getWavelenght() + (scanstep * i * direction), delay);
            }
            scan.setFinishtime(new Date());
            ConsoleOutput.serviceMessage("Scan finished");
            return scan;

        } catch (Exception e) {
            ConsoleOutput.errorMessage("Scan error " + e.toString());
            return null;
        }
    } //Start scaning procedure

    private Point scanPoint(double nm, int delay) throws InterruptedException
    {
        int p = calibration.positionCalc(nm);
        Point  point = new Point(p, nm);
        sendMotorCommand(engineCommands.moveTo(p));
        waitMoving();
        Thread.sleep(delay);
        point.setValue(sendLockinCommand(lockInCommands.getOutputX()));
        ConsoleOutput.unloggedMessage(point.getWavelenght() + "nm, " + point.getValue());
        return point;
    }

    public void startCalibration() throws IOException, SerialPortException {
        double wavelenght;
        ConsoleOutput.serviceMessage("Calibration");
        ConsoleOutput.unloggedMessage("Warning! Wrong value might damage mechanical system!");
        ConsoleOutput.unloggedMessage("Enter current position (nm)");
        while (true) {
            wavelenght = enterDouble();
            if (calibration.positionLimit(wavelenght, true)) break;
            else {
                ConsoleOutput.errorMessage("WARNING! THE VALUE IS OUT OF SAFETY LIMITS!");
                ConsoleOutput.errorMessage("SWTICH OFF THE DEVICE TO RECALIBRATE MANUALY");
                //*Add exit here*
            }
        }
        sendMotorCommand(engineCommands.setPosition(calibration.positionCalc(wavelenght)));
        sendMotorCommand(engineCommands.motorStop());
        ConsoleOutput.serviceMessage("Motor position updated");
    } //Start calibration procedure

    private int sendMotorCommand(int[] command) {
        int[] reply;
        try {
            engine.sendMessage(command);
            reply = engine.getByteResponce(engine.getResponcedelay());
            if (engineCommands.commandStatus(command, reply)) return engineCommands.getValue(reply);
            throw new IOException();
        } catch (Exception e) {
            ConsoleOutput.errorMessage("Reply wrong " + e.toString()); //add new exception, неоднозачность
            return 0; //Change, it's not safety
        }
    } //Send a command to the motor

    private String sendLockinCommand(String command) { //return String of LockIn reply
        try {
            lockin.sendMessage(command);
            return lockin.getStringResponce(engine.getResponcedelay());
        } catch (Exception e) {
            ConsoleOutput.errorMessage(e.toString()); //add new exception
            return null; //Change, it's not safety
        }
    } //Send a command to the lockin

    private void waitMoving() throws InterruptedException {
        while (true) {
            if (sendMotorCommand(engineCommands.getSpeed()) == 0) break;
            TimeUnit.MILLISECONDS.sleep(250);
        }
    } //Waiting until motor will stop move
}
