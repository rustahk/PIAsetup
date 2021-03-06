package backend.devices;

public class Calibration {

/*
This class realize calibration and positioning of the device mechanical system and safety checking
All these parameters - result of MANUAL device calibration(!!!)
*/

    //Workrange is standart limit for motor position
    private static final int minwstep = 0; //min workrange step //=1000nm
    private static final int maxwstep = 12799250; //max workrange step //=0nm
    private static final double maxwwave = 1000; //max workrange nm
    private static final double minwwave = 0;  //min workrange nm
    //Saferange is MAXIMUM/MINIMUM availble motor position. It's critical limits!!!
    private static final int minsstep = -1035257; //min saferange step
    private static final int maxsstep = 13598380; //max saferange step
    private static final double maxswave = 1081.6; //max saferange nm
    private static final double minswave = -6.1;  //min saferange nm

    public static int getMinwstep() {
        return minwstep;
    }

    public static int getMaxwstep() {
        return maxwstep;
    }

    public static boolean positionLimit(int step, boolean increaserange) //check that input position (in steps) in limit (increaserange - work/safety limit)
    {
        int maxstep;
        int minstep;
        if (increaserange) {
            maxstep = maxsstep;
            minstep = minsstep;
        } else {
            maxstep = maxwstep;
            minstep = minwstep;
        }
        if (step >= maxstep || step <= minstep) return false;
        return true;
    }

    public static boolean positionLimit(double wavelenght, boolean increaserange) //check that input position (in nm) in limit (increaserange - work/safety limit)
    {
        int maxstep;
        int minstep;
        if (increaserange) {
            maxstep = maxsstep;
            minstep = minsstep;
        } else {
            maxstep = maxwstep;
            minstep = minwstep;
        }
        int step = positionCalc(wavelenght);
        if (step > maxstep || step < minstep) return false;
        return true;
    }

    public static double wavelenghtCalc(int position) //recalc steps position to nm
    {
        double wavelenght;
        double a = position - minwstep;
        double b = minwwave - maxwwave;
        double c = maxwstep - minwstep;
        wavelenght = (a * b / c) + maxwwave;
        return wavelenght;
    }

    public static int positionCalc(double wavelenght) //recalc nm position to steps
    {
        double position;
        double a = wavelenght - maxwwave;
        double b = maxwstep - minwstep;
        double c = minwwave - maxwwave;
        position = (a * b / c) + minwstep;
        return (int) position;
    }

    public static boolean speedLimit(int speed) //check that speed value in limits
    {
        if (speed > 2047 || speed < 0) return false;
        return true;
    }
}
