package de.gymdon.inf1315.game.path

import de.gymdon.inf1315.game.tile.TileMap
import de.gymdon.inf1315.game.tile.TilePosition

data class Path(val points: Array<TilePosition>, val map: TileMap) {
    infix fun TilePosition.distTo(to: TilePosition): Double = (this distanceTo to) * (map[this].groundFactor + map[to].groundFactor) / 2
    inline val start: TilePosition
        get() = points[0]
    inline val end: TilePosition
        get() = points[points.size - 1]

    fun length(): Double {
        var length = 0.0
        var next = start
        for (i in 1..points.size - 1) {
            val cur = next
            next = points[i]
            length += cur distTo next
        }
        return length
    }

    fun ascii(): String {
        val nw = TilePosition.nw(start, end)
        val se = TilePosition.se(start, end)
        val size = se - nw
        val chars = Array(size.y + 1, {y -> CharArray(size.x + 1, { x -> when {
            !map[x, y].isWalkable -> '█'
            map[x, y].groundFactor > 1 -> '░'
            else -> ' '
        }})})
        chars[end.y - nw.y][end.x - nw.x] = 'o'
        var next = start
        for (i in 1..points.size - 1) {
            val cur = next
            next = points[i]
            chars[cur.y - nw.y][cur.x - nw.y] = (cur directionTo next).symbol
        }
        val sb = StringBuilder()
        chars.forEach { sb.append(String(it)).append('\n') }
        return sb.toString()
    }
}
