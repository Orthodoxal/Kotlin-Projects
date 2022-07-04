package chess

class NoMyPawnAtPosition(color: STATE, position: String) :
    Exception("No ${if (color == STATE.WHITE) "white" else "black"} pawn at $position") {
}