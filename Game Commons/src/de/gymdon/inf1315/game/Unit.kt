package de.gymdon.inf1315.game

abstract class Unit : GameObject() {
    var act_speed: Int = 0
    var attacked = false
    var speed: Int = 0
    var attack: Int = 0
    var range: Int = 0
    var combined: Double = 0.toDouble()

    override var options = booleanArrayOf(true, true, true, false, false, false, false)

    fun reset() {
        act_speed = speed
        attacked = false
    }

    override fun clicked(phase: Int): BooleanArray {
        options[0] = !(attacked || phase != 2)
        options[1] = !(act_speed <= 0 || phase != 1)
        options[2] = !(hp > 100 || phase != 1)

        return options
    }
}
