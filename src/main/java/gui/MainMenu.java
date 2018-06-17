package gui;

import backend.core.Initializer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu extends Application {
    private static TerminalMenu terminalMenu;

    public void start(Stage primaryStage) throws Exception {
        //Connection to setup elements
        terminalMenu = new TerminalMenu(primaryStage);
        terminalMenu.openWindow();
        /*//activate before release
        if(!Initializer.fullInit())
        {
            badInit();
            terminalMenu.closeWindow();
            primaryStage.close();
            //Initializer.fullClose(); //FIX here
        }
        else
        {
            loadMainMenu(primaryStage);
        }
        */
        //ONLY FOR DEVELOPING!!!
        Initializer.fullInit();
        loadMainMenu(primaryStage);
    }

    private void badInit()
    {
        Alert init_error = new Alert(Alert.AlertType.ERROR);
        init_error.setTitle("Initialization error");
        init_error.setHeaderText(null);
        init_error.setContentText("Initialization fail\nApplication will be closed");
        init_error.showAndWait();
    }
    private void loadMainMenu(Stage primaryStage)
    {
        //Buttons
        Button terminal = new Button("Terminal");
        Button scan = new Button("Scan");
        Button calibration = new Button("Calibrate");
        //Set button actions
        terminal.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                terminalMenu.openWindow();
            }
        });
        scan.setOnAction(new ScanMenu(primaryStage));
        calibration.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                CalibrationMenu.openDialog();
            }
        });
        //Elements order & style
        VBox vbox = new VBox();
        vbox.getChildren().addAll(terminal, scan, calibration);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 50));
        Scene scene = new Scene(vbox, 256, 256 + 128);
        primaryStage.setTitle("PIA setup");
        primaryStage.setScene(scene);
        primaryStage.show();
        //Activate first calibration
        CalibrationMenu.openDialog();
    }
}
