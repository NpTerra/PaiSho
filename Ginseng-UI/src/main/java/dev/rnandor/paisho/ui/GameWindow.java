package dev.rnandor.paisho.ui;

import javax.swing.*;

public class GameWindow extends JFrame {

    public GameWindow(String title, int width, int height) {
        super(title);

        this.setSize(width, height);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

}
