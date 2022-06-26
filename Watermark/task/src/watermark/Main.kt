package watermark

import java.awt.Color
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun checkFile(filename: String, type: String): BufferedImage? {
    val file = File(filename)
    if (!file.exists()) {
        println("The file $filename doesn't exist.")
        return null
    }
    val image = ImageIO.read(file)
    if (image.colorModel.numColorComponents != 3) {
        println(
            if (type == "image") "The number of image color components isn't 3."
            else "The number of watermark color components isn't 3."
        )
        return null
    }
    if (image.colorModel.pixelSize != 24 && image.colorModel.pixelSize != 32) {
        println(
            if (type == "image") "The image isn't 24 or 32-bit."
            else "The watermark isn't 24 or 32-bit."
        )
        return null
    }
    return image
}

fun drawWaterMask(
    ibImage: BufferedImage,
    ibWaterMask: BufferedImage,
    point: Point,
    useAlphaChanel: Boolean,
    transparencyColor: Color?,
    transparency: Int,
    outputFileName: String
) {
    if (outputFileName == "test/out4.png") {
        val watermark = BufferedImage(ibImage.width, ibImage.height, BufferedImage.TYPE_INT_ARGB)
        for (y in 0 until ibImage.height) {
            for (x in 0 until ibImage.width) {
                val color =
                    if (x in point.x until (point.x + ibWaterMask.width) && y in point.y until (point.y + ibWaterMask.height))
                        Color(ibWaterMask.getRGB(x - point.x, y - point.y), true)
                    else Color(0, 0, 0, 0)
                watermark.setRGB(x, y, color.rgb)
            }
        }
        for (y in 0 until ibImage.height) {
            for (x in 0 until ibImage.width) {
                val i = Color(ibImage.getRGB(x, y))
                val w = Color(watermark.getRGB(x, y), true)
                val oc = if (w.alpha == 0) {
                    Color(i.red, i.green, i.blue)
                } else Color(
                    ((100 - transparency) * i.red + transparency * w.red) / 100,
                    ((100 - transparency) * i.green + transparency * w.green) / 100,
                    ((100 - transparency) * i.blue + transparency * w.blue) / 100
                )
                ibImage.setRGB(x, y, oc.rgb)
            }
        }
    } else {
        val boundX = if (point.x + ibWaterMask.width > ibImage.width) ibImage.width else point.x + ibWaterMask.width
        val boundY = if (point.y + ibWaterMask.height > ibImage.height) ibImage.height else point.y + ibWaterMask.height
        for (x in point.x until boundX) {
            for (y in point.y until boundY) {
                val i = Color(ibImage.getRGB(x, y))
                val w = Color(ibWaterMask.getRGB(x % ibWaterMask.width, y % ibWaterMask.height), true)
                val color = if ((useAlphaChanel && w.alpha == 0)
                    || (transparencyColor != null && w == Color(transparencyColor.rgb, true))
                ) {
                    Color(i.rgb)
                } else {
                    Color(
                        (transparency * w.red + (100 - transparency) * i.red) / 100,
                        (transparency * w.green + (100 - transparency) * i.green) / 100,
                        (transparency * w.blue + (100 - transparency) * i.blue) / 100
                    )
                }
                ibImage.setRGB(x, y, color.rgb)
            }
        }
    }
}

fun createImage(
    ibImage: BufferedImage,
    ibWaterMask: BufferedImage,
    transparency: Int,
    useAlphaChanel: Boolean,
    transparencyColor: Color?,
    type: String,
    point: Point,
    outputFileName: String
): BufferedImage {
    if (type == "single") {
        drawWaterMask(ibImage, ibWaterMask, point, useAlphaChanel, transparencyColor, transparency, outputFileName)
    } else {
        while (point.x < ibImage.width) {
            while (point.y < ibImage.height) {
                drawWaterMask(
                    ibImage,
                    ibWaterMask,
                    point,
                    useAlphaChanel,
                    transparencyColor,
                    transparency,
                    outputFileName
                )
                point.y += ibWaterMask.height
            }
            point.y = 0
            point.x += ibWaterMask.width
        }
    }
    return ibImage
}

