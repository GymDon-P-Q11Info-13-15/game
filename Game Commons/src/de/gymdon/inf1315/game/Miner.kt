package de.gymdon.inf1315.game

class Miner(owner: Player, x: Int, y: Int) : Unit() {

    init {
        this.owner = owner
        this.x = x
        this.y = y
        speed = 6
        range = 1
        attack = 10
        defense = 5
        hp = 100
        cost = 20
        combined = 0.01
        super.reset()
    }

    override fun clicked(phase: Int): BooleanArray {
        options[0] = phase == 2
        options[1] = !(act_speed == 0 || phase != 1)
        options[2] = !(hp <= 100 || phase != 1)
        options[3] = phase == 1

        return options
    }
}
