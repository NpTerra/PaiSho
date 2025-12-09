package dev.rnandor.paisho;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;

import static dev.rnandor.paisho.Table.Locale.*;


@Slf4j
public class Table implements Serializable {
    private Tile[][] tiles = new Tile[17][17];
    private int[][] types = new int[17][17];

    /**
     * Constructs a new Pai Sho table with predefined locales.
     */
    public Table() {
        // Western Temple
        types[0][7] = WESTERN_TEMPLE.flag;
        types[0][8] = WESTERN_TEMPLE.flag;
        types[0][9] = WESTERN_TEMPLE.flag;
        types[1][8] = WESTERN_TEMPLE.flag;

        // Eastern Temple
        types[16][7] = EASTERN_TEMPLE.flag;
        types[16][8] = EASTERN_TEMPLE.flag;
        types[16][9] = EASTERN_TEMPLE.flag;
        types[15][8] = EASTERN_TEMPLE.flag;

        // Southern Temple
        types[7][0] = SOUTHERN_TEMPLE.flag;
        types[8][0] = SOUTHERN_TEMPLE.flag;
        types[9][0] = SOUTHERN_TEMPLE.flag;
        types[8][1] = SOUTHERN_TEMPLE.flag;

        // Northern Temple
        types[7][16] = NORTHERN_TEMPLE.flag;
        types[8][16] = NORTHERN_TEMPLE.flag;
        types[9][16] = NORTHERN_TEMPLE.flag;
        types[8][15] = NORTHERN_TEMPLE.flag;

        // Gardens
        for(int x = -7; x <= 7; x++) {
            for (int y = -7; y <= 7; y++) {
                int flag = x*y > 0 ? WHITE_GARDEN.flag : RED_GARDEN.flag; // if the sign matches, it's a white garden
                if(x*y == 0) flag |= WHITE_GARDEN.flag;              // if either axis is on the 0, it's both

                if(Math.abs(x) + Math.abs(y) > 7)
                    continue;

                types[x+8][y+8] |= flag;                         // setting the selected garden type(s)
            }
        }


        // debug print
        log.debug("Usable area");
        for(int i = 8; i >= -8; i--) {
            for (int j = -8; j <= 8; j++) {
                int dist = (int) Math.sqrt(i * i + j * j);
                if(dist <= 8)
                    System.out.print("o ");
                else
                    System.out.print("- ");
            }
            System.out.println();
        }
        System.out.println();

        log.debug("Table layout");
        var chars1 = "o ░_▒_#¤■";
        for(int x = 8; x >= -8; x--) {
            for (int y = -8; y <= 8; y++) {
                boolean isValid = isValidPosition(x, y);

                char c = isValid ? chars1.charAt(Math.min(types[y+8][x+8], (NORTHERN_TEMPLE.flag))) : ' ';

                System.out.print(c+" ");
            }
            System.out.println();
        }
    }


    /**
     * This method checks if the given coordinates are within the table bounds.
     * It assumes that the coordinates are in game-space coordinates.
     *
     * @param x The x coordinate to check.
     * @param y The y coordinate to check.
     * @return True if the coordinates are within the table bounds, false otherwise.
     */
    public boolean isValidPosition(int x, int y) {
        var a = Math.abs(x);
        var b = Math.abs(y);

        //return x*x + y*y <= 81; // circle check
        return (a <= 8 && b <= 8) && (a + b <= 12); // diamond check
    }

    /**
     * This method checks if the given position is within the table bounds.
     * It assumes that the coordinates are in game-space coordinates.
     *
     * @param pos The position to check.
     * @return True if the coordinates are within the table bounds, false otherwise.
     */
    public boolean isValidPosition(Position pos) {
        return isValidPosition(pos.getX(), pos.getY());
    }

    /**
     * Moves a tile from one position to another.
     * @param x1 The x coordinate of the tile to move.
     * @param y1 The y coordinate of the tile to move.
     * @param x2 The target x coordinate.
     * @param y2 The target y coordinate.
     * @return true if the tile was moved successfully, false otherwise.
     * @throws IllegalArgumentException if the source or target coordinates are invalid,
     *                                  or if there's already a tile at the target coordinates.
     */
    public boolean move(int x1, int y1, int x2, int y2) {
        if(!isValidPosition(x1, y1) || !isValidPosition(x2, y2))
            throw new IllegalArgumentException("Invalid coordinates");

        if(getTile(x2, y2).isPresent())
            throw new IllegalArgumentException("There's another Tile at the target coordinates.");

        var coords1 = fromGameCoords(x1, y1);
        var coords2 = fromGameCoords(x2, y2);

        tiles[coords2[0]][coords2[1]] = tiles[coords1[0]][coords1[1]];
        tiles[coords1[0]][coords1[1]] = null;

        getTile(x2, y2).ifPresent(t -> {
          t.setPosition(x2, y2);
          t.afterMoved();
        });
        return getTile(x2, y2).isPresent();
    }

    /**
     * Moves a tile to the specified position.
     * @param t The tile to move.
     * @param x The target x coordinate.
     * @param y The target y coordinate.
     * @return true if the tile was moved successfully, false otherwise.
     * @throws IllegalArgumentException if the tile is not on this board.
     */
    public boolean move(Tile t, int x, int y) {
        if(this.getTile(t.getPosition()).orElse(null) != t)
            throw new IllegalArgumentException("Given Tile is not on this board.");

        return this.move(t.getPosition().getX(), t.getPosition().getY(), x, y);
    }

