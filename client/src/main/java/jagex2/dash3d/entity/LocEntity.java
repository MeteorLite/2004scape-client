package jagex2.dash3d.entity;

import jagex2.config.SeqType;
import jagex2.datastruct.Linkable;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

public class LocEntity extends Linkable {

    public int heightmapSW;

    public final int heightmapSE;

    public final int heightmapNE;

    public final int heightmapNW;

    public final int index;

    public final SeqType seq;

    public int seqFrame;

    public int seqCycle;

    public LocEntity( int index, int heightmapSW, int heightmapSE, int heightmapNE, int heightmapNW, SeqType seq, boolean randomFrame) {
		this.heightmapSW = heightmapSW;
		this.heightmapSE = heightmapSE;
		this.heightmapNE = heightmapNE;
		this.heightmapNW = heightmapNW;
		this.index = index;
		this.seq = seq;

		if (randomFrame && seq.replayoff != -1) {
			this.seqFrame = (int) (Math.random() * (double) this.seq.frameCount);
			this.seqCycle = (int) (Math.random() * (double) this.seq.delay[this.seqFrame]);
		} else {
			this.seqFrame = -1;
			this.seqCycle = 0;
		}
	}
}
