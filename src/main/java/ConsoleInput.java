import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/*
This class realize text input - reading user commands from console
 */

public class ConsoleInput {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static synchronized String userEnter() //enter a value/command to console
    {
        try {
            String command = reader.readLine();
            ConsoleOutput.userCommand(command);
            return command;
        } catch (IOException e) {
            ConsoleOutput.errorMessage("USER IO " + e);
            return null;
        }
    }

}
