package chess

import kotlin.system.exitProcess

class Player(private val name: String) {
    private fun checkPosition(input: String): Pair<Cell, Cell> {
        val regex = Regex("[a-${'a' + SIZE - 1}][1-${SIZE}][a-${'a' + SIZE - 1}][1-${SIZE}]")
        if (!regex.matches(input)) {
            throw InvalidPositionException()
        }
        val colFrom = input[0] - 'a'
        val colTo = input.findLast { it.isLetter() }?.minus('a')
        val rowFrom = SIZE - input.substring(1, input.indexOfLast { it.isLetter() }).toInt()
        val rowTo = SIZE - input.substring(input.indexOfLast { it.isLetter() } + 1, input.length).toInt()
        return Pair(Cell(rowFrom, colFrom), Cell(rowTo, colTo!!))
    }

    fun step(): Pair<Cell, Cell> {
        println("$name's turn:")
        return when (val input = readln()) {
            "exit" -> {
                println("Bye!")
                exitProcess(1)
            }
            else -> checkPosition(input)
        }
    }
}