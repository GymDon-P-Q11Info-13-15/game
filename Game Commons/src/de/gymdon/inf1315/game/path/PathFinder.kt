package de.gymdon.inf1315.game.path

import de.gymdon.inf1315.game.MapGenerator
import de.gymdon.inf1315.game.tile.TileMap
import de.gymdon.inf1315.game.tile.TilePosition
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PathFinder(private val map: TileMap) {
    fun findPath(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int): Path? {
        return findPath(TilePosition(xStart, yStart), TilePosition(xEnd, yEnd))
    }

    fun findPath(start: TilePosition, end: TilePosition): Path? {
        println("finding path: $start -> $end")
        if (!map.isValid(start) || !map.isValid(end)) {
            println("Invalid position ${if (map.isValid(start)) end else start}")
            return null
        }

        infix fun TilePosition.minDistTo(to: TilePosition): Double = (this distanceTo to) * minOf(map[this].groundFactor, map[to].groundFactor)
        infix fun TilePosition.distTo(to: TilePosition): Double = (this distanceTo to) * (map[this].groundFactor + map[to].groundFactor) / 2

        val cameFrom = HashMap<TilePosition, TilePosition>()
        val fScore = HashMap<TilePosition, Double>()
        val gScore = HashMap<TilePosition, Double>()
        val open = PriorityQueue<TilePosition>(Comparator(fun (a: TilePosition, b: TilePosition): Int {
            val fa = fScore[a] ?: Double.POSITIVE_INFINITY
            val fb = fScore[b] ?: Double.POSITIVE_INFINITY
            if (fa > fb) return 1
            if (fa < fb) return -1
            return 0
        }))
        val closed = ArrayList<TilePosition>()
        open.add(start)
        gScore[start] = 0.0
        fScore[start] = start minDistTo end

        while (open.isNotEmpty()) {
            val current = open.poll()
            if (current == end) {
                var cur = end
                val list = ArrayList<TilePosition>()
                list.add(cur)
                while(true) {
                    cur = cameFrom[cur]?: return Path(list.toTypedArray(), map)
                    list.add(0, cur)
                }
            }
            closed.add(current)
            for (n in current.neighbors(map).filter{ map[it].isWalkable }) {
                if (n in closed) continue
                if (n !in open) open.add(n)
                val t = gScore[current]!! + (current distTo n)
                if (t >= gScore[n] ?: Double.POSITIVE_INFINITY) continue
                cameFrom[n] = current
                gScore[n] = t
                fScore[n] = t + (n minDistTo end)
            }
        }
        return null
    }

    companion object {

        @JvmStatic fun main(args: Array<String>) {
            val mg = MapGenerator()
            mg.generateAll()
            val p = PathFinder(mg.map)
            val start = TilePosition(0, 0)
            val end = TilePosition(mg.map.width - 1, mg.map.height - 1)
            val list = p.findPath(start, end)
            println(list)
            println(list?.ascii())
            println(list?.length())
        }
    }

}
