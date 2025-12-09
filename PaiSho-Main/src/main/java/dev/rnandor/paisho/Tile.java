package dev.rnandor.paisho;

import lombok.Getter;

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

    /**
     * Constructs a new Tile with default position (0,0).
     *
     * @param name The name of the tile.
     * @param host true if the tile belongs to the host player, false if it belongs to the guest player.
     * @param table The table on which the tile is placed.
     */
    protected Tile(String name, boolean host, Table table) {
        this(name, host, table, 0, 0);
    }

    /**
     * Constructs a new Tile.
     *
     * @param name The name of the tile.
     * @param host true if the tile belongs to the host player, false if it belongs to the guest player.
     * @param table The table on which the tile is placed.
     * @param locX The x-coordinate of the tile's position in game coordinates.
     * @param locY The y-coordinate of the tile's position in game coordinates.
     */
    protected Tile(String name, boolean host, Table table, int locX, int locY) {
        this.name = name;
        this.host = host;
        this.table = table;
        this.locX = locX;
        this.locY = locY;
    }

    /**
     * Checks if the tile belongs to the guest player.
     *
     * @return true if the tile belongs to the guest player, false otherwise.
     */
    public boolean isGuest() {
        return !host;
    }

    /**
     * Gets a list of valid moves for the tile.
     *
     * @return A list of positions representing valid moves for the tile.
     */
    public abstract List<Position> getValidMoves();

    /**
     * Gets the position of the tile.
     *
     * @return The current position of the tile.
     */
    public final Position getPosition() {
        return new Position(locX, locY);
    }

    /**
     * Sets the position of the tile.
     *
     * @param x The target x-coordinate to set in game coordinates.
     * @param y The target y-coordinate to set in game coordinates.
     */
    public final void setPosition(int x, int y) {
        this.locX = x;
        this.locY = y;
    }

    /**
     * Sets the position of the tile.
     *
     * @param pos The target position to set.
     */
    public final void setPosition(Position pos) {
        this.setPosition(pos.getX(), pos.getY());
    }

    /**
     * Checks if the tile can move to the specified position.
     *
     * @param x The target x-coordinate to check in game coordinates.
     * @param y The target y-coordinate to check in game coordinates.
     * @return true if the tile can move to the specified position, false otherwise.
     */
    public abstract boolean isValidMove(int x, int y);

    /**
     * Checks if the tile can move to the specified position.
     *
     * @param position The target position to check.
     * @return true if the tile can move to the specified position, false otherwise.
     */
    public final boolean isValidMove(Position position) {
        return isValidMove(position.getX(), position.getY());
    }

    /**
     * Checks if the tile is in a red garden.
     * @return true if the tile is in a red garden, false otherwise.
     */
    public final boolean isInRedGarden() {
        return Table.isRedGarden(table.getType(locX, locY).orElse(0));
    }

    /**
     * Checks if the tile is in a white garden.
     * @return true if the tile is in a white garden, false otherwise.
     */
    public final boolean isInWhiteGarden() {
        return Table.isWhiteGarden(table.getType(locX, locY).orElse(0));
    }

    /**
     * Checks if the tile is in a neutral garden.
     * @return true if the tile is in a neutral garden, false otherwise.
     */
    public final boolean isInNeutralGarden() {
        return Table.isNeutralGarden(table.getType(locX, locY).orElse(0));
    }

    /**
     * Checks if the tile is in a temple.
     * @return true if the tile is in a temple, false otherwise.
     */
    public final boolean isInTemple() {
        return Table.isTemple(table.getType(locX, locY).orElse(0));
    }

    /**
     * Checks if the tile is from any of the specified locales.
     *
     * @param locales
     * @return true if the tile is from any of the specified locales or the locales is empty, false otherwise.
     */
    public final boolean isFromArea(Table.Locale... locales) {
        return locales.length == 0 || Table.isFrom(table.getType(locX, locY).orElse(0), locales);
    }

    /**
     * Moves the tile to the specified position.
     *
     * @param x The target x-coordinate to move the tile to in game coordinates.
     * @param y The target y-coordinate to move the tile to in game coordinates.
     * @throws IllegalArgumentException if the target position is not a valid move for this tile.
     */
    public void move(int x, int y) {
        if(!isValidMove(x, y))
            throw new IllegalArgumentException("The target position cannot be accessed with this tile.");

        table.move(locX, locY, x, y);
    }

    /**
     * Moves the tile to the specified position.
     *
     * @param position The target position to move the tile to.
     * @throws IllegalArgumentException if the target position is not a valid move for this tile.
     */
    public final void move(Position position) {
        this.move(position.getX(), position.getY());
    }

    /**
     * Callback method, called after the tile has been moved.
     *
     * Can be overridden by subclasses to implement custom behavior after a move.
     */
    public void afterMoved() {}

    @Override
    public int compareTo(Tile o) {
        return Comparator.comparingInt(System::identityHashCode).compare(this, o);
    }
}
