package dev.rnandor.paisho.ui.panels;

import javax.swing.*;
import java.awt.*;

public class Spacer extends JPanel {
    /**
     * Creates a transparent spacer panel with no preferred size.
     */
    public Spacer() {
        this.setLayout(null);
        this.setOpaque(false);
    }

    /**
     * Creates a transparent spacer panel with the specified width and height.
     *
     * @param width  the preferred width of the spacer
     * @param height the preferred height of the spacer
     */
    public Spacer(int width, int height) {
        this();
        this.setPreferredSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
    }
}
