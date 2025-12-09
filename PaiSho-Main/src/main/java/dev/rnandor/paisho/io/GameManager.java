package dev.rnandor.paisho.io;

import dev.rnandor.paisho.PaiShoGame;
import dev.rnandor.paisho.Tile;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;

@Slf4j
public final class GameManager<T extends Tile, G extends PaiShoGame<T>> {

    /**
     * Loads a game state from the specified file path.
     *
     * @param path The file path from which to load the game state.
     * @return The loaded game state.
     * @throws IOException If an I/O error occurs during loading.
     * @throws ClassNotFoundException If the class of a serialized object cannot be found.
     */
    public G loadGame(String path) throws IOException, ClassNotFoundException {
        var file = new FileInputStream(path);
        var in = new ObjectInputStream(file);

        G game = (G) in.readObject();
        in.close();

        return game;
    }

    /**
     * Saves the given game state to the specified file path.
     *
     * @param game The game state to be saved.
     * @param path The file path where the game state will be saved.
     * @throws IOException If an I/O error occurs during saving.
     */
    public void saveGame(G game, String path) throws IOException {
        var file = new FileOutputStream(path);
        var in = new ObjectOutputStream(file);
        in.writeObject(game);
        in.close();
    }
}
