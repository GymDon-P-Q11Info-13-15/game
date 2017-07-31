package de.gymdon.inf1315.game

import de.gymdon.inf1315.game.tile.TilePosition

abstract class GameObject {
    var pos: TilePosition = TilePosition(0, 0)
    var x: Int
        get() = pos.x
        set(x) { pos = TilePosition(x, pos.y)
        }
    var y: Int
        get() = pos.y
        set(y) { pos = TilePosition(pos.x, y)}
    var cost: Int = 0
    var hp: Int = 0
    var defense: Int = 0
    var owner: Player? = null

    open var options: BooleanArray = BooleanArray(7)

    abstract fun clicked(phase: Int): BooleanArray

    open val sizeX: Int
        get() = 1

    open val sizeY: Int
        get() = 1
}
