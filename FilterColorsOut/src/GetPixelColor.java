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
            File file1 = new File("landscape.png");
            BufferedImage image1 = ImageIO.read(file1);

            //write file
            FileWriter fstream = new FileWriter("pixellog1.txt");
            BufferedWriter out = new BufferedWriter(fstream);

            //color object
            //Color cyan = new Color(0, 255, 255);

            //find cyan pixels
            for (int y = 0; y < image1.getHeight(); y++) {
                for (int x = 0; x < image1.getWidth(); x++) {

                    int c = image1.getRGB(x,y);
                    Color color = new Color(c);

                    if (color.getRed() > 50 && color.getGreen() < 10 && color.getBlue() < 20) {
                        out.write("Red pixel found at=" + x + "," + y);
                        out.newLine();
                    }

                    //int  red = (c & 0x0000FFFF) >> 16;
                    //int  green = (c & 0x0000FFFF) >> 8;
                    //int  blue = c & 0x0000FFFF;

                    //if (cyan.equals(image1.getRGB(x, y)){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}