package jagex2.graphics;

import jagex2.io.Jagfile;
import jagex2.io.Packet;

public class Pix8 extends Draw2D {

    public byte[] pixels;

    public final int[] palette;

    public int width;

    public int height;

    public int cropX;

    public int cropY;

    public int cropW;

    private int cropH;

    public Pix8( Jagfile jag, String name, int index) {
		Packet dat = new Packet(jag.read(name + ".dat", null));
		Packet idx = new Packet(jag.read("index.dat", null));
		idx.pos = dat.g2();

		this.cropW = idx.g2();
		this.cropH = idx.g2();

		int paletteCount = idx.g1();
		this.palette = new int[paletteCount];
		for ( int i = 0; i < paletteCount - 1; i++) {
			this.palette[i + 1] = idx.g3();
		}

		for ( int i = 0; i < index; i++) {
			idx.pos += 2;
			dat.pos += idx.g2() * idx.g2();
			idx.pos++;
		}

		this.cropX = idx.g1();
		this.cropY = idx.g1();
		this.width = idx.g2();
		this.height = idx.g2();

		int pixelOrder = idx.g1();
		int len = this.width * this.height;
		this.pixels = new byte[len];

		if (pixelOrder == 0) {
			for (int i = 0; i < len; i++) {
				this.pixels[i] = dat.g1b();
			}
		} else if (pixelOrder == 1) {
			for ( int x = 0; x < this.width; x++) {
				for ( int y = 0; y < this.height; y++) {
					this.pixels[x + y * this.width] = dat.g1b();
				}
			}
		}
	}

    public void shrink() {
		this.cropW /= 2;
		this.cropH /= 2;

		byte[] pixels = new byte[this.cropW * this.cropH];
		int off = 0;
		for ( int y = 0; y < this.height; y++) {
			for ( int x = 0; x < this.width; x++) {
				pixels[(x + this.cropX >> 1) + (y + this.cropY >> 1) * this.cropW] = this.pixels[off++];
			}
		}

		this.pixels = pixels;
		this.width = this.cropW;
		this.height = this.cropH;
		this.cropX = 0;
		this.cropY = 0;
	}

    public void crop() {
		if (this.width == this.cropW && this.height == this.cropH) {
			return;
		}

		byte[] pixels = new byte[this.cropW * this.cropH];
		int off = 0;
		for ( int y = 0; y < this.height; y++) {
			for ( int x = 0; x < this.width; x++) {
				pixels[x + this.cropX + (y + this.cropY) * this.cropW] = this.pixels[off++];
			}
		}

		this.pixels = pixels;
		this.width = this.cropW;
		this.height = this.cropH;
		this.cropX = 0;
		this.cropY = 0;
	}

    public void flipHorizontally() {
		byte[] pixels = new byte[this.width * this.height];
		int off = 0;
		for ( int y = 0; y < this.height; y++) {
			for ( int x = this.width - 1; x >= 0; x--) {
				pixels[off++] = this.pixels[x + y * this.width];
			}
		}

		this.pixels = pixels;
		this.cropX = this.cropW - this.width - this.cropX;
	}

    public void flipVertically() {
		byte[] pixels = new byte[this.width * this.height];
		int off = 0;
		for ( int y = this.height - 1; y >= 0; y--) {
			for ( int x = 0; x < this.width; x++) {
				pixels[off++] = this.pixels[x + y * this.width];
			}
		}

		this.pixels = pixels;
		this.cropY = this.cropH - this.height - this.cropY;
	}

    public void translate( int r, int g, int b) {
		for ( int i = 0; i < this.palette.length; i++) {
			int red = this.palette[i] >> 16 & 0xFF;
			red += r;
			if (red < 0) {
				red = 0;
			} else if (red > 255) {
				red = 255;
			}

			int green = this.palette[i] >> 8 & 0xFF;
			green += g;
			if (green < 0) {
				green = 0;
			} else if (green > 255) {
				green = 255;
			}

			int blue = this.palette[i] & 0xFF;
			blue += b;
			if (blue < 0) {
				blue = 0;
			} else if (blue > 255) {
				blue = 255;
			}

			this.palette[i] = (red << 16) + (green << 8) + blue;
		}
	}

    public void draw( int x, int y) {
		x += this.cropX;
		y += this.cropY;

		int dstOff = x + y * Draw2D.width2d;
		int srcOff = 0;
		int h = this.height;
		int w = this.width;
		int dstStep = Draw2D.width2d - w;
		int srcStep = 0;

		if (y < Draw2D.top) {
			int cutoff = Draw2D.top - y;
			h -= cutoff;
			y = Draw2D.top;
			srcOff += cutoff * w;
			dstOff += cutoff * Draw2D.width2d;
		}

		if (y + h > Draw2D.bottom) {
			h -= y + h - Draw2D.bottom;
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

		if (x + w > Draw2D.right) {
			int cutoff = x + w - Draw2D.right;
			w -= cutoff;
			srcStep += cutoff;
			dstStep += cutoff;
		}

		if (w > 0 && h > 0) {
			this.copyPixels(w, h, this.pixels, srcOff, srcStep, Draw2D.data, dstOff, dstStep, this.palette);
		}
	}

    private void copyPixels( int w, int h, byte[] src, int srcOff, int srcStep, int[] dst, int dstOff, int dstStep, int[] palette) {
		int qw = -(w >> 2);
		w = -(w & 0x3);

		for ( int y = -h; y < 0; y++) {
			for ( int x = qw; x < 0; x++) {
				byte palIndex = src[srcOff++];
				if (palIndex == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = palette[palIndex & 0xFF];
				}

				palIndex = src[srcOff++];
				if (palIndex == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = palette[palIndex & 0xFF];
				}

				palIndex = src[srcOff++];
				if (palIndex == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = palette[palIndex & 0xFF];
				}

				palIndex = src[srcOff++];
				if (palIndex == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = palette[palIndex & 0xFF];
				}
			}

			for ( int x = w; x < 0; x++) {
				byte palIndex = src[srcOff++];
				if (palIndex == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = palette[palIndex & 0xFF];
				}
			}

			dstOff += dstStep;
			srcOff += srcStep;
		}
	}
}
