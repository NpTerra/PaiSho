package dev.rnandor.paisho;

import java.util.Optional;

public class Table {
    private static int TEMPLE = 1 << 0;
    private static int RED_GARDEN = 1 << 1;
    private static int WHITE_GARDEN = 1 << 2;


    private Tile[][] tiles = new Tile[17][17];
    private int[][] types = new int[17][17];

    public Table() {
        // Western Temple
        types[0][7] = TEMPLE;
        types[0][8] = TEMPLE;
        types[0][9] = TEMPLE;
        types[1][8] = TEMPLE;

        // Eastern Temple
        types[16][7] = TEMPLE;
        types[16][8] = TEMPLE;
        types[16][9] = TEMPLE;
        types[15][8] = TEMPLE;

        // Northern Temple
        types[7][0] = TEMPLE;
        types[8][0] = TEMPLE;
        types[9][0] = TEMPLE;
        types[8][1] = TEMPLE;

        // Southern Temple
        types[7][16] = TEMPLE;
        types[8][16] = TEMPLE;
        types[9][16] = TEMPLE;
        types[8][15] = TEMPLE;

        // Gardens
        for(int x = -7; x <= 7; x++) {
            for (int y = -7; y <= 7; y++) {
                int flag = x*y > 0 ? WHITE_GARDEN : RED_GARDEN; // if the sign matches, it's a white garden
                if(x*y == 0) flag |= WHITE_GARDEN;              // if either axis is on the 0, it's both

                if(Math.abs(x) + Math.abs(y) > 7)
                    continue;

                types[x+8][y+8] |= flag;                         // setting the selected garden type(s)
            }
        }

        for(int x = 16; x >= 0; x--) {
            for (int y = 0; y < 17; y++) {
                if(types[y][x] != 0)
                    System.out.print(types[y][x] + " ");
                else
                    System.out.print("  ");
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
    private boolean isValidPosition(int x, int y) {
        return x*x + y*y <= 64;
    }

    public final Optional<Tile> getTile(int x, int y) {
        if(!isValidPosition(x, y))
            throw new IndexOutOfBoundsException("Coordinates out of bounds");

        return Optional.ofNullable(tiles[x+8][y+8]);
    }

    public final Optional<Integer> getType(int x, int y) {
        if(!isValidPosition(x, y))
            throw new IndexOutOfBoundsException("Coordinates out of bounds");

        return Optional.of(types[x+8][y+8]);
    }

    public static boolean isTemple(int type) {
        return (type & TEMPLE) != 0;
    }

    public static boolean isRedGarden(int type) {
        return (type & RED_GARDEN) != 0;
    }

    public static boolean isWhiteGarden(int type) {
        return (type & WHITE_GARDEN) != 0;
    }

}
