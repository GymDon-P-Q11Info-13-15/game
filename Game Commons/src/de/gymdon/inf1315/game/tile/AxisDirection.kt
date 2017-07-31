package de.gymdon.inf1315.game.tile

enum class AxisDirection(val symbol: Char) {
    NORTH('^'), EAST('>'), SOUTH('v'), WEST('<');

    operator fun unaryMinus(): AxisDirection = when(this) {
        NORTH -> SOUTH
        EAST -> WEST
        SOUTH -> NORTH
        WEST -> EAST
    }
}