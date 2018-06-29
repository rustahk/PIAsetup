package backend.devices;

public class Lamp extends PowerChannel{

    private static Lamp lamp;

    public Lamp(PowerChannel powerChannel) {
        super(powerChannel);
        lamp=this;
    }

    public static Lamp getLamp() {
        return lamp;
    }
}
