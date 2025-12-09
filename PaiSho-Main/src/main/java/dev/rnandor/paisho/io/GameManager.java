package dev.rnandor.paisho.io;

import dev.rnandor.paisho.PaiShoGame;
import dev.rnandor.paisho.Tile;

import java.io.*;
import java.net.URL;

public final class GameManager<T extends Tile, G extends PaiShoGame<T>> {

    public G loadGame(String path) throws IOException, ClassNotFoundException {
        var file = new FileInputStream(path);
        var in = new ObjectInputStream(file);

        G game = (G) in.readObject();
        in.close();

        return game;
    }

    public void saveGame(G game, String path) throws IOException {
        var file = new FileOutputStream(path);
        var in = new ObjectOutputStream(file);
        in.writeObject(game);
        in.close();
    }
}
