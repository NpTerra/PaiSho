package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.Tile;

import java.util.List;

public abstract class GinsengTile extends Tile {
    protected GinsengTile(String name, boolean host, int priority, Table table) {
        super(name, host, priority, table);
    }

    @Override
    public List<Position> getValidMoves() {
        return List.of();
    }

    @Override
    public boolean isValidMove(int x, int y) {
        return getValidMoves().stream().anyMatch(pos -> pos.getX() == x && pos.getY() == y);
    }

    private static void accessibleTiles() {

    }
}
