/**
 * Acc version 1.0
 *
 * @author Johan Martinson, Joakim Willard & Daniel Rydén
 */
package plugins;

import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class Acc extends PlugInComponent {

    public PluginPPort pportSpeed;
    public PluginRPort rportDistance, rportFWheel, rportRWheel;

    private int speedSet, speedF, speedR, dist, sA;

    public Acc() {
    }

    public Acc(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        Acc instance = new Acc(args);
        instance.run();
    }

    public void init() {
        // Initiate PluginPPort
        pportSpeed = new PluginPPort(this, "pport");
        rportDistance = new PluginRPort(this, "rportDistance");
        rportFWheel = new PluginRPort(this, "rportFWheel");
        rportRWheel = new PluginRPort(this, "rportRWheel");
        speedSet = 0;
    }

    public void run() {
        init();
        // do functions, for example, read front wheel speed value from sensor and then publish through MQTT
        while (true) {

            speedF = rportFWheel.readInt();
            speedR = rportRWheel.readInt();
            dist = rportDistance.readInt();

            sA = (speedF + speedR) / 2;
            sA = Math.round(sA);

            //if (sA > 95) {
            if (dist < desiredDist(sA)) {
                speedSet = maintainSpeed() - aquireControllSignal(); //aquire a negative control signal
            } else if (dist > desiredDist(sA)) {
                speedSet = aquireControllSignal(); //aquire a positive control signal
            } else {
                speedSet = aquireControllSignal();
            }
            //}

            pportSpeed.write(speedSet);

            try {
                Thread.sleep(1000);
                pportSpeed.write(0); //låt motorn vila
            } catch (InterruptedException e) {

            }
        }
    }

    /**
     *
     * @param sA cm/sec
     * @return a distance in cm
     */
    private int desiredDist(int sA) {

        return -1;
    }

    /**
     *
     * @return a controllsignal
     */
    private int maintainSpeed() {

        return 300; //standard värde för fel eftersom skalan går från -100 till 100
    }

    /**
     *
     * @return a controllsignal
     */
    private int aquireControllSignal() {

        return 300;
    }


}