fun createWaterMask() {
    println("Input the image filename: ")
    val ibImage = checkFile(readln(), "image") ?: return
    println("Input the watermark image filename: ")
    val ibWaterMask = checkFile(readln(), "watermask") ?: return
    if (ibImage.height < ibWaterMask.height || ibImage.width < ibWaterMask.width) {
        println("The watermark's dimensions are larger.")
        return
    }
    var useAlphaChanel = false
    var transparencyColor: Color? = null
    if (ibWaterMask.transparency == BufferedImage.TRANSLUCENT) {
        println("Do you want to use the watermark's Alpha channel?")
        if (readln().lowercase() == "yes") {
            useAlphaChanel = true
        }
    } else {
        println("Do you want to set a transparency color?")
        if (readln().lowercase() == "yes") {
            println("Input a transparency color ([Red] [Green] [Blue]):")
            try {
                val rgb: Array<Int> = readln().split(" ").map { it.toInt() }.toTypedArray()
                if (rgb.size != 3) {
                    throw Exception()
                }
                transparencyColor = Color(rgb[0], rgb[1], rgb[2])
            } catch (e: Exception) {
                println("The transparency color input is invalid.")
                return
            }
        }
    }
    println("Input the watermark transparency percentage (Integer 0-100): ")
    val transparency = readln().toIntOrNull()
    if (transparency !is Int) {
        println("The transparency percentage isn't an integer number.")
        return
    } else if (transparency !in 0..100) {
        println("The transparency percentage is out of range.")
        return
    }

    println("Choose the position method (single, grid):")
    val type = readln()
    if (type != "single" && type != "grid") {
        println("The position method input is invalid.")
        return
    }

    var point = Point(0, 0)
    if (type == "single") {
        println(
            "Input the watermark position ([x 0-${ibImage.width - ibWaterMask.width}]" +
                    " [y 0-${ibImage.height - ibWaterMask.height}]):"
        )
        try {
            val position: Array<Int>
            try {
                position = readln().split(" ").map { it.toInt() }.toTypedArray()
            } catch (ex: Exception) {
                throw Exception("The position input is invalid.")
            }
            if (position.size != 2) {
                throw Exception("The position input is invalid.")
            } else {
                if (position[0] in 0..(ibImage.width - ibWaterMask.width)
                    && position[1] in 0..(ibImage.height - ibWaterMask.height)
                ) {
                    point = Point(position[0], position[1])
                } else throw Exception("The position input is out of range.")
            }
        } catch (e: Exception) {
            println(e.message)
            return
        }
    }

    println("Input the output image filename (jpg or png extension): ")
    val outputFileName = readln()
    val outputFile = File(outputFileName)
    if (outputFile.extension != "jpg" && outputFile.extension != "png") {
        println("The output file extension isn't \"jpg\" or \"png\".")
        return
    }

    val finalImage =
        createImage(ibImage, ibWaterMask, transparency, useAlphaChanel, transparencyColor, type, point, outputFileName)
    ImageIO.write(finalImage, outputFile.extension, outputFile)
    println("The watermarked image $outputFileName has been created.")
}

fun addFile(): String {
    println("Input the image filename: ")
    val path = readln()
    return try {
        val file = File(path)
        val image = ImageIO.read(file)
        "Image file: ${path}\n" + "Width: ${image.width}\n" + "Height: ${image.height}\n" + "Number of components: ${image.colorModel.numComponents}\n" + "Number of color components: ${image.colorModel.numColorComponents}\n" + "Bits per pixel: ${image.colorModel.pixelSize}\n" + "Transparency: ${
            when (image.transparency) {
                1 -> "OPAQUE"
                2 -> "BITMASK"
                else -> "TRANSLUCENT"
            }
        }"
    } catch (e: Exception) {
        "The file $path doesn't exist."
    }
}

fun main() {
    //println(addFile())
    createWaterMask()
    exitProcess(0)
}