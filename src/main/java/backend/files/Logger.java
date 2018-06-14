package backend.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
    private static File log;
    private static FileWriter logwriter;
    private static Date startlogging;

    public static void init() throws IOException {
        startlogging = new Date();
        log = new File(FileManager.maindir, FileManager.convertToFileName(FileManager.getDateTimeStamp(startlogging)) + " session.log");
        log.createNewFile();
        logwriter = new FileWriter(log);
        addLogHead(FileManager.getDateTimeStamp(startlogging));
    }

    private static void addLogHead(String starttime) throws IOException {
        FileManager.addLineToFile(logwriter, "# " + starttime + " START NEW SESSION " + "#");
        logwriter.flush();
    }

    public static boolean addtoLog(String msg) {
        try {
            FileManager.addLineToFile(logwriter, msg);
            logwriter.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
