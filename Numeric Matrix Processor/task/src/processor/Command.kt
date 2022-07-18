package processor

enum class Command(val commandName: String) {
    ADD_MAT("1. Add matrices"),
    MUL_MAT_BY_CONST("2. Multiply matrix by a constant"),
    MUL_MAT_BY_MAT("3. Multiply matrices"),
    TRANSPOSE("4. Transpose matrix"),
    DETERMINATE("5. Calculate a determinant"),
    INVERSE_MAT("6. Inverse matrix"),
    EXIT("0. Exit"),
}

enum class TransposeMode(val transposeModeName: String) {
    MAIN_DIAGONAL("1. Main diagonal"),
    SIDE_DIAGONAL("2. Side diagonal"),
    VERTICAL_LINE("3. Vertical line"),
    HORIZONTAL_LINE("4. Horizontal line"),
}