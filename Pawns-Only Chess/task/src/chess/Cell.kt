package chess

data class Cell(val row: Int, val col: Int, var state: STATE = STATE.PLAYER_STEP)