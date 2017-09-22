/**
 * Acc version 1.0
 *
 * @author Johan Martinson, Joakim Willard, Daniel Rydén & Viktor Albihn.
 */
package plugins;

import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class Acc extends PlugInComponent {

    public PluginPPort pportSpeed;
    public PluginRPort rportDistance, rportFWheel, rportRWheel, rportBattery;

    private int speedSet, speedF, speedR, dist, sA, battery;

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
        rportBattery = new PluginRPort(this, "rportBattery");
        speedSet = 0;
    }

    public void run() {
        init();
        // do functions, for example, read front wheel speed value from sensor and then publish through MQTT
        while (true) {

            speedF = rportFWheel.readInt();
            speedR = rportRWheel.readInt();
            dist = rportDistance.readInt();
            battery = rportBattery.readInt();

            sA = (speedF + speedR) / 2;
            sA = Math.round(sA);

            //if (sA > 95) {
            if (dist < desiredDist()) {
                speedSet = maintainSpeed(sA) - aquireControllSignal(sA, 0); //aquire a negative control signal
            } else if (dist > desiredDist()) {
                speedSet = aquireControllSignal(sA, 1); //aquire a positive control signal
            } else {
                speedSet = maintainSpeed(sA);
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
     * @return a distance in cm
     */
    private int desiredDist() {
        return 30;
    }

    /**
     *
     * @return a controllsignal
     */
    private int maintainSpeed(int speed) {
        // f(x) = 0.98x - 22.3
        if (battery > 50%){
            return (speed*0.98)-22.3;
        }else {
            return ()
        }
        return 300; //standard värde för fel eftersom skalan går från -100 till 100
    }

    /**
     *
     * @return a controllsignal
     */
    private int aquireControllSignal(int speed, int index) {
        // f(x) = 0.98x - 22.3
        int speedtmp
        if (battery > 50%){
            /*decrease speed*/
            if (index == 0){
                speedtmp = speed-0.98;
                return Math.round((speedtmp*0.98)-22.3);
            }else if(index == 1){/*increase speed*/
                speedtmp = speed+0.98;
                return Math.round((speedtmp*0.98)-22.3);
            }
        }else{
            if (index == 0){
                speedtmp = speed-0.94;
                return Math.round((speedtmp*0.94)-10.2);
            }else if(index == 1){
                speedtmp = speed+0.94;
                return Math.round((speedtmp*0.94)-10.2);
            }
        }

        return 300;
    }


}
