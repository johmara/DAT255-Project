package absolut.can;

public class CanReader {

    private static CanReader instance = null;
    private CanManager canManager;

    private String data;

    private byte steerdata = 0;
    private byte motordata = 0;

    public static CanReader getInstance() {
        if (instance == null) {
            instance = new CanReader();
        }
        return instance;
    }

    private CanReader(){
        CanConfigParser.parseCanConfig("canConfig.xml");
        canManager = new CanManager(CanConfigParser.getSenders(), CanConfigParser.getReceivers());
        new Thread(canManager).start();
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
        String tmp = data;
        data = null;
        return tmp;
    }

    /**
     * Internally sets the distance data
     * @param s The data from the distance
     */
    public synchronized void setData(String s) {
        data = s;
        notify();
    }

    /**
     * Sets the speed of the motor
     * Valid values: -100 <-> 100
     * @param speed The speed to set
     */
    public void sendMotorSpeed(byte speed) {
        sendMotorSteer(speed, steerdata);
    }

    /**
     * Sets the current steering of the MOPED
     * Valid values: -100 <-> 100
     * @param steer The steering to set
     */
    public void sendSteering(byte steer) {
        sendMotorSteer(motordata, steer);
    }

    /**
     * Sets both the speed and steering on the MOPED
     * Valid values: -100 <-> 100
     * @param speed The speed to set
     * @param steer The steering to set
     */
    public void sendMotorSteer(byte speed, byte steer) {
        this.motordata = speed;
        this.steerdata = steer;
        canManager.sendMessage(new byte[] {motordata, steerdata});
    }
}
