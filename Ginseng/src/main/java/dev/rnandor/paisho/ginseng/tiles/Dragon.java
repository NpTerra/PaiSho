package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TileEntry(value = "drg")
public class Dragon extends GinsengTile {
    public Dragon(boolean host, Table table, int locX, int locY) {
        super("Dragon", true, host, table, locX, locY);
    }

    @Override
    public boolean canUseAbility() {
        return this.isInRedGarden() && !this.isTurtleBlocked();
    }

    @Override
    public List<Position> getAbilityAffectedTiles() {
        if(!this.canUseAbility())
            return Collections.emptyList();

        var affected = new ArrayList<Position>();
        var pos = getPosition();

        for(var tile : getTilesInSurroundings()) {
            if(canPush(tile.getPosition()))
                affected.add(tile.getPosition());
        }

        return affected;
    }

    /**
     * Checks whether the dragon can push away a tile at the target position.
     *
     * @param target The target position.
     * @return True if the dragon can push the tile, false otherwise.
     */
    private boolean canPush(Position target) {
        var pos = getPosition();
        var after = new Position(target.getX() - (pos.getX()-target.getX()), target.getY() - (pos.getY()-target.getY()));

        if(!table.isValidPosition(target) || !table.isValidPosition(after))
            return false;

        return table.getTile(target).isPresent() && table.getTile(after).isEmpty();
    }

    @Override
    public void useAbility(Position target) {
        if(!this.canUseAbility() || !canPush(target))
            return;

        var pos = getPosition();

        var after = new Position(target.getX() - (pos.getX()-target.getX()), target.getY() - (pos.getY()-target.getY()));

        table.move(table.getTile(target).get(), after);
    }
}
