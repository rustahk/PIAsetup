package backend.core;

import backend.files.Logger;
import java.util.ArrayList;
import java.util.List;


public class ErrorProcessor {

    private static List<ErrorRecipient> listeners = new ArrayList<ErrorRecipient>();

    public static void addErrorRecipient(ErrorRecipient toAdd)
    {
        listeners.add(toAdd);
    }

    public static synchronized void standartError(String comment, Exception e)
    {
        comment="[ERROR]: "+comment+ "\t";
        for(ErrorRecipient r : listeners) r.standartError(comment, e);
    }
}
