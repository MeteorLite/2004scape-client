package jagex2.dash3d.type;

import jagex2.datastruct.Linkable;

public class LocSpawned extends Linkable {

    public final int plane;

    public final int layer;

    public final int x;

    public final int z;

    public final int locIndex;

    public final int angle;

    public final int shape;

    public final int lastCycle;

    public LocSpawned( int plane, int layer, int x, int z, int locIndex, int angle, int shape, int lastCycle) {
		this.plane = plane;
		this.layer = layer;
		this.x = x;
		this.z = z;
		this.locIndex = locIndex;
		this.angle = angle;
		this.shape = shape;
		this.lastCycle = lastCycle;
	}
}
