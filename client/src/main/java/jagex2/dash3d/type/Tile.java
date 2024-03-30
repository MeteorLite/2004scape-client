package jagex2.dash3d.type;

import jagex2.datastruct.Linkable;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

public class Tile extends Linkable {

    public int level;

    public final int x;

    public final int z;

    public final int occludeLevel;

    public TileUnderlay underlay;

    public TileOverlay overlay;

    public Wall wall;

    public WallDecoration wallDecoration;

    public GroundDecoration groundDecoration;

    public ObjStack objStack;

    public int locCount;

    public final Loc[] locs = new Loc[5];

    public final int[] locSpan = new int[5];

    public int locSpans;

    public int drawLevel;

    public boolean visible;

    public boolean update;

    public boolean containsLocs;

    public int checkLocSpans;

    public int blockLocSpans;

    public int inverseBlockLocSpans;

    public int backWallTypes;

    public Tile bridge;

    public Tile( int level, int x, int z) {
		this.occludeLevel = this.level = level;
		this.x = x;
		this.z = z;
	}
}
