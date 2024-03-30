package jagex2.graphics;

import jagex2.datastruct.Hashable;
import jagex2.io.Jagfile;
import jagex2.io.Packet;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

public class Model extends Hashable {

    public int vertexCount;

    public int[] vertexX;

    public int[] vertexY;

    public int[] vertexZ;

    public int faceCount;

    public int[] faceVertexA;

    public int[] faceVertexB;

    public int[] faceVertexC;

    private int[] faceColorA;

    private int[] faceColorB;

    private int[] faceColorC;

    public int[] faceInfo;

    private int[] facePriority;

    private int[] faceAlpha;

    public int[] faceColor;

    private int priority;

    private int texturedFaceCount;

    private int[] texturedVertexA;

    private int[] texturedVertexB;

    private int[] texturedVertexC;

    public int minX;

    public int maxX;

    public int maxZ;

    public int minZ;

    public int radius;

    public int maxY;

    public int minY;

    private int maxDepth;

    private int minDepth;

    public int objRaise;

    private int[] vertexLabel;

    private int[] faceLabel;

    public int[][] labelVertices;

    public int[][] labelFaces;

    public boolean pickable = false;

    public VertexNormal[] vertexNormal;

    public VertexNormal[] vertexNormalOriginal;

    public static Metadata[] metadata;

    private static Packet head;

    public static Packet face1;

    public static Packet face2;

    public static Packet face3;

    public static Packet face4;

    public static Packet face5;

    public static Packet point1;

    public static Packet point2;

    public static Packet point3;

    public static Packet point4;

    public static Packet point5;

    public static Packet vertex1;

    public static Packet vertex2;

    public static Packet axis;

    public static boolean[] faceClippedX = new boolean[4096];

    public static boolean[] faceNearClipped = new boolean[4096];

    public static int[] vertexScreenX = new int[4096];

    public static int[] vertexScreenY = new int[4096];

    public static int[] vertexScreenZ = new int[4096];

    public static int[] vertexViewSpaceX = new int[4096];

    public static int[] vertexViewSpaceY = new int[4096];

    public static int[] vertexViewSpaceZ = new int[4096];

    public static int[] tmpDepthFaceCount = new int[1500];

    public static int[][] tmpDepthFaces = new int[1500][512];

    public static int[] tmpPriorityFaceCount = new int[12];

    public static int[][] tmpPriorityFaces = new int[12][2000];

    public static int[] tmpPriority10FaceDepth = new int[2000];

    public static int[] tmpPriority11FaceDepth = new int[2000];

    public static int[] tmpPriorityDepthSum = new int[12];

    public static final int[] clippedX = new int[10];

    public static final int[] clippedY = new int[10];

    public static final int[] clippedColor = new int[10];

    public static int baseX;

    public static int baseY;

    public static int baseZ;

    public static boolean checkHover;

    public static int mouseX;

    public static int mouseZ;

    public static int pickedCount;

    public static final int[] pickedBitsets = new int[1000];

    public static int[] sin = Draw3D.sin;

    public static int[] cos = Draw3D.cos;

    public static int[] palette = Draw3D.palette;

    public static int[] reciprical16 = Draw3D.reciprocal16;

    public Model( int id) {
		if (metadata == null) {
			return;
		}

		Metadata meta = metadata[id];
		if (meta == null) {
			System.out.println("Error model:" + id + " not found!");
		} else {
			this.vertexCount = meta.vertexCount;
			this.faceCount = meta.faceCount;
			this.texturedFaceCount = meta.texturedFaceCount;
			this.vertexX = new int[this.vertexCount];
			this.vertexY = new int[this.vertexCount];
			this.vertexZ = new int[this.vertexCount];
			this.faceVertexA = new int[this.faceCount];
			this.faceVertexB = new int[this.faceCount];
			this.faceVertexC = new int[this.faceCount];
			this.texturedVertexA = new int[this.texturedFaceCount];
			this.texturedVertexB = new int[this.texturedFaceCount];
			this.texturedVertexC = new int[this.texturedFaceCount];

			if (meta.vertexLabelsOffset >= 0) {
				this.vertexLabel = new int[this.vertexCount];
			}

			if (meta.faceInfosOffset >= 0) {
				this.faceInfo = new int[this.faceCount];
			}

			if (meta.facePrioritiesOffset >= 0) {
				this.facePriority = new int[this.faceCount];
			} else {
				this.priority = -meta.facePrioritiesOffset - 1;
			}

			if (meta.faceAlphasOffset >= 0) {
				this.faceAlpha = new int[this.faceCount];
			}

			if (meta.faceLabelsOffset >= 0) {
				this.faceLabel = new int[this.faceCount];
			}

			this.faceColor = new int[this.faceCount];

			point1.pos = meta.vertexFlagsOffset;
			point2.pos = meta.vertexXOffset;
			point3.pos = meta.vertexYOffset;
			point4.pos = meta.vertexZOffset;
			point5.pos = meta.vertexLabelsOffset;

			int dx = 0;
			int db = 0;
			int dc = 0;
			int a;
			int b;
			int c;

			for ( int v = 0; v < this.vertexCount; v++) {
				int flags = point1.g1();
				a = 0;
				if ((flags & 0x1) != 0) {
					a = point2.gsmart();
				}

				b = 0;
				if ((flags & 0x2) != 0) {
					b = point3.gsmart();
				}

				c = 0;
				if ((flags & 0x4) != 0) {
					c = point4.gsmart();
				}

				this.vertexX[v] = dx + a;
				this.vertexY[v] = db + b;
				this.vertexZ[v] = dc + c;
				dx = this.vertexX[v];
				db = this.vertexY[v];
				dc = this.vertexZ[v];

				if (this.vertexLabel != null) {
					this.vertexLabel[v] = point5.g1();
				}
			}

			face1.pos = meta.faceColorsOffset;
			face2.pos = meta.faceInfosOffset;
			face3.pos = meta.facePrioritiesOffset;
			face4.pos = meta.faceAlphasOffset;
			face5.pos = meta.faceLabelsOffset;
			for (int f = 0; f < this.faceCount; f++) {
				this.faceColor[f] = face1.g2();
				if (this.faceInfo != null) {
					this.faceInfo[f] = face2.g1();
				}

				if (this.facePriority != null) {
					this.facePriority[f] = face3.g1();
				}

				if (this.faceAlpha != null) {
					this.faceAlpha[f] = face4.g1();
				}

				if (this.faceLabel != null) {
					this.faceLabel[f] = face5.g1();
				}
			}

			vertex1.pos = meta.faceVerticesOffset;
			vertex2.pos = meta.faceOrientationsOffset;

			a = 0;
			b = 0;
			c = 0;
			int last = 0;

			for ( int f = 0; f < this.faceCount; f++) {
				int orientation = vertex2.g1();

				if (orientation == 1) {
					a = vertex1.gsmart() + last;
					b = vertex1.gsmart() + a;
					c = vertex1.gsmart() + b;
					last = c;
					this.faceVertexA[f] = a;
					this.faceVertexB[f] = b;
					this.faceVertexC[f] = c;
				} else if (orientation == 2) {
					a = a;
					b = c;
					c = vertex1.gsmart() + last;
					last = c;
					this.faceVertexA[f] = a;
					this.faceVertexB[f] = b;
					this.faceVertexC[f] = c;
				} else if (orientation == 3) {
					a = c;
					b = b;
					c = vertex1.gsmart() + last;
					last = c;
					this.faceVertexA[f] = a;
					this.faceVertexB[f] = b;
					this.faceVertexC[f] = c;
				} else if (orientation == 4) {
					int tmp = a;
					a = b;
					b = tmp;
					c = vertex1.gsmart() + last;
					last = c;
					this.faceVertexA[f] = a;
					this.faceVertexB[f] = tmp;
					this.faceVertexC[f] = c;
				}
			}

			axis.pos = meta.faceTextureAxisOffset * 6;
			for (int f = 0; f < this.texturedFaceCount; f++) {
				this.texturedVertexA[f] = axis.g2();
				this.texturedVertexB[f] = axis.g2();
				this.texturedVertexC[f] = axis.g2();
			}
		}
	}

