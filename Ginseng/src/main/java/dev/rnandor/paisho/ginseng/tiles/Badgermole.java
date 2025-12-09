package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TileEntry(value = "bm")
public class Badgermole extends GinsengTile {
    public Badgermole(boolean host, Table table, int locX, int locY) {
        super("Badgermole", true, host, table, locX, locY);
    }

    @Override
    public boolean canUseAbility() {
        return this.isInWhiteGarden();
    }

    public List<Position> getAbilityAffectedTiles() {
        if(!this.canUseAbility())
            return Collections.emptyList();

        var affected = new ArrayList<Position>();
        var pos = getPosition();

        for(var tile : getTilesInSurroundings()) {
            if(canFlip(tile.getPosition()))
                affected.add(tile.getPosition());
        }

        return affected;
    }

    private boolean canFlip(Position target) {
        var pos = getPosition();
        var after = new Position(target.getX() + (pos.getX()-target.getX())*2, target.getY() + (pos.getY()-target.getY())*2);

        if(!table.isValidPosition(target) || !table.isValidPosition(after))
            return false;

        return table.getTile(target).isPresent() && table.getTile(after).isEmpty();
    }

    public void useAbility(Position target) {
        if(!this.canUseAbility() || !canFlip(target))
            return;

        var pos = getPosition();

        var after = new Position(target.getX() + (pos.getX()-target.getX())*2, target.getY() + (pos.getY()-target.getY())*2);

        table.move(table.getTile(target).get(), after);
    }
}
