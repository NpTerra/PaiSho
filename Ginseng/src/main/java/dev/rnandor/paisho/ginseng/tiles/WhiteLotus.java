package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.Tile;
import dev.rnandor.paisho.TileEntry;

import java.util.List;

@TileEntry("wl")
public class WhiteLotus extends Tile {
    public WhiteLotus(boolean host) {
        super("White Lotus", host);
    }

    @Override
    public List<Position> getTiles(Table table) {
        return List.of();
    }
}
