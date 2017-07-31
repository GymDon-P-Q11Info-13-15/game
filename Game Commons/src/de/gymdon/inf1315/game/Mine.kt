package de.gymdon.inf1315.game

class Mine(x: Int, y: Int) : Building() {

    var superior = true

    init {
        this.x = x
        this.y = y
        this.hp = 1
        this.cost = 0
        this.defense = 0
    }

    override fun occupy(p: Player) {
        this.owner = p
        this.hp = 5000
        this.defense = 40
        if (this.superior)
            this.income = 150
        else
            this.income = 50
    }

    override fun clicked(phase: Int): BooleanArray {
        options[5] = false

        return options
    }
}
