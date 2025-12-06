package dev.rnandor.paisho;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class Tile {

    @Getter
    private final String name;
    @Getter
    private final boolean host;

    @Getter
    private final int priority;

    private final Table table;

    private int locX;
    private int locY;

    public boolean isGuest() {
        return !host;
    }

    public abstract List<Position> getValidMoves();

    public final Position getPosition() {
        return new Position(locX, locY);
    }

    public abstract boolean isValidMove(int x, int y);

    public final boolean isValidMove(Position position) {
        return isValidMove(position.getX(), position.getY());
    }
}
