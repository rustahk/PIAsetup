import backend.data.Dataset;
import backend.files.Loader;
import org.junit.Test;
import java.io.File;

public class LoaderTest {

    @Test
    public Dataset testLoadScan() throws Exception{
        return Loader.loadDataset(new File("C:\\PIAsetup\\2018.06.22 171507 test Scan.txt"));
    }
}
