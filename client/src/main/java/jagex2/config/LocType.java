package jagex2.config;

import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.io.Jagfile;
import jagex2.io.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class LocType {

	// shapes
	public static final int WALL_STRAIGHT = 0;
	public static final int WALL_DIAGONALCORNER = 1;
	public static final int WALL_L = 2;
	public static final int WALL_SQUARECORNER = 3;
	public static final int WALL_DIAGONAL = 9;
	public static final int WALLDECOR_STRAIGHT_NOOFFSET = 4;
	public static final int WALLDECOR_STRAIGHT_OFFSET = 5;
	public static final int WALLDECOR_DIAGONAL_OFFSET = 6;
	public static final int WALLDECOR_DIAGONAL_NOOFFSET = 7;
	public static final int WALLDECOR_DIAGONAL_BOTH = 8;
	public static final int CENTREPIECE_STRAIGHT = 10;
	public static final int CENTREPIECE_DIAGONAL = 11;
	public static final int GROUNDDECOR = 22;
	public static final int ROOF_STRAIGHT = 12;
	public static final int ROOF_DIAGONAL_WITH_ROOFEDGE = 13;
	public static final int ROOF_DIAGONAL = 14;
	public static final int ROOF_L_CONCAVE = 15;
	public static final int ROOF_L_CONVEX = 16;
	public static final int ROOF_FLAT = 17;
	public static final int ROOFEDGE_STRAIGHT = 18;
	public static final int ROOFEDGE_DIAGONALCORNER = 19;
	public static final int ROOFEDGE_L = 20;
	public static final int ROOFEDGE_SQUARECORNER = 21;

    public static boolean reset;

    private static int count;

    private static int[] offsets;

    private static Packet dat;

    private static LocType[] cache;

    private static int cachePos;

    public int index = -1;

    private int[] models;

    private int[] shapes;

    public String name;

    public String desc;

    private int[] recol_s;

    private int[] recol_d;

    public int width;

    public int length;

    public boolean blockwalk;

    public boolean blockrange;

    public boolean active;

    private boolean hillskew;

    private boolean sharelight;

    public boolean occlude;

    public int anim;

    public int walloff;

    private byte ambient;

    private byte contrast;

    public String[] ops;

    private boolean disposeAlpha;

    public int mapfunction;

    public int mapscene;

    private boolean mirror;

    public boolean shadow;

    private int resizex;

    private int resizey;

    private int resizez;

    private int xoff;

    private int yoff;

    private int zoff;

    public int forceapproach;

    public boolean forcedecor;

    public static LruCache modelCacheStatic = new LruCache(500);

    public static LruCache modelCacheDynamic = new LruCache(30);

    public static void unpack( Jagfile config) {
		dat = new Packet(config.read("loc.dat", null));
		Packet idx = new Packet(config.read("loc.idx", null));

		count = idx.g2();
		offsets = new int[count];

		int offset = 2;
		for ( int id = 0; id < count; id++) {
			offsets[id] = offset;
			offset += idx.g2();
		}

		cache = new LocType[10];
		for ( int id = 0; id < 10; id++) {
			cache[id] = new LocType();
		}
	}

    public static void unload() {
		modelCacheStatic = null;
		modelCacheDynamic = null;
		offsets = null;
		cache = null;
		dat = null;
	}

    public static LocType get( int id) {
		for ( int i = 0; i < 10; i++) {
			if (cache[i].index == id) {
				return cache[i];
			}
		}

		cachePos = (cachePos + 1) % 10;
		LocType loc = cache[cachePos];
		dat.pos = offsets[id];
		loc.index = id;
		loc.reset();
		loc.decode(dat);
		return loc;
	}

    public void reset() {
		this.models = null;
		this.shapes = null;
		this.name = null;
		this.desc = null;
		this.recol_s = null;
		this.recol_d = null;
		this.width = 1;
		this.length = 1;
		this.blockwalk = true;
		this.blockrange = true;
		this.active = false;
		this.hillskew = false;
		this.sharelight = false;
		this.occlude = false;
		this.anim = -1;
		this.walloff = 16;
		this.ambient = 0;
		this.contrast = 0;
		this.ops = null;
		this.disposeAlpha = false;
		this.mapfunction = -1;
		this.mapscene = -1;
		this.mirror = false;
		this.shadow = true;
		this.resizex = 128;
		this.resizey = 128;
		this.resizez = 128;
		this.forceapproach = 0;
		this.xoff = 0;
		this.yoff = 0;
		this.zoff = 0;
		this.forcedecor = false;
	}

    public void decode( Packet dat) {
		int active = -1;

		while (true) {
			int code = dat.g1();
			if (code == 0) {
				break;
			}

			if (code == 1) {
				int count = dat.g1();
				this.shapes = new int[count];
				this.models = new int[count];

				for (int i = 0; i < count; i++) {
					this.models[i] = dat.g2();
					this.shapes[i] = dat.g1();
				}
			} else if (code == 2) {
				this.name = dat.gjstr();
			} else if (code == 3) {
				this.desc = dat.gjstr();
			} else if (code == 14) {
				this.width = dat.g1();
			} else if (code == 15) {
				this.length = dat.g1();
			} else if (code == 17) {
				this.blockwalk = false;
			} else if (code == 18) {
				this.blockrange = false;
			} else if (code == 19) {
				active = dat.g1();

				if (active == 1) {
					this.active = true;
				}
			} else if (code == 21) {
				this.hillskew = true;
			} else if (code == 22) {
				this.sharelight = true;
			} else if (code == 23) {
				this.occlude = true;
			} else if (code == 24) {
				this.anim = dat.g2();
				if (this.anim == 65535) {
					this.anim = -1;
				}
			} else if (code == 25) {
				this.disposeAlpha = true;
			} else if (code == 28) {
				this.walloff = dat.g1();
			} else if (code == 29) {
				this.ambient = dat.g1b();
			} else if (code == 39) {
				this.contrast = dat.g1b();
			} else if (code >= 30 && code < 39) {
				if (this.ops == null) {
					this.ops = new String[5];
				}

				this.ops[code - 30] = dat.gjstr();
				if (this.ops[code - 30].equalsIgnoreCase("hidden")) {
					this.ops[code - 30] = null;
				}
			} else if (code == 40) {
				int count = dat.g1();
				this.recol_s = new int[count];
				this.recol_d = new int[count];

				for (int i = 0; i < count; i++) {
					this.recol_s[i] = dat.g2();
					this.recol_d[i] = dat.g2();
				}
			} else if (code == 60) {
				this.mapfunction = dat.g2();
			} else if (code == 62) {
				this.mirror = true;
			} else if (code == 64) {
				this.shadow = false;
			} else if (code == 65) {
				this.resizex = dat.g2();
			} else if (code == 66) {
				this.resizey = dat.g2();
			} else if (code == 67) {
				this.resizez = dat.g2();
			} else if (code == 68) {
				this.mapscene = dat.g2();
			} else if (code == 69) {
				this.forceapproach = dat.g1();
			} else if (code == 70) {
				this.xoff = dat.g2b();
			} else if (code == 71) {
				this.yoff = dat.g2b();
			} else if (code == 72) {
				this.zoff = dat.g2b();
			} else if (code == 73) {
				this.forcedecor = true;
			}
		}

		if (this.shapes == null) {
			this.shapes = new int[0];
		}

		if (active == -1) {
			this.active = this.shapes.length > 0 && this.shapes[0] == 10;

			if (this.ops != null) {
				this.active = true;
			}
		}
	}

    public Model getModel( int shape, int rotation, int heightmapSW, int heightmapSE, int heightmapNE, int heightmapNW, int transformId) {
		int shapeIndex = -1;
		for ( int i = 0; i < this.shapes.length; i++) {
			if (this.shapes[i] == shape) {
				shapeIndex = i;
				break;
			}
		}

		if (shapeIndex == -1) {
			return null;
		}

		long bitset = ((long) this.index << 6) + ((long) shapeIndex << 3) + rotation + ((long) (transformId + 1) << 32);
		if (reset) {
			bitset = 0L;
		}

		Model cached = (Model) modelCacheDynamic.get(bitset);
		if (cached != null) {
			if (reset) {
				return cached;
			}

			if (this.hillskew || this.sharelight) {
				cached = new Model(cached, this.hillskew, this.sharelight);
			}

			if (this.hillskew) {
				int groundY = (heightmapSW + heightmapSE + heightmapNE + heightmapNW) / 4;

				for ( int i = 0; i < cached.vertexCount; i++) {
					int x = cached.vertexX[i];
					int z = cached.vertexZ[i];

					int heightS = heightmapSW + (heightmapSE - heightmapSW) * (x + 64) / 128;
					int heightN = heightmapNW + (heightmapNE - heightmapNW) * (x + 64) / 128;
					int y = heightS + (heightN - heightS) * (z + 64) / 128;

					cached.vertexY[i] += y - groundY;
				}

				cached.calculateBoundsY();
			}

			return cached;
		}

		if (shapeIndex >= this.models.length) {
			return null;
		}

		int modelId = this.models[shapeIndex];
		if (modelId == -1) {
			return null;
		}

		boolean flipped = this.mirror ^ rotation > 3;
		if (flipped) {
			modelId += 65536;
		}

		Model model = (Model) modelCacheStatic.get(modelId);
		if (model == null) {
			model = new Model(modelId & 0xFFFF);
			if (flipped) {
				model.rotateY180();
			}
			modelCacheStatic.put(modelId, model);
		}

		boolean scaled = this.resizex != 128 || this.resizey != 128 || this.resizez != 128;
		boolean translated = this.xoff != 0 || this.yoff != 0 || this.zoff != 0;

		Model modified = new Model(model, this.recol_s == null, !this.disposeAlpha, rotation == 0 && transformId == -1 && !scaled && !translated);
		if (transformId != -1) {
			modified.createLabelReferences();
			modified.applyTransform(transformId);
			modified.labelFaces = null;
			modified.labelVertices = null;
		}

		while (rotation-- > 0) {
			modified.rotateY90();
		}

		if (this.recol_s != null) {
			for (int i = 0; i < this.recol_s.length; i++) {
				modified.recolor(this.recol_s[i], this.recol_d[i]);
			}
		}

		if (scaled) {
			modified.scale(this.resizex, this.resizey, this.resizez);
		}

		if (translated) {
			modified.translate(this.yoff, this.xoff, this.zoff);
		}

		modified.calculateNormals(this.ambient + 64, this.contrast * 5 + 768, -50, -10, -50, !this.sharelight);

		if (this.blockwalk) {
			modified.objRaise = modified.maxY;
		}

		modelCacheDynamic.put(bitset, modified);

		if (this.hillskew || this.sharelight) {
			modified = new Model(modified, this.hillskew, this.sharelight);
		}

		if (this.hillskew) {
			int groundY = (heightmapSW + heightmapSE + heightmapNE + heightmapNW) / 4;

			for ( int i = 0; i < modified.vertexCount; i++) {
				int x = modified.vertexX[i];
				int z = modified.vertexZ[i];

				int heightS = heightmapSW + (heightmapSE - heightmapSW) * (x + 64) / 128;
				int heightN = heightmapNW + (heightmapNE - heightmapNW) * (x + 64) / 128;
				int y = heightS + (heightN - heightS) * (z + 64) / 128;

				modified.vertexY[i] += y - groundY;
			}

			modified.calculateBoundsY();
		}

		return modified;
	}
}
