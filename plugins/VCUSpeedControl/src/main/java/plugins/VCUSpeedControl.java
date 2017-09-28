package plugins;

import com.sun.squawk.VM;
import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class VCUSpeedControl extends PlugInComponent {
    private PluginPPort speed;
    private PluginRPort sensor;
    private PluginPPort steering;
    //private PluginRPort frontWheelSpeed;
    //private PluginRPort backWheelSpeed

    public VCUSpeedControl() {}

    public VCUSpeedControl(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        VCUSpeedControl instance = new VCUSpeedControl(args);
        instance.run();
    }

    @Override
    public void init() {
        // Initiate PluginPPort
        speed = new PluginPPort(this, "sp");
        steering = new PluginPPort(this, "st");
        sensor = new PluginRPort(this, "ab");
        //frontWheelSpeed = new PluginRPort(this, "fw");
        //backWheelSpeed = new PluginRPort(this, "bw");
    }

    public void run() {
        init();
        doFunction();
        // do functions, for example, read front wheel speed value from sensor and then publish through MQTT
    }

    private void doFunction(){
        //int speedControlSignal = 7;
        //int actuallSpeed;
        while (true) {
            try {
                if (readDist() < 100) {
                    speed.write(0);
                    steering.write(0);
                } else {
                    steering.write(0);
                    Thread.sleep(2000);
                    speed.write(7/*speedControlSignal*/);
                    Thread.sleep(2000);
                    //actuallSpeed = (frontWheelSpeed.readInt() + backWheelSpeed.readInt()) / 2;
                    /*if(actuallSpeed <= 10 cm/s){
                        speedControlSignal++;
                    }
                     */
                    speed.write(0);
                }
            } catch (InterruptedException e) {
                //VM.println("Interrupted.");
            }
        }
    }

    /**
     * Receives an object from the SCU which it performs checks on.
     * @return the distance from the ultrasonic sensor
     */

    private int readDist(){
        int dist;
        Object obj = sensor.receive();
        if (obj != null) {
            String s = (String) obj;
            try {
                dist = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                VM.println("format exception (" + s + ")");
                dist = -1;
            }
        } else {
            dist = -1;
        }
        return dist;
    }
}