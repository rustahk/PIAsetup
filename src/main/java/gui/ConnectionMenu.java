package gui;

import backend.core.Initializer;
import backend.core.ServiceProcessor;
import backend.devices.Connection;
import backend.devices.Engine;
import backend.devices.Lockin;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.IOException;
import java.util.Optional;


public class ConnectionMenu {
    private MenuButton device;
    private MenuButton port;
    private MenuButton baud_rate;
    private MenuButton data_bits;
    private MenuButton stopbits;
    private MenuButton parity;
    private TextField delay_field;
    private Stage connectionWindow;
    private final String engine_name = "engine";
    private final String lockin_name = "lockin";
    private final String power_name = "power";
    private final String laser_name = "laser";
    private final String[] parities = new String[5];
    private boolean critical;

    public ConnectionMenu(Stage primaryStage) {
        createWindow(primaryStage);
    }

    private void createWindow(Stage primaryStage) {
        //Elements
        device = new MenuButton("Device");
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
        MenuItem engine = new MenuItem(engine_name);
        engine.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                device.setText(engine_name);
            }
        });
        device.getItems().addAll(engine);
        //Lockin:
        MenuItem lockin = new MenuItem(lockin_name);
        lockin.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                device.setText(lockin_name);
            }
        });
        device.getItems().addAll(lockin);
        //Power:
        MenuItem power = new MenuItem(power_name);
        power.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                device.setText(power_name);
            }
        });
        device.getItems().addAll(power);
        //Laser:
        MenuItem laser = new MenuItem(laser_name);
        laser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                device.setText(laser_name);
            }
        });
        device.getItems().addAll(laser);
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
        //Port
        //**Updated whe window opens**
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
        parities[0] = "NONE";
        parities[1] = "ODD";
        parities[2] = "EVEN";
        parities[3] = "MARK";
        parities[4] = "SPACE";
        for (String line : parities) {
            MenuItem item = new MenuItem(line);
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    parity.setText(line);
                }
            });
            parity.getItems().addAll(item);
        }
        //Button
        connect_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connectTo();
            }
        });
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
        updatePortList();
        connectionWindow.show();
    }

    private void updatePortList() {
        port.getItems().clear();
        for (String i : Connection.getPortNames()) {
            MenuItem item = new MenuItem(i);
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    port.setText(i);
                }
            });
            port.getItems().addAll(item);
        }
    }

    private boolean connectTo() {

        try {
            Connection connection;
            if (device.getText().equals(engine_name)) {
                ServiceProcessor.serviceMessage("Try to reconnect " + device.getText().equals(engine_name));
                connection = createConnection(9, false);
                if (Engine.getEngine().getConnection() != null && Engine.getEngine().getConnection().isOpened())
                    Engine.getEngine().getConnection().disconnect();
                new Engine(connection);
                connection.connect();
            } else if (device.getText().equals(lockin_name)) {
                ServiceProcessor.serviceMessage("Try to reconnect " + device.getText().equals(engine_name));
                connection = createConnection(0, true);
                if (Lockin.getLockin().getConnection() != null && Lockin.getLockin().getConnection().isOpened())
                    Lockin.getLockin().getConnection().disconnect();
                new Lockin(connection);
                connection.connect();
            } else {
                throw new IOException("Device is not chosen");
            }
            MainMenu.infoMessage("Connection", "Reconnect to " + device.getText() + ": OK", port.getText());
            updateConfigDialog(device.getText(), connection);
            if (critical) {
                critical = false;
                connectionWindow.close();
            }
            return true;
        } catch (IOException e) {
            MainMenu.errorMessage("Wrong value", "You have to choose all parameters before connection", e.toString(), e);
            return false;
        } catch (SerialPortException e) {
            MainMenu.errorMessage("Connection", device.getText() + "connection: FAIL", e.toString(), e);
            return false;
        }
    }

    private Connection createConnection(int message_size, boolean responce_type) throws IOException {
        return new Connection(getPort(), getBaudRate(), getDataBits(), getStopBit(), getParity(), message_size, responce_type, getDelay());
    }

    private String getPort() throws IOException {
        if (port.getText().equals("Port")) throw new IOException("Port is not chosen");
        return port.getText();
    }

    private int getBaudRate() throws IOException {
        try {
            return Integer.parseInt(baud_rate.getText());
        } catch (NumberFormatException e) {
            throw new IOException("Baud rate is not chosen");
        }
    }

    private int getDataBits() throws IOException {
        try {
            return Integer.parseInt(data_bits.getText());
        } catch (NumberFormatException e) {
            throw new IOException("Data bits are not chosen");
        }
    }

    private int getStopBit() throws IOException {

        switch (stopbits.getText()) {
            case "1.0":
                return SerialPort.STOPBITS_1;
            case "1.5":
                return SerialPort.STOPBITS_1_5;
            case "2.0":
                return SerialPort.STOPBITS_2;
            default:
                throw new IOException("Data bits are not chosen");
        }
    }

    private int getParity() throws IOException {
        switch (parity.getText()) {
            case "NONE":
                return SerialPort.PARITY_NONE;
            case "EVEN":
                return SerialPort.PARITY_EVEN;
            case "MARK":
                return SerialPort.PARITY_MARK;
            case "SPACE":
                return SerialPort.PARITY_SPACE;
            case "ODD":
                return SerialPort.PARITY_ODD;
            default:
                throw new IOException("Parity are not chosen");
        }
    }

    private int getDelay() throws IOException {
        try {
            return Integer.parseInt(delay_field.getText());
        } catch (NumberFormatException e) {
            throw new IOException("Responce value is not INT");
        }
    }

    public boolean restartEngineConnection() {
        device.setText(engine_name);
        return restartDevice();
    }

    public boolean restartLockinConnection() {
        device.setText(lockin_name);
        return restartDevice();
    }
    public boolean restartLaserConnection()
    {
        device.setText(laser_name);
        return restartDevice();
    }
    public boolean restartPowerConnection()
    {
        device.setText(power_name);
        return restartDevice();
    }

    private boolean restartDevice() {
        critical = true;
        device.setDisable(true);
        updatePortList();
        connectionWindow.showAndWait();
        if (critical) {
            return !critical;
        } else {
            device.setDisable(false);
            return true;
        }
    }

    private void updateConfigDialog(String device, Connection connection) {
        Alert confirm_cancel = new Alert(Alert.AlertType.CONFIRMATION);
        confirm_cancel.setTitle("Connection");
        confirm_cancel.setHeaderText(device + " connection is updated\nConfirm save to config file?");
        Optional<ButtonType> option = confirm_cancel.showAndWait();
        if (option.get() == null) {
            //Nothing
        } else if (option.get() == ButtonType.OK) {
            try {
                Initializer.getConfig().updateDeviceConfig(device, connection.getPortname(), connection.getBaudrate(), connection.getDatabits(), connection.getStopbits(), connection.getParity(), connection.getResponcedelay());
            }
            catch (Exception e)
            {
                MainMenu.errorMessage("Config", "Fail to update config", e.toString(), e);
            }
        } else if (option.get() == ButtonType.CANCEL) {
            //Nothing
        } else {
            //Nothing
        }
    }
}
