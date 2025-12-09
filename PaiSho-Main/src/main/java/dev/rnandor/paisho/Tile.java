package dev.rnandor.paisho;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public abstract class Tile implements Comparable<Tile>, Serializable {

    @Getter
    private final String name;
    @Getter
    private final boolean host;

    protected final Table table;

    private int locX;
    private int locY;

    public Tile(String name, boolean host, Table table) {
        this(name, host, table, 0, 0);
    }

    public Tile(String name, boolean host, Table table, int locX, int locY) {
        this.name = name;
        this.host = host;
        this.table = table;
        this.locX = locX;
        this.locY = locY;
    }

    public boolean isGuest() {
        return !host;
    }

    public abstract List<Position> getValidMoves();

    public final Position getPosition() {
        return new Position(locX, locY);
    }

    public final void setPosition(int x, int y) {
        this.locX = x;
        this.locY = y;
    }

    public final void setPosition(Position pos) {
        this.setPosition(pos.getX(), pos.getY());
    }

    public abstract boolean isValidMove(int x, int y);

    public final boolean isValidMove(Position position) {
        return isValidMove(position.getX(), position.getY());
    }

    public final boolean isInRedGarden() {
        return Table.isRedGarden(table.getType(locX, locY).orElse(0));
    }

    public final boolean isInWhiteGarden() {
        return Table.isWhiteGarden(table.getType(locX, locY).orElse(0));
    }

    public final boolean isInNeutralGarden() {
        return Table.isNeutralGarden(table.getType(locX, locY).orElse(0));
    }

    public final boolean isInTemple() {
        return Table.isTemple(table.getType(locX, locY).orElse(0));
    }

    public final boolean isFromArea(Table.Locale... locales) {
        return locales.length == 0 || Table.isFrom(table.getType(locX, locY).orElse(0), locales);
    }

    public void move(int x, int y) {
        if(!isValidMove(x, y))
            throw new IllegalArgumentException("The target position cannot be accessed with this tile.");

        table.move(locX, locY, x, y);
    }

    public final void move(Position position) {
        this.move(position.getX(), position.getY());
    }

    public void afterMoved() {}

    @Override
    public int compareTo(Tile o) {
        return Comparator.comparingInt(System::identityHashCode).compare(this, o);
    }
}
