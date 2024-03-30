package jagex2.graphics;

import jagex2.io.Jagfile;
import jagex2.io.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

import java.util.Random;

public class PixFont extends Draw2D {

    private final byte[][] charMask = new byte[94][];

    private final int[] charMaskWidth = new int[94];

    private final int[] charMaskHeight = new int[94];

    private final int[] charOffsetX = new int[94];

    private final int[] charOffsetY = new int[94];

    private final int[] charAdvance = new int[95];

    private final int[] drawWidth = new int[256];

    public int height;

    private final Random random = new Random();

    private static final int[] CHAR_LOOKUP = new int[256];

	static {
		String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";

		for ( int i = 0; i < 256; i++) {
			int c = charset.indexOf(i);
			if (c == -1) {
				c = 74;
			}

			CHAR_LOOKUP[i] = c;
		}
	}

    public PixFont( Jagfile title, String font) {
		Packet dat = new Packet(title.read(font + ".dat", null));
		Packet idx = new Packet(title.read("index.dat", null));
		idx.pos = dat.g2() + 4;

		int off = idx.g1();
		if (off > 0) {
			idx.pos += (off - 1) * 3;
		}

		for ( int i = 0; i < 94; i++) {
			this.charOffsetX[i] = idx.g1();
			this.charOffsetY[i] = idx.g1();

			int w = this.charMaskWidth[i] = idx.g2();
			int h = this.charMaskHeight[i] = idx.g2();

			int type = idx.g1();
			int len = w * h;

			this.charMask[i] = new byte[len];

			if (type == 0) {
				for (int j = 0; j < len; j++) {
					this.charMask[i][j] = dat.g1b();
				}
			} else if (type == 1) {
				for (int x = 0; x < w; x++) {
					for (int y = 0; y < h; y++) {
						this.charMask[i][x + y * w] = dat.g1b();
					}
				}
			}

			if (h > this.height) {
				this.height = h;
			}

			this.charOffsetX[i] = 1;
			this.charAdvance[i] = w + 2;

			int space = 0;
			for (int j = h / 7; j < h; j++) {
				space += this.charMask[i][j * w];
			}

			if (space <= h / 7) {
				this.charAdvance[i]--;
				this.charOffsetX[i] = 0;
			}

			space = 0;
			for ( int j = h / 7; j < h; j++) {
				space += this.charMask[i][w + j * w - 1];
			}

			if (space <= h / 7) {
				this.charAdvance[i]--;
			}
		}

		this.charAdvance[94] = this.charAdvance[8];
		for (int c = 0; c < 256; c++) {
			this.drawWidth[c] = this.charAdvance[CHAR_LOOKUP[c]];
		}
	}

    public void drawStringCenter( int x, int y, String str, int rgb) {
		this.drawString(x - this.stringWidth(str) / 2, y, str, rgb);
	}

    public void drawStringTaggableCenter( String str, int x, int y, int color, boolean shadowed) {
		this.drawStringTaggable(x - this.stringWidth(str) / 2, y, str, color, shadowed);
	}

    public int stringWidth( String str) {
		if (str == null) {
			return 0;
		}

		int size = 0;
		for ( int c = 0; c < str.length(); c++) {
			if (str.charAt(c) == '@' && c + 4 < str.length() && str.charAt(c + 4) == '@') {
				c += 4;
			} else {
				size += this.drawWidth[str.charAt(c)];
			}
		}

		return size;
	}

    public void drawString( int x, int y, String str, int rgb) {
		if (str == null) {
			return;
		}

		int offY = y - this.height;

		for ( int i = 0; i < str.length(); i++) {
			int c = CHAR_LOOKUP[str.charAt(i)];
			if (c != 94) {
				this.drawChar(this.charMask[c], x + this.charOffsetX[c], offY + this.charOffsetY[c], this.charMaskWidth[c], this.charMaskHeight[c], rgb);
			}

			x += this.charAdvance[c];
		}
	}

