package jagex2.io;

import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class Jagfile {

    private byte[] buffer;

    private int fileCount;

    private int[] fileHash;

    private int[] fileUnpackedSize;

    private int[] filePackedSize;

    private int[] fileOffset;

    private boolean unpacked;

    public Jagfile( byte[] src) {
		this.load(src);
	}

    private void load( byte[] src) {
		Packet data = new Packet(src);
		int unpackedSize = data.g3();
		int packedSize = data.g3();

		if (packedSize == unpackedSize) {
			this.buffer = src;
			this.unpacked = false;
		} else {
			byte[] temp = new byte[unpackedSize];
			BZip2.read(temp, unpackedSize, src, packedSize, 6);
			this.buffer = temp;

			data = new Packet(this.buffer);
			this.unpacked = true;
		}

		this.fileCount = data.g2();
		this.fileHash = new int[this.fileCount];
		this.fileUnpackedSize = new int[this.fileCount];
		this.filePackedSize = new int[this.fileCount];
		this.fileOffset = new int[this.fileCount];

		int pos = data.pos + this.fileCount * 10;
		for ( int i = 0; i < this.fileCount; i++) {
			this.fileHash[i] = data.g4();
			this.fileUnpackedSize[i] = data.g3();
			this.filePackedSize[i] = data.g3();
			this.fileOffset[i] = pos;
			pos += this.filePackedSize[i];
		}
	}

    public byte[] read( String name, byte[] dst) {
		int hash = 0;
		String upper = name.toUpperCase();
		for ( int i = 0; i < upper.length(); i++) {
			hash = hash * 61 + upper.charAt(i) - 32;
		}

		for ( int i = 0; i < this.fileCount; i++) {
			if (this.fileHash[i] == hash) {
				if (dst == null) {
					dst = new byte[this.fileUnpackedSize[i]];
				}

				if (this.unpacked) {
					if (this.fileUnpackedSize[i] >= 0) {
						System.arraycopy(this.buffer, this.fileOffset[i], dst, 0, this.fileUnpackedSize[i]);
					}
				} else {
					BZip2.read(dst, this.fileUnpackedSize[i], this.buffer, this.filePackedSize[i], this.fileOffset[i]);
				}
				return dst;
			}
		}

		return null;
	}
}