    public Model( Model[] models, int count) {
		boolean copyInfo = false;
		boolean copyPriorities = false;
		boolean copyAlpha = false;
		boolean copyLabels = false;

		this.vertexCount = 0;
		this.faceCount = 0;
		this.texturedFaceCount = 0;
		this.priority = -1;

		for ( int i = 0; i < count; i++) {
			Model model = models[i];
			if (model != null) {
				this.vertexCount += model.vertexCount;
				this.faceCount += model.faceCount;
				this.texturedFaceCount += model.texturedFaceCount;
				copyInfo |= model.faceInfo != null;

				if (model.facePriority == null) {
					if (this.priority == -1) {
						this.priority = model.priority;
					}

					if (this.priority != model.priority) {
						copyPriorities = true;
					}
				} else {
					copyPriorities = true;
				}

				copyAlpha |= model.faceAlpha != null;
				copyLabels |= model.faceLabel != null;
			}
		}

		this.vertexX = new int[this.vertexCount];
		this.vertexY = new int[this.vertexCount];
		this.vertexZ = new int[this.vertexCount];
		this.vertexLabel = new int[this.vertexCount];
		this.faceVertexA = new int[this.faceCount];
		this.faceVertexB = new int[this.faceCount];
		this.faceVertexC = new int[this.faceCount];
		this.texturedVertexA = new int[this.texturedFaceCount];
		this.texturedVertexB = new int[this.texturedFaceCount];
		this.texturedVertexC = new int[this.texturedFaceCount];

		if (copyInfo) {
			this.faceInfo = new int[this.faceCount];
		}

		if (copyPriorities) {
			this.facePriority = new int[this.faceCount];
		}

		if (copyAlpha) {
			this.faceAlpha = new int[this.faceCount];
		}

		if (copyLabels) {
			this.faceLabel = new int[this.faceCount];
		}

		this.faceColor = new int[this.faceCount];
		this.vertexCount = 0;
		this.faceCount = 0;
		this.texturedFaceCount = 0;

		for ( int i = 0; i < count; i++) {
			Model model = models[i];

			if (model != null) {
				for ( int face = 0; face < model.faceCount; face++) {
					if (copyInfo) {
						if (model.faceInfo == null) {
							this.faceInfo[this.faceCount] = 0;
						} else {
							this.faceInfo[this.faceCount] = model.faceInfo[face];
						}
					}

					if (copyPriorities) {
						if (model.facePriority == null) {
							this.facePriority[this.faceCount] = model.priority;
						} else {
							this.facePriority[this.faceCount] = model.facePriority[face];
						}
					}

					if (copyAlpha) {
						if (model.faceAlpha == null) {
							this.faceAlpha[this.faceCount] = 0;
						} else {
							this.faceAlpha[this.faceCount] = model.faceAlpha[face];
						}
					}

					if (copyLabels && model.faceLabel != null) {
						this.faceLabel[this.faceCount] = model.faceLabel[face];
					}

					this.faceColor[this.faceCount] = model.faceColor[face];
					this.faceVertexA[this.faceCount] = this.addVertex(model, model.faceVertexA[face]);
					this.faceVertexB[this.faceCount] = this.addVertex(model, model.faceVertexB[face]);
					this.faceVertexC[this.faceCount] = this.addVertex(model, model.faceVertexC[face]);
					this.faceCount++;
				}

				for ( int f = 0; f < model.texturedFaceCount; f++) {
					this.texturedVertexA[this.texturedFaceCount] = this.addVertex(model, model.texturedVertexA[f]);
					this.texturedVertexB[this.texturedFaceCount] = this.addVertex(model, model.texturedVertexB[f]);
					this.texturedVertexC[this.texturedFaceCount] = this.addVertex(model, model.texturedVertexC[f]);
					this.texturedFaceCount++;
				}
			}
		}
	}

    public Model( Model[] models, int count, boolean dummy) {
		boolean copyInfo = false;
		boolean copyPriority = false;
		boolean copyAlpha = false;
		boolean copyColor = false;

		this.vertexCount = 0;
		this.faceCount = 0;
		this.texturedFaceCount = 0;
		this.priority = -1;

		for ( int i = 0; i < count; i++) {
			Model model = models[i];
			if (model != null) {
				this.vertexCount += model.vertexCount;
				this.faceCount += model.faceCount;
				this.texturedFaceCount += model.texturedFaceCount;

				copyInfo |= model.faceInfo != null;

				if (model.facePriority == null) {
					if (this.priority == -1) {
						this.priority = model.priority;
					}
					if (this.priority != model.priority) {
						copyPriority = true;
					}
				} else {
					copyPriority = true;
				}

				copyAlpha |= model.faceAlpha != null;
				copyColor |= model.faceColor != null;
			}
		}

		this.vertexX = new int[this.vertexCount];
		this.vertexY = new int[this.vertexCount];
		this.vertexZ = new int[this.vertexCount];
		this.faceVertexA = new int[this.faceCount];
		this.faceVertexB = new int[this.faceCount];
		this.faceVertexC = new int[this.faceCount];
		this.faceColorA = new int[this.faceCount];
		this.faceColorB = new int[this.faceCount];
		this.faceColorC = new int[this.faceCount];
		this.texturedVertexA = new int[this.texturedFaceCount];
		this.texturedVertexB = new int[this.texturedFaceCount];
		this.texturedVertexC = new int[this.texturedFaceCount];

		if (copyInfo) {
			this.faceInfo = new int[this.faceCount];
		}

		if (copyPriority) {
			this.facePriority = new int[this.faceCount];
		}

		if (copyAlpha) {
			this.faceAlpha = new int[this.faceCount];
		}

		if (copyColor) {
			this.faceColor = new int[this.faceCount];
		}

		this.vertexCount = 0;
		this.faceCount = 0;
		this.texturedFaceCount = 0;

		int i;
		for (i = 0; i < count; i++) {
			Model model = models[i];
			if (model != null) {
				int vertexCount = this.vertexCount;

				for ( int v = 0; v < model.vertexCount; v++) {
					this.vertexX[this.vertexCount] = model.vertexX[v];
					this.vertexY[this.vertexCount] = model.vertexY[v];
					this.vertexZ[this.vertexCount] = model.vertexZ[v];
					this.vertexCount++;
				}

				for ( int f = 0; f < model.faceCount; f++) {
					this.faceVertexA[this.faceCount] = model.faceVertexA[f] + vertexCount;
					this.faceVertexB[this.faceCount] = model.faceVertexB[f] + vertexCount;
					this.faceVertexC[this.faceCount] = model.faceVertexC[f] + vertexCount;
					this.faceColorA[this.faceCount] = model.faceColorA[f];
					this.faceColorB[this.faceCount] = model.faceColorB[f];
					this.faceColorC[this.faceCount] = model.faceColorC[f];

					if (copyInfo) {
						if (model.faceInfo == null) {
							this.faceInfo[this.faceCount] = 0;
						} else {
							this.faceInfo[this.faceCount] = model.faceInfo[f];
						}
					}

					if (copyPriority) {
						if (model.facePriority == null) {
							this.facePriority[this.faceCount] = model.priority;
						} else {
							this.facePriority[this.faceCount] = model.facePriority[f];
						}
					}

					if (copyAlpha) {
						if (model.faceAlpha == null) {
							this.faceAlpha[this.faceCount] = 0;
						} else {
							this.faceAlpha[this.faceCount] = model.faceAlpha[f];
						}
					}

					if (copyColor && model.faceColor != null) {
						this.faceColor[this.faceCount] = model.faceColor[f];
					}

					this.faceCount++;
				}

				for ( int f = 0; f < model.texturedFaceCount; f++) {
					this.texturedVertexA[this.texturedFaceCount] = model.texturedVertexA[f] + vertexCount;
					this.texturedVertexB[this.texturedFaceCount] = model.texturedVertexB[f] + vertexCount;
					this.texturedVertexC[this.texturedFaceCount] = model.texturedVertexC[f] + vertexCount;
					this.texturedFaceCount++;
				}
			}
		}

		this.calculateBoundsCylinder();
	}

    public Model( Model src, boolean shareColors, boolean shareAlpha, boolean shareVertices) {
		this.vertexCount = src.vertexCount;
		this.faceCount = src.faceCount;
		this.texturedFaceCount = src.texturedFaceCount;

		if (shareVertices) {
			this.vertexX = src.vertexX;
			this.vertexY = src.vertexY;
			this.vertexZ = src.vertexZ;
		} else {
			this.vertexX = new int[this.vertexCount];
			this.vertexY = new int[this.vertexCount];
			this.vertexZ = new int[this.vertexCount];

			for (int v = 0; v < this.vertexCount; v++) {
				this.vertexX[v] = src.vertexX[v];
				this.vertexY[v] = src.vertexY[v];
				this.vertexZ[v] = src.vertexZ[v];
			}
		}

		if (shareColors) {
			this.faceColor = src.faceColor;
		} else {
			this.faceColor = new int[this.faceCount];
			System.arraycopy(src.faceColor, 0, this.faceColor, 0, this.faceCount);
		}

		if (shareAlpha) {
			this.faceAlpha = src.faceAlpha;
		} else {
			this.faceAlpha = new int[this.faceCount];
			if (src.faceAlpha == null) {
				for (int f = 0; f < this.faceCount; f++) {
					this.faceAlpha[f] = 0;
				}
			} else {
				System.arraycopy(src.faceAlpha, 0, this.faceAlpha, 0, this.faceCount);
			}
		}

		this.vertexLabel = src.vertexLabel;
		this.faceLabel = src.faceLabel;
		this.faceInfo = src.faceInfo;
		this.faceVertexA = src.faceVertexA;
		this.faceVertexB = src.faceVertexB;
		this.faceVertexC = src.faceVertexC;
		this.facePriority = src.facePriority;
		this.priority = src.priority;
		this.texturedVertexA = src.texturedVertexA;
		this.texturedVertexB = src.texturedVertexB;
		this.texturedVertexC = src.texturedVertexC;
	}

