import java.awt.Color
import java.awt.image.BufferedImage

fun drawStrings(): BufferedImage {
    val width = 200
    val height = 200
    val string = "Hello, images!"

    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics = image.createGraphics()
    graphics.color = Color.RED
    graphics.drawString(string, 50, 50)
    graphics.color = Color.GREEN
    graphics.drawString(string, 51, 51)
    graphics.color = Color.BLUE
    graphics.drawString(string, 52, 52)
    return image
}