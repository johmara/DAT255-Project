package absolut.acc;

import absolut.can.CanReader;

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
        K = 0.2;
        Ti = 100;
        Td = 0.4;
        lastError = 0;
        sensor = new Sensor();
    }


    public int calcNewSpeed(){

        double sensorValue = sensor.getDistance();
        double error = sensorValue - preferredDistance;
        double controlSignal;

        //controlSignal = K * (error + ((error - lastError) / Ti) + Td);

        controlSignal = K*(1+(0.15/(Ti*1-(1/error)))+Td*((1-(1/lastError))/0.15));

        lastError = error;

        return (int) Math.round(controlSignal);
    }
}