    public Model( Model src, boolean copyVertexY, boolean copyFaces) {
		this.vertexCount = src.vertexCount;
		this.faceCount = src.faceCount;
		this.texturedFaceCount = src.texturedFaceCount;

		if (copyVertexY) {
			this.vertexY = new int[this.vertexCount];
			System.arraycopy(src.vertexY, 0, this.vertexY, 0, this.vertexCount);
		} else {
			this.vertexY = src.vertexY;
		}

		if (copyFaces) {
			this.faceColorA = new int[this.faceCount];
			this.faceColorB = new int[this.faceCount];
			this.faceColorC = new int[this.faceCount];
			for (int f = 0; f < this.faceCount; f++) {
				this.faceColorA[f] = src.faceColorA[f];
				this.faceColorB[f] = src.faceColorB[f];
				this.faceColorC[f] = src.faceColorC[f];
			}

			this.faceInfo = new int[this.faceCount];
			if (src.faceInfo == null) {
				for (int f = 0; f < this.faceCount; f++) {
					this.faceInfo[f] = 0;
				}
			} else {
				System.arraycopy(src.faceInfo, 0, this.faceInfo, 0, this.faceCount);
			}

			this.vertexNormal = new VertexNormal[this.vertexCount];
			for (int v = 0; v < this.vertexCount; v++) {
				VertexNormal copy = this.vertexNormal[v] = new VertexNormal();
				VertexNormal original = src.vertexNormal[v];
				copy.x = original.x;
				copy.y = original.y;
				copy.z = original.z;
				copy.w = original.w;
			}

			this.vertexNormalOriginal = src.vertexNormalOriginal;
		} else {
			this.faceColorA = src.faceColorA;
			this.faceColorB = src.faceColorB;
			this.faceColorC = src.faceColorC;
			this.faceInfo = src.faceInfo;
		}

		this.vertexX = src.vertexX;
		this.vertexZ = src.vertexZ;
		this.faceColor = src.faceColor;
		this.faceAlpha = src.faceAlpha;
		this.facePriority = src.facePriority;
		this.priority = src.priority;
		this.faceVertexA = src.faceVertexA;
		this.faceVertexB = src.faceVertexB;
		this.faceVertexC = src.faceVertexC;
		this.texturedVertexA = src.texturedVertexA;
		this.texturedVertexB = src.texturedVertexB;
		this.texturedVertexC = src.texturedVertexC;
		this.maxY = src.maxY;
		this.minY = src.minY;
		this.radius = src.radius;
		this.minDepth = src.minDepth;
		this.maxDepth = src.maxDepth;
		this.minX = src.minX;
		this.maxZ = src.maxZ;
		this.minZ = src.minZ;
		this.maxX = src.maxX;
	}

    public Model( Model src, boolean shareAlpha) {
		this.vertexCount = src.vertexCount;
		this.faceCount = src.faceCount;
		this.texturedFaceCount = src.texturedFaceCount;

		this.vertexX = new int[this.vertexCount];
		this.vertexY = new int[this.vertexCount];
		this.vertexZ = new int[this.vertexCount];

		for ( int v = 0; v < this.vertexCount; v++) {
			this.vertexX[v] = src.vertexX[v];
			this.vertexY[v] = src.vertexY[v];
			this.vertexZ[v] = src.vertexZ[v];
		}

		if (shareAlpha) {
			this.faceAlpha = src.faceAlpha;
		} else {
			this.faceAlpha = new int[this.faceCount];
			if (src.faceAlpha == null) {
				for (int f = 0; f < this.faceCount; f++) {
					this.faceAlpha[f] = 0;
				}
			} else {
				System.arraycopy(src.faceAlpha, 0, this.faceAlpha, 0, this.faceCount);
			}
		}

		this.faceInfo = src.faceInfo;
		this.faceColor = src.faceColor;
		this.facePriority = src.facePriority;
		this.priority = src.priority;
		this.labelFaces = src.labelFaces;
		this.labelVertices = src.labelVertices;
		this.faceVertexA = src.faceVertexA;
		this.faceVertexB = src.faceVertexB;
		this.faceVertexC = src.faceVertexC;
		this.faceColorA = src.faceColorA;
		this.faceColorB = src.faceColorB;
		this.faceColorC = src.faceColorC;
		this.texturedVertexA = src.texturedVertexA;
		this.texturedVertexB = src.texturedVertexB;
		this.texturedVertexC = src.texturedVertexC;
	}

    public static void unload() {
		metadata = null;
		head = null;
		face1 = null;
		face2 = null;
		face3 = null;
		face4 = null;
		face5 = null;
		point1 = null;
		point2 = null;
		point3 = null;
		point4 = null;
		point5 = null;
		vertex1 = null;
		vertex2 = null;
		axis = null;
		faceClippedX = null;
		faceNearClipped = null;
		vertexScreenX = null;
		vertexScreenY = null;
		vertexScreenZ = null;
		vertexViewSpaceX = null;
		vertexViewSpaceY = null;
		vertexViewSpaceZ = null;
		tmpDepthFaceCount = null;
		tmpDepthFaces = null;
		tmpPriorityFaceCount = null;
		tmpPriorityFaces = null;
		tmpPriority10FaceDepth = null;
		tmpPriority11FaceDepth = null;
		tmpPriorityDepthSum = null;
		sin = null;
		cos = null;
		palette = null;
		reciprical16 = null;
	}

    public static void unpack( Jagfile models) {
		try {
			head = new Packet(models.read("ob_head.dat", null));
			face1 = new Packet(models.read("ob_face1.dat", null));
			face2 = new Packet(models.read("ob_face2.dat", null));
			face3 = new Packet(models.read("ob_face3.dat", null));
			face4 = new Packet(models.read("ob_face4.dat", null));
			face5 = new Packet(models.read("ob_face5.dat", null));
			point1 = new Packet(models.read("ob_point1.dat", null));
			point2 = new Packet(models.read("ob_point2.dat", null));
			point3 = new Packet(models.read("ob_point3.dat", null));
			point4 = new Packet(models.read("ob_point4.dat", null));
			point5 = new Packet(models.read("ob_point5.dat", null));
			vertex1 = new Packet(models.read("ob_vertex1.dat", null));
			vertex2 = new Packet(models.read("ob_vertex2.dat", null));
			axis = new Packet(models.read("ob_axis.dat", null));
			head.pos = 0;
			point1.pos = 0;
			point2.pos = 0;
			point3.pos = 0;
			point4.pos = 0;
			vertex1.pos = 0;
			vertex2.pos = 0;
			int count = head.g2();
			metadata = new Metadata[count + 100];
			int vertexTextureDataOffset = 0;
			int labelDataOffset = 0;
			int triangleColorDataOffset = 0;
			int triangleInfoDataOffset = 0;
			int trianglePriorityDataOffset = 0;
			int triangleAlphaDataOffset = 0;
			int triangleSkinDataOffset = 0;
			for ( int i = 0; i < count; i++) {
				int index = head.g2();
				Metadata meta = metadata[index] = new Metadata();
				meta.vertexCount = head.g2();
				meta.faceCount = head.g2();
				meta.texturedFaceCount = head.g1();
				meta.vertexFlagsOffset = point1.pos;
				meta.vertexXOffset = point2.pos;
				meta.vertexYOffset = point3.pos;
				meta.vertexZOffset = point4.pos;
				meta.faceVerticesOffset = vertex1.pos;
				meta.faceOrientationsOffset = vertex2.pos;
				int hasInfo = head.g1();
				int hasPriorities = head.g1();
				int hasAlpha = head.g1();
				int hasSkins = head.g1();
				int hasLabels = head.g1();
				for ( int v = 0; v < meta.vertexCount; v++) {
					int flags = point1.g1();
					if ((flags & 0x1) != 0) {
						point2.gsmart();
					}
					if ((flags & 0x2) != 0) {
						point3.gsmart();
					}
					if ((flags & 0x4) != 0) {
						point4.gsmart();
					}
				}
				for (int f = 0; f < meta.faceCount; f++) {
					int type = vertex2.g1();
					if (type == 1) {
						vertex1.gsmart();
						vertex1.gsmart();
					}
					vertex1.gsmart();
				}
				meta.faceColorsOffset = triangleColorDataOffset;
				triangleColorDataOffset += meta.faceCount * 2;
				if (hasInfo == 1) {
					meta.faceInfosOffset = triangleInfoDataOffset;
					triangleInfoDataOffset += meta.faceCount;
				} else {
					meta.faceInfosOffset = -1;
				}
				if (hasPriorities == 255) {
					meta.facePrioritiesOffset = trianglePriorityDataOffset;
					trianglePriorityDataOffset += meta.faceCount;
				} else {
					meta.facePrioritiesOffset = -hasPriorities - 1;
				}
				if (hasAlpha == 1) {
					meta.faceAlphasOffset = triangleAlphaDataOffset;
					triangleAlphaDataOffset += meta.faceCount;
				} else {
					meta.faceAlphasOffset = -1;
				}
				if (hasSkins == 1) {
					meta.faceLabelsOffset = triangleSkinDataOffset;
					triangleSkinDataOffset += meta.faceCount;
				} else {
					meta.faceLabelsOffset = -1;
				}
				if (hasLabels == 1) {
					meta.vertexLabelsOffset = labelDataOffset;
					labelDataOffset += meta.vertexCount;
				} else {
					meta.vertexLabelsOffset = -1;
				}
				meta.faceTextureAxisOffset = vertexTextureDataOffset;
				vertexTextureDataOffset += meta.texturedFaceCount;
			}
		} catch ( Exception ex) {
			System.out.println("Error loading model index");
			ex.printStackTrace();
		}
	}

    public static int mulColorLightness( int hsl, int scalar, int faceInfo) {
		if ((faceInfo & 0x2) == 2) {
			if (scalar < 0) {
				scalar = 0;
			} else if (scalar > 127) {
				scalar = 127;
			}
			return 127 - scalar;
		}
		scalar = scalar * (hsl & 0x7F) >> 7;
		if (scalar < 2) {
			scalar = 2;
		} else if (scalar > 126) {
			scalar = 126;
		}
		return (hsl & 0xFF80) + scalar;
	}

