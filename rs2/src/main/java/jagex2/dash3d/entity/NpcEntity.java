package jagex2.dash3d.entity;

import jagex2.config.NpcType;
import jagex2.config.SeqType;
import jagex2.config.SpotAnimType;
import jagex2.graphics.Model;

public class NpcEntity extends PathingEntity {

    public NpcType type;

    @Override
    public Model draw(int loopCycle) {
		if (this.type == null) {
			return null;
		}

		if (super.spotanimId == -1 || super.spotanimFrame == -1) {
			return this.getSequencedModel();
		}

		Model model = this.getSequencedModel();
		SpotAnimType spotanim = SpotAnimType.instances[super.spotanimId];

		Model model1 = new Model(spotanim.getModel(), true, !spotanim.disposeAlpha, false);
		model1.translate(-super.spotanimOffset, 0, 0);
		model1.createLabelReferences();
		model1.applyTransform(spotanim.seq.frames[super.spotanimFrame]);
		model1.labelFaces = null;
		model1.labelVertices = null;

		if (spotanim.resizeh != 128 || spotanim.resizev != 128) {
			model1.scale(spotanim.resizeh, spotanim.resizev, spotanim.resizeh);
		}

		model1.calculateNormals(64 + spotanim.ambient, 850 + spotanim.contrast, -30, -50, -30, true);
		Model[] models = new Model[] { model, model1 };

		Model tmp = new Model(models, 2, true);
		if (this.type.size == 1) {
			tmp.pickable = true;
		}

		return tmp;
	}

    private Model getSequencedModel() {
		if (super.primarySeqId >= 0 && super.primarySeqDelay == 0) {
			int primaryTransformId = SeqType.instances[super.primarySeqId].frames[super.primarySeqFrame];
			int secondaryTransformId = -1;
			if (super.secondarySeqId >= 0 && super.secondarySeqId != super.seqStandId) {
				secondaryTransformId = SeqType.instances[super.secondarySeqId].frames[super.secondarySeqFrame];
			}
			return this.type.getSequencedModel(primaryTransformId, secondaryTransformId, SeqType.instances[super.primarySeqId].labelGroups);
		}

		int transformId = -1;
		if (super.secondarySeqId >= 0) {
			transformId = SeqType.instances[super.secondarySeqId].frames[super.secondarySeqFrame];
		}

		Model model = this.type.getSequencedModel(transformId, -1, null);
		super.height = model.maxY;
		return model;
	}

    @Override
    public boolean isVisible() {
		return this.type != null;
	}
}
