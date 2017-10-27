package absolut.img;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;

import absolut.can.CanReader;
import com.hopding.jrpicam.RPiCamera;
import com.hopding.jrpicam.exceptions.FailedToRunRaspistillException;

/**
 * Reads an image and calculates how much the car should steer depending on the amount of red pixels
 */
public class GetPixelColor extends Thread {

    public static final int ZERO_STEERING = -75;

    private CanReader can;
    private RPiCamera piCamera;
    private byte steering = 0;

    /**
     * Setups the camera and the CanReader
     */
    public GetPixelColor(){
        try {
            piCamera = new RPiCamera("/home/pi/Pictures");
            piCamera.setTimeout(10);
            piCamera.setHeight(250);
            piCamera.setWidth(400);
        } catch (FailedToRunRaspistillException e) {
            e.printStackTrace();
        }
        can = CanReader.getInstance();
    }

    /**
     * If run separately from the ACC this starts and setups everything
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String args[]) throws IOException, InterruptedException {
        GetPixelColor pixel = new GetPixelColor();
        pixel.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                CanReader.getInstance().sendEmergencyShutdown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void run() {
        while(true){
            scanPicture();
        }
    }

    /**
     * Gets an image and analyzes it and sends the steering values on the CAN bus
     */
    public void scanPicture() {
        try {

            long time = System.currentTimeMillis();
            BufferedImage image = ImageIO.read(piCamera.takeStill("pi.jpg"));
            System.out.println("Picture taken: " + (System.currentTimeMillis() - time));
            final byte[] pixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
            final int width = image.getWidth();

            float kvot = 0;
            int redCounterLeft = 0;
            int redCounterRight = 0;

            // Checks for red pixels
            boolean alpha = image.getAlphaRaster() != null;
            int pixelLength = alpha ? 4: 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int red = ((int)pixels[pixel + (alpha ? 3: 2)] & 0xff);
                int blue = ((int)pixels[pixel + (alpha ? 1: 0)] & 0xff);
                int green = ((int)pixels[pixel + (alpha ? 2: 1)] & 0xff);
                if (/*red > blue && blue > green*/red >= 90 && blue <= 50 && green <= 50) {
                    if ( col < (width / 2))
                        redCounterLeft++;
                    else
                        redCounterRight++;
                }
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }

            // Detergents how much to steer
            if(redCounterLeft == 0 && redCounterRight == 0) {
                System.out.println("inget rött hittat nånstans");
                steering = (byte) ZERO_STEERING;
            }else {
                // Alternative steering

                /*double r = redCounterRight / 25000D;
                double l = redCounterLeft / 25000D;

                if (r < 0 && l != 0)
                    l = 1;
                if (l == 0 && r != 0)
                    r = 1;
                double d = r - l;
                double mD = remap(d, -1, 1, -100, 100);
                mD = clamp(mD, -70, 70);
                if ((r < 0.001 && l < 0.001) || mD > -1 && mD < 1)
                    mD = ZERO_STEERING;

                steering = (byte) Math.floor(mD);
                */kvot = ((float) redCounterLeft / (float) redCounterRight);

                if (kvot > 0.9 && kvot < 1.1) {
                    steering = (byte) 0;
                } else if (redCounterLeft * 0.1 > redCounterRight) {
                    steering = (byte) -70;
                } else if (redCounterLeft * 0.4 > redCounterRight) {
                    steering = (byte) -60;
                } else if (redCounterLeft * 0.7 > redCounterRight) {
                    steering = (byte) -30;
                } else if (redCounterLeft * 0.9 > redCounterRight) {
                    steering = (byte) -10;
                } else if (redCounterRight * 0.1 > redCounterLeft) {
                    steering = (byte) 70;
                } else if (redCounterRight * 0.4 > redCounterLeft) {
                    steering = (byte) 60;
                } else if (redCounterRight * 0.7 > redCounterLeft) {
                    steering = (byte) 30;
                } else if (redCounterRight * 0.9 > redCounterLeft) {
                    steering = (byte) 10;
                } else
                    steering = (byte) 0;
            }
            //Flips the steering values because of inverted steering on this specific MOPED
            steering = (byte) remap(steering, -100, 100, 100, -100);
            // Clamps the steering with an offset that this MOPED has in it's hardware
            steering = (byte) clamp((steering - 75), -100, 100);

            System.out.println("Turn " + (steering > 0 ? "left " : steering == 0 ? "straight " : "right ") + steering + " " +
                    redCounterRight + (steering > 0 ? " > " : steering == 0 ? " = " : " < ") + redCounterLeft +
                    " kvot: " + kvot);
            can.sendSteering(steering);
            System.out.println("All done: " + (System.currentTimeMillis() - time));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clamps a value between a minimum and maximum value
     * @param value The value to clamp
     * @param min The minimum value to return
     * @param max The maximum value to return
     * @return The value clamped if needed
     */
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Maps the input value to the same location as between the out high and low as it is between the in high and low
     * For example:
     * remap(30, 0, 100, 100, 0) -> 70
     * remap(30, 0, 100, 0, 1) -> 0.3
     * remap(40, 30, 70, 0, 40) -> 10
     * @param value The value to remap
     * @param low1 The lowest value the inout can have
     * @param high1 The highest value the input can have
     * @param low2 The lowest value the output will have
     * @param high2 The highest value the output will have
     * @return
     */
    public static double remap(double value, double low1, double high1, double low2, double high2) {
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
    }
}