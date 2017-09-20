package plugins;

import com.sun.squawk.VM;
import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class PluginTest extends PlugInComponent {

    public PluginPPort speed;
    public PluginPPort steering;
    public PluginRPort sensor;

    public PluginTest(String[] args) {
        super(args);
    }

    public PluginTest() {
    }

    public static void main(String[] args) {
        VM.println("PluginTest.main()\r\n");
        PluginTest ap = new PluginTest(args);
        ap.run();
        VM.println("PluginTest-main done");
    }

    public void init() {
        // Initiate PluginPPort
        VM.println("init 1");
        speed = new PluginPPort(this, "sp");
        VM.println("init 2");
        sensor = new PluginRPort(this, "se");
        VM.println("init 3");
        steering = new PluginPPort(this, "st");
    }

    public void doFunction() {
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

    public void run() {
        init();
        doFunction();
    }
}
