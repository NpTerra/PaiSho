package dev.rnandor.paisho.ui.panels;

import javax.swing.*;
import java.awt.*;

public class Spacer extends JPanel {
    public Spacer() {
        this.setLayout(null);
        this.setOpaque(false);
    }

    public Spacer(int width, int height) {
        this();
        this.setPreferredSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
    }
}
