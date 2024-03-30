package jagex2.dash3d.entity;

import jagex2.config.SpotAnimType;
import jagex2.graphics.Model;

public class ProjectileEntity extends Entity {

    private final SpotAnimType spotanim;

    public final int level;

    private final int srcX;

    private final int srcZ;

    private final int srcY;

    public final int offsetY;

    public final int startCycle;

    public final int lastCycle;

    private final int peakPitch;

    private final int arc;

    public final int target;

    private boolean mobile = false;

    public double x;

    public double z;

    public double y;

    private double velocityX;

    private double velocityZ;

    private double velocity;

    private double velocityY;

    private double accelerationY;

    public int yaw;

    private int pitch;

    private int seqFrame;

    private int seqCycle;

    public ProjectileEntity( int spotanim, int level, int srcX, int srcY, int srcZ, int startCycle, int lastCycle, int peakPitch, int arc, int target, int offsetY) {
		this.spotanim = SpotAnimType.instances[spotanim];
		this.level = level;
		this.srcX = srcX;
		this.srcZ = srcZ;
		this.srcY = srcY;
		this.startCycle = startCycle;
		this.lastCycle = lastCycle;
		this.peakPitch = peakPitch;
		this.arc = arc;
		this.target = target;
		this.offsetY = offsetY;
		this.mobile = false;
	}

    public void updateVelocity( int dstX, int dstY, int dstZ, int cycle) {
		if (!this.mobile) {
			double dx = dstX - this.srcX;
			double dz = dstZ - this.srcZ;
			double d = Math.sqrt(dx * dx + dz * dz);

			this.x = (double) this.srcX + dx * (double) this.arc / d;
			this.z = (double) this.srcZ + dz * (double) this.arc / d;
			this.y = this.srcY;
		}

		double dt = this.lastCycle + 1 - cycle;
		this.velocityX = ((double) dstX - this.x) / dt;
		this.velocityZ = ((double) dstZ - this.z) / dt;
		this.velocity = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);

		if (!this.mobile) {
			this.velocityY = -this.velocity * Math.tan((double) this.peakPitch * 0.02454369D);
		}

		this.accelerationY = ((double) dstY - this.y - this.velocityY * dt) * 2.0D / (dt * dt);
	}

    public void update( int delta) {
		this.mobile = true;
		this.x += this.velocityX * (double) delta;
		this.z += this.velocityZ * (double) delta;
		this.y += this.velocityY * (double) delta + this.accelerationY * 0.5D * (double) delta * (double) delta;
		this.velocityY += this.accelerationY * (double) delta;
		this.yaw = (int) (Math.atan2(this.velocityX, this.velocityZ) * 325.949D) + 1024 & 0x7FF;
		this.pitch = (int) (Math.atan2(this.velocityY, this.velocity) * 325.949D) & 0x7FF;

		if (this.spotanim.seq != null) {
			this.seqCycle += delta;

			while (this.seqCycle > this.spotanim.seq.delay[this.seqFrame]) {
				this.seqCycle -= this.spotanim.seq.delay[this.seqFrame] + 1;
				this.seqFrame++;
				if (this.seqFrame >= this.spotanim.seq.frameCount) {
					this.seqFrame = 0;
				}
			}
		}
	}

    @Override
    public Model draw(int loopCycle) {
		Model tmp = this.spotanim.getModel();
		Model model = new Model(tmp, true, !this.spotanim.disposeAlpha, false);

		if (this.spotanim.seq != null) {
			model.createLabelReferences();
			model.applyTransform(this.spotanim.seq.frames[this.seqFrame]);
			model.labelFaces = null;
			model.labelVertices = null;
		}

		if (this.spotanim.resizeh != 128 || this.spotanim.resizev != 128) {
			model.scale(this.spotanim.resizeh, this.spotanim.resizev, this.spotanim.resizeh);
		}

		model.rotateX(this.pitch);
		model.calculateNormals(64 + this.spotanim.ambient, 850 + this.spotanim.contrast, -30, -50, -30, true);
		return model;
	}
}
