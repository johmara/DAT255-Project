package absolut.acc;

import absolut.can.CanReader;
import java.util.Arrays;

public class Sensor{

    private CAN can;

    public Sensor(CAN can){
        this.can = can;
    }

    public int getDistance(){
        int[] a = getData();
        //Arrays.sort(a);
        System.out.println(Arrays.toString(a));
        if(a[0] == a[1]){
            return a[0];
        } else if(a[0] == a[2]){
            return a[0];
        } else if(a[1] == a[2]){
            return a[1];
        }
        return -1;
    }

    private int[] getData() {
        String data = CanReader.getInstance().getData();
        String[] sData = data.split(" ");
        int[] iData = new int[sData.length-2];
        System.out.println(Arrays.toString(sData));
        for (int i = 2; i < sData.length; i++) {
            iData[i-2] = Integer.parseInt(sData[i]);
        }
        return iData;
    }
}