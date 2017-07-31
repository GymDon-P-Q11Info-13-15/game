package de.gymdon.inf1315.game

class Castle(owner: Player?, x: Int, y: Int) : Building() {

    init {
        this.owner = owner
        this.x = x
        this.y = y
        this.hp = 10000
        this.defense = 80
        this.cost = 0
        this.income = 100
    }

    override fun occupy(p: Player) {

    }

    override val sizeX: Int
        get() = 2

    override val sizeY: Int
        get() = 2
}
