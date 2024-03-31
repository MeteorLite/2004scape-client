package meteor;

import jagex2.client.GameShell;

import javax.swing.*;
import java.awt.*;

public class GameView extends JPanel {

    public final GameShell shell;

    public GameView( GameShell shell, int width, int height) {
        this.shell = shell;
        setSize(width, height);
        setMaximumSize(getSize());
    }

    @Override
    public Graphics getGraphics() {
        Graphics g = super.getGraphics();
        return g;
    }

    @Override
    public void update( Graphics g) {
        this.shell.update(g);
    }

    @Override
    public void paint( Graphics g) {
        this.shell.paint(g);
    }
}
