package jagex2.client;

import jagex2.graphics.Pix24;
import jagex2.graphics.PixMap;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GameShell extends Applet implements Runnable, MouseListener, MouseMotionListener, KeyListener, FocusListener, WindowListener {

	private int state;

	private int deltime = 20;

	protected int mindel = 1;

	private final long[] otim = new long[10];

	public int fps;

	protected int screenWidth;

	protected int screenHeight;

	protected Graphics graphics;

	protected PixMap drawArea;

	private final Pix24[] temp = new Pix24[6];

	public ViewBox frame;

	private boolean refresh = true;

	protected int idleCycles;

	protected int mouseButton;

	protected int mouseX;

	protected int mouseY;

	protected int mouseClickButton;

	protected int mouseClickX;

	protected int mouseClickY;

	protected final int[] actionKey = new int[128];

	private final int[] keyQueue = new int[128];

	private int keyQueueReadPos;

	private int keyQueueWritePos;

	public static JPanel gamePanel;

	public final void initApplication(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
		this.frame = new ViewBox(this, width, height);
		if (Client.vanilla)
			this.graphics = this.getBaseComponent().getGraphics();
		else {
			this.frame.add(gamePanel);
			this.graphics = gamePanel.getGraphics();
		}
		this.drawArea = new PixMap(this.getBaseComponent(), this.screenWidth, this.screenHeight);
		this.startThread(this, 1);
	}

	protected final void initApplet(int width, int height) {
		this.screenWidth = width;
		this.screenHeight = height;
		this.drawArea = new PixMap(this.getBaseComponent(), this.screenWidth, this.screenHeight);
		this.startThread(this, 1);
	}

	public void run() {
		this.getBaseComponent().addMouseListener(this);
		this.getBaseComponent().addMouseMotionListener(this);
		this.getBaseComponent().addKeyListener(this);
		this.getBaseComponent().addFocusListener(this);

		if (this.frame != null) {
			this.frame.addWindowListener(this);
		}

		this.drawProgress("Loading...", 0);
		this.load();

		int opos = 0;
		int ratio = 256;
		int delta = 1;
		int count = 0;

		for ( int i = 0; i < 10; i++) {
			this.otim[i] = System.currentTimeMillis();
		}

		long last = System.currentTimeMillis();
		while (this.state >= 0) {
			if (this.state > 0) {
				this.state--;

				if (this.state == 0) {
					this.shutdown();
					return;
				}
			}

			int lastRatio = ratio;
			int lastDelta = delta;
			ratio = 300;
			delta = 1;
			last = System.currentTimeMillis();

			if (this.otim[opos] == 0L) {
				ratio = lastRatio;
				delta = lastDelta;
			} else if (last > this.otim[opos]) {
				ratio = (int) ((this.deltime * 2560L) / (last - this.otim[opos]));
			}

			if (ratio < 25) {
				ratio = 25;
			} else if (ratio > 256) {
				ratio = 256;
				delta = (int) ((long) this.deltime - (last - this.otim[opos]) / 10L);
			}

			this.otim[opos] = last;
			opos = (opos + 1) % 10;

			if (delta > 1) {
				for ( int i = 0; i < 10; i++) {
					if (this.otim[i] != 0L) {
						this.otim[i] += delta;
					}
				}
			}

			if (delta < this.mindel) {
				delta = this.mindel;
			}

			try {
				Thread.sleep(delta);
			} catch ( InterruptedException ignored) {
			}

			while (count < 256) {
				this.update();
				this.mouseClickButton = 0;
				this.keyQueueReadPos = this.keyQueueWritePos;
				count += ratio;
			}

			count &= 0xFF;

			if (this.deltime > 0) {
				this.fps = ratio * 1000 / (this.deltime * 256);
			}

			this.draw();
		}

		if (this.state == -1) {
			this.shutdown();
		}
	}

	private void shutdown() {
		this.state = -2;
		this.unload();

		if (this.frame != null) {
			try {
				System.exit(0);
			} catch ( Throwable ignored) {
			}
		}
	}

	protected final void setFramerate( int fps) {
		this.deltime = 1000 / fps;
	}

	@Override
	public final void start() {
		if (this.state >= 0) {
			this.state = 0;
		}
	}

	@Override
	public final void stop() {
		if (this.state >= 0) {
			this.state = 4000 / this.deltime;
		}
	}

	@Override
	public final void destroy() {
		this.state = -1;

		try {
			Thread.sleep(1000L);
		} catch ( Exception ignored) {
		}

		if (this.state == -1) {
			this.shutdown();
		}
	}

	@Override
	public final void update( Graphics g) {
		if (this.graphics == null) {
			if (Client.vanilla)
				this.graphics = this.frame.getGraphics();
			else
				this.graphics = gamePanel.getGraphics();
		}

		this.refresh = true;
		this.refresh();
	}

	@Override
	public final void paint( Graphics g) {
		if (this.graphics == null) {
			if (Client.vanilla)
				this.graphics = this.frame.getGraphics();
			else
				this.graphics = gamePanel.getGraphics();
		}

		this.refresh = true;
		this.refresh();
	}

	@Override
	public final void mousePressed( MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		this.idleCycles = 0;
		this.mouseClickX = x;
		this.mouseClickY = y;

		try {
			if ((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0) {
				this.mouseClickButton = 2;
				this.mouseButton = 2;
			} else if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
				this.mouseClickButton = 1;
				this.mouseButton = 1;
			}

			if (InputTracking.enabled) {
				InputTracking.mousePressed(x, y, (e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0 ? 1 : 0);
			}
		} catch (NoSuchMethodError ex) {
			if (e.isMetaDown()) {
				this.mouseClickButton = 2;
				this.mouseButton = 2;
			} else {
				this.mouseClickButton = 1;
				this.mouseButton = 1;
			}

			if (InputTracking.enabled) {
				InputTracking.mousePressed(x, y, e.isMetaDown() ? 1 : 0);
			}
		}
	}

	@Override
	public final void mouseReleased( MouseEvent e) {
		this.idleCycles = 0;
		this.mouseButton = 0;

		try {
			if (InputTracking.enabled) {
				InputTracking.mouseReleased((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) != 0 ? 1 : 0);
			}
		} catch (NoSuchMethodError ex) {
			if (InputTracking.enabled) {
				InputTracking.mouseReleased(e.isMetaDown() ? 1 : 0);
			}
		}
	}

	@Override
	public final void mouseClicked( MouseEvent e) {
	}

	@Override
	public final void mouseEntered( MouseEvent e) {
		if (InputTracking.enabled) {
			InputTracking.mouseEntered();
		}
	}

	@Override
	public final void mouseExited( MouseEvent e) {
		if (InputTracking.enabled) {
			InputTracking.mouseExited();
		}
	}

	@Override
	public final void mouseDragged( MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		this.idleCycles = 0;
		this.mouseX = x;
		this.mouseY = y;

		if (InputTracking.enabled) {
			InputTracking.mouseMoved(x, y);
		}
	}

	@Override
	public final void mouseMoved( MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		this.idleCycles = 0;
		this.mouseX = x;
		this.mouseY = y;

		if (InputTracking.enabled) {
			InputTracking.mouseMoved(x, y);
		}
	}

	@Override
	public final void keyPressed( KeyEvent e) {
		this.idleCycles = 0;

		int code = e.getKeyCode();
		int ch = e.getKeyChar();

		if (ch < 30) {
			ch = 0;
		}

		if (code == 37) {
			// KEY_LEFT
			ch = 1;
		} else if (code == 39) {
			// KEY_RIGHT
			ch = 2;
		} else if (code == 38) {
			// KEY_UP
			ch = 3;
		} else if (code == 40) {
			// KEY_DOWN
			ch = 4;
		} else if (code == 17) {
			// CONTROL
			ch = 5;
		} else if (code == 16) {
			// SHIFT
			ch = 6; // (custom)
		} else if (code == 18) {
			// ALT
			ch = 7; // (custom)
		} else if (code == 8) {
			// BACKSPACE
			ch = 8;
		} else if (code == 127) {
			// DELETE
			ch = 8;
		} else if (code == 9) {
			ch = 9;
		} else if (code == 10) {
			// ENTER
			ch = 10;
		} else if (code >= 112 && code <= 123) {
			ch = code + 1008 - 112;
		} else if (code == 36) {
			ch = 1000;
		} else if (code == 35) {
			ch = 1001;
		} else if (code == 33) {
			ch = 1002;
		} else if (code == 34) {
			ch = 1003;
		}

		if (ch > 0 && ch < 128) {
			this.actionKey[ch] = 1;
		}

		if (ch > 4) {
			this.keyQueue[this.keyQueueWritePos] = ch;
			this.keyQueueWritePos = this.keyQueueWritePos + 1 & 0x7F;
		}

		if (InputTracking.enabled) {
			InputTracking.keyPressed(ch);
		}
	}

	@Override
	public final void keyReleased( KeyEvent e) {
		this.idleCycles = 0;

		int code = e.getKeyCode();
		int ch = e.getKeyChar();

		if (ch < 30) {
			ch = 0;
		}

		if (code == 37) {
			// KEY_LEFT
			ch = 1;
		} else if (code == 39) {
			// KEY_RIGHT
			ch = 2;
		} else if (code == 38) {
			// KEY_UP
			ch = 3;
		} else if (code == 40) {
			// KEY_DOWN
			ch = 4;
		} else if (code == 17) {
			// CONTROL
			ch = 5;
		} else if (code == 16) {
			// SHIFT
			ch = 6; // (custom)
		} else if (code == 18) {
			// ALT
			ch = 7; // (custom)
		} else if (code == 8) {
			// BACKSPACE
			ch = 8;
		} else if (code == 127) {
			// DELETE
			ch = 8;
		} else if (code == 9) {
			ch = 9;
		} else if (code == 10) {
			// ENTER
			ch = 10;
		} else if (code >= 112 && code <= 123) {
			ch = code + 1008 - 112;
		} else if (code == 36) {
			ch = 1000;
		} else if (code == 35) {
			ch = 1001;
		} else if (code == 33) {
			ch = 1002;
		} else if (code == 34) {
			ch = 1003;
		}

		if (ch > 0 && ch < 128) {
			this.actionKey[ch] = 0;
		}

		if (InputTracking.enabled) {
			InputTracking.keyReleased(ch);
		}
	}

	@Override
	public final void keyTyped( KeyEvent e) {
	}

	@Override
	public final void focusGained( FocusEvent e) {
		this.refresh = true;
		this.refresh();

		if (InputTracking.enabled) {
			InputTracking.focusGained();
		}
	}

	@Override
	public final void focusLost( FocusEvent e) {
		if (InputTracking.enabled) {
			InputTracking.focusLost();
		}
	}

	protected final int pollKey() {
		int key = -1;
		if (this.keyQueueWritePos != this.keyQueueReadPos) {
			key = this.keyQueue[this.keyQueueReadPos];
			this.keyQueueReadPos = this.keyQueueReadPos + 1 & 0x7F;
		}
		return key;
	}

	public final void windowActivated( WindowEvent e) {
	}

	public final void windowClosed( WindowEvent e) {
	}

	public final void windowClosing( WindowEvent e) {
		this.destroy();
	}

	public final void windowDeactivated( WindowEvent e) {
	}

	public final void windowDeiconified( WindowEvent e) {
	}

	public final void windowIconified( WindowEvent e) {
	}

	public final void windowOpened( WindowEvent e) {
	}

	protected void load() {
	}

	protected void update() {
	}

	protected void unload() {
	}

	protected void draw() {
	}

	protected void refresh() {
	}

	protected Component getBaseComponent() {
		if (this.frame != null) {
			return this.frame;
		}

		return this;
	}

	public void startThread( Runnable runnable, int priority) {
		Thread thread = new Thread(runnable);
		thread.start();
		thread.setPriority(priority);
	}

	protected void drawProgress( String message, int progress) {
		while (this.graphics == null) {
			if (Client.vanilla)
				this.graphics = this.frame.getGraphics();
			else
				this.graphics = gamePanel.getGraphics();

			try {
				this.getBaseComponent().repaint();
			} catch ( Exception ignored) {
			}

			try {
				Thread.sleep(1000L);
			} catch ( Exception ignored) {
			}
		}

		Font bold = new Font("Helvetica", Font.BOLD, 13);
		FontMetrics boldMetrics = this.getBaseComponent().getFontMetrics(bold);
		Font plain = new Font("Helvetica", Font.PLAIN, 13);
		FontMetrics plainMetrics = this.getBaseComponent().getFontMetrics(plain);

		if (this.refresh) {
			this.graphics.setColor(Color.black);
			this.graphics.fillRect(0, 0, this.screenWidth, this.screenHeight);
			this.refresh = false;
		}

		Color barColor = new Color(140, 17, 17);
		int y = this.screenHeight / 2 - 18;

		this.graphics.setColor(barColor);
		this.graphics.drawRect(this.screenWidth / 2 - 152, y, 304, 34);
		this.graphics.fillRect(this.screenWidth / 2 - 150, y + 2, progress * 3, 30);

		this.graphics.setColor(Color.black);
		this.graphics.fillRect(this.screenWidth / 2 + progress * 3 - 150, y + 2, 300 - progress * 3, 30);

		this.graphics.setFont(bold);
		this.graphics.setColor(Color.white);
		this.graphics.drawString(message, (this.screenWidth - boldMetrics.stringWidth(message)) / 2, y + 22);
	}
}
