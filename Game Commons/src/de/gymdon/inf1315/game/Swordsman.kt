package de.gymdon.inf1315.game

class Swordsman(owner: Player, x: Int, y: Int) : Unit() {

    init {
        this.owner = owner
        this.x = x
        this.y = y
        speed = 5
        range = 1
        attack = 40
        defense = 70
        hp = 110
        cost = 130
        combined = 0.1
        super.reset()
    }
}
