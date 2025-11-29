package dev.rnandor.paisho.ui;

import javax.swing.*;
import java.awt.*;

public abstract class Drawable extends JPanel {

    @Override
    public final void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }

    protected abstract void draw(Graphics2D g);
}
