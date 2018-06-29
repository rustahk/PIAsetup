package backend.devices;

public class Calibration {

/*
This class realize calibration and positioning of the device mechanical system and safety checking
All these parameters - result of MANUAL device calibration(!!!)
*/

    //Workrange is standart limit for motor position
    private static int min_work_step;// = 0; //min workrange step //=1000nm
    private static int max_work_step;// = 12799250; //max workrange step //=0nm
    private static double max_work_wave;// = 1000; //max workrange nm
    private static double min_work_wave;// = 0;  //min workrange nm
    //Saferange is MAXIMUM/MINIMUM availble motor position. It's critical limits!!!
    private static int min_safe_step;// = -1035257; //min saferange step
    private static int max_safe_step;// = 13598380; //max saferange step
    private static double max_safe_wave;// = 1081.6; //max saferange nm
    private static double min_safe_wave;// = -6.1;  //min saferange nm
    //
    private static double freq_max;
    //HARD VOLTAGE LIMITS FOR CHOPPER
    private static final double vchp_max = 15;
    private static final double vchp_min = 0;
    //
    private int min_workstep;
    private int max_workstep;
    private double min_workwave;
    private double max_workwave;
    //
    private int min_safestep;
    private int max_safestep;
    private double min_safewave;
    private double max_safewave;

    public Calibration(int min_work_step, int max_work_step, double min_work_wave, double max_work_wave, int min_safe_step, int max_safe_step, double min_safe_wave, double max_safe_wave) {
        this.min_workstep = min_work_step;
        this.max_workstep = max_work_step;
        this.min_workwave = min_work_wave;
        this.max_workwave = max_work_wave;
        this.min_safestep = min_safe_step;
        this.max_safestep = max_safe_step;
        this.min_safewave = min_safe_wave;
        this.max_safewave = max_safe_wave;
    }

    public static void init(Calibration calibration) {
        Calibration.min_work_step = calibration.min_workstep; //min workrange step //=1000nm
        Calibration.max_work_step = calibration.max_workstep; //max workrange step //=0nm
        Calibration.min_work_wave = calibration.min_workwave;  //min workrange nm
        Calibration.max_work_wave = calibration.max_workwave; //max workrange nm
        //
        Calibration.min_safe_step = calibration.min_safestep;
        Calibration.max_safe_step = calibration.max_safestep;
        Calibration.min_safe_wave = calibration.min_safewave;
        Calibration.max_safe_wave = calibration.max_safewave;
    }

    public static int getMin_work_step() {
        return min_work_step;
    }

    public static int getMax_work_step() {
        return max_work_step;
    }

    public static boolean positionLimit(int step, boolean increaserange) //check that input position (in steps) in limit (increaserange - work/safety limit)
    {
        int maxstep;
        int minstep;
        if (increaserange) {
            maxstep = max_safe_step;
            minstep = min_safe_step;
        } else {
            maxstep = max_work_step;
            minstep = min_work_step;
        }
        if (step >= maxstep || step <= minstep) return false;
        return true;
    }

    public static boolean positionLimit(double wavelenght, boolean increaserange) //check that input position (in nm) in limit (increaserange - work/safety limit)
    {
        int maxstep;
        int minstep;
        if (increaserange) {
            maxstep = max_safe_step;
            minstep = min_safe_step;
        } else {
            maxstep = max_work_step;
            minstep = min_work_step;
        }
        int step = positionCalc(wavelenght);
        if (step > maxstep || step < minstep) return false;
        return true;
    }

    public static double wavelenghtCalc(int position) //recalc steps position to nm
    {
        double wavelenght;
        double a = position - min_work_step;
        double b = min_work_wave - max_work_wave;
        double c = max_work_step - min_work_step;
        wavelenght = (a * b / c) + max_work_wave;
        return wavelenght;
    }

    public static int positionCalc(double wavelenght) //recalc nm position to steps
    {
        double position;
        double a = wavelenght - max_work_wave;
        double b = max_work_step - min_work_step;
        double c = min_work_wave - max_work_wave;
        position = (a * b / c) + min_work_step;
        return (int) position;
    }

    public static boolean speedLimit(int speed) //check that speed value in limits
    {
        if (speed > 2047 || speed < 0) return false; //HARD SPEED LIMIT
        return true;
    }

    public static void updFreqCalibr(double freq_max) {
        Calibration.freq_max = freq_max;
    }

    public static double voltageChpCalc(double freq) {
        double voltage = (vchp_max*freq) / freq_max;
        if (voltage>vchp_max || voltage<vchp_min) throw new IllegalArgumentException(freq + "Hz -> " + voltage + "V out of limit"); //For hardware safety
        return voltage;
    }

    //public static double freqChpCalc(double voltage)
}
