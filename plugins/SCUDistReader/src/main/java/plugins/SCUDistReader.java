package plugins;

import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class SCUDistReader extends PlugInComponent {
    private PluginPPort sensorOutput;
    private PluginRPort sensorInput;

    public SCUDistReader() {}

    public SCUDistReader(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        SCUDistReader instance = new SCUDistReader(args);
        instance.run();
    }

    @Override
    public void init() {
        // Initiate PluginPPort
        sensorOutput = new PluginPPort(this, "fs");
        sensorInput = new PluginRPort(this, "ff");
    }

    public void run() {
        init();
        doFunction();
        // do functions, for example, read front wheel speed value from sensor and then publish through MQTT
    }

    /**
     * Reads the distance from the ultrasonic sensor and then sends it over the CAN-bus ever half second
     */
    private void doFunction(){
        try{
            int dist;
            while(true) {
                dist = sensorInput.readInt();
                sensorOutput.send(String.valueOf(dist));
                Thread.sleep(500);
            }
        } catch(InterruptedException ie){
            ie.printStackTrace();
        }
    }
}

