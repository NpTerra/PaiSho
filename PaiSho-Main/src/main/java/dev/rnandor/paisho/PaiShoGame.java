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

    protected PaiShoGame(TileRegistry<T> registry) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        table = new Table();
        tiles = new HashSet<>();
        this.registry = registry;
        this.status = GameStatus.RUNNING;
    }

    protected PaiShoGame(Class<T> tileClass) throws TileRegistry.EntryClashException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this(new TileRegistry<>(tileClass));
    }

    public final boolean isHostTurn() {
        return isGameRunning() && turn%2 == 0;
    }

    public final boolean isGuestTurn() {
        return isGameRunning() && turn%2 == 1;
    }

    public final void nextTurn() {
        if(isGameRunning())
            turn++;
    }

    public boolean isGameRunning() {
        return status.equals(GameStatus.RUNNING);
    }

    public boolean isGameOver() {
        return !isGameRunning();
    }

    public abstract boolean checkForDraw();

    public enum GameStatus {
        RUNNING,
        HOST_WIN,
        GUEST_WIN,
        DRAW
    }

}
