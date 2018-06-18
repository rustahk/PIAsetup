package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class ConnectionMenu {
    private MenuButton device;
    private MenuButton port;
    private MenuButton baud_rate;
    private MenuButton data_bits;
    private MenuButton stopbits;
    private MenuButton parity;
    private TextField delay_field;
    private Stage connectionWindow;


    public void createWindow(Stage primaryStage) {
        //Elements
        device = new MenuButton("Engine");
        port = new MenuButton("Port");
        baud_rate = new MenuButton("Baud rate");
        stopbits = new MenuButton("Stop bit");
        parity = new MenuButton("Parity");
        delay_field = new TextField("Responce delay (ms)");
        //Menu items & actions
        //Devices
        //Engine:
        MenuItem engine = new MenuItem("Engine");
        engine.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                device.setText("Engine");
            }
        });
        device.getItems().addAll(engine);
        //Lockin:
        MenuItem lockin = new MenuItem("Lockin");
        lockin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                device.setText("Lockin");
            }
        });
        device.getItems().addAll(lockin);
        //Baud_rate:
        for(int i = 0; i<6; i++)
        {
            int value = (int) Math.pow(2, i)*1200;
            MenuItem item = new MenuItem(value+"");
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    baud_rate.setText(value+"");
                }
            });
            baud_rate.getItems().addAll(item);
        }
        //Elements position
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(10, 10, 10, 50));
        hbox.getChildren().addAll(device, port, baud_rate, stopbits, parity, delay_field);
        //Scene & window
        Scene secondScene = new Scene(hbox, 600, 80);
        //((Group) secondScene.getRoot()).getChildren().add();
        connectionWindow = new Stage();
        connectionWindow.initModality(Modality.WINDOW_MODAL);
        connectionWindow.initOwner(primaryStage);
        connectionWindow.setTitle("Scan");
        connectionWindow.setScene(secondScene);
    }

    public void openWindow() {
        connectionWindow.show();
    }
}