    /**
     * Moves a tile to the specified position.
     * @param t The tile to move.
     * @param pos The target position.
     * @return true if the tile was moved successfully, false otherwise.
     */
    public boolean move(Tile t, Position pos) {
        return this.move(t, pos.getX(), pos.getY());
    }

    /**
     * Removes a tile from the table at the specified position.
     * @param x The x coordinate of the tile to remove.
     * @param y The y coordinate of the tile to remove.
     */
    public void remove(int x, int y) {
        var coords = fromGameCoords(x, y);
        tiles[coords[0]][coords[1]] = null;
    }

    /**
     * Removes a tile from the table.
     * @param t The tile to remove.
     * @throws IllegalArgumentException if the tile is not on this board.
     */
    public void remove(Tile t) {
        if(this.getTile(t.getPosition()).orElse(null) != t)
            throw new IllegalArgumentException("Given Tile is not on this board.");

        this.remove(t.getPosition().getX(), t.getPosition().getY());
    }

    /**
     * Places a tile on the table at its current position.
     * @param t The tile to place.
     * @throws IllegalArgumentException if there's already a tile at the target position.
     */
    public void put(Tile t) {
        var pos = t.getPosition();
        var coords = fromGameCoords(pos.getX(), pos.getY());
        if(tiles[coords[0]][coords[1]] != null) {
            throw new IllegalArgumentException("Given Tile cannot be placed as there's another Tile already at the target position.");
        }

        tiles[coords[0]][coords[1]] = t;
    }

    /**
     * Converts game coordinates to array indices.
     * @param x The x coordinate in game space.
     * @param y The y coordinate in game space.
     * @return An array containing the corresponding array indices.
     */
    public int[] fromGameCoords(int x, int y) {
        return new int[] {x+8, y+8};
    }

    /**
     * Gets the tile at the specified position.
     * @param x The x coordinate to check.
     * @param y The y coordinate to check.
     * @return An Optional containing the tile if the position is valid, otherwise an empty Optional.
     */
    public final Optional<Tile> getTile(int x, int y) {
        if(!isValidPosition(x, y))
            throw new IndexOutOfBoundsException("Coordinates out of bounds");

        var coords = fromGameCoords(x, y);

        return Optional.ofNullable(tiles[coords[0]][coords[1]]);
    }

    /**
     * Gets the tile at the specified position.
     * @param pos The position to check.
     * @return An Optional containing the tile if the position is valid, otherwise an empty Optional.
     */
    public final Optional<Tile> getTile(Position pos) {
        return  getTile(pos.getX(), pos.getY());
    }

    /**
     * Gets the type of the locale at the specified position.
     * @param x The x coordinate to check.
     * @param y The y coordinate to check.
     * @return An Optional containing the type if the position is valid, otherwise an empty Optional.
     */
    public final Optional<Integer> getType(int x, int y) {
        if(!isValidPosition(x, y))
            throw new IndexOutOfBoundsException("Coordinates out of bounds");

        var coords = fromGameCoords(x, y);

        return Optional.of(types[coords[0]][coords[1]]);
    }

    /**
     * Gets the type of the locale at the specified position.
     * @param pos The position to check.
     * @return An Optional containing the type if the position is valid, otherwise an empty Optional.
     */
    public final Optional<Integer> getType(Position pos) {
        return getType(pos.getX(), pos.getY());
    }

    /**
     * Checks if the given type corresponds to any of the specified locales.
     * @param type The type to check.
     * @param position The locales to check against.
     * @return true if the type corresponds to any of the specified locales, false otherwise.
     */
    public static boolean isFrom(int type, Locale... position) {
        boolean result = false;
        for(var pos : position) {
            result |= (type & pos.flag) != 0;
        }
        return result;
    }

    /**
     * Checks if the given type corresponds to any temple.
     * @param type The type to check.
     * @return true if the type is a temple, false otherwise.
     */
    public static boolean isTemple(int type) {
        return (type >= NORTHERN_TEMPLE.flag); // smallest temple flag or greater
    }

    /**
     * Checks if the given type corresponds to a red garden.
     * @param type The type to check.
     * @return true if the type is a red garden, false otherwise.
     */
    public static boolean isRedGarden(int type) {
        return (type & RED_GARDEN.flag) != 0;
    }

    /**
     * Checks if the given type corresponds to a white garden.
     * @param type The type to check.
     * @return true if the type is a white garden, false otherwise.
     */
    public static boolean isWhiteGarden(int type) {
        return (type & WHITE_GARDEN.flag) != 0;
    }

    /**
     * Checks if the given type corresponds to a neutral garden.
     * @param type The type to check.
     * @return true if the type is a neutral garden, false otherwise.
     */
    public static boolean isNeutralGarden(int type) {
        return type == NEUTRAL_GARDEN.flag;
    }

    /**
     * Locales on the Pai Sho table.
     */
    @RequiredArgsConstructor
    public enum Locale {
        NEUTRAL_GARDEN(0),
        RED_GARDEN(1 << 1),
        WHITE_GARDEN(1 << 2),
        NORTHERN_TEMPLE(1 << 3),
        EASTERN_TEMPLE(1 << 4),
        SOUTHERN_TEMPLE(1 << 5),
        WESTERN_TEMPLE(1 << 6);

        @Getter
        final int flag;
    }
}
