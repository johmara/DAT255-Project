package main.java;

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
        VM.println("Circle.main()\r\n");
        PluginTest ap = new PluginTest(args);
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
    }

    public void doFunction() {
        try {
            if (sensor.readInt() < 200) {
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

        while (true) {
            try {
                Thread.sleep(10000);
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
