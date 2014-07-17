package de.gymdon.inf1315.game.render;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.event.MouseInputListener;

import de.gymdon.inf1315.game.*;
import de.gymdon.inf1315.game.client.*;
import de.gymdon.inf1315.game.render.gui.GuiGameMenu;
import de.gymdon.inf1315.game.render.gui.GuiButton;
import de.gymdon.inf1315.game.render.gui.GuiPauseMenu;
import de.gymdon.inf1315.game.render.gui.GuiScreen;

public class MapRenderer extends GuiScreen implements Renderable, ActionListener, MouseInputListener, MouseWheelListener, KeyListener {

    private GuiButton gameStateButton = new GuiButton(this, 0, 20, 20, null);
    public static final int TILE_SIZE_SMALL = 32;
    public static final int TILE_SIZE_NORMAL = 64;
    public static final int TILE_SIZE_BIG = 128;
    public int tileSize = TILE_SIZE_NORMAL;
    public double zoom = 0.1;
    private BufferedImage map = null;
    private BufferedImage cache = null;
    private Tile[][] mapCache = null;
    public boolean firstClick = false;
    private int scrollX = 0;
    private int scrollY = 0;
    private int diffX = 0;
    private int diffY = 0;
    private GameObject selected;
    private boolean[][] fieldHover;
    private boolean[][] field;
    private GuiGameMenu guiGameObject;
    private int guiPosX;
    private int guiPosY;
    private int guiDebugX;
    private int guiDebugY;
    private int guiWidth;
    private int guiHeight;
    private int speed;
    private boolean squareAction = false;
    private boolean activeAction = false;
    private boolean[][] range;
    private int sRange;
    public boolean attack = false;
    public boolean move = false;
    public boolean stack = false;
    public boolean spawn = false;
    public boolean upgrade = false;
    public Class<? extends Unit> spawnClass;

    public MapRenderer() {
	controlList.add(gameStateButton);
    }

