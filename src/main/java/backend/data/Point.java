package backend.data;

public class Point
//This class is realize a datapoint of scaning
{
    private int position;//position in steps
    private double wavelenght;//position in nm
    private String value;//measured value

    public int getPosition() {
        return position;
    }

    public double getWavelenght() {
        return wavelenght;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Point(int position, double wavelenght) {
        this.position = position;
        this.wavelenght = wavelenght;
        this.setValue(null);
    }
}