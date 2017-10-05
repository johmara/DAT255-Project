package absolut.acc;

import absolut.can.CanReader;
import static java.lang.Thread.interrupted;

public class ACC implements Runnable {

    private Sensor sensor;

    public ACC(){}

    @Override
    public void run() {
        init();
        doFunction();
    }

    private void init() {
        sensor = new Sensor();
    }

    private void doFunction() {
        double dist = 0;
        while(true){
            try{
                dist = sensor.getDistance();
                System.out.println("Dist: " + dist);
                if(dist < 100){
                    System.out.println("Stopping motor");
                    CanReader.getInstance().sendMotorSpeed((byte) 0);
                }else{
                    System.out.println("Running motor");
                    CanReader.getInstance().sendMotorSpeed((byte) 50);
                    Thread.sleep(1000);
                    CanReader.getInstance().sendMotorSpeed((byte) 0);
                }

            } catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }
}