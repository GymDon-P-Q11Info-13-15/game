package de.gymdon.inf1315.game

class Barracks(owner: Player, x: Int, y: Int) : Building() {

    init {
        this.owner = owner
        this.x = x
        this.y = y
        this.hp = 10000
        this.defense = 80
        this.cost = 300
    }

    override fun occupy(p: Player) {

    }
}
