package backend.core;

import backend.files.Logger;
import java.util.ArrayList;
import java.util.List;

public class ServiceProcessor {

    private static List<SystemRecipient> listeners = new ArrayList<SystemRecipient>();

    public static void addSystemRecipient(SystemRecipient toAdd)
    {
        listeners.add(toAdd);
    }

    public static void serviceMessage(String msg)
    {
        msg="[SYSTEM]: " + msg;
        Logger.addtoLog(msg);
        for(SystemRecipient r : listeners) r.serviceMessage(msg);
    }

}
