package absolut.can;

/**
 * A singleton CanReader
 */
public class CanReader implements Runnable {

    private static volatile CanReader instance = null;
    private CanManager canManager;

    private String data;
    private byte[] sendData;

    private byte steerdata = 0;
    private byte motordata = -1;

    /**
     * Gets the instance of the CanReader
     * @return The singleton instance
     */
    public synchronized static CanReader getInstance() {
        if (instance == null) {
            instance = new CanReader();
        }
        return instance;
    }

    /**
     * Setups the canreader and starts threads that are necessary
     * The file canConfig.xml needs to be available in the current directory as this is run from
     */
    private CanReader(){
        CanConfigParser.parseCanConfig("canConfig.xml");
        canManager = new CanManager(CanConfigParser.getSenders(), CanConfigParser.getReceivers());
        new Thread(canManager).start();
        new Thread(this).start();
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
        return data;
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

    /**
     * Sends a [0, 0] to the VCU that will set the speed to 0 and steering to 0
     */
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

    /**
     * Clamps a value between a minimum and maximum value
     * @param in The value to clamp
     * @param min The minimum value to return
     * @param max The maximum value to return
     * @return The value clamped if needed
     */
    private byte clamp(byte in, byte min, byte max) {
        return (byte) Math.max(min, Math.min(in, max));
    }

    /**
     * Starts the reader
     */
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
