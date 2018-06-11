import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataStorage {

    private File maindir;
    private File log;
    private FileWriter logwriter;
    private SimpleDateFormat time;
    private SimpleDateFormat date;

    public DataStorage(String directory) {
        time = new SimpleDateFormat("HH:mm:ss");
        date = new SimpleDateFormat("yyyy.MM.dd");
        String filemark = getDataTimeStamp(new Date());
        try {
            this.maindir = new File(directory);
            maindir.mkdir();
            if (!maindir.exists()) maindir.createNewFile();
            log = new File(directory, convertTimeToFileName(filemark) + " session.log");
            log.createNewFile();
            logwriter = new FileWriter(log, true);
            addLogHead(filemark);
        } catch (IOException e) {
            ConsoleOutput.errorMessage(e.toString());
        }
    }

    public void addtoLog(String msg) {
        try {
            logwriter.write(getTimeStamp() + " " + msg + "\r\n");
            logwriter.flush();
        } catch (IOException e) {
            ConsoleOutput.errorMessage(e.toString());
        }
    }

    public void saveScan(Dataset dataset) //Save dataset after scaning
    {
        File scanfile;
        try {
            scanfile = new File(maindir.getAbsolutePath(), convertTimeToFileName(getDataTimeStamp(dataset.getStarttime())) + " Scan.txt");
            scanfile.createNewFile();
            FileWriter scanwriter = new FileWriter(scanfile, false);
            scanwriter.write(scanHead(dataset));
            for (Point i : dataset.getPoints()) {
                scanwriter.write(i.getWavelenght() + " nm; " + i.getValue() + "\r\n");
            }
            scanwriter.flush();
            ConsoleOutput.serviceMessage("Scan has been saved");
        } catch (IOException e) {
            ConsoleOutput.errorMessage(e.toString());
        }
    }

    private void addLogHead(String mark) throws IOException {
        logwriter.write("# " + mark + " START NEW SESSION " + "#" + "\r\n");
        logwriter.flush();
    }

    private String getDataTimeStamp(Date stamp) {
        return date.format(stamp) + " " + time.format(stamp);
    }

    private String getTimeStamp() {
        Date creation = new Date();
        return time.format(creation);
    }

    private String convertTimeToFileName(String mark) {
        return mark.replaceAll(":", "");
    }

    private String scanHead(Dataset dataset) {
        String line1 = "START: " + getDataTimeStamp(dataset.getStarttime()) + "\r\n";
        String line2 = "FINISH: " + getDataTimeStamp(dataset.getFinishtime()) + "\r\n";
        String line3 = "DELAY: " + dataset.getDelay() + "\r\n";
        String line4 = "STEP: " + dataset.getStep() + "\r\n";
        String line5 = "TOTAL NUMBER OF POINTS: " + dataset.getPoints().length + "\r\n";
        return line1 + line2 + line3 + line4 + line5;
    }
}
