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

    public static void openDialog() {
        dialog = new TextInputDialog("Calibration");
        result = dialog.showAndWait();
        try {
            double value = Double.parseDouble(result.get());
            if (Calibration.positionLimit(value, false)) {
                if(LogicCommands.calibratePosition(new Point(Calibration.positionCalc(value), value)))
                {
                    ServiceProcessor.serviceMessage("Position updated: " + value + " nm");
                    calibrated = true;
                }
                noReply();
            } else {
                outOfRange(value);
            }
        } catch (NumberFormatException e) {
            notDouble(e);
        } catch (NoSuchElementException e) {
            if (calibrated) noUpdate();
            else cancel();
        }
    }

    private static void outOfRange(double wavelenght) {
        String error = "[CRITICAL] Position " + wavelenght + "nm is out of workrange";
        ErrorProcessor.standartError(error, new NumberFormatException());
        Alert outoflimit_error = new Alert(Alert.AlertType.ERROR);
        outoflimit_error.setTitle("Calibration error");
        outoflimit_error.setHeaderText(null);
        outoflimit_error.setContentText(error);
        outoflimit_error.showAndWait();
        CalibrationMenu.openDialog();
    }

    private static void notDouble(NumberFormatException e) {
        String error = "This is not a DOUBLE value";
        ErrorProcessor.standartError(error, e);
        Alert not_double = new Alert(Alert.AlertType.ERROR);
        not_double.setTitle("Calibration error");
        not_double.setHeaderText(null);
        not_double.setContentText(error);
        not_double.showAndWait();
        CalibrationMenu.openDialog();
    }

    private static void noValue(NoSuchElementException e) {
        String error = "[CRITICAL] Engine must be calibrated";
        ErrorProcessor.standartError(error, e);
        Alert no_value = new Alert(Alert.AlertType.ERROR);
        no_value.setTitle("Calibration error");
        no_value.setHeaderText(null);
        no_value.setContentText(error);
        no_value.showAndWait();
        CalibrationMenu.openDialog();
    }

    private static void noUpdate() {
            String warning = "Calibration is not updated";
            ServiceProcessor.serviceMessage(warning);
            Alert outoflimit_error = new Alert(Alert.AlertType.WARNING);
            outoflimit_error.setTitle("Calibration error");
            outoflimit_error.setHeaderText(null);
            outoflimit_error.setContentText(warning);
            outoflimit_error.showAndWait();
    }

    private static void cancel() {
        Alert confirm_cancel = new Alert(Alert.AlertType.CONFIRMATION);
        confirm_cancel.setTitle("First calibration");
        confirm_cancel.setHeaderText(null);
        confirm_cancel.setContentText("You must enter actual position before using the setup \nConfirm to close application?");
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
    private static void noReply()
    {
        String error = "No reply from engine";
        ErrorProcessor.standartError(error, new IOException());
        Alert no_reply = new Alert(Alert.AlertType.ERROR);
        no_reply.setTitle("Calibration error");
        no_reply.setHeaderText(null);
        no_reply.setContentText(error);
        no_reply.showAndWait();
        confirmedCancel();
    }

}
