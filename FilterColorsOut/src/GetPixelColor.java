import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GetPixelColor {

    //int y, x, tofind, col;
    /**
     * @param args the command line arguments
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        try {
            //read image file
            File file1 = new File("Straight.jpg");
            BufferedImage image1 = ImageIO.read(file1);

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
            } else {
                System.out.println("Drive straight");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}