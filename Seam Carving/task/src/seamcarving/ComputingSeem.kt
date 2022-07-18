package seamcarving

import java.lang.Double.min
import java.util.Stack

// dynamic programming
class ComputingSeem(private val energyMatrix: Array<Array<Double>>) {
    private val weightMatrix: Array<Array<Double>> =
        Array(energyMatrix.size) { x -> Array(energyMatrix[0].size) { y -> if (y == 0) energyMatrix[x][y] else Double.POSITIVE_INFINITY } }

    private fun calculatePixelWeight(x: Int, y: Int): Double {
        var weight = energyMatrix[x][y-1]
        if (x != 0) {
            weight = min(weight, energyMatrix[x - 1][y-1])
        }
        if (x != energyMatrix.lastIndex) {
            weight = min(weight, energyMatrix[x + 1][y-1])
        }
        return weight
    }

    private fun calculateWeights() {
        for (y in 1 until energyMatrix[0].size) {
            for (x in energyMatrix.indices) {
                weightMatrix[x][y] = calculatePixelWeight(x, y)
            }
        }
    }

    fun getSeem(): Stack<Pair<Int, Int>> {
        val seemPixels = Stack<Pair<Int, Int>>()
        calculateWeights()
        var minimumInRow = Double.POSITIVE_INFINITY
        var pixelCoordinates = Pair(-1, -1)
        val row = energyMatrix[0].lastIndex
        for (x in energyMatrix.indices) {
            if (energyMatrix[x][row] < minimumInRow) {
                minimumInRow = energyMatrix[x][row]
                pixelCoordinates = Pair(x, row)
            }
        }
        seemPixels.add(pixelCoordinates)
        while (seemPixels.peek().second != 0) {
            val x = seemPixels.peek().first
            val y = seemPixels.peek().second - 1
            pixelCoordinates = Pair(x, y)
            if (x != 0 && energyMatrix[x-1][y] < energyMatrix[pixelCoordinates.first][pixelCoordinates.second]) {
                pixelCoordinates = Pair(x - 1, y)
            }
            if (x != energyMatrix.lastIndex && energyMatrix[x+1][y] < energyMatrix[pixelCoordinates.first][pixelCoordinates.second]) {
                pixelCoordinates = Pair(x + 1, y)
            }
            seemPixels.add(pixelCoordinates)
        }
        return seemPixels
    }
}