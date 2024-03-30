package jagex2.datastruct;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class Stack {

    private final Hashable head = new Hashable();

    public Stack() {
		this.head.nextHashable = this.head;
		this.head.prevHashable = this.head;
	}

    public void push( Hashable node) {
		if (node.prevHashable != null) {
			node.uncache();
		}

		node.prevHashable = this.head.prevHashable;
		node.nextHashable = this.head;
		node.prevHashable.nextHashable = node;
		node.nextHashable.prevHashable = node;
	}

    public Hashable pop() {
		Hashable node = this.head.nextHashable;
		if (node == this.head) {
			return null;
		} else {
			node.uncache();
			return node;
		}
	}
}
