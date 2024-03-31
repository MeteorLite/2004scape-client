package jagex2.client;

import jagex2.io.Packet;

public class InputTracking {

    public static boolean enabled;

    private static Packet outBuffer = null;

    private static Packet oldBuffer = null;

    private static long lastTime;

    private static int trackedCount;

    private static long lastMoveTime;

    private static int lastX;

    private static int lastY;

    public static synchronized void setEnabled() {
		outBuffer = Packet.alloc(1);
		oldBuffer = null;
		lastTime = System.currentTimeMillis();
		enabled = true;
	}

    public static synchronized void setDisabled() {
		enabled = false;
		outBuffer = null;
	}

    public static synchronized Packet flush() {
		Packet buffer = null;
		if (oldBuffer != null && enabled) {
			buffer = oldBuffer;
		}
		oldBuffer = null;
		return buffer;
	}

    public static synchronized Packet stop() {
		Packet buffer = null;
		if (outBuffer != null && outBuffer.pos > 0 && enabled) {
			buffer = outBuffer;
		}
		setDisabled();
		return buffer;
	}

    private static synchronized void ensureCapacity( int n) {
		if (outBuffer.pos + n >= 500) {
			Packet buffer = outBuffer;
			outBuffer = Packet.alloc(1);
			oldBuffer = buffer;
		}
	}

    public static synchronized void mousePressed( int x, int y, int button) {
		if (enabled && (x >= 0 && x < 789 && y >= 0 && y < 532)) {
			trackedCount++;

			long now = System.currentTimeMillis();
			long delta = (now - lastTime) / 10L;
			if (delta > 250L) {
				delta = 250L;
			}

			lastTime = now;
			ensureCapacity(5);

			if (button == 1) {
				outBuffer.p1(1);
			} else {
				outBuffer.p1(2);
			}

			outBuffer.p1((int) delta);
			outBuffer.p3(x + (y << 10));
		}
	}

    public static synchronized void mouseReleased( int button) {
		if (enabled) {
			trackedCount++;

			long now = System.currentTimeMillis();
			long delta = (now - lastTime) / 10L;
			if (delta > 250L) {
				delta = 250L;
			}

			lastTime = now;
			ensureCapacity(2);

			if (button == 1) {
				outBuffer.p1(3);
			} else {
				outBuffer.p1(4);
			}

			outBuffer.p1((int) delta);
		}
	}

    public static synchronized void mouseMoved( int x, int y) {
		if (enabled && (x >= 0 && x < 789 && y >= 0 && y < 532)) {
			long now = System.currentTimeMillis();

			if (now - lastMoveTime >= 50L) {
				lastMoveTime = now;
				trackedCount++;

				long delta = (now - lastTime) / 10L;
				if (delta > 250L) {
					delta = 250L;
				}

				lastTime = now;
				if (x - lastX < 8 && x - lastX >= -8 && y - lastY < 8 && y - lastY >= -8) {
					ensureCapacity(3);
					outBuffer.p1(5);
					outBuffer.p1((int) delta);
					outBuffer.p1(x + (y - lastY + 8 << 4) + 8 - lastX);
				} else if (x - lastX < 128 && x - lastX >= -128 && y - lastY < 128 && y - lastY >= -128) {
					ensureCapacity(4);
					outBuffer.p1(6);
					outBuffer.p1((int) delta);
					outBuffer.p1(x + 128 - lastX);
					outBuffer.p1(y + 128 - lastY);
				} else {
					ensureCapacity(5);
					outBuffer.p1(7);
					outBuffer.p1((int) delta);
					outBuffer.p3(x + (y << 10));
				}

				lastX = x;
				lastY = y;
			}
		}
	}

    public static synchronized void keyPressed( int key) {
		if (enabled) {
			trackedCount++;

			long now = System.currentTimeMillis();
			long delta = (now - lastTime) / 10L;
			if (delta > 250L) {
				delta = 250L;
			}

			lastTime = now;
			if (key == 1000) {
				key = 11;
			} else if (key == 1001) {
				key = 12;
			} else if (key == 1002) {
				key = 14;
			} else if (key == 1003) {
				key = 15;
			} else if (key >= 1008) {
				key -= 992;
			}

			ensureCapacity(3);
			outBuffer.p1(8);
			outBuffer.p1((int) delta);
			outBuffer.p1(key);
		}
	}

    public static synchronized void keyReleased( int key) {
		if (enabled) {
			trackedCount++;

			long now = System.currentTimeMillis();
			long delta = (now - lastTime) / 10L;
			if (delta > 250L) {
				delta = 250L;
			}

			lastTime = now;
			if (key == 1000) {
				key = 11;
			} else if (key == 1001) {
				key = 12;
			} else if (key == 1002) {
				key = 14;
			} else if (key == 1003) {
				key = 15;
			} else if (key >= 1008) {
				key -= 992;
			}

			ensureCapacity(3);
			outBuffer.p1(9);
			outBuffer.p1((int) delta);
			outBuffer.p1(key);
		}
	}

    public static synchronized void focusGained() {
		if (enabled) {
			trackedCount++;

			long now = System.currentTimeMillis();
			long delta = (now - lastTime) / 10L;
			if (delta > 250L) {
				delta = 250L;
			}

			lastTime = now;
			ensureCapacity(2);
			outBuffer.p1(10);
			outBuffer.p1((int) delta);
		}
	}

    public static synchronized void focusLost() {
		if (enabled) {
			trackedCount++;

			long now = System.currentTimeMillis();
			long delta = (now - lastTime) / 10L;
			if (delta > 250L) {
				delta = 250L;
			}

			lastTime = now;
			ensureCapacity(2);
			outBuffer.p1(11);
			outBuffer.p1((int) delta);
		}
	}

    public static synchronized void mouseEntered() {
		if (enabled) {
			trackedCount++;

			long now = System.currentTimeMillis();
			long delta = (now - lastTime) / 10L;
			if (delta > 250L) {
				delta = 250L;
			}

			lastTime = now;
			ensureCapacity(2);
			outBuffer.p1(12);
			outBuffer.p1((int) delta);
		}
	}

    public static synchronized void mouseExited() {
		if (enabled) {
			trackedCount++;

			long now = System.currentTimeMillis();
			long delta = (now - lastTime) / 10L;
			if (delta > 250L) {
				delta = 250L;
			}

			lastTime = now;
			ensureCapacity(2);
			outBuffer.p1(13);
			outBuffer.p1((int) delta);
		}
	}
}
