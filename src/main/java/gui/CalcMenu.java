package gui;

import backend.core.LogicCommands;
import backend.data.Dataset;
import backend.data.Point;
import backend.files.Loader;
import backend.files.Saver;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class CalcMenu {

    private ScatterChart.Series<Number, Number> X_series;
    private ScatterChart.Series<Number, Number> Y_series;
    private Stage calcWindow;
    private Button calc;
    private Button save;
    private File reference_dataset;
    private File scan_dataset;
    private FileChooser fileChooser;
    private static Dataset normalized;

    public CalcMenu(Stage primaryStage) {

        BorderPane subroot = new BorderPane();
        Scene secondScene = new Scene(subroot, 1000, 800);
        calcWindow = new Stage();
        calcWindow.initModality(Modality.NONE);
        calcWindow.initOwner(primaryStage);
        calcWindow.setTitle("Scan");
        calcWindow.setScene(secondScene);
        loadCenter(subroot);
        loadBottom(subroot);
        fileChooser = new FileChooser();
        clearPlot();
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
        sc.setTitle("Absorbtion signal");
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

    public void openWindow() {
        calcWindow.show();
    }

    private void loadCenter(BorderPane subroot) {
        subroot.setCenter(loadPlot());
    }

    private void loadBottom(BorderPane subroot) {
        Button reference = new Button("Reference scan");
        Button scan = new Button("Sample scan");
        calc = new Button("Calc");
        save = new Button("Save");
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.getChildren().addAll(reference, scan, calc, save);
        subroot.setTop(hbox);
        calc.setDisable(true);
        save.setDisable(true);

        //Actions
        reference.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                reference_dataset = null;
                reference_dataset = fileChooser.showOpenDialog(calcWindow);
                checkFiles();
            }
        });
        scan.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scan_dataset = null;
                scan_dataset = fileChooser.showOpenDialog(calcWindow);
                checkFiles();
            }
        });
        calc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Dataset scan = Loader.loadDataset(scan_dataset);
                    Dataset ref = Loader.loadDataset(reference_dataset);
                    normalized = LogicCommands.normalizeData(scan, ref);
                    clearPlot();
                    for (Point i : normalized.getPoints()) {
                        X_series.getData().add(new XYChart.Data(i.getWavelenght(), Double.parseDouble(i.getValueX())));
                        Y_series.getData().add(new XYChart.Data(i.getWavelenght(), Double.parseDouble(i.getValueY())));
                    }
                    save.setDisable(false);
                } catch (Exception e) {
                    MainMenu.errorMessage("Normalization erorr", e.toString(), null, e);
                    clearPlot();
                    normalized = null;
                }
            }
        });
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Saver.saveDataset(normalized);
                MainMenu.infoMessage("File saved", "New dataset saved", normalized.getSample_name());
            }
        });
    }

    private void clearPlot() {
        X_series.getData().clear();
        Y_series.getData().clear();
    }

    private void checkFiles() {
        if (scan_dataset != null && reference_dataset != null) calc.setDisable(false);
    }
}
