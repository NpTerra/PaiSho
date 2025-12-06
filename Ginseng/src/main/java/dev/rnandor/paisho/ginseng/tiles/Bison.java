package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;

import java.util.List;

@TileEntry(value = "wl", priority = 1)
public class Bison extends GinsengTile {
    public Bison(boolean host, int priority, Table table) {
        super("White Lotus", host, priority, table);
    }

    @Override
    public List<Position> getValidMoves() {
        return List.of();
    }

    @Override
    public boolean isValidMove(int x, int y) {
        return true;
    }
}
