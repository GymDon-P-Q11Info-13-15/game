package de.gymdon.inf1315.game.render;

import de.gymdon.inf1315.game.Barracks;
import de.gymdon.inf1315.game.Building;
import de.gymdon.inf1315.game.Castle;
import de.gymdon.inf1315.game.Mine;
import de.gymdon.inf1315.game.render.StandardTexture;

public class BuildingRenderMap {

    public static Texture getTexture(Building b) {
	if (b instanceof Barracks)
	    return StandardTexture.get("sand_old");
	if (b instanceof Castle)
	    return b.owner == null ? StandardTexture.get("castle_big_neutral") : StandardTexture.get("castle_big_" + b.owner.color.name().toLowerCase());
	if (b instanceof Mine)
	    return ((Mine) b).superior ? StandardTexture.get("mine_superior") : b.owner == null ? StandardTexture.get("mine_neutral") : StandardTexture.get("mine_" + b.owner.color.name().toLowerCase());
	return null;
    }
}
