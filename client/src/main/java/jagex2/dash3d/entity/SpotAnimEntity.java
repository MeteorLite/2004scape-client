package jagex2.dash3d.entity;

import jagex2.config.SpotAnimType;
import jagex2.graphics.Model;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class SpotAnimEntity extends Entity {

    private final SpotAnimType type;

    public final int startCycle;

    public final int level;

    public final int x;

    public final int z;

    public final int y;

    private int seqFrame;

    private int seqCycle;

    public boolean seqComplete = false;

    public SpotAnimEntity( int id, int level, int x, int z, int y, int cycle, int delay) {
		this.type = SpotAnimType.instances[id];
		this.level = level;
		this.x = x;
		this.z = z;
		this.y = y;
		this.startCycle = cycle + delay;
		this.seqComplete = false;
	}

    public void update( int delta) {
		for (this.seqCycle += delta; this.seqCycle > this.type.seq.delay[this.seqFrame]; ) {
			this.seqCycle -= this.type.seq.delay[this.seqFrame] + 1;
			this.seqFrame++;

			if (this.seqFrame >= this.type.seq.frameCount) {
				this.seqFrame = 0;
				this.seqComplete = true;
			}
		}
	}

    @Override
    public Model draw(int loopCycle) {
		Model tmp = this.type.getModel();
		Model model = new Model(tmp, true, !this.type.disposeAlpha, false);

		if (!this.seqComplete) {
			model.createLabelReferences();
			model.applyTransform(this.type.seq.frames[this.seqFrame]);
			model.labelFaces = null;
			model.labelVertices = null;
		}

		if (this.type.resizeh != 128 || this.type.resizev != 128) {
			model.scale(this.type.resizeh, this.type.resizev, this.type.resizeh);
		}

		if (this.type.orientation != 0) {
			if (this.type.orientation == 90) {
				model.rotateY90();
			} else if (this.type.orientation == 180) {
				model.rotateY90();
				model.rotateY90();
			} else if (this.type.orientation == 270) {
				model.rotateY90();
				model.rotateY90();
				model.rotateY90();
			}
		}

		model.calculateNormals(64 + this.type.ambient, 850 + this.type.contrast, -30, -50, -30, true);
		return model;
	}
}
