package gui;

import backend.core.ErrorProcessor;
import backend.core.Initializer;
import backend.core.ServiceProcessor;
import backend.files.FileManager;
import javafx.application.Application;
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

import java.util.Date;

public class NewMenu extends Application {

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

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        terminalMenu = new TerminalMenu(primaryStage);
        connectionMenu = new ConnectionMenu(primaryStage);
        BorderPane root = loadRoot();
        loadCenter(root);
        loadTop(root);
        loadRight(root);
        loadBottom(root);
        scene = new Scene(root, 1250, 750);
        primaryStage.setTitle("Photoinduced absorption setup");
        primaryStage.setScene(scene);
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
