package backend.files;

import backend.core.ErrorProcessor;
import backend.core.ServiceProcessor;
import backend.data.Dataset;
import backend.data.Point;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StandartSave {

    public static boolean saveDataset(Dataset dataset) {
        File scanfile;
        try {
            scanfile = new File(FileManager.mainfile.getAbsolutePath(), FileManager.convertToFileName(FileManager.getDateTimeStamp(dataset.getStarttime())) +" " + dataset.getSample_name() +" Scan.txt");
            scanfile.createNewFile();
            FileWriter scanwriter = new FileWriter(scanfile, false);
            scanwriter.write(scanHead(dataset));
            for (Point i : dataset.getPoints()) {
                FileManager.addPointToFile(scanwriter, i);
            }
            scanwriter.flush();
            scanwriter.close();
        } catch (IOException e) {
            ErrorProcessor.standartError("Fail to save file", e);
            return false;
        }
        ServiceProcessor.serviceMessage("Scan saved: " + scanfile.getName());
        return true;
    }

    private static String scanHead(Dataset dataset) {
        String header =
                "%SAMPLE: " + dataset.getSample_name() + "\r\n" +
                        "%START: " + FileManager.getDateTimeStamp(dataset.getStarttime()) + "\r\n" +
                        "%FINISH: " + FileManager.getDateTimeStamp(dataset.getFinishtime()) + "\r\n" +
                        "%DELAY: " + dataset.getDelay() + "\r\n" +
                        "%STEP: " + dataset.getStep() + "\r\n" +
                        "%TOTAL NUMBER OF POINTS: " + dataset.getPoints().length + "\r\n" +
                        "%nm, singal_x, signal_y" + "\r\n";
        return header;
    }

}
