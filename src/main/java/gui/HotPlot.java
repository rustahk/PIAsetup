package gui;

import backend.data.HotPoint;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class HotPlot extends Application
{
    private static HotPoint hotpoint;

    public static void setHotPoint(HotPoint hotPoint) {
        hotpoint = hotPoint;
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Scan Plot");
        NumberAxis xAxis = new NumberAxis(790, 830, 1);
        NumberAxis yAxis = new NumberAxis();
        ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis, yAxis);
        xAxis.setLabel("Wavelenght, nm");
        yAxis.setLabel("Signal, V");
        sc.setTitle("Detected signal");
        final XYChart.Series current_series = new XYChart.Series();
        sc.getData().addAll(current_series);
        Scene scene = new Scene(sc);
        stage.setScene(scene);
        stage.show();
        hotpoint.nProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                current_series.getData().add(new ScatterChart.Data<Number, Number>(hotpoint.getX(), hotpoint.getY()));
            }
        });
    }

    public static void startHotPlot()
    {
        if(hotpoint == null) new NullPointerException("hotpoint cannot be null");
        launch();
    }
}
