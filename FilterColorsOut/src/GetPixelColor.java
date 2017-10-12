import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import java.math.*;

public class GetPixelColor extends Thread{

    //int y, x, tofind, col;
    /**
     * @param args the command line arguments
     * @throws IOException
     */

    //CanReader Can;
    public static void main(String args[]) throws IOException, InterruptedException {
        //Can = new CanReader;
        float kvot;
        while(true){
            findURL();

            //Thread.sleep(1);
        }


    }

    public static String findURL() {
        URL url = null;
        String urlAdress = "ftp://gustaf:absolut@chassit.xyz/home/gustaf/moped/position/Optipos/Connected/";

        try {
            url = new URL(urlAdress);
            //folder = new File (url.toURI());
            URLConnection urlc = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

            String inputLine;
            String pictureName;
            while((inputLine = in.readLine()) != null) {

                String str = inputLine;
                String[] parts = str.split(" ");
                pictureName = parts[22];

                scanPicture(new URL(urlAdress+pictureName));

            }
            in.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

        return "left.jpg";

    }


    public static void scanPicture(URL picture) {
        try {
            //read image file
            //File file1 = new File(picture);
            //BufferedImage image1 = ImageIO.read(file1);
            BufferedImage image1 = ImageIO.read(picture);

            //write file
            FileWriter fstream = new FileWriter("pixellog1.txt");
            BufferedWriter out = new BufferedWriter(fstream);


            int redCounterLeft = 0;
            int redCounterRight = 0;

            int c;

            //find cyan pixels
            for (int y = 500; y < 600; y++) {
                for (int x = 0; x < image1.getWidth();x++){

                    c = image1.getRGB(x,y);
                    Color color = new Color(c);

                    if (color.getRed() > 140 && x < image1.getWidth()/2 )
                        redCounterLeft++;
                    else if(color.getRed() > 140 && x > image1.getWidth()/2)
                    redCounterRight++;
                }
            }


            //redCounterLeft/redCounterRight = kvot;
            //if (kvot > 0.9 || kvot < 1.1)
            //    System.out.println("Drive straight " + redCounterLeft + "     " + redCounterRight);
            if (Math.abs(redCounterLeft - redCounterRight) < 4000)
                System.out.println("Drive straight " + redCounterLeft + "     " + redCounterRight);
            else if(redCounterLeft > redCounterRight)
                System.out.println("Turn left " + redCounterLeft + " > " + redCounterRight );

            //Kod som jag tror kan funka, alla siffror e helt påhittade.
            //else if(redCounterLeft * 0.9 > redCounterRight)
                    //can.sendSteering(-10);
            //else if(redCounterLeft * 0.7 > redCounterRight)
                //can.sendSteering(-40);
            //else if(redCounterLeft * 0.4 > redCounterRight)
                //can.sendSteering(-70);
            //else if(redCounterLeft * 0.1 > redCounterRight)
                //can.sendSteering(-100);
            //else if(redCounterRight * 0.9 > redCounterLeft)
                //can.sendSteering(10);
            //else if(redCounterRight * 0.7 > redCounterLeft)
                //can.sendSteering(40);
            //else if(redCounterRight * 0.4 > redCounterLeft)
                //can.sendSteering(70);
            //else if(redCounterRight * 0.1 > redCounterLeft)
                //can.sendSteering(100);

            else System.out.println("Turn right " + redCounterRight + " > " + redCounterLeft);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}