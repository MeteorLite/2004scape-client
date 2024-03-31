package meteor

import jagex2.client.Client
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

/**
 * This is necessary for now because KeyListeners do not report events unless it's component has focus.
 * There is a bug with compose that forces focus away from the jpanel even if it is the top level component
 *
 * We remedy this by simply requesting focus after an interaction that would cause focus to be lost.
 */
object FocusGrabber : MouseListener {
    override fun mouseClicked(e: MouseEvent?) {
        Client.gamePanel.requestFocus()
    }

    override fun mousePressed(e: MouseEvent?) {
        Client.gamePanel.requestFocus()
    }

    override fun mouseReleased(e: MouseEvent?) {
        Client.gamePanel.requestFocus()
    }

    override fun mouseEntered(e: MouseEvent?) {

    }

    override fun mouseExited(e: MouseEvent?) {

    }
}