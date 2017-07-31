package de.gymdon.inf1315.game

import de.gymdon.inf1315.game.path.PathFinder
import de.gymdon.inf1315.game.tile.Tile
import de.gymdon.inf1315.game.tile.TileMap

import java.util.Random

const val FAIRNESS_FACTOR = 1.04

class MapGenerator @JvmOverloads constructor(var seed: Long = Random().nextLong()) {
    lateinit var map: TileMap
    lateinit var buildings: Array<Array<Building?>>
    var random: Random = Random(seed)
    internal var distancesLeft = 0.0
    internal var distancesRight = 0.0
    internal var fairness = 1.0
    internal var advantageLeft: Boolean = false
    val mapWidth = 48
    val mapHeight = 32
    private val mines = 4
    private val superiorMines = 1
    private val lakes = 2
    private val sandbanks = 3
    private val avgLakeSize = 20
    private val avgSandbankSize = 14
    private val averageSideWater = 5

    private fun resetAll() {

        seed = Random().nextLong()
        distancesLeft = 0.0
        distancesRight = 0.0
        fairness = 1.0

    }

    fun generateAll() {
        random.setSeed(seed)

        map = TileMap(mapWidth, mapHeight)
        // generateMapOutside();
        generateMapInside()
        generateMapGrassMargin()
        generateBuildings()
        makeWaterRandom()
        makeGrassRandom()
        makeSandRandom()

        if (fairness > FAIRNESS_FACTOR) {
            resetAll()
            generateAll()
        }
    }

    /**
     * This method is not finished yet. It creates the outline of the map
     */
    fun generateMapOutside() {

        for (i in 1..averageSideWater) {
            for (k in 1..i) {
                for (l in i downTo k) {
                    map[i - k, i - l] = Tile.water
                }
            }
        }
    }

    /**
     * This method will basically generate the map for the game.
     */
    fun generateMapInside() {

        val xLakes = IntArray(lakes)
        val yLakes = IntArray(lakes)
        var tries = 0
        for (i in 0..lakes - 1) {
            var xLake = random.nextInt(mapWidth - avgLakeSize) + avgLakeSize / 2
            var yLake = random.nextInt(mapHeight - avgLakeSize) + avgLakeSize / 2
            while (true) {
                var near = false
                for (j in 0..i - 1) {
                    val a = xLake - xLakes[j]
                    val b = yLake - yLakes[j]
                    near = near || (a * a + b * b < avgLakeSize * avgLakeSize)
                }
                if (!near || tries++ > 4)
                    break
                xLake = random.nextInt(mapWidth - avgLakeSize) + avgLakeSize / 2
                yLake = random.nextInt(mapHeight - avgLakeSize) + avgLakeSize / 2
            }
            xLakes[i] = xLake
            yLakes[i] = yLake
            var xDir: Int
            var yDir: Int

            for (j in 0..avgLakeSize / 4 - 1) {
                do {
                    xDir = random.nextInt(2) - 1
                    yDir = random.nextInt(2) - 1
                } while (xDir == 0 && yDir == 0)
                try {
                    var k = 0
                    while (k < random.nextGaussian() * avgLakeSize) {
                        map[xLake, yLake] = Tile.water
                        map[xLake, yLake + 1] = Tile.water
                        map[xLake, yLake - 1] = Tile.water
                        map[xLake + 1, yLake] = Tile.water
                        map[xLake - 1, yLake] = Tile.water
                        if (random.nextBoolean())
                            map[xLake + 1, yLake + 1] = Tile.water
                        if (random.nextBoolean())
                            map[xLake + 1, yLake - 1] = Tile.water
                        if (random.nextBoolean())
                            map[xLake - 1, yLake + 1] = Tile.water
                        if (random.nextBoolean())
                            map[xLake - 1, yLake - 1] = Tile.water
                        xLake += xDir
                        yLake += yDir
                        k++
                    }
                } catch (e: ArrayIndexOutOfBoundsException) {
                }

            }
        }

        val xSands = IntArray(sandbanks)
        val ySands = IntArray(sandbanks)
        tries = 0
        for (i in 0..sandbanks - 1) {
            var xSand = random.nextInt(mapWidth - avgSandbankSize) + avgSandbankSize / 2
            var ySand = random.nextInt(mapHeight - avgSandbankSize) + avgSandbankSize / 2
            while (true) {
                var near = false
                for (j in 0..i - 1) {
                    val a = xSand - xSands[j]
                    val b = ySand - ySands[j]
                    near = near or (a * a + b * b < avgSandbankSize * avgSandbankSize)
                }
                for (j in 0..i - 1) {
                    val a = xSand - xLakes[j]
                    val b = ySand - yLakes[j]
                    near = near or (a * a + b * b < avgSandbankSize * avgSandbankSize)
                }
                if (!near || tries++ > 4)
                    break
                xSand = random.nextInt(mapWidth - avgSandbankSize) + avgSandbankSize / 2
                ySand = random.nextInt(mapHeight - avgSandbankSize) + avgSandbankSize / 2
            }
            xSands[i] = xSand
            ySands[i] = ySand
            var xDir: Int
            var yDir: Int

            for (j in 0..avgSandbankSize / 4 - 1) {
                do {
                    xDir = random.nextInt(2) - 1
                    yDir = random.nextInt(2) - 1
                } while (xDir == 0 && yDir == 0)
                try {
                    var k = 0
                    while (k < random.nextGaussian() * avgSandbankSize) {
                        map[xSand, ySand] = Tile.sand
                        map[xSand, ySand + 1] = Tile.sand
                        map[xSand, ySand - 1] = Tile.sand
                        map[xSand + 1, ySand] = Tile.sand
                        map[xSand - 1, ySand] = Tile.sand
                        if (random.nextBoolean())
                            map[xSand + 1, ySand + 1] = Tile.sand
                        if (random.nextBoolean())
                            map[xSand + 1, ySand - 1] = Tile.sand
                        if (random.nextBoolean())
                            map[xSand - 1, ySand + 1] = Tile.sand
                        if (random.nextBoolean())
                            map[xSand - 1, ySand - 1] = Tile.sand
                        xSand += xDir
                        ySand += yDir
                        k++
                    }
                } catch (e: ArrayIndexOutOfBoundsException) {
                }

            }
        }
    }

