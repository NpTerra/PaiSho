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

    /**
     * Constructs a new GameWindow with the specified title, width, and height.
     *
     * @param title  the title of the window
     * @param width  the width of the window
     * @param height the height of the window
     * @throws IOException if an I/O error occurs
     */
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

    /**
     * Switches the view to the main menu panel.
     */
    public void showMainMenu() {
        cardLayout.show(contentPane, "menu");
        contentPane.revalidate();
        contentPane.repaint();
    }

    /**
     * Switches the view to the in-game panel with the specified game.
     *
     * @param name the name of the game
     * @param game the GinsengGame instance to display
     * @throws IOException if an I/O error occurs
     */
    public void showInGamePanel(String name, GinsengGame game) throws IOException {
        inGamePanel.setGame(name, game);
        cardLayout.show(contentPane, "game");
        contentPane.revalidate();
        contentPane.repaint();
    }

}
