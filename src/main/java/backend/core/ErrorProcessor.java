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

    public static void standartError(String comment, Exception e)
    {
        comment="[ERROR]: "+comment;
        //Logger.addtoLog(comment + " " + e.toString());
        for(ErrorRecipient r : listeners) r.standartError(comment, e);
    }
}
