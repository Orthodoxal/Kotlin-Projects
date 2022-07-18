package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt


object Controller {
    private lateinit var energyMatrix: Array<Array<Double>>
    private lateinit var image: BufferedImage
    private var maxEnergy = Double.MIN_VALUE

    private fun getDiff(colorFirst: Color, colorSecond: Color): Double {
        val redPow = (colorFirst.red - colorSecond.red).toDouble().pow(2)
        val greenPow = (colorFirst.green - colorSecond.green).toDouble().pow(2)
        val bluePow = (colorFirst.blue - colorSecond.blue).toDouble().pow(2)
        return redPow + greenPow + bluePow
    }

    private fun getDiffX(p: Pair<Int, Int>): Double {
        val colorLeft = Color(image.getRGB(p.first - 1, p.second))
        val colorRight = Color(image.getRGB(p.first + 1, p.second))
        return getDiff(colorLeft, colorRight)
    }

    private fun getDiffY(p: Pair<Int, Int>): Double {
        val colorUp = Color(image.getRGB(p.first, p.second - 1))
        val colorDown = Color(image.getRGB(p.first, p.second + 1))
        return getDiff(colorUp, colorDown)
    }

    private fun setPixelsEnergy() {
        for (x in energyMatrix.indices) {
            for (y in energyMatrix[x].indices) {
                val pointX = Pair(if (x == 0) 1 else if (x == image.width - 1) x - 1 else x, y)
                val pointY = Pair(x, if (y == 0) 1 else if (y == image.height - 1) y - 1 else y)
                val diffX = getDiffX(pointX)
                val diffY = getDiffY(pointY)
                energyMatrix[x][y] = sqrt(diffX + diffY)
                if (energyMatrix[x][y] > maxEnergy) maxEnergy = energyMatrix[x][y]
            }
        }
    }

    private fun getImage(seem: List<Pair<Int, Int>>) {
        for (pixel in seem) {
            image.setRGB(pixel.first, pixel.second, Color.RED.rgb)
        }
        /*for (x in energyMatrix.indices) {
            for (y in energyMatrix[x].indices) {
                val intensity = (255.0 * energyMatrix[x][y] / maxEnergy).toInt()
                image.setRGB(x, y, Color(intensity, intensity, intensity).rgb)
            }
        }*/
    }

    fun start(args: Array<String>) {
        val file = File(args[1])
        image = ImageIO.read(file)
        energyMatrix = Array(image.width) { Array(image.height) { 0.0 } }
        setPixelsEnergy()
        val computingSeem = ComputingSeem(energyMatrix)
        getImage(computingSeem.getSeem())
        val fileOut = File(args[3])
        ImageIO.write(image, fileOut.extension, fileOut)
    }
}