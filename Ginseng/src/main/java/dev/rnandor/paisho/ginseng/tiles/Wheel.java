package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TileEntry(value = "wh")
public class Wheel extends GinsengTile {
    public Wheel(boolean host, Table table, int locX, int locY) {
        super("Wheel", false, host, table, locX, locY);
    }

    @Override
    public List<Position> getValidMoves() {
        if(this.isCaptured())
            return Collections.emptyList();

        var moves = new ArrayList<Position>();
        var center = getPosition();

        // right
        for(int x = center.getX()+1; x < 17; x++) {
            var curr = new Position(x, center.getY());

            // if we can't move there, break
            if(!this.canMoveThere(curr, true))
                break;

            // we CAN in fact move here, so we'll add it to the list
            moves.add(curr);

            // if the move is done by capturing a tile, then we can't continue
            if(table.getTile(curr).isPresent())
                break;
        }

        // left
        for(int x = center.getX()-1; x >= -8; x--) {
            var curr = new Position(x, center.getY());

            // if we can't move there, break
            if(!this.canMoveThere(curr, true))
                break;

            // we CAN in fact move here, so we'll add it to the list
            moves.add(curr);

            // if the move is done by capturing a tile, then we can't continue
            if(table.getTile(curr).isPresent())
                break;
        }

        // up
        for(int y = center.getY()+1; y < 17; y++) {
            var curr = new Position(center.getX(), y);

            // if we can't move there, break
            if(!this.canMoveThere(curr, true))
                break;

            // we CAN in fact move here, so we'll add it to the list
            moves.add(curr);

            // if the move is done by capturing a tile, then we can't continue
            if(table.getTile(curr).isPresent())
                break;
        }

        // dowm
        for(int y = center.getY()-1; y >= -8; y--) {
            var curr = new Position(center.getX(), y);

            // if we can't move there, break
            if(!this.canMoveThere(curr, true))
                break;

            // we CAN in fact move here, so we'll add it to the list
            moves.add(curr);

            // if the move is done by capturing a tile, then we can't continue
            if(table.getTile(curr).isPresent())
                break;
        }

        return moves;
    }
}
