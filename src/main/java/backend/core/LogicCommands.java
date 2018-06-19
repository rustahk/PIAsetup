package backend.core;

import backend.data.Dataset;
import backend.data.Point;
import backend.devices.*;
import backend.files.FileManager;
import backend.files.HotSave;
import backend.files.StandartSave;
import gui.ScanMenu;
import jssc.SerialPortException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//All methods here using "clean" and cheked values.
public class LogicCommands
{
    private static List<PointRecipient> listeners = new ArrayList<PointRecipient>();

    public static Dataset startScan(Point start, Point finish, int numpoints, int delay)
    {
        int direction = 1; //Scan direction (1 -> normal, -1 -> reverse)
        Point[] points = new Point[numpoints+1];
        if (start.getWavelenght() > finish.getWavelenght()) direction = -1;
        double scanstep = Math.abs(finish.getWavelenght() - start.getWavelenght()) / numpoints; //calc delta wavelenght between points
        //**Data saving
        Dataset dataset = new Dataset(points, new Date(), delay, scanstep);
        HotSave hotSave = new HotSave();
        hotSave.startHotSave(FileManager.getDateTimeStamp(dataset.getStarttime()));
        //**Cleaning all buffers
        Engine.getConnection().cleanInputBuffer();
        Lockin.getConnection().cleanInputBuffer();
        for (int i = 0; i <= numpoints; i++)
        {
            try
            {
                points[i] = scanPoint(start.getWavelenght() + (scanstep * i * direction), delay);
                for(PointRecipient r : listeners) r.newPoint(points[i]);
                ScanMenu.updateStatus(i, numpoints);
                if(Thread.interrupted()) throw new InterruptedException();
            }
            catch (InterruptedException e)
            {
                ServiceProcessor.serviceMessage("Scan interrupted by user");
                break;
            }
            catch (Exception e)
            {
                ErrorProcessor.standartError("Scan interrupted: ", e);
                break;
            }
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
    public static void startControl()
    {

    }

    public static boolean saveScan(Dataset dataset)
    {
       return StandartSave.saveDataset(dataset);
    }

    private static Point scanPoint(double wavelenght, int delay) throws IOException, SerialPortException, InterruptedException
    {
        Point point = new Point(wavelenght);
        Engine.sendCommand(EngineByteCommands.moveTo(point.getPosition()));
        Engine.waitMoving();
        Thread.sleep(delay);
        String value = Lockin.sendCommand(LockinStringCommands.getOutputX());
        if (value.equals(""))
        {
            throw new IOException("no value from lockin");
        }
        point.setValue(value);
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
}
