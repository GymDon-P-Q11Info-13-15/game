package de.gymdon.inf1315.game.client;

import de.gymdon.inf1315.game.Game;
import de.gymdon.inf1315.game.MapGenerator;
import de.gymdon.inf1315.game.Translation;
import de.gymdon.inf1315.game.packet.Remote;
import de.gymdon.inf1315.game.render.GameCanvas;
import de.gymdon.inf1315.game.render.MapRenderer;
import de.gymdon.inf1315.game.render.gui.GuiMainMenu;
import de.gymdon.inf1315.game.render.gui.GuiScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.List;

public class Client implements Runnable, WindowListener {
    public static final boolean DEBUG = false;
    public static final String TITLE = "Game Title";
    public static final String VERSION = "Alpha 0.0.1";
    public static final String COMMIT = "$Id$";
    public static Client instance;
    public GuiScreen currentScreen;
    public Translation translation;
    public Preferences preferences;
    public Random random = new Random();
    public MacOSUtils macOsUtils;
    public List<Remote> remotes = new ArrayList<Remote>();
    public Map<Remote, Thread> remoteThreads = new HashMap<Remote, Thread>();
    public MapRenderer mapren;
    public Game game;
    private boolean running = false;
    private JFrame frame;
    private GameCanvas canvas;
    private int ticksRunning = 0;
    private int tps = 0;
    private int fps = 0;

    public Client() {
        Client.instance = this;
        frame = new JFrame(TITLE);
        frame.setSize(1280, 720);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setPreferredSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        canvas = new GameCanvas();
        frame.add(canvas);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
        frame.addWindowListener(this);
    }

    public static void main(String[] args) {
        new Thread(new Client()).start();
    }

