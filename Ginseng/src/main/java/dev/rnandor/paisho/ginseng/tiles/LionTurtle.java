package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;

@TileEntry(value = "lt")
public class LionTurtle extends GinsengTile {
    public LionTurtle(boolean host, Table table, int locX, int locY) {
        super("Lion Turtle", false, host, table, locX, locY);
    }

}
