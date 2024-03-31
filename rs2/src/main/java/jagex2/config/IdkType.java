package jagex2.config;

import jagex2.graphics.Model;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

public class IdkType {

    public static int count;

    public static IdkType[] instances;

    public int type = -1;

    private int[] models;

    private final int[] recol_s = new int[6];

    private final int[] recol_d = new int[6];

    private final int[] heads = new int[] { -1, -1, -1, -1, -1 };

    public boolean disable = false;

    public static void unpack( Jagfile config) {
		Packet dat = new Packet(config.read("idk.dat", null));
		count = dat.g2();

		if (instances == null) {
			instances = new IdkType[count];
		}

		for ( int id = 0; id < count; id++) {
			if (instances[id] == null) {
				instances[id] = new IdkType();
			}

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
				this.type = dat.g1();
			} else if (code == 2) {
				int count = dat.g1();
				this.models = new int[count];

				for ( int i = 0; i < count; i++) {
					this.models[i] = dat.g2();
				}
			} else if (code == 3) {
				this.disable = true;
			} else if (code >= 40 && code < 50) {
				this.recol_s[code - 40] = dat.g2();
			} else if (code >= 50 && code < 60) {
				this.recol_d[code - 50] = dat.g2();
			} else if (code >= 60 && code < 70) {
				this.heads[code - 60] = dat.g2();
			} else {
				System.out.println("Error unrecognised config code: " + code);
			}
		}
	}

    public Model getModel() {
		if (this.models == null) {
			return null;
		}

		Model[] models = new Model[this.models.length];
		for ( int i = 0; i < this.models.length; i++) {
			models[i] = new Model(this.models[i]);
		}

		Model model;
		if (models.length == 1) {
			model = models[0];
		} else {
			model = new Model(models, models.length);
		}

		for ( int i = 0; i < 6 && this.recol_s[i] != 0; i++) {
			model.recolor(this.recol_s[i], this.recol_d[i]);
		}

		return model;
	}

    public Model getHeadModel() {
		Model[] models = new Model[5];

		int count = 0;
		for ( int i = 0; i < 5; i++) {
			if (this.heads[i] != -1) {
				models[count++] = new Model(this.heads[i]);
			}
		}

		Model model = new Model(models, count);
		for ( int i = 0; i < 6 && this.recol_s[i] != 0; i++) {
			model.recolor(this.recol_s[i], this.recol_d[i]);
		}

		return model;
	}
}
