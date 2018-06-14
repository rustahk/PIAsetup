package console;

import backend.core.ErrorProcessor;
import backend.core.ErrorRecipient;
import backend.core.ServiceProcessor;
import backend.core.SystemRecipient;
import backend.files.Logger;

public class ConsoleOutput implements ErrorRecipient, SystemRecipient{

    /*
    This class realize text output - sending info to console and log
    */

    public ConsoleOutput() {
        ErrorProcessor.addErrorRecipient(this);
        ServiceProcessor.addSystemRecipient(this);
    }

    public void serviceMessage(String msg) {
        sendMessage(msg);
    }

    public void unloggedMessage(String msg) {
        sendMessage(msg);
    }

    public void standartError(String comment, Exception e) {
        sendMessage(comment + " " + e);
    }

    private void sendMessage(String msg) {
        System.out.println(msg);
    }


}
