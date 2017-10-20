package absolut.acc;

import absolut.can.CanReader;
import absolut.rmi.IMessageReceiver;
import absolut.rmi.RMIHandler;

public class ACC implements Runnable, IMessageReceiver {

    private CanReader can;
    private Regulator reg;
    private boolean running = true;

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
        int newControlSignal = 0;
        try {
            can.sendSteering((byte) -50);
            can.sendMotorSpeed((byte) 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            if (running) {
                try {
                    newControlSignal = reg.calcNewSpeed();
                    can.sendMotorSpeed((byte) newControlSignal);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    @Override
    public void messageReceived(String msg) {
        if (msg != null && !msg.isEmpty()) {
            running = msg.contains("ON");
        }
    }
}