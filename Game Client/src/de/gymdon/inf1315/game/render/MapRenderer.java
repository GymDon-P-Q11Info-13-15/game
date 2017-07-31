package de.gymdon.inf1315.game.render;

import de.gymdon.inf1315.game.*;
import de.gymdon.inf1315.game.client.Client;
import de.gymdon.inf1315.game.render.gui.*;
import de.gymdon.inf1315.game.tile.Tile;
import de.gymdon.inf1315.game.tile.TileMap;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class MapRenderer extends GuiScreen implements Renderable, ActionListener, MouseInputListener, MouseWheelListener, KeyListener {

    public static final int TILE_SIZE_SMALL = 32;
    public static final int TILE_SIZE_NORMAL = 64;
    public static final int TILE_SIZE_BIG = 128;
    private static final Font fontPlayer = Client.instance.translation.font.deriveFont(50F);
    private static final Font fontGold = Client.instance.translation.font.deriveFont(35F);
    private static final Font fontTiny = Client.instance.translation.font.deriveFont(25F);
    private static final Color goldColor = new Color(0xEDE275);
    private static final Color errorColor = new Color(0xEDE275);
    public int tileSize = TILE_SIZE_NORMAL;
    public double zoom = 0.1;
    public boolean firstClick = false;
    public boolean attack = false;
    public boolean move = false;
    public boolean stack = false;
    public boolean build = false;
    public boolean spawn = false;
    public boolean upgrade = false;
    public Class<? extends Unit> spawnClass;
    public Class<? extends Building> buildClass;
    private GuiButton gameStateButton = new GuiButton(this, 0, 20, 20, null);
    private BufferedImage map = null;
    private BufferedImage cache = null;
    private TileMap mapCache = null;
    private int scrollX = 0;
    private int scrollY = 0;
    private int diffX = 0;
    private int diffY = 0;
    private GameObject selected;
    private boolean[][] fieldHover;
    private boolean[][] field;
    private GuiGameMenu guiGameObject;
    private int guiDestX;
    private int guiDestY;
    private int guiPosX;
    private int guiPosY;
    private int guiWidth;
    private int guiHeight;
    private AffineTransform affinetransform = new AffineTransform();
    private FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    private boolean squareAction = false;
    private boolean activeAction = false;
    private boolean[][] range;
    private int sRange;
    private boolean stackErrHP = false;
    private boolean stackErrClass = false;
    private GameObject errObject;
    private int healthOptionRAM = -1;
    private boolean healthUpToDate = false;
    private int phaseTime = 120;
    private int leftTime = phaseTime;
    private Timer phaseTimer;
    private int tutorialPhase = -1;
    private int tutorialPhases = 9;

    public MapRenderer() {
        controlList.add(gameStateButton);
    }

    public void tutorial() {
        tutorialPhase = 0;
    }

    @Override
    public void render(Graphics2D g2do, int width, int height) {
        if (Client.instance.currentScreen == null && phaseTimer == null && tutorialPhase == -1) {
            phaseTimer = new Timer();
            phaseTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (leftTime > 0)
                        leftTime--;
                    else {
                        leftTime = phaseTime;
                        Client.instance.mapren.actionPerformed(new ActionEvent(gameStateButton, ActionEvent.ACTION_PERFORMED, null));
                    }
                    if (leftTime <= phaseTime - 8)
                        Client.instance.game.GoldDif = 0;
                }
            }, 1000, 1000);
        } else if (Client.instance.currentScreen != null && phaseTimer != null) {
            phaseTimer.cancel();
            phaseTimer.purge();
            phaseTimer = null;
        } else if (Client.instance.currentScreen == null && phaseTimer != null && tutorialPhase >= 0) {
            phaseTimer.cancel();
            phaseTimer.purge();
            phaseTimer = null;
        }
        Client.instance.game.gm.run();
        cache = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = cache.createGraphics();
        if (attack || move || stack || build || spawn || upgrade)
            activeAction = true;
        else {
            activeAction = false;
            squareAction = false;
        }

        TileMap map = Client.instance.game.map;
        int mapWidth = map.getWidth();
        int mapHeight = map.getHeight();
        double w = (mapWidth * tileSize * zoom);
        if (w < width)
            zoom /= w / width;
        double h = (mapHeight * tileSize * zoom);
        if (h < height)
            zoom /= h / height;

        AffineTransform tx = g2d.getTransform();
        g2d.translate(-scrollX, -scrollY);
        g2d.scale(zoom, zoom);

        // Rendering Map
        if (this.map == null || !map.equals(mapCache)) {
            this.map = new BufferedImage(mapWidth * tileSize, mapHeight * tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = this.map.createGraphics();
            for (int x = 0; x < mapWidth; x++) {
                for (int y = 0; y < mapHeight; y++) {
                    Tile tile = map.get(x, y);
                    Texture tex = TileRenderMap.getTexture(tile);
                    if (tex == null)
                        continue;
                    g.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize, tileSize, tex);
                }
            }
            g.dispose();
            mapCache = map;
        }
        g2d.drawImage(this.map, 0, 0, null);

        // Rendering Buildings
        Building[][] buildings = Client.instance.game.buildings;
        for (int x = 0; x < buildings.length; x++) {
            for (int y = 0; y < buildings[x].length; y++) {
                Building b = buildings[x][y];
                if (b != null) {
                    Texture tex = BuildingRenderMap.getTexture(b);
                    if (tex != null)
                        g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tex.getWidth() / (TILE_SIZE_NORMAL / tileSize), tex.getHeight() / (TILE_SIZE_NORMAL / tileSize), tex);
                    int lW = tex.getWidth() / (TILE_SIZE_NORMAL / tileSize);
                    int lH = tex.getHeight() / (TILE_SIZE_NORMAL / tileSize) / 4;
                    Texture layer = StandardTexture.get("layer");
                    g2d.drawImage(layer.getImage(), x * tileSize, y * tileSize - tileSize / 2, lW, lH, layer);
                    int lM = lW / 16;
                    int tM = lH / 4;
                    int pHP = 0;
                    try {
                        if (b instanceof Mine) {
                            Building bb = b.getClass().getConstructor(Integer.TYPE, Integer.TYPE).newInstance(0, 0);
                            bb.occupy(null);
                            pHP = bb.getHp();
                        } else
                            pHP = b.getClass().getConstructor(Player.class, Integer.TYPE, Integer.TYPE).newInstance(null, 0, 0).getHp();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    g2d.translate(x * tileSize + lM, y * tileSize - tileSize / 2 + tM);
                    {
                        // Relative (Lebensbalken)
                        if (Client.instance.preferences.game.health == 0 || Client.instance.preferences.game.health == 2) {
                            for (int i = 0; i < 10; i++) {
                                if ((i + 1) * 10 <= b.getHp() * 100 / pHP) {
                                    g2d.setColor(Color.GREEN);
                                    g2d.fillRect((lW - lM * 2) / 10 * i, 0, (lW - lM * 2) / 10, (lH - tM * 2));
                                } else {
                                    g2d.setColor(Color.GRAY);
                                    g2d.fillRect((lW - lM * 2) / 10 * i, 0, (lW - lM * 2) / 10, (lH - tM * 2));
                                }
                            }
                        }

                        // Absolute (Zahl)
                        if (Client.instance.preferences.game.health == 1 || Client.instance.preferences.game.health == 2) {
                            Font f = fontTiny.deriveFont(fontTiny.getStyle(), 11 / (TILE_SIZE_NORMAL / tileSize) * b.getSizeX());
                            g2d.setFont(f);
                            if (Client.instance.preferences.game.health == 1)
                                g2d.setColor(Color.WHITE);
                            else if (Client.instance.preferences.game.health == 2)
                                g2d.setColor(Color.BLACK);
                            int gWidth = (int) (f.getStringBounds("" + b.getHp(), frc).getWidth());
                            g2d.drawString("" + b.getHp(), (lW - lM * 2) / 2 - gWidth / 2, lH - tM * 2);
                        }
                    }
                    g2d.translate(-(x * tileSize + lM), -(y * tileSize - tileSize / 2 + tM));

                    if (Client.instance.game.GoldDif != 0 && b.getIncome() != 0 && Client.instance.game.activePlayer == b.getOwner()) {
                        g2d.setFont(fontTiny);
                        g2d.setColor(goldColor);
                        int gWidth = (int) (fontTiny.getStringBounds("+ " + b.getIncome(), frc).getWidth());
                        int gHeight = (int) (fontTiny.getStringBounds("+ " + b.getIncome(), frc).getHeight());
                        g2d.drawString("+ " + b.getIncome(), x * tileSize + lW / 2 - gWidth / 2, y * tileSize - gHeight * 2);
                    }
                }
            }
        }
        g2d.setColor(Color.WHITE);

        // Rendering Units
        if (tutorialPhase == 7 && Client.instance.game.units[5][18] == null)
            Client.instance.game.units[5][18] = new Miner(Client.instance.game.player1, 5, 18);

        Unit[][] units = Client.instance.game.units;
        for (int x = 0; x < units.length; x++) {
            for (int y = 0; y < units[x].length; y++) {
                Unit u = units[x][y];
                if (u != null) {
                    Texture tex = UnitRenderMap.getTexture(u);
                    if (tex != null && u.getOwner() == Client.instance.game.player1) {
                        g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tex.getWidth() / (TILE_SIZE_NORMAL / tileSize), tex.getHeight() / (TILE_SIZE_NORMAL / tileSize), tex);
                    } else if (tex != null && u.getOwner() == Client.instance.game.player2) {
                        g2d.drawImage(tex.getImage(), x * tileSize + tileSize, y * tileSize, -tex.getWidth() / (TILE_SIZE_NORMAL / tileSize), tex.getHeight() / (TILE_SIZE_NORMAL / tileSize), tex);
                    }
                    int lW = tex.getWidth() / (TILE_SIZE_NORMAL / tileSize);
                    int lH = tex.getHeight() / (TILE_SIZE_NORMAL / tileSize) / 4;
                    Texture layer = StandardTexture.get("layer");
                    g2d.drawImage(layer.getImage(), x * tileSize, y * tileSize - tileSize / 2, lW, lH, layer);
                    int lM = lW / 16;
                    int tM = lH / 4;
                    int pHP = 0;
                    try {
                        pHP = u.getClass().getConstructor(Player.class, Integer.TYPE, Integer.TYPE).newInstance(null, 0, 0).getHp();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    g2d.translate(x * tileSize + lM, y * tileSize - tileSize / 2 + tM);
                    {
                        // Relative (Lebensbalken)
                        if (Client.instance.preferences.game.health == 0 || Client.instance.preferences.game.health == 2) {
                            for (int i = 0; i < 10; i++) {
                                if ((i + 1) * 10 <= u.getHp() * 100 / pHP) {
                                    g2d.setColor(Color.GREEN);
                                    g2d.fillRect((lW - lM * 2) / 10 * i, 0, (lW - lM * 2) / 10, (lH - tM * 2));
                                } else {
                                    g2d.setColor(Color.GRAY);
                                    g2d.fillRect((lW - lM * 2) / 10 * i, 0, (lW - lM * 2) / 10, (lH - tM * 2));
                                }
                            }
                        }

                        // Absolute (Zahl)
                        if (Client.instance.preferences.game.health == 1 || Client.instance.preferences.game.health == 2) {
                            Font f = fontTiny.deriveFont(fontTiny.getStyle(), 11 / (TILE_SIZE_NORMAL / tileSize) * u.getSizeX());
                            g2d.setFont(f);
                            if (Client.instance.preferences.game.health == 1)
                                g2d.setColor(Color.WHITE);
                            else if (Client.instance.preferences.game.health == 2)
                                g2d.setColor(Color.BLACK);
                            int gWidth = (int) (f.getStringBounds("" + u.getHp(), frc).getWidth());
                            g2d.drawString("" + u.getHp(), (lW - lM * 2) / 2 - gWidth / 2, lH - tM * 2);
                        }
                    }
                    g2d.translate(-(x * tileSize + lM), -(y * tileSize - tileSize / 2 + tM));

                    g2d.setFont(fontTiny);
                    g2d.setColor(u.getOwner() == null ? Color.WHITE : u.getOwner().getColor().getColor());
                    g2d.fillRect(x * tileSize, y * tileSize, tileSize / 4, tileSize / 4);
                }
            }
        }
        g2d.setColor(Color.WHITE);

        if (fieldHover == null || fieldHover.length != mapWidth || fieldHover[0].length != mapHeight)
            fieldHover = new boolean[mapWidth][mapHeight];
        if (field == null || field.length != mapWidth || field[0].length != mapHeight)
            field = new boolean[mapWidth][mapHeight];
        if (range == null || range.length != mapWidth || range[0].length != mapHeight || !activeAction)
            range = new boolean[mapWidth][mapHeight];

        // Rendering Click and Hover
        for (int x = 0; x < fieldHover.length; x++) {
            for (int y = 0; y < fieldHover[x].length; y++) {
                Building b = buildings[x][y];
                Unit u = units[x][y];
                if (fieldHover[x][y]) {
                    Texture tex = StandardTexture.get("hover");
                    if (b != null)
                        g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize * b.getSizeX(), tileSize * b.getSizeY(), tex);
                    else if (u != null)
                        g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize * u.getSizeX(), tileSize * u.getSizeY(), tex);
                }

                if (field[x][y]) {
                    Texture tex = StandardTexture.get("hover_clicked");
                    if (b != null)
                        g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize * b.getSizeX(), tileSize * b.getSizeY(), tex);
                    else if (u != null)
                        g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize * u.getSizeX(), tileSize * u.getSizeY(), tex);
                }
            }
        }

        // Rendering Menu
        if (guiGameObject != null) {
            BufferedImage img = guiGameObject.render();
            if (activeAction) {
                if (attack) {
                    sRange = ((Unit) selected).getRange();
                    squareAction = true;
                }
                if (move)
                    range = Client.instance.game.gm.getAccessableField(((Unit) selected));
                if (stack)
                    range = Client.instance.game.gm.getAccessableField(((Unit) selected));
                if (build) {
                    sRange = 1;
                    squareAction = true;
                }
                if (spawn) {
                    sRange = 1;
                    squareAction = true;
                }
                if (upgrade)
                    sRange = 1;

                Texture tex = StandardTexture.get("overlay_white");
                for (int x = 0; x < range.length; x++) {
                    for (int y = 0; y < range[x].length; y++) {
                        if (keepSelectedClear(x, y)) continue;
                        if (squareAction && x >= selected.getX() - sRange && x <= selected.getX() + selected.getSizeX() - 1 + sRange && y >= selected.getY() - sRange && y <= selected.getY() + selected.getSizeY() - 1 + sRange && map.get(x, y).isWalkable())
                            g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize, tileSize, tex);
                        else if (!squareAction && range[x][y] && (!move || units[x][y] == null))
                            g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize, tileSize, tex);
                    }
                }

            } else if (img != null) {
                int x = guiDestX;
                int y = guiDestY;
                guiWidth = img.getWidth();
                guiHeight = img.getHeight();
                if (guiDestX + guiWidth > this.width)
                    x = x - (selected.getSizeX()) * tileSize - guiWidth;
                if (guiDestY + guiHeight > this.height)
                    y = y - (selected.getSizeY()) * tileSize - guiHeight;
                if (x < 0)
                    x = guiDestX;
                if (y < 0)
                    y = guiDestY;
                g2d.drawImage(img, x, y, null);
                guiPosX = x;
                guiPosY = y;
            }
        }

        // Rendering Stacking Error(s)
        if (stackErrClass && errObject != null) {
            stackErrHP = false;
            String te = Client.instance.translation.translate("game.stack.wrongClass");
            int cWidth = (int) (fontTiny.getStringBounds(te, frc).getWidth());
            g2d.setFont(fontTiny);
            g2d.setColor(errorColor);
            g2d.drawString(te, (errObject.getX() * tileSize + tileSize / 2) - cWidth / 2, errObject.getY() * tileSize);
        }
        g2d.setColor(Color.WHITE);

        if (stackErrHP && errObject != null) {
            String te = Client.instance.translation.translate("game.stack.hpErr");
            int cWidth = (int) (fontTiny.getStringBounds(te, frc).getWidth());
            g2d.setFont(fontTiny);
            g2d.setColor(errorColor);
            g2d.drawString(te, (errObject.getX() * tileSize + tileSize / 2) - cWidth / 2, errObject.getY() * tileSize);
        }
        g2d.setColor(Color.WHITE);

        g2d.setTransform(tx);

        if (scrollX > (int) (mapWidth * tileSize * zoom - this.width))
            scrollX = (int) (mapWidth * tileSize * zoom - this.width);
        if (scrollY > (int) (mapHeight * tileSize * zoom - this.height))
            scrollY = (int) (mapHeight * tileSize * zoom - this.height);

        int[] x = new int[]{width / 2, width - tileSize / 2, width / 2, tileSize / 2};
        int[] y = new int[]{tileSize / 2, height / 2, height - tileSize / 2, height / 2};
        Texture tex = StandardTexture.get("arrow_" + Client.instance.preferences.game.arrow);
        for (int i = 0; i < 4; i++) {
            if (i == 0 && scrollY <= 0)
                continue;
            if (i == 1 && scrollX >= (int) (mapWidth * tileSize * zoom - width))
                continue;
            if (i == 2 && scrollY >= (int) (mapHeight * tileSize * zoom - height))
                continue;
            if (i == 3 && scrollX <= 0)
                continue;
            g2d.translate(x[i], y[i]);
            g2d.rotate(Math.toRadians(90 * i));
            g2d.drawImage(tex.getImage(), -tileSize / 2, -tileSize / 2, tex);
            g2d.setTransform(tx);
        }

        g2d.dispose();
        g2do.drawImage(cache, 0, 0, null);

        // Rendering Round, Phase and activePlayer
        int p = Client.instance.game.phase;
        int r = Client.instance.game.round;
        String phase = Client.instance.translation.translate("game.phase." + (p % 3 == 0 ? "build" : p % 3 == 1 ? "move" : p % 3 == 2 ? "attack" : p));
        String round = Client.instance.translation.translate("game.round") + " " + (r + 1);
        String player = Client.instance.translation.translate("game.player") + " " + (Client.instance.game.activePlayer == Client.instance.game.player1 ? 1 : 2);
        g2do.setFont(fontPlayer);
        g2do.setColor(Color.WHITE);
        g2do.drawString(round + ": " + phase, 20, 50);
        g2do.setColor(Client.instance.game.activePlayer.getColor().getColor());
        g2do.drawString(player, 20, 105);

        // Rendering Gold and phaseTimer
        g2do.setFont(fontGold);
        g2do.setColor(goldColor);
        String gold = Client.instance.game.GoldDif == 0 ? "" + Client.instance.game.activePlayer.getGold() : (Client.instance.game.activePlayer.getGold() - Client.instance.game.GoldDif) + " + " + Client.instance.game.GoldDif;
        g2do.drawString(Client.instance.translation.translate("game.gold") + ": " + gold, 20, 150);
        g2do.setFont(fontGold);
        if (leftTime > 15)
            g2do.setColor(Color.WHITE);
        else if (leftTime % 2 == 0)
            g2do.setColor(Color.RED);
        else
            g2do.setColor(Color.WHITE);
        g2do.drawString(leftTime / 60 + ":" + ("00" + leftTime % 60).substring(Integer.toString(leftTime % 60).length()), 20, 200);

        int botMargin = height / 32;
        int buttonWidth = width - width / 4;
        int buttonHeight = height / 10;
        int buttonSpacing = buttonHeight / 4;
        int rightMargin = width / 32;
        int buttonWidthSmall = (buttonWidth - buttonSpacing) / 2;
        int buttonWidthVerySmall = (buttonWidthSmall - buttonSpacing) / 2;
        gameStateButton.setX(width - rightMargin - buttonWidthVerySmall);
        gameStateButton.setY(height - botMargin - buttonHeight);
        gameStateButton.setWidth(buttonWidthVerySmall);
        gameStateButton.setHeight(buttonHeight);
        if (tutorialPhase >= 0)
            gameStateButton.setText("tutorial.goOn");
        else
            gameStateButton.setText(Client.instance.game.gm.phaseButtonText());
        if (tutorialPhase == tutorialPhases)
            gameStateButton.setText("gui.back.mainmenu");
        if (tutorialPhase >= 4 && tutorialPhase <= 8)
            gameStateButton.setEnabled(false);
        if (tutorialPhase == 6)
            gameStateButton.setEnabled(true);
        super.render(g2do, width, height);

        // Rendering Tutorial
        g2do.setFont(fontPlayer);
        g2do.setColor(Color.WHITE);
        String toText = "";
        String tText = "";
        int toX = 0;
        int toY = 0;
        if (tutorialPhase == 0) {
            toText = round + ": " + phase;
            tText = Client.instance.translation.translate("tutorial.round");
            toX = 20;
            toY = 50;
        }
        if (tutorialPhase == 1) {
            toText = player;
            tText = Client.instance.translation.translate("tutorial.player");
            toX = 20;
            toY = 105;
        }
        if (tutorialPhase == 2) {
            toText = Client.instance.translation.translate("game.gold") + ": " + gold;
            tText = Client.instance.translation.translate("tutorial.gold");
            toX = 20;
            toY = 150;
        }
        if (tutorialPhase == 3) {
            toText = leftTime / 60 + ":" + ("00" + leftTime % 60).substring(Integer.toString(leftTime % 60).length());
            tText = Client.instance.translation.translate("tutorial.timer");
            toX = 20;
            toY = 200;
        }
        if (tutorialPhase == 4) {
            int tileSize = (int) (this.tileSize * zoom);
            tText = Client.instance.translation.translate("tutorial.gameobject");
            toX = (buildings[1][mapHeight / 2 - 1].getX() + buildings[1][mapHeight / 2 - 1].getSizeX()) * tileSize - scrollX;
            toY = (buildings[1][mapHeight / 2 - 1].getY() + buildings[1][mapHeight / 2 - 1].getSizeY()) * tileSize - scrollY;
        }
        if (tutorialPhase == 5) {
            tText = Client.instance.translation.translate("tutorial.action", Client.instance.translation.translate("game.option.spawn"));
            toX = (int) ((guiPosX + 500) * zoom) - scrollX;
            toY = (int) ((guiPosY + 50) * zoom) - scrollY;
        }
        if (tutorialPhase == 6) {
            int tileSize = (int) (this.tileSize * zoom);
            tText = Client.instance.translation.translate("tutorial.win");
            toX = (buildings[mapWidth - 3][mapHeight / 2 - 1].getX()) * tileSize - scrollX;
            toY = (buildings[mapWidth - 3][mapHeight / 2 - 1].getY()) * tileSize - scrollY;
        }
        if (tutorialPhase == 7) {
            Client.instance.game.phase = 1;
            int tileSize = (int) (this.tileSize * zoom);
            tText = Client.instance.translation.translate("tutorial.build", Client.instance.translation.translate("game.phase.move"), Client.instance.translation.translate("game.option.build"));
            toX = 6 * tileSize - scrollX;
            toY = 19 * tileSize - scrollY;
        }
        if (tutorialPhase == 9) {
            tText = Client.instance.translation.translate("tutorial.button", Client.instance.translation.translate("game.phase.build"), Client.instance.translation.translate("game.phase.move"), Client.instance.translation.translate("game.phase.attack"));
            toX = gameStateButton.getX();
            toY = gameStateButton.getY();
        }
        if (toX != 0 && toY != 0 && !Objects.equals(tText, "")) {
            int tSize = mapWidth * this.tileSize / 4;
            int tt = ((int) ((tSize / 2) * Math.sin(Math.PI / 12)));
            int toWidth = (int) (fontPlayer.getStringBounds(toText, frc).getWidth());
            int tWidth;
            int tHeight = (int) (fontPlayer.getStringBounds(tText, frc).getHeight());
            int sW = 0;
            int bW = 0;
            int tPosX = 0;
            int tPosY = 0;
            boolean rightSide = false;
            if (toX < width / 2 && toY < height / 2) {
                sW = -11;
                bW = -2;
                tPosX = tSize / 2 + toX + toWidth;
                tPosY = tt + toY;
                rightSide = false;
            }
            if (toX < width / 2 && toY > height / 2) {
                sW = 11;
                bW = 2;
                tPosX = tSize / 2 + toX + toWidth;
                tPosY = -tt + toY;
                rightSide = false;
            }
            if (toX > width / 2 && toY < height / 2) {
                sW = -180 + 11 + 2;
                bW = -2;
                tPosX = -(tSize / 2) + toX - toWidth;
                tPosY = tt + toY;
                rightSide = true;
            }
            if (toX > width / 2 && toY > height / 2) {
                sW = 180 - 11 - 2;
                bW = 2;
                tPosX = -(tSize / 2) + toX - toWidth;
                tPosY = -tt + toY;
                rightSide = true;
            }
            g2do.fillArc(-tSize / 2 + toX + toWidth, -tSize / 2 + toY, tSize, tSize, sW, bW);
            String[] tTextA = tText.split(" ");
            String inrow = "";
            List<String> rowText = new ArrayList<>();
            int row = 0;
            int sRow = 0;
            BufferedImage tTextImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
            Graphics2D g2t = tTextImage.createGraphics();
            g2t.setFont(fontPlayer);
            g2t.setColor(Color.BLACK);
            int timesOutOfScreen = 0;
            int lNotFitting = -1;
            for (int i = 0; i < tTextA.length; i++) {
                if (timesOutOfScreen > 15)
                    break;

                tWidth = (int) (fontPlayer.getStringBounds(inrow + tTextA[i], frc).getWidth());
                tHeight = (int) (fontPlayer.getStringBounds(inrow + tTextA[i], frc).getHeight());
                if (rightSide) {
                    if (i == 0 && tPosX - tWidth < 0)
                        break;
                    if (tPosX - tWidth > 0)
                        inrow += tTextA[i] + " ";
                    else {
                        if (i == lNotFitting) {
                            inrow = tTextA[i];
                            String a = inrow.substring(0, inrow.length() / 2) + "-";
                            String b = inrow.substring(inrow.length() / 2);
                            String[] Backup = tTextA.clone();
                            tTextA = new String[Backup.length + 1];
                            for (int j = 0; j < tTextA.length; j++) {
                                if (j < i)
                                    tTextA[j] = Backup[j];
                                else if (j == i) {
                                    tTextA[i] = a;
                                    tTextA[i + 1] = b;
                                    j++;
                                } else
                                    tTextA[j] = Backup[j - 1];
                            }
                            lNotFitting = -1;
                            row = 0;
                            inrow = "";
                            rowText.clear();
                            i = -1;
                        } else {
                            rowText.add(inrow);
                            inrow = "";
                            lNotFitting = i;
                            i--;
                            row++;
                        }
                    }
                    if (i == tTextA.length - 1) {
                        if (tPosY + tHeight / 2 + (tHeight * row) > height) {
                            sRow = 0;
                            while (tPosY + tHeight / 2 + (tHeight * (row - sRow)) > height) {
                                sRow++;
                                if (sRow > 50)
                                    break;
                            }
                            row = -sRow;
                            inrow = "";
                            rowText.clear();
                            i = -1;
                            timesOutOfScreen++;
                        } else
                            rowText.add(inrow);
                    }
                } else if (!rightSide) {
                    if (i == 0 && tPosX + tWidth > width)
                        break;
                    if (tPosX + tWidth <= width && !rightSide)
                        inrow += tTextA[i] + " ";
                    else {
                        if (i == lNotFitting) {
                            inrow = tTextA[i];
                            String a = inrow.substring(0, inrow.length() / 2) + "-";
                            String b = inrow.substring(inrow.length() / 2);
                            String[] Backup = tTextA.clone();
                            tTextA = new String[Backup.length + 1];
                            for (int j = 0; j < tTextA.length; j++) {
                                if (j < i)
                                    tTextA[j] = Backup[j];
                                else if (j == i) {
                                    tTextA[i] = a;
                                    tTextA[i + 1] = b;
                                    j++;
                                } else
                                    tTextA[j] = Backup[j - 1];
                            }
                            lNotFitting = -1;
                            row = 0;
                            inrow = "";
                            rowText.clear();
                            i = -1;
                        } else {
                            lNotFitting = i;
                            rowText.add(inrow);
                            inrow = "";
                            i--;
                            row++;
                        }
                    }
                    if (i == tTextA.length - 1) {
                        if (tPosY + (tHeight * row) > height) {
                            sRow = 1;
                            while (tPosY + (tHeight * (row - sRow)) > height) {
                                sRow++;
                                if (sRow > 50)
                                    break;
                            }
                            row = -sRow;
                            inrow = "";
                            rowText.clear();
                            i = -1;
                            timesOutOfScreen++;
                        } else
                            rowText.add(inrow);
                    }
                }
            }

            for (int i = 0; i < rowText.size(); i++) {
                int inWidth = (int) (fontPlayer.getStringBounds(rowText.get(i), frc).getWidth());
                if (rightSide)
                    g2t.drawString(rowText.get(i), tPosX - inWidth, tPosY + tHeight / 2 + (tHeight * (i - sRow)));
                else
                    g2t.drawString(rowText.get(i), tPosX, tPosY + (tHeight * (i - sRow)));
            }
            g2t.dispose();
            g2do.drawImage(tTextImage, 0, 0, width, height, null);
        }

        if (Client.instance.game.gm.won) {
            Client.instance.setGuiScreen(new GuiEndMenu());
            firstClick = true;
        }
    }

    public BufferedImage getMapBackground() {
        return cache;
    }

    private void clearOptions() {
        attack = false;
        move = false;
        stack = false;
        build = false;
        spawn = false;
        upgrade = false;
        activeAction = false;
        squareAction = false;
    }

    private void removeGui() {
        selected = null;
        guiGameObject = null;
        this.clearOptions();
        guiDestX = -1;
        guiDestY = -1;
        guiPosX = -1;
        guiPosY = -1;
        guiWidth = -1;
        guiHeight = -1;
        sRange = -1;
        if (mapCache != null)
            field = new boolean[mapCache.getWidth()][mapCache.getHeight()];
    }

    private boolean keepSelectedClear(int x, int y) {
        List<Integer> xC = new ArrayList<>();
        List<Integer> yC = new ArrayList<>();
        xC.clear();
        yC.clear();
        for (int a = 0; a < selected.getSizeX(); a++)
            xC.add(selected.getX() + a);
        for (int b = 0; b < selected.getSizeY(); b++)
            yC.add(selected.getY() + b);
        return xC.contains(x) && yC.contains(y);
    }

    private void guiAction(int x, int y) {
        Building[][] buildings = Client.instance.game.buildings;
        Unit[][] units = Client.instance.game.units;
        TileMap map = Client.instance.game.map;

        Unit u = units[x][y];
        Building b = null;
        boolean n = false;
        for (int x1 = x; x1 > x1 - 6 && x1 >= 0; x1--) {
            if (n)
                break;
            for (int y1 = y; y1 > y1 - 6 && y1 >= 0; y1--) {
                Building c = buildings[x1][y1];
                if (c != null && c.getSizeX() + x1 > x && c.getSizeY() + y1 > y) {
                    b = c;
                    n = true;
                    break;
                }
            }
        }

        // Attacking
        if (attack && u != null && u.getOwner() != selected.getOwner()) {
            Client.instance.game.gm.combat((Unit) selected, u, 0);
            Client.instance.game.units[selected.getX()][selected.getY()].setAttacked(true);
            if (selected instanceof Archer && (Math.abs(selected.getX() - u.getX()) > 1 || Math.abs(selected.getY() - u.getY()) > 1))
                Sounds.play("archer_shot");
            else
                Sounds.play("battle");
            this.removeGui();
        }

        if (attack && b != null && b.getOwner() != selected.getOwner()) {
            Client.instance.game.gm.pillage((Unit) selected, b);
            Client.instance.game.units[selected.getX()][selected.getY()].setAttacked(true);
            this.removeGui();
        }

        // Moving
        if (move && u == null && b == null) {
            Client.instance.game.gm.move((Unit) selected, x, y);
            this.removeGui();
        }

        // Stacking
        if (stack && u != null && u.getOwner() == selected.getOwner()) {
            if (!(u.getClass().equals(selected.getClass()))) {
                stackErrClass = true;
                errObject = u;
            } else if (u.getHp() + selected.getHp() > 120) {
                stackErrHP = true;
                errObject = u;
            } else {
                Client.instance.game.gm.stack((Unit) selected, units[x][y]);
                this.removeGui();
            }

        }

        // Building
        if (build && u == null && b == null && map.get(x, y).isWalkable()) {
            try {
                Building bb = buildClass.getConstructor(Player.class, Integer.TYPE, Integer.TYPE).newInstance(selected.getOwner(), x, y);
                Client.instance.game.gm.buildBuilding(bb.getOwner(), bb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.removeGui();
            if (tutorialPhase == 8)
                tutorialPhase++;
        }

        // Spawning
        if (spawn && u == null && b == null && map.get(x, y).isWalkable()) {
            try {
                Unit uu = spawnClass.getConstructor(Player.class, Integer.TYPE, Integer.TYPE).newInstance(selected.getOwner(), x, y);
                Client.instance.game.gm.create(uu.getOwner(), uu, (Building) selected);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.removeGui();
            if (tutorialPhase == 5)
                tutorialPhase++;
        }

        // Upgrade
        if (upgrade) {
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        if (mapCache == null)
            return;
        int mapWidth = mapCache.getWidth();
        int mapHeight = mapCache.getHeight();
        Building[][] buildings = Client.instance.game.buildings;
        Unit[][] units = Client.instance.game.units;

        int x = (int) (((e.getX() + scrollX) / zoom) / tileSize);
        int y = (int) (((e.getY() + scrollY) / zoom) / tileSize);

        if (x < 0 || x >= field.length || y < 0 || y >= field[x].length)
            return;
        // Clicking on Button
        int bx = gameStateButton.getX();
        int by = gameStateButton.getY();
        if (e.getX() >= bx && e.getX() <= bx + gameStateButton.getWidth() && e.getY() >= by && e.getY() <= by + gameStateButton.getHeight())
            return;

        // Clicking on guiGameObject
        int gx = (int) ((e.getX() + scrollX) / zoom);
        int gy = (int) ((e.getY() + scrollY) / zoom);
        if (activeAction) {
            if (keepSelectedClear(x, y))
                return;
            else if (squareAction && x >= selected.getX() - sRange && x <= selected.getX() + selected.getSizeX() - 1 + sRange && y >= selected.getY() - sRange && y <= selected.getY() + selected.getSizeY() - 1 + sRange) {
                this.guiAction(x, y);
                return;
            } else if (!squareAction && range[x][y] && (!move || units[x][y] == null)) {
                this.guiAction(x, y);
                return;
            }
            this.clearOptions();
            return;
        }
        if (gx >= guiPosX && gx <= guiPosX + guiWidth && gy >= guiPosY && gy <= guiPosY + guiHeight) {
            guiGameObject.mouseClicked(new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), gx - guiPosX, gy - guiPosY, e.getClickCount(), e.isPopupTrigger(), e.getButton()));
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1 && !firstClick) {

            Client.instance.game.GoldDif = 0;
            field = new boolean[mapWidth][mapHeight];
            // Clicking on Unit
            if (units[x][y] != null && units[x][y].getOwner() == Client.instance.game.activePlayer) {
                if (tutorialPhase == 7 && x == 5 && y == 18)
                    tutorialPhase++;
                field[x][y] = true;
                Unit u = units[x][y];
                selected = u;
                actionPerformed(new ActionEvent(selected, ActionEvent.ACTION_PERFORMED, null));
                guiGameObject = new GuiGameMenu(selected);
                guiDestX = (selected.getX() + u.getSizeX()) * tileSize;
                guiDestY = (selected.getY() + u.getSizeY()) * tileSize;
                return;
            }

            // Clicking on Building
            for (int x1 = x; x1 > x1 - 6 && x1 >= 0; x1--) {
                for (int y1 = y; y1 > y1 - 6 && y1 >= 0; y1--) {
                    Building b = buildings[x1][y1];
                    if (b != null && b.getSizeX() + x1 > x && b.getSizeY() + y1 > y && b.getOwner() == Client.instance.game.activePlayer) {
                        if (tutorialPhase == 4 && b.getX() == 1 && b.getY() == buildings[x].length / 2 - 1)
                            tutorialPhase++;
                        field[x1][y1] = true;
                        selected = b;
                        actionPerformed(new ActionEvent(selected, ActionEvent.ACTION_PERFORMED, null));
                        guiGameObject = new GuiGameMenu(selected);
                        guiDestX = (selected.getX() + b.getSizeX()) * tileSize;
                        guiDestY = (selected.getY() + b.getSizeY()) * tileSize;
                        return;
                    }
                }
            }

            this.removeGui();
        }
        firstClick = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        if (mapCache == null)
            return;
        int mapWidth = mapCache.getWidth();
        int mapHeight = mapCache.getHeight();

        int x = (int) (((e.getX() + scrollX) / zoom) / tileSize);
        int y = (int) (((e.getY() + scrollY) / zoom) / tileSize);
        Building[][] buildings = Client.instance.game.buildings;
        Unit[][] units = Client.instance.game.units;

        if (x < 0 || x >= fieldHover.length || y < 0 || y >= fieldHover[x].length)
            return;

        // Hovering over Button
        int bx = gameStateButton.getX();
        int by = gameStateButton.getY();
        if (e.getX() >= bx && e.getX() <= bx + gameStateButton.getWidth() && e.getY() >= by && e.getY() <= by + gameStateButton.getHeight())
            return;

        // Hovering over guiGameObject
        int gx = (int) ((e.getX() + scrollX) / zoom);
        int gy = (int) ((e.getY() + scrollY) / zoom);
        if (activeAction) {
            if (!squareAction || x < selected.getX() - sRange || x > selected.getX() + selected.getSizeX() - 1 + sRange || y < selected.getY() - sRange || y > selected.getY() + selected.getSizeY() - 1 + sRange) {
                if (squareAction || !range[x][y] || (move && units[x][y] != null)) {
                    stackErrClass = false;
                    stackErrHP = false;
                    errObject = null;
                }
            }
        }
        if (gx >= guiPosX && gx <= guiPosX + guiWidth && gy >= guiPosY && gy <= guiPosY + guiHeight && !activeAction) {
            guiGameObject.mouseMoved(new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), gx - guiPosX, gy - guiPosY, e.getClickCount(), e.isPopupTrigger()));
            return;
        }

        fieldHover = new boolean[mapWidth][mapHeight];

        // Hovering over Unit
        if (units[x][y] != null) {
            fieldHover[x][y] = true;
            return;
        }

        // Hovering over Building
        for (int x1 = x; x1 > x1 - 6 && x1 >= 0; x1--) {
            for (int y1 = y; y1 > y1 - 6 && y1 >= 0; y1--) {
                Building b = buildings[x1][y1];
                if (b != null && b.getSizeX() + x1 > x && b.getSizeY() + y1 > y) {
                    fieldHover[x1][y1] = true;
                    return;
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (e.getButton() == MouseEvent.BUTTON3) {
            diffX = e.getX();
            diffY = e.getY();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
            scrollX -= e.getX() - diffX;
            scrollY -= e.getY() - diffY;
            if (scrollX < 0)
                scrollX = 0;
            if (scrollY < 0)
                scrollY = 0;
            int mapWidth = mapCache.getWidth();
            int mapHeight = mapCache.getHeight();
            if (scrollX > (int) (mapWidth * tileSize * zoom - width))
                scrollX = (int) (mapWidth * tileSize * zoom - width);
            if (scrollY > (int) (mapHeight * tileSize * zoom - height))
                scrollY = (int) (mapHeight * tileSize * zoom - height);
            diffX = e.getX();
            diffY = e.getY();
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getID() == ActionEvent.ACTION_PERFORMED) {
            // Buttons
            if (e.getSource() instanceof GuiButton) {
                GuiButton button = (GuiButton) e.getSource();
                if (button == gameStateButton) {
                    this.removeGui();
                    if (tutorialPhase >= 0)
                        tutorialPhase++;
                    else
                        Client.instance.game.gm.nextPhase();
                    if (tutorialPhase == tutorialPhases + 1)
                        Client.instance.setGuiScreen(new GuiMainMenu());
                    leftTime = phaseTime;
                }
            }

            // GameObjects
            if (e.getSource() instanceof GameObject) {
                Client.instance.game.gm.actionPerformed(e);
            }

            // Keys
            if (e.getSource() instanceof KeyEvent) {
                int key = ((KeyEvent) e.getSource()).getKeyCode();
                int modifiers = ((KeyEvent) e.getSource()).getModifiers();
                if (key == KeyEvent.VK_LEFT)
                    scrollX -= tileSize / 4;
                else if (key == KeyEvent.VK_RIGHT)
                    scrollX += tileSize / 4;
                else if (key == KeyEvent.VK_UP)
                    scrollY -= tileSize / 4;
                else if (key == KeyEvent.VK_DOWN)
                    scrollY += tileSize / 4;
                else if (key == KeyEvent.VK_ESCAPE) {
                    Client.instance.setGuiScreen(new GuiPauseMenu());
                    firstClick = true;
                    if (healthUpToDate) {
                        Client.instance.preferences.game.health = healthOptionRAM;
                        healthUpToDate = false;
                    }
                } else if (key == Client.instance.preferences.game.absoluteKey.getKeyCode() && modifiers == Client.instance.preferences.game.absoluteKey.getModifiers()) {
                    if (!healthUpToDate)
                        healthOptionRAM = Client.instance.preferences.game.health;
                    healthUpToDate = true;
                    Client.instance.preferences.game.health = 1;
                } else if (key == Client.instance.preferences.game.collapseKey.getKeyCode() && modifiers == Client.instance.preferences.game.collapseKey.getModifiers()) {
                    this.removeGui();
                    firstClick = false;
                } else if (key == Client.instance.preferences.game.fullscreenKey.getKeyCode() && modifiers == Client.instance.preferences.game.fullscreenKey.getModifiers()) {
                    Client.instance.preferences.video.fullscreen = !Client.instance.preferences.video.fullscreen;
                    Client.instance.setFullscreen(Client.instance.preferences.video.fullscreen);
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        if (scrollX < 0)
            scrollX = 0;
        if (scrollY < 0)
            scrollY = 0;
        int mapWidth = mapCache.getWidth();
        int mapHeight = mapCache.getHeight();
        if (scrollX > (int) (mapWidth * tileSize * zoom - width))
            scrollX = (int) (mapWidth * tileSize * zoom - width);
        if (scrollY > (int) (mapHeight * tileSize * zoom - height))
            scrollY = (int) (mapHeight * tileSize * zoom - height);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);

        try {
            if (e.getKeyCode() == Client.instance.preferences.game.absoluteKey.getKeyCode() && e.getModifiers() == Client.instance.preferences.game.absoluteKey.getModifiers()) {
                Client.instance.preferences.game.health = healthOptionRAM;
                healthUpToDate = false;
            }
        } catch (NullPointerException ne) {
            ne.printStackTrace();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double z = zoom;

        zoom *= Math.pow(1.1, (Client.instance.preferences.game.invertZoom ? 1 : -1) * e.getWheelRotation());
        if (zoom < 0.2)
            zoom = 0.2;
        if (zoom > 5)
            zoom = 5;
        double d = zoom - z;
        scrollX += d * e.getX() * 2;
        scrollY += d * e.getY() * 2;
        if (scrollX < 0)
            scrollX = 0;
        if (scrollY < 0)
            scrollY = 0;
        int mapWidth = mapCache.getWidth();
        int mapHeight = mapCache.getHeight();
        if (scrollX > (int) (mapWidth * tileSize * zoom - width) && scrollX > 0)
            scrollX = (int) (mapWidth * tileSize * zoom - width);
        if (scrollY > (int) (mapHeight * tileSize * zoom - height) && scrollY > 0)
            scrollY = (int) (mapHeight * tileSize * zoom - height);
    }
}
