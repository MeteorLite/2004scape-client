package jagex2.datastruct;

public class HashTable {

    private final int size;

    private final Linkable[] nodes;

    public HashTable( int size) {
		this.size = size;
		this.nodes = new Linkable[size];

		for ( int i = 0; i < size; i++) {
			Linkable node = this.nodes[i] = new Linkable();
			node.prev = node;
			node.next = node;
		}
	}

    public Linkable get( long key) {
		Linkable start = this.nodes[(int) (key & (long) (this.size - 1))];

		for ( Linkable node = start.prev; node != start; node = node.prev) {
			if (node.id == key) {
				return node;
			}
		}

		return null;
	}

    public void put( long key, Linkable value) {
		if (value.next != null) {
			value.unlink();
		}

		Linkable node = this.nodes[(int) (key & (long) (this.size - 1))];
		value.next = node.next;
		value.prev = node;
		value.next.prev = value;
		value.prev.next = value;
		value.id = key;
	}
}
