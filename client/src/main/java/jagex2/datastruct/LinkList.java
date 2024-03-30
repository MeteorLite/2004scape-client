package jagex2.datastruct;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class LinkList {

    private final Linkable head = new Linkable();

    private Linkable peeked;

    public LinkList() {
		this.head.prev = this.head;
		this.head.next = this.head;
	}

    public void pushBack( Linkable node) {
		if (node.next != null) {
			node.unlink();
		}

		node.next = this.head.next;
		node.prev = this.head;
		node.next.prev = node;
		node.prev.next = node;
	}

    public void pushFront( Linkable node) {
		if (node.next != null) {
			node.unlink();
		}

		node.next = this.head;
		node.prev = this.head.prev;
		node.next.prev = node;
		node.prev.next = node;
	}

    public Linkable pollFront() {
		Linkable node = this.head.prev;
		if (node == this.head) {
			return null;
		}
		node.unlink();
		return node;
	}

    public Linkable peekFront() {
		Linkable node = this.head.prev;
		if (node == this.head) {
			this.peeked = null;
			return null;
		}
		this.peeked = node.prev;
		return node;
	}

    public Linkable peekBack() {
		Linkable node = this.head.next;
		if (node == this.head) {
			this.peeked = null;
			return null;
		}
		this.peeked = node.next;
		return node;
	}

    public Linkable prev() {
		Linkable node = this.peeked;
		if (node == this.head) {
			this.peeked = null;
			return null;
		}
		this.peeked = node.prev;
		return node;
	}

    public Linkable next() {
		Linkable node = this.peeked;
		if (node == this.head) {
			this.peeked = null;
			return null;
		}
		this.peeked = node.next;
		return node;
	}

    public void clear() {
		while (true) {
			Linkable node = this.head.prev;
			if (node == this.head) {
				return;
			}
			node.unlink();
		}
	}
}
