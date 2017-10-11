import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;

public class GetPixelColor {

    //int y, x, tofind, col;
    /**
     * @param args the command line arguments
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        findURL();

    }

    public static String findURL() {
        URL url = null;
        File folder = null;
        String urlAdress = "ftp://gustaf:absolut@chassit.xyz/home/gustaf/moped/position/Optipos/Connected/";

        try {
            url = new URL("ftp://gustaf:absolut@chassit.xyz/home/gustaf/moped/position/Optipos/Connected/");
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


            int redCount = 0;
            int blueCount = 0;
            int greenCount = 0;

            //find cyan pixels
            for (int y = 0; y < image1.getHeight(); y++) {
                for (int x = 0; x < image1.getWidth(); x++) {

                    int c = image1.getRGB(x,y);
                    Color color = new Color(c);

                    if (color.getRed() > 100 && color.getGreen() < 40 && color.getBlue() < 30) {
                        redCount ++;
                        //out.write("Red pixel found at=" + x + "," + y);
                        //out.newLine();
                    }
                    if (color.getRed() < 30 && color.getGreen() > 55 && color.getBlue() < 10) {
                        greenCount ++;
                        //out.write("Red pixel found at=" + x + "," + y);
                        // out.newLine();
                    }
                    if (color.getRed() < 110 && color.getGreen() < 80 && color.getBlue() > 65) {
                        blueCount ++;
                        //out.write("Red pixel found at=" + x + "," + y);
                        //out.newLine();
                    }

                }
            }
            System.out.println("Red pixels amount: " + redCount);
            System.out.println("Green pixels amount: " + greenCount);
            System.out.println("Blue pixels amount: " + blueCount);

            if((redCount - greenCount) < 0) {
                System.out.println("Turn right");
            } else if (((greenCount+500) - redCount) < 0) {
                System.out.println("Turn left");
            } else if (blueCount > 1300){
                System.out.println("Drive straight");
            } else {
                System.out.println("Don't know where to go :(");
            }
            System.out.println();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}