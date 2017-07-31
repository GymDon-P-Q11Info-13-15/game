package de.gymdon.inf1315.game.tile

data class TileMap(val width: Int, val height: Int) {
    val tiles: Array<Array<Tile>> = Array(width) { _ -> Array(height) { _ -> Tile.grass} }

    operator fun get(pos: TilePosition): Tile = tiles[pos.x][pos.y]
    operator fun get(x: Int, y: Int): Tile = tiles[x][y]

    operator fun set(pos: TilePosition, tile: Tile) {
        set(pos.x, pos.y, tile)
    }

    operator fun set(x: Int, y: Int, tile: Tile) {
        tiles[x][y] = tile
    }

    fun isValid(pos: TilePosition): Boolean = pos.x >= 0 && pos.y >= 0 && pos.x < width && pos.y < height
}