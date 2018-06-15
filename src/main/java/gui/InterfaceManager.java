package gui;

import backend.core.Initializer;
import backend.core.ServiceProcessor;
import javafx.application.Application;

public class InterfaceManager
{
    public static Thread startTerminal()
    {
        Thread terminal = new Thread(new TerminalWindow());
        try
        {
            Thread.sleep(250); //Strange bug - time to time application cannot start without this timer
            terminal.start();
            Initializer.startLogger();
            while (!TerminalWindow.isWindowStatus()) {
                Thread.sleep(250);
            }
            ServiceProcessor.serviceMessage("Terminal: OK");
            return null;
        } catch (Exception e)
        {
            throw new NullPointerException("Application GUI start fail" + e);
        }
    }

}
