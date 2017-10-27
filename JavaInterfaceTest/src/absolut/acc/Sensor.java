package absolut.acc;

import absolut.can.CanReader;
import java.util.Arrays;

/**
 * Gets the values from to sensor over the can bus
 */
public class Sensor{

    /**
     * Gets the median value of the sensor data
     * @return The median of the sensor data
     */
    public double getDistance(){
        int[] a = getData();
        Arrays.sort(a);
        double median;
        if (a.length % 2 == 1) {
            return ((double)a[a.length/2] + (double)a[a.length/2 - 1]) / 2.0; 
        } else {
            return (double)a[a.length/2];
        }
    }

    /**
     * Gets the raw data that was received from the can bus
     * @return A string with data from the can bus
     */
    public String getRawData() {
        return CanReader.getInstance().getData();
    }

    /**
     * A parsed list of data from teh sensor
     * @return A list of data
     */
    public int[] getData() {
        String data = getRawData();
        String[] sData = data.split(" ");
        int[] iData = new int[sData.length-2];
        for (int i = 2; i < sData.length; i++) {
            iData[i-2] = Integer.parseInt(sData[i]);
        }
        return iData;
    }
}