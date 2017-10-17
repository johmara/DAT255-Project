package absolut.acc;

import absolut.can.CanReader;
import java.io.IOException;

import absolut.img.GetPixelColor;
import org.omg.SendingContext.RunTime;

public class Main {

    public static void main(String args[]) throws IOException {
        ACC acc = new ACC();
        GetPixelColor pixelColor = new GetPixelColor();
        Thread accThread = new Thread(acc);
        accThread.start();
        pixelColor.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CanReader.getInstance().sendEmergencyShutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}