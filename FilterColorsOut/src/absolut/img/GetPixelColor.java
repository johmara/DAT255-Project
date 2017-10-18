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

/* Läser av en bild och räknar ut hur mycket bilen ska svänga beroende på var det finns flest röda pixlar */

public class GetPixelColor extends Thread {
    //int y, x, tofind, col;
    /**
     * @param args the command line arguments
     * @throws IOException
     */

    public static final int ZERO_STEERING = 10;

    private CanReader can;
    private RPiCamera piCamera;
    private byte steering = 0;
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

    public static void main(String args[]) throws IOException, InterruptedException {
        GetPixelColor pixel = new GetPixelColor();
        pixel.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CanReader.getInstance().sendMotorSpeed((byte) 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    @Override
    public void run() {
        while(true){
            scanPicture();
        }
    }

    public  void scanPicture() {
        try {
            //read image file
            //File file1 = new File(picture);
            //BufferedImage image = ImageIO.read(file1);
            BufferedImage image = ImageIO.read(piCamera.takeStill("pi.jpg"));
            final byte[] pixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
            final int width = image.getWidth();

            float kvot = 0;
            int redCounterLeft = 0;
            int redCounterRight = 0;
            //int c;
            //System.out.println(image.getWidth() + ":" + image.getHeight());;

            boolean alpha = image.getAlphaRaster() != null;
            int pixelLength = alpha ? 4: 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int red = ((int)pixels[pixel + (alpha ? 3: 2)] & 0xff);
                int blue = ((int)pixels[pixel + (alpha ? 1: 0)] & 0xff);
                int green = ((int)pixels[pixel + (alpha ? 2: 1)] & 0xff);
                if (red >= 90 && blue <= 50 && green <= 50) {
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
            //System.out.println(" right: " + redCounterRight + " left: " + redCounterLeft);
            //if (redCounterRight > 0) {
            if(redCounterLeft == 0 && redCounterRight == 0) {
                System.out.println("inget rött hittat nånstans");
                steering = (byte) ZERO_STEERING;
            }else {
                // Alternative steering
                /*
                double r = redCounterRight / 25000D;
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

                steering = (byte) Math.floor(mD);*/
                kvot = ((float) redCounterLeft / (float) redCounterRight);

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

            System.out.println("Turn " + (steering > 0 ? "left " : steering == 0 ? "straight " : "right ") + steering + " " +
                    redCounterRight + (steering > 0 ? " > " : steering == 0 ? " = " : " < ") + redCounterLeft +
                    " kvot: " + kvot);
            can.sendSteering(steering);


            //else System.out.println("Turn right " + redCounterRight + " > " + redCounterLeft);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static double remap(double value, double low1, double high1, double low2, double high2) {
        return low2 + (value - low1) * (high2 - low2) / (high1 - low1);
    }
}