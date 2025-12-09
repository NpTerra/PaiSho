package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;

import java.util.Collections;
import java.util.List;

@TileEntry(value = "gs")
public class Ginseng extends GinsengTile {
    public Ginseng(boolean host, Table table, int locX, int locY) {
        super("Ginseng", false, host, table, locX, locY);
    }

    @Override
    public List<Position> getValidMoves() {
        if(this.isCaptured())
            return Collections.emptyList();

        return this.getBasicMoves(false, false);
    }
}
