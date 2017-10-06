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
                    CanReader.getInstance().sendMotorSteer((byte) 0, (byte)-40);
                }else{
                    System.out.println("Running motor");
                    CanReader.getInstance().sendMotorSteer((byte) 50, (byte)-40);
                    Thread.sleep(1000);
                    CanReader.getInstance().sendMotorSteer((byte) 0, (byte)-40);
                }

            } catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }
}