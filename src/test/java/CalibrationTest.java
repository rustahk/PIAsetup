import backend.devices.Calibration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CalibrationTest {



    @Before
    public void init() {
        Calibration.updFreqCalibr(200);
    }

    @Test
    public void testFreqCalc() {
        Assert.assertEquals(7.5, Calibration.voltageChpCalc(100), 0.0);
    }
}
