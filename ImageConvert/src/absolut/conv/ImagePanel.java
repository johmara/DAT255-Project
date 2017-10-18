package absolut.conv;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

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

    public synchronized void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public synchronized void setImages(BufferedImage image, BufferedImage image2) {
        this.image = image;
        this.image2 = image2;
        repaint();
    }

    public synchronized BufferedImage getImage() {
        return image;
    }
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
