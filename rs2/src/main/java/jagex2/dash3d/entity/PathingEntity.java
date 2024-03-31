package jagex2.dash3d.entity;

import jagex2.config.SeqType;

public class PathingEntity extends Entity {

    public int x;

    public int z;

    public int yaw;

    public boolean seqStretches = false;

    public int size = 1;

    public int seqStandId = -1;

    public int seqTurnId = -1;

    public int seqWalkId = -1;

    public int seqTurnAroundId = -1;

    public int seqTurnLeftId = -1;

    public int seqTurnRightId = -1;

    public int seqRunId = -1;

    public String chat;

    public int chatTimer = 100;

    public int chatColor;

    public int chatStyle;

    public int damage;

    public int damageType;

    public int combatCycle = -1000;

    public int health;

    public int totalHealth;

    public int targetId = -1;

    public int targetTileX;

    public int targetTileZ;

    public int secondarySeqId = -1;

    public int secondarySeqFrame;

    public int secondarySeqCycle;

    public int primarySeqId = -1;

    public int primarySeqFrame;

    public int primarySeqCycle;

    public int primarySeqDelay;

    public int primarySeqLoop;

    public int spotanimId = -1;

    public int spotanimFrame;

    public int spotanimCycle;

    public int spotanimLastCycle;

    public int spotanimOffset;

    public int forceMoveStartSceneTileX;

    public int forceMoveEndSceneTileX;

    public int forceMoveStartSceneTileZ;

    public int forceMoveEndSceneTileZ;

    public int forceMoveEndCycle;

    public int forceMoveStartCycle;

    public int forceMoveFaceDirection;

    public int cycle;

    public int height;

    public int dstYaw;

    public int pathLength;

    public final int[] pathTileX = new int[10];

    public final int[] pathTileZ = new int[10];

    public final boolean[] pathRunning = new boolean[10];

    public int seqTrigger;

	public int lastMask = -1;
	public int lastMaskCycle = -1;
	public int lastFaceX = -1;
	public int lastFaceZ = -1;

    public final void move( boolean teleport, int x, int z) {
		if (this.primarySeqId != -1 && SeqType.instances[this.primarySeqId].priority <= 1) {
			this.primarySeqId = -1;
		}

		if (!teleport) {
			int dx = x - this.pathTileX[0];
			int dz = z - this.pathTileZ[0];

			if (dx >= -8 && dx <= 8 && dz >= -8 && dz <= 8) {
				if (this.pathLength < 9) {
					this.pathLength++;
				}

				for ( int i = this.pathLength; i > 0; i--) {
					this.pathTileX[i] = this.pathTileX[i - 1];
					this.pathTileZ[i] = this.pathTileZ[i - 1];
					this.pathRunning[i] = this.pathRunning[i - 1];
				}

				this.pathTileX[0] = x;
				this.pathTileZ[0] = z;
				this.pathRunning[0] = false;
				return;
			}
		}

		this.pathLength = 0;
		this.seqTrigger = 0;
		this.pathTileX[0] = x;
		this.pathTileZ[0] = z;
		this.x = this.pathTileX[0] * 128 + this.size * 64;
		this.z = this.pathTileZ[0] * 128 + this.size * 64;
	}

    public final void step( boolean running, int direction) {
		int nextX = this.pathTileX[0];
		int nextZ = this.pathTileZ[0];

		if (direction == 0) {
			nextX--;
			nextZ++;
		} else if (direction == 1) {
			nextZ++;
		} else if (direction == 2) {
			nextX++;
			nextZ++;
		} else if (direction == 3) {
			nextX--;
		} else if (direction == 4) {
			nextX++;
		} else if (direction == 5) {
			nextX--;
			nextZ--;
		} else if (direction == 6) {
			nextZ--;
		} else if (direction == 7) {
			nextX++;
			nextZ--;
		}

		if (this.primarySeqId != -1 && SeqType.instances[this.primarySeqId].priority <= 1) {
			this.primarySeqId = -1;
		}

		if (this.pathLength < 9) {
			this.pathLength++;
		}

		for ( int i = this.pathLength; i > 0; i--) {
			this.pathTileX[i] = this.pathTileX[i - 1];
			this.pathTileZ[i] = this.pathTileZ[i - 1];
			this.pathRunning[i] = this.pathRunning[i - 1];
		}

		this.pathTileX[0] = nextX;
		this.pathTileZ[0] = nextZ;
		this.pathRunning[0] = running;
	}

    public boolean isVisible() {
		return false;
	}
}
