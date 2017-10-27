package absolut.acc;

import absolut.can.CanReader;
import java.io.IOException;

import absolut.img.GetPixelColor;
import org.omg.SendingContext.RunTime;

public class Main {

    /**
     * Starts the ACC and can start the ALC
     * @param args The "all" argument needs to be present if the ALC should be started as well
     */
    public static void main(String args[]) throws IOException {
        ACC acc = new ACC();
        Thread accThread = new Thread(acc);
        accThread.start();
        if (args.length > 0 && "all".equals(args[0])) {
            GetPixelColor pixelColor = new GetPixelColor();
            pixelColor.start();
        }
        // Adds a hook for when the program is shutdown to stop the MOPED
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                CanReader.getInstance().sendEmergencyShutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }
}