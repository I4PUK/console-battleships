package org.example

fun main() {
    val game = Game()
    game.init()
    game.setupPlayerShips()
    game.display()
}

class Game {
    private val verticalFieldName = arrayOf("А", "Б", "В", "Г", "Д", "Е", "Ж", "З", "И", "К")
    private val horizontalFieldName = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")

    var userField = Array<Array<Any>>(11) { Array<Any>(11) { 0 } }
    private var opponentField = Array<Array<Any>>(11) { Array<Any>(11) { 0 } }

    private var playerShips = arrayOf(
        Battleship(length = 4),
        Battleship(length = 3),
        Battleship(length = 2),
        Battleship(length = 2),
//        Battleship(length = 2),
//        Battleship(length = 1),
//        Battleship(length = 1),
//        Battleship(length = 1),
    )

    private var opponentShips = 10

    private var hasDamage: Boolean = false

    fun init() {
        for (i in 1..<userField.size) {
            userField[0][i] = verticalFieldName[i - 1]
            userField[i][0] = horizontalFieldName[i - 1]
            opponentField[0][i] = verticalFieldName[i - 1]
            opponentField[i][0] = horizontalFieldName[i - 1]
        }
    }

    fun display() {
        for (i in 0..<userField.size) {
            val stringToOut = StringBuilder()
            for (j in 0..<userField.size) {
                if (i == 0 && j == 0) {
                    stringToOut.append("   ")
                } else {
                    stringToOut.append(userField[i][j])
                    if (j == 0 && i < 10) {
                        stringToOut.append("  ")
                    } else {
                        stringToOut.append(" ")
                    }

                }
                if (j == userField.size - 1) {
                    println(stringToOut)
                    stringToOut.clear()
                }
            }
        }
    }

    fun setupPlayerShips() {
        for (ship in playerShips) {
            ship.setup(userField)
        }
    }

    fun setupOpponentShips() {
        for (ship in playerShips) {
            ship.setup(opponentField)
        }
    }

    fun shot(to: String) {
        val columnFieldName = to.elementAt(0).toString()
        val rowFieldName = to.substring(1)

        val horizontalCoordinate1 = verticalFieldName.indexOf(columnFieldName) + 1
        val verticalCoordinate1 = horizontalFieldName.indexOf(rowFieldName) + 1

        println("$columnFieldName $rowFieldName")
        println("$horizontalCoordinate1 $verticalCoordinate1")

        if (userField[verticalCoordinate1][horizontalCoordinate1] == 1) {
            hasDamage = true
            userField[verticalCoordinate1][horizontalCoordinate1] = 8
            val isKilled = checkIfKill(verticalCoordinate1, horizontalCoordinate1)
        } else {
            hasDamage = false
            userField[verticalCoordinate1][horizontalCoordinate1] = 2
        }
    }

    fun checkIfKill(x: Int, y: Int): Boolean {
        for (i in -1..1) {
            for (j in -1..1) {
                val field = userField[x + i][y + j]
                if (field == 1 && x != 0 && y != 0) {
                    return false
                }
            }
        }

        return true
    }
}

data class Position(
    val x: Int,
    val y: Int,
)

class Battleship(val length: Int) {
    private lateinit var axis: Orientation

    private fun generateStartPosition(): Position {
        val xPosition = (1..11).random()
        val yPosition = (1..11).random()

        return Position(x = xPosition, y = yPosition)
    }

    fun setup(field: Array<Array<Any>>) {
        val randomOrientation = (0..1).random()
        when (randomOrientation) {
            0 -> setupHorizontallyIn(field)
            1 -> setupVerticallyIn(field)
        }
    }

    private fun canLocateHorizontal(position: Position, field: Array<Array<Any>>): Boolean {
        val x = position.x
        val y = position.y

        if (x + length > 11 || x + length < 1) return false

        for (i in x..<x + length) {
            val fieldUnit = field[i][x]
            if (fieldUnit != 0) {
                return false
            }
        }

        return true
    }

    private fun canLocateVertical(position: Position, field: Array<Array<Any>>): Boolean {
        val y = position.y

        if (y + length > 11 || y + length < 1) return false

        for (i in y..<y + length) {
            val fieldUnit = field[y][i]
            if (fieldUnit != 0) {
                return false
            }
        }

        return true
    }

    private fun setSafeZoneHorizontal(position: Position, field: Array<Array<Any>>) {
        val x = position.x

        for (i in x..<x + length) {
            // не у левой границы
            if (x > 1) {
                field[x - 1][i] = 2
            }
            // не у правой границы
            if (x < 10) {
                field[x + 1][i] = 2
            }
            // не у верхней границы
            if (i > 1 && i == x) {
                field[x][i - 1] = 2
                if (x > 1) {
                    field[x - 1][i - 1] = 2
                }
                if (x < 10) {
                    field[x + 1][i - 1] = 2
                }
            }
            // не у нижней границы
            if (i < 10 && i + 1 == x + length) {
                field[x][i + 1] = 2
                if (x > 1) {
                    field[x - 1][i + 1] = 2
                }
                if (x < 10) {
                    field[x + 1][i + 1] = 2
                }
            }
            field[x][i] = 1
        }
    }

    private fun setSafeZoneVertically(position: Position, field: Array<Array<Any>>) {
        val x = position.x
        val y = position.y

        for (i in y..<y + length) {
            // не у левой границы
            if (y > 1) {
                field[i][y - 1] = 2
            }
            // не у правой границы
            if (y < 10) {
                field[i][y + 1] = 2
            }
            // не у верхней границы
            if (i > 1 && i == y) {
                field[i - 1][y] = 2
                if (y > 1) {
                    field[i - 1][y - 1] = 2
                }
                if (y < 10) {
                    field[i - 1][y + 1] = 2
                }
            }
            // не у нижней границы
            if (i < 10 && i + 1 == y + length) {
                field[i + 1][y] = 2
                if (x > 1) {
                    field[i + 1][y - 1] = 2
                }
                if (y < 10) {
                    field[i + 1][y + 1] = 2
                }
            }
            field[i][y] = 1
        }
    }

    private fun setupHorizontallyIn(field: Array<Array<Any>>) {
        var position = generateStartPosition()

        while (!canLocateHorizontal(position = position, field = field)) {
            position = generateStartPosition()
        }

        setSafeZoneHorizontal(position = position, field = field)
    }

    private fun setupVerticallyIn(field: Array<Array<Any>>) {
        var position = generateStartPosition()

        while (!canLocateVertical(position = position, field = field)) {
            position = generateStartPosition()
        }

        setSafeZoneVertically(position = position, field = field)
    }
}

enum class Orientation {
    horizontal,
    vertical
}