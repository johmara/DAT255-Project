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
        Ti = 500000000;
        Td = 0;
        lastError = 0;
        sensor = new Sensor();
    }


    public int calcNewSpeed(){

        double sensorValue = sensor.getDistance();
        double error = sensorValue - preferredDistance;
        double controlSignal;

        controlSignal = error * K;

        return (int) Math.round(controlSignal);
    }
}
