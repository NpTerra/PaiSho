package dev.rnandor.paisho.ui.panels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class ColorPanel extends JPanel {

    @Getter @Setter
    private Color color;

    public ColorPanel(Color color) {
        this.color = color;
        this.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(color);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

        g2.dispose();
    }
}
