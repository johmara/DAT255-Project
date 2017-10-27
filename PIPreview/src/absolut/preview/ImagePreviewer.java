package absolut.preview;

import com.jcraft.jsch.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

/**
 * Gets an image from the PI to preview what the PI sees when it's driving
 * It creates an overlay for the color that the MOPED should follow to easily see them
 */
public class ImagePreviewer extends JFrame {

    private ImagePanel ip;

    public ImagePreviewer() {
        super("Pi Image Preview");

        ip = new ImagePanel();
        getContentPane().add(ip);

        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout());

        setVisible(true);
        connect();
    }

    /**
     * Setups the connection to the PI with JSch that is a sftp library
     */
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
                String in = JOptionPane.showInputDialog("Ip of PI", "192.168.43.86");
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

    /**
     * Starts the program
     * @param args Not arguments are taken
     */
    public static void main(String[] args) {
        new ImagePreviewer();
    }

    /**
     * Converts an BufferedImage to an overlay
     * The pixels with the color that are wanted gets a green color
     * The rest of the pixels are transparent
     * @param in The buffered image to convert
     * @return A transparent image with green pixels where desired
     */
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
                c = 0x00FF00 | (alpha << 24);
            else
                c = 0x00000000;
            //This gets the original colors of the pixel
            //c = blue | ((green << 8) | (red << 16));
            //STOP CHANGE THESE

            out.setRGB(col, row, c);
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }

        return out;
    }

}
