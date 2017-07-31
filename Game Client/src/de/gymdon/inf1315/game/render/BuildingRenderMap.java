package de.gymdon.inf1315.game.render;

import de.gymdon.inf1315.game.Barracks;
import de.gymdon.inf1315.game.Building;
import de.gymdon.inf1315.game.Castle;
import de.gymdon.inf1315.game.Mine;

public class BuildingRenderMap {

    public static Texture getTexture(Building b) {
        if (b instanceof Barracks)
            return b.getOwner() == null ? null : StandardTexture.get("baracks_" + b.getOwner().getColor().name().toLowerCase());
        if (b instanceof Castle)
            return b.getOwner() == null ? StandardTexture.get("castle_big_neutral") : StandardTexture.get("castle_big_" + b.getOwner().getColor().name().toLowerCase());
        if (b instanceof Mine)
            return ((Mine) b).getSuperior() ? StandardTexture.get("mine_superior") : b.getOwner() == null ? StandardTexture.get("mine_neutral") : StandardTexture.get("mine_" + b.getOwner().getColor().name().toLowerCase());
        return null;
    }
}
