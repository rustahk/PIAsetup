package gui;

import backend.data.HotPoint;

public class MainInterface extends Thread
{
    private HotPoint hotPoint;

    public MainInterface(HotPoint hotPoint) {
        this.hotPoint = hotPoint;
    }

    public void run() {
        HotPlot.setHotPoint(hotPoint);
        HotPlot.startHotPlot();
    }
}
