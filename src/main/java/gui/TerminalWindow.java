package gui;

import backend.core.ErrorProcessor;
import backend.core.ErrorRecipient;
import backend.core.ServiceProcessor;
import backend.core.SystemRecipient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TerminalWindow extends Application implements SystemRecipient, ErrorRecipient, Runnable{

    private static boolean windowStatus;
    private static TextArea textArea;

    public void start(Stage primaryStage) throws Exception {

        ErrorProcessor.addErrorRecipient(this);
        ServiceProcessor.addSystemRecipient(this);
        primaryStage.setTitle("Terminal");
        textArea = new TextArea();
        VBox vbox = new VBox(textArea);
        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
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

    public void run() {
        Application.launch(TerminalWindow.class, null);
    }
}