    @Override
    public void run() {
        running = true;
        long lastTime = System.nanoTime();
        double unprocessed = 0;
        double nsPerTick = 1000000000.0 / 60;
        int frames = 0;
        int ticks = 0;
        long lastTimer1 = System.currentTimeMillis();

        new Thread(this::init).start();

        while (running) {
            long now = System.nanoTime();
            unprocessed += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean shouldRender = preferences != null && !preferences.video.vsync;
            while (unprocessed >= 1) {
                ticks++;
                ticksRunning++;
                tick();
                unprocessed -= 1;
                shouldRender = true;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (shouldRender) {
                frames++;
                render();
            }

            if (System.currentTimeMillis() - lastTimer1 > 1000) {
                lastTimer1 += 1000;
                if (translation != null) {
                    frame.setTitle(translation.translate("game.title"));
                    if (DEBUG) {
                        frame.setTitle(translation.translate("game.title") + " - " + ticks + "TPS " + frames + "FPS");
                        System.out.println(frame.getTitle());
                    }
                }
                this.tps = ticks;
                this.fps = frames;
                frames = 0;
                ticks = 0;
            }
        }
        cleanUp();
    }

    private void init() {
        translation = new Translation("en_US");
        readPreferences();
        translation.load(preferences.language);
        setGuiScreen(new GuiMainMenu());
        if (MacOSUtils.isMacOS())
            macOsUtils = new MacOSUtils(frame);
        setFullscreen(preferences.video.fullscreen);
        System.out.println("Started \"" + translation.translate("game.title") + " " + VERSION + "\"");
    }

    public void setFullscreen(final boolean fullscreen) {
    /*
	 * if(macOsUtils != null) { macOsUtils.setFullscreen(fullscreen);
	 * return; }
	 */
        SwingUtilities.invokeLater(() -> {
            GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (fullscreen) {
                screen.setFullScreenWindow(frame);
                DisplayMode[] modes = screen.getDisplayModes();
                DisplayMode current = screen.getDisplayMode();
                for (DisplayMode mode : modes) {
                    if (mode.getWidth() * mode.getHeight() > current.getWidth() * current.getHeight() && mode.getRefreshRate() > current.getRefreshRate())
                        current = mode;
                }
                screen.setDisplayMode(current);
                System.out.println("DisplayMode: " + current.getWidth() + "x" + current.getHeight() + "x" + current.getBitDepth() + " @" + current.getRefreshRate() + "Hz");
                frame.setSize(current.getWidth(), current.getHeight());
            } else {
                screen.setFullScreenWindow(null);
            }
        });
    }

    public void reload() {
        translation = new Translation("en_US");
        readPreferences();
        translation.load(preferences.language);
        frame.setTitle(Client.instance.translation.translate("game.title"));
        setFullscreen(preferences.video.fullscreen);
    }

    private void readPreferences() {
        File f = new File("preferences.json");
        try {
            if (f.exists())
                preferences = Preferences.readNew(new FileReader(f));
            else {
                preferences = new Preferences();
                f.createNewFile();
                preferences.write(new FileWriter(f));
                System.out.println(translation.translate("file.created", "preferences.json"));
            }
            if (preferences.version != Preferences.CURRENT_VERSION) {
                preferences.version = Preferences.CURRENT_VERSION;
                preferences = new Preferences();
                f.createNewFile();
                preferences.write(new FileWriter(f));
                System.out.println(translation.translate("updated.version", "preferences.json", preferences.version));
            }
        } catch (Exception e) {
            throw new RuntimeException("Preferences couldn't be loaded/saved", e);
        }
    }

    private void tick() {
        for (Iterator<Remote> it = remotes.iterator(); it.hasNext(); ) {
            final Remote r = it.next();
            if (r instanceof Server && !remoteThreads.containsKey(r)) {
                Thread t = new Thread(() -> {
                    while (true) {
                        ((Server) r).processPackets();

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                remoteThreads.put(r, t);
                t.start();
            }
            if (r.left() || r.getSocket().isClosed()) {
                try {
                    remoteThreads.get(r).join(10);
                } catch (InterruptedException ignored) {}
                remoteThreads.remove(r);
            }
        }
        remotes.removeIf(Remote::left);
        if (currentScreen != null)
            currentScreen.tick();
    }

    private void render() {
        canvas.repaint();
    }

    public void stop() {
        running = false;
    }

    private void cleanUp() {
        System.out.println("Stopping");
        System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        stop();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    public void setGuiScreen(GuiScreen newScreen) {
        if (currentScreen != null) {
            canvas.removeMouseListener(currentScreen);
            canvas.removeMouseMotionListener(currentScreen);
            frame.removeKeyListener(currentScreen);
        } else {
            canvas.removeMouseListener(canvas.mapRenderer);
            canvas.removeMouseMotionListener(canvas.mapRenderer);
            canvas.removeMouseWheelListener(canvas.mapRenderer);
            frame.removeKeyListener(canvas.mapRenderer);
            canvas.mapRenderer = null;
        }
        currentScreen = newScreen;
        if (currentScreen != null) {
            canvas.addMouseListener(currentScreen);
            canvas.addMouseMotionListener(currentScreen);
            frame.addKeyListener(currentScreen);
        }
    }

    public void activateMap(boolean newMap) {
        if (newMap) {
            game = new Game(null);
            mapren = new MapRenderer();
            game.mapgen = new MapGenerator();
            game.mapgen.generateAll();
            game.map = game.mapgen.getMap();
            game.buildings = game.mapgen.getBuildings();
            game.buildings[1][game.mapgen.getMapHeight() / 2 - 1].setOwner(game.player1);
            game.buildings[game.mapgen.getMapWidth() - 3][game.mapgen.getMapHeight() / 2 - 1].setOwner(game.player2);
        }
        setGuiScreen(null);
        canvas.mapRenderer = mapren;
        canvas.addMouseListener(canvas.mapRenderer);
        canvas.addMouseMotionListener(canvas.mapRenderer);
        canvas.addMouseWheelListener(canvas.mapRenderer);
        frame.addKeyListener(canvas.mapRenderer);
    }

    public int getTicksRunning() {
        return ticksRunning;
    }

    public int getTPS() {
        return tps;
    }

    public int getFPS() {
        return fps;
    }
}