    private int addVertex( Model src, int vertexId) {
		int identical = -1;
		int x = src.vertexX[vertexId];
		int y = src.vertexY[vertexId];
		int z = src.vertexZ[vertexId];
		for ( int v = 0; v < this.vertexCount; v++) {
			if (x == this.vertexX[v] && y == this.vertexY[v] && z == this.vertexZ[v]) {
				identical = v;
				break;
			}
		}
		if (identical == -1) {
			this.vertexX[this.vertexCount] = x;
			this.vertexY[this.vertexCount] = y;
			this.vertexZ[this.vertexCount] = z;
			if (src.vertexLabel != null) {
				this.vertexLabel[this.vertexCount] = src.vertexLabel[vertexId];
			}
			identical = this.vertexCount++;
		}
		return identical;
	}

    public void calculateBoundsCylinder() {
		this.maxY = 0;
		this.radius = 0;
		this.minY = 0;
		for ( int i = 0; i < this.vertexCount; i++) {
			int x = this.vertexX[i];
			int y = this.vertexY[i];
			int z = this.vertexZ[i];
			if (-y > this.maxY) {
				this.maxY = -y;
			}
			if (y > this.minY) {
				this.minY = y;
			}
			int radiusSqr = x * x + z * z;
			if (radiusSqr > this.radius) {
				this.radius = radiusSqr;
			}
		}
		this.radius = (int) (Math.sqrt(this.radius) + 0.99D);
		this.minDepth = (int) (Math.sqrt(this.radius * this.radius + this.maxY * this.maxY) + 0.99D);
		this.maxDepth = this.minDepth + (int) (Math.sqrt(this.radius * this.radius + this.minY * this.minY) + 0.99D);
	}

    public void calculateBoundsY() {
		this.maxY = 0;
		this.minY = 0;
		for ( int v = 0; v < this.vertexCount; v++) {
			int y = this.vertexY[v];
			if (-y > this.maxY) {
				this.maxY = -y;
			}
			if (y > this.minY) {
				this.minY = y;
			}
		}
		this.minDepth = (int) (Math.sqrt(this.radius * this.radius + this.maxY * this.maxY) + 0.99D);
		this.maxDepth = this.minDepth + (int) (Math.sqrt(this.radius * this.radius + this.minY * this.minY) + 0.99D);
	}

    private void calculateBoundsAABB() {
		this.maxY = 0;
		this.radius = 0;
		this.minY = 0;
		this.minX = 999999;
		this.maxX = -999999;
		this.maxZ = -99999;
		this.minZ = 99999;
		for ( int v = 0; v < this.vertexCount; v++) {
			int x = this.vertexX[v];
			int y = this.vertexY[v];
			int z = this.vertexZ[v];
			if (x < this.minX) {
				this.minX = x;
			}
			if (x > this.maxX) {
				this.maxX = x;
			}
			if (z < this.minZ) {
				this.minZ = z;
			}
			if (z > this.maxZ) {
				this.maxZ = z;
			}
			if (-y > this.maxY) {
				this.maxY = -y;
			}
			if (y > this.minY) {
				this.minY = y;
			}
			int radiusSqr = x * x + z * z;
			if (radiusSqr > this.radius) {
				this.radius = radiusSqr;
			}
		}
		this.radius = (int) Math.sqrt(this.radius);
		this.minDepth = (int) Math.sqrt(this.radius * this.radius + this.maxY * this.maxY);
		this.maxDepth = this.minDepth + (int) Math.sqrt(this.radius * this.radius + this.minY * this.minY);
	}

    public void createLabelReferences() {
		if (this.vertexLabel != null) {
			int[] labelVertexCount = new int[256];
			int count = 0;
			for (int v = 0; v < this.vertexCount; v++) {
				int label = this.vertexLabel[v];
				int countDebug = labelVertexCount[label]++;
				if (label > count) {
					count = label;
				}
			}
			this.labelVertices = new int[count + 1][];
			for (int label = 0; label <= count; label++) {
				this.labelVertices[label] = new int[labelVertexCount[label]];
				labelVertexCount[label] = 0;
			}
			int v = 0;
			while (v < this.vertexCount) {
				int label = this.vertexLabel[v];
				this.labelVertices[label][labelVertexCount[label]++] = v++;
			}
			this.vertexLabel = null;
		}

		if (this.faceLabel != null) {
			int[] labelFaceCount = new int[256];
			int count = 0;
			for (int f = 0; f < this.faceCount; f++) {
				int label = this.faceLabel[f];
				int countDebug = labelFaceCount[label]++;
				if (label > count) {
					count = label;
				}
			}
			this.labelFaces = new int[count + 1][];
			for (int label = 0; label <= count; label++) {
				this.labelFaces[label] = new int[labelFaceCount[label]];
				labelFaceCount[label] = 0;
			}
			int face = 0;
			while (face < this.faceCount) {
				int label = this.faceLabel[face];
				this.labelFaces[label][labelFaceCount[label]++] = face++;
			}
			this.faceLabel = null;
		}
	}

    public void applyTransform( int id) {
		if (this.labelVertices != null && id != -1) {
			SeqFrame transform = SeqFrame.instances[id];
			SeqBase skeleton = transform.base;
			baseX = 0;
			baseY = 0;
			baseZ = 0;
			for ( int i = 0; i < transform.length; i++) {
				int base = transform.bases[i];
				this.applyTransform(transform.x[i], transform.y[i], transform.z[i], skeleton.labels[base], skeleton.types[base]);
			}
		}
	}

    public void applyTransforms( int primaryId, int secondaryId, int[] mask) {
		if (primaryId == -1) {
			return;
		}

		if (mask == null || secondaryId == -1) {
			this.applyTransform(primaryId);
		} else {
			SeqFrame primary = SeqFrame.instances[primaryId];
			SeqFrame secondary = SeqFrame.instances[secondaryId];
			SeqBase skeleton = primary.base;

			baseX = 0;
			baseY = 0;
			baseZ = 0;

			int counter = 0;
			int maskBase = mask[counter++];

			for ( int i = 0; i < primary.length; i++) {
				int base = primary.bases[i];
				while (base > maskBase) {
					maskBase = mask[counter++];
				}

				if (base != maskBase || skeleton.types[base] == 0) {
					this.applyTransform(primary.x[i], primary.y[i], primary.z[i], skeleton.labels[base], skeleton.types[base]);
				}
			}

			baseX = 0;
			baseY = 0;
			baseZ = 0;

			counter = 0;
			maskBase = mask[counter++];

			for (int i = 0; i < secondary.length; i++) {
				int base = secondary.bases[i];
				while (base > maskBase) {
					maskBase = mask[counter++];
				}

				if (base == maskBase || skeleton.types[base] == 0) {
					this.applyTransform(secondary.x[i], secondary.y[i], secondary.z[i], skeleton.labels[base], skeleton.types[base]);
				}
			}
		}
	}

    private void applyTransform( int x, int y, int z, int[] labels, int type) {
		int labelCount = labels.length;

		if (type == 0) {
			int count = 0;
			baseX = 0;
			baseY = 0;
			baseZ = 0;

			for (int g = 0; g < labelCount; g++) {
				int label = labels[g];
				if (label < this.labelVertices.length) {
					int[] vertices = this.labelVertices[label];
					for (int i = 0; i < vertices.length; i++) {
						int v = vertices[i];
						baseX += this.vertexX[v];
						baseY += this.vertexY[v];
						baseZ += this.vertexZ[v];
						count++;
					}
				}
			}

			if (count > 0) {
				baseX = baseX / count + x;
				baseY = baseY / count + y;
				baseZ = baseZ / count + z;
			} else {
				baseX = x;
				baseY = y;
				baseZ = z;
			}
		} else if (type == 1) {
			for (int g = 0; g < labelCount; g++) {
				int group = labels[g];
				if (group >= this.labelVertices.length) {
					continue;
				}

				int[] vertices = this.labelVertices[group];
				for (int i = 0; i < vertices.length; i++) {
					int v = vertices[i];
					this.vertexX[v] += x;
					this.vertexY[v] += y;
					this.vertexZ[v] += z;
				}
			}
		} else if (type == 2) {
			for (int g = 0; g < labelCount; g++) {
				int label = labels[g];
				if (label >= this.labelVertices.length) {
					continue;
				}

				int[] vertices = this.labelVertices[label];
				for (int i = 0; i < vertices.length; i++) {
					int v = vertices[i];
					this.vertexX[v] -= baseX;
					this.vertexY[v] -= baseY;
					this.vertexZ[v] -= baseZ;

					int pitch = (x & 0xFF) * 8;
					int yaw = (y & 0xFF) * 8;
					int roll = (z & 0xFF) * 8;

					int sin;
					int cos;

					if (roll != 0) {
						sin = Model.sin[roll];
						cos = Model.cos[roll];
						int x_ = this.vertexY[v] * sin + this.vertexX[v] * cos >> 16;
						this.vertexY[v] = this.vertexY[v] * cos - this.vertexX[v] * sin >> 16;
						this.vertexX[v] = x_;
					}

					if (pitch != 0) {
						sin = Model.sin[pitch];
						cos = Model.cos[pitch];
						int y_ = this.vertexY[v] * cos - this.vertexZ[v] * sin >> 16;
						this.vertexZ[v] = this.vertexY[v] * sin + this.vertexZ[v] * cos >> 16;
						this.vertexY[v] = y_;
					}

					if (yaw != 0) {
						sin = Model.sin[yaw];
						cos = Model.cos[yaw];
						int x_ = this.vertexZ[v] * sin + this.vertexX[v] * cos >> 16;
						this.vertexZ[v] = this.vertexZ[v] * cos - this.vertexX[v] * sin >> 16;
						this.vertexX[v] = x_;
					}

					this.vertexX[v] += baseX;
					this.vertexY[v] += baseY;
					this.vertexZ[v] += baseZ;
				}
			}
		} else if (type == 3) {
			for (int g = 0; g < labelCount; g++) {
				int label = labels[g];
				if (label >= this.labelVertices.length) {
					continue;
				}

				int[] vertices = this.labelVertices[label];
				for (int i = 0; i < vertices.length; i++) {
					int v = vertices[i];
					this.vertexX[v] -= baseX;
					this.vertexY[v] -= baseY;
					this.vertexZ[v] -= baseZ;
					this.vertexX[v] = this.vertexX[v] * x / 128;
					this.vertexY[v] = this.vertexY[v] * y / 128;
					this.vertexZ[v] = this.vertexZ[v] * z / 128;
					this.vertexX[v] += baseX;
					this.vertexY[v] += baseY;
					this.vertexZ[v] += baseZ;
				}
			}
		} else if (type == 5 && (this.labelFaces != null && this.faceAlpha != null)) {
			for (int g = 0; g < labelCount; g++) {
				int label = labels[g];
				if (label >= this.labelFaces.length) {
					continue;
				}

				int[] triangles = this.labelFaces[label];
				for (int i = 0; i < triangles.length; i++) {
					int t = triangles[i];

					this.faceAlpha[t] += x * 8;
					if (this.faceAlpha[t] < 0) {
						this.faceAlpha[t] = 0;
					}

					if (this.faceAlpha[t] > 255) {
						this.faceAlpha[t] = 255;
					}
				}
			}
		}
	}

