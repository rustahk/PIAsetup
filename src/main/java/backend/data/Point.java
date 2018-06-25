package backend.data;

import backend.devices.Calibration;

public class Point
//This class is realize a datapoint of scaning
{
    private int position;//position in steps
    private double wavelenght;//position in nm
    //measured values //Saved like string to avoid troubles with noise/wrong read from port
    private double valueX;
    private double valueY;

    public int getPosition() {
        return position;
    }

    public double getWavelenght() {
        return wavelenght;
    }

    public double getValueX() {
        return valueX;
    }

    public void setValue(double x, double y) {
        this.valueX = x;
        this.valueY = y;
    }

    public double getValueY() {
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

    public Point(double wavelenght, double valueX, double valueY) {
        this.wavelenght = wavelenght;
        this.valueX = valueX;
        this.valueY = valueY;
    }
}