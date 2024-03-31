package meteor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import jagex2.client.Client
import jagex2.client.Configuration
import jagex2.client.ViewBox
import sign.signlink
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.WindowListener
import java.awt.image.BufferedImage
import javax.swing.JPanel


object Main {

    init {
        System.setProperty("compose.interop.blending", "true")
    }

    lateinit var client: Client

    lateinit var composeWindow: ComposeWindow

    /**
     * Hello World!
     */
    @JvmStatic
    fun main(args: Array<String>) = application {
            Window(onCloseRequest = ::exitApplication, title = "Meteor", state = WindowState(size = DpSize(802.dp, 568.dp))) {
                composeWindow = this.window
                this.window.isResizable = false
                initRS2()

                composeWindow.addWindowListener(client)

                SwingPanel(background = Color.Black, factory = { Client.gamePanel }, modifier = Modifier.fillMaxSize())
                Text("Meteor 2.0.0", color = Color.Cyan, fontSize = 8.sp, modifier = Modifier.fillMaxSize())
            }
    }

    private fun initRS2() {
        //Common init

        client = Client()
        client.preInit()

        //Desktop init
        Client.gamePanel = GamePanel(789, 531)
        Client.gamePanel.size = Dimension(789, 531)
        client.initApplication(789, 531)
        Client.gamePanel.background = java.awt.Color.BLACK
        Client.gamePanel.addMouseListener(client)
        Client.gamePanel.addMouseListener(FocusGrabber)
        Client.gamePanel.addMouseMotionListener(client)
        Client.gamePanel.addKeyListener(client)
        Client.gamePanel.addFocusListener(client)
        Client.gamePanel.isFocusable = true
        Client.gamePanel?.requestFocus()
    }
}
