package de.gymdon.inf1315.game.render;

import de.gymdon.inf1315.game.Archer;
import de.gymdon.inf1315.game.Unit;
import de.gymdon.inf1315.game.Knight;
import de.gymdon.inf1315.game.Miner;
import de.gymdon.inf1315.game.Spearman;
import de.gymdon.inf1315.game.Swordsman;
import de.gymdon.inf1315.game.render.StandardTexture;

public class UnitRenderMap {

    public static Texture getTexture(Unit u) {
	if(u instanceof Archer)
	    return StandardTexture.get("sand_old");
	if(u instanceof Knight)
	    return StandardTexture.get("sand_old");
	if(u instanceof Miner)
	    return StandardTexture.get("sand_old");
	if(u instanceof Spearman)
	    return StandardTexture.get("sand_old");
	if(u instanceof Swordsman)
	    return StandardTexture.get("sand_old");
	return null;
    }
}
