package jagex2.client;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

import java.awt.*;

public class ViewBox extends Frame {

	private final GameShell shell;

	public Insets insets;

	public ViewBox( GameShell shell, int width, int height) {
		this.shell = shell;
		this.setTitle("Jagex");
		this.setResizable(false);
		this.show();
		this.toFront();
		this.insets = this.getInsets();
		this.resize(width + this.insets.left + this.insets.bottom, height + this.insets.top + this.insets.bottom);
	}

	@Override
	public Graphics getGraphics() {
		Graphics g = super.getGraphics();
        if (this.insets != null) {
		    g.translate(this.insets.left, this.insets.top);
        }
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
