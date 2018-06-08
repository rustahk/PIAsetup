import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalibrationTest {

    private final int speed = 2000;
    Calibration calibration;

    @Before
    public void init() {
        calibration = new Calibration();
    }

    @Test
    public void testSpeedLimit() {
        Assert.assertTrue(calibration.speedLimit(speed));
    }
}
