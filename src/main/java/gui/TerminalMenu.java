package gui;

import backend.core.*;
import backend.data.Point;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TerminalMenu implements SystemRecipient, ErrorRecipient, PointRecipient
{

    private static boolean windowStatus;
    private static TextArea textArea;
    private static Stage terminalWindow;


    public TerminalMenu(Stage mainMenu) throws Exception
    {
        ErrorProcessor.addErrorRecipient(this);
        ServiceProcessor.addSystemRecipient(this);
        LogicCommands.addPointRecipient(this);
        terminalWindow = new Stage();
        terminalWindow.initModality(Modality.NONE);
        terminalWindow.initOwner(mainMenu);
        terminalWindow.setTitle("Terminal");
        textArea = new TextArea();
        Scene secondScene = new Scene(textArea, 700, 256);
        terminalWindow.setScene(secondScene);
    }

    public void openWindow()
    {
        terminalWindow.setX(terminalWindow.getOwner().getX()); //Shift of window position when opens
        terminalWindow.show();
        windowStatus = true;
    }

    public void closeWindow()
    {
        terminalWindow.close();
    }

    public static void addLine(String line) {
        textArea.appendText(line + "\n");
    }

    public static boolean isWindowStatus() {
        return windowStatus;
    }

    public void standartError(String comment, Exception e) {
        addLine(comment + " " + e);
    }

    public void serviceMessage(String msg) {
        addLine(msg);
    }

    public boolean newPoint(Point point) {
        addLine("[SCAN]: " + point.getWavelenght() + " " + point.getValue());
        return true;
    }
}
