package jagex2.dash3d;

import jagex2.config.LocType;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class CollisionMap {

    private final int offsetX;

    private final int offsetZ;

    private final int sizeX;

    private final int sizeZ;

    public final int[][] flags;

    public CollisionMap( int sizeX, int sizeZ) {
		this.offsetX = 0;
		this.offsetZ = 0;
		this.sizeX = sizeX;
		this.sizeZ = sizeZ;
		this.flags = new int[this.sizeX][this.sizeZ];
		this.reset();
	}

    public void reset() {
		for ( int x = 0; x < this.sizeX; x++) {
			for ( int z = 0; z < this.sizeZ; z++) {
				if (x == 0 || z == 0 || x == this.sizeX - 1 || z == this.sizeZ - 1) {
					this.flags[x][z] = 0xFFFFFF;
				} else {
					this.flags[x][z] = 0;
				}
			}
		}
	}

    public void addWall( int tileX, int tileZ, int shape, int rotation, boolean blockrange) {
		int x = tileX - this.offsetX;
		int z = tileZ - this.offsetZ;

		if (shape == LocType.WALL_STRAIGHT) {
			if (rotation == 0) {
				this.add(x, z, 0x80);
				this.add(x - 1, z, 0x8);
			} else if (rotation == 1) {
				this.add(x, z, 0x2);
				this.add(x, z + 1, 0x20);
			} else if (rotation == 2) {
				this.add(x, z, 0x8);
				this.add(x + 1, z, 0x80);
			} else if (rotation == 3) {
				this.add(x, z, 32);
				this.add(x, z - 1, 0x2);
			}
		} else if (shape == LocType.WALL_DIAGONALCORNER || shape == LocType.WALL_SQUARECORNER) {
			if (rotation == 0) {
				this.add(x, z, 0x1);
				this.add(x - 1, z + 1, 0x10);
			} else if (rotation == 1) {
				this.add(x, z, 0x4);
				this.add(x + 1, z + 1, 0x40);
			} else if (rotation == 2) {
				this.add(x, z, 0x10);
				this.add(x + 1, z - 1, 0x1);
			} else if (rotation == 3) {
				this.add(x, z, 64);
				this.add(x - 1, z - 1, 0x4);
			}
		} else if (shape == LocType.WALL_L) {
			if (rotation == 0) {
				this.add(x, z, 0x82);
				this.add(x - 1, z, 0x8);
				this.add(x, z + 1, 0x20);
			} else if (rotation == 1) {
				this.add(x, z, 0xA);
				this.add(x, z + 1, 0x20);
				this.add(x + 1, z, 0x80);
			} else if (rotation == 2) {
				this.add(x, z, 0x28);
				this.add(x + 1, z, 0x80);
				this.add(x, z - 1, 0x2);
			} else if (rotation == 3) {
				this.add(x, z, 0xA0);
				this.add(x, z - 1, 0x2);
				this.add(x - 1, z, 0x8);
			}
		}

		if (blockrange) {
			if (shape == LocType.WALL_STRAIGHT) {
				if (rotation == 0) {
					this.add(x, z, 0x10000);
					this.add(x - 1, z, 0x1000);
				} else if (rotation == 1) {
					this.add(x, z, 0x400);
					this.add(x, z + 1, 0x4000);
				} else if (rotation == 2) {
					this.add(x, z, 0x1000);
					this.add(x + 1, z, 0x10000);
				} else if (rotation == 3) {
					this.add(x, z, 0x4000);
					this.add(x, z - 1, 0x400);
				}
			} else if (shape == LocType.WALL_DIAGONAL || shape == LocType.WALL_SQUARECORNER) {
				if (rotation == 0) {
					this.add(x, z, 0x200);
					this.add(x - 1, z + 1, 0x2000);
				} else if (rotation == 1) {
					this.add(x, z, 0x800);
					this.add(x + 1, z + 1, 0x8000);
				} else if (rotation == 2) {
					this.add(x, z, 0x2000);
					this.add(x + 1, z - 1, 0x200);
				} else if (rotation == 3) {
					this.add(x, z, 0x8000);
					this.add(x - 1, z - 1, 0x800);
				}
			} else if (shape == LocType.WALL_L) {
				if (rotation == 0) {
					this.add(x, z, 0x10400);
					this.add(x - 1, z, 0x1000);
					this.add(x, z + 1, 0x4000);
				} else if (rotation == 1) {
					this.add(x, z, 0x1400);
					this.add(x, z + 1, 0x4000);
					this.add(x + 1, z, 0x10000);
				} else if (rotation == 2) {
					this.add(x, z, 0x5000);
					this.add(x + 1, z, 0x10000);
					this.add(x, z - 1, 0x400);
				} else if (rotation == 3) {
					this.add(x, z, 0x14000);
					this.add(x, z - 1, 0x400);
					this.add(x - 1, z, 0x1000);
				}
			}
		}
	}

    public void addLoc( int tileX, int tileZ, int sizeX, int sizeZ, int rotation, boolean blockrange) {
		int flags = 0x100;
		if (blockrange) {
			flags += 0x20000;
		}

		int x = tileX - this.offsetX;
		int z = tileZ - this.offsetZ;

		if (rotation == 1 || rotation == 3) {
			int tmp = sizeX;
			sizeX = sizeZ;
			sizeZ = tmp;
		}

		for (int tx = x; tx < x + sizeX; tx++) {
			if (tx < 0 || tx >= this.sizeX) {
				continue;
			}

			for (int tz = z; tz < z + sizeZ; tz++) {
				if (tz < 0 || tz >= this.sizeZ) {
					continue;
				}

				this.add(tx, tz, flags);
			}
		}
	}

    public void setBlocked( int tileX, int tileZ) {
		int x = tileX - this.offsetX;
		int z = tileZ - this.offsetZ;
		this.flags[x][z] |= 0x200000;
	}

    private void add( int x, int z, int flags) {
		this.flags[x][z] |= flags;
	}

    public void removeWall( int tileX, int tileZ, int shape, int rotation, boolean blockrange) {
		int x = tileX - this.offsetX;
		int z = tileZ - this.offsetZ;

		if (shape == LocType.WALL_STRAIGHT) {
			if (rotation == 0) {
				this.remove(x, z, 0x80);
				this.remove(x - 1, z, 0x8);
			} else if (rotation == 1) {
				this.remove(x, z, 0x2);
				this.remove(x, z + 1, 0x20);
			} else if (rotation == 2) {
				this.remove(x, z, 0x8);
				this.remove(x + 1, z, 0x80);
			} else if (rotation == 3) {
				this.remove(x, z, 0x20);
				this.remove(x, z - 1, 0x2);
			}
		} else if (shape == LocType.WALL_DIAGONALCORNER || shape == LocType.WALL_SQUARECORNER) {
			if (rotation == 0) {
				this.remove(x, z, 0x1);
				this.remove(x - 1, z + 1, 0x10);
			} else if (rotation == 1) {
				this.remove(x, z, 0x4);
				this.remove(x + 1, z + 1, 0x40);
			} else if (rotation == 2) {
				this.remove(x, z, 0x10);
				this.remove(x + 1, z - 1, 0x1);
			} else if (rotation == 3) {
				this.remove(x, z, 0x40);
				this.remove(x - 1, z - 1, 0x4);
			}
		} else if (shape == LocType.WALL_L) {
			if (rotation == 0) {
				this.remove(x, z, 0x82);
				this.remove(x - 1, z, 0x8);
				this.remove(x, z + 1, 0x20);
			} else if (rotation == 1) {
				this.remove(x, z, 0xA);
				this.remove(x, z + 1, 0x20);
				this.remove(x + 1, z, 0x80);
			} else if (rotation == 2) {
				this.remove(x, z, 0x28);
				this.remove(x + 1, z, 0x80);
				this.remove(x, z - 1, 0x2);
			} else if (rotation == 3) {
				this.remove(x, z, 0xA0);
				this.remove(x, z - 1, 0x2);
				this.remove(x - 1, z, 0x8);
			}
		}

		if (blockrange) {
			if (shape == LocType.WALL_STRAIGHT) {
				if (rotation == 0) {
					this.remove(x, z, 0x10000);
					this.remove(x - 1, z, 0x1000);
				} else if (rotation == 1) {
					this.remove(x, z, 0x400);
					this.remove(x, z + 1, 0x4000);
				} else if (rotation == 2) {
					this.remove(x, z, 0x1000);
					this.remove(x + 1, z, 0x10000);
				} else if (rotation == 3) {
					this.remove(x, z, 0x4000);
					this.remove(x, z - 1, 0x400);
				}
			} else if (shape == LocType.WALL_DIAGONALCORNER || shape == LocType.WALL_SQUARECORNER) {
				if (rotation == 0) {
					this.remove(x, z, 0x200);
					this.remove(x - 1, z + 1, 0x2000);
				} else if (rotation == 1) {
					this.remove(x, z, 0x800);
					this.remove(x + 1, z + 1, 0x8000);
				} else if (rotation == 2) {
					this.remove(x, z, 0x2000);
					this.remove(x + 1, z - 1, 0x200);
				} else if (rotation == 3) {
					this.remove(x, z, 0x8000);
					this.remove(x - 1, z - 1, 0x800);
				}
			} else if (shape == LocType.WALL_L) {
				if (rotation == 0) {
					this.remove(x, z, 0x10400);
					this.remove(x - 1, z, 0x1000);
					this.remove(x, z + 1, 0x4000);
				} else if (rotation == 1) {
					this.remove(x, z, 0x1400);
					this.remove(x, z + 1, 0x4000);
					this.remove(x + 1, z, 0x10000);
				} else if (rotation == 2) {
					this.remove(x, z, 0x5000);
					this.remove(x + 1, z, 0x10000);
					this.remove(x, z - 1, 0x400);
				} else if (rotation == 3) {
					this.remove(x, z, 0x14000);
					this.remove(x, z - 1, 0x400);
					this.remove(x - 1, z, 0x1000);
				}
			}
		}
	}

    public void removeLoc( int tileX, int tileZ, int sizeX, int sizeZ, int rotation, boolean blockrange) {
		int flags = 0x100;
		if (blockrange) {
			flags += 0x20000;
		}

		int x = tileX - this.offsetX;
		int z = tileZ - this.offsetZ;

		if (rotation == 1 || rotation == 3) {
			int tmp = sizeX;
			sizeX = sizeZ;
			sizeZ = tmp;
		}

		for (int tx = x; tx < x + sizeX; tx++) {
			if (tx < 0 || tx >= this.sizeX) {
				continue;
			}

			for ( int tz = z; tz < z + sizeZ; tz++) {
				if (tz < 0 || tz >= this.sizeZ) {
					continue;
				}

				this.remove(tx, tz, flags);
			}
		}
	}

    private void remove( int x, int z, int flags) {
		this.flags[x][z] &= 0xFFFFFF - flags;
	}

    public void removeBlocked( int tileX, int tileZ) {
		int x = tileX - this.offsetX;
		int z = tileZ - this.offsetZ;
		this.flags[x][z] &= 0xDFFFFF;
	}

    public boolean reachedWall( int sourceX, int sourceZ, int destX, int destZ, int shape, int rotation) {
		if (sourceX == destX && sourceZ == destZ) {
			return true;
		}

		int sx = sourceX - this.offsetX;
		int sz = sourceZ - this.offsetZ;
		int dx = destX - this.offsetX;
		int dz = destZ - this.offsetZ;

		if (shape == LocType.WALL_STRAIGHT) {
			if (rotation == 0) {
				if (sx == dx - 1 && sz == dz) {
					return true;
				} else if (sx == dx && sz == dz + 1 && (this.flags[sx][sz] & 0x280120) == 0) {
					return true;
				} else if (sx == dx && sz == dz - 1 && (this.flags[sx][sz] & 0x280102) == 0) {
					return true;
				}
			} else if (rotation == 1) {
				if (sx == dx && sz == dz + 1) {
					return true;
				} else if (sx == dx - 1 && sz == dz && (this.flags[sx][sz] & 0x280108) == 0) {
					return true;
				} else if (sx == dx + 1 && sz == dz && (this.flags[sx][sz] & 0x280180) == 0) {
					return true;
				}
			} else if (rotation == 2) {
				if (sx == dx + 1 && sz == dz) {
					return true;
				} else if (sx == dx && sz == dz + 1 && (this.flags[sx][sz] & 0x280120) == 0) {
					return true;
				} else if (sx == dx && sz == dz - 1 && (this.flags[sx][sz] & 0x280102) == 0) {
					return true;
				}
			} else if (rotation == 3) {
				if (sx == dx && sz == dz - 1) {
					return true;
				} else if (sx == dx - 1 && sz == dz && (this.flags[sx][sz] & 0x280108) == 0) {
					return true;
				} else if (sx == dx + 1 && sz == dz && (this.flags[sx][sz] & 0x280180) == 0) {
					return true;
				}
			}
		} else if (shape == LocType.WALL_L) {
			if (rotation == 0) {
				if (sx == dx - 1 && sz == dz) {
					return true;
				} else if (sx == dx && sz == dz + 1) {
					return true;
				} else if (sx == dx + 1 && sz == dz && (this.flags[sx][sz] & 0x280180) == 0) {
					return true;
				} else if (sx == dx && sz == dz - 1 && (this.flags[sx][sz] & 0x280102) == 0) {
					return true;
				}
			} else if (rotation == 1) {
				if (sx == dx - 1 && sz == dz && (this.flags[sx][sz] & 0x280108) == 0) {
					return true;
				} else if (sx == dx && sz == dz + 1) {
					return true;
				} else if (sx == dx + 1 && sz == dz) {
					return true;
				} else if (sx == dx && sz == dz - 1 && (this.flags[sx][sz] & 0x280102) == 0) {
					return true;
				}
			} else if (rotation == 2) {
				if (sx == dx - 1 && sz == dz && (this.flags[sx][sz] & 0x280108) == 0) {
					return true;
				} else if (sx == dx && sz == dz + 1 && (this.flags[sx][sz] & 0x280120) == 0) {
					return true;
				} else if (sx == dx + 1 && sz == dz) {
					return true;
				} else if (sx == dx && sz == dz - 1) {
					return true;
				}
			} else if (rotation == 3) {
				if (sx == dx - 1 && sz == dz) {
					return true;
				} else if (sx == dx && sz == dz + 1 && (this.flags[sx][sz] & 0x280120) == 0) {
					return true;
				} else if (sx == dx + 1 && sz == dz && (this.flags[sx][sz] & 0x280180) == 0) {
					return true;
				} else if (sx == dx && sz == dz - 1) {
					return true;
				}
			}
		} else if (shape == LocType.WALL_DIAGONAL) {
			if (sx == dx && sz == dz + 1 && (this.flags[sx][sz] & 0x20) == 0) {
				return true;
			} else if (sx == dx && sz == dz - 1 && (this.flags[sx][sz] & 0x2) == 0) {
				return true;
			} else if (sx == dx - 1 && sz == dz && (this.flags[sx][sz] & 0x8) == 0) {
				return true;
			} else if (sx == dx + 1 && sz == dz && (this.flags[sx][sz] & 0x80) == 0) {
				return true;
			}
		}

		return false;
	}

    public boolean reachedWallDecoration( int sourceX, int sourceZ, int destX, int destZ, int shape, int rotation) {
		if (sourceX == destX && sourceZ == destZ) {
			return true;
		}

		int sx = sourceX - this.offsetX;
		int sz = sourceZ - this.offsetZ;
		int dx = destX - this.offsetX;
		int dz = destZ - this.offsetZ;

		if (shape == LocType.WALLDECOR_DIAGONAL_NOOFFSET || shape == LocType.WALLDECOR_DIAGONAL_OFFSET) {
			if (shape == LocType.WALLDECOR_DIAGONAL_OFFSET) {
				rotation = rotation + 2 & 0x3;
			}

			if (rotation == 0) {
				if (sx == dx + 1 && sz == dz && (this.flags[sx][sz] & 0x80) == 0) {
					return true;
				} else if (sx == dx && sz == dz - 1 && (this.flags[sx][sz] & 0x2) == 0) {
					return true;
				}
			} else if (rotation == 1) {
				if (sx == dx - 1 && sz == dz && (this.flags[sx][sz] & 0x8) == 0) {
					return true;
				} else if (sx == dx && sz == dz - 1 && (this.flags[sx][sz] & 0x2) == 0) {
					return true;
				}
			} else if (rotation == 2) {
				if (sx == dx - 1 && sz == dz && (this.flags[sx][sz] & 0x8) == 0) {
					return true;
				} else if (sx == dx && sz == dz + 1 && (this.flags[sx][sz] & 0x20) == 0) {
					return true;
				}
			} else if (rotation == 3) {
				if (sx == dx + 1 && sz == dz && (this.flags[sx][sz] & 0x80) == 0) {
					return true;
				} else if (sx == dx && sz == dz + 1 && (this.flags[sx][sz] & 0x20) == 0) {
					return true;
				}
			}
		} else if (shape == LocType.WALLDECOR_DIAGONAL_BOTH) {
			if (sx == dx && sz == dz + 1 && (this.flags[sx][sz] & 0x20) == 0) {
				return true;
			} else if (sx == dx && sz == dz - 1 && (this.flags[sx][sz] & 0x2) == 0) {
				return true;
			} else if (sx == dx - 1 && sz == dz && (this.flags[sx][sz] & 0x8) == 0) {
				return true;
			} else if (sx == dx + 1 && sz == dz && (this.flags[sx][sz] & 0x80) == 0) {
				return true;
			}
		}

		return false;
	}

    public boolean reachedLoc( int srcX, int srcZ, int dstX, int dstZ, int dstSizeX, int dstSizeZ, int forceapproach) {
		int maxX = dstX + dstSizeX - 1;
		int maxZ = dstZ + dstSizeZ - 1;

		if (srcX >= dstX && srcX <= maxX && srcZ >= dstZ && srcZ <= maxZ) {
			return true;
		} else if (srcX == dstX - 1 && srcZ >= dstZ && srcZ <= maxZ && (this.flags[srcX - this.offsetX][srcZ - this.offsetZ] & 0x8) == 0 && (forceapproach & 0x8) == 0) {
			return true;
		} else if (srcX == maxX + 1 && srcZ >= dstZ && srcZ <= maxZ && (this.flags[srcX - this.offsetX][srcZ - this.offsetZ] & 0x80) == 0 && (forceapproach & 0x2) == 0) {
			return true;
		} else if (srcZ == dstZ - 1 && srcX >= dstX && srcX <= maxX && (this.flags[srcX - this.offsetX][srcZ - this.offsetZ] & 0x2) == 0 && (forceapproach & 0x4) == 0) {
			return true;
		} else if (srcZ == maxZ + 1 && srcX >= dstX && srcX <= maxX && (this.flags[srcX - this.offsetX][srcZ - this.offsetZ] & 0x20) == 0 && (forceapproach & 0x1) == 0) {
			return true;
		}

		return false;
	}
}