    fun generateBuildings() {
        buildings = Array(mapWidth) { arrayOfNulls<Building>(mapHeight) }

        // Generate Castles
        val castleLeft = Castle(null, 1, mapHeight / 2 - 1)
        val castleRight = Castle(null, mapWidth - 3, mapHeight / 2 - 1)
        buildings[1][mapHeight / 2 - 1] = castleLeft
        buildings[mapWidth - 3][mapHeight / 2 - 1] = castleRight

        // Generate Mines

        run {
            var i = 0
            while (i < mines) {
                val xMine = (random.nextInt(mapWidth - 16) + 8)
                val yMine = (random.nextInt(mapHeight - 8) + 4)

                if (map[xMine, yMine] !== Tile.grass && map[xMine, yMine] !== Tile.grass2 || marginBuildings(xMine, yMine, 5)) {
                    i--
                } else {
                    val m = Mine(xMine, yMine)
                    m.superior = false
                    buildings[xMine][yMine] = m
                    distancesLeft += giveDistance(castleLeft, m)
                    distancesRight += giveDistance(castleRight, m)
                }
                i++
            }
        }

        // Calculate fairness things

        println(distancesLeft)
        println(distancesRight)
        fairness = distancesLeft / distancesRight
        if (fairness < 1) {
            fairness = 1 / fairness
            advantageLeft = true
        }
        println(fairness)
        println(advantageLeft)

        var tries = 0

        // Generate superiorMine(s)
        var i = 0
        while (i < superiorMines && tries < 50) {
            tries++

            var xSMine = mapWidth / 2

            if (!advantageLeft) {

                xSMine--

            }

            val ySMine = 5 + random.nextInt(mapHeight - 10)

            if (map[xSMine, ySMine] !== Tile.grass && map[xSMine, ySMine] !== Tile.grass2 || marginBuildings(xSMine, ySMine, 5)) {
                i--
            } else {
                val m = Mine(xSMine, ySMine)
                m.superior = true
                buildings[xSMine][ySMine] = m
            }
            i++
        }
    }

    private fun generateMapGrassMargin() {

        for (i in 0..mapWidth - 1) {

            map[i, 0] = Tile.grass
            map[i, mapHeight - 1] = Tile.grass

        }

        for (i in 0..mapHeight - 1) {

            map[0, i] = Tile.grass
            map[mapWidth - 1, i] = Tile.grass

        }

    }

    private fun makeWaterRandom() {

        for (i in 0..mapWidth - 1) {

            for (k in 0..mapHeight - 1) {

                if (map[i, k] === Tile.water) {

                    val generateTile = Random().nextBoolean()
                    if (generateTile)
                        map[i, k] = Tile.water2

                    if (map[i - 1, k] === Tile.grass || map[i - 1, k] === Tile.grass2)
                        map[i, k] = Tile.water // ersetzen mit Wasser�bergang
                    if (map[i + 1, k] === Tile.grass || map[i + 1, k] === Tile.grass2)
                        map[i, k] = Tile.water
                    if (map[i, k - 1] === Tile.grass || map[i, k - 1] === Tile.grass2)
                        map[i, k] = Tile.water
                    if (map[i, k + 1] === Tile.grass || map[i, k + 1] === Tile.grass2)
                        map[i, k] = Tile.water
                }

            }

        }

    }

    private fun makeGrassRandom() {

        for (i in 0..mapWidth - 1) {

            for (k in 0..mapHeight - 1) {

                if (map[i, k] === Tile.grass) {
                    val generateTile = Random().nextBoolean()
                    if (generateTile)
                        map[i, k] = Tile.grass2
                }

            }

        }

    }

    private fun makeSandRandom() {
        for (i in 0..mapWidth - 1) {
            for (k in 0..mapHeight - 1) {
                if (map[i, k] === Tile.sand) {
                    val generateTile = Random().nextBoolean()
                    if (generateTile)
                        map[i, k] = Tile.sand2
                }
            }
        }
    }

    private fun marginBuildings(x: Int, y: Int, m: Int): Boolean {
        for (dx in x - m..x + m - 1) {
            for (dy in y - m..y + m - 1) {
                if (dx > 0 && dy > 0 && buildings[dx][dy] != null) {
                    return true
                }
            }
        }
        return false
    }

    private fun marginWaterAndSand(x: Int, y: Int, m: Int): Boolean {
        for (i in x - m..x + m - 1) {
            for (k in y - m..y + m - 1) {
                if (i > 0 && k > 0 && map[i, k] !== Tile.grass || map[i, k] !== Tile.grass2)
                    return true
            }
        }
        return false

    }

    private fun giveDistance(castle: Building, mine: Building): Double {
        val pf = PathFinder(map)
        return pf.findPath(castle.x, castle.y, mine.x, mine.y)?.length() ?: Double.POSITIVE_INFINITY
    }
}
