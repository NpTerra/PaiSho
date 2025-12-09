package dev.rnandor.paisho.ui.panels;

import dev.rnandor.paisho.PaiShoGame;
import dev.rnandor.paisho.Position;
import dev.rnandor.paisho.Tile;
import dev.rnandor.paisho.ginseng.GinsengGame;
import dev.rnandor.paisho.ginseng.tiles.GinsengTile;
import dev.rnandor.paisho.ui.ResourceHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Slf4j
public class GameBoardPanel extends ImagePanel {

    private final HashMap<String, BufferedImage> hostImages = new HashMap<>();
    private final HashMap<String, BufferedImage> guestImages = new HashMap<>();
    private final HashMap<String, BufferedImage> statusImages = new HashMap<>();

    private GinsengGame game;

    private GinsengTile selected;
    private List<Position> moves;
    private List<Position> targets;
    private Set<Tile> swapTargets;

    @Getter
    private boolean swapFreeze = false;
    private static int[] capX = {8, 7, 6, 8, 7, 8};
    private static int[] capY = {8, 8, 8, 7, 7, 6};

    /**
     * Constructs a new GameBoardPanel with the specified background image.
     *
     * @throws IOException if an I/O error occurs
     */
    public GameBoardPanel() throws IOException {
        super(ResourceHelper.getResource("/tiles/board/classy.png"));

        moves = new ArrayList<>();
        targets = new ArrayList<>();
        swapTargets = new TreeSet<>();

        setMinimumSize(new Dimension(612, 612));
        setMaximumSize(new Dimension(612, 612));
        setPreferredSize(new Dimension(612, 612));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                if(game.isGameOver())
                    return;

                int cx = e.getX()-34/2;
                int cy = e.getY()-34/2;

                if(cx < 0 || cx > 34*17 || cy < 0 || cy > 34*17)
                    return;

                int tx = (cx/34)-8;
                int ty = 8-cy/34;

                if(!swapFreeze) {

                    if(!game.getTable().isValidPosition(tx, ty))
                        return;

                    if (selected == null) {
                        var tile = game.getTable().getTile(tx, ty);
                        tile.ifPresent(value -> {
                            var gs = (GinsengTile) value;
                            if (game.isHostTurn() != gs.isHost())
                                return;

                            selected = gs;
                            moves = selected.getValidMoves();
                        });
                    } else {
                        var pos = selected.getPosition();
                        if (tx == pos.getX() && ty == pos.getY()) {
                            selected = null;
                            if (!targets.isEmpty())
                                game.nextTurn();

                            moves = new ArrayList<>();
                            targets = new ArrayList<>();
                        }
                    }

                    boolean shouldSwap = false;

                    for (var move : moves) {
                        if (move.getX() == tx && move.getY() == ty) {
                            selected.move(move);

                            moves = new ArrayList<>();

                            if (selected.canUseAbility()) {
                                var affected = selected.getAbilityAffectedTiles();
                                if (!affected.isEmpty()) {
                                    targets = affected;
                                    break;
                                }
                            }

                            if (selected.isInTemple()) {
                                shouldSwap = true;
                                break;
                            }

                            game.nextTurn();
                            selected = null;
                            break;
                        }
                    }

                    for (var target : targets) {
                        if (target.getX() == tx && target.getY() == ty) {
                            selected.useAbility(target);
                            targets = new ArrayList<>();

                            if (selected.isInTemple()) {
                                shouldSwap = true;
                                break;
                            }

                            game.nextTurn();
                            selected = null;
                            break;
                        }
                    }

                    if(shouldSwap) {
                        showTileReplacementDialog();
                    }

                    if(selected == null) {
                        game.checkForDraw();
                    }
                }
                else {

                    var pos = selected.getPosition();
                    if (tx == pos.getX() && ty == pos.getY()) {
                        closeTileReplacementDialog();
                    }

                    var cap = swapTargets.toArray(new Tile[0]);
                    for (int i = 0; i < cap.length; i++) {
                        var x = getCapX(cap[i], i);
                        var y = getCapY(cap[i], i);
                        if (x == tx && y == ty) {
                            cap[i].setPosition(pos);
                            selected.capture();
                            ((GinsengTile) cap[i]).uncapture();

                            closeTileReplacementDialog();

                            break;
                        }
                    }
                }

                repaint();
            }
        });
    }

    /**
     * Sets the current game and initializes the tile images.
     *
     * @param game the GinsengGame instance to display
     * @throws IOException if an I/O error occurs
     */
    public void setGame(GinsengGame game) throws IOException {
        this.game = game;
        this.selected = null;
        this.moves = new ArrayList<>();
        this.targets = new ArrayList<>();

        hostImages.clear();
        guestImages.clear();
        statusImages.clear();

        statusImages.put("option", ImageIO.read(ResourceHelper.getResource("/tiles/option.png")));
        statusImages.put("capture", ImageIO.read(ResourceHelper.getResource("/tiles/capture.png")));
        statusImages.put("ability", ImageIO.read(ResourceHelper.getResource("/tiles/ability.png")));
        statusImages.put("selected", ImageIO.read(ResourceHelper.getResource("/tiles/selected.png")));

        hostImages.put("idk", ImageIO.read(ResourceHelper.getResource("/tiles/idk.png")));
        guestImages.put("idk", hostImages.get("idk"));

        for(var tc : game.getRegistry().getTiles()) {
            var code = game.getRegistry().getCode(tc);
            if(code.isEmpty())
                throw new RuntimeException("?????");
            hostImages.put(code.get(), ImageIO.read(ResourceHelper.getResource("/tiles/host/" + code.get() + ".png")));
            guestImages.put(code.get(), ImageIO.read(ResourceHelper.getResource("/tiles/guest/" + code.get() + ".png")));
        }
    }

    /**
     * Opens the tile replacement dialog, freezing the game state and preparing the captured tiles for selection.
     */
    private void showTileReplacementDialog() {
        swapFreeze = true;
        swapTargets = selected.isHost() ? game.getHostCaptured() : game.getGuestCaptured();
    }

    /**
     * Closes the tile replacement dialog and resets the relevant state.
     */
    public void closeTileReplacementDialog() {
        swapFreeze = false;
        selected = null;
        moves = new ArrayList<>();
        targets = new ArrayList<>();
        swapTargets = new TreeSet<>();
        game.nextTurn();
    }

    /**
     * Draws the tile selection screen overlay when a tile is to be swapped with one from the captured ones.
     *
     * @param g The Graphics object to draw on.
     */
    private void drawSelectionScreen(Graphics g) {
        if(!swapFreeze)
            return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(5, 5, 5, 180));
        g2.fillOval(0, 0, getWidth(), getHeight());

        drawTile(g, selected);
        var pos = selected.getPosition();
        drawImageAtPosition(g, pos.getX(), pos.getY(), statusImages.get("selected"));

        g2.setColor(new Color(18, 85, 255, 114));

        var cap = swapTargets.toArray(new Tile[0]);
        for(int i = 0; i < cap.length; i++) {
            pos = new Position(getCapX(cap[i], i), getCapY(cap[i], i));
            var coords = game.getTable().fromGameCoords(pos.getX(), pos.getY());
            coords[1] = 16-coords[1];

            g2.fillOval(coords[0]*34 + 17, coords[1]*34 + 17, 34, 34);
        }

        g2.dispose();
    }

    /**
     * Gets the x-coordinate for a captured tile based on its index.
     *
     * @param tile The captured tile.
     * @param i The index of the captured tile.
     * @return The x-coordinate for the captured tile.
     */
    private int getCapX(Tile tile, int i) {
        var x = capX[i%capX.length];

        if(tile.isGuest())
            x *= -1;

        if(i >= capX.length)
            x *= -1;

        return x;
    }

    /**
     * Gets the y-coordinate for a captured tile based on its index.
     *
     * @param tile The captured tile.
     * @param i The index of the captured tile.
     * @return The y-coordinate for the captured tile.
     */
    private int getCapY(Tile tile, int i) {
        var y = capY[i%capY.length];

        if(tile.isHost())
            y *= -1;

        return y;
    }

    /**
     * Draws the captured tiles for both players on the game board.
     *
     * @param g The Graphics object to draw on.
     */
    private void drawCaptured(Graphics g) {

        var hc = game.getHostCaptured().toArray(new Tile[0]);
        var gc = game.getGuestCaptured().toArray(new Tile[0]);

        for(int i = 0; i < hc.length; i++) {
            var x = getCapX(hc[i], i);
            var y = getCapY(hc[i], i);

            drawTile(g, (GinsengTile) hc[i], x, y);
        }

        for(int i = 0; i < gc.length; i++) {
            var x = getCapX(gc[i], i);
            var y = getCapY(gc[i], i);

            drawTile(g, (GinsengTile) gc[i], x, y);
            drawImageAtPosition(g, x, y, statusImages.get("ability"));
        }
    }

    /**
     * Draws an image at the specified game coordinates.
     * These images are connected to Tiles, so the size and position correspond to tiles on the board.
     *
     * @param g The Graphics object to draw on.
     * @param x The x-coordinate in game coordinates.
     * @param y The y-coordinate in game coordinates.
     * @param image The BufferedImage to draw.
     */
    private void drawImageAtPosition(Graphics g, int x, int y, BufferedImage image) {
        var coords = game.getTable().fromGameCoords(x, y);
        coords[1] = 16-coords[1];

        g.drawImage(image, coords[0]*34 + 17, coords[1]*34 + 17, 34, 34, this);
    }

    /**
     * Draws a centered string on the Graphics2D object.
     *
     * @param g2 The Graphics2D object to draw on.
     * @param color The color of the text.
     * @param text The text to draw.
     * @param font The font to use for the text.
     * @param x The x-coordinate of the center position.
     * @param y The y-coordinate of the center position.
     */
    public void drawCenteredString(Graphics2D g2, Color color, String text, Font font, int x, int y) {
        var pf = g2.getFont();
        var pc = g2.getColor();

        g2.setFont(font);
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();

        int textWidth  = fm.stringWidth(text);
        int textHeight = fm.getAscent() - fm.getDescent();

        int tx = x - textWidth/2;
        int ty = y + textHeight/2;

        g2.drawString(text, tx, ty);
        g2.setFont(pf);
        g2.setColor(pc);
    }

    /**
     * Checks if the given tile is in danger of being captured.
     *
     * @param tile The GinsengTile to check.
     * @return true if the tile is in danger, false otherwise.
     */
    private boolean isInDanger(GinsengTile tile) {
        var pos = tile.getPosition();
        for(var move : moves) {
            if(move.getX() == pos.getX() && move.getY() == pos.getY()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given tile is a target for an ability.
     *
     * @param tile The GinsengTile to check.
     * @return true if the tile is a target, false otherwise.
     */
    private boolean isTarget(GinsengTile tile) {
        var pos = tile.getPosition();
        for(var target : targets) {
            if(target.getX() == pos.getX() && target.getY() == pos.getY()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Draws a tile on the game board at the specified position.
     *
     * @param g The Graphics object to draw on.
     * @param tile The GinsengTile to draw.
     * @param x The x-coordinate in game coordinates.
     * @param y The y-coordinate in game coordinates.
     */
    private void drawTile(Graphics g, GinsengTile tile, int x, int y) {
        var code = game.getRegistry().getCode(tile.getClass()).orElse("idk");
        var img = tile.isHost() ? hostImages.get(code) : guestImages.get(code);

        drawImageAtPosition(g, x, y, img);
    }

    /**
     * Draws a tile on the game board at its current position.
     *
     * @param g The Graphics object to draw on.
     * @param tile The GinsengTile to draw.
     */
    private void drawTile(Graphics g, GinsengTile tile) {
        var pos = tile.getPosition();
        drawTile(g, tile, pos.getX(), pos.getY());
    }

    /**
     * Draws all tiles on the game board, highlighting those that are targets or in danger.
     *
     * @param g The Graphics object to draw on.
     */
    private void drawTiles(Graphics g) {
        game.getTiles().forEach(tile -> {
            if(tile.isCaptured())
                return;

            drawTile(g, tile);

            var pos = tile.getPosition();

            var t = isTarget(tile);
            var d = isInDanger(tile);

            if(game.isGameRunning() && (t || d || game.isHostTurn() == tile.isGuest())) {

                var coords = game.getTable().fromGameCoords(pos.getX(), pos.getY());
                coords[1] = 16-coords[1];

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                var color = new Color(35, 35, 35, 180);

                if(d) {
                    color = new Color(200, 0, 0, 120);
                }

                if(t) {
                    color = new Color(18, 85, 255, 114);
                }

                g2.setColor(color);
                g2.fillOval(coords[0]*34 + 17, coords[1]*34 + 17, 34, 34);

                g2.dispose();
            }
        });
    }

    /**
     * Draws selection accents on the game board for the selected tile, valid moves, and ability targets.
     *
     * @param g The Graphics object to draw on.
     */
    private void drawSelectionAccents(Graphics g) {
        if(selected != null) {
            var pos = selected.getPosition();

            drawImageAtPosition(g, pos.getX(), pos.getY(), statusImages.get("selected"));

            for(var move : moves) {
                var code = game.getTable().getTile(move).isEmpty() ? "option" : "capture";
                drawImageAtPosition(g, move.getX(), move.getY(), statusImages.get(code));
            }

            for(var target : targets) {
                drawImageAtPosition(g, target.getX(), target.getY(), statusImages.get("ability"));
            }
        }
    }

    /**
     * Draws the end game screen overlay if the game is over.
     *
     * @param g The Graphics object to draw on.
     */
    private void drawEndGameScreen(Graphics g) {
        if(game.isGameOver()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(5, 5, 5, 180));
            g2.fillOval(0, 0, getWidth(), getHeight());
            String text;
            Color color;
            switch(game.getStatus()) {
                case PaiShoGame.GameStatus.HOST_WIN -> {
                    text = "Host Wins!";
                    color = Color.WHITE;
                }
                case PaiShoGame.GameStatus.GUEST_WIN -> {
                    text = "Guest Wins!";
                    color = Color.ORANGE;
                }
                case PaiShoGame.GameStatus.DRAW -> {
                    text = "Draw!";
                    color = Color.CYAN;
                }
                default -> {
                    text = "GAME OVER";
                    color = Color.RED;
                }
            }
            drawCenteredString(g2, color, text, new Font("Arial", Font.BOLD, 64), getWidth()/2, getHeight()/2);

            g2.dispose();
        }
    }

    /**
     * Draws debug lines on the game board for visual reference.
     *
     * @param g The Graphics object to draw on.
     */
    private void drawDebugLines(Graphics g) {
        for(int i = 0; i <= 18; i++) {
            g.setColor(Color.RED);
            g.drawLine(0, i*34, 612, i*34);
            g.setColor(Color.GREEN);
            g.drawLine(i*34, 0, i*34, 612);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawTiles(g);

        drawSelectionAccents(g);

        drawCaptured(g);

        drawSelectionScreen(g);

        drawEndGameScreen(g);

        //drawDebugLines(g);
    }
}
