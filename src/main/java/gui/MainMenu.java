package gui;

import backend.core.ErrorProcessor;
import backend.core.Initializer;
import backend.core.ServiceProcessor;
import backend.files.FileManager;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jssc.SerialPortException;

import java.util.Date;

public class MainMenu extends Application {
    private static TerminalMenu terminalMenu;
    private static ConnectionMenu connectionMenu;
    private static Stage mainStage;

    public void start(Stage primaryStage)
    {
        //Connection to setup elements
        terminalMenu = new TerminalMenu(primaryStage);
        terminalMenu.openWindow();
        connectionMenu = new ConnectionMenu();
        connectionMenu.createWindow(primaryStage);
        try
        {
            Initializer.fullInit();
            loadMainMenu(primaryStage);//$
        } catch (SerialPortException e) {
            errorMessage("Initialization", "Connection fail", "Application will be closed: " + e.toString(), null);
            closeProgram();//$
            //$loadMainMenu(primaryStage);
        }
        catch (Exception e)
        {
            errorMessage("Initialization", "Critical error", "Application will be closed: " + e.toString(), e);
            closeProgram();
        }

    }

    private void loadMainMenu(Stage primaryStage) {
        //Buttons
        mainStage = primaryStage;
        Button terminal = new Button("Terminal");
        Button scan = new Button("Scan");
        Button calibration = new Button("Calibrate");
        Button connection = new Button("Connection");
        connection.setDisable(true);//$
        //Set button actions
        terminal.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                terminalMenu.openWindow();
            }
        });
        scan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ScanMenu.openWindow(primaryStage);
            }
        });
        calibration.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                CalibrationMenu.openDialog();
            }
        });
        connection.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connectionMenu.openWindow();
            }
        });
        //Elements order & style
        VBox vbox = new VBox();
        vbox.getChildren().addAll(terminal, scan, calibration, connection);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 50));
        Scene scene = new Scene(vbox, 256, 256 + 128);
        primaryStage.setTitle("PIA setup");
        primaryStage.setScene(scene);
        primaryStage.show();
        //Activate first calibration
        CalibrationMenu.openDialog();//$
        //connectionMenu.openWindow();
    }

    public static void closeProgram() {
        Initializer.fullClose();
        ServiceProcessor.serviceMessage(FileManager.getDateTimeStamp(new Date()) + " #Session finish");
                try {
            terminalMenu.closeWindow();
        } catch (NullPointerException e) {

        }
        try {
            mainStage.close();
        } catch (NullPointerException e) {

        }
    }

    public void stop() {
        closeProgram();
    }

    public static void errorMessage(String error_title, String error_msg, String content_text, Exception e) {
        if(e!=null) ErrorProcessor.standartError(error_msg, e);
        else ServiceProcessor.serviceMessage(error_title + " " + error_msg);
        Alert error_alert = new Alert(Alert.AlertType.ERROR);
        error_alert.setTitle(error_title);
        error_alert.setHeaderText(error_msg);
        error_alert.setContentText(content_text);
        error_alert.showAndWait();
    }

    public static void warningMessage(String warning_title, String warning_msg, String content_text) {
        ServiceProcessor.serviceMessage(warning_msg);
        Alert warining = new Alert(Alert.AlertType.WARNING);
        warining.setTitle(warning_title);
        warining.setHeaderText(warning_msg);
        warining.setContentText(content_text);
        warining.showAndWait();
    }
}
