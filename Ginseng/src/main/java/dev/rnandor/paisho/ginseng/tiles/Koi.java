package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;

import java.util.List;

@TileEntry(value = "koi")
public class Koi extends GinsengTile {
    public Koi(boolean host, Table table, int locX, int locY) {
        super("Koi", false, host, table, locX, locY);
    }

}
