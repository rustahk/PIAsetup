package backend.files;

import backend.data.Dataset;
import backend.data.Point;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Loader { //It's rape of a RAM :(

    public static Dataset loadDataset(File scanfile) throws IOException, ParseException {
        String sample_name;
        Date start;
        Date finish;
        int delay;
        double step;
        int num_points;

        List<String> lines;
        Properties props = new Properties();
        FileInputStream in = new FileInputStream(scanfile);
        props.load(in);
        in.close();
        sample_name = props.getProperty("%SAMPLE");
        start = FileManager.data_time_format.parse(props.getProperty("%START"));
        finish = FileManager.data_time_format.parse(props.getProperty("%FINISH"));
        delay = Integer.parseInt(props.getProperty("%DELAY"));
        step = Double.parseDouble(props.getProperty("%STEP"));
        num_points = Integer.parseInt(props.getProperty("%TOTAL_NUMBER_OF_POINTS"));
        props = null;
        System.gc(); //For case of BIG files
        Point[] points = new Point[num_points];
        int j=0;
        lines = readFile(scanfile);
        for (String i : lines) {
            if(i.charAt(0)=='%')
            {
                //ignore
            }
            else
            {
                String[] k = i.split("\t");
                if(k.length!=3) throw new IOException("Bad point");
                points[j] = new Point(Double.parseDouble(k[0]), k[1], k[2]);
                j++;
            }
        }
        return new Dataset(points, start, finish, delay, step, sample_name);
    }

    private static List<String> readFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
        return lines;
    }
}
