package gui;

import backend.core.Initializer;
import backend.core.ServiceProcessor;

public class InterfaceManager {
    public static Thread startGUI() {
        Thread mainmenu = new Thread(new MainMenu());
        try {
            Thread.sleep(250); //Strange bug - time to time application cannot start without this timer
            mainmenu.start();
            Initializer.startLogger();
            while (!TerminalMenu.isWindowStatus()) {
                Thread.sleep(250);
            }
            ServiceProcessor.serviceMessage("TerminalMenu: OK");
            return null;
        } catch (Exception e) {
            throw new NullPointerException("Application GUI start fail" + e);
        }
    }
}
