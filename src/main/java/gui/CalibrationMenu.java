package gui;

import backend.core.ErrorProcessor;
import backend.core.LogicCommands;
import backend.core.ServiceProcessor;
import backend.data.Point;
import backend.devices.Calibration;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.NoSuchElementException;
import java.util.Optional;

public class CalibrationMenu {
    /*
    private static Stage calibrationWindow;

    public CalibrationMenu(Stage mainMenu) throws Exception {
        Scene secondScene = new Scene(new Group());
        calibrationWindow = new Stage();
        calibrationWindow.initModality(Modality.APPLICATION_MODAL);
        calibrationWindow.initOwner(mainMenu);
        calibrationWindow.setTitle("Calibration");
        Button ok = new Button("Ok");
        Button cancel = new Button("Cancel");
        TextField textfield = new TextField("Enter current position, nm");
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(ok, cancel);
        hbox.setPadding(new Insets(10, 10, 10, 50));
        vbox.getChildren().addAll(textfield, hbox);
        ((Group)secondScene.getRoot()).getChildren().add(vbox);
        calibrationWindow.setScene(secondScene);
    }

    public void openWindow()
    {
        calibrationWindow.show();
    }
    */
    public static void openDialog()
    {
        TextInputDialog dialog = new TextInputDialog("Calibration");
        Optional<String> result = dialog.showAndWait();
        try
        {
            double value = Double.parseDouble(result.get());
            if(Calibration.positionLimit(value, false))
            {
                LogicCommands.calibratePosition(new Point(Calibration.positionCalc(value), value));
                ServiceProcessor.serviceMessage("Position updated: " + value + " nm");
            }
            else
            {
                ErrorProcessor.standartError("[CRITICAL] Value " + value + " out of workrange", new NumberFormatException());
            }
        }
        catch (NumberFormatException e)
        {
            ErrorProcessor.standartError("This is not DOUBLE", e);
        }
        catch (NoSuchElementException e)
        {
            ErrorProcessor.standartError("[CRITICAL] Engine must be calibrated", e);
        }
    }
}
