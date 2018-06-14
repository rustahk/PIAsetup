package backend.files;

import backend.core.ErrorProcessor;
import backend.core.LogicCommands;
import backend.core.PointRecipient;
import backend.data.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HotSave implements PointRecipient {

    private static boolean hotstatus;
    private static File lastscan;
    private static FileWriter hotwriter;

    public boolean newPoint(Point point)
    {
        if(!hotstatus) return false;
        try
        {
            FileManager.addPointToFile(hotwriter, point);
            hotwriter.flush();
        }
        catch (IOException e)
        {
            ErrorProcessor.standartError("Fail to save hotpoint", e);
            return false;
        }
        return true;
    }

    public boolean startHotSave(String comment) {
        if (hotstatus) return false;
        LogicCommands.addPointRecipient(this);
        hotstatus = true;
        lastscan = new File(FileManager.mainfile.getAbsolutePath(), "lastscan.txt");
        try {
            if (lastscan.exists()) lastscan.delete();
            lastscan.createNewFile();
            hotwriter = new FileWriter(lastscan, false);
            FileManager.addLineToFile(hotwriter, comment);
        } catch (IOException e) {
            ErrorProcessor.standartError("Fail to start hotsave", e);
            return false;
        }
        return true;
    }

    public boolean stopHotSave(String comment) {
        if (!hotstatus) return false;
        try {
            FileManager.addLineToFile(hotwriter, comment);
            hotwriter.flush();
            hotwriter.close();
        } catch (IOException e) {
            ErrorProcessor.standartError("Fail to stop hotsaver", e);
            return false;
        }
        LogicCommands.removePointRecipient(this);
        hotstatus = false;
        return true;
    }
}
