package backend.core;

import backend.data.Dataset;
import backend.data.DatasetsDifferent;
import backend.data.Point;
import backend.devices.*;
import backend.files.FileManager;
import backend.files.HotSave;
import backend.files.Saver;
import gui.MainMenu;
import jssc.SerialPortException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//All methods here using "clean" and cheked values.
public class LogicCommands
{
    private static List<PointRecipient> listeners = new ArrayList<PointRecipient>();

    public static Dataset startScan(String sample_name, Point start, Point finish, int numpoints, int delay) throws Exception
    {
        int direction = 1; //Scan direction (1 -> normal, -1 -> reverse)
        Point[] points = new Point[numpoints+1];
        if (start.getWavelenght() > finish.getWavelenght()) direction = -1;
        double scanstep = Math.abs(finish.getWavelenght() - start.getWavelenght()) / numpoints; //calc delta wavelenght between points
        //**Data saving
        Dataset dataset = new Dataset(sample_name, points, new Date(), delay, scanstep);
        HotSave hotSave = new HotSave();
        hotSave.startHotSave(FileManager.getDateTimeStamp(dataset.getStarttime()));
        //**Cleaning all buffers
        try {
            Engine.getConnection().cleanInputBuffer();
            Lockin.getConnection().cleanInputBuffer();
            for (int i = 0; i <= numpoints; i++) {

                    points[i] = scanPoint(start.getWavelenght() + (scanstep * i * direction), delay);
                    for (PointRecipient r : listeners) r.newPoint(points[i]);
                    MainMenu.updateStatus(i, numpoints);
                    if (Thread.interrupted()) throw new InterruptedException();
            }
        }
        catch (InterruptedException e) {
            ServiceProcessor.serviceMessage("Scan interrupted by user");
            throw e;
        } catch (Exception e) {
            ErrorProcessor.standartError("Scan interrupted: ", e);
            throw e;
        }
        dataset.setFinishtime(new Date());
        hotSave.stopHotSave(FileManager.getDateTimeStamp(dataset.getFinishtime()));
        return dataset;
    }
    public static boolean calibratePosition(Point currentpoint)
    {
        try {
            Engine.sendCommand(EngineByteCommands.setPosition(currentpoint.getPosition()));
            Engine.sendCommand(EngineByteCommands.motorStop()); //After first calibration motor try to move
        }
        catch (SerialPortException e)
        {
            return false;
        }
        catch (IOException e)
        {
            return false;
        }
        catch (InterruptedException e)
        {
            return false;
        }
        return true;
    }

    public static boolean saveScan(Dataset dataset)
    {
       return Saver.saveDataset(dataset);
    }

    private static Point scanPoint(double wavelenght, int delay) throws IOException, SerialPortException, InterruptedException
    {
        Point point = new Point(wavelenght);
        Engine.sendCommand(EngineByteCommands.moveTo(point.getPosition()));
        Engine.waitMoving();
        Thread.sleep(delay);
        String value = Lockin.sendCommand(LockinStringCommands.getOutputXY());
        if (value.equals("")) //This is here, because usually Lockin hasn't answer
        {
            throw new IOException("no value from lockin");
        }
        try {
            String[] xy = value.split(",");
            point.setValue(Double.parseDouble(xy[0]), Double.parseDouble(xy[1]));
        }
        catch (NumberFormatException e)
        {
            ErrorProcessor.standartError("Wrong value from lockin", e);
            throw new IOException(e.getMessage() + " parse error");
        }
        //point.setValue(Math.sin(wavelenght/10)*5+""); //$RANDOM_VALUES FOR TESTING
        return point;
    }

    public static void addPointRecipient(PointRecipient toAdd)
    {
        listeners.add(toAdd);
    }

    public static void removePointRecipient(PointRecipient toRemove)
    {
        if(listeners.size()>0)listeners.remove(toRemove);
    }
    public static void moveTo(Point target)
    {
        try {
            Engine.sendCommand(EngineByteCommands.moveTo(target.getPosition()));
        }
        catch (Exception e)
        {
            MainMenu.errorMessage("Engine", "Fail to move", e.toString(), e);
        }
    }
    public static Dataset normalizeData(Dataset data, Dataset reference) throws DatasetsDifferent //value_data/value_ref
    {
        if(data.getPoints().length!=reference.getPoints().length) throw new DatasetsDifferent("Different number of points");
        if(data.getPoints()[0].getWavelenght()!=reference.getPoints()[0].getWavelenght()) throw new DatasetsDifferent("Different start wavelenght");
        if(data.getPoints()[data.getPoints().length-1].getWavelenght()!=reference.getPoints()[reference.getPoints().length-1].getWavelenght()) throw new DatasetsDifferent("Different finish wavelenght");
        Point[] normalized = new Point[data.getPoints().length];
        for(int i = 0; i<data.getPoints().length; i++)
        {
            double x = (data.getPoints()[i].getValueX())/(reference.getPoints()[i].getValueX());
            double y=  (data.getPoints()[i].getValueY())/(reference.getPoints()[i].getValueY());
            normalized[i] = new Point(data.getPoints()[i].getWavelenght(), x,y);
        }
        return new Dataset(normalized, data.getStarttime(), data.getFinishtime(), data.getDelay(), data.getStep(), data.getSample_name()+" [NORMALIZED]");
    }
}
