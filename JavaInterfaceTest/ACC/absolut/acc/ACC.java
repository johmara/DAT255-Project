package absolut.acc;

import absolut.can.CanReader;
import absolut.rmi.IMessageReceiver;
import absolut.rmi.RMIHandler;

public class ACC implements Runnable, IMessageReceiver {

    private CanReader can;
    private Regulator reg;

    public ACC() {
        RMIHandler.getInstance().addReceiver(this);
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
        int newControlSignal = 0;
        try {
            can.sendSteering((byte) 10);
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
                can.sendMotorSpeed((byte) newControlSignal);
            } catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }

    @Override
    public void messageReceived(String msg) {
        // Messages from APP
        System.out.println(msg);
    }
}