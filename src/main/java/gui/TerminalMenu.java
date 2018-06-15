package gui;

import backend.core.ErrorProcessor;
import backend.core.ErrorRecipient;
import backend.core.ServiceProcessor;
import backend.core.SystemRecipient;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TerminalMenu implements SystemRecipient, ErrorRecipient{

    private static boolean windowStatus;
    private static TextArea textArea;
    private static Stage terminalWindow;


    public TerminalMenu(Stage mainMenu) throws Exception
    {
        ErrorProcessor.addErrorRecipient(this);
        ServiceProcessor.addSystemRecipient(this);
        Scene secondScene = new Scene(new Group());
        terminalWindow = new Stage();
        terminalWindow.initModality(Modality.NONE);
        terminalWindow.initOwner(mainMenu);
        terminalWindow.setTitle("Terminal");
        textArea = new TextArea();
        VBox vbox = new VBox(textArea);
        ((Group)secondScene.getRoot()).getChildren().add(vbox);
        terminalWindow.setScene(secondScene);
    }

    public void openWindow()
    {
        terminalWindow.setX(terminalWindow.getOwner().getX()); //Shift of window position when opens
        terminalWindow.show();
        windowStatus = true;
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
}
