package seamcarving

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.pow
import kotlin.math.sqrt

class SeamCarver(srcName: String, private val destName: String, verticalShift: String, horizontalShift: String) {
    // who needs error checking?
    private var src = transposedImage(ImageIO.read(File(srcName)))
    private var width: Int = src.width
        get() = src.width
    private var height: Int = src.height
        get() = src.height
    private val verticalShift = verticalShift.toInt()
    private val horizontalShift = horizontalShift.toInt()

    // a matrix with the energy per node
    private var energy = Array(height) { Array(width) { 0.0 } }
    private var maxEnergy = 0.0

    // a matrix with the calculated Shortest Path Values
    private var spv = Array(height) { Array(width) { 0.0 } }

    // an array with the current vertical seam
    private var vSeam = Array(height) { 0 }

    // helper functions
    private fun bindX(x: Int, offset: Int = 0) = x.coerceIn(0 + offset, width - 1 - offset)
    private fun bindY(y: Int, offset: Int = 0) = y.coerceIn(0 + offset, height - 1 - offset)
    private fun color(x: Int, y: Int) = Color(src.getRGB(x, y))
    private fun pow2(i: Int) = i.toDouble().pow(2)
    private operator fun Color.minus(other: Color) =
        pow2(this.red - other.red) + pow2(this.green - other.green) + pow2(this.blue - other.blue)

    // calculate the energy for each pixel and fill the energy matrix
    private fun calculateEnergy() {
        for (x in 0 until width) {
            for (y in 0 until height) {
                // using bind-functions to coerce pixels on the border of the image to the inside of the border
                val dX = color(bindX(x, 1) - 1, y) - color(bindX(x, 1) + 1, y)
                val dY = color(x, bindY(y, 1) - 1) - color(x, bindY(y, 1) + 1)
                energy[y][x] = sqrt(dX + dY)

                maxEnergy = maxOf(energy[y][x], maxEnergy)
            }
        }
    }

    // calculate the shortest path values for each pixel and save them
    private fun findShortestPaths() {
        // energy values for the first row are identical to the original values
        spv[0] = energy[0]

        // for all other rows, for each pixel, new energy is equal to its own energy
        // plus the minimum of the three above, or two if we are at the horizontal border
        for (y in 1 until height) {
            for (x in 0 until width) {
                spv[y][x] = energy[y][x] + minOf(spv[y - 1][bindX(x - 1)], spv[y - 1][x], spv[y - 1][bindX(x + 1)])
            }
        }
    }

    private fun findVerticalSeam() {
        var x = spv[height - 1].indexOf(spv[height - 1].minOf { it })
        vSeam[height - 1] = x

        for (y in height - 2 downTo 0) {
            // using a map to preserve the indexes
            val map = mutableMapOf<Int, Double>(x to spv[y][x])
            if (x > 0) map[x - 1] = spv[y][x - 1]
            if (x < width - 1) map[x + 1] = spv[y][x + 1]

            vSeam[y] = map.minByOrNull { it.value }!!.key
            x = vSeam[y]
        }
    }

    private fun shiftImage() {
        val dest = BufferedImage(width, height - 1, BufferedImage.TYPE_INT_RGB)

        for (x in 0 until width) {
            var isShift = false
            for (y in 0 until height - 1) {
                if (vSeam[y] == x) isShift = true
                dest.setRGB(x, y, if (isShift) src.getRGB(x, y + 1) else src.getRGB(x, y))
            }
        }

        src = dest
    }

    private fun transposedImage(image: BufferedImage): BufferedImage {
        val transposedImage = BufferedImage(image.height, image.width, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until image.width) {
            for (y in 0 until image.height) {
                transposedImage.setRGB(y, x, image.getRGB(x, y))
            }
        }
        return transposedImage
    }

    private fun reboot() {
        energy = Array(height) { Array(width) { 0.0 } }
        maxEnergy = 0.0
        spv = Array(height) { Array(width) { 0.0 } }
        vSeam = Array(height) { 0 }
    }

    private fun transposeValues() {
        src = transposedImage(src)
        val temp = height
        height = width
        width = temp
    }

    fun run() {
        repeat(verticalShift) {
            reboot()
            calculateEnergy()
            findShortestPaths()
            findVerticalSeam()
            shiftImage()
        }
        transposeValues()
        repeat(horizontalShift) {
            reboot()
            calculateEnergy()
            findShortestPaths()
            findVerticalSeam()
            shiftImage()
        }
        ImageIO.write(src, "png", File(destName))
    }
}

fun main(args: Array<String>) = SeamCarver(args[1], args[3], args[5], args[7]).run()