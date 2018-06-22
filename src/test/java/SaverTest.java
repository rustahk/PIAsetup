import backend.core.LogicCommands;
import backend.data.Dataset;
import backend.data.Point;
import backend.devices.Calibration;
import backend.files.FileManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class SaverTest {

    @Before
    public void init() throws IOException {
        FileManager.init();
    }

    @Test
    public void testSaveScan() {
        Point[] points = {new Point(1), new Point(2), new Point(3)};
        points[0].setValue("1.0", "2.0");
        points[1].setValue("1.0", "2.0");
        points[2].setValue("1.0", "2.0");
        Dataset dataset = new Dataset("test",points, new Date(), 125, 1);
        dataset.setFinishtime(new Date());
        LogicCommands.saveScan(dataset);
    }
}
