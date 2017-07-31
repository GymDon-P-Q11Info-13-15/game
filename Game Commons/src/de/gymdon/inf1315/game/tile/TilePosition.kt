package de.gymdon.inf1315.game.tile

data class TilePosition(val x: Int, val y: Int) {
    fun offset(dir: AxisDirection, offset: Int = 1): TilePosition {
        when (dir) {
            AxisDirection.NORTH -> return TilePosition(x, y - offset)
            AxisDirection.EAST -> return TilePosition(x + offset, y)
            AxisDirection.SOUTH -> return TilePosition(x, y + offset)
            AxisDirection.WEST -> return TilePosition(x - offset, y)
        }
    }

    fun north(offset: Int = 1): TilePosition = offset(AxisDirection.NORTH, offset)
    val north: TilePosition
        get() = north()

    fun east(offset: Int = 1): TilePosition = offset(AxisDirection.EAST, offset)
    val east: TilePosition
        get() = east()

    fun south(offset: Int = 1): TilePosition = offset(AxisDirection.SOUTH, offset)
    val south: TilePosition
        get() = south()

    fun west(offset: Int = 1): TilePosition = offset(AxisDirection.WEST, offset)
    val west: TilePosition
        get() = west()

    fun directNeighbors(map: TileMap) = directNeighbors(map.width, map.height)
    fun directNeighbors(width: Int, height: Int): List<TilePosition> {
        val l = ArrayList<TilePosition>(4)
        if (x > 0) l.add(west)
        if (y > 0) l.add(north)
        if (x < width - 1) l.add(east)
        if (y < height - 1) l.add(south)
        return l
    }

    fun neighbors(map: TileMap) = neighbors(map.width, map.height)
    fun neighbors(width: Int, height: Int): List<TilePosition> {
        val l = ArrayList<TilePosition>(8)
        if (x > 0) {
            l.add(west)
            if (y > 0) l.add(north.west)
            if (y < height - 1) l.add(south.west)
        }
        if (y > 0) l.add(north)
        if (x < width - 1) {
            l.add(east)
            if (y > 0) l.add(north.east)
            if (y < height - 1) l.add(south.east)
        }
        if (y < height - 1) l.add(south)
        return l
    }

    infix fun directionTo(to: TilePosition): Direction {
        if (to.x > x) {
            if (to.y < y) return Direction.NORTHEAST
            if (to.y > y) return Direction.SOUTHEAST
            return Direction.EAST
        }
        if (to.x < x) {
            if (to.y < y) return Direction.NORTHWEST
            if (to.y > y) return Direction.SOUTHWEST
            return Direction.WEST
        }
        if (to.y > y) return Direction.SOUTH
        return Direction.NORTH
    }

    infix fun distanceTo(to: TilePosition): Double {
        val rel = to - this
        val xDist = Math.abs(rel.x).toDouble()
        val yDist = Math.abs(rel.y).toDouble()
        return Math.sqrt(xDist * xDist + yDist * yDist)
    }

    operator fun plus(other: TilePosition): TilePosition = TilePosition(x + other.x, y + other.y)
    operator fun minus(other: TilePosition): TilePosition = TilePosition(x - other.x, y - other.y)

    operator fun times(factor: Int): TilePosition = TilePosition(x * factor, y * factor)

    operator fun rangeTo(other: TilePosition): List<TilePosition> {
        val nw = TilePosition(minOf(x, other.x), minOf(y, other.y))
        val se = TilePosition(maxOf(x, other.x), maxOf(y, other.y))
        val size = se - nw
        val l = ArrayList<TilePosition>(size.x * size.y)
        for (y in nw.y..se.y) {
            (nw.x..se.x).mapTo(l) { x -> TilePosition(x, y) }
        }
        return l
    }

    operator fun unaryPlus(): TilePosition = TilePosition(Math.abs(x), Math.abs(y))

    override fun toString(): String {
        return "($x,$y)"
    }

    companion object {
        fun nw(a: TilePosition, b: TilePosition) = TilePosition(minOf(a.x, b.x), minOf(a.y, b.y))
        fun ne(a: TilePosition, b: TilePosition) = TilePosition(maxOf(a.x, b.x), minOf(a.y, b.y))
        fun sw(a: TilePosition, b: TilePosition) = TilePosition(minOf(a.x, b.x), maxOf(a.y, b.y))
        fun se(a: TilePosition, b: TilePosition) = TilePosition(maxOf(a.x, b.x), maxOf(a.y, b.y))
    }
}
