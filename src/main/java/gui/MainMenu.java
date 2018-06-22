package gui;

import backend.core.PointRecipient;
import backend.core.ErrorProcessor;
import backend.core.Initializer;
import backend.core.LogicCommands;
import backend.core.ServiceProcessor;
import backend.data.Dataset;
import backend.data.Point;
import backend.devices.Calibration;
import backend.devices.Engine;
import backend.devices.EngineByteCommands;
import backend.files.FileManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jssc.SerialPortException;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

public class MainMenu extends Application implements PointRecipient {


    private static TerminalMenu terminalMenu;
    private static ConnectionMenu connectionMenu;
    private static Stage mainStage;
    private Scene scene;
    //Center plot
    private ScatterChart.Series<Number, Number> X_series;
    private ScatterChart.Series<Number, Number> Y_series;
    //Right data fields
    private TextField sample_name;
    private TextField start_field;
    private TextField finish_field;
    private TextField pointsnumber_field;
    private TextField delay_field;
    private CheckBox return_box;
    private Button start_button;
    private Button stop_button;
    //Bottom status fields
    ProgressBar progressBar;
    //Task
    private static Scanning scan_task;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        terminalMenu = new TerminalMenu(primaryStage);
        connectionMenu = new ConnectionMenu(primaryStage);
        try {
            Initializer.fullInit();
            loadMenu(primaryStage);
        } catch (SerialPortException e) {
            terminalMenu.openWindow();
            errorMessage("Initialization", "Connection fail", "Application will be closed: " + e.toString(), null);
            closeProgram();
        } catch (Exception e) {
            terminalMenu.openWindow();
            errorMessage("Initialization", "Critical error", "Application will be closed: " + e.toString(), e);
            closeProgram();
        }
        loadMenu(primaryStage); //$
    }

    private void loadMenu(Stage primaryStage) {
        BorderPane root = loadRoot();
        loadCenter(root);
        loadTop(root);
        loadRight(root);
        loadBottom(root);
        scene = new Scene(root, 1250, 750);
        primaryStage.setTitle("Photoinduced absorption setup");
        primaryStage.setScene(scene);
        clearPlot();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
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
                } catch (NullPointerException e) {
                    //Nothing, it means that task doesn't exist
                }
            }
        });
        primaryStage.show();
    }

    private BorderPane loadRoot() {
        BorderPane root = new BorderPane();
        //root.setPadding(new Insets(15, 20, 10, 10));
        return root;
    }

    private void loadCenter(BorderPane root) {
        root.setCenter(loadPlot());
    }

    private void loadTop(BorderPane root) {
        Button terminal = new Button("Terminal");
        Button calibration = new Button("Calibrate");
        Button connection = new Button("Connection");
        Button calc = new Button("Calc spectra");
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.getChildren().addAll(terminal, calibration, connection, calc);
        root.setTop(hbox);
        //Button actions
        terminal.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                terminalMenu.openWindow();
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
        //DEVELOPING
        connection.setDisable(true);
        calc.setDisable(true);
    }

    private void loadRight(BorderPane root) {
        //Data fields
        sample_name = new TextField("Sample name");
        start_field = new TextField("Start wavelenght (nm)");
        finish_field = new TextField("Finish wavelenght (nm)");
        pointsnumber_field = new TextField("Number of points");
        delay_field = new TextField("Delay (ms)");
        return_box = new CheckBox("Auto return");
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(5, 10, 5, 5));
        vbox.getChildren().addAll(sample_name, start_field, finish_field, pointsnumber_field, delay_field, return_box);
        return_box.setSelected(true);
        //Buttons
        start_button = new Button("Start");
        stop_button = new Button("Stop");
        start_button.setPrefWidth(190);
        stop_button.setPrefWidth(190);
        vbox.getChildren().addAll(start_button, stop_button);
        //
        start_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                startScan();
            }
        });
        stop_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    scan_task.cancel(true);
                } catch (NullPointerException e) {
                    //Nothing, task didn't exist
                }
                try {
                    Engine.sendCommand(EngineByteCommands.motorStop());

                } catch (Exception e) {
                    errorMessage("STOP", "Fail to interrupt engine moving", e.toString(), e);
                }
            }
        });
        //
        root.setRight(vbox);
    }

    private void loadBottom(BorderPane root) {
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.getChildren().addAll(progressBar);
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        root.setBottom(hbox);
    }

    private ScatterChart<Number, Number> loadPlot() {
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
        //**Series**
        X_series = new ScatterChart.Series<Number, Number>();
        Y_series = new ScatterChart.Series<Number, Number>();
        X_series.setName("X signal component");
        Y_series.setName("Y signal component");
        X_series.getData().add(new ScatterChart.Data<Number, Number>(0, 0)); //Labels dosen't work withot that
        Y_series.getData().add(new ScatterChart.Data<Number, Number>(0, 0)); //Labels dosen't work withot that
        sc.getData().addAll(X_series, Y_series);
        LogicCommands.addPointRecipient(this);
        return sc;
    }

    public static void closeProgram() {
        Initializer.fullClose();
        try {
            terminalMenu.closeWindow();
        } catch (NullPointerException e) {
        }
        try {
            mainStage.close();
        } catch (NullPointerException e) {

        }
    }

    public void close() {
        ServiceProcessor.serviceMessage(FileManager.getDateTimeStamp(new Date()) + " #Session finish");
    }

    public static void errorMessage(String error_title, String error_msg, String content_text, Exception e) {
        if (e != null) ErrorProcessor.standartError(error_msg, e);
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

    private void startScan() {
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
            autoreturn = return_box.isSelected();
            if (!Calibration.positionLimit(start_wavelenght, false)) {
                wrongValue("Start wavelenght is out of range", null);
                return;
            }
            if (!Calibration.positionLimit(finish_wavelenght, false)) {
                wrongValue("Finish wavelenght is out of range", null);
                return;
            }
            if (points_number < 0) {
                wrongValue("Point number cannot be negative", null);
                return;
            }
            if (delay < 0) {
                wrongValue("Delay cannot be negative", null);
                return;
            }
        } catch (NumberFormatException e) {
            wrongValue(e.getMessage() + " value is wrong", e.toString());
            return;
        } catch (IOException e) {
            wrongValue(e.getMessage(), null);
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
                if (autoreturn) LogicCommands.moveTo(new Point(start_wavelenght));
            }
        });
        scan_task.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                MainMenu.warningMessage("Scan interrupted by user", "Final file hasn't been saved correct", null);
                scanEnd();
                if (autoreturn) LogicCommands.moveTo(new Point(start_wavelenght));
            }
        });
        scan_task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                MainMenu.errorMessage("Scan interrupted", "Scan interrupted by system", "Final file hasn't been saved correct", null);
                scanEnd();
                progressBar.setProgress(0);
            }
        });
        new Thread(scan_task).start();
    }

    private void scanEnd() {
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
        String value = field.getText();
        char[] check = value.toCharArray();
        for (char i : check) {
            if (i == '\\' || i == '/' || i == ':' || i == '*' || i == '?' || i == '\"' || i == '<' || i == '>' || i == '|' || i == '+')
                throw new IOException("Forbidden symbol " + i + "\n" + parameter_name + " cannot has \\ / : * ? \" < > | +");
        }
        return value;
    }

    private static void wrongValue(String msg, String content) {
        MainMenu.errorMessage("Wrong value", msg, content, null);
    }

    public boolean newPoint(Point e) {
        Platform.runLater(new Runnable() {
            public void run() {
                X_series.getData().add(new ScatterChart.Data<Number, Number>(e.getWavelenght(), Double.parseDouble(e.getValueX())));
                Y_series.getData().add(new ScatterChart.Data<Number, Number>(e.getWavelenght(), Double.parseDouble(e.getValueY())));
            }
        });
        return true;
    }

    private class Scanning extends Task<Void> {
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

    private void clearPlot() {
        X_series.getData().clear();
        Y_series.getData().clear();
    }
}
