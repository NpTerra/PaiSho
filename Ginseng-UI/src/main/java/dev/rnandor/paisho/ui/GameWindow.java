package dev.rnandor.paisho.ui;

import dev.rnandor.paisho.ginseng.GinsengGame;
import dev.rnandor.paisho.ui.panels.InGamePanel;
import dev.rnandor.paisho.ui.panels.MainMenuPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GameWindow extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPane;

    private InGamePanel inGamePanel;

    public GameWindow(String title, int width, int height) throws IOException {
        super(title);

        this.setSize(width, height);
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);
        inGamePanel = new InGamePanel(this);

        contentPane.add(new MainMenuPanel(this), "menu");
        contentPane.add(inGamePanel, "game");

        add(contentPane);

        this.setVisible(true);

        showMainMenu();
    }

    public void showMainMenu() {
        cardLayout.show(contentPane, "menu");
        contentPane.revalidate();
        contentPane.repaint();
    }

    public void showInGamePanel(String name, GinsengGame game) throws IOException {
        inGamePanel.setGame(name, game);
        cardLayout.show(contentPane, "game");
        contentPane.revalidate();
        contentPane.repaint();
    }

}
