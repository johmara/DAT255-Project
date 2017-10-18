package absolut.can;

public class CanReader implements Runnable {

    private static volatile CanReader instance = null;
    private CanManager canManager;

    private String data;
    private byte[] sendData;

    private byte steerdata = 0;
    private byte motordata = -1;

    public synchronized static CanReader getInstance() {
        if (instance == null) {
            instance = new CanReader();
        }
        return instance;
    }

    private CanReader(){
        CanConfigParser.parseCanConfig("canConfig.xml");
        canManager = new CanManager(CanConfigParser.getSenders(), CanConfigParser.getReceivers());
        new Thread(canManager).start();
        new Thread(this).start();
    }

    public CanManager getCanManager() {
        return canManager;
    }

    /**
     * Gets the distance data
     * @return The distance data from SCU
     */
    public synchronized String getData() {
        while (data == null) {
            try {
                wait();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        //String tmp = data;
        return data;//tmp;
    }

    /**
     * Internally sets the distance data
     * @param s The data from the distance
     */
    public synchronized void setData(String s) {
        data = s;
        notifyAll();
    }

    /**
     * Sets the speed of the motor
     * Valid values: -100 <-> 100
     * @param speed The speed to set
     */
    public void sendMotorSpeed(byte speed) throws InterruptedException {
        sendMotorSteer(speed, steerdata);
    }

    /**
     * Sets the current steering of the MOPED
     * Valid values: -100 <-> 100
     * @param steer The steering to set
     */
    public void sendSteering(byte steer) throws InterruptedException {
        sendMotorSteer(motordata, steer);
    }

    public synchronized void sendEmergencyShutdown() throws InterruptedException {
        sendData = new byte[] {0, 0};
        canManager.sendMessage(sendData);
    }

    /**
     * Sets both the speed and steering on the MOPED
     * Valid values: -100 <-> 100
     * @param speed The speed to set
     * @param steer The steering to set
     */
    public void sendMotorSteer(byte speed, byte steer) throws InterruptedException {
        byte tmpSpeed = clamp(speed, (byte)-100, (byte)100);
        byte tmpSteer = clamp(steer, (byte)-100, (byte)100);
        if (motordata == tmpSpeed && steerdata == tmpSteer) return;

        this.motordata = tmpSpeed;
        this.steerdata = tmpSteer;
        sentMessage(new byte[] {motordata, steerdata});
        //canManager.sendMessage(new byte[] {motordata, steerdata});
        //Thread.sleep(10);
    }

    private synchronized void sentMessage(byte[] data) {
        sendData = data;
        notifyAll();
    }
    private synchronized void sendMessage() {
        while (sendData == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        canManager.sendMessage(sendData);
        sendData = null;
        notifyAll();
    }

    private byte clamp(byte in, byte min, byte max) {
        return (byte) Math.max(min, Math.min(in, max));
    }

    @Override
    public void run() {
        while (true) {
            try {
                sendMessage();
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
