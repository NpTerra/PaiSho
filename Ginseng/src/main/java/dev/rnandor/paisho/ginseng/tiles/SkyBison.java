package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;

import java.util.Collections;
import java.util.List;

@TileEntry(value = "sb")
public class SkyBison extends GinsengTile {
    public SkyBison(boolean host, Table table, int locX, int locY) {
        super("Sky Bison", false, host, table, locX, locY);
    }

    @Override
    public List<Position> getValidMoves() {
        if(this.isCaptured())
            return Collections.emptyList();

        //return this.getBasicMoves(true, getGame().isBisonFlightMode());
        return this.getBasicMoves(true, false);
    }
}
