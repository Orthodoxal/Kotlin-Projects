package processor

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

class MatrixCalculator {
    private fun getMinor(matrix: Array<DoubleArray>, row: Int, col: Int): Array<DoubleArray> {
        val result = Array(matrix.lastIndex) { DoubleArray(matrix.lastIndex) { 0.0 } }
        var isShiftRow = 0
        for (i in result.indices) {
            if (i == row) isShiftRow = 1
            var isShiftCol = 0
            for (j in result.indices) {
                if (j == col) isShiftCol = 1
                result[i][j] = matrix[i + isShiftRow][j + isShiftCol]
            }
        }
        return result
    }

    fun calculateInverseMatrix(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val determinate = calculateDeterminate(matrix)
        if (determinate == 0.0) throw Exception("This matrix doesn't have an inverse.\n")
        val adjointMatrix = Array(matrix.size) { i ->
            DoubleArray(matrix.size) { j ->
                (-1.0).pow(i + j) * calculateDeterminate(
                    getMinor(
                        matrix,
                        i,
                        j
                    )
                )
            }
        }
        val result =
            multiplyMatrixByNumber(1 / determinate, transposeMatrix(adjointMatrix, TransposeMode.MAIN_DIAGONAL))
        for (i in result.indices) {
            for (j in result.indices) {
                result[i][j] =
                    if (result[i][j] == -0.0) 0.0 else if (result[i][j] > 0.0) floor(result[i][j] * 100) / 100 else (floor(
                        abs(result[i][j]) * 100
                    ) / 100) * -1
            }
        }
        return result
    }

    fun calculateDeterminate(matrix: Array<DoubleArray>): Double {
        if (matrix.size == 1) {
            return matrix[0][0]
        }
        if (matrix.size == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1]
        }
        var rowWithMoreZeros = Pair(0, 0)
        for (i in matrix.indices) {
            var amountZeros = 0
            for (j in matrix[0].indices) {
                if (matrix[i][j] == 0.0) {
                    amountZeros++
                }
            }
            if (rowWithMoreZeros.second < amountZeros) {
                rowWithMoreZeros = Pair(i, amountZeros)
            }
        }
        return matrix[rowWithMoreZeros.first].withIndex().sumOf {
            if (it.value == 0.0) 0.0
            else (-1.0).pow(rowWithMoreZeros.first + it.index) * calculateDeterminate(
                getMinor(
                    matrix,
                    rowWithMoreZeros.first,
                    it.index
                )
            ) * matrix[rowWithMoreZeros.first][it.index]
        }
    }

    fun transposeMatrix(
        matrix: Array<DoubleArray>,
        transposeMode: TransposeMode = TransposeMode.MAIN_DIAGONAL
    ): Array<DoubleArray> {
        return when (transposeMode) {
            TransposeMode.SIDE_DIAGONAL -> Array(matrix[0].size) { i -> DoubleArray(matrix.size) { j -> matrix[matrix[0].lastIndex - j][matrix.lastIndex - i] } }
            TransposeMode.VERTICAL_LINE -> Array(matrix[0].size) { i -> DoubleArray(matrix.size) { j -> matrix[i][matrix[0].lastIndex - j] } }
            TransposeMode.HORIZONTAL_LINE -> Array(matrix[0].size) { i -> DoubleArray(matrix.size) { j -> matrix[matrix.lastIndex - i][j] } }
            else -> Array(matrix[0].size) { i -> DoubleArray(matrix.size) { j -> matrix[j][i] } }
        }
    }

    fun multiplyMatrixByMatrix(matrix1: Array<DoubleArray>, matrix2: Array<DoubleArray>): Array<DoubleArray> {
        val transposedMatrix = transposeMatrix(matrix1)
        return Array(matrix1.size) { i ->
            DoubleArray(matrix2[0].size) { j ->
                var value = 0.0;
                for (index in matrix1[0].indices) {
                    value += transposedMatrix[index][i] * matrix2[index][j]
                }; value
            }
        }
    }

    fun multiplyMatrixByNumber(number: Double, matrix: Array<DoubleArray>) =
        Array(matrix.size) { i -> DoubleArray(matrix[0].size) { j -> number * matrix[i][j] } }

    fun addMatrix(matrix1: Array<DoubleArray>, matrix2: Array<DoubleArray>) =
        Array(matrix1.size) { i -> DoubleArray(matrix1[0].size) { j -> matrix1[i][j] + matrix2[i][j] } }
}