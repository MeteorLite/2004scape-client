package jagex2.graphics;

import jagex2.io.Jagfile;
import jagex2.io.Packet;

public class SeqFrame {

    public static SeqFrame[] instances;

    public int delay;

    public SeqBase base;

    public int length;

    public int[] bases;

    public int[] x;

    public int[] y;

    public int[] z;

    public static void unpack( Jagfile models) {
		Packet head = new Packet(models.read("frame_head.dat", null));
		Packet tran1 = new Packet(models.read("frame_tran1.dat", null));
		Packet tran2 = new Packet(models.read("frame_tran2.dat", null));
		Packet del = new Packet(models.read("frame_del.dat", null));

		int total = head.g2();
		int count = head.g2();
		instances = new SeqFrame[count + 1];

		int[] labels = new int[500];
		int[] x = new int[500];
		int[] y = new int[500];
		int[] z = new int[500];

		for ( int i = 0; i < total; i++) {
			int id = head.g2();
			SeqFrame frame = instances[id] = new SeqFrame();
			frame.delay = del.g1();

			int baseId = head.g2();
			SeqBase base = SeqBase.instances[baseId];
			frame.base = base;

			int groupCount = head.g1();
			int lastGroup = -1;
			int current = 0;

			for ( int j = 0; j < groupCount; j++) {
				int flags = tran1.g1();

				if (flags > 0) {
					if (base.types[j] != 0) {
						for ( int group = j - 1; group > lastGroup; group--) {
							if (base.types[group] == 0) {
								labels[current] = group;
								x[current] = 0;
								y[current] = 0;
								z[current] = 0;
								current++;
								break;
							}
						}
					}

					labels[current] = j;

					short defaultValue = 0;
					if (base.types[labels[current]] == 3) {
						defaultValue = 128;
					}

					if ((flags & 0x1) == 0) {
						x[current] = defaultValue;
					} else {
						x[current] = tran2.gsmart();
					}

					if ((flags & 0x2) == 0) {
						y[current] = defaultValue;
					} else {
						y[current] = tran2.gsmart();
					}

					if ((flags & 0x4) == 0) {
						z[current] = defaultValue;
					} else {
						z[current] = tran2.gsmart();
					}

					lastGroup = j;
					current++;
				}
			}

			frame.length = current;
			frame.bases = new int[current];
			frame.x = new int[current];
			frame.y = new int[current];
			frame.z = new int[current];

			for (int j = 0; j < current; j++) {
				frame.bases[j] = labels[j];
				frame.x[j] = x[j];
				frame.y[j] = y[j];
				frame.z[j] = z[j];
			}
		}
	}

}