    @Override
    public void render(Graphics2D g2do, int width, int height) {
	Client.instance.game.gm.run();
	cache = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2d = cache.createGraphics();

	Tile[][] map = Client.instance.game.map;
	int mapWidth = map.length;
	int mapHeight = map[0].length;
	double w = (mapWidth * tileSize * zoom);
	if (w < width)
	    zoom /= w / width;
	double h = (mapHeight * tileSize * zoom);
	if (h < height)
	    zoom /= h / height;
	if (attack || move || stack || spawn || upgrade)
	    activeAction = true;
	else {
	    activeAction = false;
	    squareAction = false;
	    sRange = 0;
	    range = new boolean[map.length][map[0].length];
	}
	AffineTransform tx = g2d.getTransform();
	g2d.translate(-scrollX, -scrollY);
	g2d.scale(zoom, zoom);

	// Rendering Map
	if (this.map == null || !map.equals(mapCache)) {
	    this.map = new BufferedImage(map.length * tileSize, map[0].length * tileSize, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = this.map.createGraphics();
	    for (int x = 0; x < map.length; x++) {
		for (int y = 0; y < map[x].length; y++) {
		    Tile tile = map[x][y];
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
		}
	    }
	}

	// Rendering Units
	Unit[][] units = Client.instance.game.units;
	for (int x = 0; x < units.length; x++) {
	    for (int y = 0; y < units[x].length; y++) {
		Unit u = units[x][y];
		if (u != null) {
		    Texture tex = UnitRenderMap.getTexture(u);
		    if (tex != null)
			g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tex.getWidth() / (TILE_SIZE_NORMAL / tileSize), tex.getHeight() / (TILE_SIZE_NORMAL / tileSize), tex);
		    g2d.drawString(Integer.toString(u.getHP()), x * tileSize, y * tileSize - tileSize/4);
		}
	    }
	}

	if (fieldHover == null || fieldHover.length != mapWidth || fieldHover[0].length != mapHeight)
	    fieldHover = new boolean[mapWidth][mapHeight];
	if (field == null || field.length != mapWidth || field[0].length != mapHeight)
	    field = new boolean[mapWidth][mapHeight];

	// Rendering Click and Hover
	for (int x = 0; x < fieldHover.length; x++) {
	    for (int y = 0; y < fieldHover[x].length; y++) {
		if (fieldHover[x][y]) {
		    Texture tex = StandardTexture.get("hover");
		    Building b = buildings[x][y];
		    if (b != null)
			g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize * b.getSizeX(), tileSize * b.getSizeY(), tex);
		    else
			g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize, tileSize, tex);
		}

		if (field[x][y]) {
		    Texture tex = StandardTexture.get("hover_clicked");
		    Building b = buildings[x][y];
		    if (b != null) {
			g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize * b.getSizeX(), tileSize * b.getSizeY(), tex);
		    } else {
			g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize, tileSize, tex);
		    }
		}
	    }
	}

	// Rendering Menu
	if (guiGameObject != null) {
	    guiGameObject.keepSizesUpToDate();
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
		if (spawn) {
		    sRange = ((Building) selected).getSizeX();
		    squareAction = true;
		}
		if (upgrade)
		    sRange = ((Building) selected).getSizeX();

		Texture tex = StandardTexture.get("overlay_white");
		for (int x = 0; x < range.length; x++) {
		    for (int y = 0; y < range[x].length; y++) {
			if (x == selected.x && y == selected.y) {
			} else if (squareAction && Math.abs(x - selected.x) <= sRange && Math.abs(y - selected.y) <= sRange)
			    g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize, tileSize, tex);
			else if (range[x][y])
			    g2d.drawImage(tex.getImage(), x * tileSize, y * tileSize, tileSize, tileSize, tex);
		    }
		}

	    } else if (img != null) {
		int x = guiPosX;
		int y = guiPosY;
		guiWidth = img.getWidth();
		guiHeight = img.getHeight();
		if (guiPosX + guiWidth > this.width)
		    x = (x - tileSize) - guiWidth;
		if (guiPosY + guiHeight > this.height)
		    y = (y - tileSize) - guiHeight;
		if (x < 0)
		    x = guiPosX;
		if (y < 0)
		    y = guiPosY;
		g2d.drawImage(img, x, y, null);
		guiDebugX = x;
		guiDebugY = y;
	    }
	}

	g2d.setTransform(tx);

	if (scrollX > (int) (mapWidth * tileSize * zoom - this.width))
	    scrollX = (int) (mapWidth * tileSize * zoom - this.width);
	if (scrollY > (int) (mapHeight * tileSize * zoom - this.height))
	    scrollY = (int) (mapHeight * tileSize * zoom - this.height);

	int[] x = new int[] { width / 2, width - tileSize / 2, width / 2, tileSize / 2 };
	int[] y = new int[] { tileSize / 2, height / 2, height - tileSize / 2, height / 2 };
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

	// Rendering Round and Phase
	int p = Client.instance.game.phase;
	int r = Client.instance.game.round;
	String phase = Client.instance.translation.translate("game.phase." + (p % 3 == 0 ? "build" : p % 3 == 1 ? "move" : p % 3 == 2 ? "attack" : "" + p), new Object[0]);
	String round = Client.instance.translation.translate("game.round", new Object[0]) + " " + (r + 1);
	g2do.setFont(Client.instance.translation.font.deriveFont(50F));
	g2do.setColor(new Color(0xFFFFFF));
	g2do.drawString(round + ": " + phase, 20, 50);

	// Rendering Gold
	g2do.setFont(Client.instance.translation.font.deriveFont(35F));
	g2do.setColor(new Color(0xEDE275));
	g2do.drawString(Client.instance.translation.translate("game.gold", new Object[0]) + ": " + "\u221E", 20, 100);

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
	gameStateButton.setText(Client.instance.game.gm.phaseButtonText());
	super.render(g2do, width, height);
    }

    public BufferedImage getMapBackground() {
	return cache;
    }

    private void clearOptions() {
	attack = false;
	move = false;
	stack = false;
	spawn = false;
	upgrade = false;
    }

    private void removeGui() {
	selected = null;
	guiGameObject = null;
	this.clearOptions();
	guiPosX = -1;
	guiPosY = -1;
	guiDebugX = -1;
	guiDebugY = -1;
	guiWidth = -1;
	guiHeight = -1;
	if (mapCache != null)
	    field = new boolean[mapCache.length][mapCache[0].length];
    }

    private void guiAction(int x, int y) {
	Building[][] buildings = Client.instance.game.buildings;
	Unit[][] units = Client.instance.game.units;

	// Attacking
	if (attack && units[x][y] != null) {
	    Client.instance.game.gm.combat((Unit) selected, units[x][y], 0);
	    this.removeGui();
	}
	
	if (attack && buildings[x][y] != null) {
	    // Client.instance.game.gm.combat((Unit) selected, buildings[x][y], 0);
	    System.err.println("Why can't i fight buildings? I need METHODS for that...");
	    this.removeGui();
	}

	// Moving
	if (move && units[x][y] == null) {
	    Client.instance.game.gm.move((Unit) selected, x, y);
	    this.removeGui();
	}

	// Stacking
	if (stack && units[x][y] != null) {
	    Client.instance.game.gm.stack((Unit) selected, units[x][y]);
	    this.removeGui();
	}

	// Spawning
	if (spawn && units[x][y] == null) {
	    try {
		Unit u = spawnClass.getConstructor(Player.class, Integer.TYPE, Integer.TYPE).newInstance(null, x, y);
		Client.instance.game.gm.create(u.owner, u, (Building) selected);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    this.removeGui();
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
	int mapWidth = mapCache.length;
	int mapHeight = mapCache[0].length;
	Building[][] buildings = Client.instance.game.buildings;
	Unit[][] units = Client.instance.game.units;

	int x = (int) (((e.getX() + scrollX) / zoom) / tileSize);
	int y = (int) (((e.getY() + scrollY) / zoom) / tileSize);

	if (x < 0 || x >= field.length || y < 0 || y >= field[x].length)
	    return;
	// Clicking on Button
	int bx = gameStateButton.getX();
	int by = gameStateButton.getY();
	if (e.getX() >= bx && e.getX() <= bx + gameStateButton.getWidth() && e.getY() >= by && e.getY() <= by + gameStateButton.getHeight()) {
	    this.removeGui();
	    return;
	}

	// Clicking on guiGameObject
	int gx = (int) ((e.getX() + scrollX) / zoom);
	int gy = (int) ((e.getY() + scrollY) / zoom);
	if (activeAction) {

	    if (x == selected.x && y == selected.y)
		return;
	    else if (squareAction && Math.abs(x - selected.x) <= sRange && Math.abs(y - selected.y) <= sRange) {
		this.guiAction(x, y);
		return;
	    } else if (range[x][y]) {
		this.guiAction(x, y);
		return;
	    }
	    this.clearOptions();
	    return;
	}
	if (gx >= guiDebugX && gx <= guiDebugX + guiWidth && gy >= guiDebugY && gy <= guiDebugY + guiHeight) {
	    guiGameObject.mouseClicked(new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), gx - guiDebugX, gy - guiDebugY, e.getClickCount(), e.isPopupTrigger(), e.getButton()));
	    return;
	}

	if (e.getButton() == MouseEvent.BUTTON1 && !firstClick) {

	    field = new boolean[mapWidth][mapHeight];

	    // Clicking on Unit
	    if (units[x][y] != null) {
		field[x][y] = true;
		Unit u = units[x][y];
		selected = u;
		actionPerformed(new ActionEvent(selected, ActionEvent.ACTION_PERFORMED, null));
		guiGameObject = new GuiGameMenu(selected);
		guiPosX = (x + 1) * tileSize;
		guiPosY = (y + 1) * tileSize;
		return;
	    }

	    // Clicking on Building
	    for (int x1 = x; x1 > x1 - 6 && x1 >= 0; x1--) {
		for (int y1 = y; y1 > y1 - 6 && y1 >= 0; y1--) {
		    Building b = buildings[x1][y1];
		    if (b != null && b.getSizeX() + x1 > x && b.getSizeY() + y1 > y) {
			field[x1][y1] = true;
			selected = b;
			actionPerformed(new ActionEvent(selected, ActionEvent.ACTION_PERFORMED, null));
			guiGameObject = new GuiGameMenu(selected);
			guiPosX = (x + b.getSizeX()) * tileSize;
			guiPosY = (y + b.getSizeY()) * tileSize;
			return;
		    }
		}
	    }

	    if (mapCache[x][y].isWalkable()) {
		Class<?>[] classes = new Class<?>[] { Archer.class, Knight.class, Spearman.class, Swordsman.class };
		@SuppressWarnings("unchecked")
		Class<? extends Unit> clazz = (Class<? extends Unit>) classes[Client.instance.random.nextInt(classes.length)];
		try {
		    Unit u = clazz.getConstructor(Player.class, Integer.TYPE, Integer.TYPE).newInstance(null, x, y);
		    units[x][y] = u;
		} catch (Exception e1) {
		    e1.printStackTrace();
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
	int mapWidth = mapCache.length;
	int mapHeight = mapCache[0].length;

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
	if (gx >= guiDebugX && gx <= guiDebugX + guiWidth && gy >= guiDebugY && gy <= guiDebugY + guiHeight && !activeAction) {
	    guiGameObject.mouseMoved(new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), gx - guiDebugX, gy - guiDebugY, e.getClickCount(), e.isPopupTrigger()));
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
	    int mapWidth = mapCache.length;
	    int mapHeight = mapCache[0].length;
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
		if (button == gameStateButton)
		    Client.instance.game.gm.nextPhase();
	    }

	    // GameObjects
	    if (e.getSource() instanceof GameObject) {
		Client.instance.game.gm.actionPerformed(e);
	    }

	    // Keys
	    if (e.getSource() instanceof KeyEvent) {
		int key = ((KeyEvent) e.getSource()).getKeyCode();
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
	int mapWidth = mapCache.length;
	int mapHeight = mapCache[0].length;
	if (scrollX > (int) (mapWidth * tileSize * zoom - width))
	    scrollX = (int) (mapWidth * tileSize * zoom - width);
	if (scrollY > (int) (mapHeight * tileSize * zoom - height))
	    scrollY = (int) (mapHeight * tileSize * zoom - height);
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
	int mapWidth = mapCache.length;
	int mapHeight = mapCache[0].length;
	if (scrollX > (int) (mapWidth * tileSize * zoom - width) && scrollX > 0)
	    scrollX = (int) (mapWidth * tileSize * zoom - width);
	if (scrollY > (int) (mapHeight * tileSize * zoom - height) && scrollY > 0)
	    scrollY = (int) (mapHeight * tileSize * zoom - height);
    }
}
