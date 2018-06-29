package backend.devices;

import jssc.SerialPortException;

import java.io.IOException;

public class PowerChannel {
    private int channel;
    private double v_min;
    private double v_max;
    private double i_min;
    private double i_max;

    public PowerChannel(int channel, double v_min, double v_max, double i_min, double i_max) {
        this.channel = channel;
        this.v_min = v_min;
        this.v_max = v_max;
        this.i_min = i_min;
        this.i_max = i_max;
    }

    public PowerChannel(PowerChannel powerChannel) {
        this.channel = powerChannel.channel;
        this.v_min = powerChannel.v_min;
        this.v_max = powerChannel.v_max;
        this.i_min = powerChannel.i_min;
        this.i_max = powerChannel.i_max;
    }

    public void setVolage(double volage) throws SerialPortException, IOException, InterruptedException {
        Power.sendCommand(PowerSupplyCommands.setVoltage(channel, volage));
    }

    public void setCurrent(double current) throws SerialPortException, IOException, InterruptedException {
        Power.sendCommand(PowerSupplyCommands.setCurrent(channel, current));
    }

    public double getRealVolage() throws SerialPortException, IOException, InterruptedException {
        return Double.valueOf(Power.sendCommand(PowerSupplyCommands.getVoltageValue(channel)));
    }

    public double getRealCurrent() throws SerialPortException, IOException, InterruptedException {
        return Double.valueOf(Power.sendCommand(PowerSupplyCommands.getCurrentValue(channel)));
    }

    public double getTargetVolage() throws SerialPortException, IOException, InterruptedException {
        return Double.valueOf(Power.sendCommand(PowerSupplyCommands.getVoltageParameter(channel)));
    }

    public double getTargetCurrent() throws SerialPortException, IOException, InterruptedException {
        return Double.valueOf(Power.sendCommand(PowerSupplyCommands.getCurrentParameter(channel)));
    }
}
