import java.awt.Color
import java.awt.image.BufferedImage  

fun drawSquare(): BufferedImage {
    val width = 500
    val height = 500

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.color = Color.RED
    graphics.drawRect(100, 100, 300, 300)
    return image
}