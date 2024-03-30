package jagex2.config;

import jagex2.graphics.SeqFrame;
import jagex2.io.Jagfile;
import jagex2.io.Packet;

public class SeqType {

    private static int count;

    public static SeqType[] instances;

    public int frameCount;

    public int[] frames;

    public int[] iframes;

    public int[] delay;

    public int replayoff = -1;

    public int[] labelGroups;

    public boolean stretches = false;

    public int priority = 5;

    public int mainhand = -1;

    public int offhand = -1;

    public int replaycount = 99;

    public static void unpack( Jagfile config) {
		Packet dat = new Packet(config.read("seq.dat", null));
		count = dat.g2();

		if (instances == null) {
			instances = new SeqType[count];
		}

		for ( int id = 0; id < count; id++) {
			if (instances[id] == null) {
				instances[id] = new SeqType();
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
				this.frameCount = dat.g1();
				this.frames = new int[this.frameCount];
				this.iframes = new int[this.frameCount];
				this.delay = new int[this.frameCount];

				for (int i = 0; i < this.frameCount; i++) {
					this.frames[i] = dat.g2();

					this.iframes[i] = dat.g2();
					if (this.iframes[i] == 65535) {
						this.iframes[i] = -1;
					}

					this.delay[i] = dat.g2();
					if (this.delay[i] == 0) {
						this.delay[i] = SeqFrame.instances[this.frames[i]].delay;
					}

					if (this.delay[i] == 0) {
						this.delay[i] = 1;
					}
				}
			} else if (code == 2) {
				this.replayoff = dat.g2();
			} else if (code == 3) {
				int count = dat.g1();
				this.labelGroups = new int[count + 1];

				for ( int i = 0; i < count; i++) {
					this.labelGroups[i] = dat.g1();
				}

				this.labelGroups[count] = 9999999;
			} else if (code == 4) {
				this.stretches = true;
			} else if (code == 5) {
				this.priority = dat.g1();
			} else if (code == 6) {
				this.mainhand = dat.g2();
			} else if (code == 7) {
				this.offhand = dat.g2();
			} else if (code == 8) {
				this.replaycount = dat.g1();
			} else {
				System.out.println("Error unrecognised seq config code: " + code);
			}
		}

		if (this.frameCount == 0) {
			this.frameCount = 1;

			this.frames = new int[1];
			this.frames[0] = -1;

			this.iframes = new int[1];
			this.iframes[0] = -1;

			this.delay = new int[1];
			this.delay[0] = -1;
		}
	}
}
