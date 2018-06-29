package backend.devices;

public class Chopper extends PowerChannel{

    private static Chopper chopper;

    public Chopper(PowerChannel powerChannel) {
        super(powerChannel);
        chopper=this;
    }

    public static Chopper getChopper() {
        return chopper;
    }
}
