package de.gymdon.inf1315.game.render.gui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.gymdon.inf1315.game.Utils;
import de.gymdon.inf1315.game.client.Client;
import de.gymdon.inf1315.game.render.StandardTexture;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GuiOptions extends GuiScreen {

    private GuiScreen last;
    private Section section;
    private GuiButton backButton = new GuiButton(this, 0, 300, 550, "gui.back");

    // Sections
    private GuiButton videoButton = new GuiButton(this, 0, 100, 200, "gui.options.video");
    private GuiButton languageButton = new GuiButton(this, 0, 100, 200, "gui.options.language");
    private GuiButton gameButton = new GuiButton(this, 0, 100, 200, "gui.options.game");
    // -- Video
    private GuiButton videoVsyncButton = new GuiButton(this, 0, 100, 200, "gui.options.video.vsync." + (Client.instance.preferences.video.vsync ? "on" : "off"));
    private GuiButton videoFullscreenButton = new GuiButton(this, 0, 100, 200, "gui.options.video.fullscreen." + (Client.instance.preferences.video.fullscreen ? "on" : "off"));
    // -- Language
    private List<GuiButton> languageButtons = new ArrayList<GuiButton>();
    // -- Game Options
    private GuiButton gameArrowButton = new GuiButton(this, 0, 100, 200, "gui.options.game.arrow");
    private GuiButton gameZoomButton = new GuiButton(this, 0, 100, 200, "gui.options.game.zoom." + (Client.instance.preferences.game.invertZoom ? "inverted" : "normal"));
    private GuiButton gameHealthButton = new GuiButton(this, 0, 100, 200, "gui.options.game.health." + (Client.instance.preferences.game.health == 0 ? "relative" : Client.instance.preferences.game.health == 1 ? "absolute" : "both"));
    private GuiButton gameKeysButton = new GuiButton(this, 0, 100, 200, "gui.options.game.keys");
    // -- Arrows
    private List<GuiButton> arrowButtons = new ArrayList<GuiButton>();
    // -- Keys
    private int kDefID = 0;
    private int BC = backButton.getBorderColor();
    private int BCK = 0xFF8C00;
    private GuiButton absoluteKeyButton = new GuiButton(this, 1, 100, 200, "gui.options.game.keys.absolute").setTextData(Client.instance.preferences.game.absoluteKey == null ? "" : (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.absoluteKey.getModifiers()) + (Client.instance.preferences.game.absoluteKey.getModifiers() == 0 || KeyEvent.getKeyModifiersText(Client.instance.preferences.game.absoluteKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.absoluteKey.getKeyCode())) ? "" : " + ")) + (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.absoluteKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.absoluteKey.getKeyCode())) ? "" : KeyEvent.getKeyText(Client.instance.preferences.game.absoluteKey.getKeyCode()))).setBorderColor(kDefID == 1 ? BCK : BC);
    private GuiButton collapseKeyButton = new GuiButton(this, 2, 100, 200, "gui.options.game.keys.collapse").setTextData(Client.instance.preferences.game.collapseKey == null ? "" : (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.collapseKey.getModifiers()) + (Client.instance.preferences.game.collapseKey.getModifiers() == 0 || KeyEvent.getKeyModifiersText(Client.instance.preferences.game.collapseKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.collapseKey.getKeyCode())) ? "" : " + ")) + (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.collapseKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.collapseKey.getKeyCode())) ? "" : KeyEvent.getKeyText(Client.instance.preferences.game.collapseKey.getKeyCode()))).setBorderColor(kDefID == 2 ? BCK : BC);
    private GuiButton fullscreenKeyButton = new GuiButton(this, 3, 100, 200, "gui.options.game.keys.fullscreen").setTextData(Client.instance.preferences.game.fullscreenKey == null ? "" : (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.fullscreenKey.getModifiers()) + (Client.instance.preferences.game.fullscreenKey.getModifiers() == 0 || KeyEvent.getKeyModifiersText(Client.instance.preferences.game.fullscreenKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.fullscreenKey.getKeyCode())) ? "" : " + ")) + (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.fullscreenKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.fullscreenKey.getKeyCode())) ? "" : KeyEvent.getKeyText(Client.instance.preferences.game.fullscreenKey.getKeyCode()))).setBorderColor(kDefID == 3 ? BCK : BC);

    private Stack<Section> sectionStack = new Stack<Section>();

    public GuiOptions() {
        setSection(Section.MAIN);
    }

    public GuiOptions(GuiScreen last) {
        this();
        this.last = last;
    }

    public void rebuild() {
        backButton = new GuiButton(this, 0, 300, 550, "gui.back");
        videoButton = new GuiButton(this, 0, 100, 200, "gui.options.video");
        videoVsyncButton = new GuiButton(this, 0, 100, 200, "gui.options.video.vsync." + (Client.instance.preferences.video.vsync ? "on" : "off"));
        videoFullscreenButton = new GuiButton(this, 0, 100, 200, "gui.options.video.fullscreen." + (Client.instance.preferences.video.fullscreen ? "on" : "off"));
        languageButton = new GuiButton(this, 0, 100, 200, "gui.options.language");
        languageButtons.clear();
        gameButton = new GuiButton(this, 0, 100, 200, "gui.options.game");
        gameArrowButton = new GuiButton(this, 0, 100, 200, "gui.options.game.arrow");
        arrowButtons.clear();
        gameZoomButton = new GuiButton(this, 0, 100, 200, "gui.options.game.zoom." + (Client.instance.preferences.game.invertZoom ? "inverted" : "normal"));
        gameHealthButton = new GuiButton(this, 0, 100, 200, "gui.options.game.health." + (Client.instance.preferences.game.health == 0 ? "relative" : Client.instance.preferences.game.health == 1 ? "absolute" : "both"));
        gameKeysButton = new GuiButton(this, 0, 100, 200, "gui.options.game.keys");
        absoluteKeyButton = new GuiButton(this, 1, 100, 200, "gui.options.game.keys.absolute").setTextData(Client.instance.preferences.game.absoluteKey == null ? "" : (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.absoluteKey.getModifiers()) + (Client.instance.preferences.game.absoluteKey.getModifiers() == 0 || KeyEvent.getKeyModifiersText(Client.instance.preferences.game.absoluteKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.absoluteKey.getKeyCode())) ? "" : " + ")) + (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.absoluteKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.absoluteKey.getKeyCode())) ? "" : KeyEvent.getKeyText(Client.instance.preferences.game.absoluteKey.getKeyCode()))).setBorderColor(kDefID == 1 ? BCK : BC);
        collapseKeyButton = new GuiButton(this, 2, 100, 200, "gui.options.game.keys.collapse").setTextData(Client.instance.preferences.game.collapseKey == null ? "" : (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.collapseKey.getModifiers()) + (Client.instance.preferences.game.collapseKey.getModifiers() == 0 || KeyEvent.getKeyModifiersText(Client.instance.preferences.game.collapseKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.collapseKey.getKeyCode())) ? "" : " + ")) + (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.collapseKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.collapseKey.getKeyCode())) ? "" : KeyEvent.getKeyText(Client.instance.preferences.game.collapseKey.getKeyCode()))).setBorderColor(kDefID == 2 ? BCK : BC);
        fullscreenKeyButton = new GuiButton(this, 3, 100, 200, "gui.options.game.keys.fullscreen").setTextData(Client.instance.preferences.game.fullscreenKey == null ? "" : (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.fullscreenKey.getModifiers()) + (Client.instance.preferences.game.fullscreenKey.getModifiers() == 0 || KeyEvent.getKeyModifiersText(Client.instance.preferences.game.fullscreenKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.fullscreenKey.getKeyCode())) ? "" : " + ")) + (KeyEvent.getKeyModifiersText(Client.instance.preferences.game.fullscreenKey.getModifiers()).contains(KeyEvent.getKeyText(Client.instance.preferences.game.fullscreenKey.getKeyCode())) ? "" : KeyEvent.getKeyText(Client.instance.preferences.game.fullscreenKey.getKeyCode()))).setBorderColor(kDefID == 3 ? BCK : BC);
        setSection(section);
    }

    @Override
    public void render(Graphics2D g2d, int width, int height) {
        drawBackground(g2d, width, height);

        Font f = Client.instance.translation.font.deriveFont(Font.BOLD, 80F);
        g2d.setFont(f);
        String title = Client.instance.translation.translate("gui.options" + (section != Section.MAIN ? "." + section.name().toLowerCase() : ""));
        Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(title, g2d);
        int titleX = (int) (width / 2 - bounds.getCenterX());
        int titleY = (int) (80 + bounds.getMaxY());
        g2d.setColor(Color.WHITE);
        g2d.drawString(title, titleX, titleY);

        int buttonWidth = width - width / 4;
        int buttonHeight = height / 10;
        int buttonSpacing = buttonHeight / 4;
        int topMargin = 150;
        int leftMargin = width / 2 - buttonWidth / 2;
        int buttonWidthSmall = (buttonWidth - buttonSpacing) / 2;
        backButton.setX(leftMargin);
        backButton.setY(height - buttonSpacing - buttonHeight);
        backButton.setWidth(buttonWidth);
        backButton.setHeight(buttonHeight);

        if (section == Section.MAIN) {
            videoButton.setX(leftMargin);
            videoButton.setY(topMargin);
            videoButton.setWidth(buttonWidthSmall);
            videoButton.setHeight(buttonHeight);
            languageButton.setX(leftMargin + buttonWidthSmall + buttonSpacing);
            languageButton.setY(topMargin);
            languageButton.setWidth(buttonWidthSmall);
            languageButton.setHeight(buttonHeight);
            gameButton.setX(leftMargin);
            gameButton.setY(topMargin + buttonHeight + buttonSpacing);
            gameButton.setWidth(buttonWidthSmall);
            gameButton.setHeight(buttonHeight);
        } else if (section == Section.VIDEO) {
            videoVsyncButton.setX(leftMargin);
            videoVsyncButton.setY(topMargin);
            videoVsyncButton.setWidth(buttonWidthSmall);
            videoVsyncButton.setHeight(buttonHeight);
            videoFullscreenButton.setX(leftMargin + buttonWidthSmall + buttonSpacing);
            videoFullscreenButton.setY(topMargin);
            videoFullscreenButton.setWidth(buttonWidthSmall);
            videoFullscreenButton.setHeight(buttonHeight);
        } else if (section == Section.LANGUAGE) {
            int i = 0;
            for (GuiButton b : languageButtons) {
                b.setX(leftMargin);
                b.setY(topMargin + (i++) * (buttonHeight + buttonSpacing));
                b.setWidth(buttonWidth);
                b.setHeight(buttonHeight);
            }
        } else if (section == Section.GAME) {
            gameArrowButton.setX(leftMargin);
            gameArrowButton.setY(topMargin);
            gameArrowButton.setWidth(buttonWidthSmall);
            gameArrowButton.setHeight(buttonHeight);
            gameZoomButton.setX(leftMargin + buttonWidthSmall + buttonSpacing);
            gameZoomButton.setY(topMargin);
            gameZoomButton.setWidth(buttonWidthSmall);
            gameZoomButton.setHeight(buttonHeight);
            gameHealthButton.setX(leftMargin);
            gameHealthButton.setY(topMargin + buttonHeight + buttonSpacing);
            gameHealthButton.setWidth(buttonWidthSmall);
            gameHealthButton.setHeight(buttonHeight);
            gameKeysButton.setX(leftMargin + buttonWidthSmall + buttonSpacing);
            gameKeysButton.setY(topMargin + buttonHeight + buttonSpacing);
            gameKeysButton.setWidth(buttonWidthSmall);
            gameKeysButton.setHeight(buttonHeight);
        } else if (section == Section.ARROWS) {
            int i = 0;
            for (GuiButton b : arrowButtons) {
                b.setX((int) (width / 2 - buttonHeight * 2 - buttonSpacing * 7.5 + (i % 4) * (buttonHeight + buttonSpacing * 5)));
                b.setY(topMargin + (i / 4) * (buttonHeight + buttonSpacing * 5));
                b.setWidth(buttonHeight);
                b.setHeight(buttonHeight);
                i++;
            }
        } else if (section == Section.KEYS) {
            absoluteKeyButton.setX(leftMargin);
            absoluteKeyButton.setY(topMargin);
            absoluteKeyButton.setWidth(buttonWidth);
            absoluteKeyButton.setHeight(buttonHeight);
            collapseKeyButton.setX(leftMargin);
            collapseKeyButton.setY(topMargin + buttonHeight + buttonSpacing);
            collapseKeyButton.setWidth(buttonWidth);
            collapseKeyButton.setHeight(buttonHeight);
            fullscreenKeyButton.setX(leftMargin);
            fullscreenKeyButton.setY(topMargin + (buttonHeight + buttonSpacing) * 2);
            fullscreenKeyButton.setWidth(buttonWidth);
            fullscreenKeyButton.setHeight(buttonHeight);
        }
        super.render(g2d, width, height);
    }

    @Override
    public void tick() {
        super.tick();
        videoFullscreenButton.setText("gui.options.video.fullscreen." + (Client.instance.preferences.video.fullscreen ? "on" : "off"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getID() == ActionEvent.ACTION_PERFORMED) {
            // Buttons
            if (e.getSource() instanceof GuiButton && kDefID == 0) {
                GuiButton button = (GuiButton) e.getSource();
                if (button == backButton) {
                    setSection(sectionStack.isEmpty() ? null : sectionStack.peek(), true);
                } else if (button == videoButton) {
                    setSection(Section.VIDEO);
                } else if (button == videoVsyncButton) {
                    Client.instance.preferences.video.vsync = !Client.instance.preferences.video.vsync;
                    this.rebuild();
                } else if (button == videoFullscreenButton) {
                    Client.instance.preferences.video.fullscreen = !Client.instance.preferences.video.fullscreen;
                    this.rebuild();
                    Client.instance.setFullscreen(Client.instance.preferences.video.fullscreen);
                } else if (button == languageButton) {
                    setSection(Section.LANGUAGE);
                } else if (languageButtons.contains(button)) {
                    String lang = (String) button.getTextData()[0];
                    Client.instance.preferences.language = lang;
                    Client.instance.translation.reload("en_US");
                    Client.instance.translation.load(lang);
                    this.rebuild();
                    if (last != null)
                        last.rebuild();
                } else if (button == gameButton) {
                    setSection(Section.GAME);
                } else if (button == gameArrowButton) {
                    setSection(Section.ARROWS);
                } else if (button == gameZoomButton) {
                    Client.instance.preferences.game.invertZoom = !Client.instance.preferences.game.invertZoom;
                    this.rebuild();
                } else if (button == gameHealthButton) {
                    if (Client.instance.preferences.game.health < 2 && Client.instance.preferences.game.health >= 0)
                        Client.instance.preferences.game.health++;
                    else
                        Client.instance.preferences.game.health = 0;
                    this.rebuild();
                } else if (button == gameKeysButton) {
                    setSection(Section.KEYS);
                } else if (button == absoluteKeyButton) {
                    kDefID = absoluteKeyButton.getId();
                    this.rebuild();
                } else if (button == collapseKeyButton) {
                    kDefID = collapseKeyButton.getId();
                    this.rebuild();
                } else if (button == fullscreenKeyButton) {
                    kDefID = fullscreenKeyButton.getId();
                    this.rebuild();
                } else if (arrowButtons.contains(button)) {
                    Client.instance.preferences.game.arrow = button.getId();
                    this.rebuild();
                    if (last != null)
                        last.rebuild();
                }
            }

            // Keys
            if (e.getSource() instanceof KeyEvent) {
                int key = ((KeyEvent) e.getSource()).getKeyCode();

                // Keys Pressed
                if (kDefID == 0) {
                    if (key == KeyEvent.VK_ESCAPE)
                        actionPerformed(new ActionEvent(backButton, ActionEvent.ACTION_PERFORMED, null));
                }

                // Key Definitions
                else if (kDefID != 0) {
                    if (key == KeyEvent.VK_ESCAPE || key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT)
                        kDefID = 0;
                    else if (kDefID == absoluteKeyButton.getId())
                        Client.instance.preferences.game.absoluteKey = (KeyEvent) e.getSource();
                    else if (kDefID == collapseKeyButton.getId())
                        Client.instance.preferences.game.collapseKey = (KeyEvent) e.getSource();
                    else if (kDefID == fullscreenKeyButton.getId())
                        Client.instance.preferences.game.fullscreenKey = (KeyEvent) e.getSource();
                    this.rebuild();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        super.keyReleased(e);

        // Key Definitions
        if (kDefID != 0) {
            if (!(e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isMetaDown() || e.isShiftDown()))
                kDefID = 0;
            this.rebuild();
        }
    }

    private void setSection(Section s) {
        setSection(s, false);
    }

    private void setSection(Section s, boolean back) {
        if (!back && section != null && section != s)
            sectionStack.push(section);
        if (back) {
            if (!sectionStack.isEmpty())
                sectionStack.pop();
            else {
                Client.instance.setGuiScreen(last);
                try {
                    Client.instance.preferences.write(new FileWriter("preferences.json"));
                } catch (IOException e1) {
                    System.err.println("Unable to save preferences");
                }
                return;
            }
        }
        section = s;
        controlList.clear();
        switch (s) {
            case MAIN:
                controlList.add(videoButton);
                controlList.add(languageButton);
                controlList.add(gameButton);
                break;
            case VIDEO:
                controlList.add(videoVsyncButton);
                controlList.add(videoFullscreenButton);
                break;
            case LANGUAGE:
                languageButtons.clear();
                Map<String, String> languages = new Gson().fromJson(new InputStreamReader(GuiOptions.class.getResourceAsStream("/lang/languages.json")), new TypeToken<Map<String, String>>() {
                }.getType());
                int i = 0x100;
                for (Map.Entry<String, String> e : languages.entrySet()) {
                    GuiButton button = new GuiButton(this, i, 0, 0, e.getValue()).setTranslate(false).setTextData(e.getKey());
                    languageButtons.add(button);
                    if (e.getKey().equals(Client.instance.preferences.language))
                        button.setEnabled(false);
                }
                controlList.addAll(languageButtons);
                break;
            case GAME:
                controlList.add(gameArrowButton);
                controlList.add(gameZoomButton);
                controlList.add(gameHealthButton);
                controlList.add(gameKeysButton);
                break;
            case ARROWS:
                arrowButtons.clear();
                int arrows = 0;
                try {
                    arrows = Utils.getResourceListing(GuiOptions.class.getClassLoader(), "/textures/arrow_").size();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                for (int j = 0; j < arrows; j++) {
                    GuiButton button = new GuiButton(this, j, 0, 0, null);
                    button.setTexture(StandardTexture.get("arrow_" + j));
                    arrowButtons.add(button);
                    if (j == Client.instance.preferences.game.arrow)
                        button.setEnabled(false);
                }
                controlList.addAll(arrowButtons);
                break;
            case KEYS:
                controlList.add(absoluteKeyButton);
                controlList.add(collapseKeyButton);
                controlList.add(fullscreenKeyButton);
                break;
        }
        controlList.add(backButton);
    }

    private enum Section {
        MAIN, VIDEO, LANGUAGE, GAME, ARROWS, KEYS;
    }
}
