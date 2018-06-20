package gui;

import backend.core.ErrorProcessor;
import backend.core.LogicCommands;
import backend.core.ServiceProcessor;
import backend.data.Point;
import backend.devices.Calibration;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CalibrationMenu {

    private static boolean calibrated;
    private static Optional<String> result;
    private static TextInputDialog dialog;
    private static String error_title = "Calibration error";

    public static void openDialog() {
        dialog = new TextInputDialog("Calibration");
        result = dialog.showAndWait();
        try {
            double value = Double.parseDouble(result.get());
            if (Calibration.positionLimit(value, false)) {
                if(LogicCommands.calibratePosition(new Point(value)))
                {
                    ServiceProcessor.serviceMessage("Position updated: " + value + " nm");
                    calibrated = true;
                }
                else {
                    if(calibrated) noUpdate();
                    else MainMenu.errorMessage(error_title, "No reply from engine", null, new IOException("No reply from enfine"));; //Add exit to connection menu?
                    confirmedCancel();
                }
            } else {
                MainMenu.errorMessage(error_title, "[CRITICAL] Position " + value + "nm is out of workrange",null, new NumberFormatException("out of range"));
                CalibrationMenu.openDialog();
            }
        } catch (NumberFormatException e) {
            MainMenu.errorMessage(error_title,"This is not a DOUBLE value",e.toString(), e);
            CalibrationMenu.openDialog();
        } catch (NoSuchElementException e) {
            if (calibrated) noUpdate();
            else cancel();
        }
    }

    private static void noUpdate() {
            MainMenu.warningMessage("Calibration", "Calibration is not updated", null);
    }

    private static void cancel() {
        Alert confirm_cancel = new Alert(Alert.AlertType.CONFIRMATION);
        confirm_cancel.setTitle("First calibration");
        confirm_cancel.setHeaderText("You must enter actual position before using the setup \nConfirm to close application?");
        Optional<ButtonType> option = confirm_cancel.showAndWait();
        if (option.get() == null) {
            confirmedCancel();
        } else if (option.get() == ButtonType.OK) {
            confirmedCancel();
        } else if (option.get() == ButtonType.CANCEL) {
            CalibrationMenu.openDialog();
        } else {
            confirmedCancel();
        }
    }

    private static void confirmedCancel() {
        ServiceProcessor.serviceMessage("First calibration has been canceled");
        MainMenu.closeProgram();
    }
}
