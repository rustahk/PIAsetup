package backend.data;

import backend.devices.Calibration;

public class Point
//This class is realize a datapoint of scaning
{
    private int position;//position in steps
    private double wavelenght;//position in nm
    //measured values //Saved like string to avoid troubles with noise/wrong read from port
    private String valueX;
    private String valueY;

    public int getPosition() {
        return position;
    }

    public double getWavelenght() {
        return wavelenght;
    }

    public String getValueX() {
        return valueX;
    }

    public void setValue(String x, String y) {
        this.valueX = x;
        this.valueY = y;
    }

    public String getValueY() {
        return valueY;
    }

    public Point(int position, double wavelenght) {
        this.position = position;
        this.wavelenght = wavelenght;
        //this.setValue(null, null);
    }
    public Point(double wavelenght) {
        this.position = Calibration.positionCalc(wavelenght);
        this.wavelenght = wavelenght;
        //this.setValue(null, null);
    }
}