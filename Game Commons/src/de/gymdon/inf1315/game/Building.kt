package de.gymdon.inf1315.game

abstract class Building : GameObject() {
    var income: Int = 0
    override var options = booleanArrayOf(false, false, false, false, false, true, false)

    abstract fun occupy(p: Player)

    override fun clicked(phase: Int): BooleanArray {
        options[5] = phase == 0

        return options
    }
}
