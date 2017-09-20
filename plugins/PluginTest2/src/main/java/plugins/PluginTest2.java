package plugins;

import com.sun.squawk.VM;
import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class PluginTest2 extends PlugInComponent {

    public PluginPPort speed;
    public PluginPPort steering;
    public PluginRPort sensor;
    public PluginPPort sensorOutput;

    public PluginTest2(String[] args) {
        super(args);
    }

    public PluginTest2() {
    }

    public static void main(String[] args) {
        VM.println("Circle.main()\r\n");
        PluginTest2 ap = new PluginTest2(args);
        ap.run();
        VM.println("Circle-main done");
    }

    public void init() {
        // Initiate PluginPPort
        VM.println("init 1");
        speed = new PluginPPort(this, "sp");
        VM.println("init 2");
        sensor = new PluginRPort(this, "se");
        VM.println("init 3");
        steering = new PluginPPort(this, "st");
        VM.println("init 4");
        sensorOutput = new PluginPPort(this, "sen");
    }

    public void doFunction() {
        while (true) {
            try {
                steering.write(100);
                Thread.sleep(2000);
                speed.write(1);
                Thread.sleep(2000);
                speed.write(0);
                VM.println(String.valueOf(sensor.readInt()));
                sensorOutput.write("PluginTest2|" + String.valueOf(sensor.readInt()));
            } catch (InterruptedException e) {
                VM.println("Interrupted.");
            }
        }
    }

    public void run() {
        init();
        doFunction();
    }
}