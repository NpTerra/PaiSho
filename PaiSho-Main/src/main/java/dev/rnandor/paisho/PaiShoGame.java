package dev.rnandor.paisho;

import lombok.Getter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class PaiShoGame<T extends Tile> {

    private final Table table;

    private final Set<T> tiles;

    private final TileRegistry<T> registry;

    private int turn = 1;

    protected PaiShoGame(TileRegistry<T> registry) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        table = new Table();
        tiles = new HashSet<>();
        this.registry = registry;

        for (Class<? extends T> tileClass : registry.getTiles()) {
            Constructor<? extends T> construct = tileClass.getDeclaredConstructor(boolean.class);
            tiles.add(construct.newInstance(true));
            tiles.add(construct.newInstance(false));
        }
    }

    protected PaiShoGame(Class<T> tileClass) throws TileRegistry.EntryClashException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this(new TileRegistry<>(tileClass));
    }

    public final boolean isHostTurn() {
        return turn%2 == 0;
    }

    public final boolean isGuestTurn() {
        return turn%2 == 1;
    }

    public final void nextTurn() {
        turn++;
    }

}
