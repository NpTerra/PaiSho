package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.PaiShoGame;
import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.TileEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@TileEntry(value = "wl")
public class WhiteLotus extends GinsengTile {

    public WhiteLotus(boolean host, Table table, int locX, int locY) {
        super("White Lotus", false, host, table, locX, locY);
    }

    @Override
    public List<Position> getValidMoves() {
        List<Position> moves = new ArrayList<>();
        var center = this.getPosition();

        var visited = new boolean[17][17];
        var centerNormalised = table.fromGameCoords(center.getX(), center.getY());
        visited[centerNormalised[0]][centerNormalised[1]] = true;

        Queue<Position> queue = new LinkedList<>();
        queue.add(center);

        while (!queue.isEmpty()) {
            var curr = queue.poll();

            var upRightAnchor = new Position(curr.getX()+1, curr.getY()+1);
            var upRight = new Position(curr.getX()+2, curr.getY()+2);

            var downRightAnchor = new Position(curr.getX()+1, curr.getY()-1);
            var downRight = new Position(curr.getX()+2, curr.getY()-2);

            var upLeftAnchor = new Position(curr.getX()-1, curr.getY()+1);
            var upLeft = new Position(curr.getX()-2, curr.getY()+2);

            var downLeftAnchor = new Position(curr.getX()-1, curr.getY()-1);
            var downLeft = new Position(curr.getX()-2, curr.getY()-2);

            addIfValid(upRightAnchor, upRight, queue, visited, moves);
            addIfValid(downRightAnchor, downRight, queue, visited, moves);
            addIfValid(upLeftAnchor, upLeft, queue, visited, moves);
            addIfValid(downLeftAnchor, downLeft, queue, visited, moves);
        }

        return moves;
    }

    private void addIfValid(Position anchor, Position next, Queue<Position> queue, boolean[][] visited, List<Position> moves) {
        if(!table.isValidPosition(next))
            return;

        var norm = table.fromGameCoords(next.getX(), next.getY());
        if(visited[norm[0]][norm[1]])
            return;

        if(canMoveThere(next, false)) {
            visited[norm[0]][norm[1]] = true;

            if(table.getTile(anchor).isPresent()) {
                moves.add(next);
                if (table.getTile(next).isEmpty())
                    queue.add(next);
            }
        }
    }

    @Override
    public void capture() {
        table.move(this, 0, this.isGuest() ? 8 : -8);
    }

    @Override
    public void afterMoved() {
        if(this.isGuest() && this.getPosition().getY() < 0) {
            getGame().setStatus(PaiShoGame.GameStatus.GUEST_WIN);
        }

        if(this.isHost() && this.getPosition().getY() > 0) {
            getGame().setStatus(PaiShoGame.GameStatus.HOST_WIN);
        }
    }
}
