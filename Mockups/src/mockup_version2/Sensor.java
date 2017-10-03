package mockup_version2;

import java.util.concurrent.Semaphore;

public class Sensor implements Runnable{

    private int distance, fwVelocity, bwVelocity, battery;

    public Semaphore sensorSem;


    public Sensor(){
        this.battery = 100; //100% battery
        sensorSem = new Semaphore(1);

    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setBwVelocity(int bwVelocity) {
        this.bwVelocity = bwVelocity;
    }

    public void setFwVelocity(int fwVelocity) {
        this.fwVelocity = fwVelocity;
    }

    public int getDistance() {
        return distance;
    }

    public int getBwVelocity() {
        return bwVelocity;
    }

    public int getFwVelocity() {
        return fwVelocity;
    }

    public int getBattery() {
        return battery;
    }

    @Override
    public void run() {
        while(true) {
            System.out.println(distance);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
