package de.gymdon.inf1315.game.tile

enum class Direction(val symbol: Char) {
    NORTH('↑'), NORTHEAST('↗'),
    EAST('→'), SOUTHEAST('↘'),
    SOUTH('↓'), SOUTHWEST('↙'),
    WEST('←'), NORTHWEST('↖');

    operator fun unaryMinus(): Direction = when(this) {
        NORTH -> SOUTH
        NORTHEAST -> SOUTHWEST
        EAST -> WEST
        SOUTHEAST -> NORTHWEST
        SOUTH -> NORTH
        SOUTHWEST -> NORTHEAST
        WEST -> EAST
        NORTHWEST -> SOUTHEAST
    }
}