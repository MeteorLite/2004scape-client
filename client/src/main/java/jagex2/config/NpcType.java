package jagex2.config;

import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

public class NpcType {

    private static int count;

    private static int[] offsets;

    private static Packet dat;

    private static NpcType[] cache;

    private static int cachePos;

    public long index = -1L;

    public String name;

    public String desc;

    public byte size = 1;

    private int[] models;

    private int[] heads;

    public int readyanim = -1;

    public int walkanim = -1;

    public int walkanim_b = -1;

    public int walkanim_r = -1;

    public int walkanim_l = -1;

    private boolean disposeAlpha = false;

    private int[] recol_s;

    private int[] recol_d;

    public String[] ops;

    private int code90 = -1;

    private int code91 = -1;

    private int code92 = -1;

    public boolean visonmap = true;

    public int vislevel = -1;

    private int resizeh = 128;

    private int resizev = 128;

    public static LruCache modelCache = new LruCache(30);

    public static void unpack( Jagfile config) {
		dat = new Packet(config.read("npc.dat", null));
		Packet idx = new Packet(config.read("npc.idx", null));

		count = idx.g2();
		offsets = new int[count];

		int offset = 2;
		for ( int id = 0; id < count; id++) {
			offsets[id] = offset;
			offset += idx.g2();
		}

		cache = new NpcType[20];
		for ( int id = 0; id < 20; id++) {
			cache[id] = new NpcType();
		}
	}

    public static void unload() {
		modelCache = null;
		offsets = null;
		cache = null;
		dat = null;
	}

    public static NpcType get( int id) {
		for ( int i = 0; i < 20; i++) {
			if (cache[i].index == (long) id) {
				return cache[i];
			}
		}

		cachePos = (cachePos + 1) % 20;
		NpcType npc = cache[cachePos] = new NpcType();
		dat.pos = offsets[id];
		npc.index = id;
		npc.decode(dat);
		return npc;
	}

    public void decode( Packet dat) {
		while (true) {
			int code = dat.g1();
			if (code == 0) {
				return;
			}

			if (code == 1) {
				int count = dat.g1();
				this.models = new int[count];

				for (int i = 0; i < count; i++) {
					this.models[i] = dat.g2();
				}
			} else if (code == 2) {
				this.name = dat.gjstr();
			} else if (code == 3) {
				this.desc = dat.gjstr();
			} else if (code == 12) {
				this.size = dat.g1b();
			} else if (code == 13) {
				this.readyanim = dat.g2();
			} else if (code == 14) {
				this.walkanim = dat.g2();
			} else if (code == 16) {
				this.disposeAlpha = true;
			} else if (code == 17) {
				this.walkanim = dat.g2();
				this.walkanim_b = dat.g2();
				this.walkanim_r = dat.g2();
				this.walkanim_l = dat.g2();
			} else if (code >= 30 && code < 40) {
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
				int count = dat.g1();
				this.heads = new int[count];

				for (int i = 0; i < count; i++) {
					this.heads[i] = dat.g2();
				}
			} else if (code == 90) {
				this.code90 = dat.g2();
			} else if (code == 91) {
				this.code91 = dat.g2();
			} else if (code == 92) {
				this.code92 = dat.g2();
			} else if (code == 93) {
				this.visonmap = false;
			} else if (code == 95) {
				this.vislevel = dat.g2();
			} else if (code == 97) {
				this.resizeh = dat.g2();
			} else if (code == 98) {
				this.resizev = dat.g2();
			}
		}
	}

    public Model getSequencedModel( int primaryTransformId, int secondaryTransformId, int[] seqMask) {
		Model tmp = null;
		Model model = (Model) modelCache.get(this.index);

		if (model == null) {
			Model[] models = new Model[this.models.length];
			for ( int i = 0; i < this.models.length; i++) {
				models[i] = new Model(this.models[i]);
			}

			if (models.length == 1) {
				model = models[0];
			} else {
				model = new Model(models, models.length);
			}

			if (this.recol_s != null) {
				for ( int i = 0; i < this.recol_s.length; i++) {
					model.recolor(this.recol_s[i], this.recol_d[i]);
				}
			}

			model.createLabelReferences();
			model.calculateNormals(64, 850, -30, -50, -30, true);
			modelCache.put(this.index, model);
		}

		tmp = new Model(model, !this.disposeAlpha);

		if (primaryTransformId != -1 && secondaryTransformId != -1) {
			tmp.applyTransforms(primaryTransformId, secondaryTransformId, seqMask);
		} else if (primaryTransformId != -1) {
			tmp.applyTransform(primaryTransformId);
		}

		if (this.resizeh != 128 || this.resizev != 128) {
			tmp.scale(this.resizeh, this.resizev, this.resizeh);
		}

		tmp.calculateBoundsCylinder();
		tmp.labelFaces = null;
		tmp.labelVertices = null;

		if (this.size == 1) {
			tmp.pickable = true;
		}

		return tmp;
	}

    public Model getHeadModel() {
		if (this.heads == null) {
			return null;
		}

		Model[] models = new Model[this.heads.length];
		for ( int i = 0; i < this.heads.length; i++) {
			models[i] = new Model(this.heads[i]);
		}

		Model model;
		if (models.length == 1) {
			model = models[0];
		} else {
			model = new Model(models, models.length);
		}

		if (this.recol_s != null) {
			for ( int i = 0; i < this.recol_s.length; i++) {
				model.recolor(this.recol_s[i], this.recol_d[i]);
			}
		}

		return model;
	}
}
