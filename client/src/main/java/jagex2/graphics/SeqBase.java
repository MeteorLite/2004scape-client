package jagex2.graphics;

import jagex2.io.Jagfile;
import jagex2.io.Packet;

public class SeqBase {

    public static SeqBase[] instances;

    public int length;

    public int[] types;

    public int[][] labels;

    public static void unpack( Jagfile models) {
		Packet head = new Packet(models.read("base_head.dat", null));
		Packet type = new Packet(models.read("base_type.dat", null));
		Packet label = new Packet(models.read("base_label.dat", null));

		int total = head.g2();
		int count = head.g2();
		instances = new SeqBase[count + 1];

		for ( int i = 0; i < total; i++) {
			int id = head.g2();
			int length = head.g1();

			int[] transformTypes = new int[length];
			int[][] groupLabels = new int[length][];

			for ( int j = 0; j < length; j++) {
				transformTypes[j] = type.g1();

				int groupCount = label.g1();
				groupLabels[j] = new int[groupCount];

				for ( int k = 0; k < groupCount; k++) {
					groupLabels[j][k] = label.g1();
				}
			}

			instances[id] = new SeqBase();
			instances[id].length = length;
			instances[id].types = transformTypes;
			instances[id].labels = groupLabels;
		}
	}
}
