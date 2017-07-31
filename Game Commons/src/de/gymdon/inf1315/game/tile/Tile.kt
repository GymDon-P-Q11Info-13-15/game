package de.gymdon.inf1315.game.tile

data class Tile(val id: Int, val name: String, val groundFactor: Double = 1.0) {

    val isWalkable: Boolean
        get() = groundFactor != Double.POSITIVE_INFINITY

    companion object {
        val grass = Tile(0, "grass", 1.0)
        val grass2 = Tile(0, "grass", 1.0)
        val sand = Tile(1, "sand", 2.0)
        val sand2 = Tile(1, "sand", 2.0)
        val water = Tile(2, "water", Double.POSITIVE_INFINITY)
        val water2 = Tile(2, "water", Double.POSITIVE_INFINITY)
    }
}
