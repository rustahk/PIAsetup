package gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
        data_bits = new MenuButton("Data bits");
        stopbits = new MenuButton("Stop bit");
        parity = new MenuButton("Parity");
        delay_field = new TextField("Responce delay (ms)");
        Button connect_button = new Button("Connect");
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
        //Baud_rate
        for (int i = 0; i <= 5; i++) {
            int value = (int) Math.pow(2, i) * 1200;
            MenuItem item = new MenuItem(value + "");
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    baud_rate.setText(value + "");
                }
            });
            baud_rate.getItems().addAll(item);
        }
        //Data_bits
        for (int i = 5; i <= 8; i++) {
            final int value = i;
            MenuItem item = new MenuItem(value + "");
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    data_bits.setText(value + "");
                }
            });
            data_bits.getItems().addAll(item);
        }
        //Stopbits
        for (double i = 1; i <= 2; i += 0.5) {
            final double value = i;
            MenuItem item = new MenuItem(value + "");
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stopbits.setText(value + "");
                }
            });
            stopbits.getItems().addAll(item);
        }
        //Parity
        String[] parities = new String[5];
        parities[0]="NONE";
        parities[1]="ODD";
        parities[2]="EVEN";
        parities[3]="MARK";
        parities[4]="SPACE";
        for (String line : parities)
        {
            MenuItem item = new MenuItem(line);
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    parity.setText(line);
                }
            });
            parity.getItems().addAll(item);
        }
        //Elements position
        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(10, 10, 10, 50));
        //vbox.setPadding(new Insets(10, 10, 10, 50));
        hbox.getChildren().addAll(device, port, baud_rate, data_bits, stopbits, parity, delay_field, connect_button);
        //Scene & window
        Scene secondScene = new Scene(hbox, 950, 80);
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

    private class DeviceParamenters {
        private String device;
        private String port;
        private int baud_rate;
        private int data_bits;
        private int stopbits;
        private int parity;
        private int delay_field;
    }
}
