package jagex2.dash3d;

import jagex2.config.FloType;
import jagex2.config.LocType;
import jagex2.config.SeqType;
import jagex2.dash3d.entity.LocEntity;
import jagex2.datastruct.LinkList;
import jagex2.graphics.Draw3D;
import jagex2.graphics.Model;
import jagex2.io.Packet;

public class World {

    public static boolean lowMemory = true;

    public static int levelBuilt;

    public static boolean fullbright;

    private final int maxTileX;

    private final int maxTileZ;

    private final int[][][] levelHeightmap;

    private final byte[][][] levelTileFlags;

    private final byte[][][] levelTileUnderlayIds;

    private final byte[][][] levelTileOverlayIds;

    private final byte[][][] levelTileOverlayShape;

    private final byte[][][] levelTileOverlayRotation;

    private final byte[][][] levelShademap;

    private final int[][] levelLightmap;

    private final int[] blendChroma;

    private final int[] blendSaturation;

    private final int[] blendLightness;

    private final int[] blendLuminance;

    private final int[] blendMagnitude;

    private final int[][][] levelOccludemap;

    public static final int[] ROTATION_WALL_TYPE = new int[] { 1, 2, 4, 8 };

    public static final int[] ROTATION_WALL_CORNER_TYPE = new int[] { 16, 32, 64, 128 };

    public static final int[] WALL_DECORATION_ROTATION_FORWARD_X = new int[] { 1, 0, -1, 0 };

    public static final int[] WALL_DECORATION_ROTATION_FORWARD_Z = new int[] { 0, -1, 0, 1 };

    public static int randomHueOffset = (int) (Math.random() * 17.0D) - 8;

    public static int randomLightnessOffset = (int) (Math.random() * 33.0D) - 16;

    public World( int maxTileX, int maxTileZ, int[][][] levelHeightmap, byte[][][] levelTileFlags) {
		this.maxTileX = maxTileX;
		this.maxTileZ = maxTileZ;
		this.levelHeightmap = levelHeightmap;
		this.levelTileFlags = levelTileFlags;

		this.levelTileUnderlayIds = new byte[4][this.maxTileX][this.maxTileZ];
		this.levelTileOverlayIds = new byte[4][this.maxTileX][this.maxTileZ];
		this.levelTileOverlayShape = new byte[4][this.maxTileX][this.maxTileZ];
		this.levelTileOverlayRotation = new byte[4][this.maxTileX][this.maxTileZ];

		this.levelOccludemap = new int[4][this.maxTileX + 1][this.maxTileZ + 1];
		this.levelShademap = new byte[4][this.maxTileX + 1][this.maxTileZ + 1];
		this.levelLightmap = new int[this.maxTileX + 1][this.maxTileZ + 1];

		this.blendChroma = new int[this.maxTileZ];
		this.blendSaturation = new int[this.maxTileZ];
		this.blendLightness = new int[this.maxTileZ];
		this.blendLuminance = new int[this.maxTileZ];
		this.blendMagnitude = new int[this.maxTileZ];
	}

    public static int perlin( int x, int z) {
		int value = perlin(x + 45365, z + 91923, 4) + (perlin(x + 10294, z + 37821, 2) - 128 >> 1) + (perlin(x, z, 1) - 128 >> 2) - 128;
		value = (int) ((double) value * 0.3D) + 35;
		if (value < 10) {
			value = 10;
		} else if (value > 60) {
			value = 60;
		}
		return value;
	}

    private static int perlin( int x, int z, int scale) {
		int intX = x / scale;
		int fracX = x & scale - 1;
		int intZ = z / scale;
		int fracZ = z & scale - 1;
		int v1 = smoothNoise(intX, intZ);
		int v2 = smoothNoise(intX + 1, intZ);
		int v3 = smoothNoise(intX, intZ + 1);
		int v4 = smoothNoise(intX + 1, intZ + 1);
		int i1 = interpolate(v1, v2, fracX, scale);
		int i2 = interpolate(v3, v4, fracX, scale);
		return interpolate(i1, i2, fracZ, scale);
	}

    private static int interpolate( int a, int b, int x, int scale) {
		int f = 65536 - Draw3D.cos[x * 1024 / scale] >> 1;
		return (a * (65536 - f) >> 16) + (b * f >> 16);
	}

    private static int smoothNoise( int x, int y) {
		int corners = noise(x - 1, y - 1) + noise(x + 1, y - 1) + noise(x - 1, y + 1) + noise(x + 1, y + 1);
		int sides = noise(x - 1, y) + noise(x + 1, y) + noise(x, y - 1) + noise(x, y + 1);
		int center = noise(x, y);
		return corners / 16 + sides / 8 + center / 4;
	}

    private static int noise( int x, int y) {
		int n = x + y * 57;
		int n1 = n << 13 ^ n;
		int n2 = n1 * (n1 * n1 * 15731 + 789221) + 1376312589 & Integer.MAX_VALUE;
		return n2 >> 19 & 0xFF;
	}

