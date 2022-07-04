package chess

const val GAME_NAME = "Pawns-Only Chess"

object Chess {
    private lateinit var player1: Player
    private lateinit var player2: Player
    private val table = Table()

    private fun step(player: Player) {
        val color = if (player == player1) STATE.WHITE else STATE.BLACK
        while (true) {
            try {
                val stepCells = player.step()
                val stepFrom = table.getCell(stepCells.first)
                val stepTo = table.getCell(stepCells.second)
                if (stepFrom != null && stepTo != null) {
                    if (stepFrom.state != color) throw NoMyPawnAtPosition(
                        color,
                        "${('a' + stepFrom.col)}${SIZE - stepFrom.row}"
                    )
                    table.makeStep(
                        Step(stepFrom, stepTo)
                    )
                } else {
                    throw InvalidPositionException()
                }
                break
            } catch (e: InvalidPositionException) {
                println("Invalid Input")
            } catch (e: NoMyPawnAtPosition) {
                println(e.message)
            }
        }
        println(table.drawTable())
        when (table.checkGame(color)) {
            GAME_STATUS.WIN -> println("${if (player == player1) "WHITE" else "BLACK"} Wins!").also { return }
            GAME_STATUS.DRAW -> println("Stalemate!").also { return }
            else -> step((if (player == player1) player2 else player1))
        }
    }

    private fun getPlayersNames() {
        println("First Player's name:")
        player1 = Player(readln())
        println("Second Player's name:")
        player2 = Player(readln())
        println(table.drawTable())
    }

    fun initGame() = println(GAME_NAME).also { getPlayersNames() }.also { step(player1) }.also { println("Bye!") }
}