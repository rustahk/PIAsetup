package gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CalcMenu {

    private ScatterChart.Series<Number, Number> X_series;
    private ScatterChart.Series<Number, Number> Y_series;
    private Stage calcWindow;

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
        clearPlot();
    }

    private ScatterChart<Number, Number> loadPlot()
    {
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

    public void openWindow()
    {

        calcWindow.show();
    }

    private void loadCenter(BorderPane subroot)
    {
        subroot.setCenter(loadPlot());
    }

    private void loadBottom(BorderPane subroot)
    {
        Button reference = new Button("Reference scan");
        Button scan = new Button("Sample scan");
        Button calc = new Button("Calc");
        Button save = new Button("Save");
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.getChildren().addAll(reference, scan, calc, save);
        subroot.setTop(hbox);
    }

    private void clearPlot() {
        X_series.getData().clear();
        Y_series.getData().clear();
    }
}
