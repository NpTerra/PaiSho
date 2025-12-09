package dev.rnandor.paisho.ui.panels;

import dev.rnandor.paisho.ginseng.GinsengGame;
import dev.rnandor.paisho.ginseng.tiles.GinsengTile;
import dev.rnandor.paisho.io.GameManager;
import dev.rnandor.paisho.ui.GameWindow;
import dev.rnandor.paisho.ui.ResourceHelper;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

@Slf4j
public class MainMenuPanel extends ImagePanel {

    private GameWindow gameWindow;

    JTextField text;
    JCheckBox bisonChange;
    JCheckBox ginsengChange;

    public MainMenuPanel(GameWindow gameWindow) throws IOException {
        super(ResourceHelper.getResource("/background/yes.jpg"));
        this.gameWindow = gameWindow;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var settings = new JPanel();
        settings.setOpaque(false);
        var changes = new JPanel();
        changes.setOpaque(false);
        var creation = new JPanel();
        creation.setOpaque(false);
        var loading = new JPanel();
        loading.setOpaque(false);

        text = new JTextField("", 20);
        bisonChange = new JCheckBox("Bison Change");
        ginsengChange = new JCheckBox("Ginseng Change");

        bisonChange.setOpaque(false);
        bisonChange.setForeground(Color.WHITE);
        ginsengChange.setOpaque(false);
        ginsengChange.setForeground(Color.WHITE);

        JButton startButton = new JButton("New Game");
        startButton.setBounds(100, 100, 100, 30);
        startButton.addActionListener(_ -> startNewGame());

        JButton loadButton = new JButton("Load Game");
        loadButton.setBounds(100, 100, 100, 30);
        loadButton.addActionListener(_ -> {
            try {
                loadGame();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));

        changes.setLayout(new BoxLayout(changes, BoxLayout.X_AXIS));
        changes.add(bisonChange);
        changes.add(ginsengChange);

        settings.add(text);
        settings.add(changes);

        creation.add(settings);
        creation.add(startButton);

        loading.add(loadButton);

        add(new Spacer());
        //add(settings);
        add(creation);
        add(loading);

        log.info("Main menu created.");
    }

    private void startNewGame() {
        log.debug("Starting New Game: '{}'", text.getText());
        if(text.getText().isEmpty()) {
            log.error("Game name cannot be empty.");
            return;
        }

        try {
            var game = new GinsengGame(bisonChange.isSelected(), ginsengChange.isSelected());
            gameWindow.showInGamePanel(text.getText(), game);
        } catch (Exception e) {
            log.error("Could not start New Game:", e);
        }
    }

    private void loadGame() throws IOException, ClassNotFoundException {
        log.info("Game selection opened.");

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a .ginseng save file to load.");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setCurrentDirectory(new File("."));

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Game Saves", "ginseng");
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            var selected = chooser.getSelectedFile();
            log.info("Selected file: {}", selected.getAbsolutePath());
            var game = new GameManager<GinsengTile, GinsengGame>().loadGame(selected.getAbsolutePath());
            var name = selected.getName().substring(0, selected.getName().lastIndexOf('.'));

            gameWindow.showInGamePanel(name, game);
        }
    }

    @Override
    public void show() {
        super.show();
        text.setText("");
    }
}
