import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//This class realize datastorage: saving scans and logging

public class DataStorage {

    private File maindir;
    private File log;
    private FileWriter logwriter;
    private SimpleDateFormat time;
    private SimpleDateFormat date;
    private boolean hotstatus;
    private File lastscan;
    private FileWriter hotwriter;

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
                addPointToFile(scanwriter, i);
            }
            scanwriter.flush();
            scanwriter.close();
            ConsoleOutput.serviceMessage("Scan has been saved");
        } catch (IOException e) {
            ConsoleOutput.errorMessage(e.toString());
        }
    }

    private void addLogHead(String mark) throws IOException {
        logwriter.write("# " + mark + " START NEW SESSION " + "#" + "\r\n");
        logwriter.flush();
    }

    public String getDataTimeStamp(Date stamp) {
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

    public boolean startHotSave(String comment)
    {
        if(hotstatus) return false;
        hotstatus=true;
        lastscan = new File(maindir.getAbsolutePath(), "lastscan.txt");
        try
        {
            if (lastscan.exists()) lastscan.delete();
            lastscan.createNewFile();
            hotwriter = new FileWriter(lastscan, false);
            addLineToFile(hotwriter, comment);
        }
        catch (IOException e)
        {
            ConsoleOutput.errorMessage(e.toString());
            return false;
        }
        return true;
    }

    public boolean addtoHotSave(Point point)
    {
        if(!hotstatus) return false;
        try
        {
            addPointToFile(hotwriter, point);
            hotwriter.flush();
        }
        catch (IOException e)
        {
            ConsoleOutput.errorMessage(e.toString());
            return false;
        }
        return true;
    }
    public boolean stopHotSave(String comment)
    {
        if(!hotstatus) return false;
        try {
            addLineToFile(hotwriter, comment);
            hotwriter.flush();
            hotwriter.close();
        }
        catch (IOException e)
        {
            ConsoleOutput.errorMessage(e.toString());
            return false;
        }
        hotstatus=false;
        return true;
    }

    private static void addPointToFile(FileWriter writer, Point point) throws IOException
    {
        addLineToFile(writer, point.getWavelenght() + " nm; " + point.getValue());
    }
    private static void addLineToFile(FileWriter writer, String line) throws IOException
    {
        writer.write(line + "\r\n");
    }
}