    public void rotateY90() {
		for ( int v = 0; v < this.vertexCount; v++) {
			int tmp = this.vertexX[v];
			this.vertexX[v] = this.vertexZ[v];
			this.vertexZ[v] = -tmp;
		}
	}

    public void rotateX( int angle) {
		int sin = Model.sin[angle];
		int cos = Model.cos[angle];
		for ( int v = 0; v < this.vertexCount; v++) {
			int tmp = this.vertexY[v] * cos - this.vertexZ[v] * sin >> 16;
			this.vertexZ[v] = this.vertexY[v] * sin + this.vertexZ[v] * cos >> 16;
			this.vertexY[v] = tmp;
		}
	}

    public void translate( int y, int x, int z) {
		for ( int v = 0; v < this.vertexCount; v++) {
			this.vertexX[v] += x;
			this.vertexY[v] += y;
			this.vertexZ[v] += z;
		}
	}

    public void recolor( int src, int dst) {
		for ( int f = 0; f < this.faceCount; f++) {
			if (this.faceColor[f] == src) {
				this.faceColor[f] = dst;
			}
		}
	}

    public void rotateY180() {
		for ( int v = 0; v < this.vertexCount; v++) {
			this.vertexZ[v] = -this.vertexZ[v];
		}

		for ( int f = 0; f < this.faceCount; f++) {
			int temp = this.faceVertexA[f];
			this.faceVertexA[f] = this.faceVertexC[f];
			this.faceVertexC[f] = temp;
		}
	}

    public void scale( int x, int y, int z) {
		for ( int v = 0; v < this.vertexCount; v++) {
			this.vertexX[v] = this.vertexX[v] * x / 128;
			this.vertexY[v] = this.vertexY[v] * y / 128;
			this.vertexZ[v] = this.vertexZ[v] * z / 128;
		}
	}

    public void calculateNormals( int lightAmbient, int lightAttenuation, int lightSrcX, int lightSrcY, int lightSrcZ, boolean applyLighting) {
		int lightMagnitude = (int) Math.sqrt(lightSrcX * lightSrcX + lightSrcY * lightSrcY + lightSrcZ * lightSrcZ);
		int attenuation = lightAttenuation * lightMagnitude >> 8;

		if (this.faceColorA == null) {
			this.faceColorA = new int[this.faceCount];
			this.faceColorB = new int[this.faceCount];
			this.faceColorC = new int[this.faceCount];
		}

		if (this.vertexNormal == null) {
			this.vertexNormal = new VertexNormal[this.vertexCount];
			for (int v = 0; v < this.vertexCount; v++) {
				this.vertexNormal[v] = new VertexNormal();
			}
		}

		for (int f = 0; f < this.faceCount; f++) {
			int a = this.faceVertexA[f];
			int b = this.faceVertexB[f];
			int c = this.faceVertexC[f];

			int dxAB = this.vertexX[b] - this.vertexX[a];
			int dyAB = this.vertexY[b] - this.vertexY[a];
			int dzAB = this.vertexZ[b] - this.vertexZ[a];

			int dxAC = this.vertexX[c] - this.vertexX[a];
			int dyAC = this.vertexY[c] - this.vertexY[a];
			int dzAC = this.vertexZ[c] - this.vertexZ[a];

			int nx = dyAB * dzAC - dyAC * dzAB;
			int ny = dzAB * dxAC - dzAC * dxAB;
			int nz;
			for (nz = dxAB * dyAC - dxAC * dyAB; nx > 8192 || ny > 8192 || nz > 8192 || nx < -8192 || ny < -8192 || nz < -8192; nz >>= 0x1) {
				nx >>= 0x1;
				ny >>= 0x1;
			}

			int length = (int) Math.sqrt(nx * nx + ny * ny + nz * nz);
			if (length <= 0) {
				length = 1;
			}

			nx = nx * 256 / length;
			ny = ny * 256 / length;
			nz = nz * 256 / length;

			if (this.faceInfo == null || (this.faceInfo[f] & 0x1) == 0) {
				VertexNormal n = this.vertexNormal[a];
				n.x += nx;
				n.y += ny;
				n.z += nz;
				n.w++;

				n = this.vertexNormal[b];
				n.x += nx;
				n.y += ny;
				n.z += nz;
				n.w++;

				n = this.vertexNormal[c];
				n.x += nx;
				n.y += ny;
				n.z += nz;
				n.w++;
			} else {
				int lightness = lightAmbient + (lightSrcX * nx + lightSrcY * ny + lightSrcZ * nz) / (attenuation + attenuation / 2);
				this.faceColorA[f] = mulColorLightness(this.faceColor[f], lightness, this.faceInfo[f]);
			}
		}

		if (applyLighting) {
			this.applyLighting(lightAmbient, attenuation, lightSrcX, lightSrcY, lightSrcZ);
		} else {
			this.vertexNormalOriginal = new VertexNormal[this.vertexCount];
			for (int v = 0; v < this.vertexCount; v++) {
				VertexNormal normal = this.vertexNormal[v];
				VertexNormal copy = this.vertexNormalOriginal[v] = new VertexNormal();
				copy.x = normal.x;
				copy.y = normal.y;
				copy.z = normal.z;
				copy.w = normal.w;
			}
		}

		if (applyLighting) {
			this.calculateBoundsCylinder();
		} else {
			this.calculateBoundsAABB();
		}
	}

    public void applyLighting( int lightAmbient, int lightAttenuation, int lightSrcX, int lightSrcY, int lightSrcZ) {
		for ( int f = 0; f < this.faceCount; f++) {
			int a = this.faceVertexA[f];
			int b = this.faceVertexB[f];
			int c = this.faceVertexC[f];

			if (this.faceInfo == null) {
				int color = this.faceColor[f];

				VertexNormal n = this.vertexNormal[a];
				int lightness = lightAmbient + (lightSrcX * n.x + lightSrcY * n.y + lightSrcZ * n.z) / (lightAttenuation * n.w);
				this.faceColorA[f] = mulColorLightness(color, lightness, 0);

				n = this.vertexNormal[b];
				lightness = lightAmbient + (lightSrcX * n.x + lightSrcY * n.y + lightSrcZ * n.z) / (lightAttenuation * n.w);
				this.faceColorB[f] = mulColorLightness(color, lightness, 0);

				n = this.vertexNormal[c];
				lightness = lightAmbient + (lightSrcX * n.x + lightSrcY * n.y + lightSrcZ * n.z) / (lightAttenuation * n.w);
				this.faceColorC[f] = mulColorLightness(color, lightness, 0);
			} else if ((this.faceInfo[f] & 0x1) == 0) {
				int color = this.faceColor[f];
				int info = this.faceInfo[f];

				VertexNormal n = this.vertexNormal[a];
				int lightness = lightAmbient + (lightSrcX * n.x + lightSrcY * n.y + lightSrcZ * n.z) / (lightAttenuation * n.w);
				this.faceColorA[f] = mulColorLightness(color, lightness, info);

				n = this.vertexNormal[b];
				lightness = lightAmbient + (lightSrcX * n.x + lightSrcY * n.y + lightSrcZ * n.z) / (lightAttenuation * n.w);
				this.faceColorB[f] = mulColorLightness(color, lightness, info);

				n = this.vertexNormal[c];
				lightness = lightAmbient + (lightSrcX * n.x + lightSrcY * n.y + lightSrcZ * n.z) / (lightAttenuation * n.w);
				this.faceColorC[f] = mulColorLightness(color, lightness, info);
			}
		}

		this.vertexNormal = null;
		this.vertexNormalOriginal = null;
		this.vertexLabel = null;
		this.faceLabel = null;

		if (this.faceInfo != null) {
			for (int f = 0; f < this.faceCount; f++) {
				if ((this.faceInfo[f] & 0x2) == 2) {
					return;
				}
			}
		}

		this.faceColor = null;
	}

