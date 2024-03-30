package jagex2.io;

import jagex2.datastruct.Hashable;
import jagex2.datastruct.LinkList;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

import java.math.BigInteger;

public class Packet extends Hashable {

    private static final int[] crctable = new int[256];

    public static final int[] BITMASK = new int[33];

    public byte[] data;

    public int pos;

    public int bitPos;

    public Isaac random;

    public static int cacheMinCount;

    public static int cacheMidCount;

    public static int cacheMaxCount;

    public static final LinkList cacheMin = new LinkList();

    public static final LinkList cacheMid = new LinkList();

    public static final LinkList cacheMax = new LinkList();

	static {
		for (int i = 0; i < 32; i++) {
			BITMASK[i] = (1 << i) - 1;
		}
		BITMASK[32] = 0xFFFFFFFF;

		for ( int b = 0; b < 256; b++) {
			int remainder = b;

			for ( int bit = 0; bit < 8; bit++) {
				if ((remainder & 0x1) == 1) {
					remainder = remainder >>> 1 ^ 0xEDB88320;
				} else {
					remainder >>>= 0x1;
				}
			}

			crctable[b] = remainder;
		}
	}

    public Packet() {
	}

    public Packet( byte[] src) {
		this.data = src;
		this.pos = 0;
	}

    public static Packet alloc( int type) {
		synchronized (cacheMid) {
			Packet cached = null;
			if (type == 0 && cacheMinCount > 0) {
				cacheMinCount--;
				cached = (Packet) cacheMin.pollFront();
			} else if (type == 1 && cacheMidCount > 0) {
				cacheMidCount--;
				cached = (Packet) cacheMid.pollFront();
			} else if (type == 2 && cacheMaxCount > 0) {
				cacheMaxCount--;
				cached = (Packet) cacheMax.pollFront();
			}

			if (cached != null) {
				cached.pos = 0;
				return cached;
			}
		}

		Packet packet = new Packet();
		packet.pos = 0;
		if (type == 0) {
			packet.data = new byte[100];
		} else if (type == 1) {
			packet.data = new byte[5000];
		} else {
			packet.data = new byte[30000];
		}
		return packet;
	}

    public void release() {
		synchronized (cacheMid) {
			this.pos = 0;

			if (this.data.length == 100 && cacheMinCount < 1000) {
				cacheMin.pushBack(this);
				cacheMinCount++;
			} else if (this.data.length == 5000 && cacheMidCount < 250) {
				cacheMid.pushBack(this);
				cacheMidCount++;
			} else if (this.data.length == 30000 && cacheMaxCount < 50) {
				cacheMax.pushBack(this);
				cacheMaxCount++;
			}
		}
	}

    public void p1isaac( int opcode) {
		this.data[this.pos++] = (byte) (opcode + this.random.nextInt());
	}

    public void p1( int value) {
		this.data[this.pos++] = (byte) value;
	}

    public void p2( int value) {
		this.data[this.pos++] = (byte) (value >> 8);
		this.data[this.pos++] = (byte) value;
	}

    public void ip2( int value) {
		this.data[this.pos++] = (byte) value;
		this.data[this.pos++] = (byte) (value >> 8);
	}

    public void p3( int value) {
		this.data[this.pos++] = (byte) (value >> 16);
		this.data[this.pos++] = (byte) (value >> 8);
		this.data[this.pos++] = (byte) value;
	}

    public void p4( int value) {
		this.data[this.pos++] = (byte) (value >> 24);
		this.data[this.pos++] = (byte) (value >> 16);
		this.data[this.pos++] = (byte) (value >> 8);
		this.data[this.pos++] = (byte) value;
	}

    public void ip4( int value) {
		this.data[this.pos++] = (byte) value;
		this.data[this.pos++] = (byte) (value >> 8);
		this.data[this.pos++] = (byte) (value >> 16);
		this.data[this.pos++] = (byte) (value >> 24);
	}

    public void p8( long value) {
		this.data[this.pos++] = (byte) (value >> 56);
		this.data[this.pos++] = (byte) (value >> 48);
		this.data[this.pos++] = (byte) (value >> 40);
		this.data[this.pos++] = (byte) (value >> 32);
		this.data[this.pos++] = (byte) (value >> 24);
		this.data[this.pos++] = (byte) (value >> 16);
		this.data[this.pos++] = (byte) (value >> 8);
		this.data[this.pos++] = (byte) value;
	}

