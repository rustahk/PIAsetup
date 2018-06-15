package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
                calibrationMenu.openWindow();
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
        calibrationMenu = new CalibrationMenu(primaryStage);
        calibrationMenu.openWindow();
    }


}
