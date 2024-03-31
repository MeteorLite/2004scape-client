package jagex2.dash3d.entity;

import jagex2.config.IdkType;
import jagex2.config.ObjType;
import jagex2.config.SeqType;
import jagex2.config.SpotAnimType;
import jagex2.datastruct.JString;
import jagex2.datastruct.LruCache;
import jagex2.graphics.Model;
import jagex2.io.Packet;

public class PlayerEntity extends PathingEntity {

    public static final int[] DESIGN_HAIR_COLOR = new int[] {
		9104, 10275, 7595, 3610, 7975, 8526, 918, 38802, 24466, 10145, 58654, 5027, 1457, 16565, 34991, 25486
	};

    public static final int[][] DESIGN_BODY_COLOR = new int[][] {
		{ 6798, 107, 10283, 16, 4797, 7744, 5799, 4634, 33697, 22433, 2983, 54193 },
		{ 8741, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003, 25239 },
		{ 25238, 8742, 12, 64030, 43162, 7735, 8404, 1701, 38430, 24094, 10153, 56621, 4783, 1341, 16578, 35003 },
		{ 4626, 11146, 6439, 12, 4758, 10270 },
		{ 4550, 4537, 5681, 5673, 5790, 6806, 8076, 4574 }
	};

    public String name;

    private boolean visible = false;

    private int gender;

    public int headicons;

    private final int[] appearances = new int[12];

    private final int[] colors = new int[5];

    public int combatLevel;

    private long appearanceHashcode;

    public int y;

    public int locStartCycle;

    public int locStopCycle;

    public int locOffsetX;

    public int locOffsetY;

    public int locOffsetZ;

    public Model locModel;

    public int minTileX;

    public int minTileZ;

    public int maxTileX;

    public int maxTileZ;

    public boolean lowMemory = false;

    public static LruCache modelCache = new LruCache(200);

    public void read( Packet buf) {
		buf.pos = 0;

		this.gender = buf.g1();
		this.headicons = buf.g1();

		for ( int part = 0; part < 12; part++) {
			int msb = buf.g1();
			if (msb == 0) {
				this.appearances[part] = 0;
			} else {
				int lsb = buf.g1();
				this.appearances[part] = (msb << 8) + lsb;
			}
		}

		for (int part = 0; part < 5; part++) {
			int color = buf.g1();
			if (color < 0 || color >= DESIGN_BODY_COLOR[part].length) {
				color = 0;
			}

			this.colors[part] = color;
		}

		super.seqStandId = buf.g2();
		if (super.seqStandId == 65535) {
			super.seqStandId = -1;
		}

		super.seqTurnId = buf.g2();
		if (super.seqTurnId == 65535) {
			super.seqTurnId = -1;
		}

		super.seqWalkId = buf.g2();
		if (super.seqWalkId == 65535) {
			super.seqWalkId = -1;
		}

		super.seqTurnAroundId = buf.g2();
		if (super.seqTurnAroundId == 65535) {
			super.seqTurnAroundId = -1;
		}

		super.seqTurnLeftId = buf.g2();
		if (super.seqTurnLeftId == 65535) {
			super.seqTurnLeftId = -1;
		}

		super.seqTurnRightId = buf.g2();
		if (super.seqTurnRightId == 65535) {
			super.seqTurnRightId = -1;
		}

		super.seqRunId = buf.g2();
		if (super.seqRunId == 65535) {
			super.seqRunId = -1;
		}

		this.name = JString.formatName(JString.fromBase37(buf.g8()));
		this.combatLevel = buf.g1();

		this.visible = true;
		this.appearanceHashcode = 0L;
		for (int part = 0; part < 12; part++) {
			this.appearanceHashcode <<= 0x4;

			if (this.appearances[part] >= 256) {
				this.appearanceHashcode += this.appearances[part] - 256;
			}
		}

		if (this.appearances[0] >= 256) {
			this.appearanceHashcode += this.appearances[0] - 256 >> 4;
		}

		if (this.appearances[1] >= 256) {
			this.appearanceHashcode += this.appearances[1] - 256 >> 8;
		}

		for ( int part = 0; part < 5; part++) {
			this.appearanceHashcode <<= 0x3;
			this.appearanceHashcode += this.colors[part];
		}

		this.appearanceHashcode <<= 0x1;
		this.appearanceHashcode += this.gender;
	}

