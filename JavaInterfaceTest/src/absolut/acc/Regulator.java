package absolut.acc;

public class Regulator {

    private Sensor sensor;
    private int preferredDistance;
    private double kp;
    private double ki;
    private double kd;
    private double dt;
    private double integral;
    private double lastError;

    public Regulator(){
        init();
    }

    /**
     * Setups the base values of the regulator
     */
    private void init(){
        preferredDistance = 40;
        kp = 0.85;
        ki = 0.00001;
        kd = 0.001;
        dt = 0.1;
        integral = 0;
        lastError = 0;

        sensor = new Sensor();
    }

    /**
     * Calculates the new speed based on the values form the sensor and last values
     * @return The control signal to send to the motor
     */
    public int calcNewSpeed() {

        double sensorValue = sensor.getDistance();
        double error = sensorValue - preferredDistance;
        double controlSignal;

        double derivate = 0;

        integral = integral + (error * dt);
        //derivate = (error - lastError) / dt;

        integral = integral > 4 ? 4 : integral;
        integral = integral < -10 ? -10 : integral;

        //System.out.println("Intergral: " + integral);

        controlSignal = kp * error + (ki * integral) + (kd * (error - lastError)/*derivate*/);

        controlSignal = clamp(Math.round(controlSignal), -100, 100);

        if(controlSignal > 15){
            controlSignal = 15;
        }
        if(controlSignal < -10){
            controlSignal = -10;
        }

        //System.out.println(controlSignal);

        lastError = error;
        /*try {
            Thread.sleep((long)dt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return (int) Math.round(controlSignal);
    }

    /**
     * Clamps a value between a minimum and maximum value
     * @param in The value to clamp
     * @param min The minimum value to return
     * @param max The maximum value to return
     * @return The value clamped if needed
     */
    private double clamp(double in, double min, double max) {
        return Math.max(min, Math.min(in, max));
    }
}
