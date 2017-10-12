package absolut.acc;

public class Regulator {

    private Sensor sensor;
    private int preferredDistance;
    //private int sensorValue;
    private double K;
    private double Ti;
    private double Td;
    private double lastError;


    public Regulator(){
        init();
    }

    private void init(){
        preferredDistance = 100;
        K = 0.6;
        //K = 0.2;
        Ti = 100;
        Td = 0.4;
        lastError = 0;
        sensor = new Sensor();
    }


    public int calcNewSpeed(int lastControlSignal){

        double sensorValue = sensor.getDistance();
        double error = sensorValue - preferredDistance;
        double controlSignal;

        //controlSignal = K * (error + ((error - lastError) / Ti) + Td);


        controlSignal = K * error + ((lastError - error)/Ti) + Td + lastControlSignal;


        //controlSignal = K*(error+((1/Ti)*((0.15*(lastError-error))))+((Td*(lastError-error)/0.15)));
        controlSignal = clamp(Math.round(controlSignal), 0, 100);


        System.out.println(controlSignal);

        lastError = error;

        if(controlSignal < 7 && controlSignal > 0)
            return lastControlSignal;


        return (int) Math.round(controlSignal) + lastControlSignal;
    }

    private double clamp(double in, double min, double max) {
        return Math.max(min, Math.min(in, max));
    }

}
