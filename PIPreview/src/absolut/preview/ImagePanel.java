package absolut.preview;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * A panel that draws images from a BufferedImage object
 */
public class ImagePanel extends JPanel {

    private transient BufferedImage image = null;
    private transient BufferedImage image2 = null;
    private AffineTransform at;

    public ImagePanel() {
        at = new AffineTransform();
        at.translate(200, 125);
        at.rotate(Math.PI);
        at.translate(-200, -125);
    }

    /**
     * Sets both the images
     * @param image The base image to show
     * @param image2 The overlay image to show
     */
    public synchronized void setImages(BufferedImage image, BufferedImage image2) {
        this.image = image;
        this.image2 = image2;
        repaint();
    }

    /**
     * Gets the base image
     * @return The base image
     */
    public synchronized BufferedImage getImage() {
        return image;
    }

    /**
     * Gets the overlay image
     * @return The overlay image
     */
    public synchronized BufferedImage getImage2() {
        return image2;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (g instanceof Graphics2D) {
            if (getImage() != null) {
                ((Graphics2D) g).drawImage(getImage(), at, this); // see javadoc for more info on the parameters
            }
            if (getImage2() != null) {
                ((Graphics2D) g).drawImage(getImage2(), at, this); // see javadoc for more info on the parameters
            }
        }
    }


}