    @Override
    public Model draw(int loopCycle) {
		if (!this.visible) {
			return null;
		}

		Model model = this.getSequencedModel();
		super.height = model.maxY;
		model.pickable = true;

		if (this.lowMemory) {
			return model;
		}

		if (super.spotanimId != -1 && super.spotanimFrame != -1) {
			SpotAnimType spotanim = SpotAnimType.instances[super.spotanimId];
			Model model2 = new Model(spotanim.getModel(), true, !spotanim.disposeAlpha, false);

			model2.translate(-super.spotanimOffset, 0, 0);
			model2.createLabelReferences();
			model2.applyTransform(spotanim.seq.frames[super.spotanimFrame]);
			model2.labelFaces = null;
			model2.labelVertices = null;
			if (spotanim.resizeh != 128 || spotanim.resizev != 128) {
				model2.scale(spotanim.resizeh, spotanim.resizev, spotanim.resizeh);
			}
			model2.calculateNormals(spotanim.ambient + 64, spotanim.contrast + 850, -30, -50, -30, true);

			Model[] models = new Model[] { model, model2 };
			model = new Model(models, 2, true);
		}

		if (this.locModel != null) {
			if (loopCycle >= this.locStopCycle) {
				this.locModel = null;
			}

			if (loopCycle >= this.locStartCycle && loopCycle < this.locStopCycle) {
				Model loc = this.locModel;
				loc.translate(this.locOffsetY - this.y, this.locOffsetX - super.x, this.locOffsetZ - super.z);
				if (super.dstYaw == 512) {
					loc.rotateY90();
					loc.rotateY90();
					loc.rotateY90();
				} else if (super.dstYaw == 1024) {
					loc.rotateY90();
					loc.rotateY90();
				} else if (super.dstYaw == 1536) {
					loc.rotateY90();
				}

				Model[] models = new Model[] { model, loc };
				model = new Model(models, 2, true);
				if (super.dstYaw == 512) {
					loc.rotateY90();
				} else if (super.dstYaw == 1024) {
					loc.rotateY90();
					loc.rotateY90();
				} else if (super.dstYaw == 1536) {
					loc.rotateY90();
					loc.rotateY90();
					loc.rotateY90();
				}

				loc.translate(this.y - this.locOffsetY, super.x - this.locOffsetX, super.z - this.locOffsetZ);
			}
		}

		model.pickable = true;
		return model;
	}

    private Model getSequencedModel() {
		long hashCode = this.appearanceHashcode;
		int primaryTransformId = -1;
		int secondaryTransformId = -1;
		int rightHandValue = -1;
		int leftHandValue = -1;

		if (super.primarySeqId >= 0 && super.primarySeqDelay == 0) {
			SeqType seq = SeqType.instances[super.primarySeqId];

			primaryTransformId = seq.frames[super.primarySeqFrame];
			if (super.secondarySeqId >= 0 && super.secondarySeqId != super.seqStandId) {
				secondaryTransformId = SeqType.instances[super.secondarySeqId].frames[super.secondarySeqFrame];
			}

			if (seq.mainhand >= 0) {
				rightHandValue = seq.mainhand;
				hashCode += (long) rightHandValue - this.appearances[5] << 8;
			}

			if (seq.offhand >= 0) {
				leftHandValue = seq.offhand;
				hashCode += (long) leftHandValue - this.appearances[3] << 16;
			}
		} else if (super.secondarySeqId >= 0) {
			primaryTransformId = SeqType.instances[super.secondarySeqId].frames[super.secondarySeqFrame];
		}

		Model model = (Model) modelCache.get(hashCode);
		if (model == null) {
			Model[] models = new Model[12];
			int modelCount = 0;

			for ( int part = 0; part < 12; part++) {
				int value = this.appearances[part];

				if (leftHandValue >= 0 && part == 3) {
					value = leftHandValue;
				}

				if (rightHandValue >= 0 && part == 5) {
					value = rightHandValue;
				}

				if (value >= 256 && value < 512) {
					models[modelCount++] = IdkType.instances[value - 256].getModel();
				}

				if (value >= 512) {
					ObjType obj = ObjType.get(value - 512);
					Model wornModel = obj.getWornModel(this.gender);

					if (wornModel != null) {
						models[modelCount++] = wornModel;
					}
				}
			}

			model = new Model(models, modelCount);
			for (int part = 0; part < 5; part++) {
				if (this.colors[part] != 0) {
					model.recolor(DESIGN_BODY_COLOR[part][0], DESIGN_BODY_COLOR[part][this.colors[part]]);

					if (part == 1) {
						model.recolor(DESIGN_HAIR_COLOR[0], DESIGN_HAIR_COLOR[this.colors[part]]);
					}
				}
			}

			model.createLabelReferences();
			model.calculateNormals(64, 850, -30, -50, -30, true);
			modelCache.put(hashCode, model);
		}

		if (this.lowMemory) {
			return model;
		}

		Model tmp = new Model(model, true);
		if (primaryTransformId != -1 && secondaryTransformId != -1) {
			tmp.applyTransforms(primaryTransformId, secondaryTransformId, SeqType.instances[super.primarySeqId].labelGroups);
		} else if (primaryTransformId != -1) {
			tmp.applyTransform(primaryTransformId);
		}

		tmp.calculateBoundsCylinder();
		tmp.labelFaces = null;
		tmp.labelVertices = null;
		return tmp;
	}

    public Model getHeadModel() {
		if (!this.visible) {
			return null;
		}

		Model[] models = new Model[12];
		int modelCount = 0;
		for ( int part = 0; part < 12; part++) {
			int value = this.appearances[part];

			if (value >= 256 && value < 512) {
				models[modelCount++] = IdkType.instances[value - 256].getHeadModel();
			}

			if (value >= 512) {
				Model headModel = ObjType.get(value - 512).getHeadModel(this.gender);

				if (headModel != null) {
					models[modelCount++] = headModel;
				}
			}
		}

		Model tmp = new Model(models, modelCount);
		for ( int part = 0; part < 5; part++) {
			if (this.colors[part] != 0) {
				tmp.recolor(DESIGN_BODY_COLOR[part][0], DESIGN_BODY_COLOR[part][this.colors[part]]);

				if (part == 1) {
					tmp.recolor(DESIGN_HAIR_COLOR[0], DESIGN_HAIR_COLOR[this.colors[part]]);
				}
			}
		}

		return tmp;
	}

    @Override
    public boolean isVisible() {
		return this.visible;
	}
}
