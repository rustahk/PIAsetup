package backend.files;

import backend.data.Point;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {

    public final static String maindir = "C:\\PIAsetup";;
    public final static File mainfile;
    public final static SimpleDateFormat time_format;
    public final static SimpleDateFormat date_format;
    public final static SimpleDateFormat data_time_format;

    static
    {
        mainfile = new File(maindir);
        time_format = new SimpleDateFormat("HH:mm:ss");
        date_format = new SimpleDateFormat("yyyy.MM.dd");
        data_time_format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    }

    public static void init() throws IOException
    {
        mainfile.mkdir();
        if(!mainfile.exists()) mainfile.createNewFile();
    }

    public static String convertToFileName(String stamp)
    {
        return stamp.replaceAll(":", "");
    }

    public static String getDateStamp(Date date)
    {
        return date_format.format(date);
    }
    public static String getTimeStamp(Date date)
    {
        return time_format.format(date);
    }

    public static String getDateTimeStamp(Date date)
    {
        return date_format.format(date) + " " + time_format.format(date);
    }

    public static void addLineToFile(FileWriter writer, String line) throws IOException
    {
        writer.write(line + "\r\n");
    }

    public static void addPointToFile(FileWriter writer, Point point) throws IOException
    {
        FileManager.addLineToFile(writer, point.getWavelenght() + "\t" + point.getValueX()+ "\t" + point.getValueY());
    }
}
