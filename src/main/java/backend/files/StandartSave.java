package backend.files;

import backend.core.ErrorProcessor;
import backend.data.Dataset;
import backend.data.Point;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StandartSave
{

    public static boolean saveDataset(Dataset dataset)
    {
        File scanfile;
        try {
            scanfile = new File(FileManager.mainfile.getAbsolutePath(), FileManager.convertToFileName(FileManager.getDateTimeStamp(dataset.getStarttime())) + " Scan.txt");
            scanfile.createNewFile();
            FileWriter scanwriter = new FileWriter(scanfile, false);
            scanwriter.write(scanHead(dataset));
            for (Point i : dataset.getPoints()) {
                FileManager.addPointToFile(scanwriter, i);
            }
            scanwriter.flush();
            scanwriter.close();
        } catch (IOException e)
        {
            return false;
        }
        return true;
    }

    private static String scanHead(Dataset dataset) {
        String line1 = "START: " + FileManager.getDateTimeStamp(dataset.getStarttime()) + "\r\n";
        String line2 = "FINISH: " + FileManager.getDateTimeStamp(dataset.getFinishtime()) + "\r\n";
        String line3 = "DELAY: " + dataset.getDelay() + "\r\n";
        String line4 = "STEP: " + dataset.getStep() + "\r\n";
        String line5 = "TOTAL NUMBER OF POINTS: " + dataset.getPoints().length + "\r\n";
        return line1 + line2 + line3 + line4 + line5;
    }

}
