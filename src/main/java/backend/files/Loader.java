package backend.files;

import backend.data.Dataset;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Loader {

    public static Dataset loadDataset(File scanfile)
    {
        List<String> lines;
        try
        {
            lines = readFile(scanfile);
            for(String i : lines)
            {
                System.out.println(i);
            }
        }
        catch (IOException e)
        {
            return null;
        }
        return null;
    }

    private static List<String> readFile(File file) throws IOException
    {
        List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
        return lines;
    }
}
