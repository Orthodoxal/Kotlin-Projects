package chess

import java.util.*
import kotlin.math.abs

const val SIZE = 8
const val upDownBorder = "  +---+---+---+---+---+---+---+---+\n"

class Table {
    private val table: Array<Array<Cell>> = Array(SIZE) { row ->
        Array(SIZE) { col ->
            when (row) {
                1 -> Cell(row, col, STATE.BLACK)
                SIZE - 2 -> Cell(row, col, STATE.WHITE)
                else -> Cell(row, col, STATE.EMPTY)
            }
        }
    }
    private val stepsHistory = Stack<Step>()

    private fun changePosition(
        step: Step
    ) {
        step.cellTo.state = step.cellFrom.state
        step.cellFrom.state = STATE.EMPTY
    }

    private fun getNextRow(state: STATE) = if (state == STATE.BLACK) 1 else -1

    private fun oppositePosition(cell: Cell, state: STATE): Boolean {
        val oppositeColor = if (state == STATE.BLACK) STATE.WHITE else STATE.BLACK
        return table[cell.row][cell.col].state == oppositeColor
    }

    private fun checkKill(step: Step): Boolean {
        val nextRow = getNextRow(step.cellFrom.state)
        return step.cellFrom.row + nextRow == step.cellTo.row
                && (step.cellFrom.col - 1 == step.cellTo.col
                || step.cellFrom.col + 1 == step.cellTo.col)
    }

    private fun checkForward(step: Step) = step.cellFrom.row + getNextRow(step.cellFrom.state) == step.cellTo.row

    private fun checkDoubleForward(step: Step) =
        step.cellFrom.row + getNextRow(step.cellFrom.state) * 2 == step.cellTo.row
                && table[step.cellFrom.row + getNextRow(step.cellFrom.state)][step.cellFrom.col].state == STATE.EMPTY
                && if (step.cellFrom.state == STATE.BLACK) step.cellFrom.row == 1 else step.cellFrom.row == SIZE - 2

    private fun checkEnPassant(step: Step) =
        stepsHistory.isNotEmpty()
                && abs(stepsHistory.peek().cellTo.row - stepsHistory.peek().cellFrom.row) == 2
                && (if (stepsHistory.peek().cellTo.state == STATE.BLACK)
            stepsHistory.peek().cellFrom.row == 1 else stepsHistory.peek().cellFrom.row == SIZE - 2)
                && (stepsHistory.peek().cellTo.col == step.cellFrom.col + 1 || stepsHistory.peek().cellTo.col == step.cellFrom.col - 1)
                && stepsHistory.peek().cellTo.row == step.cellFrom.row

    private fun canMakeStep(cell: Cell): Boolean {
        val nextRow = getNextRow(cell.state)
        // diagonal kill || En Passant
        var possibleCell = getCell(Cell(cell.row + nextRow, cell.col + 1))
        if (possibleCell != null
            && ((oppositePosition(possibleCell, cell.state) && checkKill(Step(cell, possibleCell)))
                    || (possibleCell.state == STATE.EMPTY && checkEnPassant(Step(cell, possibleCell))))
        ) return true
        possibleCell = getCell(Cell(cell.row + nextRow, cell.col - 1))
        if (possibleCell != null
            && ((oppositePosition(possibleCell, cell.state) && checkKill(Step(cell, possibleCell)))
                    || (possibleCell.state == STATE.EMPTY && checkEnPassant(Step(cell, possibleCell))))
        ) return true
        // move forward
        possibleCell = getCell(Cell(cell.row + nextRow, cell.col))
        if (possibleCell != null
            && possibleCell.state == STATE.EMPTY
            && cell.col == possibleCell.col
            && checkForward(Step(cell, possibleCell))
        ) return true
        // move double forward
        possibleCell = getCell(Cell(cell.row + nextRow * 2, cell.col))
        if (possibleCell != null
            && possibleCell.state == STATE.EMPTY
            && cell.col == possibleCell.col
            && checkDoubleForward(Step(cell, possibleCell))
        ) return true
        return false
    }

    fun checkGame(color: STATE): GAME_STATUS {
        //WIN
        table[0].forEach { cell ->
            if (cell.state == STATE.WHITE) {
                return GAME_STATUS.WIN
            }
        }
        table[SIZE - 1].forEach { cell ->
            if (cell.state == STATE.BLACK) {
                return GAME_STATUS.WIN
            }
        }
        val nextPlayer = if (color == STATE.BLACK) STATE.WHITE else STATE.BLACK
        val cells = mutableListOf<Cell>()
        table.forEach { it.forEach { if (it.state == nextPlayer) cells.add(it) } }
        if (cells.isEmpty()) return GAME_STATUS.WIN
        //CONTINUE
        cells.forEach { if (canMakeStep(it)) return GAME_STATUS.CONTINUE }
        //DRAW
        return GAME_STATUS.DRAW
    }

    fun makeStep(step: Step) {
        // diagonal kill
        if (oppositePosition(step.cellTo, step.cellFrom.state) && checkKill(step)) {
            changePosition(step).also { stepsHistory.add(step) }.also { return }
        }
        if (step.cellTo.state == STATE.EMPTY) {
            // step forward
            if (step.cellFrom.col == step.cellTo.col
                && (checkForward(step) || checkDoubleForward(step))
            ) {
                changePosition(step).also { stepsHistory.add(step) }.also { return }
            }
            // en passant
            if (checkEnPassant(step)) {
                changePosition(step)
                    .also { stepsHistory.peek().cellTo.state = STATE.EMPTY }
                    .also { stepsHistory.add(step) }
                    .also { return }
            }
        }
        throw InvalidPositionException()
    }

    fun getCell(cellParams: Cell): Cell? {
        return try {
            table[cellParams.row][cellParams.col]
        } catch (e: Exception) {
            null
        }
    }

    fun drawTable(): String {
        var output = ""
        var rowNumber = SIZE
        for (row in table) {
            output += "$upDownBorder$rowNumber "
            for (cell in row) {
                output += "| ${
                    when (cell.state) {
                        STATE.WHITE -> "W"
                        STATE.BLACK -> "B"
                        else -> " "
                    }
                } "
            }
            output += "|\n"
            rowNumber--
        }
        var letterLine = ""
        for (letter in 'a' until 'a' + SIZE) letterLine += "   $letter"
        output += "$upDownBorder $letterLine\n"
        return output
    }
}