    public void drawSimple( int pitch, int yaw, int roll, int eyePitch, int eyeX, int eyeY, int eyeZ) {
		int centerX = Draw3D.centerX;
		int centerY = Draw3D.centerY;
		int sinPitch = sin[pitch];
		int cosPitch = cos[pitch];
		int sinYaw = sin[yaw];
		int cosYaw = cos[yaw];
		int sinRoll = sin[roll];
		int cosRoll = cos[roll];
		int sinEyePitch = sin[eyePitch];
		int cosEyePitch = cos[eyePitch];
		int midZ = eyeY * sinEyePitch + eyeZ * cosEyePitch >> 16;

		for ( int v = 0; v < this.vertexCount; v++) {
			int x = this.vertexX[v];
			int y = this.vertexY[v];
			int z = this.vertexZ[v];

			int temp;
			if (roll != 0) {
				temp = y * sinRoll + x * cosRoll >> 16;
				y = y * cosRoll - x * sinRoll >> 16;
				x = temp;
			}

			if (pitch != 0) {
				temp = y * cosPitch - z * sinPitch >> 16;
				z = y * sinPitch + z * cosPitch >> 16;
				y = temp;
			}

			if (yaw != 0) {
				temp = z * sinYaw + x * cosYaw >> 16;
				z = z * cosYaw - x * sinYaw >> 16;
				x = temp;
			}

			x += eyeX;
			y += eyeY;
			z += eyeZ;

			temp = y * cosEyePitch - z * sinEyePitch >> 16;
			z = y * sinEyePitch + z * cosEyePitch >> 16;

			vertexScreenZ[v] = z - midZ;
			vertexScreenX[v] = centerX + (x << 9) / z;
			vertexScreenY[v] = centerY + (temp << 9) / z;

			if (this.texturedFaceCount > 0) {
				vertexViewSpaceX[v] = x;
				vertexViewSpaceY[v] = temp;
				vertexViewSpaceZ[v] = z;
			}
		}

		try {
			this.draw(false, false, 0);
		} catch ( Exception ex) {
		}
	}

    public void draw( int yaw, int sinEyePitch, int cosEyePitch, int sinEyeYaw, int cosEyeYaw, int relativeX, int relativeY, int relativeZ, int bitset) {
		int zPrime = relativeZ * cosEyeYaw - relativeX * sinEyeYaw >> 16;
		int midZ = relativeY * sinEyePitch + zPrime * cosEyePitch >> 16;
		int radiusCosEyePitch = this.radius * cosEyePitch >> 16;

		int maxZ = midZ + radiusCosEyePitch;
		if (maxZ <= 50 || midZ >= 3500) {
			return;
		}

		int midX = relativeZ * sinEyeYaw + relativeX * cosEyeYaw >> 16;
		int leftX = midX - this.radius << 9;
		if (leftX / maxZ >= Draw2D.centerX2d) {
			return;
		}

		int rightX = midX + this.radius << 9;
		if (rightX / maxZ <= -Draw2D.centerX2d) {
			return;
		}

		int midY = relativeY * cosEyePitch - zPrime * sinEyePitch >> 16;
		int radiusSinEyePitch = this.radius * sinEyePitch >> 16;

		int bottomY = midY + radiusSinEyePitch << 9;
		if (bottomY / maxZ <= -Draw2D.centerY2d) {
			return;
		}

		int yPrime = radiusSinEyePitch + (this.maxY * cosEyePitch >> 16);
		int topY = midY - yPrime << 9;
		if (topY / maxZ >= Draw2D.centerY2d) {
			return;
		}

		int radiusZ = radiusCosEyePitch + (this.maxY * sinEyePitch >> 16);

		boolean clipped = midZ - radiusZ <= 50;
		boolean picking = false;

		if (bitset > 0 && checkHover) {
			int z = midZ - radiusCosEyePitch;
			if (z <= 50) {
				z = 50;
			}

			if (midX > 0) {
				leftX /= maxZ;
				rightX /= z;
			} else {
				rightX /= maxZ;
				leftX /= z;
			}

			if (midY > 0) {
				topY /= maxZ;
				bottomY /= z;
			} else {
				bottomY /= maxZ;
				topY /= z;
			}

			int mouseX = Model.mouseX - Draw3D.centerX;
			int mouseY = mouseZ - Draw3D.centerY;
			if (mouseX > leftX && mouseX < rightX && mouseY > topY && mouseY < bottomY) {
				if (this.pickable) {
					pickedBitsets[pickedCount++] = bitset;
				} else {
					picking = true;
				}
			}
		}

		int centerX = Draw3D.centerX;
		int centerY = Draw3D.centerY;

		int sinYaw = 0;
		int cosYaw = 0;
		if (yaw != 0) {
			sinYaw = sin[yaw];
			cosYaw = cos[yaw];
		}

		for ( int v = 0; v < this.vertexCount; v++) {
			int x = this.vertexX[v];
			int y = this.vertexY[v];
			int z = this.vertexZ[v];

			int temp;
			if (yaw != 0) {
				temp = z * sinYaw + x * cosYaw >> 16;
				z = z * cosYaw - x * sinYaw >> 16;
				x = temp;
			}

			x += relativeX;
			y += relativeY;
			z += relativeZ;

			temp = z * sinEyeYaw + x * cosEyeYaw >> 16;
			z = z * cosEyeYaw - x * sinEyeYaw >> 16;
			x = temp;

			temp = y * cosEyePitch - z * sinEyePitch >> 16;
			z = y * sinEyePitch + z * cosEyePitch >> 16;

			vertexScreenZ[v] = z - midZ;

			if (z >= 50) {
				vertexScreenX[v] = centerX + (x << 9) / z;
				vertexScreenY[v] = centerY + (temp << 9) / z;
			} else {
				vertexScreenX[v] = -5000;
				clipped = true;
			}

			if (clipped || this.texturedFaceCount > 0) {
				vertexViewSpaceX[v] = x;
				vertexViewSpaceY[v] = temp;
				vertexViewSpaceZ[v] = z;
			}
		}

		try {
			this.draw(clipped, picking, bitset);
		} catch ( Exception ex) {
		}
	}

    private void draw( boolean clipped, boolean picking, int bitset) {
		for ( int depth = 0; depth < this.maxDepth; depth++) {
			tmpDepthFaceCount[depth] = 0;
		}

		for ( int f = 0; f < this.faceCount; f++) {
			if (this.faceInfo == null || this.faceInfo[f] != -1) {
				int a = this.faceVertexA[f];
				int b = this.faceVertexB[f];
				int c = this.faceVertexC[f];

				int xA = vertexScreenX[a];
				int xB = vertexScreenX[b];
				int xC = vertexScreenX[c];

				if (clipped && (xA == -5000 || xB == -5000 || xC == -5000)) {
					faceNearClipped[f] = true;
					int depthAverage = (vertexScreenZ[a] + vertexScreenZ[b] + vertexScreenZ[c]) / 3 + this.minDepth;
					tmpDepthFaces[depthAverage][tmpDepthFaceCount[depthAverage]++] = f;
				} else {
					if (picking && this.pointWithinTriangle(mouseX, mouseZ, vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], xA, xB, xC)) {
						pickedBitsets[pickedCount++] = bitset;
						picking = false;
					}

					if ((xA - xB) * (vertexScreenY[c] - vertexScreenY[b]) - (vertexScreenY[a] - vertexScreenY[b]) * (xC - xB) > 0) {
						faceNearClipped[f] = false;
						faceClippedX[f] = xA < 0 || xB < 0 || xC < 0 || xA > Draw2D.boundX || xB > Draw2D.boundX || xC > Draw2D.boundX;
						int depthAverage = (vertexScreenZ[a] + vertexScreenZ[b] + vertexScreenZ[c]) / 3 + this.minDepth;
						tmpDepthFaces[depthAverage][tmpDepthFaceCount[depthAverage]++] = f;
					}
				}
			}
		}

		if (this.facePriority == null) {
			for (int depth = this.maxDepth - 1; depth >= 0; depth--) {
				int count = tmpDepthFaceCount[depth];
				if (count > 0) {
					int[] faces = tmpDepthFaces[depth];
					for (int f = 0; f < count; f++) {
						this.drawFace(faces[f]);
					}
				}
			}
			return;
		}

		for (int priority = 0; priority < 12; priority++) {
			tmpPriorityFaceCount[priority] = 0;
			tmpPriorityDepthSum[priority] = 0;
		}

		for (int depth = this.maxDepth - 1; depth >= 0; depth--) {
			int faceCount = tmpDepthFaceCount[depth];
			if (faceCount > 0) {
				int[] faces = tmpDepthFaces[depth];
				for (int i = 0; i < faceCount; i++) {
					int priorityDepth = faces[i];
					int priorityFace = this.facePriority[priorityDepth];
					int priorityFaceCount = tmpPriorityFaceCount[priorityFace]++;
					tmpPriorityFaces[priorityFace][priorityFaceCount] = priorityDepth;
					if (priorityFace < 10) {
						tmpPriorityDepthSum[priorityFace] += depth;
					} else if (priorityFace == 10) {
						tmpPriority10FaceDepth[priorityFaceCount] = depth;
					} else {
						tmpPriority11FaceDepth[priorityFaceCount] = depth;
					}
				}
			}
		}

