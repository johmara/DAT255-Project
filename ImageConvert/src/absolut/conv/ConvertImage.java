package absolut.conv;

import com.jcraft.jsch.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class ConvertImage extends JFrame {

    private ImagePanel ip;
    //private ImagePanel ip2;

    public ConvertImage() {
        super("Pi Image Preview");

        ip = new ImagePanel();
        //ip2 = new ImagePanel();
        getContentPane().add(ip);
        //getContentPane().add(ip2);

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setLayout(new GridLayout());

        setVisible(true);
        connect();
    }

    private void connect() {
        new Thread(() -> {
            Session session = null;
            ChannelSftp sftpChannel = null;
            try {
                JSch jsch = new JSch();
                String knownHostsFilename = System.getProperty("user.home") + File.separator + ".ssh" + File.separator +"known_hosts";
                File f = new File(knownHostsFilename);
                if (!f.exists()) {
                    f.mkdirs();
                    f.createNewFile();
                }
                jsch.setKnownHosts( knownHostsFilename );
                String in = JOptionPane.showInputDialog("Ip of PI", "192.168.");
                session = jsch.getSession( "pi", in);
                {
                    UserInfo ui = new Sftp.MyUserInfo();
                    session.setUserInfo(ui);
                    //session.setPassword("pi");
                }
                session.connect();

                Channel channel = session.openChannel( "sftp" );
                channel.connect();
                sftpChannel = (ChannelSftp) channel;
                while (true) {
                    BufferedImage image = ImageIO.read(sftpChannel.get("Pictures/pi.jpg"));
                    ip.setImages(image, getConverted(image));
                    //ip2.setImage(image);
                }
            } catch (IOException | JSchException | SftpException e) {
                e.printStackTrace();
            } finally {
                if (sftpChannel != null)
                    sftpChannel.exit();
                if (session != null)
                    session.disconnect();
            }
        }).start();
    }

    public static void main(String[] args) {
        new ConvertImage();

        /*BufferedImage img = null;

        try {
            img = ImageIO.read(new File(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (img != null) {
            byte[] data = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
            int width = img.getWidth();
            int height = img.getHeight();
            boolean hasAlphaChannel = img.getAlphaRaster() != null;
            int pixelLength = hasAlphaChannel ? 4: 3;
            int[][] result = new int[height][width];
            for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
                //int argb = 0;
                //if (hasAlphaChannel)
                //    argb += (((int) data[pixel] & 0xff) << 24); // alpha
                //argb += ((int) data[pixel + (hasAlphaChannel ? 1: 0)] & 0xff); // blue
                //argb += (((int) data[pixel + (hasAlphaChannel ? 2: 1)] & 0xff) << 8); // green
                //argb += (((int) data[pixel + (hasAlphaChannel ? 3: 2)] & 0xff) << 16); // red
                int red = (((int) data[pixel + (hasAlphaChannel ? 3: 2)] & 0xff)); // red
                if (red < 200)
                    red = 0x00;
                else
                    red = 0xFF0000;
                result[row][col] = red;
                //result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }*/
            /*int[][] result = new int[height][width];

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int c = img.getRGB(col, row);
                    int r = (c >> 16) & 0xFF;
                    if (r > 200) {
                        c &= ~(0xFF);
                        c &= ~(0xFF << 8);
                        c |= (0xFF << 16);
                    } else {
                        c &= ~(0xFF);
                        c &= ~(0xFF << 8);
                        c &= ~(0xFF << 16);
                    }

                    result[row][col] = c;
                }
            }*/

            /*BufferedImage image = new BufferedImage(result[0].length, result.length, img.getType());

            for (int i = 0; i < result.length; i++) {
                for (int j = 0; j < result[i].length; j++) {
                    //int[] pixel = new int[3];
                    //pixel[0] = result[i][j];
                    //pixel[1] = result[i][j];
                    //pixel[2] = result[i][j];
                    //raster.setPixel(i, j, pixel);
                    image.setRGB(j, i, result[i][j]);
                }
            }
            File out = new File(args[0].substring(0, args[0].lastIndexOf('.')) + "-conv.jpg");
            try {
                ImageIO.write(image, "JPG", out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/



    }

    private BufferedImage getConverted(BufferedImage in) {
        byte[] data = ((DataBufferByte) in.getRaster().getDataBuffer()).getData();
        int width = in.getWidth(), height = in.getHeight();
        boolean hasAlphaChannel = in.getAlphaRaster() != null;
        int pixelLength = hasAlphaChannel ? 4: 3;
        int[][] result = new int[height][width];
        BufferedImage out = new BufferedImage(result[0].length, result.length, BufferedImage.TYPE_INT_ARGB);
        for (int pixel = 0, row = 0, col = 0; pixel < data.length; pixel += pixelLength) {
            int c = 0;
            int blue = (((int) data[pixel + (hasAlphaChannel ? 1: 0)] & 0xff));
            int green = (((int) data[pixel + (hasAlphaChannel ? 2: 1)] & 0xff));
            int red = (((int) data[pixel + (hasAlphaChannel ? 3: 2)] & 0xff));

            //Change these values to pick what to see
            //START CHANGE THESE
            int alpha = 0xFF;
            if (red >= 90 && blue <= 50 && green <= 50)
                c = 0xFF0000 | (alpha << 24);//blue | ((green << 8) | (red << 16));
            else
                c = 0x00000000;
            //This get the original colors of the pixel
            //c = blue | ((green << 8) | (red << 16));
            //STOP CHANGE THESE

            out.setRGB(col, row, c);
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }

        /*for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                //int[] pixel = new int[3];
                //pixel[0] = result[i][j];
                //pixel[1] = result[i][j];
                //pixel[2] = result[i][j];
                //raster.setPixel(i, j, pixel);
                out.setRGB(j, i, result[i][j]);
            }
        }*/
        return out;
    }

}
