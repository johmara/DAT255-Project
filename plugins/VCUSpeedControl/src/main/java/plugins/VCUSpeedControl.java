package plugins;

import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class VCUSpeedControl extends PlugInComponent {
    private PluginPPort speed;
    private PluginRPort sensor;
    private PluginPPort steering;

    public VCUSpeedControl() {}

    public VCUSpeedControl(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        SCUDistReader instance = new SCUDistReader(args);
        instance.run();
    }

    @Override
    public void init() {
        // Initiate PluginPPort
        speed = new PluginPPort(this, "sp");
        steering = new PluginPPort(this, "st");
        sensor = new PluginRPort(this, "ab");
    }

    public void run() {
        init();
        doFunction();
        // do functions, for example, read front wheel speed value from sensor and then publish through MQTT
    }

    private void doFunction(){
        while (true) {
            try {
                if (sensor.readInt() < 100) {
                    speed.write(0);
                } else {
                    steering.write(0);
                    Thread.sleep(2000);
                    speed.write(7);
                    Thread.sleep(2000);
                    speed.write(0);
                }
            } catch (InterruptedException e) {
                //VM.println("Interrupted.");
            }
        }
    }
}