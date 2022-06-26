import java.awt.Color
import java.awt.image.BufferedImage

fun drawLines(): BufferedImage {
    val width = 200
    val height = 200

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.color = Color.RED
    graphics.drawLine(0, 0, 200, 200)
    graphics.color = Color.GREEN
    graphics.drawLine(200, 0, 0, 200)
    return image
}