package jagex2.config;

import jagex2.io.Jagfile;
import jagex2.io.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class VarpType {

    private static int count;

    public static VarpType[] instances;

    public static int code3Count;

    public static int[] code3;

    private String code10;

    private int code1;

    private int code2;

    private boolean hasCode3 = false;

    private boolean code4 = true;

    public int clientcode;

    private int code7;

    private boolean code6 = false;

    private boolean code8 = false;

    public static void unpack( Jagfile config) {
		Packet dat = new Packet(config.read("varp.dat", null));
		code3Count = 0;
		count = dat.g2();

		if (instances == null) {
			instances = new VarpType[count];
		}

		if (code3 == null) {
			code3 = new int[count];
		}

		for ( int id = 0; id < count; id++) {
			if (instances[id] == null) {
				instances[id] = new VarpType();
			}

			instances[id].decode(id, dat);
		}
	}

    public void decode( int id, Packet dat) {
		while (true) {
			int code = dat.g1();
			if (code == 0) {
				return;
			}

			if (code == 1) {
				this.code1 = dat.g1();
			} else if (code == 2) {
				this.code2 = dat.g1();
			} else if (code == 3) {
				this.hasCode3 = true;
				VarpType.code3[VarpType.code3Count++] = id;
			} else if (code == 4) {
				this.code4 = false;
			} else if (code == 5) {
				this.clientcode = dat.g2();
			} else if (code == 6) {
				this.code6 = true;
			} else if (code == 7) {
				this.code7 = dat.g4();
			} else if (code == 8) {
				this.code8 = true;
			} else if (code == 10) {
				this.code10 = dat.gjstr();
			} else {
				System.out.println("Error unrecognised config code: " + code);
			}
		}
	}
}
