package jagex2.datastruct;

import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;

public class Hashable extends Linkable {

    public Hashable nextHashable;

    public Hashable prevHashable;

    public final void uncache() {
		if (this.prevHashable != null) {
			this.prevHashable.nextHashable = this.nextHashable;
			this.nextHashable.prevHashable = this.prevHashable;
			this.nextHashable = null;
			this.prevHashable = null;
		}
	}
}