		int averagePriorityDepthSum1_2 = 0;
		if (tmpPriorityFaceCount[1] > 0 || tmpPriorityFaceCount[2] > 0) {
			averagePriorityDepthSum1_2 = (tmpPriorityDepthSum[1] + tmpPriorityDepthSum[2]) / (tmpPriorityFaceCount[1] + tmpPriorityFaceCount[2]);
		}
		int averagePriorityDepthSum3_4 = 0;
		if (tmpPriorityFaceCount[3] > 0 || tmpPriorityFaceCount[4] > 0) {
			averagePriorityDepthSum3_4 = (tmpPriorityDepthSum[3] + tmpPriorityDepthSum[4]) / (tmpPriorityFaceCount[3] + tmpPriorityFaceCount[4]);
		}
		int averagePriorityDepthSum6_8 = 0;
		if (tmpPriorityFaceCount[6] > 0 || tmpPriorityFaceCount[8] > 0) {
			averagePriorityDepthSum6_8 = (tmpPriorityDepthSum[6] + tmpPriorityDepthSum[8]) / (tmpPriorityFaceCount[6] + tmpPriorityFaceCount[8]);
		}

		int priorityFace = 0;
		int priorityFaceCount = tmpPriorityFaceCount[10];

		int[] priorityFaces = tmpPriorityFaces[10];
		int[] priorithFaceDepths = tmpPriority10FaceDepth;
		if (priorityFace == priorityFaceCount) {
			priorityFace = 0;
			priorityFaceCount = tmpPriorityFaceCount[11];
			priorityFaces = tmpPriorityFaces[11];
			priorithFaceDepths = tmpPriority11FaceDepth;
		}

		int priorityDepth;
		if (priorityFace < priorityFaceCount) {
			priorityDepth = priorithFaceDepths[priorityFace];
		} else {
			priorityDepth = -1000;
		}

		for ( int priority = 0; priority < 10; priority++) {
			while (priority == 0 && priorityDepth > averagePriorityDepthSum1_2) {
				this.drawFace(priorityFaces[priorityFace++]);

				if (priorityFace == priorityFaceCount && priorityFaces != tmpPriorityFaces[11]) {
					priorityFace = 0;
					priorityFaceCount = tmpPriorityFaceCount[11];
					priorityFaces = tmpPriorityFaces[11];
					priorithFaceDepths = tmpPriority11FaceDepth;
				}

				if (priorityFace < priorityFaceCount) {
					priorityDepth = priorithFaceDepths[priorityFace];
				} else {
					priorityDepth = -1000;
				}
			}

			while (priority == 3 && priorityDepth > averagePriorityDepthSum3_4) {
				this.drawFace(priorityFaces[priorityFace++]);

				if (priorityFace == priorityFaceCount && priorityFaces != tmpPriorityFaces[11]) {
					priorityFace = 0;
					priorityFaceCount = tmpPriorityFaceCount[11];
					priorityFaces = tmpPriorityFaces[11];
					priorithFaceDepths = tmpPriority11FaceDepth;
				}

				if (priorityFace < priorityFaceCount) {
					priorityDepth = priorithFaceDepths[priorityFace];
				} else {
					priorityDepth = -1000;
				}
			}

			while (priority == 5 && priorityDepth > averagePriorityDepthSum6_8) {
				this.drawFace(priorityFaces[priorityFace++]);

				if (priorityFace == priorityFaceCount && priorityFaces != tmpPriorityFaces[11]) {
					priorityFace = 0;
					priorityFaceCount = tmpPriorityFaceCount[11];
					priorityFaces = tmpPriorityFaces[11];
					priorithFaceDepths = tmpPriority11FaceDepth;
				}

				if (priorityFace < priorityFaceCount) {
					priorityDepth = priorithFaceDepths[priorityFace];
				} else {
					priorityDepth = -1000;
				}
			}

			int count = tmpPriorityFaceCount[priority];
			int[] faces = tmpPriorityFaces[priority];
			for ( int i = 0; i < count; i++) {
				this.drawFace(faces[i]);
			}
		}

