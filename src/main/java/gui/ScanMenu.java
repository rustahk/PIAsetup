package gui;

import backend.core.ErrorProcessor;
import backend.core.LogicCommands;
import backend.core.PointRecipient;
import backend.core.ServiceProcessor;
import backend.data.Point;
import backend.devices.Calibration;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ScanMenu implements PointRecipient {
    private static TextField start_field;
    private static TextField finish_field;
    private static TextField pointsnumber_field;
    private static TextField delay_field;
    private static Button start_button;
    private static Button stop_button;
    private static ProgressBar progressBar;
    private static XYChart.Series current_series;
    private static Scanning scan_task;

    public static void openWindow(Stage primaryStage) {
        //**Axis**
        NumberAxis xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        xAxis.setLabel("Wavelenght, nm");
        yAxis.setLabel("Signal, V");
        //**Scatter**
        ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis, yAxis);
        sc.setTitle("Detected signal");
        current_series = new XYChart.Series();
        sc.getData().addAll(current_series);
        //Real-time plotting
        LogicCommands.addPointRecipient(new ScanMenu());
        //**Parameters elements**
        start_field = new TextField("Start wavelenght (nm)");
        finish_field = new TextField("Finish wavelenght (nm)");
        pointsnumber_field = new TextField("Number of points");
        delay_field = new TextField("Delay (ms)");
        //**Other elements
        progressBar = new ProgressBar(0);
        //progressBar.setDisable(true);
        VBox vbox = new VBox();
        HBox hbox_1 = new HBox();
        HBox hbox_2 = new HBox();
        start_button = new Button("Start");
        stop_button = new Button("Stop");
        stop_button.setDisable(true);
        hbox_1.setSpacing(10);
        hbox_2.getChildren().addAll(progressBar);
        hbox_1.getChildren().addAll(start_field, finish_field, pointsnumber_field, delay_field, start_button, stop_button);
        vbox.getChildren().addAll(sc, hbox_1, hbox_2);
        hbox_1.setPadding(new Insets(10, 10, 10, 50));
        hbox_2.setPadding(new Insets(10, 10, 10, 50));
        //**Main Scene**
        Scene secondScene = new Scene(new Group(), 800, 512);
        Stage scanWindow = new Stage();
        ((Group) secondScene.getRoot()).getChildren().add(vbox);
        scanWindow.initModality(Modality.WINDOW_MODAL);
        scanWindow.initOwner(primaryStage);
        scanWindow.setTitle("Scan");
        scanWindow.setScene(secondScene);
        //Actions
        start_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                startScan();
            }
        });
        stop_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scan_task.cancel(true);

            }
        });
        scanWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try
                {
                    scan_task.cancel(true);
                }
                catch (NullPointerException e)
                {
                    //Nothing, it means that task doesn't exist
                }
            }
        });
        //Show
        scanWindow.show();
    }

    private static void startScan() {
        //**Get user data from the fields
        double start_wavelenght;
        double finish_wavelenght;
        int points_number;
        int delay;
        try {
            start_wavelenght = getDobuleValue(start_field, "Start wavelenght");
            finish_wavelenght = getDobuleValue(finish_field, "Finish wavelenght");
            points_number = getIntValue(pointsnumber_field, "Number of points");
            delay = getIntValue(delay_field, "Delay");
        } catch (NumberFormatException e) {
            wrongValue(e.getMessage() + " value is wrong");
            return;
        }
        if (!Calibration.positionLimit(start_wavelenght, false)) {
            wrongValue("Start wavelenght is out of range");
            return;
        }
        if (!Calibration.positionLimit(finish_wavelenght, false)) {
            wrongValue("Finish wavelenght is out of range");
            return;
        }
        scan_task = new Scanning(new Point(start_wavelenght), new Point(finish_wavelenght), points_number, delay);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(scan_task.progressProperty());
        scan_task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                ServiceProcessor.serviceMessage("Scan finished");
                progressBar.progressProperty().unbind();
                progressBar.setDisable(true);
                stop_button.setDisable(true);
                start_button.setDisable(false);
            }
        });
        scan_task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                progressBar.progressProperty().unbind();
                progressBar.setDisable(true);
                stop_button.setDisable(true);
                start_button.setDisable(false);
            }
        });
        new Thread(scan_task).start();
    }

    private static int getIntValue(TextField field, String parameter_name) throws NumberFormatException {
        try {
            int value = Integer.parseInt(field.getText());
            return value;
        } catch (NumberFormatException e) {
            ErrorProcessor.standartError(parameter_name + " value is not a INT", e);
            throw new NumberFormatException(parameter_name);
        }
    }

    private static double getDobuleValue(TextField field, String parameter_name) throws NumberFormatException {
        try {
            double value = Double.parseDouble(field.getText());
            return value;
        } catch (NumberFormatException e) {
            ErrorProcessor.standartError(parameter_name + " value is not a Double", e);
            throw new NumberFormatException(parameter_name);
        }
    }

    private static void wrongValue(String msg) {
        Alert wrong_value = new Alert(Alert.AlertType.ERROR);
        wrong_value.setTitle("Wrong value");
        wrong_value.setHeaderText(null);
        wrong_value.setContentText(msg);
        wrong_value.showAndWait();
    }

    @Override
    public boolean newPoint(Point e) {
        Platform.runLater(new Runnable() {
            public void run() {
                current_series.getData().add(new ScatterChart.Data<Number, Number>(e.getWavelenght(), Double.parseDouble(e.getValue())));
            }
        });
        return true;
    }

    private static class Scanning extends Task<Void> {
        private Point start;
        private Point finish;
        private int numpoints;
        private int delay;

        public Scanning(Point start, Point finish, int numpoints, int delay) {
            this.start = start;
            this.finish = finish;
            this.numpoints = numpoints;
            this.delay = delay;
        }

        @Override
        protected Void call() throws Exception {
            current_series.getData().clear();
            ServiceProcessor.serviceMessage("Scan parameters: start " + start.getWavelenght() + " finish " + finish.getWavelenght() + " points " + numpoints + " delay " + delay);
            progressBar.setDisable(false);
            start_button.setDisable(true);
            stop_button.setDisable(false);
            LogicCommands.startScan(start, finish, numpoints, delay);
            return null;
        }

        private void updateInnerStatus(double workDone, double total) {
            this.updateProgress(workDone, total);
        }
    }

    public static void updateStatus(double workDone, double total) {
        if (scan_task != null) scan_task.updateInnerStatus(workDone, total);
    }
}