    public static int mulHSL( int hsl, int lightness) {
		if (hsl == -1) {
			return 12345678;
		}

		lightness = lightness * (hsl & 0x7F) / 128;
		if (lightness < 2) {
			lightness = 2;
		} else if (lightness > 126) {
			lightness = 126;
		}

		return (hsl & 0xFF80) + lightness;
	}

    public static void addLoc( int level, int x, int z, World3D scene, int[][][] levelHeightmap, LinkList locs, CollisionMap collision, int locId, int shape, int rotation, int trueLevel) {
		int heightSW = levelHeightmap[trueLevel][x][z];
		int heightSE = levelHeightmap[trueLevel][x + 1][z];
		int heightNW = levelHeightmap[trueLevel][x + 1][z + 1];
		int heightNE = levelHeightmap[trueLevel][x][z + 1];
		int y = heightSW + heightSE + heightNW + heightNE >> 2;

		LocType loc = LocType.get(locId);
		int bitset = x + (z << 7) + (locId << 14) + 0x40000000;
		if (!loc.active) {
			bitset += Integer.MIN_VALUE;
		}

		byte info = (byte) ((rotation << 6) + shape);
		Model model1;
		int width;
		int offset;
		Model model2;

		if (shape == LocType.GROUNDDECOR) {
			model1 = loc.getModel(LocType.GROUNDDECOR, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addGroundDecoration(model1, level, x, z, y, bitset, info);

			if (loc.blockwalk && loc.active) {
				collision.setBlocked(x, z);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 3, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.CENTREPIECE_STRAIGHT || shape == LocType.CENTREPIECE_DIAGONAL) {
			model1 = loc.getModel(LocType.CENTREPIECE_STRAIGHT, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			if (model1 != null) {
				int yaw = 0;
				if (shape == LocType.CENTREPIECE_DIAGONAL) {
					yaw += 256;
				}

				int height;
				if (rotation == 1 || rotation == 3) {
					width = loc.length;
					height = loc.width;
				} else {
					width = loc.width;
					height = loc.length;
				}

				scene.addLoc(level, x, z, y, model1, null, bitset, info, width, height, yaw);
			}

			if (loc.blockwalk) {
				collision.addLoc(x, z, loc.width, loc.length, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 2, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape >= LocType.ROOF_STRAIGHT) {
			model1 = loc.getModel(shape, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addLoc(level, x, z, y, model1, null, bitset, info, 1, 1, 0);

			if (loc.blockwalk) {
				collision.addLoc(x, z, loc.width, loc.length, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 2, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALL_STRAIGHT) {
			model1 = loc.getModel(LocType.WALL_STRAIGHT, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addWall(level, x, z, y, ROTATION_WALL_TYPE[rotation], 0, model1, null, bitset, info);

			if (loc.blockwalk) {
				collision.addWall(x, z, shape, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 0, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALL_DIAGONALCORNER) {
			model1 = loc.getModel(LocType.WALL_DIAGONALCORNER, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addWall(level, x, z, y, ROTATION_WALL_CORNER_TYPE[rotation], 0, model1, null, bitset, info);

			if (loc.blockwalk) {
				collision.addWall(x, z, shape, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 0, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALL_L) {
			int nextRotation = rotation + 1 & 0x3;
			Model model3 = loc.getModel(LocType.WALL_L, rotation + 4, heightSW, heightSE, heightNW, heightNE, -1);
			model2 = loc.getModel(LocType.WALL_L, nextRotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addWall(level, x, z, y, ROTATION_WALL_TYPE[rotation], ROTATION_WALL_TYPE[nextRotation], model3, model2, bitset, info);

			if (loc.blockwalk) {
				collision.addWall(x, z, shape, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 0, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALL_SQUARECORNER) {
			model1 = loc.getModel(LocType.WALL_SQUARECORNER, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addWall(level, x, z, y, ROTATION_WALL_CORNER_TYPE[rotation], 0, model1, null, bitset, info);

			if (loc.blockwalk) {
				collision.addWall(x, z, shape, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 0, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALL_DIAGONAL) {
			model1 = loc.getModel(shape, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addLoc(level, x, z, y, model1, null, bitset, info, 1, 1, 0);

			if (loc.blockwalk) {
				collision.addLoc(x, z, loc.width, loc.length, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 2, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_STRAIGHT_NOOFFSET) {
			model1 = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, 0, 0, bitset, model1, info, rotation * 512, ROTATION_WALL_TYPE[rotation]);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_STRAIGHT_OFFSET) {
			offset = 16;
			width = scene.getWallBitset(level, x, z);

			if (width > 0) {
				offset = LocType.get(width >> 14 & 0x7FFF).walloff;
			}

			model2 = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, WALL_DECORATION_ROTATION_FORWARD_X[rotation] * offset, WALL_DECORATION_ROTATION_FORWARD_Z[rotation] * offset, bitset, model2, info, rotation * 512, ROTATION_WALL_TYPE[rotation]);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_DIAGONAL_OFFSET) {
			model1 = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, 0, 0, bitset, model1, info, rotation, 256);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_DIAGONAL_NOOFFSET) {
			model1 = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, 0, 0, bitset, model1, info, rotation, 512);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_DIAGONAL_BOTH) {
			model1 = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, 0, 0, bitset, model1, info, rotation, 768);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		}
	}

    public void clearLandscape( int startX, int startZ, int endX, int endZ) {
		byte waterOverlay = 0;
		for ( int i = 0; i < FloType.count; i++) {
			if (FloType.instances[i].name.equalsIgnoreCase("water")) {
				waterOverlay = (byte) (i + 1);
				break;
			}
		}

		for ( int z = startX; z < startX + endX; z++) {
			for ( int x = startZ; x < startZ + endZ; x++) {
				if (x < 0 || x >= this.maxTileX || z < 0 || z >= this.maxTileZ) {
					continue;
				}

				this.levelTileOverlayIds[0][x][z] = waterOverlay;

				for ( int level = 0; level < 4; level++) {
					this.levelHeightmap[level][x][z] = 0;
					this.levelTileFlags[level][x][z] = 0;
				}
			}
		}
	}

    public void readLandscape( int originX, int originZ, int xOffset, int zOffset, byte[] src) {
		Packet buf = new Packet(src);

		for ( int level = 0; level < 4; level++) {
			for ( int x = 0; x < 64; x++) {
				for ( int z = 0; z < 64; z++) {
					int stx = x + xOffset;
					int stz = z + zOffset;
					int opcode;

					if (stx >= 0 && stx < 104 && stz >= 0 && stz < 104) {
						this.levelTileFlags[level][stx][stz] = 0;
						while (true) {
							opcode = buf.g1();
							if (opcode == 0) {
								if (level == 0) {
									this.levelHeightmap[0][stx][stz] = -perlin(stx + originX + 932731, stz + 556238 + originZ) * 8;
								} else {
									this.levelHeightmap[level][stx][stz] = this.levelHeightmap[level - 1][stx][stz] - 240;
								}
								break;
							}

							if (opcode == 1) {
								int height = buf.g1();
								if (height == 1) {
									height = 0;
								}
								if (level == 0) {
									this.levelHeightmap[0][stx][stz] = -height * 8;
								} else {
									this.levelHeightmap[level][stx][stz] = this.levelHeightmap[level - 1][stx][stz] - height * 8;
								}
								break;
							}

							if (opcode <= 49) {
								this.levelTileOverlayIds[level][stx][stz] = buf.g1b();
								this.levelTileOverlayShape[level][stx][stz] = (byte) ((opcode - 2) / 4);
								this.levelTileOverlayRotation[level][stx][stz] = (byte) (opcode - 2 & 0x3);
							} else if (opcode <= 81) {
								this.levelTileFlags[level][stx][stz] = (byte) (opcode - 49);
							} else {
								this.levelTileUnderlayIds[level][stx][stz] = (byte) (opcode - 81);
							}
						}
					} else {
						while (true) {
							opcode = buf.g1();
							if (opcode == 0) {
								break;
							}

							if (opcode == 1) {
								buf.g1();
								break;
							}

							if (opcode <= 49) {
								buf.g1();
							}
						}
					}
				}
			}
		}
	}

    public void readLocs( World3D scene, LinkList locs, CollisionMap[] collision, byte[] src, int xOffset, int zOffset) {
		Packet buf = new Packet(src);
		int locId = -1;

		while (true) {
			int deltaId = buf.gsmarts();
			if (deltaId == 0) {
				return;
			}

			locId += deltaId;

			int locPos = 0;
			while (true) {
				int deltaPos = buf.gsmarts();
				if (deltaPos == 0) {
					break;
				}

				locPos += deltaPos - 1;
				int z = locPos & 0x3F;
				int x = locPos >> 6 & 0x3F;
				int level = locPos >> 12;

				int info = buf.g1();
				int shape = info >> 2;
				int rotation = info & 0x3;
				int stx = x + xOffset;
				int stz = z + zOffset;

				if (stx > 0 && stz > 0 && stx < 103 && stz < 103) {
					int currentLevel = level;
					if ((this.levelTileFlags[1][stx][stz] & 0x2) == 2) {
						currentLevel = level - 1;
					}

					CollisionMap collisionMap = null;
					if (currentLevel >= 0) {
						collisionMap = collision[currentLevel];
					}

					this.addLoc(level, stx, stz, scene, locs, collisionMap, locId, shape, rotation);
				}
			}
		}
	}

    private void addLoc( int level, int x, int z, World3D scene, LinkList locs, CollisionMap collision, int locId, int shape, int rotation) {
		if (lowMemory) {
			if ((this.levelTileFlags[level][x][z] & 0x10) != 0) {
				return;
			}

			if (this.getDrawLevel(level, x, z) != levelBuilt) {
				return;
			}
		}

		int heightSW = this.levelHeightmap[level][x][z];
		int heightSE = this.levelHeightmap[level][x + 1][z];
		int heightNW = this.levelHeightmap[level][x + 1][z + 1];
		int heightNE = this.levelHeightmap[level][x][z + 1];
		int y = heightSW + heightSE + heightNW + heightNE >> 2;

		LocType loc = LocType.get(locId);
		int bitset = x + (z << 7) + (locId << 14) + 0x40000000;
		if (!loc.active) {
			bitset += Integer.MIN_VALUE;
		}

		byte info = (byte) ((rotation << 6) + shape);
		Model model;
		int width;
		int offset;
		Model model1;

		if (shape == LocType.GROUNDDECOR) {
			if (lowMemory && !loc.active && !loc.forcedecor) {
				return;
			}

			model = loc.getModel(LocType.GROUNDDECOR, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addGroundDecoration(model, level, x, z, y, bitset, info);

			if (loc.blockwalk && loc.active && collision != null) {
				collision.setBlocked(x, z);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 3, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.CENTREPIECE_STRAIGHT || shape == LocType.CENTREPIECE_DIAGONAL) {
			model = loc.getModel(LocType.CENTREPIECE_STRAIGHT, rotation, heightSW, heightSE, heightNW, heightNE, -1);

			if (model != null) {
				int yaw = 0;
				if (shape == LocType.CENTREPIECE_DIAGONAL) {
					yaw += 256;
				}

				int height;
				if (rotation == 1 || rotation == 3) {
					width = loc.length;
					height = loc.width;
				} else {
					width = loc.width;
					height = loc.length;
				}

				if (scene.addLoc(level, x, z, y, model, null, bitset, info, width, height, yaw) && loc.shadow) {
					for ( int dx = 0; dx <= width; dx++) {
						for ( int dz = 0; dz <= height; dz++) {
							int shade = model.radius / 4;
							if (shade > 30) {
								shade = 30;
							}

							if (shade > this.levelShademap[level][x + dx][z + dz]) {
								this.levelShademap[level][x + dx][z + dz] = (byte) shade;
							}
						}
					}
				}
			}

			if (loc.blockwalk && collision != null) {
				collision.addLoc(x, z, loc.width, loc.length, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 2, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape >= LocType.ROOF_STRAIGHT) {
			model = loc.getModel(shape, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addLoc(level, x, z, y, model, null, bitset, info, 1, 1, 0);

			if (shape >= LocType.ROOF_STRAIGHT && shape <= LocType.ROOF_FLAT && shape != LocType.ROOF_DIAGONAL_WITH_ROOFEDGE && level > 0) {
				this.levelOccludemap[level][x][z] |= 0x924;
			}

			if (loc.blockwalk && collision != null) {
				collision.addLoc(x, z, loc.width, loc.length, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 2, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALL_STRAIGHT) {
			model = loc.getModel(LocType.WALL_STRAIGHT, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addWall(level, x, z, y, ROTATION_WALL_TYPE[rotation], 0, model, null, bitset, info);

			if (rotation == 0) {
				if (loc.shadow) {
					this.levelShademap[level][x][z] = 50;
					this.levelShademap[level][x][z + 1] = 50;
				}

				if (loc.occlude) {
					this.levelOccludemap[level][x][z] |= 0x249;
				}
			} else if (rotation == 1) {
				if (loc.shadow) {
					this.levelShademap[level][x][z + 1] = 50;
					this.levelShademap[level][x + 1][z + 1] = 50;
				}

				if (loc.occlude) {
					this.levelOccludemap[level][x][z + 1] |= 0x492;
				}
			} else if (rotation == 2) {
				if (loc.shadow) {
					this.levelShademap[level][x + 1][z] = 50;
					this.levelShademap[level][x + 1][z + 1] = 50;
				}

				if (loc.occlude) {
					this.levelOccludemap[level][x + 1][z] |= 0x249;
				}
			} else if (rotation == 3) {
				if (loc.shadow) {
					this.levelShademap[level][x][z] = 50;
					this.levelShademap[level][x + 1][z] = 50;
				}

				if (loc.occlude) {
					this.levelOccludemap[level][x][z] |= 0x492;
				}
			}

			if (loc.blockwalk && collision != null) {
				collision.addWall(x, z, shape, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 0, x, z, SeqType.instances[loc.anim], true));
			}

			if (loc.walloff != 16) {
				scene.setWallDecorationOffset(level, x, z, loc.walloff);
			}
		} else if (shape == LocType.WALL_DIAGONALCORNER) {
			model = loc.getModel(LocType.WALL_DIAGONALCORNER, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addWall(level, x, z, y, ROTATION_WALL_CORNER_TYPE[rotation], 0, model, null, bitset, info);

			if (loc.shadow) {
				if (rotation == 0) {
					this.levelShademap[level][x][z + 1] = 50;
				} else if (rotation == 1) {
					this.levelShademap[level][x + 1][z + 1] = 50;
				} else if (rotation == 2) {
					this.levelShademap[level][x + 1][z] = 50;
				} else if (rotation == 3) {
					this.levelShademap[level][x][z] = 50;
				}
			}

			if (loc.blockwalk && collision != null) {
				collision.addWall(x, z, shape, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 0, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALL_L) {
			int nextRotation = rotation + 1 & 0x3;
			Model model3 = loc.getModel(LocType.WALL_L, rotation + 4, heightSW, heightSE, heightNW, heightNE, -1);
			model1 = loc.getModel(LocType.WALL_L, nextRotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addWall(level, x, z, y, ROTATION_WALL_TYPE[rotation], ROTATION_WALL_TYPE[nextRotation], model3, model1, bitset, info);

			if (loc.occlude) {
				if (rotation == 0) {
					this.levelOccludemap[level][x][z] |= 0x109;
					this.levelOccludemap[level][x][z + 1] |= 0x492;
				} else if (rotation == 1) {
					this.levelOccludemap[level][x][z + 1] |= 0x492;
					this.levelOccludemap[level][x + 1][z] |= 0x249;
				} else if (rotation == 2) {
					this.levelOccludemap[level][x + 1][z] |= 0x249;
					this.levelOccludemap[level][x][z] |= 0x492;
				} else if (rotation == 3) {
					this.levelOccludemap[level][x][z] |= 0x492;
					this.levelOccludemap[level][x][z] |= 0x249;
				}
			}

			if (loc.blockwalk && collision != null) {
				collision.addWall(x, z, shape, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 0, x, z, SeqType.instances[loc.anim], true));
			}

			if (loc.walloff != 16) {
				scene.setWallDecorationOffset(level, x, z, loc.walloff);
			}
		} else if (shape == LocType.WALL_SQUARECORNER) {
			model = loc.getModel(LocType.WALL_SQUARECORNER, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addWall(level, x, z, y, ROTATION_WALL_CORNER_TYPE[rotation], 0, model, null, bitset, info);

			if (loc.shadow) {
				if (rotation == 0) {
					this.levelShademap[level][x][z + 1] = 50;
				} else if (rotation == 1) {
					this.levelShademap[level][x + 1][z + 1] = 50;
				} else if (rotation == 2) {
					this.levelShademap[level][x + 1][z] = 50;
				} else if (rotation == 3) {
					this.levelShademap[level][x][z] = 50;
				}
			}

			if (loc.blockwalk && collision != null) {
				collision.addWall(x, z, shape, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 0, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALL_DIAGONAL) {
			model = loc.getModel(shape, rotation, heightSW, heightSE, heightNW, heightNE, -1);
			scene.addLoc(level, x, z, y, model, null, bitset, info, 1, 1, 0);

			if (loc.blockwalk && collision != null) {
				collision.addLoc(x, z, loc.width, loc.length, rotation, loc.blockrange);
			}

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 2, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_STRAIGHT_NOOFFSET) {
			model = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, 0, 0, bitset, model, info, rotation * 512, ROTATION_WALL_TYPE[rotation]);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_STRAIGHT_OFFSET) {
			offset = 16;
			width = scene.getWallBitset(level, x, z);
			if (width > 0) {
				offset = LocType.get(width >> 14 & 0x7FFF).walloff;
			}

			model1 = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, WALL_DECORATION_ROTATION_FORWARD_X[rotation] * offset, WALL_DECORATION_ROTATION_FORWARD_Z[rotation] * offset, bitset, model1, info, rotation * 512, ROTATION_WALL_TYPE[rotation]);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_DIAGONAL_OFFSET) {
			model = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, 0, 0, bitset, model, info, rotation, 256);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_DIAGONAL_NOOFFSET) {
			model = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, 0, 0, bitset, model, info, rotation, 512);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		} else if (shape == LocType.WALLDECOR_DIAGONAL_BOTH) {
			model = loc.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightSW, heightSE, heightNW, heightNE, -1);
			scene.setWallDecoration(level, x, z, y, 0, 0, bitset, model, info, rotation, 768);

			if (loc.anim != -1) {
				locs.pushBack(new LocEntity(locId, level, 1, x, z, SeqType.instances[loc.anim], true));
			}
		}
	}

    public void build( World3D scene, CollisionMap[] collision) {
		for ( int level = 0; level < 4; level++) {
			for (int x = 0; x < 104; x++) {
				for (int z = 0; z < 104; z++) {
					// solid
					if ((this.levelTileFlags[level][x][z] & 0x1) == 1) {
						int trueLevel = level;

						// bridge
						if ((this.levelTileFlags[1][x][z] & 0x2) == 2) {
							trueLevel--;
						}

						if (trueLevel >= 0) {
							collision[trueLevel].setBlocked(x, z);
						}
					}
				}
			}
		}

		randomHueOffset += (int) (Math.random() * 5.0D) - 2;
		if (randomHueOffset < -8) {
			randomHueOffset = -8;
		} else if (randomHueOffset > 8) {
			randomHueOffset = 8;
		}

		randomLightnessOffset += (int) (Math.random() * 5.0D) - 2;
		if (randomLightnessOffset < -16) {
			randomLightnessOffset = -16;
		} else if (randomLightnessOffset > 16) {
			randomLightnessOffset = 16;
		}

		for (int level = 0; level < 4; level++) {
			byte[][] shademap = this.levelShademap[level];
			byte lightAmbient = 96;
			short lightAttenuation = 768;
			byte lightX = -50;
			byte lightY = -10;
			byte lightZ = -50;
			int lightMag = (int) Math.sqrt(lightX * lightX + lightY * lightY + lightZ * lightZ);
			int lightMagnitude = lightAttenuation * lightMag >> 8;

			for (int z = 1; z < this.maxTileZ - 1; z++) {
				for (int x = 1; x < this.maxTileX - 1; x++) {
					int dx = this.levelHeightmap[level][x + 1][z] - this.levelHeightmap[level][x - 1][z];
					int dz = this.levelHeightmap[level][x][z + 1] - this.levelHeightmap[level][x][z - 1];
					int len = (int) Math.sqrt(dx * dx + dz * dz + 65536);
					int normalX = (dx << 8) / len;
					int normalY = 65536 / len;
					int normalZ = (dz << 8) / len;
					int light = lightAmbient + (lightX * normalX + lightY * normalY + lightZ * normalZ) / lightMagnitude;
					int shade = (shademap[x - 1][z] >> 2) + (shademap[x + 1][z] >> 3) + (shademap[x][z - 1] >> 2) + (shademap[x][z + 1] >> 3) + (shademap[x][z] >> 1);
					this.levelLightmap[x][z] = light - shade;
				}
			}

			for (int z = 0; z < this.maxTileZ; z++) {
				this.blendChroma[z] = 0;
				this.blendSaturation[z] = 0;
				this.blendLightness[z] = 0;
				this.blendLuminance[z] = 0;
				this.blendMagnitude[z] = 0;
			}

			for (int x0 = -5; x0 < this.maxTileX + 5; x0++) {
				for (int z0 = 0; z0 < this.maxTileZ; z0++) {
					int x1 = x0 + 5;
					int debugMag;

					if (x1 >= 0 && x1 < this.maxTileX) {
						int underlayId = this.levelTileUnderlayIds[level][x1][z0] & 0xFF;

						if (underlayId > 0) {
							FloType flu = FloType.instances[underlayId - 1];
							this.blendChroma[z0] += flu.chroma;
							this.blendSaturation[z0] += flu.saturation;
							this.blendLightness[z0] += flu.lightness;
							this.blendLuminance[z0] += flu.luminance;
							debugMag = this.blendMagnitude[z0]++;
						}
					}

					int x2 = x0 - 5;
					if (x2 >= 0 && x2 < this.maxTileX) {
						int underlayId = this.levelTileUnderlayIds[level][x2][z0] & 0xFF;

						if (underlayId > 0) {
							FloType flu = FloType.instances[underlayId - 1];
							this.blendChroma[z0] -= flu.chroma;
							this.blendSaturation[z0] -= flu.saturation;
							this.blendLightness[z0] -= flu.lightness;
							this.blendLuminance[z0] -= flu.luminance;
							debugMag = this.blendMagnitude[z0]--;
						}
					}
				}

				if (x0 >= 1 && x0 < this.maxTileX - 1) {
					int hueAccumulator = 0;
					int saturationAccumulator = 0;
					int lightnessAccumulator = 0;
					int luminanceAccumulator = 0;
					int magnitudeAccumulator = 0;

					for (int z0 = -5; z0 < this.maxTileZ + 5; z0++) {
						int dz1 = z0 + 5;
						if (dz1 >= 0 && dz1 < this.maxTileZ) {
							hueAccumulator += this.blendChroma[dz1];
							saturationAccumulator += this.blendSaturation[dz1];
							lightnessAccumulator += this.blendLightness[dz1];
							luminanceAccumulator += this.blendLuminance[dz1];
							magnitudeAccumulator += this.blendMagnitude[dz1];
						}

						int dz2 = z0 - 5;
						if (dz2 >= 0 && dz2 < this.maxTileZ) {
							hueAccumulator -= this.blendChroma[dz2];
							saturationAccumulator -= this.blendSaturation[dz2];
							lightnessAccumulator -= this.blendLightness[dz2];
							luminanceAccumulator -= this.blendLuminance[dz2];
							magnitudeAccumulator -= this.blendMagnitude[dz2];
						}

						if (z0 >= 1 && z0 < this.maxTileZ - 1 && (!lowMemory || (this.levelTileFlags[level][x0][z0] & 0x10) == 0 && this.getDrawLevel(level, x0, z0) == levelBuilt)) {
							int underlayId = this.levelTileUnderlayIds[level][x0][z0] & 0xFF;
							int overlayId = this.levelTileOverlayIds[level][x0][z0] & 0xFF;

							if (underlayId > 0 || overlayId > 0) {
								int heightSW = this.levelHeightmap[level][x0][z0];
								int heightSE = this.levelHeightmap[level][x0 + 1][z0];
								int heightNE = this.levelHeightmap[level][x0 + 1][z0 + 1];
								int heightNW = this.levelHeightmap[level][x0][z0 + 1];

								int lightSW = this.levelLightmap[x0][z0];
								int lightSE = this.levelLightmap[x0 + 1][z0];
								int lightNE = this.levelLightmap[x0 + 1][z0 + 1];
								int lightNW = this.levelLightmap[x0][z0 + 1];

								int baseColor = -1;
								int tintColor = -1;

								if (underlayId > 0) {
									int hue = hueAccumulator * 256 / luminanceAccumulator;
									int saturation = saturationAccumulator / magnitudeAccumulator;
									int lightness = lightnessAccumulator / magnitudeAccumulator;
									baseColor = this.hsl24to16(hue, saturation, lightness);
									int randomHue = hue + randomHueOffset & 0xFF;
									lightness += randomLightnessOffset;
									if (lightness < 0) {
										lightness = 0;
									} else if (lightness > 255) {
										lightness = 255;
									}
									tintColor = this.hsl24to16(randomHue, saturation, lightness);
								}

								if (level > 0) {
									boolean occludes = underlayId != 0 || this.levelTileOverlayShape[level][x0][z0] == 0;

									if (overlayId > 0 && !FloType.instances[overlayId - 1].occlude) {
										occludes = false;
									}

									// occludes && flat
									if (occludes && heightSW == heightSE && heightSW == heightNE && heightSW == heightNW) {
										this.levelOccludemap[level][x0][z0] |= 0x924;
									}
								}

								int shadeColor = 0;
								if (baseColor != -1) {
									shadeColor = Draw3D.palette[mulHSL(tintColor, 96)];
								}

								if (overlayId == 0) {
									scene.setTile(level, x0, z0, 0, 0, -1, heightSW, heightSE, heightNE, heightNW, mulHSL(baseColor, lightSW), mulHSL(baseColor, lightSE), mulHSL(baseColor, lightNE), mulHSL(baseColor, lightNW), 0, 0, 0, 0, shadeColor, 0);
								} else {
									int shape = this.levelTileOverlayShape[level][x0][z0] + 1;
									byte rotation = this.levelTileOverlayRotation[level][x0][z0];
									FloType flo = FloType.instances[overlayId - 1];
									int textureId = flo.texture;
									int hsl;
									int rgb;

									if (textureId >= 0) {
										rgb = Draw3D.getAverageTextureRGB(textureId);
										hsl = -1;
									} else if (flo.rgb == 16711935) {
										rgb = 0;
										hsl = -2;
										textureId = -1;
									} else {
										hsl = this.hsl24to16(flo.hue, flo.saturation, flo.lightness);
										rgb = Draw3D.palette[this.adjustLightness(flo.hsl, 96)];
									}

									scene.setTile(level, x0, z0, shape, rotation, textureId, heightSW, heightSE, heightNE, heightNW, mulHSL(baseColor, lightSW), mulHSL(baseColor, lightSE), mulHSL(baseColor, lightNE), mulHSL(baseColor, lightNW), this.adjustLightness(hsl, lightSW), this.adjustLightness(hsl, lightSE), this.adjustLightness(hsl, lightNE), this.adjustLightness(hsl, lightNW), shadeColor, rgb);
								}
							}
						}
					}
				}
			}

			for (int stz = 1; stz < this.maxTileZ - 1; stz++) {
				for (int stx = 1; stx < this.maxTileX - 1; stx++) {
					scene.setDrawLevel(level, stx, stz, this.getDrawLevel(level, stx, stz));
				}
			}
		}

		if (!fullbright) {
			scene.buildModels(64, 768, -50, -10, -50);
		}

		for (int x = 0; x < this.maxTileX; x++) {
			for (int z = 0; z < this.maxTileZ; z++) {
				if ((this.levelTileFlags[1][x][z] & 0x2) == 2) {
					scene.setBridge(x, z);
				}
			}
		}

		if (!fullbright) {
			int wall0 = 0x1; // this flag is set by walls with rotation 0 or 2
			int wall1 = 0x2; // this flag is set by walls with rotation 1 or 3
			int floor = 0x4; // this flag is set by floors which are flat

			for ( int topLevel = 0; topLevel < 4; topLevel++) {
				if (topLevel > 0) {
					wall0 <<= 0x3;
					wall1 <<= 0x3;
					floor <<= 0x3;
				}

				for ( int level = 0; level <= topLevel; level++) {
					for (int tileZ = 0; tileZ <= this.maxTileZ; tileZ++) {
						for (int tileX = 0; tileX <= this.maxTileX; tileX++) {
							if ((this.levelOccludemap[level][tileX][tileZ] & wall0) != 0) {
								int minTileZ = tileZ;
								int maxTileZ = tileZ;
								int minLevel = level;
								int maxLevel = level;

								while (minTileZ > 0 && (this.levelOccludemap[level][tileX][minTileZ - 1] & wall0) != 0) {
									minTileZ--;
								}

								while (maxTileZ < this.maxTileZ && (this.levelOccludemap[level][tileX][maxTileZ + 1] & wall0) != 0) {
									maxTileZ++;
								}

								find_min_level:
								while (minLevel > 0) {
									for (int z = minTileZ; z <= maxTileZ; z++) {
										if ((this.levelOccludemap[minLevel - 1][tileX][z] & wall0) == 0) {
											break find_min_level;
										}
									}
									minLevel--;
								}

								find_max_level:
								while (maxLevel < topLevel) {
									for (int z = minTileZ; z <= maxTileZ; z++) {
										if ((this.levelOccludemap[maxLevel + 1][tileX][z] & wall0) == 0) {
											break find_max_level;
										}
									}
									maxLevel++;
								}

								int area = (maxLevel + 1 - minLevel) * (maxTileZ + 1 - minTileZ);
								if (area >= 8) {
									int minY = this.levelHeightmap[maxLevel][tileX][minTileZ] - 240;
									int maxX = this.levelHeightmap[minLevel][tileX][minTileZ];

									World3D.addOccluder(topLevel, 1, tileX * 128, minY, minTileZ * 128, tileX * 128, maxX, maxTileZ * 128 + 128);

									for (int l = minLevel; l <= maxLevel; l++) {
										for (int z = minTileZ; z <= maxTileZ; z++) {
											this.levelOccludemap[l][tileX][z] &= ~wall0;
										}
									}
								}
							}

							if ((this.levelOccludemap[level][tileX][tileZ] & wall1) != 0) {
								int minTileX = tileX;
								int maxTileX = tileX;
								int minLevel = level;
								int maxLevel = level;

								while (minTileX > 0 && (this.levelOccludemap[level][minTileX - 1][tileZ] & wall1) != 0) {
									minTileX--;
								}

								while (maxTileX < this.maxTileX && (this.levelOccludemap[level][maxTileX + 1][tileZ] & wall1) != 0) {
									maxTileX++;
								}

								find_min_level2:
								while (minLevel > 0) {
									for (int x = minTileX; x <= maxTileX; x++) {
										if ((this.levelOccludemap[minLevel - 1][x][tileZ] & wall1) == 0) {
											break find_min_level2;
										}
									}
									minLevel--;
								}

								find_max_level2:
								while (maxLevel < topLevel) {
									for (int x = minTileX; x <= maxTileX; x++) {
										if ((this.levelOccludemap[maxLevel + 1][x][tileZ] & wall1) == 0) {
											break find_max_level2;
										}
									}
									maxLevel++;
								}

								int area = (maxLevel + 1 - minLevel) * (maxTileX + 1 - minTileX);

								if (area >= 8) {
									int minY = this.levelHeightmap[maxLevel][minTileX][tileZ] - 240;
									int maxY = this.levelHeightmap[minLevel][minTileX][tileZ];

									World3D.addOccluder(topLevel, 2, minTileX * 128, minY, tileZ * 128, maxTileX * 128 + 128, maxY, tileZ * 128);

									for (int l = minLevel; l <= maxLevel; l++) {
										for (int x = minTileX; x <= maxTileX; x++) {
											this.levelOccludemap[l][x][tileZ] &= ~wall1;
										}
									}
								}
							}
							if ((this.levelOccludemap[level][tileX][tileZ] & floor) != 0) {
								int minTileX = tileX;
								int maxTileX = tileX;
								int minTileZ = tileZ;
								int maxTileZ = tileZ;

								while (minTileZ > 0 && (this.levelOccludemap[level][tileX][minTileZ - 1] & floor) != 0) {
									minTileZ--;
								}

								while (maxTileZ < this.maxTileZ && (this.levelOccludemap[level][tileX][maxTileZ + 1] & floor) != 0) {
									maxTileZ++;
								}

								find_min_tile_xz:
								while (minTileX > 0) {
									for (int z = minTileZ; z <= maxTileZ; z++) {
										if ((this.levelOccludemap[level][minTileX - 1][z] & floor) == 0) {
											break find_min_tile_xz;
										}
									}
									minTileX--;
								}

								find_max_tile_xz:
								while (maxTileX < this.maxTileX) {
									for (int z = minTileZ; z <= maxTileZ; z++) {
										if ((this.levelOccludemap[level][maxTileX + 1][z] & floor) == 0) {
											break find_max_tile_xz;
										}
									}
									maxTileX++;
								}

								if ((maxTileX + 1 - minTileX) * (maxTileZ + 1 - minTileZ) >= 4) {
									int y = this.levelHeightmap[level][minTileX][minTileZ];

									World3D.addOccluder(topLevel, 4, minTileX * 128, y, minTileZ * 128, maxTileX * 128 + 128, y, maxTileZ * 128 + 128);

									for (int x = minTileX; x <= maxTileX; x++) {
										for (int z = minTileZ; z <= maxTileZ; z++) {
											this.levelOccludemap[level][x][z] &= ~floor;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

    private int getDrawLevel( int level, int stx, int stz) {
		if ((this.levelTileFlags[level][stx][stz] & 0x8) == 0) {
			return level <= 0 || (this.levelTileFlags[1][stx][stz] & 0x2) == 0 ? level : level - 1;
		} else {
			return 0;
		}
	}

    private int adjustLightness( int hsl, int scalar) {
		if (hsl == -2) {
			return 12345678;
		}

		if (hsl == -1) {
			if (scalar < 0) {
				scalar = 0;
			} else if (scalar > 127) {
				scalar = 127;
			}
			return 127 - scalar;
		} else {
			scalar = scalar * (hsl & 0x7F) / 128;
			if (scalar < 2) {
				scalar = 2;
			} else if (scalar > 126) {
				scalar = 126;
			}
			return (hsl & 0xFF80) + scalar;
		}
	}

    private int hsl24to16( int hue, int saturation, int lightness) {
		if (lightness > 179) {
			saturation /= 2;
		}

		if (lightness > 192) {
			saturation /= 2;
		}

		if (lightness > 217) {
			saturation /= 2;
		}

		if (lightness > 243) {
			saturation /= 2;
		}

		return (hue / 4 << 10) + (saturation / 32 << 7) + lightness / 2;
	}
}
