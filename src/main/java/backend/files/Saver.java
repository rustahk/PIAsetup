package backend.files;

import backend.core.ErrorProcessor;
import backend.core.ServiceProcessor;
import backend.data.Dataset;
import backend.data.Point;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

public class Saver {

    public static boolean saveDataset(Dataset dataset) {
        File scanfile;
        try {
            File savedir = new File(FileManager.maindir+"\\"+"Scans");
            savedir.mkdir();
            if(!savedir.exists()) savedir.createNewFile();
            scanfile = new File(savedir.getAbsolutePath(), FileManager.convertToFileName(FileManager.getDateTimeStamp(dataset.getStarttime())) +" " + dataset.getSample_name() +" Scan.txt");
            scanfile.createNewFile();
            FileWriter scanwriter = new FileWriter(scanfile, false);
            scanHead(dataset, scanwriter);
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

    private static void scanHead(Dataset dataset, FileWriter writer) throws IOException{
        LinkedList<String> header = new LinkedList<String>();
        header.add("%SAMPLE: " + dataset.getSample_name());
        header.add("%START: " + FileManager.data_time_format.format(dataset.getStarttime()));
        header.add("%FINISH: " + FileManager.data_time_format.format(dataset.getFinishtime()));
        header.add("%DELAY: " + dataset.getDelay());
        header.add("%STEP: " + dataset.getStep());
        header.add("%TOTAL_NUMBER_OF_POINTS: " + dataset.getPoints().length);
        header.add("%nm, singal_x, signal_y");
        for(String i : header) FileManager.addLineToFile(writer, i);
    }

}
