package absolut.acc;

import absolut.can.CanReader;
import java.io.IOException;

import absolut.img.GetPixelColor;
import org.omg.SendingContext.RunTime;

public class Main {

    public static void main(String args[]) throws IOException {
        ACC acc = new ACC();
        Thread accThread = new Thread(acc);
        accThread.start();
        if (args.length > 0 && "all".equals(args[0])) {
            GetPixelColor pixelColor = new GetPixelColor();
            pixelColor.start();
        }
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