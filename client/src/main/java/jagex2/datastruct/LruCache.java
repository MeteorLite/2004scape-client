package jagex2.datastruct;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class LruCache {

    private final int capacity;

    private int available;

    private final HashTable hashtable = new HashTable(1024);

    private final Stack history = new Stack();

    public LruCache( int size) {
		this.capacity = size;
		this.available = size;
	}

    public Hashable get( long key) {
		Hashable node = (Hashable) this.hashtable.get(key);
		if (node != null) {
			this.history.push(node);
		}

		return node;
	}

    public void put( long key, Hashable value) {
		if (this.available == 0) {
			Hashable node = this.history.pop();
			node.unlink();
			node.uncache();
		} else {
			this.available--;
		}

		this.hashtable.put(key, value);
		this.history.push(value);
	}

    public void clear() {
		while (true) {
			Hashable node = this.history.pop();
			if (node == null) {
				this.available = this.capacity;
				return;
			}

			node.unlink();
			node.uncache();
		}
	}
}
