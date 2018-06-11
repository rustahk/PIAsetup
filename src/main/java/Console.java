//Would be good to rewrite
public class Console {
    private ConsoleCommands consoleCommands;
    private DataStorage dataStorage;
    private String bufferline;

    public Console(ConsoleCommands consoleCommands, DataStorage dataStorage) {
        this.consoleCommands = consoleCommands;
        this.dataStorage = dataStorage;
    }

    public void startMode() {
        String line;
        welcomemessage();
        calibr();

        try {
            while (true) {
                line = ConsoleInput.userEnter();
                if (line.equals("quit")) {
                    quit();
                    break;
                } else if (line.equals("help")) {
                    helpmain();
                } else if (line.equals("control")) {
                    control();
                    ConsoleOutput.unloggedMessage("Main menu");
                } else if (line.equals("scan")) {
                    scan();
                    ConsoleOutput.unloggedMessage("Main menu");
                } else if (line.equals("calibr")) {
                    calibr();
                    ConsoleOutput.unloggedMessage("Main menu");

                } else System.out.println("Unknown command");
            }
        } catch (Exception e) {
            ConsoleOutput.errorMessage(e.toString());
        }
    }

    private void welcomemessage() {
        ConsoleOutput.serviceMessage("PIA setup is online");
    }

    private void helpmain() {
        ConsoleOutput.unloggedMessage("Commands list:");
        ConsoleOutput.unloggedMessage(" 'calibr' to start calibration ");
        ConsoleOutput.unloggedMessage(" 'scan' to start scan ");
        ConsoleOutput.unloggedMessage(" 'control' to activate manual control");
        ConsoleOutput.unloggedMessage(" 'quit' to close the program ");
    }

    private void helpcontrol() {
        ConsoleOutput.unloggedMessage("Manual control commands:");
        ConsoleOutput.unloggedMessage(" 'back' to reutrn to main menu ");
    }

    private void quit() {

    }

    private void control() {
        boolean nm = true;
        ConsoleOutput.unloggedMessage("Manual control mode is active");
        ConsoleOutput.unloggedMessage("default UNIT: nm");
        try {

            while (true) {
                bufferline = ConsoleInput.userEnter();
                if (bufferline.equals("back")) {
                    break;
                } else if (bufferline.equals("unit")) {
                    nm = !nm;
                    if (nm) ConsoleOutput.unloggedMessage("UNIT: nm");
                    else {
                        ConsoleOutput.unloggedMessage("UNIT: steps");
                    }
                } else if (bufferline.equals("left")) {
                    consoleCommands.rotateLeft();
                } else if (bufferline.equals("right")) {
                    consoleCommands.rotateRight();
                } else if (bufferline.equals("stop")) {
                    consoleCommands.motorStop();
                } else if (bufferline.equals("getpos")) {
                    ConsoleOutput.unloggedMessage("Position " + consoleCommands.getAbsPosition());
                } else if (bufferline.equals("moveto")) {
                    consoleCommands.moveToAbsPosition(nm);
                } else if (bufferline.equals("help")) {
                    helpcontrol();
                } else if (bufferline.equals("speed")) {
                    ConsoleOutput.unloggedMessage("Speed " + consoleCommands.getSpeed());
                } else System.out.println("Unknown command");
            }
        } catch (Exception e) {
            ConsoleOutput.errorMessage(e.toString());
        }
    }

    private void scan() {
        ConsoleOutput.unloggedMessage("Scan mode is active");
        try {
            dataStorage.saveScan(consoleCommands.standartScan());

        } catch (Exception e) {
            ConsoleOutput.errorMessage("SCAN FAIL: " + e.toString());
        }
    }

    private void calibr() {
        try {
            consoleCommands.startCalibration();
        } catch (Exception e) {
            ConsoleOutput.errorMessage("CALIBRATION FAIL" + e.toString());
        }
    }


}