package absolut.acc;

import absolut.can.CanReader;
import absolut.rmi.IMessageReceiver;
import absolut.rmi.RMIHandler;

/**
 * The ACC implementation
 */
public class ACC implements Runnable, IMessageReceiver {

    private CanReader can;
    private Regulator reg;
    private boolean running = true;

    /**
     * Adds this as an listener of messages from the APP
     */
    public ACC() {
        RMIHandler.getInstance().addReceiver(this);
    }

    @Override
    public void run() {
        init();
        doFunction();
    }

    /**
     * Setups the regulator and CanReader
     */
    private void init() {
        reg = new Regulator();
        can = CanReader.getInstance();
    }

    /**
     * Sends some starting values and starts the value reading and motor speed signals
     */
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