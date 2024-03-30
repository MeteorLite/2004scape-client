package jagex2.graphics;

import jagex2.io.Jagfile;
import jagex2.io.Packet;

import java.awt.*;
import java.awt.image.PixelGrabber;

public class Pix24 extends Draw2D {

    public int[] pixels;

    public int width;

    public int height;

    private int cropX;

    private int cropY;

    public int cropW;

    public int cropH;

    public Pix24( int width, int height) {
		this.pixels = new int[width * height];
		this.width = this.cropW = width;
		this.height = this.cropH = height;
		this.cropX = this.cropY = 0;
	}

    public Pix24( byte[] src, Component c) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(src);
			MediaTracker tracker = new MediaTracker(c);
			tracker.addImage(image, 0);
			tracker.waitForAll();
			this.width = image.getWidth(c);
			this.height = image.getHeight(c);
			this.cropW = this.width;
			this.cropH = this.height;
			this.cropX = 0;
			this.cropY = 0;
			this.pixels = new int[this.width * this.height];
			PixelGrabber grabber = new PixelGrabber(image, 0, 0, this.width, this.height, this.pixels, 0, this.width);
			grabber.grabPixels();
		} catch ( Exception ignored) {
			System.out.println("Error converting jpg");
		}
	}

    public Pix24( Jagfile jag, String name, int index) {
		Packet dat = new Packet(jag.read(name + ".dat", null));
		Packet idx = new Packet(jag.read("index.dat", null));

		idx.pos = dat.g2();
		this.cropW = idx.g2();
		this.cropH = idx.g2();

		int paletteCount = idx.g1();
		int[] palette = new int[paletteCount];
		for ( int i = 0; i < paletteCount - 1; i++) {
			palette[i + 1] = idx.g3();
			if (palette[i + 1] == 0) {
				palette[i + 1] = 1;
			}
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
		this.pixels = new int[len];

		if (pixelOrder == 0) {
			for (int i = 0; i < len; i++) {
				this.pixels[i] = palette[dat.g1()];
			}
		} else if (pixelOrder == 1) {
			for (int x = 0; x < this.width; x++) {
				for ( int y = 0; y < this.height; y++) {
					this.pixels[x + y * this.width] = palette[dat.g1()];
				}
			}
		}
	}

    public void bind() {
		Draw2D.bind(this.width, this.height, this.pixels);
	}

    public void translate( int r, int g, int b) {
		for ( int i = 0; i < this.pixels.length; i++) {
			int rgb = this.pixels[i];

			if (rgb != 0) {
				int red = rgb >> 16 & 0xFF;
				red += r;
				if (red < 1) {
					red = 1;
				} else if (red > 255) {
					red = 255;
				}

				int green = rgb >> 8 & 0xFF;
				green += g;
				if (green < 1) {
					green = 1;
				} else if (green > 255) {
					green = 255;
				}

				int blue = rgb & 0xFF;
				blue += b;
				if (blue < 1) {
					blue = 1;
				} else if (blue > 255) {
					blue = 255;
				}

				this.pixels[i] = (red << 16) + (green << 8) + blue;
			}
		}
	}

    public void blitOpaque( int x, int y) {
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
			this.copyPixels(w, h, this.pixels, srcOff, srcStep, Draw2D.data, dstOff, dstStep);
		}
	}

    private void copyPixels( int w, int h, int[] src, int srcOff, int srcStep, int[] dst, int dstOff, int dstStep) {
		int qw = -(w >> 2);
		w = -(w & 0x3);

		for ( int y = -h; y < 0; y++) {
			for ( int x = qw; x < 0; x++) {
				dst[dstOff++] = src[srcOff++];
				dst[dstOff++] = src[srcOff++];
				dst[dstOff++] = src[srcOff++];
				dst[dstOff++] = src[srcOff++];
			}

			for ( int x = w; x < 0; x++) {
				dst[dstOff++] = src[srcOff++];
			}

			dstOff += dstStep;
			srcOff += srcStep;
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
			this.copyPixels(Draw2D.data, this.pixels, srcOff, dstOff, w, h, dstStep, srcStep);
		}
	}

    private void copyPixels( int[] dst, int[] src, int srcOff, int dstOff, int w, int h, int dstStep, int srcStep) {
		int qw = -(w >> 2);
		w = -(w & 0x3);

		for ( int y = -h; y < 0; y++) {
			for ( int x = qw; x < 0; x++) {
				int rgb = src[srcOff++];
				if (rgb == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}

				rgb = src[srcOff++];
				if (rgb == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}

				rgb = src[srcOff++];
				if (rgb == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}

				rgb = src[srcOff++];
				if (rgb == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}
			}

			for ( int x = w; x < 0; x++) {
				int rgb = src[srcOff++];
				if (rgb == 0) {
					dstOff++;
				} else {
					dst[dstOff++] = rgb;
				}
			}

			dstOff += dstStep;
			srcOff += srcStep;
		}
	}

    public void crop( int x, int y, int w, int h) {
		try {
			int currentW = this.width;
			int currentH = this.height;

			int offW = 0;
			int offH = 0;
			int scaleWidth = (currentW << 16) / w;
			int scaleHeight = (currentH << 16) / h;

			int cw = this.cropW;
			int ch = this.cropH;
			int scaleCropWidth = (cw << 16) / w;
			int scaleCropHeight = (ch << 16) / h;

			x += (this.cropX * w + cw - 1) / cw;
			y += (this.cropY * h + ch - 1) / ch;

			if (this.cropX * w % cw != 0) {
				offW = (cw - this.cropX * w % cw << 16) / w;
			}

			if (this.cropY * h % ch != 0) {
				offH = (ch - this.cropY * h % ch << 16) / h;
			}

			w = w * (this.width - (offW >> 16)) / cw;
			h = h * (this.height - (offH >> 16)) / ch;

			int dstStep = x + y * Draw2D.width2d;
			int dstOff = Draw2D.width2d - w;

			if (y < Draw2D.top) {
				int cutoff = Draw2D.top - y;
				h -= cutoff;
				y = 0;
				dstStep += cutoff * Draw2D.width2d;
				offH += scaleCropHeight * cutoff;
			}

			if (y + h > Draw2D.bottom) {
				h -= y + h - Draw2D.bottom;
			}

			if (x < Draw2D.left) {
				int cutoff = Draw2D.left - x;
				w -= cutoff;
				x = 0;
				dstStep += cutoff;
				offW += scaleCropWidth * cutoff;
				dstOff += cutoff;
			}

			if (x + w > Draw2D.right) {
				int cutoff = x + w - Draw2D.right;
				w -= cutoff;
				dstOff += cutoff;
			}

			this.scale(w, h, this.pixels, offW, offH, Draw2D.data, dstOff, dstStep, currentW, scaleCropWidth, scaleCropHeight);
		} catch ( Exception ignored) {
			System.out.println("error in sprite clipping routine");
		}
	}

    private void scale( int w, int h, int[] src, int offW, int offH, int[] dst, int dstStep, int dstOff, int currentW, int scaleCropWidth, int scaleCropHeight) {
		try {
			int lastOffW = offW;

			for ( int y = -h; y < 0; y++) {
				int offY = (offH >> 16) * currentW;

				for ( int x = -w; x < 0; x++) {
					int rgb = src[(offW >> 16) + offY];
					if (rgb == 0) {
						dstOff++;
					} else {
						dst[dstOff++] = rgb;
					}

					offW += scaleCropWidth;
				}

				offH += scaleCropHeight;
				offW = lastOffW;
				dstOff += dstStep;
			}
		} catch ( Exception ignored) {
			System.out.println("error in plot_scale");
		}
	}

    public void drawAlpha( int alpha, int x, int y) {
		x += this.cropX;
		y += this.cropY;

		int dstStep = x + y * Draw2D.width2d;
		int srcStep = 0;
		int h = this.height;
		int w = this.width;
		int dstOff = Draw2D.width2d - w;
		int srcOff = 0;

		if (y < Draw2D.top) {
			int cutoff = Draw2D.top - y;
			h -= cutoff;
			y = Draw2D.top;
			srcStep += cutoff * w;
			dstStep += cutoff * Draw2D.width2d;
		}

		if (y + h > Draw2D.bottom) {
			h -= y + h - Draw2D.bottom;
		}

		if (x < Draw2D.left) {
			int cutoff = Draw2D.left - x;
			w -= cutoff;
			x = Draw2D.left;
			srcStep += cutoff;
			dstStep += cutoff;
			srcOff += cutoff;
			dstOff += cutoff;
		}

		if (x + w > Draw2D.right) {
			int cutoff = x + w - Draw2D.right;
			w -= cutoff;
			srcOff += cutoff;
			dstOff += cutoff;
		}

		if (w > 0 && h > 0) {
			this.copyPixelsAlpha(w, h, this.pixels, srcStep, srcOff, Draw2D.data, dstStep, dstOff, alpha);
		}
	}

    private void copyPixelsAlpha( int w, int h, int[] src, int srcOff, int srcStep, int[] dst, int dstOff, int dstStep, int alpha) {
		int invAlpha = 256 - alpha;

		for ( int y = -h; y < 0; y++) {
			for ( int x = -w; x < 0; x++) {
				int rgb = src[srcOff++];
				if (rgb == 0) {
					dstOff++;
				} else {
					int dstRgb = dst[dstOff];
					dst[dstOff++] = ((rgb & 0xFF00FF) * alpha + (dstRgb & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((rgb & 0xFF00) * alpha + (dstRgb & 0xFF00) * invAlpha & 0xFF0000) >> 8;
				}
			}

			dstOff += dstStep;
			srcOff += srcStep;
		}
	}

    public void drawRotatedMasked( int x, int y, int w, int h, int[] lineStart, int[] lineWidth, int anchorX, int anchorY, int theta, int zoom) {
		try {
			int centerX = -w / 2;
			int centerY = -h / 2;

			int sin = (int) (Math.sin((double) theta / 326.11D) * 65536.0D);
			int cos = (int) (Math.cos((double) theta / 326.11D) * 65536.0D);
			int sinZoom = sin * zoom >> 8;
			int cosZoom = cos * zoom >> 8;

			int leftX = (anchorX << 16) + centerY * sinZoom + centerX * cosZoom;
			int leftY = (anchorY << 16) + (centerY * cosZoom - centerX * sinZoom);
			int leftOff = x + y * Draw2D.width2d;

			for ( int i = 0; i < h; i++) {
				int dstOff = lineStart[i];
				int dstX = leftOff + dstOff;

				int srcX = leftX + cosZoom * dstOff;
				int srcY = leftY - sinZoom * dstOff;
				for ( int j = -lineWidth[i]; j < 0; j++) {
					Draw2D.data[dstX++] = this.pixels[(srcX >> 16) + (srcY >> 16) * this.width];
					srcX += cosZoom;
					srcY -= sinZoom;
				}

				leftX += sinZoom;
				leftY += cosZoom;
				leftOff += Draw2D.width2d;
			}
		} catch ( Exception ignored) {
		}
	}

    public void drawMasked( int x, int y, Pix8 mask) {
		x += this.cropX;
		y += this.cropY;

		int dstStep = x + y * Draw2D.width2d;
		int srcStep = 0;
		int h = this.height;
		int w = this.width;
		int dstOff = Draw2D.width2d - w;
		int srcOff = 0;

		if (y < Draw2D.top) {
			int cutoff = Draw2D.top - y;
			h -= cutoff;
			y = Draw2D.top;
			srcStep += cutoff * w;
			dstStep += cutoff * Draw2D.width2d;
		}

		if (y + h > Draw2D.bottom) {
			h -= y + h - Draw2D.bottom;
		}

		if (x < Draw2D.left) {
			int cutoff = Draw2D.left - x;
			w -= cutoff;
			x = Draw2D.left;
			srcStep += cutoff;
			dstStep += cutoff;
			srcOff += cutoff;
			dstOff += cutoff;
		}

		if (x + w > Draw2D.right) {
			int cutoff = x + w - Draw2D.right;
			w -= cutoff;
			srcOff += cutoff;
			dstOff += cutoff;
		}

		if (w > 0 && h > 0) {
			this.copyPixelsMasked(w, h, this.pixels, srcOff, srcStep, Draw2D.data, dstStep, dstOff, mask.pixels);
		}
	}

    private void copyPixelsMasked( int w, int h, int[] src, int srcStep, int srcOff, int[] dst, int dstOff, int dstStep, byte[] mask) {
		int qw = -(w >> 2);
		w = -(w & 0x3);

		for ( int y = -h; y < 0; y++) {
			for ( int x = qw; x < 0; x++) {
				int rgb = src[srcOff++];
				if (rgb != 0 && mask[dstOff] == 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}

				rgb = src[srcOff++];
				if (rgb != 0 && mask[dstOff] == 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}

				rgb = src[srcOff++];
				if (rgb != 0 && mask[dstOff] == 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}

				rgb = src[srcOff++];
				if (rgb != 0 && mask[dstOff] == 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}
			}

			for ( int x = w; x < 0; x++) {
				int rgb = src[srcOff++];
				if (rgb != 0 && mask[dstOff] == 0) {
					dst[dstOff++] = rgb;
				} else {
					dstOff++;
				}
			}

			dstOff += dstStep;
			srcOff += srcStep;
		}
	}
}
