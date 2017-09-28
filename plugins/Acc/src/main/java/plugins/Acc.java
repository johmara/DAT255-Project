/**
 * Acc version 1.0
 *
 * @author Johan Martinson, Joakim Willard, Daniel Rydén & Viktor Albihn.
 */
package plugins;

import com.sun.squawk.VM;
import sics.plugin.PlugInComponent;
import sics.port.PluginPPort;
import sics.port.PluginRPort;

public class Acc extends PlugInComponent {

    private PluginPPort pportSpeed;
    private PluginRPort rportDistance, rportFWheel, rportRWheel, rportBattery;

    private int speedSet, speedF, speedR, dist, sA, battery;
    private double sAdouble;

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
        pportSpeed = new PluginPPort(this, "pportSpeed");
        rportDistance = new PluginRPort(this, "rportDistance");
        rportFWheel = new PluginRPort(this, "rportFWheel");
        rportRWheel = new PluginRPort(this, "rportRWheel");
        rportBattery = new PluginRPort(this, "rportBattery");
        speedSet = 0;
    }

    public void run() {
        init();
        doFunction();
    }

    private int round(double num){

        double tmp;
        tmp = (num%10)%1;
        if(tmp >= 0.5){
            num -= tmp;
            tmp = 1;
            num += tmp;
            return (int)num;

        }else{
            num -= tmp;
            tmp = 1;
            num -= tmp;
            return (int)num;
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
        if (battery > battery * 0.5){
            return (int) ((speed*0.98)-22.3);
        }else {
            return (int) ((speed*0.94)-10.2); //satte bara ett värde för retur satsen
        }
        //return 300; //standard värde för fel eftersom skalan går från -100 till 100
    }

    /**
     *
     * @return a controllsignal
     */
    private int aquireControllSignal(int speed, int index) {
        // f(x) = 0.98x - 22.3
        int speedtmp;
        if (battery > battery * 0.5){
            /*decrease speed*/
            if (index == 0){
                speedtmp = (int) (speed-0.98);
                return round((speedtmp*0.98)-22.3);
            }else if(index == 1){/*increase speed*/
                speedtmp = (int)(speed+0.98);
                return round((speedtmp*0.98)-22.3);
            }
        }else{
            if (index == 0){
                speedtmp = (int)(speed-0.94);
                return round((speedtmp*0.94)-10.2);
            }else if(index == 1){
                speedtmp = (int)(speed+0.94);
                return round((speedtmp*0.94)-10.2);
            }
        }

        return 300;
    }

    private void doFunction() {
        while (true) {

            speedF = rportFWheel.readInt();
            speedR = rportRWheel.readInt();
            dist = readDist();
            battery = rportBattery.readInt();

            sAdouble = (speedF + speedR) / 2;
            sA = round(sAdouble);

            if (dist < desiredDist()) {
                speedSet = maintainSpeed(sA) - aquireControllSignal(sA,
                        0); //aquire a negative control signal
            } else if (dist > desiredDist()) {
                speedSet = aquireControllSignal(sA, 1); //aquire a positive control signal
            } else {
                speedSet = maintainSpeed(sA);
            }

            pportSpeed.write(speedSet);

            try {
                Thread.sleep(1000);
                pportSpeed.write(0); //låt motorn vila
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Receives an object from the SCU and does control checks on that object.
     * @return the distance from the sensor.
     */

    private int readDist(){
        int dist;
        Object obj = rportDistance.receive();
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
