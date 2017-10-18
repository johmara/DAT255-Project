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
        ki = 0.001;
        kd = 0.001;
        dt = 100;
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
        derivate = (error - lastError) / dt;

        integral = integral > 10 ? 10 : integral;
        integral = integral < -4 ? -4 : integral;

        System.out.println("Intergral: " + integral);

        controlSignal = kp * error + (ki * integral) + (kd * derivate);

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
            Thread.sleep((long)dt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (int) Math.round(controlSignal);
    }

    private double clamp(double in, double min, double max) {
        return Math.max(min, Math.min(in, max));
    }
}
