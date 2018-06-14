package backend.core;

import backend.data.Dataset;
import backend.data.HotPoint;
import backend.data.Point;
import backend.devices.*;
import backend.files.FileManager;
import backend.files.HotSave;
import backend.files.StandartSave;
import gui.HotPlot;
import gui.MainInterface;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//All methods here using "clean" and cheked values.
public class LogicCommands
{
    private static HotPoint hotPoint;

    private static List<PointRecipient> listeners = new ArrayList<PointRecipient>();

    public static Dataset startScan(Point start, Point finish, int numpoints, int delay)
    {
        int direction = 1;
        Point[] points = new Point[numpoints+1];
        if (start.getWavelenght() > finish.getWavelenght()) direction = -1;
        double scanstep = Math.abs(finish.getWavelenght() - start.getWavelenght()) / numpoints;
        Dataset dataset = new Dataset(points, new Date(), delay, scanstep);
        HotSave hotSave = new HotSave();
        hotSave.startHotSave(FileManager.getDateTimeStamp(dataset.getStarttime()));
        hotPoint = new HotPoint();
        HotPlot.setHotPoint(hotPoint);
        new Thread(new HotPlot()).start();
        for (int i = 0; i <= numpoints; i++)
        {
            points[i] = scanPoint(start.getWavelenght() + (scanstep * i * direction), delay);
            for(PointRecipient r : listeners) r.newPoint(points[i]);
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
        catch (Exception e)
        {
            return false;
        }
        return false;
    }
    public static void startControl()
    {

    }

    public static boolean saveScan(Dataset dataset)
    {
       return StandartSave.saveDataset(dataset);
    }

    private static Point scanPoint(double wavelenght, int delay)
    {
        Point point = new Point(Calibration.positionCalc(wavelenght), wavelenght);
        try
        {
            Engine.sendCommand(EngineByteCommands.moveTo(Calibration.positionCalc(wavelenght)));
        }
        catch (Exception e)
        {
            return null;
        }
        Engine.waitMoving();
        try
        {
            Thread.sleep(delay);
        }
        catch (InterruptedException e)
        {
            return null;
        }
        point.setValue(Lockin.sendCommand(LockinStringCommands.getOutputX()));
        HotPoint.updatePoint(hotPoint, point.getWavelenght(), Double.parseDouble(point.getValue()));
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
}
