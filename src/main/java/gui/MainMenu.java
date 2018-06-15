package gui;

import backend.core.LogicCommands;
import backend.core.ServiceProcessor;
import backend.core.SystemRecipient;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class MainMenu extends Application implements Runnable {
    private static TerminalMenu terminalMenu;
    private static CalibrationMenu calibrationMenu;

    public void run() {
        Application.launch(MainMenu.class, null);
    }

    public void start(Stage primaryStage) throws Exception {
        terminalMenu = new TerminalMenu(primaryStage);
        terminalMenu.openWindow();

        Button terminal = new Button("Terminal");
        terminal.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                terminalMenu.openWindow();
            }
        });
        Button scan = new Button("Scan");
        scan.setOnAction(new ScanMenu(primaryStage));
        Button calibration = new Button("Calibrate");
        calibration.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //calibrationMenu.openWindow();
                CalibrationMenu.openDialog();
            }
        });
        VBox vbox = new VBox();
        vbox.getChildren().addAll(terminal, scan, calibration);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 50));
        Scene scene = new Scene(vbox, 256, 256 + 128);
        primaryStage.setTitle("PIA setup");
        primaryStage.setScene(scene);
        primaryStage.show();
        //calibrationMenu = new CalibrationMenu(primaryStage);
        //calibrationMenu.openWindow();
        CalibrationMenu.openDialog();
    }


}