	public void drawStringRight(int x, int y, String str, int rgb, boolean shadowed) {
		if (shadowed) {
			this.drawString(x + 1 - this.stringWidth(str), y + 1, str, 0x000000);
		}

		this.drawString(x - this.stringWidth(str), y, str, rgb);
	}

    public void drawCenteredWave( int x, int y, String str, int rgb, int phase) {
		if (str == null) {
			return;
		}

		x -= this.stringWidth(str) / 2;
		int offY = y - this.height;

		for ( int i = 0; i < str.length(); i++) {
			int c = CHAR_LOOKUP[str.charAt(i)];

			if (c != 94) {
				this.drawChar(this.charMask[c], x + this.charOffsetX[c], offY + this.charOffsetY[c] + (int) (Math.sin((double) i / 2.0D + (double) phase / 5.0D) * 5.0D), this.charMaskWidth[c], this.charMaskHeight[c], rgb);
			}

			x += this.charAdvance[c];
		}
	}

    public void drawStringTaggable( int x, int y, String str, int rgb, boolean shadowed) {
		if (str == null) {
			return;
		}

		int offY = y - this.height;

		for ( int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '@' && i + 4 < str.length() && str.charAt(i + 4) == '@') {
				rgb = this.evaluateTag(str.substring(i + 1, i + 4));
				i += 4;
			} else {
				int c = CHAR_LOOKUP[str.charAt(i)];
				if (c != 94) {
					if (shadowed) {
						this.drawChar(this.charMask[c], x + this.charOffsetX[c] + 1, offY + this.charOffsetY[c] + 1, this.charMaskWidth[c], this.charMaskHeight[c], 0);
					}

					this.drawChar(this.charMask[c], x + this.charOffsetX[c], offY + this.charOffsetY[c], this.charMaskWidth[c], this.charMaskHeight[c], rgb);
				}

				x += this.charAdvance[c];
			}
		}
	}

    public void drawStringTooltip( int x, int y, String str, int color, boolean shadowed, int seed) {
		if (str == null) {
			return;
		}

		this.random.setSeed(seed);

		int rand = (this.random.nextInt() & 0x1F) + 192;
		int offY = y - this.height;
		for ( int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '@' && i + 4 < str.length() && str.charAt(i + 4) == '@') {
				color = this.evaluateTag(str.substring(i + 1, i + 4));
				i += 4;
			} else {
				int c = CHAR_LOOKUP[str.charAt(i)];
				if (c != 94) {
					if (shadowed) {
						this.drawCharAlpha(x + this.charOffsetX[c] + 1, offY + this.charOffsetY[c] + 1, this.charMaskWidth[c], this.charMaskHeight[c], 0, 192, this.charMask[c]);
					}

					this.drawCharAlpha(x + this.charOffsetX[c], offY + this.charOffsetY[c], this.charMaskWidth[c], this.charMaskHeight[c], color, rand, this.charMask[c]);
				}

				x += this.charAdvance[c];
				if ((this.random.nextInt() & 0x3) == 0) {
					x++;
				}
			}
		}
	}

    private int evaluateTag( String tag) {
		if (tag.equals("red")) {
			return 0xff0000;
		} else if (tag.equals("gre")) {
			return 0xff00;
		} else if (tag.equals("blu")) {
			return 0xff;
		} else if (tag.equals("yel")) {
			return 0xffff00;
		} else if (tag.equals("cya")) {
			return 0xffff;
		} else if (tag.equals("mag")) {
			return 0xff00ff;
		} else if (tag.equals("whi")) {
			return 0xffffff;
		} else if (tag.equals("bla")) {
			return 0;
		} else if (tag.equals("lre")) {
			return 0xff9040;
		} else if (tag.equals("dre")) {
			return 0x800000;
		} else if (tag.equals("dbl")) {
			return 0x80;
		} else if (tag.equals("or1")) {
			return 0xffb000;
		} else if (tag.equals("or2")) {
			return 0xff7000;
		} else if (tag.equals("or3")) {
			return 0xff3000;
		} else if (tag.equals("gr1")) {
			return 0xc0ff00;
		} else if (tag.equals("gr2")) {
			return 0x80ff00;
		} else if (tag.equals("gr3")) {
			return 0x40ff00;
		} else {
			return 0;
		}
	}

    private void drawChar( byte[] data, int x, int y, int w, int h, int rgb) {
		int dstOff = x + y * Draw2D.width2d;
		int dstStep = Draw2D.width2d - w;

		int srcStep = 0;
		int srcOff = 0;

		if (y < Draw2D.top) {
			int cutoff = Draw2D.top - y;
			h -= cutoff;
			y = Draw2D.top;
			srcOff += cutoff * w;
			dstOff += cutoff * Draw2D.width2d;
		}

		if (y + h >= Draw2D.bottom) {
			h -= y + h + 1 - Draw2D.bottom;
		}

		if (x < Draw2D.left) {
			int cutoff = Draw2D.left - x;
			w -= cutoff;
			x = Draw2D.left;
			srcOff += cutoff;
			dstOff += cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}

		if (x + w >= Draw2D.right) {
			int cutoff = x + w + 1 - Draw2D.right;
			w -= cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}

		if (w > 0 && h > 0) {
			this.drawMask(w, h, data, srcOff, srcStep, Draw2D.data, dstOff, dstStep, rgb);
		}
	}

    private void drawMask( int w, int h, byte[] src, int srcOff, int srcStep, int[] dst, int dstOff, int dstStep, int rgb) {
		int hw = -(w >> 2);
		w = -(w & 0x3);

		for ( int y = -h; y < 0; y++) {
			for ( int x = hw; x < 0; x++) {
				if (src[srcOff++] == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}

				if (src[srcOff++] == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}

				if (src[srcOff++] == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}

				if (src[srcOff++] == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}
			}

			for ( int x = w; x < 0; x++) {
				if (src[srcOff++] == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}
			}

			dstOff += dstStep;
			srcOff += srcStep;
		}
	}

    private void drawCharAlpha( int x, int y, int w, int h, int rgb, int alpha, byte[] mask) {
		int dstOff = x + y * Draw2D.width2d;
		int dstStep = Draw2D.width2d - w;

		int srcStep = 0;
		int srcOff = 0;

		if (y < Draw2D.top) {
			int cutoff = Draw2D.top - y;
			h -= cutoff;
			y = Draw2D.top;
			srcOff += cutoff * w;
			dstOff += cutoff * Draw2D.width2d;
		}

		if (y + h >= Draw2D.bottom) {
			h -= y + h + 1 - Draw2D.bottom;
		}

		if (x < Draw2D.left) {
			int cutoff = Draw2D.left - x;
			w -= cutoff;
			x = Draw2D.left;
			srcOff += cutoff;
			dstOff += cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}

		if (x + w >= Draw2D.right) {
			int cutoff = x + w + 1 - Draw2D.right;
			w -= cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}

		if (w > 0 && h > 0) {
			this.drawMaskAlpha(w, h, Draw2D.data, dstOff, dstStep, mask, srcOff, srcStep, rgb, alpha);
		}
	}

    private void drawMaskAlpha( int w, int h, int[] dst, int dstOff, int dstStep, byte[] mask, int maskOff, int maskStep, int color, int alpha) {
		int rgb = ((color & 0xFF00FF) * alpha & 0xFF00FF00) + ((color & 0xFF00) * alpha & 0xFF0000) >> 8;
		int invAlpha = 256 - alpha;

		for ( int y = -h; y < 0; y++) {
			for ( int x = -w; x < 0; x++) {
				if (mask[maskOff++] == 0) {
					dstOff++;
				} else {
					int dstRgb = dst[dstOff];
					dst[dstOff++] = (((dstRgb & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((dstRgb & 0xFF00) * invAlpha & 0xFF0000) >> 8) + rgb;
				}
			}

			dstOff += dstStep;
			maskOff += maskStep;
		}
	}
}
