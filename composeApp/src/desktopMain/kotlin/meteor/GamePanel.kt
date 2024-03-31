package meteor

import java.awt.image.BufferedImage
import javax.swing.JPanel

/**
 * All drawing is done to the background of this, we should move to the BufferedImage for manipulation
 */
class GamePanel(private val w: Int, val h: Int) : JPanel() {
    val gameImage = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
}