    public void pjstr( String str) {
		str.getBytes(0, str.length(), this.data, this.pos);
		this.pos += str.length();
		this.data[this.pos++] = 10;
	}

    public void pdata( byte[] src, int length, int offset) {
		for ( int i = offset; i < offset + length; i++) {
			this.data[this.pos++] = src[i];
		}
	}

    public void psize1( int size) {
		this.data[this.pos - size - 1] = (byte) size;
	}

    public int g1() {
		return this.data[this.pos++] & 0xFF;
	}

    public byte g1b() {
		return this.data[this.pos++];
	}

    public int g2() {
		this.pos += 2;
		return ((this.data[this.pos - 2] & 0xFF) << 8) + (this.data[this.pos - 1] & 0xFF);
	}

    public int g2b() {
		this.pos += 2;
		int value = ((this.data[this.pos - 2] & 0xFF) << 8) + (this.data[this.pos - 1] & 0xFF);
		if (value > 32767) {
			value -= 65536;
		}
		return value;
	}

    public int g3() {
		this.pos += 3;
		return ((this.data[this.pos - 3] & 0xFF) << 16) + ((this.data[this.pos - 2] & 0xFF) << 8) + (this.data[this.pos - 1] & 0xFF);
	}

    public int g4() {
		this.pos += 4;
		return ((this.data[this.pos - 4] & 0xFF) << 24) + ((this.data[this.pos - 3] & 0xFF) << 16) + ((this.data[this.pos - 2] & 0xFF) << 8) + (this.data[this.pos - 1] & 0xFF);
	}

    public long g8() {
		long high = (long) this.g4() & 0xFFFFFFFFL;
		long low = (long) this.g4() & 0xFFFFFFFFL;
		return (high << 32) + low;
	}

    public String gjstr() {
		int start = this.pos;
		while (this.data[this.pos++] != 10) {}

		return new String(this.data, start, this.pos - start - 1);
	}

    public byte[] gstrbyte() {
		int start = this.pos;
		while (this.data[this.pos++] != 10) {}

		byte[] temp = new byte[this.pos - start - 1];
		if (this.pos - 1 - start >= 0) System.arraycopy(this.data, start, temp, 0, this.pos - 1 - start);
		return temp;
	}

    public void gdata( int length, int offset, byte[] dest) {
		for ( int i = offset; i < offset + length; i++) {
			dest[i] = this.data[this.pos++];
		}
	}

    public void accessBits() {
		this.bitPos = this.pos * 8;
	}

    public int gBit( int n) {
		int bytePos = this.bitPos >> 3;
		int remainingBits = 8 - (this.bitPos & 0x7);

		int value = 0;
		this.bitPos += n;

		while (n > remainingBits) {
			value += (this.data[bytePos++] & BITMASK[remainingBits]) << n - remainingBits;
			n -= remainingBits;
			remainingBits = 8;
		}

		if (n == remainingBits) {
			value += this.data[bytePos] & BITMASK[remainingBits];
		} else {
			value += this.data[bytePos] >> remainingBits - n & BITMASK[n];
		}

		return value;
	}

    public void accessBytes() {
		this.pos = (this.bitPos + 7) / 8;
	}

    public int gsmart() {
		int value = this.data[this.pos] & 0xFF;
		return value < 128 ? this.g1() - 64 : this.g2() - 49152;
	}

    public int gsmarts() {
		int value = this.data[this.pos] & 0xFF;
		return value < 128 ? this.g1() : this.g2() - 32768;
	}

    public void rsaenc( BigInteger mod, BigInteger exp) {
		int length = this.pos;
		this.pos = 0;

		byte[] temp = new byte[length];
		this.gdata(length, 0, temp);
		BigInteger bigRaw = new BigInteger(temp);
		BigInteger bigEnc = bigRaw.modPow(exp, mod);
		byte[] rawEnc = bigEnc.toByteArray();

		this.pos = 0;
		this.p1(rawEnc.length);
		this.pdata(rawEnc, rawEnc.length, 0);
	}
}
