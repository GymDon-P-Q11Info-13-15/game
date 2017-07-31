package de.gymdon.inf1315.game.render;

import de.gymdon.inf1315.game.*;

public class UnitRenderMap {

    public static Texture getTexture(Unit u) {
        if (u instanceof Archer)
            return StandardTexture.get("bow");
        if (u instanceof Knight)
            return StandardTexture.get("knight");
        if (u instanceof Miner)
            return StandardTexture.get("miner");
        if (u instanceof Spearman)
            return StandardTexture.get("spear");
        if (u instanceof Swordsman)
            return StandardTexture.get("sword");
        return null;
    }
}
