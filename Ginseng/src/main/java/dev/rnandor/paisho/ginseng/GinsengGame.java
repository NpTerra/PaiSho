package dev.rnandor.paisho.ginseng;

import dev.rnandor.paisho.PaiShoGame;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.Tile;
import dev.rnandor.paisho.TileRegistry;
import dev.rnandor.paisho.ginseng.tiles.GinsengTile;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

@Getter
@Slf4j
public class GinsengGame extends PaiShoGame<GinsengTile> {

    private Set<Tile> hostCaptured;
    private Set<Tile> guestCaptured;
    private static TileRegistry<GinsengTile> registry;

    static {
        try {
            registry = new TileRegistry<>(GinsengTile.class);
        } catch (TileRegistry.EntryClashException e) {
            log.error("TileRegistry.EntryClashException", e);
        }
    }

    public GinsengGame() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(registry);
    }
}
