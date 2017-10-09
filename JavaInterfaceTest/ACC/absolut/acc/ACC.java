package absolut.acc;

import absolut.can.CanReader;
import static java.lang.Thread.interrupted;

public class ACC implements Runnable {

    private CanReader can;
    private Regulator reg;

    public ACC() {
    }

    @Override
    public void run() {
        init();
        doFunction();
    }

    private void init() {
        reg = new Regulator();
        can = CanReader.getInstance();
    }

    private void doFunction() {
        //double dist = 0;
        int lastControlSignal = 0;
        int newControlSignal = 0;
        try {
            can.sendSteering((byte) -40);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                //dist = sensor.getDistance();
                //System.out.println("Dist: " + dist);
                /*if(dist < 100){
                    System.out.println("Stopping motor");
                    CanReader.getInstance().sendMotorSteer((byte) 0, (byte)-40);
                }else{
                    System.out.println("Running motor");
                    CanReader.getInstance().sendMotorSteer((byte) 50, (byte)-40);
                    Thread.sleep(1000);
                    CanReader.getInstance().sendMotorSteer((byte) 0, (byte)-40);
                }*/
                /*
                * if sats som kollar newSpeed
                * */
                /*if ((lastControlSignal += reg.calcNewSpeed()) <= 0 || lastControlSignal > 127) {
                    newControlSignal = 0;
                } else {
                    newControlSignal = lastControlSignal;
                }*/
                newControlSignal = reg.calcNewSpeed();



                lastControlSignal = newControlSignal;

                can.sendMotorSpeed((byte) newControlSignal);
            } catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }
}