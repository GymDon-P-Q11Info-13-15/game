package de.gymdon.inf1315.game

import java.awt.*

class Player {
    var gold = 500
    var color: PColor? = null

    enum class PColor constructor(val color: Color) {
        RED(Color.RED), BLUE(Color.BLUE)
    }
}
