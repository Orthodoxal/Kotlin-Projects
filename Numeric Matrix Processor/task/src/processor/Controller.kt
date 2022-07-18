package processor

import kotlin.system.exitProcess

object Controller {
    private val matrixCalculator = MatrixCalculator()

    private fun isDouble(matrix: Array<DoubleArray>): Boolean {
        matrix.forEach { row -> row.forEach { if (it - it.toInt() > 0.001) return true } }
        return false
    }

    private fun getMatrix(matrixNumber: String, anotherString: Boolean = false): Array<DoubleArray> {
        print(if (!anotherString) "Enter size of$matrixNumber matrix: " else "Enter matrix size: ")
        val size = readln().split(" ").map { it.toInt() }
        println("Enter$matrixNumber matrix:")
        return Array(size[0]) { readln().split(" ").map { it.toDouble() }.toDoubleArray() }
    }

    private fun matrixToString(matrix: Array<DoubleArray>) = "The result is:\n" + if (isDouble(matrix))
        matrix.joinToString("\n", postfix = "\n") { it.joinToString(" ") } else {
        val matrixInt = Array(matrix.size) { i -> Array(matrix[0].size) { j -> matrix[i][j].toInt() } }
        matrixInt.joinToString("\n", postfix = "\n") { it.joinToString(" ") }
    }

    private fun calculateInverseMatrix(): String {
        val matrix = getMatrix("", true)
        return try {
            val result = matrixCalculator.calculateInverseMatrix(matrix)
            matrixToString(result)
        } catch (e: Exception) {
            e.message ?: ""
        }
    }

    private fun calculateDeterminate(): String {
        val matrix = getMatrix("", true)
        val result = matrixCalculator.calculateDeterminate(matrix)
        return "The result is:\n" + (if (result - result.toInt() > 0.001) result else result.toInt()) + "\n"
    }

    private fun transposeMatrix(): String {
        print(TransposeMode.values().joinToString("", "\n", "Your choice: ") { "${it.transposeModeName}\n" })
        val choice = readln()
        val matrix = getMatrix("", true)
        val result = when (choice) {
            "2" -> matrixCalculator.transposeMatrix(matrix, TransposeMode.SIDE_DIAGONAL)
            "3" -> matrixCalculator.transposeMatrix(matrix, TransposeMode.VERTICAL_LINE)
            "4" -> matrixCalculator.transposeMatrix(matrix, TransposeMode.HORIZONTAL_LINE)
            else -> matrixCalculator.transposeMatrix(matrix, TransposeMode.MAIN_DIAGONAL)
        }
        return matrixToString(result)
    }

    private fun multiplyMatrixByMatrix(): String {
        val matrix1 = getMatrix(" first")
        val matrix2 = getMatrix(" second")
        if (matrix1[0].size != matrix2.size)
            return "The operation cannot be performed.\n"
        val result = matrixCalculator.multiplyMatrixByMatrix(matrix1, matrix2)
        return matrixToString(result)
    }

    private fun multiplyMatrixByNumber(): String {
        val matrix = getMatrix("")
        print("Enter constant: ")
        val number = readln().toDouble()
        val result = matrixCalculator.multiplyMatrixByNumber(number, matrix)
        return matrixToString(result)
    }

    private fun addMatrix(): String {
        val matrix1 = getMatrix(" first")
        val matrix2 = getMatrix(" second")
        if (matrix1.size != matrix2.size || matrix1[0].size != matrix2[0].size)
            return "The operation cannot be performed.\n"
        val result = matrixCalculator.addMatrix(matrix1, matrix2)
        return matrixToString(result)
    }

    fun start() {
        while (true) {
            print(Command.values().joinToString("", postfix = "Your choice: ") { "${it.commandName}\n" })
            println(
                when (readln()) {
                    "1" -> addMatrix()
                    "2" -> multiplyMatrixByNumber()
                    "3" -> multiplyMatrixByMatrix()
                    "4" -> transposeMatrix()
                    "5" -> calculateDeterminate()
                    "6" -> calculateInverseMatrix()
                    "0" -> exitProcess(1)
                    else -> continue
                }
            )
        }
    }
}