		while (priorityDepth != -1000) {
			this.drawFace(priorityFaces[priorityFace++]);

			if (priorityFace == priorityFaceCount && priorityFaces != tmpPriorityFaces[11]) {
				priorityFace = 0;
				priorityFaces = tmpPriorityFaces[11];
				priorityFaceCount = tmpPriorityFaceCount[11];
				priorithFaceDepths = tmpPriority11FaceDepth;
			}

			if (priorityFace < priorityFaceCount) {
				priorityDepth = priorithFaceDepths[priorityFace];
			} else {
				priorityDepth = -1000;
			}
		}
	}

    private void drawFace( int face) {
		if (faceNearClipped[face]) {
			this.drawNearClippedFace(face);
			return;
		}

		int a = this.faceVertexA[face];
		int b = this.faceVertexB[face];
		int c = this.faceVertexC[face];

		Draw3D.clipX = faceClippedX[face];

		if (this.faceAlpha == null) {
			Draw3D.alpha = 0;
		} else {
			Draw3D.alpha = this.faceAlpha[face];
		}

		int type;
		if (this.faceInfo == null) {
			type = 0;
		} else {
			type = this.faceInfo[face] & 0x3;
		}

		if (type == 0) {
			Draw3D.fillGouraudTriangle(vertexScreenX[a], vertexScreenX[b], vertexScreenX[c], vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], this.faceColorA[face], this.faceColorB[face], this.faceColorC[face]);
		} else if (type == 1) {
			Draw3D.fillTriangle(vertexScreenX[a], vertexScreenX[b], vertexScreenX[c], vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], palette[this.faceColorA[face]]);
		} else if (type == 2) {
			int texturedFace = this.faceInfo[face] >> 2;
			int tA = this.texturedVertexA[texturedFace];
			int tB = this.texturedVertexB[texturedFace];
			int tC = this.texturedVertexC[texturedFace];
			Draw3D.fillTexturedTriangle(vertexScreenX[a], vertexScreenX[b], vertexScreenX[c], vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], this.faceColorA[face], this.faceColorB[face], this.faceColorC[face], vertexViewSpaceX[tA], vertexViewSpaceY[tA], vertexViewSpaceZ[tA], vertexViewSpaceX[tB], vertexViewSpaceX[tC], vertexViewSpaceY[tB], vertexViewSpaceY[tC], vertexViewSpaceZ[tB], vertexViewSpaceZ[tC], this.faceColor[face]);
		} else if (type == 3) {
			int texturedFace = this.faceInfo[face] >> 2;
			int tA = this.texturedVertexA[texturedFace];
			int tB = this.texturedVertexB[texturedFace];
			int tC = this.texturedVertexC[texturedFace];
			Draw3D.fillTexturedTriangle(vertexScreenX[a], vertexScreenX[b], vertexScreenX[c], vertexScreenY[a], vertexScreenY[b], vertexScreenY[c], this.faceColorA[face], this.faceColorA[face], this.faceColorA[face], vertexViewSpaceX[tA], vertexViewSpaceY[tA], vertexViewSpaceZ[tA], vertexViewSpaceX[tB], vertexViewSpaceX[tC], vertexViewSpaceY[tB], vertexViewSpaceY[tC], vertexViewSpaceZ[tB], vertexViewSpaceZ[tC], this.faceColor[face]);
		}
	}

    private void drawNearClippedFace( int face) {
		int centerX = Draw3D.centerX;
		int centerY = Draw3D.centerY;
		int elements = 0;

		int a = this.faceVertexA[face];
		int b = this.faceVertexB[face];
		int c = this.faceVertexC[face];

		int zA = vertexViewSpaceZ[a];
		int zB = vertexViewSpaceZ[b];
		int zC = vertexViewSpaceZ[c];

		if (zA >= 50) {
			clippedX[elements] = vertexScreenX[a];
			clippedY[elements] = vertexScreenY[a];
			clippedColor[elements++] = this.faceColorA[face];
		} else {
			int xA = vertexViewSpaceX[a];
			int yA = vertexViewSpaceY[a];
			int colorA = this.faceColorA[face];

			if (zC >= 50) {
				int scalar = (50 - zA) * reciprical16[zC - zA];
				clippedX[elements] = centerX + (xA + ((vertexViewSpaceX[c] - xA) * scalar >> 16) << 9) / 50;
				clippedY[elements] = centerY + (yA + ((vertexViewSpaceY[c] - yA) * scalar >> 16) << 9) / 50;
				clippedColor[elements++] = colorA + ((this.faceColorC[face] - colorA) * scalar >> 16);
			}

			if (zB >= 50) {
				int scalar = (50 - zA) * reciprical16[zB - zA];
				clippedX[elements] = centerX + (xA + ((vertexViewSpaceX[b] - xA) * scalar >> 16) << 9) / 50;
				clippedY[elements] = centerY + (yA + ((vertexViewSpaceY[b] - yA) * scalar >> 16) << 9) / 50;
				clippedColor[elements++] = colorA + ((this.faceColorB[face] - colorA) * scalar >> 16);
			}
		}

		if (zB >= 50) {
			clippedX[elements] = vertexScreenX[b];
			clippedY[elements] = vertexScreenY[b];
			clippedColor[elements++] = this.faceColorB[face];
		} else {
			int xB = vertexViewSpaceX[b];
			int yB = vertexViewSpaceY[b];
			int colorB = this.faceColorB[face];

			if (zA >= 50) {
				int scalar = (50 - zB) * reciprical16[zA - zB];
				clippedX[elements] = centerX + (xB + ((vertexViewSpaceX[a] - xB) * scalar >> 16) << 9) / 50;
				clippedY[elements] = centerY + (yB + ((vertexViewSpaceY[a] - yB) * scalar >> 16) << 9) / 50;
				clippedColor[elements++] = colorB + ((this.faceColorA[face] - colorB) * scalar >> 16);
			}

			if (zC >= 50) {
				int scalar = (50 - zB) * reciprical16[zC - zB];
				clippedX[elements] = centerX + (xB + ((vertexViewSpaceX[c] - xB) * scalar >> 16) << 9) / 50;
				clippedY[elements] = centerY + (yB + ((vertexViewSpaceY[c] - yB) * scalar >> 16) << 9) / 50;
				clippedColor[elements++] = colorB + ((this.faceColorC[face] - colorB) * scalar >> 16);
			}
		}

		if (zC >= 50) {
			clippedX[elements] = vertexScreenX[c];
			clippedY[elements] = vertexScreenY[c];
			clippedColor[elements++] = this.faceColorC[face];
		} else {
			int xC = vertexViewSpaceX[c];
			int yC = vertexViewSpaceY[c];
			int colorC = this.faceColorC[face];

			if (zB >= 50) {
				int scalar = (50 - zC) * reciprical16[zB - zC];
				clippedX[elements] = centerX + (xC + ((vertexViewSpaceX[b] - xC) * scalar >> 16) << 9) / 50;
				clippedY[elements] = centerY + (yC + ((vertexViewSpaceY[b] - yC) * scalar >> 16) << 9) / 50;
				clippedColor[elements++] = colorC + ((this.faceColorB[face] - colorC) * scalar >> 16);
			}

			if (zA >= 50) {
				int scalar = (50 - zC) * reciprical16[zA - zC];
				clippedX[elements] = centerX + (xC + ((vertexViewSpaceX[a] - xC) * scalar >> 16) << 9) / 50;
				clippedY[elements] = centerY + (yC + ((vertexViewSpaceY[a] - yC) * scalar >> 16) << 9) / 50;
				clippedColor[elements++] = colorC + ((this.faceColorA[face] - colorC) * scalar >> 16);
			}
		}

		int x0 = clippedX[0];
		int x1 = clippedX[1];
		int x2 = clippedX[2];
		int y0 = clippedY[0];
		int y1 = clippedY[1];
		int y2 = clippedY[2];

		if ((x0 - x1) * (y2 - y1) - (y0 - y1) * (x2 - x1) <= 0) {
			return;
		}

		Draw3D.clipX = false;

		if (elements == 3) {
			if (x0 < 0 || x1 < 0 || x2 < 0 || x0 > Draw2D.boundX || x1 > Draw2D.boundX || x2 > Draw2D.boundX) {
				Draw3D.clipX = true;
			}

			int type;
			if (this.faceInfo == null) {
				type = 0;
			} else {
				type = this.faceInfo[face] & 0x3;
			}

			if (type == 0) {
				Draw3D.fillGouraudTriangle(x0, x1, x2, y0, y1, y2, clippedColor[0], clippedColor[1], clippedColor[2]);
			} else if (type == 1) {
				Draw3D.fillTriangle(x0, x1, x2, y0, y1, y2, palette[this.faceColorA[face]]);
			} else if (type == 2) {
				int texturedFace = this.faceInfo[face] >> 2;
				int tA = this.texturedVertexA[texturedFace];
				int tB = this.texturedVertexB[texturedFace];
				int tC = this.texturedVertexC[texturedFace];
				Draw3D.fillTexturedTriangle(x0, x1, x2, y0, y1, y2, clippedColor[0], clippedColor[1], clippedColor[2], vertexViewSpaceX[tA], vertexViewSpaceY[tA], vertexViewSpaceZ[tA], vertexViewSpaceX[tB], vertexViewSpaceX[tC], vertexViewSpaceY[tB], vertexViewSpaceY[tC], vertexViewSpaceZ[tB], vertexViewSpaceZ[tC], this.faceColor[face]);
			} else if (type == 3) {
				int texturedFace = this.faceInfo[face] >> 2;
				int tA = this.texturedVertexA[texturedFace];
				int tB = this.texturedVertexB[texturedFace];
				int tC = this.texturedVertexC[texturedFace];
				Draw3D.fillTexturedTriangle(x0, x1, x2, y0, y1, y2, this.faceColorA[face], this.faceColorA[face], this.faceColorA[face], vertexViewSpaceX[tA], vertexViewSpaceY[tA], vertexViewSpaceZ[tA], vertexViewSpaceX[tB], vertexViewSpaceX[tC], vertexViewSpaceY[tB], vertexViewSpaceY[tC], vertexViewSpaceZ[tB], vertexViewSpaceZ[tC], this.faceColor[face]);
			}
		} else if (elements == 4) {
			if (x0 < 0 || x1 < 0 || x2 < 0 || x0 > Draw2D.boundX || x1 > Draw2D.boundX || x2 > Draw2D.boundX || clippedX[3] < 0 || clippedX[3] > Draw2D.boundX) {
				Draw3D.clipX = true;
			}

			int type;
			if (this.faceInfo == null) {
				type = 0;
			} else {
				type = this.faceInfo[face] & 0x3;
			}

			if (type == 0) {
				Draw3D.fillGouraudTriangle(x0, x1, x2, y0, y1, y2, clippedColor[0], clippedColor[1], clippedColor[2]);
				Draw3D.fillGouraudTriangle(x0, x2, clippedX[3], y0, y2, clippedY[3], clippedColor[0], clippedColor[2], clippedColor[3]);
			} else if (type == 1) {
				int colorA = palette[this.faceColorA[face]];
				Draw3D.fillTriangle(x0, x1, x2, y0, y1, y2, colorA);
				Draw3D.fillTriangle(x0, x2, clippedX[3], y0, y2, clippedY[3], colorA);
			} else if (type == 2) {
				int texturedFace = this.faceInfo[face] >> 2;
				int tA = this.texturedVertexA[texturedFace];
				int tB = this.texturedVertexB[texturedFace];
				int tC = this.texturedVertexC[texturedFace];
				Draw3D.fillTexturedTriangle(x0, x1, x2, y0, y1, y2, clippedColor[0], clippedColor[1], clippedColor[2], vertexViewSpaceX[tA], vertexViewSpaceY[tA], vertexViewSpaceZ[tA], vertexViewSpaceX[tB], vertexViewSpaceX[tC], vertexViewSpaceY[tB], vertexViewSpaceY[tC], vertexViewSpaceZ[tB], vertexViewSpaceZ[tC], this.faceColor[face]);
				Draw3D.fillTexturedTriangle(x0, x2, clippedX[3], y0, y2, clippedY[3], clippedColor[0], clippedColor[2], clippedColor[3], vertexViewSpaceX[tA], vertexViewSpaceY[tA], vertexViewSpaceZ[tA], vertexViewSpaceX[tB], vertexViewSpaceX[tC], vertexViewSpaceY[tB], vertexViewSpaceY[tC], vertexViewSpaceZ[tB], vertexViewSpaceZ[tC], this.faceColor[face]);
			} else if (type == 3) {
				int texturedFace = this.faceInfo[face] >> 2;
				int tA = this.texturedVertexA[texturedFace];
				int tB = this.texturedVertexB[texturedFace];
				int tC = this.texturedVertexC[texturedFace];
				Draw3D.fillTexturedTriangle(x0, x1, x2, y0, y1, y2, this.faceColorA[face], this.faceColorA[face], this.faceColorA[face], vertexViewSpaceX[tA], vertexViewSpaceY[tA], vertexViewSpaceZ[tA], vertexViewSpaceX[tB], vertexViewSpaceX[tC], vertexViewSpaceY[tB], vertexViewSpaceY[tC], vertexViewSpaceZ[tB], vertexViewSpaceZ[tC], this.faceColor[face]);
				Draw3D.fillTexturedTriangle(x0, x2, clippedX[3], y0, y2, clippedY[3], this.faceColorA[face], this.faceColorA[face], this.faceColorA[face], vertexViewSpaceX[tA], vertexViewSpaceY[tA], vertexViewSpaceZ[tA], vertexViewSpaceX[tB], vertexViewSpaceX[tC], vertexViewSpaceY[tB], vertexViewSpaceY[tC], vertexViewSpaceZ[tB], vertexViewSpaceZ[tC], this.faceColor[face]);
			}
		}
	}

    private boolean pointWithinTriangle( int x, int y, int yA, int yB, int yC, int xA, int xB, int xC) {
		if (y < yA && y < yB && y < yC) {
			return false;
		} else if (y > yA && y > yB && y > yC) {
			return false;
		} else if (x < xA && x < xB && x < xC) {
			return false;
		} else {
			return x <= xA || x <= xB || x <= xC;
		}
	}

	public static final class Metadata {

        public int vertexCount;

        public int faceCount;

        public int texturedFaceCount;

        public int vertexFlagsOffset;

        public int vertexXOffset;

        public int vertexYOffset;

        public int vertexZOffset;

        public int vertexLabelsOffset;

        public int faceVerticesOffset;

        public int faceOrientationsOffset;

        public int faceColorsOffset;

        public int faceInfosOffset;

        public int facePrioritiesOffset;

        public int faceAlphasOffset;

        public int faceLabelsOffset;

        public int faceTextureAxisOffset;
	}

	public static final class VertexNormal {

        public int x;

        public int y;

        public int z;

        public int w;
	}

}
