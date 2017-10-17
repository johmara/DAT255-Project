package absolut.acc;

public class Regulator {

    private Sensor sensor;
    private int preferredDistance;
    //private int sensorValue;
    private double Kp;
    private double Ki;
    private double Kd;
    private double Dt;
    private double integral;
    private double lastError;

    /*Hugos kod*/
    //private double v1; /* Last speed given to MOPED */
    //private double lastSpeed; /* Desired speed */
    //private double vDes; /* Desired distance */
    //private double dDes; /* Acceleration */
    //private double accFactor; /* ?? */ /* private double a0; */ /* Multiplier */
    //private double k; /* Integrating factor */
    //private double i; /* Acceleration */
    //private double i_acc; /* dist1 and dist2 is used for simulator (I think), deltaDist is delta of dist1 and dist 2 */ /* private double dist1; */ /* private double dist2; */ /* private double deltaDist; */ /* Derivating factor */
    //private double d; /* Last error */
    //private double lastEr; /* K = 0.48 */ /* T0 = 9.3 */ /* I = 4.65 */ /* D = 1.16 */ /* Ticks */ /* private int s; */
    //private double maxSpeed; /* Limitations to regulator */
    //private double minSpeed;
    /*Slut på hugos kod*/


    public Regulator(){
        init();
    }

    private void init(){
        preferredDistance = 40;
        Kp = 0.85;
        Ki = 0.001;
        Kd = 0.001;
        Dt = 100;
        integral = 0;
        lastError = 0;

        sensor = new Sensor();

        /*Hugos kod*/
        /*v1 = 0;
        dDes = 20;
        k = 0.3;
        i = 0.04;
        i_acc = 0;
        d = 0.8;
        lastEr = 0;

        maxSpeed = 80;
        minSpeed = -80;*/
        /*Slut på hugos kod*/
    }


    public int calcNewSpeed() {

        double sensorValue = sensor.getDistance();
        double error = sensorValue - preferredDistance;
        double controlSignal;

        double derivate = 0;

        integral = integral + (error * Dt);
        derivate = (error - lastError) / Dt;

        integral = integral > 10 ? 10 : integral;
        integral = integral < -4 ? -4 : integral;

        System.out.println("Intergral: " + integral);

        controlSignal = Kp * error + (Ki * integral) + (Kd * derivate);

        controlSignal = clamp(Math.round(controlSignal), 0, 100);

        if(controlSignal > 40){
            controlSignal = 40;
        }
        if(controlSignal < -40){
            controlSignal = -40;
        }


        System.out.println(controlSignal);

        lastError = error;
        try {
            Thread.sleep((long)Dt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (int) Math.round(controlSignal);

        /*Hugos kod*/
        /*double sensorValue = sensor.getDistance();
        double error = sensorValue - dDes;

        if(Math.abs(error) < 100)
        {
            i_acc += error * i;
        }

        i_acc = i_acc > 10 ? 10 : i_acc;
        i_acc = i_acc < -4 ? -4 : i_acc;

        v1 = error * k + i_acc + (error - lastEr) * d;

        if (v1 < minSpeed)
        {
            v1 = minSpeed;
        }
        else if(v1 > maxSpeed)
        {
            v1 = maxSpeed;
        }

        System.out.println(v1);

        lastEr = error;

        return (int)v1;*/
    }

    private double clamp(double in, double min, double max) {
        return Math.max(min, Math.min(in, max));
    }

}
