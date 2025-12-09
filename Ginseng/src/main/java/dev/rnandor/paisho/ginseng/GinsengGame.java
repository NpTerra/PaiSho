package dev.rnandor.paisho.ginseng;

import com.sun.source.tree.Tree;
import dev.rnandor.paisho.*;
import dev.rnandor.paisho.ginseng.tiles.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Getter
@Slf4j
public class GinsengGame extends PaiShoGame<GinsengTile> {

    private Set<Tile> hostCaptured;
    private Set<Tile> guestCaptured;
    private static TileRegistry<GinsengTile> registry;

    private WhiteLotus hostLotus;
    private WhiteLotus guestLotus;

    private final boolean bisonFlightMode;
    private final boolean alternativeGinsengMode;

    static {
        try {
            registry = new TileRegistry<>(GinsengTile.class);
        } catch (TileRegistry.EntryClashException e) {
            log.error("TileRegistry.EntryClashException", e);
        }
    }

    public GinsengGame(boolean bisonGrantsFlight, boolean alternativeGinsengProtection) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        super(registry);

        this.bisonFlightMode = bisonGrantsFlight;
        this.alternativeGinsengMode = alternativeGinsengProtection;

        hostCaptured = new TreeSet<>();
        guestCaptured = new TreeSet<>();

        guestLotus = new WhiteLotus(false, table, 0, 8);
        hostLotus = new WhiteLotus(true, table, 0, -8);

        tiles.add(new Dragon(false, table, -1, 7));

        tiles.add(new Badgermole(false, table, 1, 7));

        tiles.add(new SkyBison(false, table, -2, 6));

        tiles.add(new Koi(false, table, 2, 6));

        tiles.add(new Wheel(false, table, -3, 5));
        tiles.add(new Wheel(false, table, 3, 5));

        tiles.add(new Ginseng(false, table, -4, 4));
        tiles.add(new Ginseng(false, table, 4, 4));

        tiles.add(new Orchid(false, table, -5, 4));
        tiles.add(new Orchid(false, table, 5, 4));

        tiles.add(new LionTurtle(false, table, 0, 4));

        var mirrored = new ArrayList<GinsengTile>();
        for (var tile : this.tiles) {
            var pos = tile.getPosition();
            var construct = tile.getClass().getDeclaredConstructor(boolean.class, table.getClass(), int.class, int.class)
                    .newInstance(!tile.isHost(), table, -pos.getX(), -pos.getY());
            mirrored.add(construct);
        }
        tiles.addAll(mirrored);

        tiles.add(guestLotus);
        tiles.add(hostLotus);

        for(var tile : tiles) {
            tile.setGame(this);
            table.put(tile);
        }
    }

    public boolean isCapturingAllowed() {
        return !hostLotus.isInTemple() && !guestLotus.isInTemple();
    }

    @Override
    public boolean checkForDraw() {
        for(var tile : this.tiles) {
            if(tile.isGuest() != this.isGuestTurn())
                continue;

            if(tile.isCaptured())
                continue;

            if(!tile.getValidMoves().isEmpty())
                return false;
        }

        this.setStatus(GameStatus.DRAW);
        return true;
    }
}
