package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;

import java.io.Serializable;
import java.util.List;

@TileEntry(value = "oc")
public class Orchid extends GinsengTile {
    public Orchid(boolean host, Table table, int locX, int locY) {
        super("Orchid", false, host, table,  locX, locY);
    }

    @Override
    public void move(int x, int y) {
        var cap = this.isHost() ? getGame().getGuestCaptured() : getGame().getHostCaptured();
        var prev = cap.size();
        super.move(x, y);

        if(cap.size() != prev && !this.isTurtleBlocked())
            this.capture();
    }
}
