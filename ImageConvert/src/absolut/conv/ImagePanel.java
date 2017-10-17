package absolut.conv;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {

    private BufferedImage image = null;
    private AffineTransform at;

    public ImagePanel() {
        at = new AffineTransform();
        at.translate(250, 250);
        at.rotate(Math.PI);
        at.translate(-250, -250);
    }

    public synchronized void setImage(BufferedImage image) {
        this.image = image;
        repaint();
    }

    public synchronized BufferedImage getImage() {
        return image;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (getImage() != null) {
            ((Graphics2D)g).drawImage(getImage(), at, this); // see javadoc for more info on the parameters
        }
    }


}
