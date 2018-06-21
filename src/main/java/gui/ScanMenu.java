package gui;

import backend.core.ErrorProcessor;
import backend.core.LogicCommands;
import backend.core.PointRecipient;
import backend.core.ServiceProcessor;
import backend.data.Dataset;
import backend.data.Point;
import backend.devices.Calibration;
import backend.devices.Engine;
import backend.devices.EngineByteCommands;
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
import jssc.SerialPortException;

import java.io.IOException;
import java.util.Optional;

public class ScanMenu implements PointRecipient {
    private static TextField sample_name;
    private static TextField start_field;
    private static TextField finish_field;
    private static TextField pointsnumber_field;
    private static TextField delay_field;
    private static Button start_button;
    private static Button stop_button;
    private static ProgressBar progressBar;
    private static ScatterChart.Series<Number, Number> X_series;
    private static ScatterChart.Series<Number, Number> Y_series;
    private static Scanning scan_task;
    private static CheckBox return_box;

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
        //X_series = new XYChart.Series();
        //Y_series = new XYChart.Series();
        X_series = new ScatterChart.Series<Number, Number>();
        Y_series = new ScatterChart.Series<Number, Number>();
        X_series.setName("X signal component");
        Y_series.setName("Y signal component");
        X_series.getData().add(new ScatterChart.Data<Number, Number>(0, 0)); //Labels dosen't work withot that
        Y_series.getData().add(new ScatterChart.Data<Number, Number>(0, 0)); //Labels dosen't work withot that
        sc.getData().addAll(X_series, Y_series);
        //Real-time plotting
        LogicCommands.addPointRecipient(new ScanMenu());
        //**Parameters elements**
        sample_name = new TextField("Sample name");
        start_field = new TextField("Start wavelenght (nm)");
        finish_field = new TextField("Finish wavelenght (nm)");
        pointsnumber_field = new TextField("Number of points");
        delay_field = new TextField("Delay (ms)");
        return_box = new CheckBox("Auto return");
        return_box.setSelected(true);
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
        hbox_2.setSpacing(10);
        hbox_2.getChildren().addAll(sample_name, progressBar);
        hbox_1.getChildren().addAll(start_field, finish_field, pointsnumber_field, delay_field, return_box, start_button, stop_button);
        vbox.getChildren().addAll(sc, hbox_1, hbox_2);
        hbox_1.setPadding(new Insets(10, 10, 10, 50));
        hbox_2.setPadding(new Insets(10, 10, 10, 50));
        //**Main Scene**
        Scene secondScene = new Scene(new Group(), 1100, 512);
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
                try {
                    Engine.sendCommand(EngineByteCommands.motorStop());
                } catch (Exception e) {
                    ErrorProcessor.standartError("Fail to interrupt engine moving", e);
                }
            }
        });
        scanWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                try {
                    if (scan_task.isRunning()) {
                        Alert confirm_exit = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm_exit.setTitle("Stop scan");
                        confirm_exit.setHeaderText("Confirm to stop scan?");
                        Optional<ButtonType> option = confirm_exit.showAndWait();
                        if (option.get() == ButtonType.OK) {
                            scan_task.cancel(true);
                        } else {
                            event.consume();
                        }
                    }
                }
                catch (NullPointerException e) {
                    //Nothing, it means that task doesn't exist
                }
            }
        });
        //Show
        clearPlot();
        scanWindow.show();
    }

    private static void startScan() {
        //**Get user data from the fields
        String name;
        double start_wavelenght;
        double finish_wavelenght;
        int points_number;
        int delay;
        boolean autoreturn;
        try {
            name = getFileNameValue(sample_name, "Sample name");
            start_wavelenght = getDobuleValue(start_field, "Start wavelenght");
            finish_wavelenght = getDobuleValue(finish_field, "Finish wavelenght");
            points_number = getIntValue(pointsnumber_field, "Number of points");
            delay = getIntValue(delay_field, "Delay");
            autoreturn=return_box.isSelected();
        } catch (NumberFormatException e) {
            wrongValue(e.getMessage() + " value is wrong");
            return;
        }
        catch (IOException e)
        {
            wrongValue(e.getMessage());
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
        scan_task = new Scanning(name, new Point(start_wavelenght), new Point(finish_wavelenght), points_number, delay);
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(scan_task.progressProperty());
        scan_task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                ServiceProcessor.serviceMessage("Scan finished");
                scanEnd();
                if(autoreturn) LogicCommands.moveTo(new Point(start_wavelenght));
            }
        });
        scan_task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                MainMenu.warningMessage("Scan interrupted by user","Final file hasn't been saved correct", null);
                scanEnd();
                if(autoreturn) LogicCommands.moveTo(new Point(start_wavelenght));
            }
        });
        scan_task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                MainMenu.errorMessage("Scan interrupted by system", "Final file hasn't been saved correct", null, null);
                scanEnd();
            }
        });
        new Thread(scan_task).start();
    }

    private static void scanEnd()
    {
        progressBar.progressProperty().unbind();
        progressBar.setDisable(true);
        stop_button.setDisable(true);
        start_button.setDisable(false);
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

    private static String getFileNameValue(TextField field, String parameter_name) throws IOException {
        try {
            String value = field.getText();
            char[] check = value.toCharArray();
            for (char i : check)
            {
                if(i=='\\' || i=='/' || i==':' || i=='*' || i=='?' || i=='\"' || i=='<' || i=='>' || i=='|' || i=='+') throw new IOException("Forbidden symbol " + i);
            }
            return value;
        } catch (IOException e) {
            ErrorProcessor.standartError(parameter_name + " cannot has \\ / : * ? \" < > | +", e);
            throw e;
        }
    }

    private static void wrongValue(String msg) {
        MainMenu.errorMessage("Wrong value", msg, null, null);
        Alert wrong_value = new Alert(Alert.AlertType.ERROR);
        wrong_value.setTitle("Wrong value");
        wrong_value.setHeaderText(msg);
        wrong_value.showAndWait();
    }

    @Override
    public boolean newPoint(Point e) {
        Platform.runLater(new Runnable() {
            public void run() {
                X_series.getData().add(new ScatterChart.Data<Number, Number>(e.getWavelenght(), Double.parseDouble(e.getValueX())));
                Y_series.getData().add(new ScatterChart.Data<Number, Number>(e.getWavelenght(), Double.parseDouble(e.getValueY())));
            }
        });
        return true;
    }

    private static class Scanning extends Task<Void> {
        private Point start;
        private Point finish;
        private int numpoints;
        private int delay;
        private String sample_name;

        public Scanning(String sample_name, Point start, Point finish, int numpoints, int delay) {
            this.start = start;
            this.finish = finish;
            this.numpoints = numpoints;
            this.delay = delay;
            this.sample_name = sample_name;
        }

        @Override
        protected Void call() throws Exception {
            clearPlot();
            ServiceProcessor.serviceMessage("Scan parameters: start " + start.getWavelenght() + " finish " + finish.getWavelenght() + " points " + numpoints + " delay " + delay);
            progressBar.setDisable(false);
            start_button.setDisable(true);
            stop_button.setDisable(false);
            Dataset dataset;
            dataset = LogicCommands.startScan(sample_name, start, finish, numpoints, delay);
            LogicCommands.saveScan(dataset);
            return null;
        }

        private void updateInnerStatus(double workDone, double total) {
            this.updateProgress(workDone, total);
        }
    }

    public static void updateStatus(double workDone, double total) {
        if (scan_task != null) scan_task.updateInnerStatus(workDone, total);
    }

    private static void clearPlot()
    {
        X_series.getData().clear();
        Y_series.getData().clear();
    }
}