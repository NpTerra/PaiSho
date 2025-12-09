package dev.rnandor.paisho.ui.panels;

import dev.rnandor.paisho.PaiShoGame;
import dev.rnandor.paisho.ginseng.GinsengGame;
import dev.rnandor.paisho.ginseng.tiles.GinsengTile;
import dev.rnandor.paisho.io.GameManager;
import dev.rnandor.paisho.ui.GameWindow;
import dev.rnandor.paisho.ui.ResourceHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

@Slf4j
public class InGamePanel extends ImagePanel {

    private GameWindow gameWindow;

    private GameBoardPanel gameBoard;

    private JLabel titleLabel;

    @Getter
    private GinsengGame game;

    @Getter
    private String gameName;

    public InGamePanel(GameWindow gameWindow) throws IOException {
        super(ResourceHelper.getResource("/background/panorama.jpg"));
        this.gameWindow = gameWindow;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        var sidePanel = new ColorPanel(new Color(255, 255, 255, 120));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setOpaque(false);

        var welcome = new JLabel("Welcome to PaiSho!");
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel = new JLabel();
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton forfeitButton = new JButton("Forfeit");
        forfeitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        forfeitButton.setBounds(100, 100, 100, 30);
        forfeitButton.addActionListener(_ -> {
            if(game.isGameOver())
                return;

            game.setStatus(game.isGuestTurn() ? PaiShoGame.GameStatus.HOST_WIN : PaiShoGame.GameStatus.GUEST_WIN);
            repaint();
        });

        JButton saveButton = new JButton("Save Game");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.setBounds(100, 100, 100, 30);
        saveButton.addActionListener(_ -> {
            try {
                saveGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        JButton exitButton = new JButton("Leave Game");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setBounds(100, 100, 100, 30);
        exitButton.addActionListener(_ -> gameWindow.showMainMenu());

        sidePanel.add(new Spacer(0, 10));
        sidePanel.add(welcome);
        sidePanel.add(titleLabel);
        sidePanel.add(new Spacer());
        sidePanel.add(forfeitButton);
        sidePanel.add(new Spacer());
        sidePanel.add(saveButton);
        sidePanel.add(new Spacer(0, 10));
        sidePanel.add(exitButton);
        sidePanel.add(new Spacer());

        var sideBox = new Spacer();
        sideBox.setLayout(new BoxLayout(sideBox, BoxLayout.Y_AXIS));

        sideBox.add(new Spacer(0, 44));
        sideBox.add(sidePanel);
        sideBox.add(new Spacer(0, 44));

        add(new Spacer(44, 0));
        add(gameBoard = new GameBoardPanel());
        add(new Spacer(44, 0));
        add(sideBox);
        add(new Spacer(44, 0));
    }

    private void saveGame() throws IOException {
        log.info("Saving game...");

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Specify a .ginseng file to save into.");
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setCurrentDirectory(new File("."));

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            var br = new StringBuilder(this.gameName);

            var selected = chooser.getSelectedFile().toPath().resolve(br + ".ginseng");
            while(selected.toFile().exists()) {
                br.append("+");
                selected = chooser.getSelectedFile().toPath().resolve(br+".ginseng");
            }
            log.info("Selected file: {}", selected.toAbsolutePath());

            if(gameBoard.isSwapFreeze()) {
                gameBoard.closeTileReplacementDialog();
                repaint();
            }

            new GameManager<GinsengTile, GinsengGame>().saveGame(game, selected.toAbsolutePath().toString());
            log.info("Saved game.");
        }
    }

    @Override
    public void show() {
        super.show();

        log.debug("Showing InGamePanel");
    }

    public void setGame(String name, GinsengGame game) throws IOException {
        if(name.length() >= 20) {
            name = name.substring(0, 16)+"...";
        }
        this.titleLabel.setText("Game: '" + name +"'");
        this.game = game;
        this.gameName = name;
        gameBoard.setGame(game);
    }
}
