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

        if(controlSignal > 10){
            controlSignal = 10;
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

    private double clamp(double in, double min, double max) {
        return Math.max(min, Math.min(in, max));
    }
}
