package dev.rnandor.paisho.ui.panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImagePanel extends JPanel {
    private BufferedImage background;

    /**
     * Constructs an ImagePanel with the specified background image.
     *
     * @param image the URL of the background image
     * @throws IOException if an I/O error occurs while reading the image
     */
    public ImagePanel(URL image) throws IOException {
        this.background = ImageIO.read(image);

        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image
        if(background != null)
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
