package dev.rnandor.paisho.ginseng.tiles;

import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Table;
import dev.rnandor.paisho.Tile;
import dev.rnandor.paisho.ginseng.GinsengGame;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public abstract class GinsengTile extends Tile {

    @Getter @Setter
    private GinsengGame game;

    private final boolean hasUtility;

    @Getter @Setter
    private boolean captured;

    protected GinsengTile(String name, boolean hasUtility, boolean host, Table table, int locX, int locY) {
        super(name, host, table, locX, locY);
        this.hasUtility = hasUtility;
    }

    @Override
    public final boolean isValidMove(int x, int y) {
        return getValidMoves().stream().anyMatch(pos -> pos.getX() == x && pos.getY() == y);
    }

    @Override
    public List<Position> getValidMoves() {
        if(this.isCaptured())
            return Collections.emptyList();

        return getBasicMoves(true, false);
    }

    protected final List<Position> getBasicMoves(boolean canCapture, boolean canFly) {

        // flying = rhombus pattern
        // not flying = BFS
        boolean bisonBoosted = isBisonBoosted();
        int radius = bisonBoosted ? 6 : 5;
        boolean flying = game.isBisonFlightMode() || canFly;

        if(bisonBoosted && flying)
            return getMovesByRhombus(radius, canCapture);
        else
            return getMovesByBFS(radius, canCapture);
    }

    private List<Position> getMovesByRhombus(int radius, boolean withCaptures) {
        var center = this.getPosition();
        var moves = new ArrayList<Position>();

        for(int i = center.getX()-radius; i <= center.getX()+radius; i++) {
            for(int j = center.getY()-radius; j <= center.getY()+radius; j++) {

                // skip self
                if(i == center.getX() && j == center.getY())
                    continue;

                var ax = Math.abs(center.getX() - i);
                var ay = Math.abs(center.getY() - j);

                if(ax+ay <= radius && canMoveThere(i, j, withCaptures)) {
                    moves.add(new Position(i, j));
                }
            }
        }

        return moves;
    }

    private List<Position> getMovesByBFS(int radius, boolean withCaptures) {
        var dist = new int[17][17];
        for(var i = 0; i < dist.length; i++) {
            for(int j = 0; j < dist.length; j++) {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }

        var center = this.getPosition();
        var centerNormalised = table.fromGameCoords(center.getX(), center.getY());
        dist[centerNormalised[0]][centerNormalised[1]] = 0;

        Queue<Position> queue = new LinkedList<>();
        queue.add(center);

        var moves = new ArrayList<Position>();

        while(!queue.isEmpty()) {
            var curr = queue.poll();

            var up = new Position(curr.getX(), curr.getY()+1);
            var down = new Position(curr.getX(), curr.getY()-1);
            var left = new Position(curr.getX()-1, curr.getY());
            var right = new Position(curr.getX()+1, curr.getY());

            addIfValid(curr, up, queue, withCaptures, dist, radius, moves);
            addIfValid(curr, down, queue, withCaptures, dist, radius, moves);
            addIfValid(curr, left, queue, withCaptures, dist, radius, moves);
            addIfValid(curr, right, queue, withCaptures, dist, radius, moves);
        }

        return moves;
    }

    private void addIfValid(Position curr, Position next, Queue<Position> queue, boolean withCaptures, int[][] dist, int radius, List<Position> moves) {
        if(!table.isValidPosition(next.getX(), next.getY()))
            return;

        var norm = table.fromGameCoords(next.getX(), next.getY());
        if(dist[norm[0]][norm[1]] != Integer.MAX_VALUE)
            return;

        if(canMoveThere(next, withCaptures)) {
            var currNorm = table.fromGameCoords(curr.getX(), curr.getY());
            dist[norm[0]][norm[1]] = dist[currNorm[0]][currNorm[1]] + 1;

            if(dist[norm[0]][norm[1]] <= radius) {
                moves.add(next);
                if(table.getTile(next).isEmpty())
                    queue.add(next);
            }
        }
    }

    public boolean canMoveThere(int x, int y, boolean withCaptures) {
        if(!table.isValidPosition(x, y))
            return false;

        var tile = table.getTile(x, y);
        int type = table.getType(x, y).orElse(0);

        // only lotuses may use the northern and southern temples
        if(Table.isFrom(type, Table.Locale.NORTHERN_TEMPLE)) {
            return this instanceof WhiteLotus && this.isGuest();
        }

        if(Table.isFrom(type, Table.Locale.SOUTHERN_TEMPLE)) {
            return this instanceof WhiteLotus && this.isHost();
        }

        // any other place
        return (tile.isEmpty()                                              // - if the target is empty, no other check is needed
               || (withCaptures                                             // - if capturing is enabled, then:
                   && tile.get().isGuest() != this.isGuest()                //   - the target tile's owner should be different
                   && game.isCapturingAllowed()                             //   - both lotuses should be outside temples
                   && !(((GinsengTile) tile.get()).isGinsengProtected())    //   - the target tile can't be protected by a Ginseng
               )) && !this.isKoiTrapped();                                  // - Koi tiles can block enemy tiles from moving
    }

    public boolean canMoveThere(Position pos, boolean withCaptures) {
        return canMoveThere(pos.getX(), pos.getY(), withCaptures);
    }

    public boolean isGinsengProtected() {
        return game.isAlternativeGinsengMode() ?
                this.isGinsengProtectedByProximity() :
                this.isGinsengProtectedByLineOfSight();
    }

    private int checkForGinseng(int x, int y) {
        if(!table.isValidPosition(x, y))
            return -1;

        var t = table.getTile(x, y);
        if(t.isEmpty())
            return 0;

        if(t.get() instanceof Ginseng gin) {
            if (gin.isGuest() == this.isGuest())
                return 1;
        }
        return -1;
    }

    private boolean isGinsengProtectedByProximity() {
        var pos =  getPosition();

        for(int i = 1; i <= 5; ++i) {
            int up = checkForGinseng(pos.getX(), pos.getY()+i);
            int down = checkForGinseng(pos.getX(), pos.getY()-i);
            int left = checkForGinseng(pos.getX()-1, pos.getY());
            int right = checkForGinseng(pos.getX()+1, pos.getY());

            if(up == 1 || down == 1 || left == 1 || right == 1)
                return true;
        }

        return false;
    }

    private boolean isGinsengProtectedByLineOfSight() {
        var pos = getPosition();

        for(int x = pos.getX()+1; x < 17; ++x) {
            int r = checkForGinseng(x, pos.getY());

            if(r == -1)
                break;

            if(r == 1)
                return true;
        }

        for(int x = pos.getX()-1; x >= 0; --x) {
            int r = checkForGinseng(x, pos.getY());

            if(r == -1)
                break;

            if(r == 1)
                return true;
        }

        for(int y = pos.getY()+1; y < 17; ++y) {
            int r = checkForGinseng(pos.getX(), y);

            if(r == -1)
                break;

            if(r == 1)
                return true;
        }

        for(int y = pos.getY()-1; y >= 0; --y) {
            int r = checkForGinseng(pos.getX(), y);

            if(r == -1)
                break;

            if(r == 1)
                return true;
        }

        return false;
    }

    public boolean isBisonBoosted() {
        return this.hasTileInSurroundings(SkyBison.class, true, true, Table.Locale.RED_GARDEN);
    }

    public boolean isKoiTrapped() {
        return this.hasTileInSurroundings(Koi.class, false, true, Table.Locale.WHITE_GARDEN);
    }

    public boolean isTurtleBlocked() {
        return this.hasTileInSurroundings(LionTurtle.class, false, false);
    }

    private boolean hasTileInSurroundings(Class<? extends GinsengTile> tileClass, boolean sameTeam, boolean turtleCheck, Table.Locale... locales) {
        var pos = getPosition();
        for(int i = pos.getX()-1; i <= pos.getX()+1; i++) {
            for(int j = pos.getY()-1; j <= pos.getY()+1; j++) {
                if(( i == pos.getX() && j == pos.getY() ) || !table.isValidPosition(i, j))
                    continue;

                var t = this.table.getTile(i, j);
                if(t.isEmpty() || !( (t.get() instanceof GinsengTile gs) && (gs.getClass().equals(tileClass)) ))
                    continue;

                if(gs.isFromArea(locales)
                   && gs.isGuest() == (sameTeam == this.isGuest())) {
                    return !turtleCheck || !gs.isTurtleBlocked();
                }
            }
        }

        return false;
    }

    protected List<? extends GinsengTile> getTilesInSurroundings() {
        var pos = getPosition();
        var tiles = new ArrayList<GinsengTile>();
        for(int i = pos.getX()-1; i <= pos.getX()+1; i++) {
            for(int j = pos.getY()-1; j <= pos.getY()+1; j++) {
                if(( i == pos.getX() && j == pos.getY() ) || !table.isValidPosition(i, j))
                    continue;

                var t = this.table.getTile(i, j);
                if(t.isEmpty() || !(t.get() instanceof GinsengTile gs))
                    continue;

                tiles.add(gs);
            }
        }

        return tiles;
    }

    public boolean canUseAbility() {
        return false;
    }

    public List<Position> getAbilityAffectedTiles() {
        if(this.hasUtility)
            throw new UnsupportedOperationException("Not yet implemented.");
        else
            throw new UnsupportedOperationException("This tile does not have an additional utility.");
    }

    public void useAbility(Position target) {
        if(this.hasUtility)
            throw new UnsupportedOperationException("Not yet implemented.");
        else
            throw new UnsupportedOperationException("This tile does not have an additional utility.");
    }

    @Override
    public void move(int x, int y) throws IllegalArgumentException {
        if(!isValidMove(x, y))
            throw new IllegalArgumentException("The target position cannot be accessed with this tile.");

        table.getTile(x, y).ifPresent(tile -> {
            if(tile instanceof GinsengTile gs) {
                gs.capture();
            }
        });

        super.move(x, y);
    }

    public void capture() {
        table.remove(this);
        this.setCaptured(true);

        if(this.isGuest()) {
            game.getGuestCaptured().add(this);
        }
        else {
            game.getHostCaptured().add(this);
        }
    }

    public void uncapture() {
        this.setCaptured(false);
        table.put(this);

        if(this.isGuest()) {
            game.getGuestCaptured().remove(this);
        }
        else {
            game.getHostCaptured().remove(this);
        }
    }
}
