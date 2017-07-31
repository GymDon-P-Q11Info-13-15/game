package de.gymdon.inf1315.game.render;

import de.gymdon.inf1315.game.tile.Tile;

import java.util.HashMap;
import java.util.Map;

public class TileRenderMap {

    private static Map<Tile, Texture> map = new HashMap<Tile, Texture>();

    static {
        map.put(Tile.Companion.getGrass(), new StandardTexture("grass"));
        map.put(Tile.Companion.getGrass2(), new StandardTexture("grass2"));
        map.put(Tile.Companion.getSand(), new StandardTexture("sand"));
        map.put(Tile.Companion.getSand2(), new StandardTexture("sand_old"));
        map.put(Tile.Companion.getWater(), new StandardTexture("water"));
        map.put(Tile.Companion.getWater2(), new StandardTexture("water2"));
    }

    public static Texture getTexture(Tile t) {
        return map.get(t);
    }
}
