package dev.rnandor.paisho.ginseng;

import dev.rnandor.paisho.PaiShoGame;
import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.ginseng.tiles.GinsengTile;
import dev.rnandor.paisho.io.GameManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class GameTests {

    private GinsengGame defaultGame;
    private GinsengGame alternativeGame;

    @BeforeEach
    void setup() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.defaultGame = new GinsengGame(false, false);
        this.alternativeGame = new GinsengGame(true, true);
    }

    private void performMove(GinsengGame game, int fromX, int fromY, int toX, int toY, boolean printBoard) {
        var tile = game.getTable().getTile(fromX, fromY);
        assertTrue(tile.isPresent(), "There should be a tile at (" + fromX + ", " + fromY + ").");
        var t = tile.get();
        log.debug("Tile at (" + fromX + ", " + fromY + ") is " + t.getName() + ":" + (t.isHost() ? "Host" : "Guest"));

        if(printBoard) {
            for (int i = 8; i >= -8; i--) {
                for (int j = -8; j <= 8; j++) {
                    if (!game.getTable().isValidPosition(j, i)) {
                        System.out.print("  ");
                        continue;
                    }

                    var til = game.getTable().getTile(j, i);
                    if (til.isPresent()) {
                        System.out.print("X ");
                    } else System.out.print(". ");
                }
                System.out.println();
            }
        }
        assertTrue(t.isValidMove(toX, toY));
        t.move(toX, toY);
    }

    private void allowCapturing(GinsengGame game) {
        boolean print = false;
        performMove(game, 2, 6, 2, 5, print);
        performMove(game, 2, -6, 2, -5, print);

        performMove(game, 4, 4, 3, 3, print);
        performMove(game, 4, -4, 3, -3, print);

        performMove(game, 0, 4, 1, 1, print);
        performMove(game, 0, -4, 1, -1, print);

        performMove(game, -3, 5, -3, -1, print);
        performMove(game, -3, -5, -3, -4, print);

        performMove(game, -3, -1, -1, -1, print);

        performMove(game, 0, 8, 2, 2, print);
    }

    @Test
    public void testWinCondition() {
        assertTrue(defaultGame.isGameRunning());
        allowCapturing(defaultGame);
        performMove(defaultGame, 2, 2, -2, -2, true);
        assertTrue(defaultGame.isGameOver() && defaultGame.getStatus() == PaiShoGame.GameStatus.GUEST_WIN);
    }

    @Test
    public void testDragonAbility() {
        var dragon = (GinsengTile) defaultGame.getTable().getTile(-1, 7).get();
        assertFalse(dragon.canUseAbility(), "Dragon should not be able to use ability outside Red Garden.");

        performMove(defaultGame, -1, 7, -1, 5, false);
        assertTrue(dragon.canUseAbility(), "Dragon should be able to use ability inside Red Garden.");

        var affected = dragon.getAbilityAffectedTiles();
        assertEquals(2, affected.size(), "Dragon should be able to push 3 tiles.");

        dragon.useAbility(new Position(0, 4));
        assertTrue(defaultGame.getTable().getTile(1, 3).isPresent(), "Tile should have been pushed to (0,6).");
        assertTrue(defaultGame.getTable().getTile(0, 4).isEmpty(), "Tile should have been pushed from (0,5).");
    }

    @Test
    public void testBadgermoleAbility() {
        var badgermole = (GinsengTile) defaultGame.getTable().getTile(1, 7).get();
        assertFalse(badgermole.canUseAbility(), "Badgermole should not be able to use ability outside White Garden.");

        performMove(defaultGame, 1, 7, 2, 5, false);
        assertTrue(badgermole.canUseAbility(), "Badgermole should be able to use ability inside White Garden.");

        var affected = badgermole.getAbilityAffectedTiles();
        assertEquals(2, affected.size(), "Badgermole should be able to tunnel to 4 tiles.");

        badgermole.useAbility(new Position(2, 6));
        assertTrue(defaultGame.getTable().getTile(2, 4).isPresent(), "Badgermole should have tunneled to (1,2).");
        assertTrue(defaultGame.getTable().getTile(2, 6).isEmpty(), "Badgermole should have tunneled from (1,5).");
    }

    @Test
    public void testSerialisation() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
        var temp = new GinsengGame(false, true);
        allowCapturing(temp);

        var manager = new GameManager<GinsengTile, GinsengGame>();
        var file = new File(System.identityHashCode(temp)+"_"+System.currentTimeMillis() + ".ginseng");
        manager.saveGame(temp, file.getAbsolutePath());

        var loaded = manager.loadGame(file.getAbsolutePath());
        assertEquals(temp.getTurn(), loaded.getTurn(), "Loaded game should have the same turn number.");
        assertEquals(temp.getStatus(), loaded.getStatus(), "Loaded game should have the same status.");

        assertEquals(temp.isAlternativeGinsengMode(), loaded.isAlternativeGinsengMode(), "Loaded game should have the same Ginseng mode.");
        assertEquals(temp.isBisonFlightMode(), loaded.isBisonFlightMode(), "Loaded game should have the same Bison mode.");

        for(int i = -8; i <= 8; i++) {
            for(int j = -8; j <= 8; j++) {
                if(!temp.getTable().isValidPosition(j, i))
                    continue;

                var tileOriginal = temp.getTable().getTile(j, i);
                var tileLoaded = loaded.getTable().getTile(j, i);

                assertEquals(tileOriginal.isPresent(), tileLoaded.isPresent(), "Tile presence at (" + j + "," + i + ") should be the same.");

                if(tileOriginal.isPresent()) {
                    var to = tileOriginal.get();
                    var tl = tileLoaded.get();

                    assertEquals(to.getName(), tl.getName(), "Tile name at (" + j + "," + i + ") should be the same.");
                    assertEquals(to.isHost(), tl.isHost(), "Tile host/guest status at (" + j + "," + i + ") should be the same.");
                }
            }
        }

        file.delete();
    }
}
