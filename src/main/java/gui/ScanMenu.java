package gui;

import backend.core.LogicCommands;
import backend.data.HotPoint;
import backend.data.Point;
import backend.devices.Calibration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

class ScanMenu implements EventHandler<ActionEvent>
{
    private Stage primaryStage;

    public ScanMenu(Stage mainMenu) {
        this.primaryStage = mainMenu;
    }

    public void handle(ActionEvent event)
    {
        HotPoint.test = new HotPoint(); //MAIN GUI TEST
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
        final XYChart.Series current_series = new XYChart.Series();
        sc.getData().addAll(current_series);
        //**Other elements
        VBox vbox = new VBox();
        HBox hbox = new HBox();
        Button start = new Button("Start");
        Button stop = new Button("Stop");
        hbox.setSpacing(10);
        hbox.getChildren().addAll(start, stop);
        vbox.getChildren().addAll(sc, hbox);
        hbox.setPadding(new Insets(10, 10, 10, 50));
        //**Main Scene**
        Scene secondScene = new Scene(new Group(), 512, 512);
        Stage scanWindow = new Stage();
        ((Group)secondScene.getRoot()).getChildren().add(vbox);
        scanWindow.initModality(Modality.WINDOW_MODAL);
        scanWindow.initOwner(primaryStage);
        scanWindow.setTitle("Scan");
        scanWindow.setScene(secondScene);
        //Actions
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Task scannig = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        Point start = new Point(Calibration.positionCalc(400), 400);
                        Point finish = new Point(Calibration.positionCalc(500), 500);
                        LogicCommands.startScan(start, finish, 100, 50);
                        return null;
                    }
                };
                new Thread(scannig).start();

            }
        });
        HotPoint.test.nProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                current_series.getData().add(new ScatterChart.Data<Number, Number>(HotPoint.test.getX(), HotPoint.test.getY()));
            }
        });

        //Show
        scanWindow.show();
    }
}