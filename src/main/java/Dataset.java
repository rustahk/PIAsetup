import java.util.Date;

/*
This class realize a single scan - array of points and support data
 */

public class Dataset {
    private Point[] points;
    private Date starttime;
    private Date finishtime;
    private int delay;
    private double step;

    public Dataset(Point[] points, Date starttime, int delay, double step) {
        this.points = points;
        this.starttime = starttime;
        this.delay = delay;
        this.step = step;
    }

    public void setFinishtime(Date finishtime) {
        this.finishtime = finishtime;
    }

    public Point[] getPoints() {
        return points;
    }

    public Date getStarttime() {
        return starttime;
    }

    public Date getFinishtime() {
        return finishtime;
    }

    public int getDelay() {
        return delay;
    }

    public double getStep() {
        return step;
    }
}
