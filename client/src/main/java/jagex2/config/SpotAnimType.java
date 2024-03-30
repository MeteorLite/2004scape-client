package jagex2.config;

import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.io.Jagfile;
import jagex2.io.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class SpotAnimType {

    private static int count;

    public static SpotAnimType[] instances;

    public int index;

    private int model;

    private int anim = -1;

    public SeqType seq;

    public boolean disposeAlpha = false;

    private final int[] recol_s = new int[6];

    private final int[] recol_d = new int[6];

    public int resizeh = 128;

    public int resizev = 128;

    public int orientation;

    public int ambient;

    public int contrast;

    public static LruCache modelCache = new LruCache(30);

    public static void unpack( Jagfile config) {
		Packet dat = new Packet(config.read("spotanim.dat", null));
		count = dat.g2();

		if (instances == null) {
			instances = new SpotAnimType[count];
		}

		for ( int id = 0; id < count; id++) {
			if (instances[id] == null) {
				instances[id] = new SpotAnimType();
			}

			instances[id].index = id;
			instances[id].decode(dat);
		}
	}

    public void decode( Packet dat) {
		while (true) {
			int code = dat.g1();
			if (code == 0) {
				break;
			}

			if (code == 1) {
				this.model = dat.g2();
			} else if (code == 2) {
				this.anim = dat.g2();

				if (SeqType.instances != null) {
					this.seq = SeqType.instances[this.anim];
				}
			} else if (code == 3) {
				this.disposeAlpha = true;
			} else if (code == 4) {
				this.resizeh = dat.g2();
			} else if (code == 5) {
				this.resizev = dat.g2();
			} else if (code == 6) {
				this.orientation = dat.g2();
			} else if (code == 7) {
				this.ambient = dat.g1();
			} else if (code == 8) {
				this.contrast = dat.g1();
			} else if (code >= 40 && code < 50) {
				this.recol_s[code - 40] = dat.g2();
			} else if (code >= 50 && code < 60) {
				this.recol_d[code - 50] = dat.g2();
			} else {
				System.out.println("Error unrecognised spotanim config code: " + code);
			}
		}
	}

    public Model getModel() {
		Model model = (Model) modelCache.get(this.index);
		if (model != null) {
			return model;
		}

		model = new Model(this.model);
		for ( int i = 0; i < 6; i++) {
			if (this.recol_s[0] != 0) {
				model.recolor(this.recol_s[i], this.recol_d[i]);
			}
		}

		modelCache.put(this.index, model);
		return model;
	}
}
