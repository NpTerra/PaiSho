package dev.rnandor.paisho;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Getter
public abstract class PaiShoGame<T extends Tile> implements Serializable {

    protected final Table table;

    protected final Set<T> tiles;

    private final TileRegistry<T> registry;

    private int turn = 1;

    @Setter
    private GameStatus status;

    /**
     * Constructs a PaiShoGame with the specified TileRegistry.
     *
     * @param registry The TileRegistry for the game.
     * @throws NoSuchMethodException If a required method is not found.
     * @throws InvocationTargetException If there is an error during tile instantiation.
     * @throws InstantiationException If there is an error during tile instantiation.
     * @throws IllegalAccessException If there is illegal access during tile instantiation.
     */
    protected PaiShoGame(TileRegistry<T> registry) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        table = new Table();
        tiles = new HashSet<>();
        this.registry = registry;
        this.status = GameStatus.RUNNING;
    }

    /**
     * Constructs a PaiShoGame with a TileRegistry for the specified tile class.
     *
     * @param tileClass The class of the tiles used in the game.
     * @throws TileRegistry.EntryClashException If there is a clash in tile entry IDs.
     * @throws InvocationTargetException If there is an error during tile instantiation.
     * @throws NoSuchMethodException If a required method is not found.
     * @throws InstantiationException If there is an error during tile instantiation.
     * @throws IllegalAccessException If there is illegal access during tile instantiation.
     */
    protected PaiShoGame(Class<T> tileClass) throws TileRegistry.EntryClashException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this(new TileRegistry<>(tileClass));
    }

    /**
     * Checks if it's the host player's turn.
     *
     * @return true if it's the host player's turn, false otherwise.
     */
    public final boolean isHostTurn() {
        return isGameRunning() && turn%2 == 0;
    }

    /**
     * Checks if it's the guest player's turn.
     *
     * @return true if it's the guest player's turn, false otherwise.
     */
    public final boolean isGuestTurn() {
        return isGameRunning() && turn%2 == 1;
    }

    /**
     * Advances the game to the next turn if the game is running.
     */
    public final void nextTurn() {
        if(isGameRunning())
            turn++;
    }

    /**
     * Checks if the game is currently running.
     *
     * @return true if the game is running, false otherwise.
     */
    public boolean isGameRunning() {
        return status.equals(GameStatus.RUNNING);
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return !isGameRunning();
    }

    /**
     * Checks for a draw condition in the game.
     * Should be implemented by subclasses.
     * Should set the game status to DRAW if a draw is detected.
     *
     * @return true if the game is a draw, false otherwise.
     */
    public abstract boolean checkForDraw();

    // Enum representing the status of the game.
    public enum GameStatus {
        RUNNING,
        HOST_WIN,
        GUEST_WIN,
        DRAW
    }

}
