package jagex2.client;

import audio.MidiPlayer;
import audio.SoundPlayer;
import jagex2.config.*;
import jagex2.dash3d.CollisionMap;
import jagex2.dash3d.World;
import jagex2.dash3d.World3D;
import jagex2.dash3d.entity.*;
import jagex2.dash3d.type.LocSpawned;
import jagex2.dash3d.type.LocTemporary;
import jagex2.dash3d.type.Tile;
import jagex2.datastruct.JString;
import jagex2.datastruct.LinkList;
import jagex2.graphics.*;
import jagex2.io.*;
import jagex2.sound.Wave;
import jagex2.wordenc.WordFilter;
import jagex2.wordenc.WordPack;
import sign.signlink;

import javax.sound.midi.MidiChannel;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.zip.CRC32;

import static audio.MidiPlayer.isJingle;
import static jagex2.client.Configuration.*;

public class Client extends GameShell {

	public static boolean vanilla = true;

	public boolean showDebug = false;
	public boolean showPerformance = false;

	// alt+shift click to add a tile overlay
	public Tile[] userTileMarkers = new Tile[4];
	public int userTileMarkerIndex = 0;

	public static int opHeld1Counter;

	public static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\| ";

	private int midiSyncLen;

	private int cutsceneSrcLocalTileX;

	private int cutsceneSrcLocalTileZ;

	private int cutsceneSrcHeight;

	private int cutsceneMoveSpeed;

	private int cutsceneMoveAcceleration;

	private int localPid = -1;

	private int[] areaChatbackOffsets;

	private int[] areaSidebarOffsets;

	private int[] areaViewportOffsets;

	private int crossX;

	private int crossY;

	private int crossCycle;

	private int crossMode;

	private final int[] designColors = new int[5];

	private Packet login = Packet.alloc(1);

	private int nextMusicDelay;

	private int hintTileX;

	private int hintTileZ;

	private int hintHeight;

	private int hintOffsetX;

	private int hintOffsetZ;

	private int minimapOffsetCycle;

	private boolean redrawTitleBackground = false;

	private LinkList locList = new LinkList();

	private Isaac randomIn;

	private final boolean[] cameraModifierEnabled = new boolean[5];

	private int privateChatSetting;

	private int selectedTab = 3;

	private int[][] bfsCost = new int[104][104];

	private int socialAction;

	private int sceneBaseTileX;

	private int sceneBaseTileZ;

	private int mapLastBaseX;

	private int mapLastBaseZ;

	private String socialInput = "";

	private LinkList temporaryLocs = new LinkList();

	private final long[] ignoreName37 = new long[100];

	private int weightCarried;

	private byte[][] sceneMapLandData;

	public static int opLoc4Counter;

	private int[] friendWorld = new int[100];

	private int minimapLevel = -1;

	private String socialMessage = "";

	private Pix24[] imageHitmarks = new Pix24[20];

	private long lastWaveStartTime;

	private int packetSize;

	private int packetType;

	private int idleNetCycles;

	private int heartbeatTimer;

	private int idleTimeout;

	private String chatbackInput = "";

	private int cameraOffsetCycle;

	public static int lastWaveId = -1;

	private boolean updateDesignModel = false;

	private final int[] designIdentikits = new int[7];

	private Pix24[] activeMapFunctions = new Pix24[1000];

	private int chatScrollHeight = 78;

	private int ignoreCount;

	private int[][][] levelHeightmap;

	private Packet in = Packet.alloc(1);

	public static int opNpc5Counter;

	private Packet out = Packet.alloc(1);

	private boolean startMidiThread = false;

	private int chatEffects;

	private int hintNpc;

	private int overrideChat;

	public static int drawCounter;

	public static int opHeld4Counter;

	private final int[] skillLevel = new int[50];

	private final ComType chatInterface = new ComType();

	private final int[] waveLoops = new int[50];

	private int mouseButtonsOption;

	private final int[] archiveChecksum = new int[9];

	private boolean midiThreadActive = true;

	private Pix8[] imageSideicons = new Pix8[13];

	private int lastWaveLength;

	private int orbitCameraPitch = 128;

	private int orbitCameraYaw;

	private int orbitCameraYawVelocity;

	private int orbitCameraPitchVelocity;

	private final int MAX_PLAYER_COUNT = 2048;

	private final int LOCAL_PLAYER_INDEX = 2047;

	private PlayerEntity[] players = new PlayerEntity[this.MAX_PLAYER_COUNT];

	private int playerCount;

	private int[] playerIds = new int[this.MAX_PLAYER_COUNT];

	private int entityUpdateCount;

	private int[] entityUpdateIds = new int[this.MAX_PLAYER_COUNT];

	private Packet[] playerAppearanceBuffer = new Packet[this.MAX_PLAYER_COUNT];

	private int lastPacketType0;

	private int lastPacketType1;

	private int lastPacketType2;

	private World3D scene;

	private LinkList projectiles = new LinkList();

	private int splitPrivateChat;

	private String[] menuOption = new String[500];

	private boolean midiActive = true;

	private boolean designGenderMale = true;

	private int sceneCycle;

	private int sceneCenterZoneX;

	private int sceneCenterZoneZ;

	private byte[][][] levelTileFlags;

	private int[] flameBuffer0;

	private int[] flameBuffer1;

	private int objDragInterfaceId;

	private int objDragSlot;

	private int objDragArea;

	private int objGrabX;

	private int objGrabY;

	private final int[] flameLineOffset = new int[256];

	private PixMap areaBackbase1;

	private PixMap areaBackbase2;

	private PixMap areaBackhmid1;

	private int privateMessageCount;

	private final int[] compassMaskLineOffsets = new int[33];

	public static int opLoc5Counter;

	private final int[] waveDelay = new int[50];

	private int chatHoveredInterfaceIndex;

	private final int[] tabInterfaceId = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	private int baseX;

	private int baseZ;

	private static final int[] levelExperience = new int[99];

	private boolean errorLoading = false;

	public static int opNpc3Counter;

	private int lastHoveredInterfaceId;

	private boolean showSocialInput = false;

	private boolean pressedContinueOption = false;

	private int daysSinceLastLogin;

	private int flameGradientCycle0;

	private int flameGradientCycle1;

	public static int opHeld9Counter;

	private final int[] messageIds = new int[100];

	private boolean menuVisible = false;

	private int currentLevel;

	private boolean reportAbuseMuteOption = false;

	public static int sidebarInputCounter;

	private LinkList spawnedLocations = new LinkList();

	private int tradeChatSetting;

	public static int nodeId = 10;

	public static int portOffset;

	public static boolean members = true;

	public static boolean lowMemory;

	public static int opPlayer2Counter;

	private Pix8 imageRedstone1;

	private Pix8 imageRedstone2;

	private Pix8 imageRedstone3;

	private Pix8 imageRedstone1h;

	private Pix8 imageRedstone2h;

	private final int[] messageType = new int[100];

	private final String[] messageSender = new String[100];

	private final String[] messageText = new String[100];

	private long socialName37;

	private int daysSinceRecoveriesChanged;

	private boolean flameActive = false;

	private int[] flameGradient;

	private int[] flameGradient0;

	private int[] flameGradient1;

	private int[] flameGradient2;

	private int reportAbuseInterfaceID = -1;

	private Pix8 imageBackbase1;

	private Pix8 imageBackbase2;

	private Pix8 imageBackhmid1;

	private int hintType;

	public static int updatePlayersCounter;

	private int orbitCameraX;

	private int orbitCameraZ;

	private int cameraMovedWrite;

	private int activeMapFunctionCount;

	private int[] activeMapFunctionX = new int[1000];

	private int[] activeMapFunctionZ = new int[1000];

	private int[][] tileLastOccupiedCycle = new int[104][104];

	private boolean redrawPrivacySettings = false;

	public static final BigInteger RSA_EXPONENT = new BigInteger("58778699976184461502525193738213253649000149147835990136706041084440742975821");

	private boolean errorHost = false;

	private int objDragCycles;

	private int[] sceneMapIndex;

	private final int[] skillBaseLevel = new int[50];

	private NpcEntity[] npcs = new NpcEntity[8192];

	private int npcCount;

	private int[] npcIds = new int[8192];

	private int minimapZoom;

	private int minimapZoomModifier = 1;

	private int cameraPitchClamp;

	private int worldLocationState;

	private int dragCycles;

	private String modalMessage;

	public static int ifButton5Counter;

	private int[] varps = new int[2000];

	private int entityRemovalCount;

	private int[] entityRemovalIds = new int[1000];

	private int sidebarHoveredInterfaceIndex;

	private long[] friendName37 = new long[100];

	private int selectedCycle;

	private int selectedInterface;

	private int selectedItem;

	private int selectedArea;

	private int cutsceneDstLocalTileX;

	private int cutsceneDstLocalTileZ;

	private int cutsceneDstHeight;

	private int cutsceneRotateSpeed;

	private int cutsceneRotateAcceleration;

	private final int[] minimapMaskLineLengths = new int[151];

	private CollisionMap[] levelCollisionMap = new CollisionMap[4];

	public static int loopCycle;

	private Pix24[] imageHeadicons = new Pix24[20];

	private int systemUpdateTimer;

	private final int[] cameraModifierJitter = new int[5];

	private boolean objGrabThreshold = false;

	private Pix24 genderButtonImage0;

	private Pix24 genderButtonImage1;

	private int midiSyncCrc;

	private boolean redrawSidebar = false;

	private boolean redrawChatback = false;

	private final int[] cameraModifierWobbleScale = new int[5];

	private PlayerEntity localPlayer;

	private boolean cutscene = false;

	private int sceneDelta;

	private String reportAbuseInput = "";

	private int viewportInterfaceId = -1;

	private int titleLoginField;

	private Pix8[] imageRunes;

	private boolean ingame = false;

	private boolean flamesThread = false;

	private int publicChatSetting;

	private int chatScrollOffset;

	private Pix24 imageFlamesLeft;

	private Pix24 imageFlamesRight;

	private final int SCROLLBAR_GRIP_LOWLIGHT = 0x332d25;

	private Pix8 imageInvback;

	private Pix8 imageMapback;

	private Pix8 imageChatback;

	private int inMultizone;

	private PixFont fontPlain11;

	private PixFont fontPlain12;

	private PixFont fontBold12;

	private PixFont fontQuill8;

	private int tryMoveNearest;

	private int[] flameBuffer3;

	private int[] flameBuffer2;

	private final int SCROLLBAR_GRIP_HIGHLIGHT = 0x766654;

	private int[] bfsStepX = new int[4000];

	private int[] bfsStepZ = new int[4000];

	private final CRC32 crc32 = new CRC32();

	private Pix24 imageMapflag;

	public static int updateCounter;

	private ClientStream stream;

	private byte[][] sceneMapLocData;

	private int chatInterfaceId = -1;

	private int objSelected;

	private int objSelectedSlot;

	private int objSelectedInterface;

	private int objInterface;

	private String objSelectedName;

	private PixMap areaBackleft1;

	private PixMap areaBackleft2;

	private PixMap areaBackright1;

	private PixMap areaBackright2;

	private PixMap areaBacktop1;

	private PixMap areaBacktop2;

	private PixMap areaBackvmid1;

	private PixMap areaBackvmid2;

	private PixMap areaBackvmid3;

	private PixMap areaBackhmid2;

	private int waveCount;

	private int projectX = -1;

	private int projectY = -1;

	private int stickyChatInterfaceId = -1;

	private boolean rights = false;

	private final int[] cameraModifierCycle = new int[5];

	private int spellSelected;

	private int activeSpellId;

	private int activeSpellFlags;

	private String spellCaption;

	private PixMap imageTitle2;

	private PixMap imageTitle3;

	private PixMap imageTitle4;

	private PixMap imageTitle0;

	private PixMap imageTitle1;

	private PixMap imageTitle5;

	private PixMap imageTitle6;

	private PixMap imageTitle7;

	private PixMap imageTitle8;

	private Pix8[] imageMapscene = new Pix8[50];

	private Pix8 imageRedstone1v;

	private Pix8 imageRedstone2v;

	private Pix8 imageRedstone3v;

	private Pix8 imageRedstone1hv;

	private Pix8 imageRedstone2hv;

	private final int[] CHAT_COLORS = new int[] { 16776960, 16711680, 65280, 65535, 16711935, 16777215 };

	private PixMap areaSidebar;

	private PixMap areaMapback;

	private PixMap areaViewport;

	private PixMap areaChatback;

	private final int SCROLLBAR_TRACK = 0x23201b;

	private int flagSceneTileX;

	private int flagSceneTileZ;

	private Pix24 imageMinimap;

	private int unreadMessages;

	private boolean chatbackInputOpen = false;

	private LinkList spotanims = new LinkList();

	private Pix24 imageMapdot0;

	private Pix24 imageMapdot1;

	private Pix24 imageMapdot2;

	private Pix24 imageMapdot3;

	private int lastAddress;

	public static final BigInteger RSA_MODULUS = new BigInteger("7162900525229798032761816791230527296329313291232324290237849263501208207972894053929065636522363163621000728841182238772712427862772219676577293600221789");

	private int viewportHoveredInterfaceIndex;

	private String midiSyncName;

	private int lastWaveLoops = -1;

	private String username = "";

	private String password = "";

	private byte[] textureBuffer = new byte[16384];

	private boolean errorStarted = false;

	private int energy;

	private int menuSize;

	private final int[] varCache = new int[2000];

	private int hintPlayer;

	private int sceneState;

	private final int[] skillExperience = new int[50];

	private boolean redrawSideicons = false;

	private Pix8 imageScrollbar0;

	private Pix8 imageScrollbar1;

	private String loginMessage0 = "";

	private String loginMessage1 = "";

	private int minimapAnticheatAngle;

	private int minimapAngleModifier = 2;

	private int hoveredSlot;

	private int hoveredSlotParentId;

	private int friendCount;

	public static int update2Counter;

	private int chatCount;

	private final int MAX_CHATS = 50;

	private final int[] chatX = new int[this.MAX_CHATS];

	private final int[] chatY = new int[this.MAX_CHATS];

	private final int[] chatHeight = new int[this.MAX_CHATS];

	private final int[] chatWidth = new int[this.MAX_CHATS];

	private final int[] chatColors = new int[this.MAX_CHATS];

	private final int[] chatStyles = new int[this.MAX_CHATS];

	private final int[] chatTimers = new int[this.MAX_CHATS];

	private final String[] chats = new String[this.MAX_CHATS];

	private int wildernessLevel;

	public static boolean started;

	private Pix8 imageTitlebox;

	private Pix8 imageTitlebutton;

	private final int[] LOC_KIND_TO_CLASS_ID = new int[] { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };

	public static int updateLocCounter;

	private int titleScreenState;

	private int midiCrc;

	private int cameraX;

	private int cameraY;

	private int cameraZ;

	private int cameraPitch;

	private int cameraYaw;

	private final int[] compassMaskLineLengths = new int[33];

	private int[][] bfsDirection = new int[104][104];

	private String currentMidi;

	private Pix24[] imageCrosses = new Pix24[8];

	private boolean flameThread = false;

	private final Object midiSync = new Object();

	private final int[] waveIds = new int[50];

	private int cameraAnticheatOffsetX;

	private int cameraOffsetXModifier = 2;

	private String[] friendName = new String[100];

	private int flashingTab = -1;

	private int sidebarInterfaceId = -1;

	private int cameraAnticheatOffsetZ;

	private int cameraOffsetZModifier = 2;

	private final int[] minimapMaskLineOffsets = new int[151];

	private int cameraAnticheatAngle;

	private int cameraOffsetYawModifier = 1;

	private Jagfile archiveTitle;

	private String chatTyped = "";

	private Pix24[] imageMapfunction = new Pix24[50];

	private int[] menuParamB = new int[500];

	private int[] menuParamC = new int[500];

	private int[] menuAction = new int[500];

	private int[] menuParamA = new int[500];

	private boolean scrollGrabbed = false;

	private Pix24 imageCompass;

	private long serverSeed;

	private int menuArea;

	private int menuX;

	private int menuY;

	private int menuWidth;

	private int menuHeight;

	private boolean waveEnabled = true;

	private int scrollInputPadding;

	private int midiSize;

	private int flameCycle0;

	private LinkList[][][] levelObjStacks = new LinkList[4][104][104];

	private final int SCROLLBAR_GRIP_FOREGROUND = 0x4d4233;

	private final int[] cameraModifierWobbleSpeed = new int[5];

	static {
		int acc = 0;
		for ( int i = 0; i < 99; i++) {
			int level = i + 1;
			int delta = (int) ((double) level + Math.pow(2.0D, (double) level / 7.0D) * 300.0D);
			acc += delta;
			levelExperience[i] = acc / 4;
		}
	}

	public static void main( String[] args) {
		try {
			System.out.println("RS2 user jagex2.client.client - release #" + signlink.clientversion);

			if (args.length > 0) {
				nodeId = Integer.parseInt(args[0]);
			} else {
				nodeId = 10;
			}

			if (args.length > 1) {
				portOffset = Integer.parseInt(args[1]);
			} else {
				portOffset = Configuration.PORT_OFFSET;
			}

			if (args.length > 2 && args[2].equals("lowmem")) {
				setLowMemory();
			} else {
				setHighMemory();
			}

			members = args.length <= 3 || !args[3].equals("free");

			String vendor = System.getProperties().getProperty("java.vendor");
			if (vendor.toLowerCase().indexOf("sun") != -1 || vendor.toLowerCase().indexOf("apple") != -1) {
				signlink.sunjava = true;
			}

			signlink.startDaemon();

			Client c = new Client();
			c.initApplication(789, 531);
		} catch ( Exception _ex) {
		}
	}

	public static void setLowMemory() {
		World3D.lowMemory = true;
		Draw3D.lowMemory = true;
		lowMemory = true;
		World.lowMemory = true;
	}

	public static String formatObjCountTagged( int amount) {
		String s = String.valueOf(amount);
		for ( int i = s.length() - 3; i > 0; i -= 3) {
			s = s.substring(0, i) + "," + s.substring(i);
		}

		if (s.length() > 8) {
			s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@(" + s + ")";
		} else if (s.length() > 4) {
			s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
		}

		return " " + s;
	}

	public static String getCombatLevelColorTag( int viewerLevel, int otherLevel) {
		int diff = viewerLevel - otherLevel;
		if (diff < -9) {
			return "@red@";
		} else if (diff < -6) {
			return "@or3@";
		} else if (diff < -3) {
			return "@or2@";
		} else if (diff < 0) {
			return "@or1@";
		} else if (diff > 9) {
			return "@gre@";
		} else if (diff > 6) {
			return "@gr3@";
		} else if (diff > 3) {
			return "@gr2@";
		} else if (diff > 0) {
			return "@gr1@";
		} else {
			return "@yel@";
		}
	}

	public static void setHighMemory() {
		World3D.lowMemory = false;
		Draw3D.lowMemory = false;
		lowMemory = false;
		World.lowMemory = false;
	}

	public static String formatObjCount( int amount) {
		if (amount < 100000) {
			return String.valueOf(amount);
		} else if (amount < 10000000) {
			return amount / 1000 + "K";
		} else {
			return amount / 1000000 + "M";
		}
	}

	private void setMidi( String name, int crc, int len) {
		synchronized (this.midiSync) {
			this.midiSyncName = name;
			this.midiSyncCrc = crc;
			this.midiSyncLen = len;
		}
	}

	private void draw2DEntityElements() {
		this.chatCount = 0;

		for ( int index = -1; index < this.playerCount + this.npcCount; index++) {
			PathingEntity entity;
			if (index == -1) {
				entity = this.localPlayer;
			} else if (index < this.playerCount) {
				entity = this.players[this.playerIds[index]];
			} else {
				entity = this.npcs[this.npcIds[index - this.playerCount]];
			}

			if (entity == null || !entity.isVisible()) {
				continue;
			}

			if (this.showDebug) {
				// true tile overlay
				if (entity.pathLength > 0 || entity.forceMoveEndCycle >= loopCycle || entity.forceMoveStartCycle > loopCycle) {
					int halfUnit = 64 * entity.size;
					this.drawTileOverlay(entity.pathTileX[0] * 128 + halfUnit, entity.pathTileZ[0] * 128 + halfUnit, this.currentLevel, entity.size, 0x00FFFF, false);
				}

				// local tile overlay
				this.drawTileOverlay(entity.x, entity.z, this.currentLevel, entity.size, 0x666666, false);

				int offsetY = 0;
				this.projectFromGround(entity, entity.height + 30);

				if (index < this.playerCount) {
					// player debug
					PlayerEntity player = (PlayerEntity) entity;

					this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, player.name, 0xFFFFFF);
					offsetY -= 15;

					if (player.lastMask != -1 && loopCycle - player.lastMaskCycle < 30) {
						if ((player.lastMask & 0x1) == 0x1) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Appearance Update", 0xFFFFFF);
							offsetY -= 15;
						}

						if ((player.lastMask & 0x2) == 0x2) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Play Seq: " + player.primarySeqId, 0xFFFFFF);
							offsetY -= 15;
						}

						if ((player.lastMask & 0x4) == 0x4) {
							int target = player.targetId;
							if (target > 32767) {
								target -= 32768;
							}
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Face Entity: " + target, 0xFFFFFF);
							offsetY -= 15;
						}

						if ((player.lastMask & 0x8) == 0x8) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Say", 0xFFFFFF);
							offsetY -= 15;
						}

						if ((player.lastMask & 0x10) == 0x10) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Hit: Type " + player.damageType + " Amount " + player.damage + " HP " + player.health + "/" + player.totalHealth, 0xFFFFFF);
							offsetY -= 15;
						}

						if ((player.lastMask & 0x20) == 0x20) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Face Coord: " + (player.lastFaceX / 2) + " " + (player.lastFaceZ / 2), 0xFFFFFF);
							offsetY -= 15;
						}

						if ((player.lastMask & 0x40) == 0x40) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Chat", 0xFFFFFF);
							offsetY -= 15;
						}

						if ((player.lastMask & 0x100) == 0x100) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Play Spotanim: " + player.spotanimId, 0xFFFFFF);
							offsetY -= 15;
						}

						if ((player.lastMask & 0x200) == 0x200) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Exact Move", 0xFFFFFF);
							offsetY -= 15;
						}
					}
				} else {
					// npc debug
					NpcEntity npc = (NpcEntity) entity;

					this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, npc.type.name, 0xFFFFFF);
					offsetY -= 15;

					if (npc.lastMask != -1 && loopCycle - npc.lastMaskCycle < 30) {
						if ((npc.lastMask & 0x2) == 0x2) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Play Seq: " + npc.primarySeqId, 0xFFFFFF);
							offsetY -= 15;
						}

						if ((npc.lastMask & 0x4) == 0x4) {
							int target = npc.targetId;
							if (target > 32767) {
								target -= 32768;
							}
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Face Entity: " + target, 0xFFFFFF);
							offsetY -= 15;
						}

						if ((npc.lastMask & 0x8) == 0x8) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Say", 0xFFFFFF);
							offsetY -= 15;
						}

						if ((npc.lastMask & 0x10) == 0x10) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Hit: Type " + npc.damageType + " Amount " + npc.damage + " HP " + npc.health + "/" + npc.totalHealth, 0xFFFFFF);
							offsetY -= 15;
						}

						if ((npc.lastMask & 0x20) == 0x20) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Change Type: " + npc.type.index, 0xFFFFFF);
							offsetY -= 15;
						}

						if ((npc.lastMask & 0x40) == 0x40) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Play Spotanim: " + npc.spotanimId, 0xFFFFFF);
							offsetY -= 15;
						}

						if ((npc.lastMask & 0x80) == 0x80) {
							this.fontPlain11.drawStringCenter(this.projectX, this.projectY + offsetY, "Face Coord: " + (npc.lastFaceX / 2) + " " + (npc.lastFaceZ / 2), 0xFFFFFF);
							offsetY -= 15;
						}
					}
				}
			}

			if (index < this.playerCount) {
				int y = 30;

				PlayerEntity player = (PlayerEntity) entity;
				if (player.headicons != 0) {
					this.projectFromGround(entity, entity.height + 15);

					if (this.projectX > -1) {
						for (int icon = 0; icon < 8; icon++) {
							if ((player.headicons & 0x1 << icon) != 0) {
								this.imageHeadicons[icon].draw(this.projectX - 12, this.projectY - y);
								y -= 25;
							}
						}
					}
				}

				if (index >= 0 && this.hintType == 10 && this.hintPlayer == this.playerIds[index]) {
					this.projectFromGround(entity, entity.height + 15);
					if (this.projectX > -1) {
						this.imageHeadicons[7].draw(this.projectX - 12, this.projectY - y);
					}
				}
			} else if (this.hintType == 1 && this.hintNpc == this.npcIds[index - this.playerCount] && loopCycle % 20 < 10) {
				this.projectFromGround(entity, entity.height + 15);
				if (this.projectX > -1) {
					this.imageHeadicons[2].draw(this.projectX - 12, this.projectY - 28);
				}
			}

			if (entity.chat != null && (index >= this.playerCount || this.publicChatSetting == 0 || this.publicChatSetting == 3 || this.publicChatSetting == 1 && this.isFriend(((PlayerEntity) entity).name))) {
				this.projectFromGround(entity, entity.height);

				if (this.projectX > -1 && this.chatCount < this.MAX_CHATS) {
					this.chatWidth[this.chatCount] = this.fontBold12.stringWidth(entity.chat) / 2;
					this.chatHeight[this.chatCount] = this.fontBold12.height;
					this.chatX[this.chatCount] = this.projectX;
					this.chatY[this.chatCount] = this.projectY;

					this.chatColors[this.chatCount] = entity.chatColor;
					this.chatStyles[this.chatCount] = entity.chatStyle;
					this.chatTimers[this.chatCount] = entity.chatTimer;
					this.chats[this.chatCount++] = entity.chat;

					if (this.chatEffects == 0 && entity.chatStyle == 1) {
						this.chatHeight[this.chatCount] += 10;
						this.chatY[this.chatCount] += 5;
					}

					if (this.chatEffects == 0 && entity.chatStyle == 2) {
						this.chatWidth[this.chatCount] = 60;
					}
				}
			}

			if (entity.combatCycle > loopCycle + 100) {
				this.projectFromGround(entity, entity.height + 15);

				if (this.projectX > -1) {
					int w = entity.health * 30 / entity.totalHealth;
					if (w > 30) {
						w = 30;
					}
					Draw2D.fillRect(this.projectX - 15, this.projectY - 3, 0xff00, w, 5);
					Draw2D.fillRect(this.projectX - 15 + w, this.projectY - 3, 0xff0000, 30 - w, 5);
				}
			}

			if (entity.combatCycle > loopCycle + 330) {
				this.projectFromGround(entity, entity.height / 2);

				if (this.projectX > -1) {
					this.imageHitmarks[entity.damageType].draw(this.projectX - 12, this.projectY - 12);
					this.fontPlain11.drawStringCenter(this.projectX, this.projectY + 4, String.valueOf(entity.damage), 0);
					this.fontPlain11.drawStringCenter(this.projectX - 1, this.projectY + 3, String.valueOf(entity.damage), 0xffffff);
				}
			}
		}

		if (this.showDebug) {
			for (int i = 0; i < this.userTileMarkers.length; i++) {
				if (this.userTileMarkers[i] == null || this.userTileMarkers[i].level != this.currentLevel || this.userTileMarkers[i].x < 0 || this.userTileMarkers[i].z < 0 || this.userTileMarkers[i].x >= 104 || this.userTileMarkers[i].z >= 104) {
					continue;
				}

				this.drawTileOverlay(this.userTileMarkers[i].x * 128 + 64, this.userTileMarkers[i].z * 128 + 64, this.userTileMarkers[i].level, 1, 0xFFFF00, false);
			}
		}

		for ( int i = 0; i < this.chatCount; i++) {
			int x = this.chatX[i];
			int y = this.chatY[i];
			int padding = this.chatWidth[i];
			int height = this.chatHeight[i];
			boolean sorting = true;
			while (sorting) {
				sorting = false;
				for ( int j = 0; j < i; j++) {
					if (y + 2 > this.chatY[j] - this.chatHeight[j] && y - height < this.chatY[j] + 2 && x - padding < this.chatX[j] + this.chatWidth[j] && x + padding > this.chatX[j] - this.chatWidth[j] && this.chatY[j] - this.chatHeight[j] < y) {
						y = this.chatY[j] - this.chatHeight[j];
						sorting = true;
					}
				}
			}
			this.projectX = this.chatX[i];
			this.projectY = this.chatY[i] = y;
			String message = this.chats[i];
			if (this.chatEffects == 0) {
				int color = 0xffff00;
				if (this.chatColors[i] < 6) {
					color = this.CHAT_COLORS[this.chatColors[i]];
				}
				if (this.chatColors[i] == 6) {
					color = this.sceneCycle % 20 < 10 ? 0xff0000 : 0xffff00;
				}
				if (this.chatColors[i] == 7) {
					color = this.sceneCycle % 20 < 10 ? 0xff : 0xffff;
				}
				if (this.chatColors[i] == 8) {
					color = this.sceneCycle % 20 < 10 ? 0xb000 : 0x80ff80;
				}
				if (this.chatColors[i] == 9) {
					int delta = 150 - this.chatTimers[i];
					if (delta < 50) {
						color = delta * 1280 + 0xff0000;
					} else if (delta < 100) {
						color = 0xffff00 - (delta - 50) * 327680;
					} else if (delta < 150) {
						color = (delta - 100) * 5 + 65280;
					}
				}
				if (this.chatColors[i] == 10) {
					int delta = 150 - this.chatTimers[i];
					if (delta < 50) {
						color = delta * 5 + 0xff0000;
					} else if (delta < 100) {
						color = 0xff00ff - (delta - 50) * 327680;
					} else if (delta < 150) {
						color = (delta - 100) * 327680 + 255 - (delta - 100) * 5;
					}
				}
				if (this.chatColors[i] == 11) {
					int delta = 150 - this.chatTimers[i];
					if (delta < 50) {
						color = 16777215 - delta * 327685;
					} else if (delta < 100) {
						color = (delta - 50) * 327685 + 65280;
					} else if (delta < 150) {
						color = 16777215 - (delta - 100) * 327680;
					}
				}
				if (this.chatStyles[i] == 0) {
					this.fontBold12.drawStringCenter(this.projectX, this.projectY + 1, message, 0);
					this.fontBold12.drawStringCenter(this.projectX, this.projectY, message, color);
				}
				if (this.chatStyles[i] == 1) {
					this.fontBold12.drawCenteredWave(this.projectX, this.projectY + 1, message, 0, this.sceneCycle);
					this.fontBold12.drawCenteredWave(this.projectX, this.projectY, message, color, this.sceneCycle);
				}
				if (this.chatStyles[i] == 2) {
					int w = this.fontBold12.stringWidth(message);
					int offsetX = (150 - this.chatTimers[i]) * (w + 100) / 150;
					Draw2D.setBounds(334, this.projectX + 50, 0, this.projectX - 50);
					this.fontBold12.drawString(this.projectX + 50 - offsetX, this.projectY + 1, message, 0);
					this.fontBold12.drawString(this.projectX + 50 - offsetX, this.projectY, message, color);
					Draw2D.resetBounds();
				}
			} else {
				this.fontBold12.drawStringCenter(this.projectX, this.projectY + 1, message, 0);
				this.fontBold12.drawStringCenter(this.projectX, this.projectY, message, 16776960);
			}
		}
	}

	private void closeInterfaces() {
		// CLOSE_MODAL
		this.out.p1isaac(231);

		if (this.sidebarInterfaceId != -1) {
			this.sidebarInterfaceId = -1;
			this.redrawSidebar = true;
			this.pressedContinueOption = false;
			this.redrawSideicons = true;
		}

		if (this.chatInterfaceId != -1) {
			this.chatInterfaceId = -1;
			this.redrawChatback = true;
			this.pressedContinueOption = false;
		}

		this.viewportInterfaceId = -1;
	}

	private void stopMidi() {
		signlink.midifade = 0;
		signlink.midi = "stop";
	}

	private void drawWildyLevel() {
		int x = (this.localPlayer.x >> 7) + this.sceneBaseTileX;
		int z = (this.localPlayer.z >> 7) + this.sceneBaseTileZ;

		if (x >= 2944 && x < 3392 && z >= 3520 && z < 6400) {
			this.wildernessLevel = (z - 3520) / 8 + 1;
		} else if (x >= 2944 && x < 3392 && z >= 9920 && z < 12800) {
			this.wildernessLevel = (z - 9920) / 8 + 1;
		} else {
			this.wildernessLevel = 0;
		}

		this.worldLocationState = 0;
		if (x >= 3328 && x < 3392 && z >= 3200 && z < 3264) {
			int localX = x & 63;
			int localZ = z & 63;

			if (localX >= 4 && localX <= 29 && localZ >= 44 && localZ <= 58) {
				this.worldLocationState = 1;
			} else if (localX >= 36 && localX <= 61 && localZ >= 44 && localZ <= 58) {
				this.worldLocationState = 1;
			} else if (localX >= 4 && localX <= 29 && localZ >= 25 && localZ <= 39) {
				this.worldLocationState = 1;
			} else if (localX >= 36 && localX <= 61 && localZ >= 25 && localZ <= 39) {
				this.worldLocationState = 1;
			} else if (localX >= 4 && localX <= 29 && localZ >= 6 && localZ <= 20) {
				this.worldLocationState = 1;
			} else if (localX >= 36 && localX <= 61 && localZ >= 6 && localZ <= 20) {
				this.worldLocationState = 1;
			}
		}

		if (this.worldLocationState == 0 && x >= 3328 && x <= 3393 && z >= 3203 && z <= 3325) {
			this.worldLocationState = 2;
		}

		this.overrideChat = 0;
		if (x >= 3053 && x <= 3156 && z >= 3056 && z <= 3136) {
			this.overrideChat = 1;
		} else if (x >= 3072 && x <= 3118 && z >= 9492 && z <= 9535) {
			this.overrideChat = 1;
		}

		if (this.overrideChat == 1 && x >= 3139 && x <= 3199 && z >= 3008 && z <= 3062) {
			this.overrideChat = 0;
		}
	}

	private void drawPrivateMessages() {
		if (this.splitPrivateChat == 0) {
			return;
		}

		PixFont font = this.fontPlain12;
		int lineOffset = 0;
		if (this.systemUpdateTimer != 0) {
			lineOffset = 1;
		}

		for ( int i = 0; i < 100; i++) {
			if (this.messageText[i] == null) {
				continue;
			}

			int type = this.messageType[i];
			int y;
			if ((type == 3 || type == 7) && (type == 7 || this.privateChatSetting == 0 || this.privateChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
				y = 329 - lineOffset * 13;
				font.drawString(4, y, "From " + this.messageSender[i] + ": " + this.messageText[i], 0);
				font.drawString(4, y - 1, "From " + this.messageSender[i] + ": " + this.messageText[i], 65535);

				lineOffset++;
				if (lineOffset >= 5) {
					return;
				}
			}

			if (type == 5 && this.privateChatSetting < 2) {
				y = 329 - lineOffset * 13;
				font.drawString(4, y, this.messageText[i], 0);
				font.drawString(4, y - 1, this.messageText[i], 65535);

				lineOffset++;
				if (lineOffset >= 5) {
					return;
				}
			}

			if (type == 6 && this.privateChatSetting < 2) {
				y = 329 - lineOffset * 13;
				font.drawString(4, y, "To " + this.messageSender[i] + ": " + this.messageText[i], 0);
				font.drawString(4, y - 1, "To " + this.messageSender[i] + ": " + this.messageText[i], 65535);

				lineOffset++;
				if (lineOffset >= 5) {
					return;
				}
			}
		}
	}

	private void readNpcUpdates( Packet buf, int size) {
		for ( int i = 0; i < this.entityUpdateCount; i++) {
			int id = this.entityUpdateIds[i];
			NpcEntity npc = this.npcs[id];
			int mask = buf.g1();

			npc.lastMask = mask;
			npc.lastMaskCycle = loopCycle;

			if ((mask & 0x2) == 2) {
				int seqId = buf.g2();
				if (seqId == 65535) {
					seqId = -1;
				}
				if (seqId == npc.primarySeqId) {
					npc.primarySeqLoop = 0;
				}
				int delay = buf.g1();
				if (seqId == -1 || npc.primarySeqId == -1 || SeqType.instances[seqId].priority > SeqType.instances[npc.primarySeqId].priority || SeqType.instances[npc.primarySeqId].priority == 0) {
					npc.primarySeqId = seqId;
					npc.primarySeqFrame = 0;
					npc.primarySeqCycle = 0;
					npc.primarySeqDelay = delay;
					npc.primarySeqLoop = 0;
				}
			}
			if ((mask & 0x4) == 4) {
				npc.targetId = buf.g2();
				if (npc.targetId == 65535) {
					npc.targetId = -1;
				}
			}
			if ((mask & 0x8) == 8) {
				npc.chat = buf.gjstr();
				npc.chatTimer = 100;
			}
			if ((mask & 0x10) == 16) {
				npc.damage = buf.g1();
				npc.damageType = buf.g1();
				npc.combatCycle = loopCycle + 400;
				npc.health = buf.g1();
				npc.totalHealth = buf.g1();
			}
			if ((mask & 0x20) == 32) {
				npc.type = NpcType.get(buf.g2());
				npc.seqWalkId = npc.type.walkanim;
				npc.seqTurnAroundId = npc.type.walkanim_b;
				npc.seqTurnLeftId = npc.type.walkanim_r;
				npc.seqTurnRightId = npc.type.walkanim_l;
				npc.seqStandId = npc.type.readyanim;
			}
			if ((mask & 0x40) == 64) {
				npc.spotanimId = buf.g2();
				int info = buf.g4();
				npc.spotanimOffset = info >> 16;
				npc.spotanimLastCycle = loopCycle + (info & 0xFFFF);
				npc.spotanimFrame = 0;
				npc.spotanimCycle = 0;
				if (npc.spotanimLastCycle > loopCycle) {
					npc.spotanimFrame = -1;
				}
				if (npc.spotanimId == 65535) {
					npc.spotanimId = -1;
				}
			}
			if ((mask & 0x80) == 128) {
				npc.targetTileX = buf.g2();
				npc.targetTileZ = buf.g2();
				npc.lastFaceX = npc.targetTileX;
				npc.lastFaceZ = npc.targetTileZ;
			}
		}
	}

	private void addIgnore( long username) {
		if (username == 0L) {
			return;
		}

		if (this.ignoreCount >= 100) {
			this.addMessage(0, "Your ignore list is full. Max of 100 hit", "");
			return;
		}

		String displayName = JString.formatName(JString.fromBase37(username));
		for ( int i = 0; i < this.ignoreCount; i++) {
			if (this.ignoreName37[i] == username) {
				this.addMessage(0, displayName + " is already on your ignore list", "");
				return;
			}

		}

		for ( int i = 0; i < this.friendCount; i++) {
			if (this.friendName37[i] == username) {
				this.addMessage(0, "Please remove " + displayName + " from your friend list first", "");
				return;
			}
		}

		this.ignoreName37[this.ignoreCount++] = username;
		this.redrawSidebar = true;
		// IGNORELIST_ADD
		this.out.p1isaac(79);
		this.out.p8(username);
	}

	private void readZonePacket( Packet buf, int opcode) {
		int pos = buf.g1();
		int x = this.baseX + (pos >> 4 & 0x7);
		int z = this.baseZ + (pos & 0x7);

		if (opcode == 59 || opcode == 76) {
			// LOC_ADD_CHANGE || LOC_DEL
			int info = buf.g1();
			int shape = info >> 2;
			int angle = info & 0x3;
			int layer = this.LOC_KIND_TO_CLASS_ID[shape];
			int id;
			if (opcode == 76) {
				id = -1;
			} else {
				id = buf.g2();
			}
			if (x >= 0 && z >= 0 && x < 104 && z < 104) {
				LocTemporary loc = null;
				for ( LocTemporary next = (LocTemporary) this.spawnedLocations.peekFront(); next != null; next = (LocTemporary) this.spawnedLocations.prev()) {
					if (next.plane == this.currentLevel && next.x == x && next.z == z && next.layer == layer) {
						loc = next;
						break;
					}
				}
				if (loc == null) {
					int bitset = 0;
					int otherId = -1;
					int otherShape = 0;
					int otherAngle = 0;
					if (layer == 0) {
						bitset = this.scene.getWallBitset(this.currentLevel, x, z);
					}
					if (layer == 1) {
						bitset = this.scene.getWallDecorationBitset(this.currentLevel, z, x);
					}
					if (layer == 2) {
						bitset = this.scene.getLocBitset(this.currentLevel, x, z);
					}
					if (layer == 3) {
						bitset = this.scene.getGroundDecorationBitset(this.currentLevel, x, z);
					}
					if (bitset != 0) {
						int otherInfo = this.scene.getInfo(this.currentLevel, x, z, bitset);
						otherId = bitset >> 14 & 0x7FFF;
						otherShape = otherInfo & 0x1F;
						otherAngle = otherInfo >> 6;
					}
					loc = new LocTemporary();
					loc.plane = this.currentLevel;
					loc.layer = layer;
					loc.x = x;
					loc.z = z;
					loc.lastLocIndex = otherId;
					loc.lastShape = otherShape;
					loc.lastAngle = otherAngle;
					this.spawnedLocations.pushBack(loc);
				}
				loc.locIndex = id;
				loc.shape = shape;
				loc.angle = angle;
				this.addLoc(this.currentLevel, x, z, id, angle, shape, layer);
			}
		} else if (opcode == 42) {
			// LOC_ANIM
			int info = buf.g1();
			int shape = info >> 2;
			int layer = this.LOC_KIND_TO_CLASS_ID[shape];
			int id = buf.g2();
			if (x >= 0 && z >= 0 && x < 104 && z < 104) {
				int bitset = 0;
				if (layer == 0) {
					bitset = this.scene.getWallBitset(this.currentLevel, x, z);
				}
				if (layer == 1) {
					bitset = this.scene.getWallDecorationBitset(this.currentLevel, z, x);
				}
				if (layer == 2) {
					bitset = this.scene.getLocBitset(this.currentLevel, x, z);
				}
				if (layer == 3) {
					bitset = this.scene.getGroundDecorationBitset(this.currentLevel, x, z);
				}
				if (bitset != 0) {
					LocEntity loc = new LocEntity(bitset >> 14 & 0x7FFF, this.currentLevel, layer, x, z, SeqType.instances[id], false);
					this.locList.pushBack(loc);
				}
			}
		} else if (opcode == 223) {
			// OBJ_ADD
			int id = buf.g2();
			int count = buf.g2();
			if (x >= 0 && z >= 0 && x < 104 && z < 104) {
				ObjStackEntity obj = new ObjStackEntity();
				obj.index = id;
				obj.count = count;
				if (this.levelObjStacks[this.currentLevel][x][z] == null) {
					this.levelObjStacks[this.currentLevel][x][z] = new LinkList();
				}
				this.levelObjStacks[this.currentLevel][x][z].pushBack(obj);
				this.sortObjStacks(x, z);
			}
		} else if (opcode == 49) {
			// OBJ_DEL
			int id = buf.g2();
			if (x >= 0 && z >= 0 && x < 104 && z < 104) {
				LinkList list = this.levelObjStacks[this.currentLevel][x][z];
				if (list != null) {
					for (ObjStackEntity next = (ObjStackEntity) list.peekFront(); next != null; next = (ObjStackEntity) list.prev()) {
						if (next.index == (id & 0x7FFF)) {
							next.unlink();
							break;
						}
					}
					if (list.peekFront() == null) {
						this.levelObjStacks[this.currentLevel][x][z] = null;
					}
					this.sortObjStacks(x, z);
				}
			}
		} else if (opcode == 69) {
			// MAP_PROJANIM
			int dx = x + buf.g1b();
			int dz = z + buf.g1b();
			int target = buf.g2b();
			int spotanim = buf.g2();
			int srcHeight = buf.g1();
			int dstHeight = buf.g1();
			int startDelay = buf.g2();
			int endDelay = buf.g2();
			int peak = buf.g1();
			int arc = buf.g1();
			if (x >= 0 && z >= 0 && x < 104 && z < 104 && dx >= 0 && dz >= 0 && dx < 104 && dz < 104) {
				x = x * 128 + 64;
				z = z * 128 + 64;
				dx = dx * 128 + 64;
				dz = dz * 128 + 64;
				ProjectileEntity proj = new ProjectileEntity(spotanim, this.currentLevel, x, this.getHeightmapY(this.currentLevel, x, z) - srcHeight, z, startDelay + loopCycle, endDelay + loopCycle, peak, arc, target, dstHeight);
				proj.updateVelocity(dx, this.getHeightmapY(this.currentLevel, dx, dz) - dstHeight, dz, startDelay + loopCycle);
				this.projectiles.pushBack(proj);
			}
		} else if (opcode == 191) {
			// MAP_ANIM
			int id = buf.g2();
			int height = buf.g1();
			int delay = buf.g2();
			if (x >= 0 && z >= 0 && x < 104 && z < 104) {
				x = x * 128 + 64;
				z = z * 128 + 64;
				SpotAnimEntity spotanim = new SpotAnimEntity(id, this.currentLevel, x, z, this.getHeightmapY(this.currentLevel, x, z) - height, loopCycle, delay);
				this.spotanims.pushBack(spotanim);
			}
		} else if (opcode == 50) {
			// OBJ_REVEAL
			int id = buf.g2();
			int count = buf.g2();
			int receiver = buf.g2();
			if (x >= 0 && z >= 0 && x < 104 && z < 104 && receiver != this.localPid) {
				ObjStackEntity obj = new ObjStackEntity();
				obj.index = id;
				obj.count = count;
				if (this.levelObjStacks[this.currentLevel][x][z] == null) {
					this.levelObjStacks[this.currentLevel][x][z] = new LinkList();
				}
				this.levelObjStacks[this.currentLevel][x][z].pushBack(obj);
				this.sortObjStacks(x, z);
			}
		} else if (opcode == 23) {
			// LOC_MERGE
			int info = buf.g1();
			int shape = info >> 2;
			int angle = info & 0x3;
			int layer = this.LOC_KIND_TO_CLASS_ID[shape];
			int id = buf.g2();
			int start = buf.g2();
			int end = buf.g2();
			int pid = buf.g2();
			byte east = buf.g1b();
			byte south = buf.g1b();
			byte west = buf.g1b();
			byte north = buf.g1b();

			PlayerEntity player;
			if (pid == this.localPid) {
				player = this.localPlayer;
			} else {
				player = this.players[pid];
			}

			if (player != null) {
				LocSpawned loc1 = new LocSpawned(this.currentLevel, layer, x, z, -1, angle, shape, start + loopCycle);
				this.temporaryLocs.pushBack(loc1);

				LocSpawned loc2 = new LocSpawned(this.currentLevel, layer, x, z, id, angle, shape, end + loopCycle);
				this.temporaryLocs.pushBack(loc2);

				int y0 = this.levelHeightmap[this.currentLevel][x][z];
				int y1 = this.levelHeightmap[this.currentLevel][x + 1][z];
				int y2 = this.levelHeightmap[this.currentLevel][x + 1][z + 1];
				int y3 = this.levelHeightmap[this.currentLevel][x][z + 1];
				LocType loc = LocType.get(id);

				player.locStartCycle = start + loopCycle;
				player.locStopCycle = end + loopCycle;
				player.locModel = loc.getModel(shape, angle, y0, y1, y2, y3, -1);

				int width = loc.width;
				int height = loc.length;
				if (angle == 1 || angle == 3) {
					width = loc.length;
					height = loc.width;
				}

				player.locOffsetX = x * 128 + width * 64;
				player.locOffsetZ = z * 128 + height * 64;
				player.locOffsetY = this.getHeightmapY(this.currentLevel, player.locOffsetX, player.locOffsetZ);

				byte tmp;
				if (east > west) {
					tmp = east;
					east = west;
					west = tmp;
				}

				if (south > north) {
					tmp = south;
					south = north;
					north = tmp;
				}

				player.minTileX = x + east;
				player.maxTileX = x + west;
				player.minTileZ = z + south;
				player.maxTileZ = z + north;
			}
		} else if (opcode == 151) {
			// OBJ_COUNT
			int id = buf.g2();
			int oldCount = buf.g2();
			int newCount = buf.g2();
			if (x >= 0 && z >= 0 && x < 104 && z < 104) {
				LinkList list = this.levelObjStacks[this.currentLevel][x][z];
				if (list != null) {
					for ( ObjStackEntity next = (ObjStackEntity) list.peekFront(); next != null; next = (ObjStackEntity) list.prev()) {
						if (next.index == (id & 0x7FFF) && next.count == oldCount) {
							next.count = newCount;
							break;
						}
					}
					this.sortObjStacks(x, z);
				}
			}
		}
	}

	private int getTopLevel() {
		int top = 3;
		if (this.cameraPitch < 310) {
			int cameraLocalTileX = this.cameraX >> 7;
			int cameraLocalTileZ = this.cameraZ >> 7;
			int playerLocalTileX = this.localPlayer.x >> 7;
			int playerLocalTileZ = this.localPlayer.z >> 7;
			if ((this.levelTileFlags[this.currentLevel][cameraLocalTileX][cameraLocalTileZ] & 0x4) != 0) {
				top = this.currentLevel;
			}
			int tileDeltaX;
			if (playerLocalTileX > cameraLocalTileX) {
				tileDeltaX = playerLocalTileX - cameraLocalTileX;
			} else {
				tileDeltaX = cameraLocalTileX - playerLocalTileX;
			}
			int tileDeltaZ;
			if (playerLocalTileZ > cameraLocalTileZ) {
				tileDeltaZ = playerLocalTileZ - cameraLocalTileZ;
			} else {
				tileDeltaZ = cameraLocalTileZ - playerLocalTileZ;
			}
			int delta;
			int accumulator;
			if (tileDeltaX > tileDeltaZ) {
				delta = tileDeltaZ * 65536 / tileDeltaX;
				accumulator = 32768;
				while (cameraLocalTileX != playerLocalTileX) {
					if (cameraLocalTileX < playerLocalTileX) {
						cameraLocalTileX++;
					} else if (cameraLocalTileX > playerLocalTileX) {
						cameraLocalTileX--;
					}
					if ((this.levelTileFlags[this.currentLevel][cameraLocalTileX][cameraLocalTileZ] & 0x4) != 0) {
						top = this.currentLevel;
					}
					accumulator += delta;
					if (accumulator >= 65536) {
						accumulator -= 65536;
						if (cameraLocalTileZ < playerLocalTileZ) {
							cameraLocalTileZ++;
						} else if (cameraLocalTileZ > playerLocalTileZ) {
							cameraLocalTileZ--;
						}
						if ((this.levelTileFlags[this.currentLevel][cameraLocalTileX][cameraLocalTileZ] & 0x4) != 0) {
							top = this.currentLevel;
						}
					}
				}
			} else {
				delta = tileDeltaX * 65536 / tileDeltaZ;
				accumulator = 32768;
				while (cameraLocalTileZ != playerLocalTileZ) {
					if (cameraLocalTileZ < playerLocalTileZ) {
						cameraLocalTileZ++;
					} else if (cameraLocalTileZ > playerLocalTileZ) {
						cameraLocalTileZ--;
					}
					if ((this.levelTileFlags[this.currentLevel][cameraLocalTileX][cameraLocalTileZ] & 0x4) != 0) {
						top = this.currentLevel;
					}
					accumulator += delta;
					if (accumulator >= 65536) {
						accumulator -= 65536;
						if (cameraLocalTileX < playerLocalTileX) {
							cameraLocalTileX++;
						} else if (cameraLocalTileX > playerLocalTileX) {
							cameraLocalTileX--;
						}
						if ((this.levelTileFlags[this.currentLevel][cameraLocalTileX][cameraLocalTileZ] & 0x4) != 0) {
							top = this.currentLevel;
						}
					}
				}
			}
		}
		if ((this.levelTileFlags[this.currentLevel][this.localPlayer.x >> 7][this.localPlayer.z >> 7] & 0x4) != 0) {
			top = this.currentLevel;
		}
		return top;
	}

	private int getTopLevelCutscene() {
		int y = this.getHeightmapY(this.currentLevel, this.cameraX, this.cameraZ);
		return y - this.cameraY >= 800 || (this.levelTileFlags[this.currentLevel][this.cameraX >> 7][this.cameraZ >> 7] & 0x4) == 0 ? 3 : this.currentLevel;
	}

	private void drawScene() {
		this.sceneCycle++;
		this.pushPlayers();
		this.pushNpcs();
		this.pushProjectiles();
		this.pushSpotanims();
		this.pushLocs();

		if (!this.cutscene) {
			int pitch = this.orbitCameraPitch;

			if (this.cameraPitchClamp / 256 > pitch) {
				pitch = this.cameraPitchClamp / 256;
			}

			if (this.cameraModifierEnabled[4] && this.cameraModifierWobbleScale[4] + 128 > pitch) {
				pitch = this.cameraModifierWobbleScale[4] + 128;
			}

			int yaw = this.orbitCameraYaw + this.cameraAnticheatAngle & 0x7FF;
			this.orbitCamera(this.orbitCameraX, this.getHeightmapY(this.currentLevel, this.localPlayer.x, this.localPlayer.z) - 50, this.orbitCameraZ, yaw, pitch, pitch * 3 + 600);

			drawCounter++;
			if (drawCounter > 1802) {
				drawCounter = 0;
				// ANTICHEAT_CYCLELOGIC2
				this.out.p1isaac(146);
				this.out.p1(0);
				int start = this.out.pos;
				this.out.p2(29711);
				this.out.p1(70);
				this.out.p1((int) (Math.random() * 256.0D));
				this.out.p1(242);
				this.out.p1(186);
				this.out.p1(39);
				this.out.p1(61);
				if ((int) (Math.random() * 2.0D) == 0) {
					this.out.p1(13);
				}
				if ((int) (Math.random() * 2.0D) == 0) {
					this.out.p2(57856);
				}
				this.out.p2((int) (Math.random() * 65536.0D));
				this.out.psize1(this.out.pos - start);
			}
		}

		int level;
		if (this.cutscene) {
			level = this.getTopLevelCutscene();
		} else {
			level = this.getTopLevel();
		}

		int cameraX = this.cameraX;
		int cameraY = this.cameraY;
		int cameraZ = this.cameraZ;
		int cameraPitch = this.cameraPitch;
		int cameraYaw = this.cameraYaw;
		int jitter;
		for ( int type = 0; type < 5; type++) {
			if (this.cameraModifierEnabled[type]) {
				jitter = (int) (Math.random() * (double) (this.cameraModifierJitter[type] * 2 + 1) - (double) this.cameraModifierJitter[type] + Math.sin((double) this.cameraModifierCycle[type] * ((double) this.cameraModifierWobbleSpeed[type] / 100.0D)) * (double) this.cameraModifierWobbleScale[type]);
				if (type == 0) {
					this.cameraX += jitter;
				}
				if (type == 1) {
					this.cameraY += jitter;
				}
				if (type == 2) {
					this.cameraZ += jitter;
				}
				if (type == 3) {
					this.cameraYaw = this.cameraYaw + jitter & 0x7FF;
				}
				if (type == 4) {
					this.cameraPitch += jitter;
					if (this.cameraPitch < 128) {
						this.cameraPitch = 128;
					}
					if (this.cameraPitch > 383) {
						this.cameraPitch = 383;
					}
				}
			}
		}
		jitter = Draw3D.cycle;
		Model.checkHover = true;
		Model.pickedCount = 0;
		Model.mouseX = super.mouseX - 8;
		Model.mouseZ = super.mouseY - 11;
		Draw2D.clear();
		this.scene.draw(this.cameraX, this.cameraY, this.cameraZ, level, this.cameraYaw, this.cameraPitch, loopCycle);
		this.scene.clearTemporaryLocs();
		this.draw2DEntityElements();
		this.drawTileHint();
		this.updateTextures(jitter);
		this.draw3DEntityElements();
		this.areaViewport.draw(super.graphics, 8, 11);
		this.cameraX = cameraX;
		this.cameraY = cameraY;
		this.cameraZ = cameraZ;
		this.cameraPitch = cameraPitch;
		this.cameraYaw = cameraYaw;
	}

	private void runMidi() {
		this.startMidiThread = false;
		while (this.midiThreadActive) {
			try {
				Thread.sleep(50L);
			} catch ( Exception ex) {
			}

			String name;
			int crc;
			int len;
			synchronized (this.midiSync) {
				name = this.midiSyncName;
				crc = this.midiSyncCrc;
				len = this.midiSyncLen;
				this.midiSyncName = null;
				this.midiSyncCrc = 0;
				this.midiSyncLen = 0;
			}

			if (name != null) {
				byte[] data = signlink.cacheload(name + ".mid");
				int dataCrc;
				if (data != null && crc != 12345678) {
					this.crc32.reset();
					this.crc32.update(data);
					dataCrc = (int) this.crc32.getValue();
					if (dataCrc != crc) {
						data = null;
					}
				}

				if (data == null) {
					try {
						DataInputStream stream = this.openUrl(name + "_" + crc + ".mid");
						data = new byte[len];
						int read;
						for ( int off = 0; off < len; off += read) {
							read = stream.read(data, off, len - off);
							if (read == -1) {
								byte[] tmp = new byte[off];
								System.arraycopy(data, 0, tmp, 0, off);
								data = tmp;
								len = off;
								break;
							}
						}
						stream.close();
						signlink.cachesave(name + ".mid", data);
					} catch ( Exception ex) {
					}
				}
				if (data == null) {
					return;
				}
				int uncompressedLength = (new Packet(data)).g4();
				byte[] uncompressed = new byte[uncompressedLength];
				BZip2.read(uncompressed, uncompressedLength, data, len, 4);
				this.saveMidi(uncompressed, uncompressedLength, 1);
			}
		}
	}

	private void drawFlames() {
		short height = 256;

		if (this.flameGradientCycle0 > 0) {
			for (int i = 0; i < 256; i++) {
				if (this.flameGradientCycle0 > 768) {
					this.flameGradient[i] = this.mix(this.flameGradient0[i], 1024 - this.flameGradientCycle0, this.flameGradient1[i]);
				} else if (this.flameGradientCycle0 > 256) {
					this.flameGradient[i] = this.flameGradient1[i];
				} else {
					this.flameGradient[i] = this.mix(this.flameGradient1[i], 256 - this.flameGradientCycle0, this.flameGradient0[i]);
				}
			}
		} else if (this.flameGradientCycle1 > 0) {
			for (int i = 0; i < 256; i++) {
				if (this.flameGradientCycle1 > 768) {
					this.flameGradient[i] = this.mix(this.flameGradient0[i], 1024 - this.flameGradientCycle1, this.flameGradient2[i]);
				} else if (this.flameGradientCycle1 > 256) {
					this.flameGradient[i] = this.flameGradient2[i];
				} else {
					this.flameGradient[i] = this.mix(this.flameGradient2[i], 256 - this.flameGradientCycle1, this.flameGradient0[i]);
				}
			}
		} else {
			System.arraycopy(this.flameGradient0, 0, this.flameGradient, 0, 256);
		}
		System.arraycopy(this.imageFlamesLeft.pixels, 0, this.imageTitle0.pixels, 0, 33920);

		int srcOffset = 0;
		int dstOffset = 1152;

		for ( int y = 1; y < height - 1; y++) {
			int offset = this.flameLineOffset[y] * (height - y) / height;
			int step = offset + 22;
			if (step < 0) {
				step = 0;
			}
			srcOffset += step;
			for (int x = step; x < 128; x++) {
				int value = this.flameBuffer3[srcOffset++];
				if (value == 0) {
					dstOffset++;
				} else {
					int alpha = value;
					int invAlpha = 256 - value;
					value = this.flameGradient[value];
					int background = this.imageTitle0.pixels[dstOffset];
					this.imageTitle0.pixels[dstOffset++] = ((value & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((value & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
				}
			}
			dstOffset += step;
		}

		this.imageTitle0.draw(super.graphics, 0, 0);

		System.arraycopy(this.imageFlamesRight.pixels, 0, this.imageTitle1.pixels, 0, 33920);

		srcOffset = 0;
		dstOffset = 1176;
		for (int y = 1; y < height - 1; y++) {
			int offset = this.flameLineOffset[y] * (height - y) / height;
			int step = 103 - offset;
			dstOffset += offset;
			for (int x = 0; x < step; x++) {
				int value = this.flameBuffer3[srcOffset++];
				if (value == 0) {
					dstOffset++;
				} else {
					int alpha = value;
					int invAlpha = 256 - value;
					value = this.flameGradient[value];
					int background = this.imageTitle1.pixels[dstOffset];
					this.imageTitle1.pixels[dstOffset++] = ((value & 0xFF00FF) * alpha + (background & 0xFF00FF) * invAlpha & 0xFF00FF00) + ((value & 0xFF00) * alpha + (background & 0xFF00) * invAlpha & 0xFF0000) >> 8;
				}
			}
			srcOffset += 128 - step;
			dstOffset += 128 - step - offset;
		}

		this.imageTitle1.draw(super.graphics, 661, 0);
	}

	private void handleInterfaceInput( ComType com, int mouseX, int mouseY, int x, int y, int scrollPosition) {
		if (com.type != 0 || com.childId == null || com.hide || (mouseX < x || mouseY < y || mouseX > x + com.width || mouseY > y + com.height)) {
			return;
		}

		int children = com.childId.length;
		for ( int i = 0; i < children; i++) {
			int childX = com.childX[i] + x;
			int childY = com.childY[i] + y - scrollPosition;
			ComType child = ComType.instances[com.childId[i]];

			childX += child.x;
			childY += child.y;

			if ((child.overLayer >= 0 || child.overColour != 0) && mouseX >= childX && mouseY >= childY && mouseX < childX + child.width && mouseY < childY + child.height) {
				if (child.overLayer >= 0) {
					this.lastHoveredInterfaceId = child.overLayer;
				} else {
					this.lastHoveredInterfaceId = child.id;
				}
			}

			if (child.type == 0) {
				this.handleInterfaceInput(child, mouseX, mouseY, childX, childY, child.scrollPosition);

				if (child.scroll > child.height) {
					this.handleScrollInput(mouseX, mouseY, child.scroll, child.height, true, childX + child.width, childY, child);
				}
			} else if (child.type == 2) {
				int slot = 0;

				for ( int row = 0; row < child.height; row++) {
					for ( int col = 0; col < child.width; col++) {
						int slotX = childX + col * (child.marginX + 32);
						int slotY = childY + row * (child.marginY + 32);

						if (slot < 20) {
							slotX += child.invSlotOffsetX[slot];
							slotY += child.invSlotOffsetY[slot];
						}

						if (mouseX < slotX || mouseY < slotY || mouseX >= slotX + 32 || mouseY >= slotY + 32) {
							slot++;
							continue;
						}

						this.hoveredSlot = slot;
						this.hoveredSlotParentId = child.id;

						if (child.invSlotObjId[slot] <= 0) {
							slot++;
							continue;
						}

						ObjType obj = ObjType.get(child.invSlotObjId[slot] - 1);

						if (this.objSelected == 1 && child.interactable) {
							if (child.id != this.objSelectedInterface || slot != this.objSelectedSlot) {
								this.menuOption[this.menuSize] = "Use " + this.objSelectedName + " with @lre@" + obj.name;
								this.menuAction[this.menuSize] = 881;
								this.menuParamA[this.menuSize] = obj.index;
								this.menuParamB[this.menuSize] = slot;
								this.menuParamC[this.menuSize] = child.id;
								this.menuSize++;
							}
						} else if (this.spellSelected == 1 && child.interactable) {
							if ((this.activeSpellFlags & 0x10) == 16) {
								this.menuOption[this.menuSize] = this.spellCaption + " @lre@" + obj.name;
								this.menuAction[this.menuSize] = 391;
								this.menuParamA[this.menuSize] = obj.index;
								this.menuParamB[this.menuSize] = slot;
								this.menuParamC[this.menuSize] = child.id;
								this.menuSize++;
							}
						} else {
							if (child.interactable) {
								for (int op = 4; op >= 3; op--) {
									if (obj.iops != null && obj.iops[op] != null) {
										this.menuOption[this.menuSize] = obj.iops[op] + " @lre@" + obj.name;
										if (op == 3) {
											this.menuAction[this.menuSize] = 478;
										} else if (op == 4) {
											this.menuAction[this.menuSize] = 347;
										}
										this.menuParamA[this.menuSize] = obj.index;
										this.menuParamB[this.menuSize] = slot;
										this.menuParamC[this.menuSize] = child.id;
										this.menuSize++;
									} else if (op == 4) {
										this.menuOption[this.menuSize] = "Drop @lre@" + obj.name;
										this.menuAction[this.menuSize] = 347;
										this.menuParamA[this.menuSize] = obj.index;
										this.menuParamB[this.menuSize] = slot;
										this.menuParamC[this.menuSize] = child.id;
										this.menuSize++;
									}
								}
							}

							if (child.usable) {
								this.menuOption[this.menuSize] = "Use @lre@" + obj.name;
								this.menuAction[this.menuSize] = 188;
								this.menuParamA[this.menuSize] = obj.index;
								this.menuParamB[this.menuSize] = slot;
								this.menuParamC[this.menuSize] = child.id;
								this.menuSize++;
							}

							if (child.interactable && obj.iops != null) {
								for (int op = 2; op >= 0; op--) {
									if (obj.iops[op] != null) {
										this.menuOption[this.menuSize] = obj.iops[op] + " @lre@" + obj.name;
										if (op == 0) {
											this.menuAction[this.menuSize] = 405;
										} else if (op == 1) {
											this.menuAction[this.menuSize] = 38;
										} else if (op == 2) {
											this.menuAction[this.menuSize] = 422;
										}
										this.menuParamA[this.menuSize] = obj.index;
										this.menuParamB[this.menuSize] = slot;
										this.menuParamC[this.menuSize] = child.id;
										this.menuSize++;
									}
								}
							}

							if (child.iops != null) {
								for (int op = 4; op >= 0; op--) {
									if (child.iops[op] != null) {
										this.menuOption[this.menuSize] = child.iops[op] + " @lre@" + obj.name;
										if (op == 0) {
											this.menuAction[this.menuSize] = 602;
										} else if (op == 1) {
											this.menuAction[this.menuSize] = 596;
										} else if (op == 2) {
											this.menuAction[this.menuSize] = 22;
										} else if (op == 3) {
											this.menuAction[this.menuSize] = 892;
										} else if (op == 4) {
											this.menuAction[this.menuSize] = 415;
										}
										this.menuParamA[this.menuSize] = obj.index;
										this.menuParamB[this.menuSize] = slot;
										this.menuParamC[this.menuSize] = child.id;
										this.menuSize++;
									}
								}
							}

							this.menuOption[this.menuSize] = "Examine @lre@" + obj.name;
							if (this.showDebug) {
								this.menuOption[this.menuSize] += "@whi@ (" + (obj.index) + ")";
							}
							this.menuAction[this.menuSize] = 1773;
							this.menuParamA[this.menuSize] = obj.index;
							this.menuParamC[this.menuSize] = child.invSlotObjCount[slot];
							this.menuSize++;
						}

						slot++;
					}
				}
			} else if (mouseX >= childX && mouseY >= childY && mouseX < childX + child.width && mouseY < childY + child.height) {
				if (child.buttonType == ComType.BUTTON_OK) {
					boolean override = false;
					if (child.clientCode != 0) {
						override = this.handleSocialMenuOption(child);
					}

					if (!override) {
						this.menuOption[this.menuSize] = child.option;
						this.menuAction[this.menuSize] = 951;
						this.menuParamC[this.menuSize] = child.id;
						this.menuSize++;
					}
				} else if (child.buttonType == ComType.BUTTON_TARGET && this.spellSelected == 0) {
					String prefix = child.actionVerb;
					if (prefix.indexOf(" ") != -1) {
						prefix = prefix.substring(0, prefix.indexOf(" "));
					}

					this.menuOption[this.menuSize] = prefix + " @gre@" + child.action;
					this.menuAction[this.menuSize] = 930;
					this.menuParamC[this.menuSize] = child.id;
					this.menuSize++;
				} else if (child.buttonType == ComType.BUTTON_CLOSE) {
					this.menuOption[this.menuSize] = "Close";
					this.menuAction[this.menuSize] = 947;
					this.menuParamC[this.menuSize] = child.id;
					this.menuSize++;
				} else if (child.buttonType == ComType.BUTTON_TOGGLE) {
					this.menuOption[this.menuSize] = child.option;
					this.menuAction[this.menuSize] = 465;
					this.menuParamC[this.menuSize] = child.id;
					this.menuSize++;
				} else if (child.buttonType == ComType.BUTTON_SELECT) {
					this.menuOption[this.menuSize] = child.option;
					this.menuAction[this.menuSize] = 960;
					this.menuParamC[this.menuSize] = child.id;
					this.menuSize++;
				} else if (child.buttonType == ComType.BUTTON_CONTINUE && !this.pressedContinueOption) {
					this.menuOption[this.menuSize] = child.option;
					this.menuAction[this.menuSize] = 44;
					this.menuParamC[this.menuSize] = child.id;
					this.menuSize++;
				}
			}
		}
	}

	private void handleChatSettingsInput() {
		if (super.mouseClickButton != 1) {
			return;
		}

		if (super.mouseClickX >= 8 && super.mouseClickX <= 108 && super.mouseClickY >= 490 && super.mouseClickY <= 522) {
			this.publicChatSetting = (this.publicChatSetting + 1) % 4;
			this.redrawPrivacySettings = true;
			this.redrawChatback = true;

			// CHAT_SETMODE
			this.out.p1isaac(244);
			this.out.p1(this.publicChatSetting);
			this.out.p1(this.privateChatSetting);
			this.out.p1(this.tradeChatSetting);
		} else if (super.mouseClickX >= 137 && super.mouseClickX <= 237 && super.mouseClickY >= 490 && super.mouseClickY <= 522) {
			this.privateChatSetting = (this.privateChatSetting + 1) % 3;
			this.redrawPrivacySettings = true;
			this.redrawChatback = true;

			// CHAT_SETMODE
			this.out.p1isaac(244);
			this.out.p1(this.publicChatSetting);
			this.out.p1(this.privateChatSetting);
			this.out.p1(this.tradeChatSetting);
		} else if (super.mouseClickX >= 275 && super.mouseClickX <= 375 && super.mouseClickY >= 490 && super.mouseClickY <= 522) {
			this.tradeChatSetting = (this.tradeChatSetting + 1) % 3;
			this.redrawPrivacySettings = true;
			this.redrawChatback = true;

			// CHAT_SETMODE
			this.out.p1isaac(244);
			this.out.p1(this.publicChatSetting);
			this.out.p1(this.privateChatSetting);
			this.out.p1(this.tradeChatSetting);
		} else if (super.mouseClickX >= 416 && super.mouseClickX <= 516 && super.mouseClickY >= 490 && super.mouseClickY <= 522) {
			this.closeInterfaces();

			this.reportAbuseInput = "";
			this.reportAbuseMuteOption = false;

			for ( int i = 0; i < ComType.instances.length; i++) {
				if (ComType.instances[i] != null && ComType.instances[i].clientCode == 600) {
					this.reportAbuseInterfaceID = this.viewportInterfaceId = ComType.instances[i].layer;
					return;
				}
			}
		}
	}

	private void handleChatMouseInput( int mouseX, int mouseY) {
		int line = 0;
		for ( int i = 0; i < 100; i++) {
			if (this.messageText[i] == null) {
				continue;
			}

			int type = this.messageType[i];
			int y = this.chatScrollOffset + 70 + 4 - line * 14;
			if (y < -20) {
				break;
			}

			if (type == 0) {
				line++;
			}

			if ((type == 1 || type == 2) && (type == 1 || this.publicChatSetting == 0 || this.publicChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
				if (mouseY > y - 14 && mouseY <= y && !this.messageSender[i].equals(this.localPlayer.name)) {
					if (this.rights) {
						this.menuOption[this.menuSize] = "Report abuse @whi@" + this.messageSender[i];
						this.menuAction[this.menuSize] = 34;
						this.menuSize++;
					}

					this.menuOption[this.menuSize] = "Add ignore @whi@" + this.messageSender[i];
					this.menuAction[this.menuSize] = 436;
					this.menuSize++;
					this.menuOption[this.menuSize] = "Add friend @whi@" + this.messageSender[i];
					this.menuAction[this.menuSize] = 406;
					this.menuSize++;
				}

				line++;
			}

			if ((type == 3 || type == 7) && this.splitPrivateChat == 0 && (type == 7 || this.privateChatSetting == 0 || this.privateChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
				if (mouseY > y - 14 && mouseY <= y) {
					if (this.rights) {
						this.menuOption[this.menuSize] = "Report abuse @whi@" + this.messageSender[i];
						this.menuAction[this.menuSize] = 34;
						this.menuSize++;
					}

					this.menuOption[this.menuSize] = "Add ignore @whi@" + this.messageSender[i];
					this.menuAction[this.menuSize] = 436;
					this.menuSize++;
					this.menuOption[this.menuSize] = "Add friend @whi@" + this.messageSender[i];
					this.menuAction[this.menuSize] = 406;
					this.menuSize++;
				}

				line++;
			}

			if (type == 4 && (this.tradeChatSetting == 0 || this.tradeChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
				if (mouseY > y - 14 && mouseY <= y) {
					this.menuOption[this.menuSize] = "Accept trade @whi@" + this.messageSender[i];
					this.menuAction[this.menuSize] = 903;
					this.menuSize++;
				}

				line++;
			}

			if ((type == 5 || type == 6) && this.splitPrivateChat == 0 && this.privateChatSetting < 2) {
				line++;
			}

			if (type == 8 && (this.tradeChatSetting == 0 || this.tradeChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
				if (mouseY > y - 14 && mouseY <= y) {
					this.menuOption[this.menuSize] = "Accept duel @whi@" + this.messageSender[i];
					this.menuAction[this.menuSize] = 363;
					this.menuSize++;
				}

				line++;
			}
		}
	}

	private void pushPlayers() {
		if (this.localPlayer.x >> 7 == this.flagSceneTileX && this.localPlayer.z >> 7 == this.flagSceneTileZ) {
			this.flagSceneTileX = 0;
		}

		for ( int i = -1; i < this.playerCount; i++) {
			PlayerEntity player;
			int id;
			if (i == -1) {
				player = this.localPlayer;
				id = this.LOCAL_PLAYER_INDEX << 14;
			} else {
				player = this.players[this.playerIds[i]];
				id = this.playerIds[i] << 14;
			}

			if (player == null || !player.isVisible()) {
				continue;
			}

			player.lowMemory = (lowMemory && this.playerCount > 50 || this.playerCount > 200) && i != -1 && player.secondarySeqId == player.seqStandId;
			int stx = player.x >> 7;
			int stz = player.z >> 7;

			if (stx < 0 || stx >= 104 || stz < 0 || stz >= 104) {
				continue;
			}

			if (player.locModel == null || loopCycle < player.locStartCycle || loopCycle >= player.locStopCycle) {
				if ((player.x & 0x7F) == 64 && (player.z & 0x7F) == 64) {
					if (this.tileLastOccupiedCycle[stx][stz] == this.sceneCycle) {
						continue;
					}

					this.tileLastOccupiedCycle[stx][stz] = this.sceneCycle;
				}

				player.y = this.getHeightmapY(this.currentLevel, player.x, player.z);
				this.scene.addTemporary(this.currentLevel, player.x, player.y, player.z, null, player, id, player.yaw, 60, player.seqStretches);
			} else {
				player.lowMemory = false;
				player.y = this.getHeightmapY(this.currentLevel, player.x, player.z);
				this.scene.addTemporary(this.currentLevel, player.x, player.y, player.z, player.minTileX, player.minTileZ, player.maxTileX, player.maxTileZ, null, player, id, player.yaw);
			}
		}
	}

	private int getHeightmapY( int level, int sceneX, int sceneZ) {
		int tileX = Math.min(sceneX >> 7, 103);
		int tileZ = Math.min(sceneZ >> 7, 103);
		int realLevel = level;
		if (level < 3 && (this.levelTileFlags[1][tileX][tileZ] & 0x2) == 2) {
			realLevel = level + 1;
		}

		int tileLocalX = sceneX & 0x7F;
		int tileLocalZ = sceneZ & 0x7F;
		int y00 = this.levelHeightmap[realLevel][tileX][tileZ] * (128 - tileLocalX) + this.levelHeightmap[realLevel][tileX + 1][tileZ] * tileLocalX >> 7;
		int y11 = this.levelHeightmap[realLevel][tileX][tileZ + 1] * (128 - tileLocalX) + this.levelHeightmap[realLevel][tileX + 1][tileZ + 1] * tileLocalX >> 7;
		return y00 * (128 - tileLocalZ) + y11 * tileLocalZ >> 7;
	}

	private void addNpcOptions( NpcType npc, int a, int b, int c) {
		if (this.menuSize >= 400) {
			return;
		}

		String tooltip = npc.name;
		if (npc.vislevel != 0) {
			tooltip = tooltip + getCombatLevelColorTag(this.localPlayer.combatLevel, npc.vislevel) + " (level-" + npc.vislevel + ")";
		}

		if (this.objSelected == 1) {
			this.menuOption[this.menuSize] = "Use " + this.objSelectedName + " with @yel@" + tooltip;
			this.menuAction[this.menuSize] = 900;
			this.menuParamA[this.menuSize] = a;
			this.menuParamB[this.menuSize] = b;
			this.menuParamC[this.menuSize] = c;
			this.menuSize++;
		} else if (this.spellSelected != 1) {
			int type;
			if (npc.ops != null) {
				for (type = 4; type >= 0; type--) {
					if (npc.ops[type] != null && !npc.ops[type].equalsIgnoreCase("attack")) {
						this.menuOption[this.menuSize] = npc.ops[type] + " @yel@" + tooltip;

						if (type == 0) {
							this.menuAction[this.menuSize] = 728;
						} else if (type == 1) {
							this.menuAction[this.menuSize] = 542;
						} else if (type == 2) {
							this.menuAction[this.menuSize] = 6;
						} else if (type == 3) {
							this.menuAction[this.menuSize] = 963;
						} else if (type == 4) {
							this.menuAction[this.menuSize] = 245;
						}

						this.menuParamA[this.menuSize] = a;
						this.menuParamB[this.menuSize] = b;
						this.menuParamC[this.menuSize] = c;
						this.menuSize++;
					}
				}
			}

			if (npc.ops != null) {
				for (type = 4; type >= 0; type--) {
					if (npc.ops[type] != null && npc.ops[type].equalsIgnoreCase("attack")) {
						short action = 0;
						if (npc.vislevel > this.localPlayer.combatLevel) {
							action = 2000;
						}

						this.menuOption[this.menuSize] = npc.ops[type] + " @yel@" + tooltip;

						if (type == 0) {
							this.menuAction[this.menuSize] = action + 728;
						} else if (type == 1) {
							this.menuAction[this.menuSize] = action + 542;
						} else if (type == 2) {
							this.menuAction[this.menuSize] = action + 6;
						} else if (type == 3) {
							this.menuAction[this.menuSize] = action + 963;
						} else if (type == 4) {
							this.menuAction[this.menuSize] = action + 245;
						}

						this.menuParamA[this.menuSize] = a;
						this.menuParamB[this.menuSize] = b;
						this.menuParamC[this.menuSize] = c;
						this.menuSize++;
					}
				}
			}

			this.menuOption[this.menuSize] = "Examine @yel@" + tooltip;
			if (this.showDebug) {
				this.menuOption[this.menuSize] += "@whi@ (" + (npc.index) + ")";
			}
			this.menuAction[this.menuSize] = 1607;
			this.menuParamA[this.menuSize] = a;
			this.menuParamB[this.menuSize] = b;
			this.menuParamC[this.menuSize] = c;
			this.menuSize++;
		} else if ((this.activeSpellFlags & 0x2) == 2) {
			this.menuOption[this.menuSize] = this.spellCaption + " @yel@" + tooltip;
			this.menuAction[this.menuSize] = 265;
			this.menuParamA[this.menuSize] = a;
			this.menuParamB[this.menuSize] = b;
			this.menuParamC[this.menuSize] = c;
			this.menuSize++;
		}
	}

	private void handleInputKey() {
		while (true) {
			int key;
			do {
				while (true) {
					key = this.pollKey();
					if (key == -1) {
						return;
					}

					if (this.viewportInterfaceId != -1 && this.viewportInterfaceId == this.reportAbuseInterfaceID) {
						if (key == 8 && this.reportAbuseInput.length() > 0) {
							this.reportAbuseInput = this.reportAbuseInput.substring(0, this.reportAbuseInput.length() - 1);
						}
						break;
					}

					if (this.showSocialInput) {
						if (key >= 32 && key <= 122 && this.socialInput.length() < 80) {
							this.socialInput = this.socialInput + (char) key;
							this.redrawChatback = true;
						}

						if (key == 8 && this.socialInput.length() > 0) {
							this.socialInput = this.socialInput.substring(0, this.socialInput.length() - 1);
							this.redrawChatback = true;
						}

						if (key == 13 || key == 10) {
							this.showSocialInput = false;
							this.redrawChatback = true;

							long username;
							if (this.socialAction == 1) {
								username = JString.toBase37(this.socialInput);
								this.addFriend(username);
							}

							if (this.socialAction == 2 && this.friendCount > 0) {
								username = JString.toBase37(this.socialInput);
								this.removeFriend(username);
							}

							if (this.socialAction == 3 && this.socialInput.length() > 0) {
								// MESSAGE_PRIVATE
								this.out.p1isaac(148);
								this.out.p1(0);
								int start = this.out.pos;
								this.out.p8(this.socialName37);
								WordPack.pack(this.out, this.socialInput);
								this.out.psize1(this.out.pos - start);
								this.socialInput = JString.toSentenceCase(this.socialInput);
								this.socialInput = WordFilter.filter(this.socialInput);
								this.addMessage(6, this.socialInput, JString.formatName(JString.fromBase37(this.socialName37)));
								if (this.privateChatSetting == 2) {
									this.privateChatSetting = 1;
									this.redrawPrivacySettings = true;
									// CHAT_SETMODE
									this.out.p1isaac(244);
									this.out.p1(this.publicChatSetting);
									this.out.p1(this.privateChatSetting);
									this.out.p1(this.tradeChatSetting);
								}
							}

							if (this.socialAction == 4 && this.ignoreCount < 100) {
								username = JString.toBase37(this.socialInput);
								this.addIgnore(username);
							}

							if (this.socialAction == 5 && this.ignoreCount > 0) {
								username = JString.toBase37(this.socialInput);
								this.removeIgnore(username);
							}
						}
					} else if (this.chatbackInputOpen) {
						if (key >= 48 && key <= 57 && this.chatbackInput.length() < 10) {
							this.chatbackInput = this.chatbackInput + (char) key;
							this.redrawChatback = true;
						}

						if (key == 8 && this.chatbackInput.length() > 0) {
							this.chatbackInput = this.chatbackInput.substring(0, this.chatbackInput.length() - 1);
							this.redrawChatback = true;
						}

						if (key == 13 || key == 10) {
							if (this.chatbackInput.length() > 0) {
								int value = 0;
								try {
									value = Integer.parseInt(this.chatbackInput);
								} catch ( Exception ignored) {
								}
								// RESUME_P_COUNTDIALOG
								this.out.p1isaac(237);
								this.out.p4(value);
							}
							this.chatbackInputOpen = false;
							this.redrawChatback = true;
						}
					} else if (this.chatInterfaceId == -1) {
						if (key >= 32 && key <= 122 && this.chatTyped.length() < 80) {
							this.chatTyped = this.chatTyped + (char) key;
							this.redrawChatback = true;
						}

						if (key == 8 && this.chatTyped.length() > 0) {
							this.chatTyped = this.chatTyped.substring(0, this.chatTyped.length() - 1);
							this.redrawChatback = true;
						}

						if ((key == 13 || key == 10) && this.chatTyped.length() > 0) {
							// if (this.rights) {
							if (this.chatTyped.equals("::clientdrop") && super.frame != null) {
								this.tryReconnect();
							} else if (this.chatTyped.equals("::noclip")) {
								for (int level = 0; level < 4; level++) {
									for (int x = 1; x < 103; x++) {
										for (int z = 1; z < 103; z++) {
											this.levelCollisionMap[level].flags[x][z] = 0;
										}
									}
								}
							} else if (this.chatTyped.equals("::debug")) {
								this.showDebug = !this.showDebug;
							} else if (this.chatTyped.equals("::perf")) {
								this.showPerformance = !this.showPerformance;
							}
							// }

							if (this.chatTyped.startsWith("::")) {
								// CLIENT_CHEAT
								this.out.p1isaac(4);
								this.out.p1(this.chatTyped.length() - 1);
								this.out.pjstr(this.chatTyped.substring(2));
							} else {
								byte color = 0;
								if (this.chatTyped.startsWith("yellow:")) {
									color = 0;
									this.chatTyped = this.chatTyped.substring(7);
								} else if (this.chatTyped.startsWith("red:")) {
									color = 1;
									this.chatTyped = this.chatTyped.substring(4);
								} else if (this.chatTyped.startsWith("green:")) {
									color = 2;
									this.chatTyped = this.chatTyped.substring(6);
								} else if (this.chatTyped.startsWith("cyan:")) {
									color = 3;
									this.chatTyped = this.chatTyped.substring(5);
								} else if (this.chatTyped.startsWith("purple:")) {
									color = 4;
									this.chatTyped = this.chatTyped.substring(7);
								} else if (this.chatTyped.startsWith("white:")) {
									color = 5;
									this.chatTyped = this.chatTyped.substring(6);
								} else if (this.chatTyped.startsWith("flash1:")) {
									color = 6;
									this.chatTyped = this.chatTyped.substring(7);
								} else if (this.chatTyped.startsWith("flash2:")) {
									color = 7;
									this.chatTyped = this.chatTyped.substring(7);
								} else if (this.chatTyped.startsWith("flash3:")) {
									color = 8;
									this.chatTyped = this.chatTyped.substring(7);
								} else if (this.chatTyped.startsWith("glow1:")) {
									color = 9;
									this.chatTyped = this.chatTyped.substring(6);
								} else if (this.chatTyped.startsWith("glow2:")) {
									color = 10;
									this.chatTyped = this.chatTyped.substring(6);
								} else if (this.chatTyped.startsWith("glow3:")) {
									color = 11;
									this.chatTyped = this.chatTyped.substring(6);
								}

								byte effect = 0;
								if (this.chatTyped.startsWith("wave:")) {
									effect = 1;
									this.chatTyped = this.chatTyped.substring(5);
								}
								if (this.chatTyped.startsWith("scroll:")) {
									effect = 2;
									this.chatTyped = this.chatTyped.substring(7);
								}

								// MESSAGE_PUBLIC
								this.out.p1isaac(158);
								this.out.p1(0);
								int start = this.out.pos;
								this.out.p1(color);
								this.out.p1(effect);
								WordPack.pack(this.out, this.chatTyped);
								this.out.psize1(this.out.pos - start);

								this.chatTyped = JString.toSentenceCase(this.chatTyped);
								this.chatTyped = WordFilter.filter(this.chatTyped);
								this.localPlayer.chat = this.chatTyped;
								this.localPlayer.chatColor = color;
								this.localPlayer.chatStyle = effect;
								this.localPlayer.chatTimer = 150;
								this.addMessage(2, this.localPlayer.chat, this.localPlayer.name);

								if (this.publicChatSetting == 2) {
									this.publicChatSetting = 3;
									this.redrawPrivacySettings = true;
									// CHAT_SETMODE
									this.out.p1isaac(244);
									this.out.p1(this.publicChatSetting);
									this.out.p1(this.privateChatSetting);
									this.out.p1(this.tradeChatSetting);
								}
							}

							this.chatTyped = "";
							this.redrawChatback = true;
						}
					}
				}
			} while ((key < 97 || key > 122) && (key < 65 || key > 90) && (key < 48 || key > 57) && key != 32);

			if (this.reportAbuseInput.length() < 12) {
				this.reportAbuseInput = this.reportAbuseInput + (char) key;
			}
		}
	}

	public void preInit() throws UnknownHostException {
		Client.vanilla = false;
		Client.nodeId = 10;
		Client.portOffset = Configuration.PORT_OFFSET;
		Client.setHighMemory();
		Client.members = true;
		signlink.startDaemon();
	}

	@Override
	protected void draw() {
		if (this.errorStarted || this.errorLoading || this.errorHost) {
			this.drawError();
		} else {
			if (this.ingame) {
				this.drawGame();
			} else {
				this.drawTitleScreen();
			}

			this.dragCycles = 0;
		}
		if (MidiPlayer.synthesizer != null) {
			MidiChannel[] channels = MidiPlayer.synthesizer.getChannels();
			for (MidiChannel channel : channels) {
				channel.controlChange(7, MidiPlayer.volume);
			}
		}
	}

	private void updateTitle() {
		if (this.titleScreenState == 0) {
			int x = super.screenWidth / 2 - 80;
			int y = super.screenHeight / 2 + 20;

			y += 20;
			if (super.mouseClickButton == 1 && super.mouseClickX >= x - 75 && super.mouseClickX <= x + 75 && super.mouseClickY >= y - 20 && super.mouseClickY <= y + 20) {
				this.titleScreenState = 3;
				this.titleLoginField = 0;
			}

			x = super.screenWidth / 2 + 80;
			if (super.mouseClickButton == 1 && super.mouseClickX >= x - 75 && super.mouseClickX <= x + 75 && super.mouseClickY >= y - 20 && super.mouseClickY <= y + 20) {
				this.loginMessage0 = "";
				this.loginMessage1 = "Enter your username & password.";
				this.titleScreenState = 2;
				this.titleLoginField = 0;
			}
		} else if (this.titleScreenState == 2) {
			int y = super.screenHeight / 2 - 40;
			y += 30;
			y += 25;

			if (super.mouseClickButton == 1 && super.mouseClickY >= y - 15 && super.mouseClickY < y) {
				this.titleLoginField = 0;
			}
			y += 15;

			if (super.mouseClickButton == 1 && super.mouseClickY >= y - 15 && super.mouseClickY < y) {
				this.titleLoginField = 1;
			}
			y += 15;

			int buttonX = super.screenWidth / 2 - 80;
			int buttonY = super.screenHeight / 2 + 50;
			buttonY += 20;

			if (super.mouseClickButton == 1 && super.mouseClickX >= buttonX - 75 && super.mouseClickX <= buttonX + 75 && super.mouseClickY >= buttonY - 20 && super.mouseClickY <= buttonY + 20) {
				this.login(this.username, this.password, false);
			}

			buttonX = super.screenWidth / 2 + 80;
			if (super.mouseClickButton == 1 && super.mouseClickX >= buttonX - 75 && super.mouseClickX <= buttonX + 75 && super.mouseClickY >= buttonY - 20 && super.mouseClickY <= buttonY + 20) {
				this.titleScreenState = 0;
				this.username = "";
				this.password = "";
			}

			while (true) {
				while (true) {
					int key = this.pollKey();
					if (key == -1) {
						return;
					}

					boolean valid = false;
					for ( int i = 0; i < CHARSET.length(); i++) {
						if (key == CHARSET.charAt(i)) {
							valid = true;
							break;
						}
					}

					if (this.titleLoginField == 0) {
						if (key == 8 && this.username.length() > 0) {
							this.username = this.username.substring(0, this.username.length() - 1);
						}

						if (key == 9 || key == 10 || key == 13) {
							this.titleLoginField = 1;
						}

						if (valid) {
							this.username = this.username + (char) key;
						}

						if (this.username.length() > 12) {
							this.username = this.username.substring(0, 12);
						}
					} else if (this.titleLoginField == 1) {
						if (key == 8 && this.password.length() > 0) {
							this.password = this.password.substring(0, this.password.length() - 1);
						}

						if (key == 9 || key == 10 || key == 13) {
							this.titleLoginField = 0;
						}

						if (valid) {
							this.password = this.password + (char) key;
						}

						if (this.password.length() > 20) {
							this.password = this.password.substring(0, 20);
						}
					}
				}
			}
		} else if (this.titleScreenState == 3) {
			int x = super.screenWidth / 2;
			int y = super.screenHeight / 2 + 50;
			y += 20;

			if (super.mouseClickButton == 1 && super.mouseClickX >= x - 75 && super.mouseClickX <= x + 75 && super.mouseClickY >= y - 20 && super.mouseClickY <= y + 20) {
				this.titleScreenState = 0;
			}
		}
	}

	private Jagfile loadArchive( String name, int crc, String displayName, int displayProgress) {
		int retry = 5;
		byte[] data = signlink.cacheload(name);
		if (data != null) {
			this.crc32.reset();
			this.crc32.update(data);
			int crcValue = (int) this.crc32.getValue();
			if (crcValue != crc) {
				data = null;
			}
		}

		if (data != null) {
			return new Jagfile(data);
		}

		while (data == null) {
			this.drawProgress("Requesting " + displayName, displayProgress);

			try {
				int lastProgress = 0;
				DataInputStream stream = this.openUrl(name + crc);
				byte[] header = new byte[6];
				stream.readFully(header, 0, 6);
				Packet head = new Packet(header);
				head.pos = 3;
				int length = head.g3() + 6;
				int offset = 6;
				data = new byte[length];
				System.arraycopy(header, 0, data, 0, 6);
				while (offset < length) {
					int remaining = length - offset;
					if (remaining > 1000) {
						remaining = 1000;
					}

					offset += stream.read(data, offset, remaining);
					int progress = offset * 100 / length;
					if (progress != lastProgress) {
						this.drawProgress("Loading " + displayName + " - " + progress + "%", displayProgress);
					}
					lastProgress = progress;
				}
				stream.close();
			} catch ( IOException ex) {
				data = null;
				for (int i = retry; i > 0; i--) {
					this.drawProgress("Error loading - Will retry in " + i + " secs.", displayProgress);
					try {
						Thread.sleep(1000L);
					} catch ( Exception ignored) {
					}
				}

				retry *= 2;
				if (retry > 60) {
					retry = 60;
				}
			}
		}

		signlink.cachesave(name, data);
		return new Jagfile(data);
	}

	private void unloadTitle() {
		this.flameActive = false;
		while (this.flameThread) {
			this.flameActive = false;
			try {
				Thread.sleep(50L);
			} catch ( Exception ignored) {
			}
		}

		this.imageTitlebox = null;
		this.imageTitlebutton = null;
		this.imageRunes = null;
		this.flameGradient = null;
		this.flameGradient0 = null;
		this.flameGradient1 = null;
		this.flameGradient2 = null;
		this.flameBuffer0 = null;
		this.flameBuffer1 = null;
		this.flameBuffer3 = null;
		this.flameBuffer2 = null;
		this.imageFlamesLeft = null;
		this.imageFlamesRight = null;
	}

	private void orbitCamera( int targetX, int targetY, int targetZ, int yaw, int pitch, int distance) {
		int invPitch = 2048 - pitch & 0x7FF;
		int invYaw = 2048 - yaw & 0x7FF;
		int x = 0;
		int z = 0;
		int y = distance;
		int sin;
		int cos;
		int tmp;

		if (invPitch != 0) {
			sin = Model.sin[invPitch];
			cos = Model.cos[invPitch];
			tmp = z * cos - distance * sin >> 16;
			y = z * sin + distance * cos >> 16;
			z = tmp;
		}

		if (invYaw != 0) {
			sin = Model.sin[invYaw];
			cos = Model.cos[invYaw];
			tmp = y * sin + x * cos >> 16;
			y = y * cos - x * sin >> 16;
			x = tmp;
		}

		this.cameraX = targetX - x;
		this.cameraY = targetY - z;
		this.cameraZ = targetZ - y;
		this.cameraPitch = pitch;
		this.cameraYaw = yaw;
	}

	private void updateTextures( int cycle) {
		if (!lowMemory) {
			if (Draw3D.textureCycle[17] >= cycle) {
				Pix8 texture = Draw3D.textures[17];
				int bottom = texture.width * texture.height - 1;
				int adjustment = texture.width * this.sceneDelta * 2;

				byte[] src = texture.pixels;
				byte[] dst = this.textureBuffer;
				for (int i = 0; i <= bottom; i++) {
					dst[i] = src[i - adjustment & bottom];
				}

				texture.pixels = dst;
				this.textureBuffer = src;
				Draw3D.pushTexture(17);
			}

			if (Draw3D.textureCycle[24] >= cycle) {
				Pix8 texture = Draw3D.textures[24];
				int bottom = texture.width * texture.height - 1;
				int adjustment = texture.width * this.sceneDelta * 2;

				byte[] src = texture.pixels;
				byte[] dst = this.textureBuffer;
				for (int i = 0; i <= bottom; i++) {
					dst[i] = src[i - adjustment & bottom];
				}

				texture.pixels = dst;
				this.textureBuffer = src;
				Draw3D.pushTexture(24);
			}
		}
	}

	private void updateFlames() {
		short height = 256;
		for ( int x = 10; x < 117; x++) {
			int rand = (int) (Math.random() * 100.0D);
			if (rand < 50) {
				this.flameBuffer3[x + (height - 2 << 7)] = 255;
			}
		}

		for (int l = 0; l < 100; l++) {
			int x = (int) (Math.random() * 124.0D) + 2;
			int y = (int) (Math.random() * 128.0D) + 128;
			int index = x + (y << 7);
			this.flameBuffer3[index] = 192;
		}

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < 127; x++) {
				int index = x + (y << 7);
				this.flameBuffer2[index] = (this.flameBuffer3[index - 1] + this.flameBuffer3[index + 1] + this.flameBuffer3[index - 128] + this.flameBuffer3[index + 128]) / 4;
			}
		}

		this.flameCycle0 += 128;
		if (this.flameCycle0 > this.flameBuffer0.length) {
			this.flameCycle0 -= this.flameBuffer0.length;
			int rand = (int) (Math.random() * 12.0D);
			this.updateFlameBuffer(this.imageRunes[rand]);
		}

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < 127; x++) {
				int index = x + (y << 7);
				int intensity = this.flameBuffer2[index + 128] - this.flameBuffer0[index + this.flameCycle0 & this.flameBuffer0.length - 1] / 5;
				if (intensity < 0) {
					intensity = 0;
				}
				this.flameBuffer3[index] = intensity;
			}
		}

		for (int y = 0; y < height - 1; y++) {
			this.flameLineOffset[y] = this.flameLineOffset[y + 1];
		}

		this.flameLineOffset[height - 1] = (int) (Math.sin((double) loopCycle / 14.0D) * 16.0D + Math.sin((double) loopCycle / 15.0D) * 14.0D + Math.sin((double) loopCycle / 16.0D) * 12.0D);

		if (this.flameGradientCycle0 > 0) {
			this.flameGradientCycle0 -= 4;
		}

		if (this.flameGradientCycle1 > 0) {
			this.flameGradientCycle1 -= 4;
		}

		if (this.flameGradientCycle0 == 0 && this.flameGradientCycle1 == 0) {
			int rand = (int) (Math.random() * 2000.0D);

			if (rand == 0) {
				this.flameGradientCycle0 = 1024;
			} else if (rand == 1) {
				this.flameGradientCycle1 = 1024;
			}
		}
	}

	private void drawMinimap() {
		this.areaMapback.bind();
		int angle = this.orbitCameraYaw + this.minimapAnticheatAngle & 0x7FF;
		int anchorX = this.localPlayer.x / 32 + 48;
		int anchorY = 464 - this.localPlayer.z / 32;

		this.imageMinimap.drawRotatedMasked(21, 9, 146, 151, this.minimapMaskLineOffsets, this.minimapMaskLineLengths, anchorX, anchorY, angle, this.minimapZoom + 256);
		this.imageCompass.drawRotatedMasked(0, 0, 33, 33, this.compassMaskLineOffsets, this.compassMaskLineLengths, 25, 25, this.orbitCameraYaw, 256);
		for ( int i = 0; i < this.activeMapFunctionCount; i++) {
			anchorX = this.activeMapFunctionX[i] * 4 + 2 - this.localPlayer.x / 32;
			anchorY = this.activeMapFunctionZ[i] * 4 + 2 - this.localPlayer.z / 32;
			this.drawOnMinimap(anchorY, this.activeMapFunctions[i], anchorX);
		}

		for ( int ltx = 0; ltx < 104; ltx++) {
			for (int ltz = 0; ltz < 104; ltz++) {
				LinkList stack = this.levelObjStacks[this.currentLevel][ltx][ltz];
				if (stack != null) {
					anchorX = ltx * 4 + 2 - this.localPlayer.x / 32;
					anchorY = ltz * 4 + 2 - this.localPlayer.z / 32;
					this.drawOnMinimap(anchorY, this.imageMapdot0, anchorX);
				}
			}
		}

		for (int i = 0; i < this.npcCount; i++) {
			NpcEntity npc = this.npcs[this.npcIds[i]];
			if (npc != null && npc.isVisible() && npc.type.visonmap) {
				anchorX = npc.x / 32 - this.localPlayer.x / 32;
				anchorY = npc.z / 32 - this.localPlayer.z / 32;
				this.drawOnMinimap(anchorY, this.imageMapdot1, anchorX);
			}
		}

		for ( int i = 0; i < this.playerCount; i++) {
			PlayerEntity player = this.players[this.playerIds[i]];
			if (player != null && player.isVisible()) {
				anchorX = player.x / 32 - this.localPlayer.x / 32;
				anchorY = player.z / 32 - this.localPlayer.z / 32;

				boolean friend = false;
				long name37 = JString.toBase37(player.name);
				for ( int j = 0; j < this.friendCount; j++) {
					if (name37 == this.friendName37[j] && this.friendWorld[j] != 0) {
						friend = true;
						break;
					}
				}

				if (friend) {
					this.drawOnMinimap(anchorY, this.imageMapdot3, anchorX);
				} else {
					this.drawOnMinimap(anchorY, this.imageMapdot2, anchorX);
				}
			}
		}

		if (this.flagSceneTileX != 0) {
			anchorX = this.flagSceneTileX * 4 + 2 - this.localPlayer.x / 32;
			anchorY = this.flagSceneTileZ * 4 + 2 - this.localPlayer.z / 32;
			this.drawOnMinimap(anchorY, this.imageMapflag, anchorX);
		}

		Draw2D.fillRect(93, 82, 0xffffff, 3, 3);
		this.areaViewport.bind();
	}

	public Component getBaseComponent() {
		if (signlink.mainapp != null) {
			return signlink.mainapp;
		}

		if (super.frame != null) {
			return super.frame;
		}

		return this;
	}

	private void updateTemporaryLocs() {
		if (this.sceneState == 2) {
			for ( LocSpawned loc = (LocSpawned) this.temporaryLocs.peekFront(); loc != null; loc = (LocSpawned) this.temporaryLocs.prev()) {
				if (loopCycle >= loc.lastCycle) {
					this.addLoc(loc.plane, loc.x, loc.z, loc.locIndex, loc.angle, loc.shape, loc.layer);
					loc.unlink();
				}
			}

			updateLocCounter++;
			if (updateLocCounter > 85) {
				updateLocCounter = 0;
				// ANTICHEAT_CYCLELOGIC5
				this.out.p1isaac(85);
			}
		}
	}

	private void createMinimap( int level) {
		int[] pixels = this.imageMinimap.pixels;
		int length = pixels.length;
		for ( int i = 0; i < length; i++) {
			pixels[i] = 0;
		}

		for ( int z = 1; z < 103; z++) {
			int offset = (103 - z) * 512 * 4 + 24628;

			for (int x = 1; x < 103; x++) {
				if ((this.levelTileFlags[level][x][z] & 0x18) == 0) {
					this.scene.drawMinimapTile(level, x, z, pixels, offset, 512);
				}

				if (level < 3 && (this.levelTileFlags[level + 1][x][z] & 0x8) != 0) {
					this.scene.drawMinimapTile(level + 1, x, z, pixels, offset, 512);
				}

				offset += 4;
			}
		}

		int wallRgb = ((int) (Math.random() * 20.0D) + 238 - 10 << 16) + ((int) (Math.random() * 20.0D) + 238 - 10 << 8) + (int) (Math.random() * 20.0D) + 238 - 10;
		int doorRgb = (int) (Math.random() * 20.0D) + 238 - 10 << 16;

		this.imageMinimap.bind();

		for ( int z = 1; z < 103; z++) {
			for (int x = 1; x < 103; x++) {
				if ((this.levelTileFlags[level][x][z] & 0x18) == 0) {
					this.drawMinimapLoc(x, z, level, wallRgb, doorRgb);
				}

				if (level < 3 && (this.levelTileFlags[level + 1][x][z] & 0x8) != 0) {
					this.drawMinimapLoc(x, z, level + 1, wallRgb, doorRgb);
				}
			}
		}

		this.areaViewport.bind();
		this.activeMapFunctionCount = 0;

		for (int x = 0; x < 104; x++) {
			for ( int z = 0; z < 104; z++) {
				int bitset = this.scene.getGroundDecorationBitset(this.currentLevel, x, z);
				if (bitset == 0) {
					continue;
				}

				bitset = bitset >> 14 & 0x7FFF;

				int func = LocType.get(bitset).mapfunction;
				if (func < 0) {
					continue;
				}

				int stx = x;
				int stz = z;

				if (func != 22 && func != 29 && func != 34 && func != 36 && func != 46 && func != 47 && func != 48) {
					byte maxX = 104;
					byte maxZ = 104;
					int[][] flags = this.levelCollisionMap[this.currentLevel].flags;

					for ( int i = 0; i < 10; i++) {
						int rand = (int) (Math.random() * 4.0D);
						if (rand == 0 && stx > 0 && stx > x - 3 && (flags[stx - 1][stz] & 0x280108) == 0) {
							stx--;
						}

						if (rand == 1 && stx < maxX - 1 && stx < x + 3 && (flags[stx + 1][stz] & 0x280180) == 0) {
							stx++;
						}

						if (rand == 2 && stz > 0 && stz > z - 3 && (flags[stx][stz - 1] & 0x280102) == 0) {
							stz--;
						}

						if (rand == 3 && stz < maxZ - 1 && stz < z + 3 && (flags[stx][stz + 1] & 0x280120) == 0) {
							stz++;
						}
					}
				}

				this.activeMapFunctions[this.activeMapFunctionCount] = this.imageMapfunction[func];
				this.activeMapFunctionX[this.activeMapFunctionCount] = stx;
				this.activeMapFunctionZ[this.activeMapFunctionCount] = stz;
				this.activeMapFunctionCount++;
			}
		}
	}

	private void drawMinimapLoc( int tileX, int tileZ, int level, int wallRgb, int doorRgb) {
		int bitset = this.scene.getWallBitset(level, tileX, tileZ);
		if (bitset != 0) {
			int info = this.scene.getInfo(level, tileX, tileZ, bitset);
			int angle = info >> 6 & 0x3;
			int shape = info & 0x1F;
			int rgb = wallRgb;
			if (bitset > 0) {
				rgb = doorRgb;
			}

			int[] dst = this.imageMinimap.pixels;
			int offset = tileX * 4 + (103 - tileZ) * 512 * 4 + 24624;
			int locId = bitset >> 14 & 0x7FFF;

			LocType loc = LocType.get(locId);
			if (loc.mapscene == -1) {
				if (shape == LocType.WALL_STRAIGHT || shape == LocType.WALL_L) {
					if (angle == 0) {
						dst[offset] = rgb;
						dst[offset + 512] = rgb;
						dst[offset + 1024] = rgb;
						dst[offset + 1536] = rgb;
					} else if (angle == 1) {
						dst[offset] = rgb;
						dst[offset + 1] = rgb;
						dst[offset + 2] = rgb;
						dst[offset + 3] = rgb;
					} else if (angle == 2) {
						dst[offset + 3] = rgb;
						dst[offset + 3 + 512] = rgb;
						dst[offset + 3 + 1024] = rgb;
						dst[offset + 3 + 1536] = rgb;
					} else if (angle == 3) {
						dst[offset + 1536] = rgb;
						dst[offset + 1536 + 1] = rgb;
						dst[offset + 1536 + 2] = rgb;
						dst[offset + 1536 + 3] = rgb;
					}
				}

				if (shape == LocType.WALL_SQUARECORNER) {
					if (angle == 0) {
						dst[offset] = rgb;
					} else if (angle == 1) {
						dst[offset + 3] = rgb;
					} else if (angle == 2) {
						dst[offset + 3 + 1536] = rgb;
					} else if (angle == 3) {
						dst[offset + 1536] = rgb;
					}
				}

				if (shape == LocType.WALL_L) {
					if (angle == 3) {
						dst[offset] = rgb;
						dst[offset + 512] = rgb;
						dst[offset + 1024] = rgb;
						dst[offset + 1536] = rgb;
					} else if (angle == 0) {
						dst[offset] = rgb;
						dst[offset + 1] = rgb;
						dst[offset + 2] = rgb;
						dst[offset + 3] = rgb;
					} else if (angle == 1) {
						dst[offset + 3] = rgb;
						dst[offset + 3 + 512] = rgb;
						dst[offset + 3 + 1024] = rgb;
						dst[offset + 3 + 1536] = rgb;
					} else if (angle == 2) {
						dst[offset + 1536] = rgb;
						dst[offset + 1536 + 1] = rgb;
						dst[offset + 1536 + 2] = rgb;
						dst[offset + 1536 + 3] = rgb;
					}
				}
			} else {
				Pix8 scene = this.imageMapscene[loc.mapscene];
				if (scene != null) {
					int offsetX = (loc.width * 4 - scene.width) / 2;
					int offsetY = (loc.length * 4 - scene.height) / 2;
					scene.draw(tileX * 4 + 48 + offsetX, (104 - tileZ - loc.length) * 4 + offsetY + 48);
				}
			}
		}

		bitset = this.scene.getLocBitset(level, tileX, tileZ);
		if (bitset != 0) {
			int info = this.scene.getInfo(level, tileX, tileZ, bitset);
			int angle = info >> 6 & 0x3;
			int shape = info & 0x1F;
			int locId = bitset >> 14 & 0x7FFF;
			LocType loc = LocType.get(locId);

			if (loc.mapscene != -1) {
				Pix8 scene = this.imageMapscene[loc.mapscene];
				if (scene != null) {
					int offsetX = (loc.width * 4 - scene.width) / 2;
					int offsetY = (loc.length * 4 - scene.height) / 2;
					scene.draw(tileX * 4 + 48 + offsetX, (104 - tileZ - loc.length) * 4 + offsetY + 48);
				}
			} else if (shape == LocType.WALL_DIAGONAL) {
				int rgb = 0xeeeeee;
				if (bitset > 0) {
					rgb = 0xee0000;
				}

				int[] dst = this.imageMinimap.pixels;
				int offset = tileX * 4 + (103 - tileZ) * 512 * 4 + 24624;

				if (angle == 0 || angle == 2) {
					dst[offset + 1536] = rgb;
					dst[offset + 1024 + 1] = rgb;
					dst[offset + 512 + 2] = rgb;
					dst[offset + 3] = rgb;
				} else {
					dst[offset] = rgb;
					dst[offset + 512 + 1] = rgb;
					dst[offset + 1024 + 2] = rgb;
					dst[offset + 1536 + 3] = rgb;
				}
			}
		}

		bitset = this.scene.getGroundDecorationBitset(level, tileX, tileZ);
		if (bitset != 0) {
			int locId = bitset >> 14 & 0x7FFF;
			LocType loc = LocType.get(locId);
			if (loc.mapscene != -1) {
				Pix8 scene = this.imageMapscene[loc.mapscene];
				if (scene != null) {
					int offsetX = (loc.width * 4 - scene.width) / 2;
					int offsetY = (loc.length * 4 - scene.height) / 2;
					scene.draw(tileX * 4 + 48 + offsetX, (104 - tileZ - loc.length) * 4 + offsetY + 48);
				}
			}
		}
	}

	private void readNpcInfo( Packet buf, int size) {
		this.entityRemovalCount = 0;
		this.entityUpdateCount = 0;

		this.readNpcs(buf, size);
		this.readNewNpcs(buf, size);
		this.readNpcUpdates(buf, size);

		for ( int i = 0; i < this.entityRemovalCount; i++) {
			int index = this.entityRemovalIds[i];
			if (this.npcs[index].cycle != loopCycle) {
				this.npcs[index].type = null;
				this.npcs[index] = null;
			}
		}

		if (buf.pos != size) {
			signlink.reporterror(this.username + " size mismatch in getnpcpos - pos:" + buf.pos + " psize:" + size);
			throw new RuntimeException("eek");
		}

		for (int i = 0; i < this.npcCount; i++) {
			if (this.npcs[this.npcIds[i]] == null) {
				signlink.reporterror(this.username + " null entry in npc list - pos:" + i + " size:" + this.npcCount);
				throw new RuntimeException("eek");
			}
		}
	}

	@Override
	public void startThread( Runnable runnable, int priority) {
		if (signlink.mainapp != null) {
			signlink.startthread(runnable, priority);
		} else {
			super.startThread(runnable, priority);
		}
	}

	private void loadTitleImages() {
		this.imageTitlebox = new Pix8(this.archiveTitle, "titlebox", 0);
		this.imageTitlebutton = new Pix8(this.archiveTitle, "titlebutton", 0);
		this.imageRunes = new Pix8[12];
		for ( int i = 0; i < 12; i++) {
			this.imageRunes[i] = new Pix8(this.archiveTitle, "runes", i);
		}
		this.imageFlamesLeft = new Pix24(128, 265);
		this.imageFlamesRight = new Pix24(128, 265);
		System.arraycopy(this.imageTitle0.pixels, 0, this.imageFlamesLeft.pixels, 0, 33920);
		System.arraycopy(this.imageTitle1.pixels, 0, this.imageFlamesRight.pixels, 0, 33920);
		this.flameGradient0 = new int[256];
		for ( int i = 0; i < 64; i++) {
			this.flameGradient0[i] = i * 262144;
		}
		for ( int i = 0; i < 64; i++) {
			this.flameGradient0[i + 64] = i * 1024 + 16711680;
		}
		for ( int i = 0; i < 64; i++) {
			this.flameGradient0[i + 128] = i * 4 + 16776960;
		}
		for ( int i = 0; i < 64; i++) {
			this.flameGradient0[i + 192] = 16777215;
		}
		this.flameGradient1 = new int[256];
		for ( int i = 0; i < 64; i++) {
			this.flameGradient1[i] = i * 1024;
		}
		for ( int i = 0; i < 64; i++) {
			this.flameGradient1[i + 64] = i * 4 + 65280;
		}
		for ( int i = 0; i < 64; i++) {
			this.flameGradient1[i + 128] = i * 262144 + 65535;
		}
		for ( int i = 0; i < 64; i++) {
			this.flameGradient1[i + 192] = 16777215;
		}
		this.flameGradient2 = new int[256];
		for ( int i = 0; i < 64; i++) {
			this.flameGradient2[i] = i * 4;
		}
		for ( int i = 0; i < 64; i++) {
			this.flameGradient2[i + 64] = i * 262144 + 255;
		}
		for ( int i = 0; i < 64; i++) {
			this.flameGradient2[i + 128] = i * 1024 + 16711935;
		}
		for ( int i = 0; i < 64; i++) {
			this.flameGradient2[i + 192] = 16777215;
		}
		this.flameGradient = new int[256];
		this.flameBuffer0 = new int[32768];
		this.flameBuffer1 = new int[32768];
		this.updateFlameBuffer(null);
		this.flameBuffer3 = new int[32768];
		this.flameBuffer2 = new int[32768];
		this.drawProgress("Connecting to fileserver", 10);
		if (!this.flameActive) {
			this.flamesThread = true;
			this.flameActive = true;
			this.startThread(this, 2);
		}
	}

	private void readPlayers( Packet buf, int size) {
		int count = buf.gBit(8);

		if (count < this.playerCount) {
			for (int i = count; i < this.playerCount; i++) {
				this.entityRemovalIds[this.entityRemovalCount++] = this.playerIds[i];
			}
		}

		if (count > this.playerCount) {
			signlink.reporterror(this.username + " Too many players");
			throw new RuntimeException("eek");
		}

		this.playerCount = 0;
		for (int i = 0; i < count; i++) {
			int index = this.playerIds[i];
			PlayerEntity player = this.players[index];

			int hasUpdate = buf.gBit(1);
			if (hasUpdate == 0) {
				this.playerIds[this.playerCount++] = index;
				player.cycle = loopCycle;
			} else {
				int updateType = buf.gBit(2);

				if (updateType == 0) {
					this.playerIds[this.playerCount++] = index;
					player.cycle = loopCycle;
					this.entityUpdateIds[this.entityUpdateCount++] = index;
				} else if (updateType == 1) {
					this.playerIds[this.playerCount++] = index;
					player.cycle = loopCycle;

					int walkDir = buf.gBit(3);
					player.step(false, walkDir);

					int hasMaskUpdate = buf.gBit(1);
					if (hasMaskUpdate == 1) {
						this.entityUpdateIds[this.entityUpdateCount++] = index;
					}
				} else if (updateType == 2) {
					this.playerIds[this.playerCount++] = index;
					player.cycle = loopCycle;

					int walkDir = buf.gBit(3);
					player.step(true, walkDir);
					int runDir = buf.gBit(3);
					player.step(true, runDir);

					int hasMaskUpdate = buf.gBit(1);
					if (hasMaskUpdate == 1) {
						this.entityUpdateIds[this.entityUpdateCount++] = index;
					}
				} else if (updateType == 3) {
					this.entityRemovalIds[this.entityRemovalCount++] = index;
				}
			}
		}
	}

	private void drawScrollbar( int x, int y, int scrollY, int scrollHeight, int height) {
		this.imageScrollbar0.draw(x, y);
		this.imageScrollbar1.draw(x, y + height - 16);
		Draw2D.fillRect(x, y + 16, this.SCROLLBAR_TRACK, 16, height - 32);

		int gripSize = (height - 32) * height / scrollHeight;
		if (gripSize < 8) {
			gripSize = 8;
		}

		int gripY = (height - gripSize - 32) * scrollY / (scrollHeight - height);
		Draw2D.fillRect(x, y + gripY + 16, this.SCROLLBAR_GRIP_FOREGROUND, 16, gripSize);

		Draw2D.drawVerticalLine(x, y + gripY + 16, this.SCROLLBAR_GRIP_HIGHLIGHT, gripSize);
		Draw2D.drawVerticalLine(x + 1, y + gripY + 16, this.SCROLLBAR_GRIP_HIGHLIGHT, gripSize);

		Draw2D.drawHorizontalLine(x, y + gripY + 16, this.SCROLLBAR_GRIP_HIGHLIGHT, 16);
		Draw2D.drawHorizontalLine(x, y + gripY + 17, this.SCROLLBAR_GRIP_HIGHLIGHT, 16);

		Draw2D.drawVerticalLine(x + 15, y + gripY + 16, this.SCROLLBAR_GRIP_LOWLIGHT, gripSize);
		Draw2D.drawVerticalLine(x + 14, y + gripY + 17, this.SCROLLBAR_GRIP_LOWLIGHT, gripSize - 1);

		Draw2D.drawHorizontalLine(x, y + gripY + gripSize + 15, this.SCROLLBAR_GRIP_LOWLIGHT, 16);
		Draw2D.drawHorizontalLine(x + 1, y + gripY + gripSize + 14, this.SCROLLBAR_GRIP_LOWLIGHT, 15);
	}

	private void validateCharacterDesign() {
		this.updateDesignModel = true;

		for ( int i = 0; i < 7; i++) {
			this.designIdentikits[i] = -1;

			for ( int j = 0; j < IdkType.count; j++) {
				if (!IdkType.instances[j].disable && IdkType.instances[j].type == i + (this.designGenderMale ? 0 : 7)) {
					this.designIdentikits[i] = j;
					break;
				}
			}
		}
	}

	private void saveMidi( byte[] src, int length, int fade) {
		signlink.midifade = fade;
		signlink.midisave(src, length);
	}

	private void pushNpcs() {
		for ( int i = 0; i < this.npcCount; i++) {
			NpcEntity npc = this.npcs[this.npcIds[i]];
			int bitset = (this.npcIds[i] << 14) + 0x20000000;

			if (npc == null || !npc.isVisible()) {
				continue;
			}

			int x = npc.x >> 7;
			int z = npc.z >> 7;

			if (x < 0 || x >= 104 || z < 0 || z >= 104) {
				continue;
			}

			if (npc.size == 1 && (npc.x & 0x7F) == 64 && (npc.z & 0x7F) == 64) {
				if (this.tileLastOccupiedCycle[x][z] == this.sceneCycle) {
					continue;
				}

				this.tileLastOccupiedCycle[x][z] = this.sceneCycle;
			}

			this.scene.addTemporary(this.currentLevel, npc.x, this.getHeightmapY(this.currentLevel, npc.x, npc.z), npc.z, null, npc, bitset, npc.yaw, (npc.size - 1) * 64 + 60, npc.seqStretches);
		}
	}

	private void setMidiVolume( int volume) {
		signlink.midivol = volume;
		signlink.midi = "voladjust";

		switch (volume) {
			case 0:
				MidiPlayer.volume = 80;
				break;
			case -400:
				MidiPlayer.volume = 60;
				break;
			case -800:
				MidiPlayer.volume = 40;
				break;
			case -1200:
				MidiPlayer.volume = 20;
				break;
			default:
				MidiPlayer.stop();
				break;
		}
	}

	private void drawTitleScreen() {
		this.loadTitle();
		this.imageTitle4.bind();
		this.imageTitlebox.draw(0, 0);

		int w = 360;
		int h = 200;

		if (this.titleScreenState == 0) {
			int y = h / 2 - 20;
			this.fontBold12.drawStringTaggableCenter("Welcome to RuneScape", w / 2, y, 16776960, true);

			int x = w / 2 - 80;
			y = h / 2 + 20;

			this.imageTitlebutton.draw(x - 73, y - 20);
			this.fontBold12.drawStringTaggableCenter("New user", x, y + 5, 16777215, true);

			x = w / 2 + 80;
			this.imageTitlebutton.draw(x - 73, y - 20);
			this.fontBold12.drawStringTaggableCenter("Existing User", x, y + 5, 16777215, true);
		} else if (this.titleScreenState == 2) {
			int y = h / 2 - 40;
			if (this.loginMessage0.length() > 0) {
				this.fontBold12.drawStringTaggableCenter(this.loginMessage0, w / 2, y - 15, 16776960, true);
				this.fontBold12.drawStringTaggableCenter(this.loginMessage1, w / 2, y, 16776960, true);
				y += 30;
			} else {
				this.fontBold12.drawStringTaggableCenter(this.loginMessage1, w / 2, y - 7, 16776960, true);
				y += 30;
			}

			this.fontBold12.drawStringTaggable(w / 2 - 90, y, "Username: " + this.username + (this.titleLoginField == 0 & loopCycle % 40 < 20 ? "@yel@|" : ""), 16777215, true);
			y += 15;

			this.fontBold12.drawStringTaggable(w / 2 - 88, y, "Password: " + JString.toAsterisks(this.password) + (this.titleLoginField == 1 & loopCycle % 40 < 20 ? "@yel@|" : ""), 16777215, true);
			y += 15;

			int x = w / 2 - 80;
			y = h / 2 + 50;
			this.imageTitlebutton.draw(x - 73, y - 20);
			this.fontBold12.drawStringTaggableCenter("Login", x, y + 5, 16777215, true);

			x = w / 2 + 80;
			this.imageTitlebutton.draw(x - 73, y - 20);
			this.fontBold12.drawStringTaggableCenter("Cancel", x, y + 5, 16777215, true);
		} else if (this.titleScreenState == 3) {
			this.fontBold12.drawStringTaggableCenter("Create a free account", w / 2, h / 2 - 60, 16776960, true);

			int y = h / 2 - 35;
			this.fontBold12.drawStringTaggableCenter("To create a new account you need to", w / 2, y, 16777215, true);
			y += 15;

			this.fontBold12.drawStringTaggableCenter("go back to the main RuneScape webpage", w / 2, y, 16777215, true);
			y += 15;

			this.fontBold12.drawStringTaggableCenter("and choose the red 'create account'", w / 2, y, 16777215, true);
			y += 15;

			this.fontBold12.drawStringTaggableCenter("button at the top right of that page.", w / 2, y, 16777215, true);
			y += 15;

			int x = w / 2;
			y = h / 2 + 50;
			this.imageTitlebutton.draw(x - 73, y - 20);
			this.fontBold12.drawStringTaggableCenter("Cancel", x, y + 5, 16777215, true);
		}

		this.imageTitle4.draw(super.graphics, 214, 186);
		if (this.redrawTitleBackground || ALWAYS_REDRAW_TITLE) {
			this.redrawTitleBackground = false;
			this.imageTitle2.draw(super.graphics, 128, 0);
			this.imageTitle3.draw(super.graphics, 214, 386);
			this.imageTitle5.draw(super.graphics, 0, 265);
			this.imageTitle6.draw(super.graphics, 574, 265);
			this.imageTitle7.draw(super.graphics, 128, 186);
			this.imageTitle8.draw(super.graphics, 574, 186);
		}
	}

	private void prepareGameScreen() {
		if (this.areaChatback != null) {
			return;
		}

		this.unloadTitle();
		super.drawArea = null;
		this.imageTitle2 = null;
		this.imageTitle3 = null;
		this.imageTitle4 = null;
		this.imageTitle0 = null;
		this.imageTitle1 = null;
		this.imageTitle5 = null;
		this.imageTitle6 = null;
		this.imageTitle7 = null;
		this.imageTitle8 = null;
		this.areaChatback = new PixMap(this.getBaseComponent(), 479, 96);
		this.areaMapback = new PixMap(this.getBaseComponent(), 168, 160);
		Draw2D.clear();
		this.imageMapback.draw(0, 0);
		this.areaSidebar = new PixMap(this.getBaseComponent(), 190, 261);
		this.areaViewport = new PixMap(this.getBaseComponent(), 512, 334);
		Draw2D.clear();
		this.areaBackbase1 = new PixMap(this.getBaseComponent(), 501, 61);
		this.areaBackbase2 = new PixMap(this.getBaseComponent(), 288, 40);
		this.areaBackhmid1 = new PixMap(this.getBaseComponent(), 269, 66);
		this.redrawTitleBackground = true;
	}

	private void readNewPlayers( int size, Packet buf) {
		int index;
		while (buf.bitPos + 10 < size * 8) {
			index = buf.gBit(11);
			if (index == 2047) {
				break;
			}

			if (this.players[index] == null) {
				this.players[index] = new PlayerEntity();
				if (this.playerAppearanceBuffer[index] != null) {
					this.players[index].read(this.playerAppearanceBuffer[index]);
				}
			}

			this.playerIds[this.playerCount++] = index;
			PlayerEntity player = this.players[index];
			player.cycle = loopCycle;
			int dx = buf.gBit(5);
			if (dx > 15) {
				dx -= 32;
			}
			int dz = buf.gBit(5);
			if (dz > 15) {
				dz -= 32;
			}
			int jump = buf.gBit(1);
			player.move(jump == 1, this.localPlayer.pathTileX[0] + dx, this.localPlayer.pathTileZ[0] + dz);

			int hasMaskUpdate = buf.gBit(1);
			if (hasMaskUpdate == 1) {
				this.entityUpdateIds[this.entityUpdateCount++] = index;
			}
		}

		buf.accessBytes();
	}

	private void logout() {
		try {
			if (this.stream != null) {
				this.stream.close();
			}
		} catch ( Exception ignored) {
		}

		this.stream = null;
		this.ingame = false;
		this.titleScreenState = 0;
		this.username = "";
		this.password = "";

		InputTracking.setDisabled();
		this.clearCaches();
		this.scene.reset();

		for ( int level = 0; level < 4; level++) {
			this.levelCollisionMap[level].reset();
		}

		System.gc();
		this.stopMidi();
		this.currentMidi = null;
		this.nextMusicDelay = 0;
	}

	private void drawInterface( ComType com, int x, int y, int scrollY) {
		if (com.type != 0 || com.childId == null || (com.hide && this.viewportHoveredInterfaceIndex != com.id && this.sidebarHoveredInterfaceIndex != com.id && this.chatHoveredInterfaceIndex != com.id)) {
			return;
		}

		int left = Draw2D.left;
		int top = Draw2D.top;
		int right = Draw2D.right;
		int bottom = Draw2D.bottom;

		Draw2D.setBounds(y + com.height, x + com.width, y, x);
		int children = com.childId.length;

		for ( int i = 0; i < children; i++) {
			int childX = com.childX[i] + x;
			int childY = com.childY[i] + y - scrollY;

			ComType child = ComType.instances[com.childId[i]];
			childX += child.x;
			childY += child.y;

			if (child.clientCode > 0) {
				this.updateInterfaceContent(child);
			}

			if (child.type == 0) {
				if (child.scrollPosition > child.scroll - child.height) {
					child.scrollPosition = child.scroll - child.height;
				}

				if (child.scrollPosition < 0) {
					child.scrollPosition = 0;
				}

				this.drawInterface(child, childX, childY, child.scrollPosition);
				if (child.scroll > child.height) {
					this.drawScrollbar(childX + child.width, childY, child.scrollPosition, child.scroll, child.height);
				}
			} else if (child.type == 2) {
				int slot = 0;

				for (int row = 0; row < child.height; row++) {
					for (int col = 0; col < child.width; col++) {
						int slotX = childX + col * (child.marginX + 32);
						int slotY = childY + row * (child.marginY + 32);

						if (slot < 20) {
							slotX += child.invSlotOffsetX[slot];
							slotY += child.invSlotOffsetY[slot];
						}

						if (child.invSlotObjId[slot] > 0) {
							int dx = 0;
							int dy = 0;
							int id = child.invSlotObjId[slot] - 1;

							if (slotX >= -32 && slotX <= 512 && slotY >= -32 && slotY <= 334 || this.objDragArea != 0 && this.objDragSlot == slot) {
								Pix24 icon = ObjType.getIcon(id, child.invSlotObjCount[slot]);
								if (this.objDragArea != 0 && this.objDragSlot == slot && this.objDragInterfaceId == child.id) {
									dx = super.mouseX - this.objGrabX;
									dy = super.mouseY - this.objGrabY;

									if (dx < 5 && dx > -5) {
										dx = 0;
									}

									if (dy < 5 && dy > -5) {
										dy = 0;
									}

									if (this.objDragCycles < 5) {
										dx = 0;
										dy = 0;
									}

									icon.drawAlpha(128, slotX + dx, slotY + dy);
								} else if (this.selectedArea != 0 && this.selectedItem == slot && this.selectedInterface == child.id) {
									icon.drawAlpha(128, slotX, slotY);
								} else {
									icon.draw(slotX, slotY);
								}

								if (icon.cropW == 33 || child.invSlotObjCount[slot] != 1) {
									int count = child.invSlotObjCount[slot];
									this.fontPlain11.drawString(slotX + dx + 1, slotY + 10 + dy, formatObjCount(count), 0);
									this.fontPlain11.drawString(slotX + dx, slotY + 9 + dy, formatObjCount(count), 0xffff00);
								}
							}
						} else if (child.invSlotSprite != null && slot < 20) {
							Pix24 image = child.invSlotSprite[slot];

							if (image != null) {
								image.draw(slotX, slotY);
							}
						}

						slot++;
					}
				}
			} else if (child.type == 3) {
				if (child.fill) {
					Draw2D.fillRect(childX, childY, child.colour, child.width, child.height);
				} else {
					Draw2D.drawRect(childX, childY, child.colour, child.width, child.height);
				}
			} else if (child.type == 4) {
				PixFont font = child.font;
				int color = child.colour;
				String text = child.text;

				if ((this.chatHoveredInterfaceIndex == child.id || this.sidebarHoveredInterfaceIndex == child.id || this.viewportHoveredInterfaceIndex == child.id) && child.overColour != 0) {
					color = child.overColour;
				}

				if (this.executeInterfaceScript(child)) {
					color = child.activeColour;

					if (child.activeText.length() > 0) {
						text = child.activeText;
					}
				}

				if (child.buttonType == ComType.BUTTON_CONTINUE && this.pressedContinueOption) {
					text = "Please wait...";
					color = child.colour;
				}

				for (int lineY = childY + font.height; text.length() > 0; lineY += font.height) {
					if (text.indexOf("%") != -1) {
						do {
							int index = text.indexOf("%1");
							if (index == -1) {
								break;
							}

							text = text.substring(0, index) + this.getIntString(this.executeClientscript1(child, 0)) + text.substring(index + 2);
						} while (true);

						do {
							int index = text.indexOf("%2");
							if (index == -1) {
								break;
							}

							text = text.substring(0, index) + this.getIntString(this.executeClientscript1(child, 1)) + text.substring(index + 2);
						} while (true);

						do {
							int index = text.indexOf("%3");
							if (index == -1) {
								break;
							}

							text = text.substring(0, index) + this.getIntString(this.executeClientscript1(child, 2)) + text.substring(index + 2);
						} while (true);

						do {
							int index = text.indexOf("%4");
							if (index == -1) {
								break;
							}

							text = text.substring(0, index) + this.getIntString(this.executeClientscript1(child, 3)) + text.substring(index + 2);
						} while (true);

						do {
							int index = text.indexOf("%5");
							if (index == -1) {
								break;
							}

							text = text.substring(0, index) + this.getIntString(this.executeClientscript1(child, 4)) + text.substring(index + 2);
						} while (true);
					}

					int newline = text.indexOf("\\n");
					String split;
					if (newline != -1) {
						split = text.substring(0, newline);
						text = text.substring(newline + 2);
					} else {
						split = text;
						text = "";
					}

					if (child.center) {
						font.drawStringTaggableCenter(split, childX + child.width / 2, lineY, color, child.shadowed);
					} else {
						font.drawStringTaggable(childX, lineY, split, color, child.shadowed);
					}
				}
			} else if (child.type == 5) {
				Pix24 image;
				if (this.executeInterfaceScript(child)) {
					image = child.activeGraphic;
				} else {
					image = child.graphic;
				}

				if (image != null) {
					image.draw(childX, childY);
				}
			} else if (child.type == 6) {
				int tmpX = Draw3D.centerX;
				int tmpY = Draw3D.centerY;

				Draw3D.centerX = childX + child.width / 2;
				Draw3D.centerY = childY + child.height / 2;

				int eyeY = Draw3D.sin[child.xan] * child.zoom >> 16;
				int eyeZ = Draw3D.cos[child.xan] * child.zoom >> 16;

				boolean active = this.executeInterfaceScript(child);
				int seqId;
				if (active) {
					seqId = child.activeAnim;
				} else {
					seqId = child.anim;
				}

				Model model;
				if (seqId == -1) {
					model = child.getModel(-1, -1, active);
				} else {
					SeqType seq = SeqType.instances[seqId];
					model = child.getModel(seq.frames[child.seqFrame], seq.iframes[child.seqFrame], active);
				}

				if (model != null) {
					model.drawSimple(0, child.yan, 0, child.xan, 0, eyeY, eyeZ);
				}

				Draw3D.centerX = tmpX;
				Draw3D.centerY = tmpY;
			} else if (child.type == 7) {
				PixFont font = child.font;
				int slot = 0;
				for (int row = 0; row < child.height; row++) {
					for (int col = 0; col < child.width; col++) {
						if (child.invSlotObjId[slot] > 0) {
							ObjType obj = ObjType.get(child.invSlotObjId[slot] - 1);
							String text = obj.name;
							if (obj.stackable || child.invSlotObjCount[slot] != 1) {
								text = text + " x" + formatObjCountTagged(child.invSlotObjCount[slot]);
							}

							int textX = childX + col * (child.marginX + 115);
							int textY = childY + row * (child.marginY + 12);

							if (child.center) {
								font.drawStringTaggableCenter(text, textX + child.width / 2, textY, child.colour, child.shadowed);
							} else {
								font.drawStringTaggable(textX, textY, text, child.colour, child.shadowed);
							}
						}

						slot++;
					}
				}
			}
		}

		Draw2D.setBounds(bottom, right, top, left);
	}

	private void readPlayerUpdates( Packet buf, int size) {
		for ( int i = 0; i < this.entityUpdateCount; i++) {
			int index = this.entityUpdateIds[i];
			PlayerEntity player = this.players[index];
			int mask = buf.g1();
			if ((mask & 0x80) == 128) {
				mask += buf.g1() << 8;
			}
			this.readPlayerUpdates(player, index, mask, buf);
		}
	}

	private void updateVarp( int id) {
		int clientcode = VarpType.instances[id].clientcode;
		if (clientcode == 0) {
			return;
		}

		int value = this.varps[id];
		if (clientcode == 1) {
			if (value == 1) {
				Draw3D.setBrightness(0.9D);
			} else if (value == 2) {
				Draw3D.setBrightness(0.8D);
			} else if (value == 3) {
				Draw3D.setBrightness(0.7D);
			} else if (value == 4) {
				Draw3D.setBrightness(0.6D);
			}

			ObjType.iconCache.clear();
			this.redrawTitleBackground = true;
		} else if (clientcode == 3) {
			boolean lastMidiActive = this.midiActive;
			if (value == 0) {
				this.setMidiVolume(0);
				this.midiActive = true;
			} else if (value == 1) {
				this.setMidiVolume(-400);
				this.midiActive = true;
			} else if (value == 2) {
				this.setMidiVolume(-800);
				this.midiActive = true;
			} else if (value == 3) {
				this.setMidiVolume(-1200);
				this.midiActive = true;
			} else if (value == 4) {
				this.midiActive = false;
			}

			if (this.midiActive != lastMidiActive) {
				if (this.midiActive) {
					this.setMidi(this.currentMidi, this.midiCrc, this.midiSize);
				} else {
					this.stopMidi();
				}

				this.nextMusicDelay = 0;
			}
		} else if (clientcode == 4) {
			if (value == 0) {
				this.waveEnabled = true;
				this.setWaveVolume(0);
			} else if (value == 1) {
				this.waveEnabled = true;
				this.setWaveVolume(-400);
			} else if (value == 2) {
				this.waveEnabled = true;
				this.setWaveVolume(-800);
			} else if (value == 3) {
				this.waveEnabled = true;
				this.setWaveVolume(-1200);
			} else if (value == 4) {
				this.waveEnabled = false;
			}
		} else if (clientcode == 5) {
			this.mouseButtonsOption = value;
		} else if (clientcode == 6) {
			this.chatEffects = value;
		} else if (clientcode == 8) {
			this.splitPrivateChat = value;
			this.redrawChatback = true;
		}
	}

	private void updateNpcs() {
		for ( int i = 0; i < this.npcCount; i++) {
			int id = this.npcIds[i];
			NpcEntity npc = this.npcs[id];
			if (npc != null) {
				this.updateEntity(npc, npc.type.size);
			}
		}
    }

	private void updateEntity( PathingEntity entity, int size) {
		if (entity.x < 128 || entity.z < 128 || entity.x >= 13184 || entity.z >= 13184) {
			entity.primarySeqId = -1;
			entity.spotanimId = -1;
			entity.forceMoveEndCycle = 0;
			entity.forceMoveStartCycle = 0;
			entity.x = entity.pathTileX[0] * 128 + entity.size * 64;
			entity.z = entity.pathTileZ[0] * 128 + entity.size * 64;
			entity.pathLength = 0;
		}

		if (entity == this.localPlayer && (entity.x < 1536 || entity.z < 1536 || entity.x >= 11776 || entity.z >= 11776)) {
			entity.primarySeqId = -1;
			entity.spotanimId = -1;
			entity.forceMoveEndCycle = 0;
			entity.forceMoveStartCycle = 0;
			entity.x = entity.pathTileX[0] * 128 + entity.size * 64;
			entity.z = entity.pathTileZ[0] * 128 + entity.size * 64;
			entity.pathLength = 0;
		}

		if (entity.forceMoveEndCycle > loopCycle) {
			this.updateForceMovement(entity);
		} else if (entity.forceMoveStartCycle >= loopCycle) {
			this.startForceMovement(entity);
		} else {
			this.updateMovement(entity);
		}

		this.updateFacingDirection(entity);
		this.updateSequences(entity);
	}

	private void updateForceMovement( PathingEntity enttiy) {
		int delta = enttiy.forceMoveEndCycle - loopCycle;
		int dstX = enttiy.forceMoveStartSceneTileX * 128 + enttiy.size * 64;
		int dstZ = enttiy.forceMoveStartSceneTileZ * 128 + enttiy.size * 64;

		enttiy.x += (dstX - enttiy.x) / delta;
		enttiy.z += (dstZ - enttiy.z) / delta;

		enttiy.seqTrigger = 0;

		if (enttiy.forceMoveFaceDirection == 0) {
			enttiy.dstYaw = 1024;
		}

		if (enttiy.forceMoveFaceDirection == 1) {
			enttiy.dstYaw = 1536;
		}

		if (enttiy.forceMoveFaceDirection == 2) {
			enttiy.dstYaw = 0;
		}

		if (enttiy.forceMoveFaceDirection == 3) {
			enttiy.dstYaw = 512;
		}
	}

	private void startForceMovement( PathingEntity entity) {
		if (entity.forceMoveStartCycle == loopCycle || entity.primarySeqId == -1 || entity.primarySeqDelay != 0 || entity.primarySeqCycle + 1 > SeqType.instances[entity.primarySeqId].delay[entity.primarySeqFrame]) {
			int duration = entity.forceMoveStartCycle - entity.forceMoveEndCycle;
			int delta = loopCycle - entity.forceMoveEndCycle;
			int dx0 = entity.forceMoveStartSceneTileX * 128 + entity.size * 64;
			int dz0 = entity.forceMoveStartSceneTileZ * 128 + entity.size * 64;
			int dx1 = entity.forceMoveEndSceneTileX * 128 + entity.size * 64;
			int dz1 = entity.forceMoveEndSceneTileZ * 128 + entity.size * 64;
			entity.x = (dx0 * (duration - delta) + dx1 * delta) / duration;
			entity.z = (dz0 * (duration - delta) + dz1 * delta) / duration;
		}

		entity.seqTrigger = 0;

		if (entity.forceMoveFaceDirection == 0) {
			entity.dstYaw = 1024;
		}

		if (entity.forceMoveFaceDirection == 1) {
			entity.dstYaw = 1536;
		}

		if (entity.forceMoveFaceDirection == 2) {
			entity.dstYaw = 0;
		}

		if (entity.forceMoveFaceDirection == 3) {
			entity.dstYaw = 512;
		}

		entity.yaw = entity.dstYaw;
	}

	private void updateMovement( PathingEntity entity) {
		entity.secondarySeqId = entity.seqStandId;

		if (entity.pathLength == 0) {
			entity.seqTrigger = 0;
			return;
		}

		if (entity.primarySeqId != -1 && entity.primarySeqDelay == 0) {
			SeqType seq = SeqType.instances[entity.primarySeqId];
			if (seq.labelGroups == null) {
				entity.seqTrigger++;
				return;
			}
		}

		int x = entity.x;
		int z = entity.z;
		int dstX = entity.pathTileX[entity.pathLength - 1] * 128 + entity.size * 64;
		int dstZ = entity.pathTileZ[entity.pathLength - 1] * 128 + entity.size * 64;

		if (dstX - x <= 256 && dstX - x >= -256 && dstZ - z <= 256 && dstZ - z >= -256) {
			if (x < dstX) {
				if (z < dstZ) {
					entity.dstYaw = 1280;
				} else if (z > dstZ) {
					entity.dstYaw = 1792;
				} else {
					entity.dstYaw = 1536;
				}
			} else if (x > dstX) {
				if (z < dstZ) {
					entity.dstYaw = 768;
				} else if (z > dstZ) {
					entity.dstYaw = 256;
				} else {
					entity.dstYaw = 512;
				}
			} else if (z < dstZ) {
				entity.dstYaw = 1024;
			} else {
				entity.dstYaw = 0;
			}

			int deltaYaw = entity.dstYaw - entity.yaw & 0x7FF;
			if (deltaYaw > 1024) {
				deltaYaw -= 2048;
			}

			int seqId = entity.seqTurnAroundId;
			if (deltaYaw >= -256 && deltaYaw <= 256) {
				seqId = entity.seqWalkId;
			} else if (deltaYaw >= 256 && deltaYaw < 768) {
				seqId = entity.seqTurnRightId;
			} else if (deltaYaw >= -768 && deltaYaw <= -256) {
				seqId = entity.seqTurnLeftId;
			}

			if (seqId == -1) {
				seqId = entity.seqWalkId;
			}

			entity.secondarySeqId = seqId;
			int moveSpeed = 4;
			if (entity.yaw != entity.dstYaw && entity.targetId == -1) {
				moveSpeed = 2;
			}

			if (entity.pathLength > 2) {
				moveSpeed = 6;
			}

			if (entity.pathLength > 3) {
				moveSpeed = 8;
			}

			if (entity.seqTrigger > 0 && entity.pathLength > 1) {
				moveSpeed = 8;
				entity.seqTrigger--;
			}

			if (entity.pathRunning[entity.pathLength - 1]) {
				moveSpeed <<= 0x1;
			}

			if (moveSpeed >= 8 && entity.secondarySeqId == entity.seqWalkId && entity.seqRunId != -1) {
				entity.secondarySeqId = entity.seqRunId;
			}

			if (x < dstX) {
				entity.x += moveSpeed;
				if (entity.x > dstX) {
					entity.x = dstX;
				}
			} else if (x > dstX) {
				entity.x -= moveSpeed;
				if (entity.x < dstX) {
					entity.x = dstX;
				}
			}
			if (z < dstZ) {
				entity.z += moveSpeed;
				if (entity.z > dstZ) {
					entity.z = dstZ;
				}
			} else if (z > dstZ) {
				entity.z -= moveSpeed;
				if (entity.z < dstZ) {
					entity.z = dstZ;
				}
			}

			if (entity.x == dstX && entity.z == dstZ) {
				entity.pathLength--;
			}
		} else {
			entity.x = dstX;
			entity.z = dstZ;
		}
	}

	private void updateFacingDirection( PathingEntity e) {
		if (e.targetId != -1 && e.targetId < 32768) {
			NpcEntity npc = this.npcs[e.targetId];
			if (npc != null) {
				int dstX = e.x - npc.x;
				int dstZ = e.z - npc.z;

				if (dstX != 0 || dstZ != 0) {
					e.dstYaw = (int) (Math.atan2(dstX, dstZ) * 325.949D) & 0x7FF;
				}
			}
		}

		if (e.targetId >= 32768) {
			int index = e.targetId - 32768;
			if (index == this.localPid) {
				index = this.LOCAL_PLAYER_INDEX;
			}

			PlayerEntity player = this.players[index];
			if (player != null) {
				int dstX = e.x - player.x;
				int dstZ = e.z - player.z;

				if (dstX != 0 || dstZ != 0) {
					e.dstYaw = (int) (Math.atan2(dstX, dstZ) * 325.949D) & 0x7FF;
				}
			}
		}

		if ((e.targetTileX != 0 || e.targetTileZ != 0) && (e.pathLength == 0 || e.seqTrigger > 0)) {
			int dstX = e.x - (e.targetTileX - this.sceneBaseTileX - this.sceneBaseTileX) * 64;
			int dstZ = e.z - (e.targetTileZ - this.sceneBaseTileZ - this.sceneBaseTileZ) * 64;

			if (dstX != 0 || dstZ != 0) {
				e.dstYaw = (int) (Math.atan2(dstX, dstZ) * 325.949D) & 0x7FF;
			}

			e.targetTileX = 0;
			e.targetTileZ = 0;
		}

		int remainingYaw = e.dstYaw - e.yaw & 0x7FF;

		if (remainingYaw != 0) {
			if (remainingYaw < 32 || remainingYaw > 2016) {
				e.yaw = e.dstYaw;
			} else if (remainingYaw > 1024) {
				e.yaw -= 32;
			} else {
				e.yaw += 32;
			}

			e.yaw &= 0x7ff;

			if (e.secondarySeqId == e.seqStandId && e.yaw != e.dstYaw) {
				if (e.seqTurnId != -1) {
					e.secondarySeqId = e.seqTurnId;
					return;
				}

				e.secondarySeqId = e.seqWalkId;
			}
		}
	}

	private void updateSequences( PathingEntity e) {
		e.seqStretches = false;

		SeqType seq;
		if (e.secondarySeqId != -1) {
			seq = SeqType.instances[e.secondarySeqId];
			e.secondarySeqCycle++;
			if (e.secondarySeqFrame < seq.frameCount && e.secondarySeqCycle > seq.delay[e.secondarySeqFrame]) {
				e.secondarySeqCycle = 0;
				e.secondarySeqFrame++;
			}
			if (e.secondarySeqFrame >= seq.frameCount) {
				e.secondarySeqCycle = 0;
				e.secondarySeqFrame = 0;
			}
		}

		if (e.primarySeqId != -1 && e.primarySeqDelay == 0) {
			seq = SeqType.instances[e.primarySeqId];
			e.primarySeqCycle++;
			while (e.primarySeqFrame < seq.frameCount && e.primarySeqCycle > seq.delay[e.primarySeqFrame]) {
				e.primarySeqCycle -= seq.delay[e.primarySeqFrame];
				e.primarySeqFrame++;
			}

			if (e.primarySeqFrame >= seq.frameCount) {
				e.primarySeqFrame -= seq.replayoff;
				e.primarySeqLoop++;
				if (e.primarySeqLoop >= seq.replaycount) {
					e.primarySeqId = -1;
				}
				if (e.primarySeqFrame < 0 || e.primarySeqFrame >= seq.frameCount) {
					e.primarySeqId = -1;
				}
			}

			e.seqStretches = seq.stretches;
		}

		if (e.primarySeqDelay > 0) {
			e.primarySeqDelay--;
		}

		if (e.spotanimId != -1 && loopCycle >= e.spotanimLastCycle) {
			if (e.spotanimFrame < 0) {
				e.spotanimFrame = 0;
			}

			seq = SpotAnimType.instances[e.spotanimId].seq;
			e.spotanimCycle++;
			while (e.spotanimFrame < seq.frameCount && e.spotanimCycle > seq.delay[e.spotanimFrame]) {
				e.spotanimCycle -= seq.delay[e.spotanimFrame];
				e.spotanimFrame++;
			}

			if (e.spotanimFrame >= seq.frameCount) {
				if (e.spotanimFrame < 0 || e.spotanimFrame >= seq.frameCount) {
					e.spotanimId = -1;
				}
			}
		}
	}

	private void drawGame() {
		if (this.redrawTitleBackground || ALWAYS_REDRAW_TITLE) {
			this.redrawTitleBackground = false;
			this.areaBackleft1.draw(super.graphics, 0, 11);
			this.areaBackleft2.draw(super.graphics, 0, 375);
			this.areaBackright1.draw(super.graphics, 729, 5);
			this.areaBackright2.draw(super.graphics, 752, 231);
			this.areaBacktop1.draw(super.graphics, 0, 0);
			this.areaBacktop2.draw(super.graphics, 561, 0);
			this.areaBackvmid1.draw(super.graphics, 520, 11);
			this.areaBackvmid2.draw(super.graphics, 520, 231);
			this.areaBackvmid3.draw(super.graphics, 501, 375);
			this.areaBackhmid2.draw(super.graphics, 0, 345);
			this.redrawSidebar = true;
			this.redrawChatback = true;
			this.redrawSideicons = true;
			this.redrawPrivacySettings = true;
			if (this.sceneState != 2) {
				this.areaViewport.draw(super.graphics, 8, 11);
				this.areaMapback.draw(super.graphics, 561, 5);
			}
		}

		if (this.sceneState == 2) {
			this.drawScene();
		}

		if (this.menuVisible && this.menuArea == 1) {
			this.redrawSidebar = true;
		}

		boolean redraw;
		if (this.sidebarInterfaceId != -1) {
			redraw = this.updateInterfaceAnimation(this.sidebarInterfaceId, this.sceneDelta);
			if (redraw) {
				this.redrawSidebar = true;
			}
		}

		if (this.selectedArea == 2) {
			this.redrawSidebar = true;
		}

		if (this.objDragArea == 2) {
			this.redrawSidebar = true;
		}

		if (this.redrawSidebar || ALWAYS_REDRAW_GAME_FRAME) {
			this.drawSidebar();
			this.redrawSidebar = false;
		}

		if (this.chatInterfaceId == -1) {
			this.chatInterface.scrollPosition = this.chatScrollHeight - this.chatScrollOffset - 77;
			if (super.mouseX > 453 && super.mouseX < 565 && super.mouseY > 350) {
				this.handleScrollInput(super.mouseX - 22, super.mouseY - 375, this.chatScrollHeight, 77, false, 463, 0, this.chatInterface);
			}

			int offset = this.chatScrollHeight - this.chatInterface.scrollPosition - 77;
			if (offset < 0) {
				offset = 0;
			}

			if (offset > this.chatScrollHeight - 77) {
				offset = this.chatScrollHeight - 77;
			}

			if (this.chatScrollOffset != offset) {
				this.chatScrollOffset = offset;
				this.redrawChatback = true;
			}
		}

		if (this.chatInterfaceId != -1) {
			redraw = this.updateInterfaceAnimation(this.chatInterfaceId, this.sceneDelta);
			if (redraw) {
				this.redrawChatback = true;
			}
		}

		if (this.selectedArea == 3) {
			this.redrawChatback = true;
		}

		if (this.objDragArea == 3) {
			this.redrawChatback = true;
		}

		if (this.modalMessage != null) {
			this.redrawChatback = true;
		}

		if (this.menuVisible && this.menuArea == 2) {
			this.redrawChatback = true;
		}

		if (this.redrawChatback || ALWAYS_REDRAW_CHAT_BACK) {
			this.drawChatback();
			this.redrawChatback = false;
		}

		if (this.sceneState == 2 || ALWAYS_REDRAW_MINIMAP) {
			this.drawMinimap();
			this.areaMapback.draw(super.graphics, 561, 5);
		}

		if (this.flashingTab != -1) {
			this.redrawSideicons = true;
		}

		if (this.redrawSideicons || ALWAYS_REDRAW_SIDE_ICONS) {
			if (this.flashingTab != -1 && this.flashingTab == this.selectedTab) {
				this.flashingTab = -1;
				// TUTORIAL_CLICKSIDE
				this.out.p1isaac(175);
				this.out.p1(this.selectedTab);
			}

			this.redrawSideicons = false;
			this.areaBackhmid1.bind();
			this.imageBackhmid1.draw(0, 0);

			if (this.sidebarInterfaceId == -1) {
				if (this.tabInterfaceId[this.selectedTab] != -1) {
					if (this.selectedTab == 0) {
						this.imageRedstone1.draw(29, 30);
					} else if (this.selectedTab == 1) {
						this.imageRedstone2.draw(59, 29);
					} else if (this.selectedTab == 2) {
						this.imageRedstone2.draw(87, 29);
					} else if (this.selectedTab == 3) {
						this.imageRedstone3.draw(115, 29);
					} else if (this.selectedTab == 4) {
						this.imageRedstone2h.draw(156, 29);
					} else if (this.selectedTab == 5) {
						this.imageRedstone2h.draw(184, 29);
					} else if (this.selectedTab == 6) {
						this.imageRedstone1h.draw(212, 30);
					}
				}

				if (this.tabInterfaceId[0] != -1 && (this.flashingTab != 0 || loopCycle % 20 < 10)) {
					this.imageSideicons[0].draw(35, 34);
				}

				if (this.tabInterfaceId[1] != -1 && (this.flashingTab != 1 || loopCycle % 20 < 10)) {
					this.imageSideicons[1].draw(59, 32);
				}

				if (this.tabInterfaceId[2] != -1 && (this.flashingTab != 2 || loopCycle % 20 < 10)) {
					this.imageSideicons[2].draw(86, 32);
				}

				if (this.tabInterfaceId[3] != -1 && (this.flashingTab != 3 || loopCycle % 20 < 10)) {
					this.imageSideicons[3].draw(121, 33);
				}

				if (this.tabInterfaceId[4] != -1 && (this.flashingTab != 4 || loopCycle % 20 < 10)) {
					this.imageSideicons[4].draw(157, 34);
				}

				if (this.tabInterfaceId[5] != -1 && (this.flashingTab != 5 || loopCycle % 20 < 10)) {
					this.imageSideicons[5].draw(185, 32);
				}

				if (this.tabInterfaceId[6] != -1 && (this.flashingTab != 6 || loopCycle % 20 < 10)) {
					this.imageSideicons[6].draw(212, 34);
				}
			}

			this.areaBackhmid1.draw(super.graphics, 520, 165);
			this.areaBackbase2.bind();
			this.imageBackbase2.draw(0, 0);

			if (this.sidebarInterfaceId == -1) {
				if (this.tabInterfaceId[this.selectedTab] != -1) {
					if (this.selectedTab == 7) {
						this.imageRedstone1v.draw(49, 0);
					} else if (this.selectedTab == 8) {
						this.imageRedstone2v.draw(81, 0);
					} else if (this.selectedTab == 9) {
						this.imageRedstone2v.draw(108, 0);
					} else if (this.selectedTab == 10) {
						this.imageRedstone3v.draw(136, 1);
					} else if (this.selectedTab == 11) {
						this.imageRedstone2hv.draw(178, 0);
					} else if (this.selectedTab == 12) {
						this.imageRedstone2hv.draw(205, 0);
					} else if (this.selectedTab == 13) {
						this.imageRedstone1hv.draw(233, 0);
					}
				}

				if (this.tabInterfaceId[8] != -1 && (this.flashingTab != 8 || loopCycle % 20 < 10)) {
					this.imageSideicons[7].draw(80, 2);
				}

				if (this.tabInterfaceId[9] != -1 && (this.flashingTab != 9 || loopCycle % 20 < 10)) {
					this.imageSideicons[8].draw(107, 3);
				}

				if (this.tabInterfaceId[10] != -1 && (this.flashingTab != 10 || loopCycle % 20 < 10)) {
					this.imageSideicons[9].draw(142, 4);
				}

				if (this.tabInterfaceId[11] != -1 && (this.flashingTab != 11 || loopCycle % 20 < 10)) {
					this.imageSideicons[10].draw(179, 2);
				}

				if (this.tabInterfaceId[12] != -1 && (this.flashingTab != 12 || loopCycle % 20 < 10)) {
					this.imageSideicons[11].draw(206, 2);
				}

				if (this.tabInterfaceId[13] != -1 && (this.flashingTab != 13 || loopCycle % 20 < 10)) {
					this.imageSideicons[12].draw(230, 2);
				}
			}
			this.areaBackbase2.draw(super.graphics, 501, 492);
			this.areaViewport.bind();
		}

		if (this.redrawPrivacySettings || ALWAYS_REDRAW_PRIVACY_SETTINGS) {
			this.redrawPrivacySettings = false;
			this.areaBackbase1.bind();
			this.imageBackbase1.draw(0, 0);

			this.fontPlain12.drawStringTaggableCenter("Public chat", 57, 33, 16777215, true);
			if (this.publicChatSetting == 0) {
				this.fontPlain12.drawStringTaggableCenter("On", 57, 46, 65280, true);
			}
			if (this.publicChatSetting == 1) {
				this.fontPlain12.drawStringTaggableCenter("Friends", 57, 46, 16776960, true);
			}
			if (this.publicChatSetting == 2) {
				this.fontPlain12.drawStringTaggableCenter("Off", 57, 46, 16711680, true);
			}
			if (this.publicChatSetting == 3) {
				this.fontPlain12.drawStringTaggableCenter("Hide", 57, 46, 65535, true);
			}

			this.fontPlain12.drawStringTaggableCenter("Private chat", 186, 33, 16777215, true);
			if (this.privateChatSetting == 0) {
				this.fontPlain12.drawStringTaggableCenter("On", 186, 46, 65280, true);
			}
			if (this.privateChatSetting == 1) {
				this.fontPlain12.drawStringTaggableCenter("Friends", 186, 46, 16776960, true);
			}
			if (this.privateChatSetting == 2) {
				this.fontPlain12.drawStringTaggableCenter("Off", 186, 46, 16711680, true);
			}

			this.fontPlain12.drawStringTaggableCenter("Trade/duel", 326, 33, 16777215, true);
			if (this.tradeChatSetting == 0) {
				this.fontPlain12.drawStringTaggableCenter("On", 326, 46, 65280, true);
			}
			if (this.tradeChatSetting == 1) {
				this.fontPlain12.drawStringTaggableCenter("Friends", 326, 46, 16776960, true);
			}
			if (this.tradeChatSetting == 2) {
				this.fontPlain12.drawStringTaggableCenter("Off", 326, 46, 16711680, true);
			}

			this.fontPlain12.drawStringTaggableCenter("Report abuse", 462, 38, 16777215, true);
			this.areaBackbase1.draw(super.graphics, 0, 471);
			this.areaViewport.bind();
		}

		this.sceneDelta = 0;
	}

	private boolean isAddFriendOption( int option) {
		if (option < 0) {
			return false;
		}

		int action = this.menuAction[option];
		if (action >= 2000) {
			action -= 2000;
		}
		return action == 406;
	}

	private void useMenuOption( int optionId) {
		if (optionId < 0) {
			return;
		}

		if (this.chatbackInputOpen) {
			this.chatbackInputOpen = false;
			this.redrawChatback = true;
		}

		int action = this.menuAction[optionId];
		int a = this.menuParamA[optionId];
		int b = this.menuParamB[optionId];
		int c = this.menuParamC[optionId];

		if (action >= 2000) {
			action -= 2000;
		}

		if (action == 903 || action == 363) {
			String option = this.menuOption[optionId];
			int tag = option.indexOf("@whi@");

			if (tag != -1) {
				option = option.substring(tag + 5).trim();
				String name = JString.formatName(JString.fromBase37(JString.toBase37(option)));
				boolean found = false;

				for (int i = 0; i < this.playerCount; i++) {
					PlayerEntity player = this.players[this.playerIds[i]];

					if (player != null && player.name != null && player.name.equalsIgnoreCase(name)) {
						this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], player.pathTileX[0], player.pathTileZ[0], 2, 1, 1, 0, 0, 0, false);

						if (action == 903) {
							// OPPLAYER4
							this.out.p1isaac(206);
						} else if (action == 363) {
							// OPPLAYER1
							this.out.p1isaac(164);
						}

						this.out.p2(this.playerIds[i]);
						found = true;
						break;
					}
				}

				if (!found) {
					this.addMessage(0, "Unable to find " + name, "");
				}
			}
		} else if (action == 450) {
			// OPLOCU
			if (this.interactWithLoc(75, b, c, a)) {
				this.out.p2(this.objInterface);
				this.out.p2(this.objSelectedSlot);
				this.out.p2(this.objSelectedInterface);
			}
		} else if (action == 405 || action == 38 || action == 422 || action == 478 || action == 347) {
			if (action == 478) {
				if ((b & 0x3) == 0) {
					opHeld1Counter++;
				}

				if (opHeld1Counter >= 90) {
					// ANTICHEAT_OPLOGIC5
					this.out.p1isaac(220);
				}

				// OPHELD4
				this.out.p1isaac(157);
			} else if (action == 347) {
				// OPHELD5
				this.out.p1isaac(211);
			} else if (action == 422) {
				// OPHELD3
				this.out.p1isaac(133);
			} else if (action == 405) {
				opHeld4Counter += a;
				if (opHeld4Counter >= 97) {
					// ANTICHEAT_OPLOGIC3
					this.out.p1isaac(30);
					this.out.p3(14953816);
				}

				// OPHELD1
				this.out.p1isaac(195);
			} else if (action == 38) {
				// OPHELD2
				this.out.p1isaac(71);
			}

			this.out.p2(a);
			this.out.p2(b);
			this.out.p2(c);
			this.selectedCycle = 0;
			this.selectedInterface = c;
			this.selectedItem = b;
			this.selectedArea = 2;

			if (ComType.instances[c].layer == this.viewportInterfaceId) {
				this.selectedArea = 1;
			}

			if (ComType.instances[c].layer == this.chatInterfaceId) {
				this.selectedArea = 3;
			}
		} else if (action == 728 || action == 542 || action == 6 || action == 963 || action == 245) {
			NpcEntity npc = this.npcs[a];
			if (npc != null) {
				this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], npc.pathTileX[0], npc.pathTileZ[0], 2, 1, 1, 0, 0, 0, false);

				this.crossX = super.mouseClickX;
				this.crossY = super.mouseClickY;
				this.crossMode = 2;
				this.crossCycle = 0;

				if (action == 542) {
					// OPNPC2
					this.out.p1isaac(8);
				} else if (action == 6) {
					if ((a & 0x3) == 0) {
						opNpc3Counter++;
					}

					if (opNpc3Counter >= 124) {
						// ANTICHEAT_OPLOGIC2
						this.out.p1isaac(88);
						this.out.p4(0);
					}

					// OPNPC3
					this.out.p1isaac(27);
				} else if (action == 963) {
					// OPNPC4
					this.out.p1isaac(113);
				} else if (action == 728) {
					// OPNPC1
					this.out.p1isaac(194);
				} else if (action == 245) {
					if ((a & 0x3) == 0) {
						opNpc5Counter++;
					}

					if (opNpc5Counter >= 85) {
						// ANTICHEAT_OPLOGIC4
						this.out.p1isaac(176);
						this.out.p2(39596);
					}

					// OPNPC5
					this.out.p1isaac(100);
				}

				this.out.p2(a);
			}
		} else if (action == 217) {
			boolean success = this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], b, c, 2, 0, 0, 0, 0, 0, false);
			if (!success) {
				this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], b, c, 2, 1, 1, 0, 0, 0, false);
			}

			this.crossX = super.mouseClickX;
			this.crossY = super.mouseClickY;
			this.crossMode = 2;
			this.crossCycle = 0;

			// OPOBJU
			this.out.p1isaac(239);
			this.out.p2(b + this.sceneBaseTileX);
			this.out.p2(c + this.sceneBaseTileZ);
			this.out.p2(a);
			this.out.p2(this.objInterface);
			this.out.p2(this.objSelectedSlot);
			this.out.p2(this.objSelectedInterface);
		} else if (action == 1175) {
			int locId = a >> 14 & 0x7FFF;
			LocType loc = LocType.get(locId);

			String examine;
			if (loc.desc == null) {
				examine = "It's a " + loc.name + ".";
			} else {
				examine = loc.desc;
			}

			this.addMessage(0, examine, "");
		} else if (action == 285) {
			// OPLOC1
			this.interactWithLoc(245, b, c, a);
		} else if (action == 881) {
			// OPHELDU
			this.out.p1isaac(130);
			this.out.p2(a);
			this.out.p2(b);
			this.out.p2(c);
			this.out.p2(this.objInterface);
			this.out.p2(this.objSelectedSlot);
			this.out.p2(this.objSelectedInterface);

			this.selectedCycle = 0;
			this.selectedInterface = c;
			this.selectedItem = b;
			this.selectedArea = 2;

			if (ComType.instances[c].layer == this.viewportInterfaceId) {
				this.selectedArea = 1;
			}

			if (ComType.instances[c].layer == this.chatInterfaceId) {
				this.selectedArea = 3;
			}
		} else if (action == 391) {
			// OPHELDT
			this.out.p1isaac(48);
			this.out.p2(a);
			this.out.p2(b);
			this.out.p2(c);
			this.out.p2(this.activeSpellId);

			this.selectedCycle = 0;
			this.selectedInterface = c;
			this.selectedItem = b;
			this.selectedArea = 2;

			if (ComType.instances[c].layer == this.viewportInterfaceId) {
				this.selectedArea = 1;
			}

			if (ComType.instances[c].layer == this.chatInterfaceId) {
				this.selectedArea = 3;
			}
		} else if (action == 660) {
			if (this.menuVisible) {
				this.scene.click(b - 8, c - 11);
			} else {
				this.scene.click(super.mouseClickX - 8, super.mouseClickY - 11);
			}
		} else if (action == 188) {
			this.objSelected = 1;
			this.objSelectedSlot = b;
			this.objSelectedInterface = c;
			this.objInterface = a;
			this.objSelectedName = ObjType.get(a).name;
			this.spellSelected = 0;
			return;
		} else if (action == 44) {
			if (!this.pressedContinueOption) {
				// RESUME_PAUSEBUTTON
				this.out.p1isaac(235);
				this.out.p2(c);
				this.pressedContinueOption = true;
			}
		} else if (action == 1773) {
			ObjType obj = ObjType.get(a);
			String examine;

			if (c >= 100000) {
				examine = c + " x " + obj.name;
			} else if (obj.desc == null) {
				examine = "It's a " + obj.name + ".";
			} else {
				examine = obj.desc;
			}

			this.addMessage(0, examine, "");
		} else if (action == 900) {
			NpcEntity npc = this.npcs[a];

			if (npc != null) {
				this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], npc.pathTileX[0], npc.pathTileZ[0], 2, 1, 1, 0, 0, 0, false);
				this.crossX = super.mouseClickX;
				this.crossY = super.mouseClickY;
				this.crossMode = 2;
				this.crossCycle = 0;
				// OPNPCU
				this.out.p1isaac(202);
				this.out.p2(a);
				this.out.p2(this.objInterface);
				this.out.p2(this.objSelectedSlot);
				this.out.p2(this.objSelectedInterface);
			}
		} else if (action == 1373 || action == 1544 || action == 151 || action == 1101) {
			PlayerEntity player = this.players[a];
			if (player != null) {
				this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], player.pathTileX[0], player.pathTileZ[0], 2, 1, 1, 0, 0, 0, false);

				this.crossX = super.mouseClickX;
				this.crossY = super.mouseClickY;
				this.crossMode = 2;
				this.crossCycle = 0;

				if (action == 1101) {
					// OPPLAYER1
					this.out.p1isaac(164);
				} else if (action == 151) {
					opPlayer2Counter++;
					if (opPlayer2Counter >= 90) {
						// ANTICHEAT_OPLOGIC8
						this.out.p1isaac(2);
						this.out.p2(31114);
					}

					// OPPLAYER2
					this.out.p1isaac(53);
				} else if (action == 1373) {
					// OPPLAYER4
					this.out.p1isaac(206);
				} else if (action == 1544) {
					// OPPLAYER3
					this.out.p1isaac(185);
				}

				this.out.p2(a);
			}
		} else if (action == 265) {
			NpcEntity npc = this.npcs[a];
			if (npc != null) {
				this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], npc.pathTileX[0], npc.pathTileZ[0], 2, 1, 1, 0, 0, 0, false);

				this.crossX = super.mouseClickX;
				this.crossY = super.mouseClickY;
				this.crossMode = 2;
				this.crossCycle = 0;

				// OPNPCT
				this.out.p1isaac(134);
				this.out.p2(a);
				this.out.p2(this.activeSpellId);
			}
		} else if (action == 679) {
			String option = this.menuOption[optionId];
			int tag = option.indexOf("@whi@");

			if (tag != -1) {
				long name37 = JString.toBase37(option.substring(tag + 5).trim());
				int friend = -1;
				for ( int i = 0; i < this.friendCount; i++) {
					if (this.friendName37[i] == name37) {
						friend = i;
						break;
					}
				}

				if (friend != -1 && this.friendWorld[friend] > 0) {
					this.redrawChatback = true;
					this.chatbackInputOpen = false;
					this.showSocialInput = true;
					this.socialInput = "";
					this.socialAction = 3;
					this.socialName37 = this.friendName37[friend];
					this.socialMessage = "Enter message to send to " + this.friendName[friend];
				}
			}
		} else if (action == 55) {
			// OPLOCT
			if (this.interactWithLoc(9, b, c, a)) {
				this.out.p2(this.activeSpellId);
			}
		} else if (action == 224 || action == 993 || action == 99 || action == 746 || action == 877) {
			boolean success = this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], b, c, 2, 0, 0, 0, 0, 0, false);
			if (!success) {
				this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], b, c, 2, 1, 1, 0, 0, 0, false);
			}

			this.crossX = super.mouseClickX;
			this.crossY = super.mouseClickY;
			this.crossMode = 2;
			this.crossCycle = 0;

			if (action == 224) {
				// OPOBJ1
				this.out.p1isaac(140);
			} else if (action == 746) {
				// OPOBJ4
				this.out.p1isaac(178);
			} else if (action == 877) {
				// OPOBJ5
				this.out.p1isaac(247);
			} else if (action == 99) {
				// OPOBJ3
				this.out.p1isaac(200);
			} else if (action == 993) {
				// OPOBJ2
				this.out.p1isaac(40);
			}

			this.out.p2(b + this.sceneBaseTileX);
			this.out.p2(c + this.sceneBaseTileZ);
			this.out.p2(a);
		} else if (action == 1607) {
			NpcEntity npc = this.npcs[a];
			if (npc != null) {
				String examine;

				if (npc.type.desc == null) {
					examine = "It's a " + npc.type.name + ".";
				} else {
					examine = npc.type.desc;
				}

				this.addMessage(0, examine, "");
			}
		} else if (action == 504) {
			// OPLOC2
			this.interactWithLoc(172, b, c, a);
		} else if (action == 930) {
			ComType com = ComType.instances[c];
			this.spellSelected = 1;
			this.activeSpellId = c;
			this.activeSpellFlags = com.actionTarget;
			this.objSelected = 0;

			String prefix = com.actionVerb;
			if (prefix.indexOf(" ") != -1) {
				prefix = prefix.substring(0, prefix.indexOf(" "));
			}

			String suffix = com.actionVerb;
			if (suffix.indexOf(" ") != -1) {
				suffix = suffix.substring(suffix.indexOf(" ") + 1);
			}

			this.spellCaption = prefix + " " + com.action + " " + suffix;
			if (this.activeSpellFlags == 16) {
				this.redrawSidebar = true;
				this.selectedTab = 3;
				this.redrawSideicons = true;
			}

			return;
		} else if (action == 951) {
			ComType com = ComType.instances[c];
			boolean notify = true;

			if (com.clientCode > 0) {
				notify = this.handleInterfaceAction(com);
			}

			if (notify) {
				// IF_BUTTON
				this.out.p1isaac(155);
				this.out.p2(c);
			}
		} else if (action == 602 || action == 596 || action == 22 || action == 892 || action == 415) {
			if (action == 22) {
				// INV_BUTTON3
				this.out.p1isaac(212);
			} else if (action == 415) {
				if ((c & 0x3) == 0) {
					ifButton5Counter++;
				}

				if (ifButton5Counter >= 55) {
					// ANTICHEAT_OPLOGIC7
					this.out.p1isaac(17);
					this.out.p4(0);
				}

				// INV_BUTTON5
				this.out.p1isaac(6);
			} else if (action == 602) {
				// INV_BUTTON1
				this.out.p1isaac(31);
			} else if (action == 892) {
				if ((b & 0x3) == 0) {
					opHeld9Counter++;
				}

				if (opHeld9Counter >= 130) {
					// ANTICHEAT_OPLOGIC9
					this.out.p1isaac(238);
					this.out.p1(177);
				}

				// INV_BUTTON4
				this.out.p1isaac(38);
			} else if (action == 596) {
				// INV_BUTTON2
				this.out.p1isaac(59);
			}

			this.out.p2(a);
			this.out.p2(b);
			this.out.p2(c);

			this.selectedCycle = 0;
			this.selectedInterface = c;
			this.selectedItem = b;
			this.selectedArea = 2;

			if (ComType.instances[c].layer == this.viewportInterfaceId) {
				this.selectedArea = 1;
			}

			if (ComType.instances[c].layer == this.chatInterfaceId) {
				this.selectedArea = 3;
			}
		} else if (action == 581) {
			if ((a & 0x3) == 0) {
				opLoc4Counter++;
			}

			if (opLoc4Counter >= 99) {
				// ANTICHEAT_OPLOGIC1
				this.out.p1isaac(7);
				this.out.p4(0);
			}

			// OPLOC4
			this.interactWithLoc(97, b, c, a);
		} else if (action == 965) {
			boolean success = this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], b, c, 2, 0, 0, 0, 0, 0, false);
			if (!success) {
				this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], b, c, 2, 1, 1, 0, 0, 0, false);
			}

			this.crossX = super.mouseClickX;
			this.crossY = super.mouseClickY;
			this.crossMode = 2;
			this.crossCycle = 0;

			// OPOBJT
			this.out.p1isaac(138);
			this.out.p2(b + this.sceneBaseTileX);
			this.out.p2(c + this.sceneBaseTileZ);
			this.out.p2(a);
			this.out.p2(this.activeSpellId);
		} else if (action == 1501) {
			opLoc5Counter += this.sceneBaseTileZ;
			if (opLoc5Counter >= 92) {
				// ANTICHEAT_OPLOGIC6
				this.out.p1isaac(66);
				this.out.p4(0);
			}

			// OPLOC5
			this.interactWithLoc(116, b, c, a);
		} else if (action == 364) {
			// OPLOC3
			this.interactWithLoc(96, b, c, a);
		} else if (action == 1102) {
			ObjType obj = ObjType.get(a);
			String examine;

			if (obj.desc == null) {
				examine = "It's a " + obj.name + ".";
			} else {
				examine = obj.desc;
			}
			this.addMessage(0, examine, "");
		} else if (action == 960) {
			// IF_BUTTON
			this.out.p1isaac(155);
			this.out.p2(c);

			ComType com = ComType.instances[c];
			if (com.scripts != null && com.scripts[0][0] == 5) {
				int varp = com.scripts[0][1];
				if (this.varps[varp] != com.scriptOperand[0]) {
					this.varps[varp] = com.scriptOperand[0];
					this.updateVarp(varp);
					this.redrawSidebar = true;
				}
			}
		} else if (action == 34) {
			String option = this.menuOption[optionId];
			int tag = option.indexOf("@whi@");

			if (tag != -1) {
				this.closeInterfaces();

				this.reportAbuseInput = option.substring(tag + 5).trim();
				this.reportAbuseMuteOption = false;

				for ( int i = 0; i < ComType.instances.length; i++) {
					if (ComType.instances[i] != null && ComType.instances[i].clientCode == 600) {
						this.reportAbuseInterfaceID = this.viewportInterfaceId = ComType.instances[i].layer;
						break;
					}
				}
			}
		} else if (action == 947) {
			this.closeInterfaces();
		} else if (action == 367) {
			PlayerEntity player = this.players[a];
			if (player != null) {
				this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], player.pathTileX[0], player.pathTileZ[0], 2, 1, 1, 0, 0, 0, false);

				this.crossX = super.mouseClickX;
				this.crossY = super.mouseClickY;
				this.crossMode = 2;
				this.crossCycle = 0;

				// OPPLAYERU
				this.out.p1isaac(248);
				this.out.p2(a);
				this.out.p2(this.objInterface);
				this.out.p2(this.objSelectedSlot);
				this.out.p2(this.objSelectedInterface);
			}
		} else if (action == 465) {
			// IF_BUTTON
			this.out.p1isaac(155);
			this.out.p2(c);

			ComType com = ComType.instances[c];
			if (com.scripts != null && com.scripts[0][0] == 5) {
				int varp = com.scripts[0][1];
				this.varps[varp] = 1 - this.varps[varp];
				this.updateVarp(varp);
				this.redrawSidebar = true;
			}
		} else if (action == 406 || action == 436 || action == 557 || action == 556) {
			String option = this.menuOption[optionId];
			int tag = option.indexOf("@whi@");

			if (tag != -1) {
				long username = JString.toBase37(option.substring(tag + 5).trim());
				if (action == 406) {
					this.addFriend(username);
				} else if (action == 436) {
					this.addIgnore(username);
				} else if (action == 557) {
					this.removeFriend(username);
				} else if (action == 556) {
					this.removeIgnore(username);
				}
			}
		} else if (action == 651) {
			PlayerEntity player = this.players[a];

			if (player != null) {
				this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], player.pathTileX[0], player.pathTileZ[0], 2, 1, 1, 0, 0, 0, false);

				this.crossX = super.mouseClickX;
				this.crossY = super.mouseClickY;
				this.crossMode = 2;
				this.crossCycle = 0;

				// OPPLAYERT
				this.out.p1isaac(177);
				this.out.p2(a);
				this.out.p2(this.activeSpellId);
			}
		}

		this.objSelected = 0;
		this.spellSelected = 0;
	}

	private String getHost() {
		if (signlink.mainapp != null) {
			return signlink.mainapp.getDocumentBase().getHost().toLowerCase();
		}

		if (super.frame != null) {
			return "2004scape.org";
		}

		return super.getDocumentBase().getHost().toLowerCase();
	}

	private void drawMenu() {
		int x = this.menuX;
		int y = this.menuY;
		int w = this.menuWidth;
		int h = this.menuHeight;
		int background = 0x5d5447;
		Draw2D.fillRect(x, y, background, w, h);
		Draw2D.fillRect(x + 1, y + 1, 0, w - 2, 16);
		Draw2D.drawRect(x + 1, y + 18, 0, w - 2, h - 19);

		this.fontBold12.drawString(x + 3, y + 14, "Choose Option", background);
		int mouseX = super.mouseX;
		int mouseY = super.mouseY;
		if (this.menuArea == 0) {
			mouseX -= 8;
			mouseY -= 11;
		}
		if (this.menuArea == 1) {
			mouseX -= 562;
			mouseY -= 231;
		}
		if (this.menuArea == 2) {
			mouseX -= 22;
			mouseY -= 375;
		}

		for ( int i = 0; i < this.menuSize; i++) {
			int optionY = y + (this.menuSize - 1 - i) * 15 + 31;
			int rgb = 0xffffff;
			if (mouseX > x && mouseX < x + w && mouseY > optionY - 13 && mouseY < optionY + 3) {
				rgb = 0xffff00;
			}
			this.fontBold12.drawStringTaggable(x + 3, optionY, this.menuOption[i], rgb, true);
		}
	}

	private void handlePrivateChatInput( int mouseX, int mouseY) {
		if (this.splitPrivateChat == 0) {
			return;
		}

		int lineOffset = 0;
		if (this.systemUpdateTimer != 0) {
			lineOffset = 1;
		}

		for ( int i = 0; i < 100; i++) {
			if (this.messageText[i] != null) {
				int type = this.messageType[i];
				if ((type == 3 || type == 7) && (type == 7 || this.privateChatSetting == 0 || this.privateChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
					int y = 329 - lineOffset * 13;
					if (super.mouseX > 8 && super.mouseX < 520 && mouseY - 11 > y - 10 && mouseY - 11 <= y + 3) {
						if (this.rights) {
							this.menuOption[this.menuSize] = "Report abuse @whi@" + this.messageSender[i];
							this.menuAction[this.menuSize] = 2034;
							this.menuSize++;
						}
						this.menuOption[this.menuSize] = "Add ignore @whi@" + this.messageSender[i];
						this.menuAction[this.menuSize] = 2436;
						this.menuSize++;
						this.menuOption[this.menuSize] = "Add friend @whi@" + this.messageSender[i];
						this.menuAction[this.menuSize] = 2406;
						this.menuSize++;
					}

					lineOffset++;
					if (lineOffset >= 5) {
						return;
					}
				}

				if ((type == 5 || type == 6) && this.privateChatSetting < 2) {
					lineOffset++;
					if (lineOffset >= 5) {
						return;
					}
				}
			}
		}
	}

	private void updateInterfaceContent( ComType component) {
		int clientCode = component.clientCode;

		if (clientCode >= 1 && clientCode <= 100) {
			clientCode--;
			if (clientCode >= this.friendCount) {
				component.text = "";
				component.buttonType = 0;
			} else {
				component.text = this.friendName[clientCode];
				component.buttonType = 1;
			}
		} else if (clientCode >= 101 && clientCode <= 200) {
			clientCode -= 101;
			if (clientCode >= this.friendCount) {
				component.text = "";
				component.buttonType = 0;
			} else {
				if (this.friendWorld[clientCode] == 0) {
					component.text = "@red@Offline";
				} else if (this.friendWorld[clientCode] == nodeId) {
					component.text = "@gre@World-" + (this.friendWorld[clientCode] - 9);
				} else {
					component.text = "@yel@World-" + (this.friendWorld[clientCode] - 9);
				}
				component.buttonType = 1;
			}
		} else if (clientCode == 203) {
			component.scroll = this.friendCount * 15 + 20;
			if (component.scroll <= component.height) {
				component.scroll = component.height + 1;
			}
		} else if (clientCode >= 401 && clientCode <= 500) {
			clientCode -= 401;
			if (clientCode >= this.ignoreCount) {
				component.text = "";
				component.buttonType = 0;
			} else {
				component.text = JString.formatName(JString.fromBase37(this.ignoreName37[clientCode]));
				component.buttonType = 1;
			}
		} else if (clientCode == 503) {
			component.scroll = this.ignoreCount * 15 + 20;
			if (component.scroll <= component.height) {
				component.scroll = component.height + 1;
			}
		} else if (clientCode == 327) {
			component.xan = 150;
			component.yan = (int) (Math.sin((double) loopCycle / 40.0D) * 256.0D) & 0x7FF;
			if (this.updateDesignModel) {
				this.updateDesignModel = false;

				Model[] models = new Model[7];
				int modelCount = 0;
				for ( int part = 0; part < 7; part++) {
					int kit = this.designIdentikits[part];
					if (kit >= 0) {
						models[modelCount++] = IdkType.instances[kit].getModel();
					}
				}

				Model model = new Model(models, modelCount);
				for ( int part = 0; part < 5; part++) {
					if (this.designColors[part] != 0) {
						model.recolor(PlayerEntity.DESIGN_BODY_COLOR[part][0], PlayerEntity.DESIGN_BODY_COLOR[part][this.designColors[part]]);
						if (part == 1) {
							model.recolor(PlayerEntity.DESIGN_HAIR_COLOR[0], PlayerEntity.DESIGN_HAIR_COLOR[this.designColors[part]]);
						}
					}
				}

				model.createLabelReferences();
				model.applyTransform(SeqType.instances[this.localPlayer.seqStandId].frames[0]);
				model.calculateNormals(64, 850, -30, -50, -30, true);
				component.model = model;
			}
		} else if (clientCode == 324) {
			if (this.genderButtonImage0 == null) {
				this.genderButtonImage0 = component.graphic;
				this.genderButtonImage1 = component.activeGraphic;
			}
			if (this.designGenderMale) {
				component.graphic = this.genderButtonImage1;
			} else {
				component.graphic = this.genderButtonImage0;
			}
		} else if (clientCode == 325) {
			if (this.genderButtonImage0 == null) {
				this.genderButtonImage0 = component.graphic;
				this.genderButtonImage1 = component.activeGraphic;
			}
			if (this.designGenderMale) {
				component.graphic = this.genderButtonImage0;
			} else {
				component.graphic = this.genderButtonImage1;
			}
		} else if (clientCode == 600) {
			component.text = this.reportAbuseInput;
			if (loopCycle % 20 < 10) {
				component.text = component.text + "|";
			} else {
				component.text = component.text + " ";
			}
		} else if (clientCode == 613) {
			if (!this.rights) {
				component.text = "";
			} else if (this.reportAbuseMuteOption) {
				component.colour = 16711680;
				component.text = "Moderator option: Mute player for 48 hours: <ON>";
			} else {
				component.colour = 16777215;
				component.text = "Moderator option: Mute player for 48 hours: <OFF>";
			}
		} else if (clientCode == 650 || clientCode == 655) {
			if (this.lastAddress == 0) {
				component.text = "";
			} else {
				String text;
				if (this.daysSinceLastLogin == 0) {
					text = "earlier today";
				} else if (this.daysSinceLastLogin == 1) {
					text = "yesterday";
				} else {
					text = this.daysSinceLastLogin + " days ago";
				}
				component.text = "You last logged in " + text + " from: " + signlink.dns;
			}
		} else if (clientCode == 651) {
			if (this.unreadMessages == 0) {
				component.text = "0 unread messages";
				component.colour = 16776960;
			}
			if (this.unreadMessages == 1) {
				component.text = "1 unread message";
				component.colour = 65280;
			}
			if (this.unreadMessages > 1) {
				component.text = this.unreadMessages + " unread messages";
				component.colour = 65280;
			}
		} else if (clientCode == 652) {
			if (this.daysSinceRecoveriesChanged == 201) {
				component.text = "";
			} else if (this.daysSinceRecoveriesChanged == 200) {
				component.text = "You have not yet set any password recovery questions.";
			} else {
				String text;
				if (this.daysSinceRecoveriesChanged == 0) {
					text = "Earlier today";
				} else if (this.daysSinceRecoveriesChanged == 1) {
					text = "Yesterday";
				} else {
					text = this.daysSinceRecoveriesChanged + " days ago";
				}
				component.text = text + " you changed your recovery questions";
			}
		} else if (clientCode == 653) {
			if (this.daysSinceRecoveriesChanged == 201) {
				component.text = "";
			} else if (this.daysSinceRecoveriesChanged == 200) {
				component.text = "We strongly recommend you do so now to secure your account.";
			} else {
				component.text = "If you do not remember making this change then cancel it immediately";
			}
		} else if (clientCode == 654) {
			if (this.daysSinceRecoveriesChanged == 201) {
				component.text = "";
			} else if (this.daysSinceRecoveriesChanged == 200) {
				component.text = "Do this from the 'account management' area on our front webpage";
			} else {
				component.text = "Do this from the 'account management' area on our front webpage";
			}
		}
	}

	private boolean saveWave( byte[] src, int length) {
		return src == null || signlink.wavesave(src, length);
	}

	private boolean replayWave() {
		return signlink.wavereplay();
	}

	private void setWaveVolume( int volume) {
		signlink.wavevol = volume;
	}

	private void readNewNpcs( Packet buf, int size) {
		while (buf.bitPos + 21 < size * 8) {
			int index = buf.gBit(13);
			if (index == 8191) {
				break;
			}
			if (this.npcs[index] == null) {
				this.npcs[index] = new NpcEntity();
			}
			NpcEntity npc = this.npcs[index];
			this.npcIds[this.npcCount++] = index;
			npc.cycle = loopCycle;
			npc.type = NpcType.get(buf.gBit(11));
			npc.size = npc.type.size;
			npc.seqWalkId = npc.type.walkanim;
			npc.seqTurnAroundId = npc.type.walkanim_b;
			npc.seqTurnLeftId = npc.type.walkanim_r;
			npc.seqTurnRightId = npc.type.walkanim_l;
			npc.seqStandId = npc.type.readyanim;
			int dx = buf.gBit(5);
			if (dx > 15) {
				dx -= 32;
			}
			int dz = buf.gBit(5);
			if (dz > 15) {
				dz -= 32;
			}
			npc.move(false, this.localPlayer.pathTileX[0] + dx, this.localPlayer.pathTileZ[0] + dz);
			int update = buf.gBit(1);
			if (update == 1) {
				this.entityUpdateIds[this.entityUpdateCount++] = index;
			}
		}
		buf.accessBytes();
	}

	private boolean handleInterfaceAction( ComType com) {
		int clientCode = com.clientCode;
		if (clientCode == 201) {
			this.redrawChatback = true;
			this.chatbackInputOpen = false;
			this.showSocialInput = true;
			this.socialInput = "";
			this.socialAction = 1;
			this.socialMessage = "Enter name of friend to add to list";
		}

		if (clientCode == 202) {
			this.redrawChatback = true;
			this.chatbackInputOpen = false;
			this.showSocialInput = true;
			this.socialInput = "";
			this.socialAction = 2;
			this.socialMessage = "Enter name of friend to delete from list";
		}

		if (clientCode == 205) {
			this.idleTimeout = 250;
			return true;
		}

		if (clientCode == 501) {
			this.redrawChatback = true;
			this.chatbackInputOpen = false;
			this.showSocialInput = true;
			this.socialInput = "";
			this.socialAction = 4;
			this.socialMessage = "Enter name of player to add to list";
		}

		if (clientCode == 502) {
			this.redrawChatback = true;
			this.chatbackInputOpen = false;
			this.showSocialInput = true;
			this.socialInput = "";
			this.socialAction = 5;
			this.socialMessage = "Enter name of player to delete from list";
		}

		if (clientCode >= 300 && clientCode <= 313) {
			int part = (clientCode - 300) / 2;
			int direction = clientCode & 0x1;
			int kit = this.designIdentikits[part];

			if (kit != -1) {
				while (true) {
					if (direction == 0) {
						kit--;
						if (kit < 0) {
							kit = IdkType.count - 1;
						}
					}

					if (direction == 1) {
						kit++;
						if (kit >= IdkType.count) {
							kit = 0;
						}
					}

					if (!IdkType.instances[kit].disable && IdkType.instances[kit].type == part + (this.designGenderMale ? 0 : 7)) {
						this.designIdentikits[part] = kit;
						this.updateDesignModel = true;
						break;
					}
				}
			}
		}

		if (clientCode >= 314 && clientCode <= 323) {
			int part = (clientCode - 314) / 2;
			int direction = clientCode & 0x1;
			int color = this.designColors[part];

			if (direction == 0) {
				color--;
				if (color < 0) {
					color = PlayerEntity.DESIGN_BODY_COLOR[part].length - 1;
				}
			}

			if (direction == 1) {
				color++;
				if (color >= PlayerEntity.DESIGN_BODY_COLOR[part].length) {
					color = 0;
				}
			}

			this.designColors[part] = color;
			this.updateDesignModel = true;
		}

		if (clientCode == 324 && !this.designGenderMale) {
			this.designGenderMale = true;
			this.validateCharacterDesign();
		}

		if (clientCode == 325 && this.designGenderMale) {
			this.designGenderMale = false;
			this.validateCharacterDesign();
		}

		if (clientCode == 326) {
			// IF_DESIGN
			this.out.p1isaac(52);
			this.out.p1(this.designGenderMale ? 0 : 1);
			for (int i = 0; i < 7; i++) {
				this.out.p1(this.designIdentikits[i]);
			}
			for (int i = 0; i < 5; i++) {
				this.out.p1(this.designColors[i]);
			}
			return true;
		}

		if (clientCode == 613) {
			this.reportAbuseMuteOption = !this.reportAbuseMuteOption;
		}

		if (clientCode >= 601 && clientCode <= 612) {
			this.closeInterfaces();

			if (this.reportAbuseInput.length() > 0) {
				// BUG_REPORT
				this.out.p1isaac(190);
				this.out.p8(JString.toBase37(this.reportAbuseInput));
				this.out.p1(clientCode - 601);
				this.out.p1(this.reportAbuseMuteOption ? 1 : 0);
			}
		}

		return false;
	}

	@Override
	protected void load() {
		if (signlink.sunjava) {
			super.mindel = 5;
		}

		if (!lowMemory) {
			this.startMidiThread = true;
			this.midiThreadActive = true;
			this.startThread(this, 2);
			this.setMidi("scape_main", 12345678, 40000);
		}

		if (started) {
			this.errorStarted = true;
			return;
		}

		started = true;

		boolean good = this.frame != null;
		String host = this.getHost();
		if (host.endsWith("2004scape.org")) {
			// intended domain for players
			good = true;
		}
		if (host.endsWith("localhost") || host.endsWith("127.0.0.1")) {
			// allow localhost
			good = true;
		}
		if (host.startsWith("192.168.") || host.startsWith("172.16.") || host.startsWith("10.")) {
			// allow lan
			good = true;
		}
		if (!good) {
			this.errorHost = true;
			return;
		}

		try {
			int retry = 5;
			this.archiveChecksum[8] = 0;
			while (this.archiveChecksum[8] == 0) {
				this.drawProgress("Connecting to fileserver", 10);
				try {
					DataInputStream stream = this.openUrl("crc" + (int) (Math.random() * 9.9999999E7D));
					Packet checksums = new Packet(new byte[36]);
					stream.readFully(checksums.data, 0, 36);
					for ( int i = 0; i < 9; i++) {
						this.archiveChecksum[i] = checksums.g4();
					}
					stream.close();
				} catch ( IOException ex) {
					for ( int i = retry; i > 0; i--) {
						this.drawProgress("Error loading - Will retry in " + i + " secs.", 10);
						try {
							Thread.sleep(1000L);
						} catch ( Exception ignored) {
						}
					}
					retry *= 2;
					if (retry > 60) {
						retry = 60;
					}
				}
			}

			this.archiveTitle = this.loadArchive("title", this.archiveChecksum[1], "title screen", 10);
			this.fontPlain11 = new PixFont(this.archiveTitle, "p11");
			this.fontPlain12 = new PixFont(this.archiveTitle, "p12");
			this.fontBold12 = new PixFont(this.archiveTitle, "b12");
			this.fontQuill8 = new PixFont(this.archiveTitle, "q8");
			this.loadTitleBackground();
			this.loadTitleImages();

			Jagfile config = this.loadArchive("config", this.archiveChecksum[2], "config", 15);
			Jagfile inter = this.loadArchive("interface", this.archiveChecksum[3], "interface", 20);
			Jagfile media = this.loadArchive("media", this.archiveChecksum[4], "2d graphics", 30);
			Jagfile models = this.loadArchive("models", this.archiveChecksum[5], "3d graphics", 40);
			Jagfile textures = this.loadArchive("textures", this.archiveChecksum[6], "textures", 60);
			Jagfile wordenc = this.loadArchive("wordenc", this.archiveChecksum[7], "chat system", 65);
			Jagfile sounds = this.loadArchive("sounds", this.archiveChecksum[8], "sound effects", 70);

			this.levelTileFlags = new byte[4][104][104];
			this.levelHeightmap = new int[4][105][105];
			this.scene = new World3D(this.levelHeightmap, 104, 4, 104);
			for ( int level = 0; level < 4; level++) {
				this.levelCollisionMap[level] = new CollisionMap(104, 104);
			}
			this.imageMinimap = new Pix24(512, 512);
			this.drawProgress("Unpacking media", 75);
			this.imageInvback = new Pix8(media, "invback", 0);
			this.imageChatback = new Pix8(media, "chatback", 0);
			this.imageMapback = new Pix8(media, "mapback", 0);
			this.imageBackbase1 = new Pix8(media, "backbase1", 0);
			this.imageBackbase2 = new Pix8(media, "backbase2", 0);
			this.imageBackhmid1 = new Pix8(media, "backhmid1", 0);
			for ( int i = 0; i < 13; i++) {
				this.imageSideicons[i] = new Pix8(media, "sideicons", i);
			}
			this.imageCompass = new Pix24(media, "compass", 0);

			try {
				for (int i = 0; i < 50; i++) {
					if (i == 22) {
						// weird debug sprite along water
						continue;
					}

					this.imageMapscene[i] = new Pix8(media, "mapscene", i);
				}
			} catch ( Exception ex) {
			}
			try {
				for (int i = 0; i < 50; i++) {
					this.imageMapfunction[i] = new Pix24(media, "mapfunction", i);
				}
			} catch ( Exception ex) {
			}
			try {
				for (int i = 0; i < 20; i++) {
					this.imageHitmarks[i] = new Pix24(media, "hitmarks", i);
				}
			} catch ( Exception ex) {
			}
			try {
				for (int i = 0; i < 20; i++) {
					this.imageHeadicons[i] = new Pix24(media, "headicons", i);
				}
			} catch ( Exception ex) {
			}
			this.imageMapflag = new Pix24(media, "mapflag", 0);
			for (int i = 0; i < 8; i++) {
				this.imageCrosses[i] = new Pix24(media, "cross", i);
			}
			this.imageMapdot0 = new Pix24(media, "mapdots", 0);
			this.imageMapdot1 = new Pix24(media, "mapdots", 1);
			this.imageMapdot2 = new Pix24(media, "mapdots", 2);
			this.imageMapdot3 = new Pix24(media, "mapdots", 3);
			this.imageScrollbar0 = new Pix8(media, "scrollbar", 0);
			this.imageScrollbar1 = new Pix8(media, "scrollbar", 1);
			this.imageRedstone1 = new Pix8(media, "redstone1", 0);
			this.imageRedstone2 = new Pix8(media, "redstone2", 0);
			this.imageRedstone3 = new Pix8(media, "redstone3", 0);
			this.imageRedstone1h = new Pix8(media, "redstone1", 0);
			this.imageRedstone1h.flipHorizontally();
			this.imageRedstone2h = new Pix8(media, "redstone2", 0);
			this.imageRedstone2h.flipHorizontally();
			this.imageRedstone1v = new Pix8(media, "redstone1", 0);
			this.imageRedstone1v.flipVertically();
			this.imageRedstone2v = new Pix8(media, "redstone2", 0);
			this.imageRedstone2v.flipVertically();
			this.imageRedstone3v = new Pix8(media, "redstone3", 0);
			this.imageRedstone3v.flipVertically();
			this.imageRedstone1hv = new Pix8(media, "redstone1", 0);
			this.imageRedstone1hv.flipHorizontally();
			this.imageRedstone1hv.flipVertically();
			this.imageRedstone2hv = new Pix8(media, "redstone2", 0);
			this.imageRedstone2hv.flipHorizontally();
			this.imageRedstone2hv.flipVertically();
			Pix24 backleft1 = new Pix24(media, "backleft1", 0);
			this.areaBackleft1 = new PixMap(this.getBaseComponent(), backleft1.width, backleft1.height);
			backleft1.blitOpaque(0, 0);
			Pix24 backleft2 = new Pix24(media, "backleft2", 0);
			this.areaBackleft2 = new PixMap(this.getBaseComponent(), backleft2.width, backleft2.height);
			backleft2.blitOpaque(0, 0);
			Pix24 backright1 = new Pix24(media, "backright1", 0);
			this.areaBackright1 = new PixMap(this.getBaseComponent(), backright1.width, backright1.height);
			backright1.blitOpaque(0, 0);
			Pix24 backright2 = new Pix24(media, "backright2", 0);
			this.areaBackright2 = new PixMap(this.getBaseComponent(), backright2.width, backright2.height);
			backright2.blitOpaque(0, 0);
			Pix24 backtop1 = new Pix24(media, "backtop1", 0);
			this.areaBacktop1 = new PixMap(this.getBaseComponent(), backtop1.width, backtop1.height);
			backtop1.blitOpaque(0, 0);
			Pix24 backtop2 = new Pix24(media, "backtop2", 0);
			this.areaBacktop2 = new PixMap(this.getBaseComponent(), backtop2.width, backtop2.height);
			backtop2.blitOpaque(0, 0);
			Pix24 backvmid1 = new Pix24(media, "backvmid1", 0);
			this.areaBackvmid1 = new PixMap(this.getBaseComponent(), backvmid1.width, backvmid1.height);
			backvmid1.blitOpaque(0, 0);
			Pix24 backvmid2 = new Pix24(media, "backvmid2", 0);
			this.areaBackvmid2 = new PixMap(this.getBaseComponent(), backvmid2.width, backvmid2.height);
			backvmid2.blitOpaque(0, 0);
			Pix24 backvmid3 = new Pix24(media, "backvmid3", 0);
			this.areaBackvmid3 = new PixMap(this.getBaseComponent(), backvmid3.width, backvmid3.height);
			backvmid3.blitOpaque(0, 0);
			Pix24 backhmid2 = new Pix24(media, "backhmid2", 0);
			this.areaBackhmid2 = new PixMap(this.getBaseComponent(), backhmid2.width, backhmid2.height);
			backhmid2.blitOpaque(0, 0);

			int randR = (int) (Math.random() * 21.0D) - 10;
			int randG = (int) (Math.random() * 21.0D) - 10;
			int randB = (int) (Math.random() * 21.0D) - 10;
			int rand = (int) (Math.random() * 41.0D) - 20;
			for ( int i = 0; i < 50; i++) {
				if (this.imageMapfunction[i] != null) {
					this.imageMapfunction[i].translate(randR + rand, randG + rand, randB + rand);
				}

				if (this.imageMapscene[i] != null) {
					this.imageMapscene[i].translate(randR + rand, randG + rand, randB + rand);
				}
			}

			this.drawProgress("Unpacking textures", 80);
			Draw3D.unpackTextures(textures);
			Draw3D.setBrightness(0.8D);
			Draw3D.initPool(20);
			this.drawProgress("Unpacking models", 83);
			Model.unpack(models);
			SeqBase.unpack(models);
			SeqFrame.unpack(models);
			this.drawProgress("Unpacking config", 86);
			SeqType.unpack(config);
			LocType.unpack(config);
			FloType.unpack(config);
			ObjType.unpack(config);
			NpcType.unpack(config);
			IdkType.unpack(config);
			SpotAnimType.unpack(config);
			VarpType.unpack(config);

			ObjType.membersWorld = members;
			if (!lowMemory) {
				this.drawProgress("Unpacking sounds", 90);
				byte[] data = sounds.read("sounds.dat", null);
				Packet soundDat = new Packet(data);
				Wave.unpack(soundDat);
			}

			this.drawProgress("Unpacking interfaces", 92);
			PixFont[] fonts = new PixFont[] { this.fontPlain11, this.fontPlain12, this.fontBold12, this.fontQuill8 };
			ComType.unpack(inter, media, fonts);

			this.drawProgress("Preparing game engine", 97);
			for ( int y = 0; y < 33; y++) {
				int left = 999;
				int right = 0;
				for (int x = 0; x < 35; x++) {
					if (this.imageMapback.pixels[x + y * this.imageMapback.width] == 0) {
						if (left == 999) {
							left = x;
						}
					} else if (left != 999) {
						right = x;
						break;
					}
				}
				this.compassMaskLineOffsets[y] = left;
				this.compassMaskLineLengths[y] = right - left;
			}

			for (int y = 9; y < 160; y++) {
				int left = 999;
				int right = 0;
				for (int x = 10; x < 168; x++) {
					if (this.imageMapback.pixels[x + y * this.imageMapback.width] == 0 && (x > 34 || y > 34)) {
						if (left == 999) {
							left = x;
						}
					} else if (left != 999) {
						right = x;
						break;
					}
				}
				this.minimapMaskLineOffsets[y - 9] = left - 21;
				this.minimapMaskLineLengths[y - 9] = right - left;
			}

			Draw3D.init3D(479, 96);
			this.areaChatbackOffsets = Draw3D.lineOffset;
			Draw3D.init3D(190, 261);
			this.areaSidebarOffsets = Draw3D.lineOffset;
			Draw3D.init3D(512, 334);
			this.areaViewportOffsets = Draw3D.lineOffset;

			int[] distance = new int[9];
			for (int x = 0; x < 9; x++) {
				int angle = x * 32 + 128 + 15;
				int offset = angle * 3 + 600;
				int sin = Draw3D.sin[angle];
				distance[x] = offset * sin >> 16;
			}

			World3D.init(512, 334, 500, 800, distance);
			WordFilter.unpack(wordenc);
		} catch ( Exception ex) {
			this.errorLoading = true;
		}
	}

	private void handleInput() {
		if (this.objDragArea != 0) {
			return;
		}

		this.menuOption[0] = "Cancel";
		this.menuAction[0] = 1252;
		this.menuSize = 1;
		this.handlePrivateChatInput(super.mouseX, super.mouseY);
		this.lastHoveredInterfaceId = 0;

		if (super.mouseX > 8 && super.mouseY > 11 && super.mouseX < 520 && super.mouseY < 345) {
			if (this.viewportInterfaceId == -1) {
				this.handleViewportOptions();
			} else {
				this.handleInterfaceInput(ComType.instances[this.viewportInterfaceId], super.mouseX, super.mouseY, 8, 11, 0);
			}
		}

		if (this.lastHoveredInterfaceId != this.viewportHoveredInterfaceIndex) {
			this.viewportHoveredInterfaceIndex = this.lastHoveredInterfaceId;
		}

		this.lastHoveredInterfaceId = 0;

		if (super.mouseX > 562 && super.mouseY > 231 && super.mouseX < 752 && super.mouseY < 492) {
			if (this.sidebarInterfaceId != -1) {
				this.handleInterfaceInput(ComType.instances[this.sidebarInterfaceId], super.mouseX, super.mouseY, 562, 231, 0);
			} else if (this.tabInterfaceId[this.selectedTab] != -1) {
				this.handleInterfaceInput(ComType.instances[this.tabInterfaceId[this.selectedTab]], super.mouseX, super.mouseY, 562, 231, 0);
			}
		}

		if (this.lastHoveredInterfaceId != this.sidebarHoveredInterfaceIndex) {
			this.redrawSidebar = true;
			this.sidebarHoveredInterfaceIndex = this.lastHoveredInterfaceId;
		}

		this.lastHoveredInterfaceId = 0;

		if (super.mouseX > 22 && super.mouseY > 375 && super.mouseX < 431 && super.mouseY < 471) {
			if (this.chatInterfaceId == -1) {
				this.handleChatMouseInput(super.mouseX - 22, super.mouseY - 375);
			} else {
				this.handleInterfaceInput(ComType.instances[this.chatInterfaceId], super.mouseX, super.mouseY, 22, 375, 0);
			}
		}

		if (this.chatInterfaceId != -1 && this.lastHoveredInterfaceId != this.chatHoveredInterfaceIndex) {
			this.redrawChatback = true;
			this.chatHoveredInterfaceIndex = this.lastHoveredInterfaceId;
		}

		boolean done = false;
		while (!done) {
			done = true;

			for ( int i = 0; i < this.menuSize - 1; i++) {
				if (this.menuAction[i] < 1000 && this.menuAction[i + 1] > 1000) {
					String tmp0 = this.menuOption[i];
					this.menuOption[i] = this.menuOption[i + 1];
					this.menuOption[i + 1] = tmp0;

					int tmp1 = this.menuAction[i];
					this.menuAction[i] = this.menuAction[i + 1];
					this.menuAction[i + 1] = tmp1;

					int tmp2 = this.menuParamB[i];
					this.menuParamB[i] = this.menuParamB[i + 1];
					this.menuParamB[i + 1] = tmp2;

					int tmp3 = this.menuParamC[i];
					this.menuParamC[i] = this.menuParamC[i + 1];
					this.menuParamC[i + 1] = tmp3;

					int tmp4 = this.menuParamA[i];
					this.menuParamA[i] = this.menuParamA[i + 1];
					this.menuParamA[i + 1] = tmp4;

					done = false;
				}
			}
		}
	}

	private void clearCaches() {
		LocType.modelCacheStatic.clear();
		LocType.modelCacheDynamic.clear();
		NpcType.modelCache.clear();
		ObjType.modelCache.clear();
		ObjType.iconCache.clear();
		PlayerEntity.modelCache.clear();
		SpotAnimType.modelCache.clear();
	}

	private void draw3DEntityElements() {
		this.drawPrivateMessages();
		if (this.crossMode == 1) {
			this.imageCrosses[this.crossCycle / 100].draw(this.crossX - 8 - 8, this.crossY - 8 - 11);
		}

		if (this.crossMode == 2) {
			this.imageCrosses[this.crossCycle / 100 + 4].draw(this.crossX - 8 - 8, this.crossY - 8 - 11);
		}

		if (this.viewportInterfaceId != -1) {
			this.updateInterfaceAnimation(this.viewportInterfaceId, this.sceneDelta);
			this.drawInterface(ComType.instances[this.viewportInterfaceId], 0, 0, 0);
		}

		this.drawWildyLevel();

		if (!this.menuVisible) {
			this.handleInput();
			this.drawTooltip();
		} else if (this.menuArea == 0) {
			this.drawMenu();
		}

		if (this.inMultizone == 1) {
			if (this.wildernessLevel > 0 || this.worldLocationState == 1) {
				this.imageHeadicons[1].draw(472, 258);
			} else {
				this.imageHeadicons[1].draw(472, 296);
			}
		}

		if (this.wildernessLevel > 0) {
			this.imageHeadicons[0].draw(472, 296);
			this.fontPlain12.drawStringCenter(484, 329, "Level: " + this.wildernessLevel, 16776960);
		}

		if (this.worldLocationState == 1) {
			this.imageHeadicons[6].draw(472, 296);
			this.fontPlain12.drawStringCenter(484, 329, "Arena", 16776960);
		}

		if (this.systemUpdateTimer != 0) {
			int seconds = this.systemUpdateTimer / 50;
			int minutes = seconds / 60;
			seconds %= 60;

			if (seconds < 10) {
				this.fontPlain12.drawString(4, 329, "System update in: " + minutes + ":0" + seconds, 16776960);
			} else {
				this.fontPlain12.drawString(4, 329, "System update in: " + minutes + ":" + seconds, 16776960);
			}
		}

		this.drawInfoOverlay();
	}

	private void drawInfoOverlay() {
		int x = 507;
		int y = 13;

		if (this.showPerformance) {
			this.fontPlain11.drawStringRight(x, y, "FPS: " + super.fps, 0xFFFF00, true);
			y += 13;
		}
	}

	private void drawTileOverlay(int x, int z, int level, int size, int color, boolean crossed) {
		int height = this.getHeightmapY(level, x, z);
		int x0, y0;
		int x1, y1;
		int x2, y2;
		int x3, y3;

		int halfUnit = 64 * size;
		this.project(x - halfUnit, height, z - halfUnit);
		x0 = this.projectX;
		y0 = this.projectY;
		this.project(x + halfUnit, height, z - halfUnit);
		x1 = this.projectX;
		y1 = this.projectY;
		this.project(x - halfUnit, height, z + halfUnit);
		x2 = this.projectX;
		y2 = this.projectY;
		this.project(x + halfUnit, height, z + halfUnit);
		x3 = this.projectX;
		y3 = this.projectY;

		// one of our points failed to project
		if ((x0 == -1) || (x1 == -1) || (x2 == -1) || (x3 == -1)) {
			return;
		}

		if (crossed) {
			Draw2D.drawLine(x0, y0, x3, y3, (color & 0xFEFEFE) >> 1);
			Draw2D.drawLine(x1, y1, x2, y2, (color & 0xFEFEFE) >> 1);
		}

		Draw2D.drawLine(x0, y0, x1, y1, color);
		Draw2D.drawLine(x0, y0, x2, y2, color);
		Draw2D.drawLine(x1, y1, x3, y3, color);
		Draw2D.drawLine(x2, y2, x3, y3, color);
	}

	private void updateOrbitCamera() {
		int orbitX = this.localPlayer.x + this.cameraAnticheatOffsetX;
		int orbitZ = this.localPlayer.z + this.cameraAnticheatOffsetZ;
		if (this.orbitCameraX - orbitX < -500 || this.orbitCameraX - orbitX > 500 || this.orbitCameraZ - orbitZ < -500 || this.orbitCameraZ - orbitZ > 500) {
			this.orbitCameraX = orbitX;
			this.orbitCameraZ = orbitZ;
		}
		if (this.orbitCameraX != orbitX) {
			this.orbitCameraX += (orbitX - this.orbitCameraX) / 16;
		}
		if (this.orbitCameraZ != orbitZ) {
			this.orbitCameraZ += (orbitZ - this.orbitCameraZ) / 16;
		}
		if (super.actionKey[1] == 1) {
			this.orbitCameraYawVelocity += (-this.orbitCameraYawVelocity - 24) / 2;
		} else if (super.actionKey[2] == 1) {
			this.orbitCameraYawVelocity += (24 - this.orbitCameraYawVelocity) / 2;
		} else {
			this.orbitCameraYawVelocity /= 2;
		}
		if (super.actionKey[3] == 1) {
			this.orbitCameraPitchVelocity += (12 - this.orbitCameraPitchVelocity) / 2;
		} else if (super.actionKey[4] == 1) {
			this.orbitCameraPitchVelocity += (-this.orbitCameraPitchVelocity - 12) / 2;
		} else {
			this.orbitCameraPitchVelocity /= 2;
		}
		this.orbitCameraYaw = this.orbitCameraYaw + this.orbitCameraYawVelocity / 2 & 0x7FF;
		this.orbitCameraPitch += this.orbitCameraPitchVelocity / 2;
		if (this.orbitCameraPitch < 128) {
			this.orbitCameraPitch = 128;
		}
		if (this.orbitCameraPitch > 383) {
			this.orbitCameraPitch = 383;
		}

		int orbitTileX = this.orbitCameraX >> 7;
		int orbitTileZ = this.orbitCameraZ >> 7;
		int orbitY = this.getHeightmapY(this.currentLevel, this.orbitCameraX, this.orbitCameraZ);
		int maxY = 0;

		if (orbitTileX > 3 && orbitTileZ > 3 && orbitTileX < 100 && orbitTileZ < 100) {
			for (int x = orbitTileX - 4; x <= orbitTileX + 4; x++) {
				for ( int z = orbitTileZ - 4; z <= orbitTileZ + 4; z++) {
					int level = this.currentLevel;
					if (level < 3 && (this.levelTileFlags[1][x][z] & 0x2) == 2) {
						level++;
					}

					int y = orbitY - this.levelHeightmap[level][x][z];
					if (y > maxY) {
						maxY = y;
					}
				}
			}
		}

		int clamp = maxY * 192;
		if (clamp > 98048) {
			clamp = 98048;
		}

		if (clamp < 32768) {
			clamp = 32768;
		}

		if (clamp > this.cameraPitchClamp) {
			this.cameraPitchClamp += (clamp - this.cameraPitchClamp) / 24;
		} else if (clamp < this.cameraPitchClamp) {
			this.cameraPitchClamp += (clamp - this.cameraPitchClamp) / 80;
		}
	}

	private void pushProjectiles() {
		for ( ProjectileEntity proj = (ProjectileEntity) this.projectiles.peekFront(); proj != null; proj = (ProjectileEntity) this.projectiles.prev()) {
			if (proj.level != this.currentLevel || loopCycle > proj.lastCycle) {
				proj.unlink();
			} else if (loopCycle >= proj.startCycle) {
				if (proj.target > 0) {
					NpcEntity npc = this.npcs[proj.target - 1];
					if (npc != null) {
						proj.updateVelocity(npc.x, this.getHeightmapY(proj.level, npc.x, npc.z) - proj.offsetY, npc.z, loopCycle);
					}
				}

				if (proj.target < 0) {
					int index = -proj.target - 1;
					PlayerEntity player;
					if (index == this.localPid) {
						player = this.localPlayer;
					} else {
						player = this.players[index];
					}
					if (player != null) {
						proj.updateVelocity(player.x, this.getHeightmapY(proj.level, player.x, player.z) - proj.offsetY, player.z, loopCycle);
					}
				}

				proj.update(this.sceneDelta);
				this.scene.addTemporary(this.currentLevel, (int) proj.x, (int) proj.y, (int) proj.z, null, proj, -1, proj.yaw, 60, false);
			}
		}
	}

	@Override
	public void refresh() {
		this.redrawTitleBackground = true;
	}

	private void drawOnMinimap( int dy, Pix24 image, int dx) {
		int angle = this.orbitCameraYaw + this.minimapAnticheatAngle & 0x7FF;
		int distance = dx * dx + dy * dy;
		if (distance > 6400) {
			return;
		}

		int sinAngle = Model.sin[angle];
		int cosAngle = Model.cos[angle];

		sinAngle = sinAngle * 256 / (this.minimapZoom + 256);
		cosAngle = cosAngle * 256 / (this.minimapZoom + 256);

		int x = dy * sinAngle + dx * cosAngle >> 16;
		int y = dy * cosAngle - dx * sinAngle >> 16;

		if (distance > 2500) {
			image.drawMasked(x + 94 - image.cropW / 2, 83 - y - image.cropH / 2, this.imageMapback);
		} else {
			image.draw(x + 94 - image.cropW / 2, 83 - y - image.cropH / 2);
		}
	}

	private int mix( int src, int alpha, int dst) {
		int invAlpha = 256 - alpha;
		return ((src & 0xFF00FF) * invAlpha + (dst & 0xFF00FF) * alpha & 0xFF00FF00) + ((src & 0xFF00) * invAlpha + (dst & 0xFF00) * alpha & 0xFF0000) >> 8;
	}

	private String getIntString( int value) {
		return value < 999999999 ? String.valueOf(value) : "*";
	}

	private void projectFromGround( PathingEntity entity, int height) {
		this.projectFromGround(entity.x, height, entity.z);
	}

	private void projectFromGround( int x, int height, int z) {
		if (x < 128 || z < 128 || x > 13056 || z > 13056) {
			this.projectX = -1;
			this.projectY = -1;
			return;
		}

		int y = this.getHeightmapY(this.currentLevel, x, z) - height;
		this.project(x, y, z);
	}

	private void project(int x, int y, int z) {
		int dx = x - this.cameraX;
		int dy = y - this.cameraY;
		int dz = z - this.cameraZ;

		int sinPitch = Model.sin[this.cameraPitch];
		int cosPitch = Model.cos[this.cameraPitch];
		int sinYaw = Model.sin[this.cameraYaw];
		int cosYaw = Model.cos[this.cameraYaw];

		int tmp = dz * sinYaw + dx * cosYaw >> 16;
		dz = dz * cosYaw - dx * sinYaw >> 16;
		dx = tmp;

		tmp = dy * cosPitch - dz * sinPitch >> 16;
		dz = dy * sinPitch + dz * cosPitch >> 16;
		dy = tmp;

		if (dz >= 50) {
			this.projectX = Draw3D.centerX + (dx << 9) / dz;
			this.projectY = Draw3D.centerY + (dy << 9) / dz;
		} else {
			this.projectX = -1;
			this.projectY = -1;
		}
	}

	private boolean interactWithLoc( int opcode, int x, int z, int bitset) {
		int locId = bitset >> 14 & 0x7FFF;
		int info = this.scene.getInfo(this.currentLevel, x, z, bitset);
		if (info == -1) {
			return false;
		}

		int type = info & 0x1F;
		int angle = info >> 6 & 0x3;
		if (type == 10 || type == 11 || type == 22) {
			LocType loc = LocType.get(locId);
			int width;
			int height;

			if (angle == 0 || angle == 2) {
				width = loc.width;
				height = loc.length;
			} else {
				width = loc.length;
				height = loc.width;
			}

			int forceapproach = loc.forceapproach;
			if (angle != 0) {
				forceapproach = (forceapproach << angle & 0xF) + (forceapproach >> 4 - angle);
			}

			this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], x, z, 2, width, height, 0, 0, forceapproach, false);
		} else {
			this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], x, z, 2, 0, 0, angle, type + 1, 0, false);
		}

		this.crossX = super.mouseClickX;
		this.crossY = super.mouseClickY;
		this.crossMode = 2;
		this.crossCycle = 0;

		this.out.p1isaac(opcode);
		this.out.p2(x + this.sceneBaseTileX);
		this.out.p2(z + this.sceneBaseTileZ);
		this.out.p2(locId);
		return true;
	}

	private void showContextMenu() {
		int width = this.fontBold12.stringWidth("Choose Option");
		int maxWidth;
		for ( int i = 0; i < this.menuSize; i++) {
			maxWidth = this.fontBold12.stringWidth(this.menuOption[i]);
			if (maxWidth > width) {
				width = maxWidth;
			}
		}
		width += 8;

		int height = this.menuSize * 15 + 21;

		int x;
		int y;
		if (super.mouseClickX > 8 && super.mouseClickY > 11 && super.mouseClickX < 520 && super.mouseClickY < 345) {
			x = super.mouseClickX - width / 2 - 8;
			if (x + width > 512) {
				x = 512 - width;
			} else if (x < 0) {
				x = 0;
			}

			y = super.mouseClickY - 11;
			if (y + height > 334) {
				y = 334 - height;
			} else if (y < 0) {
				y = 0;
			}

			this.menuVisible = true;
			this.menuArea = 0;
			this.menuX = x;
			this.menuY = y;
			this.menuWidth = width;
			this.menuHeight = this.menuSize * 15 + 22;
		}
		if (super.mouseClickX > 562 && super.mouseClickY > 231 && super.mouseClickX < 752 && super.mouseClickY < 492) {
			x = super.mouseClickX - width / 2 - 562;
			if (x < 0) {
				x = 0;
			} else if (x + width > 190) {
				x = 190 - width;
			}

			y = super.mouseClickY - 231;
			if (y < 0) {
				y = 0;
			} else if (y + height > 261) {
				y = 261 - height;
			}

			this.menuVisible = true;
			this.menuArea = 1;
			this.menuX = x;
			this.menuY = y;
			this.menuWidth = width;
			this.menuHeight = this.menuSize * 15 + 22;
		}
		if (super.mouseClickX > 22 && super.mouseClickY > 375 && super.mouseClickX < 501 && super.mouseClickY < 471) {
			x = super.mouseClickX - width / 2 - 22;
			if (x < 0) {
				x = 0;
			} else if (x + width > 479) {
				x = 479 - width;
			}

			y = super.mouseClickY - 375;
			if (y < 0) {
				y = 0;
			} else if (y + height > 96) {
				y = 96 - height;
			}

			this.menuVisible = true;
			this.menuArea = 2;
			this.menuX = x;
			this.menuY = y;
			this.menuWidth = width;
			this.menuHeight = this.menuSize * 15 + 22;
		}
	}

	private DataInputStream openUrl( String url) throws IOException {
		if (signlink.mainapp != null) {
			return signlink.openurl(url);
		}

		return new DataInputStream((new URL(new URL(Configuration.URL), url)).openStream());
	}

	private void loadTitle() {
		if (this.imageTitle2 != null) {
			return;
		}

		super.drawArea = null;
		this.areaChatback = null;
		this.areaMapback = null;
		this.areaSidebar = null;
		this.areaViewport = null;
		this.areaBackbase1 = null;
		this.areaBackbase2 = null;
		this.areaBackhmid1 = null;

		this.imageTitle0 = new PixMap(this.getBaseComponent(), 128, 265);
		Draw2D.clear();

		this.imageTitle1 = new PixMap(this.getBaseComponent(), 128, 265);
		Draw2D.clear();

		this.imageTitle2 = new PixMap(this.getBaseComponent(), 533, 186);
		Draw2D.clear();

		this.imageTitle3 = new PixMap(this.getBaseComponent(), 360, 146);
		Draw2D.clear();

		this.imageTitle4 = new PixMap(this.getBaseComponent(), 360, 200);
		Draw2D.clear();

		this.imageTitle5 = new PixMap(this.getBaseComponent(), 214, 267);
		Draw2D.clear();

		this.imageTitle6 = new PixMap(this.getBaseComponent(), 215, 267);
		Draw2D.clear();

		this.imageTitle7 = new PixMap(this.getBaseComponent(), 86, 79);
		Draw2D.clear();

		this.imageTitle8 = new PixMap(this.getBaseComponent(), 87, 79);
		Draw2D.clear();

		if (this.archiveTitle != null) {
			this.loadTitleBackground();
			this.loadTitleImages();
		}

		this.redrawTitleBackground = true;
	}

	private void runFlames() {
		this.flameThread = true;

		try {
			long last = System.currentTimeMillis();
			int cycle = 0;
			int interval = 20;
			while (this.flameActive) {
				this.updateFlames();
				this.updateFlames();
				this.drawFlames();

				cycle++;

				if (cycle > 10) {
					long now = System.currentTimeMillis();
					int delay = (int) (now - last) / 10 - interval;

					interval = 40 - delay;
					if (interval < 5) {
						interval = 5;
					}

					cycle = 0;
					last = now;
				}

				try {
					Thread.sleep(interval);
				} catch ( Exception ignored) {
				}
			}
		} catch ( Exception ignored) {
		}

		this.flameThread = false;
	}

	@Override
	public void run() {
		if (this.flamesThread) {
			this.runFlames();
		} else if (this.startMidiThread) {
			this.runMidi();
		} else {
			super.run();
		}
	}

	private void handleScrollInput( int mouseX, int mouseY, int scrollableHeight, int height, boolean redraw, int left, int top, ComType component) {
		if (this.scrollGrabbed) {
			this.scrollInputPadding = 32;
		} else {
			this.scrollInputPadding = 0;
		}

		this.scrollGrabbed = false;

		if (mouseX >= left && mouseX < left + 16 && mouseY >= top && mouseY < top + 16) {
			component.scrollPosition -= this.dragCycles * 4;
			if (redraw) {
				this.redrawSidebar = true;
			}
		} else if (mouseX >= left && mouseX < left + 16 && mouseY >= top + height - 16 && mouseY < top + height) {
			component.scrollPosition += this.dragCycles * 4;
			if (redraw) {
				this.redrawSidebar = true;
			}
		} else if (mouseX >= left - this.scrollInputPadding && mouseX < left + this.scrollInputPadding + 16 && mouseY >= top + 16 && mouseY < top + height - 16 && this.dragCycles > 0) {
			int gripSize = (height - 32) * height / scrollableHeight;
			if (gripSize < 8) {
				gripSize = 8;
			}
			int gripY = mouseY - top - gripSize / 2 - 16;
			int maxY = height - gripSize - 32;
			component.scrollPosition = (scrollableHeight - height) * gripY / maxY;
			if (redraw) {
				this.redrawSidebar = true;
			}
			this.scrollGrabbed = true;
		}
	}

	private void login( String username, String password, boolean reconnect) {
		signlink.errorname = username;
		try {
			if (!reconnect) {
				this.loginMessage0 = "";
				this.loginMessage1 = "Connecting to server...";
				this.drawTitleScreen();
			}

			this.stream = new ClientStream(this, this.openSocket(Configuration.PORT_OFFSET + Configuration.PORT));
			this.stream.read(this.in.data, 0, 8);
			this.in.pos = 0;

			this.serverSeed = this.in.g8();
			int[] seed = new int[] { (int) (Math.random() * 9.9999999E7D), (int) (Math.random() * 9.9999999E7D), (int) (this.serverSeed >> 32), (int) this.serverSeed };

			this.out.pos = 0;
			this.out.p1(10);
			this.out.p4(seed[0]);
			this.out.p4(seed[1]);
			this.out.p4(seed[2]);
			this.out.p4(seed[3]);
			this.out.p4(signlink.uid);
			this.out.pjstr(username);
			this.out.pjstr(password);
			this.out.rsaenc(RSA_MODULUS, RSA_EXPONENT);

			this.login.pos = 0;
			if (reconnect) {
				this.login.p1(18);
			} else {
				this.login.p1(16);
			}

			this.login.p1(this.out.pos + 36 + 1 + 1);
			this.login.p1(signlink.clientversion);
			this.login.p1(lowMemory ? 1 : 0);
			for ( int i = 0; i < 9; i++) {
				this.login.p4(this.archiveChecksum[i]);
			}
			this.login.pdata(this.out.data, this.out.pos, 0);
			this.out.random = new Isaac(seed);
			for ( int i = 0; i < 4; i++) {
				seed[i] += 50;
			}
			this.randomIn = new Isaac(seed);
			this.stream.write(this.login.data, this.login.pos, 0);

			int reply = this.stream.read();
			if (reply == 1) {
				try {
					Thread.sleep(2000L);
				} catch ( Exception ex) {
				}

				this.login(username, password, reconnect);
			} else if (reply == 2 || reply == 18) {
				this.rights = reply == 18;
				InputTracking.setDisabled();

				this.ingame = true;
				this.out.pos = 0;
				this.in.pos = 0;
				this.packetType = -1;
				this.lastPacketType0 = -1;
				this.lastPacketType1 = -1;
				this.lastPacketType2 = -1;
				this.packetSize = 0;
				this.idleNetCycles = 0;
				this.systemUpdateTimer = 0;
				this.idleTimeout = 0;
				this.hintType = 0;
				this.menuSize = 0;
				this.menuVisible = false;
				super.idleCycles = 0;

				for ( int i = 0; i < 100; i++) {
					this.messageText[i] = null;
				}

				this.objSelected = 0;
				this.spellSelected = 0;
				this.sceneState = 0;
				this.waveCount = 0;

				this.cameraAnticheatOffsetX = (int) (Math.random() * 100.0D) - 50;
				this.cameraAnticheatOffsetZ = (int) (Math.random() * 110.0D) - 55;
				this.cameraAnticheatAngle = (int) (Math.random() * 80.0D) - 40;
				this.minimapAnticheatAngle = (int) (Math.random() * 120.0D) - 60;
				this.minimapZoom = (int) (Math.random() * 30.0D) - 20;
				this.orbitCameraYaw = (int) (Math.random() * 20.0D) - 10 & 0x7FF;

				this.minimapLevel = -1;
				this.flagSceneTileX = 0;
				this.flagSceneTileZ = 0;

				this.playerCount = 0;
				this.npcCount = 0;

				for ( int i = 0; i < this.MAX_PLAYER_COUNT; i++) {
					this.players[i] = null;
					this.playerAppearanceBuffer[i] = null;
				}

				for ( int i = 0; i < 8192; i++) {
					this.npcs[i] = null;
				}

				this.localPlayer = this.players[this.LOCAL_PLAYER_INDEX] = new PlayerEntity();
				this.projectiles.clear();
				this.spotanims.clear();
				this.temporaryLocs.clear();
				for ( int level = 0; level < 4; level++) {
					for (int x = 0; x < 104; x++) {
						for ( int z = 0; z < 104; z++) {
							this.levelObjStacks[level][x][z] = null;
						}
					}
				}

				this.spawnedLocations = new LinkList();
				this.friendCount = 0;
				this.stickyChatInterfaceId = -1;
				this.chatInterfaceId = -1;
				this.viewportInterfaceId = -1;
				this.sidebarInterfaceId = -1;
				this.pressedContinueOption = false;
				this.selectedTab = 3;
				this.chatbackInputOpen = false;
				this.menuVisible = false;
				this.showSocialInput = false;
				this.modalMessage = null;
				this.inMultizone = 0;
				this.flashingTab = -1;
				this.designGenderMale = true;

				this.validateCharacterDesign();
				for (int i = 0; i < 5; i++) {
					this.designColors[i] = 0;
				}

				opLoc4Counter = 0;
				opNpc3Counter = 0;
				opHeld4Counter = 0;
				opNpc5Counter = 0;
				opHeld1Counter = 0;
				opLoc5Counter = 0;
				ifButton5Counter = 0;
				opPlayer2Counter = 0;
				opHeld9Counter = 0;
				this.prepareGameScreen();
			} else if (reply == 3) {
				this.loginMessage0 = "";
				this.loginMessage1 = "Invalid username or password.";
			} else if (reply == 4) {
				this.loginMessage0 = "Your account has been disabled.";
				this.loginMessage1 = "Please check your message-centre for details.";
			} else if (reply == 5) {
				this.loginMessage0 = "Your account is already logged in.";
				this.loginMessage1 = "Try again in 60 secs...";
			} else if (reply == 6) {
				this.loginMessage0 = "RuneScape has been updated!";
				this.loginMessage1 = "Please reload this page.";
			} else if (reply == 7) {
				this.loginMessage0 = "This world is full.";
				this.loginMessage1 = "Please use a different world.";
			} else if (reply == 8) {
				this.loginMessage0 = "Unable to connect.";
				this.loginMessage1 = "Login server offline.";
			} else if (reply == 9) {
				this.loginMessage0 = "Login limit exceeded.";
				this.loginMessage1 = "Too many connections from your address.";
			} else if (reply == 10) {
				this.loginMessage0 = "Unable to connect.";
				this.loginMessage1 = "Bad session id.";
			} else if (reply == 11) {
				// this.loginMessage1 = "Login server rejected session.";
				this.loginMessage1 = "Please try again.";
			} else if (reply == 12) {
				this.loginMessage0 = "You need a members account to login to this world.";
				this.loginMessage1 = "Please subscribe, or use a different world.";
			} else if (reply == 13) {
				this.loginMessage0 = "Could not complete login.";
				this.loginMessage1 = "Please try using a different world.";
			} else if (reply == 14) {
				this.loginMessage0 = "The server is being updated.";
				this.loginMessage1 = "Please wait 1 minute and try again.";
			} else if (reply == 15) {
				this.ingame = true;
				this.out.pos = 0;
				this.in.pos = 0;
				this.packetType = -1;
				this.lastPacketType0 = -1;
				this.lastPacketType1 = -1;
				this.lastPacketType2 = -1;
				this.packetSize = 0;
				this.idleNetCycles = 0;
				this.systemUpdateTimer = 0;
				this.menuSize = 0;
				this.menuVisible = false;
			} else if (reply == 16) {
				this.loginMessage0 = "Login attempts exceeded.";
				this.loginMessage1 = "Please wait 1 minute and try again.";
			} else if (reply == 17) {
				this.loginMessage0 = "You are standing in a members-only area.";
				this.loginMessage1 = "To play on this world move to a free area first";
			}
		} catch ( IOException ex) {
			this.loginMessage0 = "";
			this.loginMessage1 = "Error connecting to server.";
		}
	}

	private void addLoc( int level, int x, int z, int id, int angle, int shape, int layer) {
		if (x < 1 || z < 1 || x > 102 || z > 102) {
			return;
		}

		if (lowMemory && level != this.currentLevel) {
			return;
		}

		int bitset = 0;

		if (layer == 0) {
			bitset = this.scene.getWallBitset(level, x, z);
		}

		if (layer == 1) {
			bitset = this.scene.getWallDecorationBitset(level, z, x);
		}

		if (layer == 2) {
			bitset = this.scene.getLocBitset(level, x, z);
		}

		if (layer == 3) {
			bitset = this.scene.getGroundDecorationBitset(level, x, z);
		}

		if (bitset != 0) {
			int otherInfo = this.scene.getInfo(level, x, z, bitset);
			int otherId = bitset >> 14 & 0x7FFF;
			int otherShape = otherInfo & 0x1F;
			int otherRotation = otherInfo >> 6;

			if (layer == 0) {
				this.scene.removeWall(level, x, z, 1);
				LocType type = LocType.get(otherId);

				if (type.blockwalk) {
					this.levelCollisionMap[level].removeWall(x, z, otherShape, otherRotation, type.blockrange);
				}
			}

			if (layer == 1) {
				this.scene.removeWallDecoration(level, x, z);
			}

			if (layer == 2) {
				this.scene.removeLoc(level, x, z);
				LocType type = LocType.get(otherId);

				if (x + type.width > 103 || z + type.width > 103 || x + type.length > 103 || z + type.length > 103) {
					return;
				}

				if (type.blockwalk) {
					this.levelCollisionMap[level].removeLoc(x, z, type.width, type.length, otherRotation, type.blockrange);
				}
			}

			if (layer == 3) {
				this.scene.removeGroundDecoration(level, x, z);
				LocType type = LocType.get(otherId);

				if (type.blockwalk && type.active) {
					this.levelCollisionMap[level].removeBlocked(x, z);
				}
			}
		}

		if (id >= 0) {
			int tileLevel = level;

			if (level < 3 && (this.levelTileFlags[1][x][z] & 0x2) == 2) {
				tileLevel = level + 1;
			}

			World.addLoc(level, x, z, this.scene, this.levelHeightmap, this.locList, this.levelCollisionMap[level], id, shape, angle, tileLevel);
		}
	}

	private void addFriend( long username) {
		if (username == 0L) {
			return;
		}

		if (this.friendCount >= 100) {
			this.addMessage(0, "Your friends list is full. Max of 100 hit", "");
			return;
		}

		String displayName = JString.formatName(JString.fromBase37(username));
		for ( int i = 0; i < this.friendCount; i++) {
			if (this.friendName37[i] == username) {
				this.addMessage(0, displayName + " is already on your friend list", "");
				return;
			}
		}

		for ( int i = 0; i < this.ignoreCount; i++) {
			if (this.ignoreName37[i] == username) {
				this.addMessage(0, "Please remove " + displayName + " from your ignore list first", "");
				return;
			}
		}

		if (!displayName.equals(this.localPlayer.name)) {
			this.friendName[this.friendCount] = displayName;
			this.friendName37[this.friendCount] = username;
			this.friendWorld[this.friendCount] = 0;
			this.friendCount++;
			this.redrawSidebar = true;

			// FRIENDLIST_ADD
			this.out.p1isaac(118);
			this.out.p8(username);
		}
	}

	@Override
	protected void unload() {
		signlink.reporterror = false;

		try {
			if (this.stream != null) {
				this.stream.close();
			}
		} catch ( Exception ex) {
		}

		this.stream = null;
		this.stopMidi();
		this.midiThreadActive = false;
		this.out = null;
		this.login = null;
		this.in = null;
		this.sceneMapIndex = null;
		this.sceneMapLandData = null;
		this.sceneMapLocData = null;
		this.levelHeightmap = null;
		this.levelTileFlags = null;
		this.scene = null;
		this.levelCollisionMap = null;
		this.bfsDirection = null;
		this.bfsCost = null;
		this.bfsStepX = null;
		this.bfsStepZ = null;
		this.textureBuffer = null;
		this.areaSidebar = null;
		this.areaMapback = null;
		this.areaViewport = null;
		this.areaChatback = null;
		this.areaBackbase1 = null;
		this.areaBackbase2 = null;
		this.areaBackhmid1 = null;
		this.areaBackleft1 = null;
		this.areaBackleft2 = null;
		this.areaBackright1 = null;
		this.areaBackright2 = null;
		this.areaBacktop1 = null;
		this.areaBacktop2 = null;
		this.areaBackvmid1 = null;
		this.areaBackvmid2 = null;
		this.areaBackvmid3 = null;
		this.areaBackhmid2 = null;
		this.imageInvback = null;
		this.imageMapback = null;
		this.imageChatback = null;
		this.imageBackbase1 = null;
		this.imageBackbase2 = null;
		this.imageBackhmid1 = null;
		this.imageSideicons = null;
		this.imageRedstone1 = null;
		this.imageRedstone2 = null;
		this.imageRedstone3 = null;
		this.imageRedstone1h = null;
		this.imageRedstone2h = null;
		this.imageRedstone1v = null;
		this.imageRedstone2v = null;
		this.imageRedstone3v = null;
		this.imageRedstone1hv = null;
		this.imageRedstone2hv = null;
		this.imageCompass = null;
		this.imageHitmarks = null;
		this.imageHeadicons = null;
		this.imageCrosses = null;
		this.imageMapdot0 = null;
		this.imageMapdot1 = null;
		this.imageMapdot2 = null;
		this.imageMapdot3 = null;
		this.imageMapscene = null;
		this.imageMapfunction = null;
		this.tileLastOccupiedCycle = null;
		this.players = null;
		this.playerIds = null;
		this.entityUpdateIds = null;
		this.playerAppearanceBuffer = null;
		this.entityRemovalIds = null;
		this.npcs = null;
		this.npcIds = null;
		this.levelObjStacks = null;
		this.spawnedLocations = null;
		this.temporaryLocs = null;
		this.projectiles = null;
		this.spotanims = null;
		this.locList = null;
		this.menuParamB = null;
		this.menuParamC = null;
		this.menuAction = null;
		this.menuParamA = null;
		this.menuOption = null;
		this.varps = null;
		this.activeMapFunctionX = null;
		this.activeMapFunctionZ = null;
		this.activeMapFunctions = null;
		this.imageMinimap = null;
		this.friendName = null;
		this.friendName37 = null;
		this.friendWorld = null;
		this.imageTitle0 = null;
		this.imageTitle1 = null;
		this.imageTitle2 = null;
		this.imageTitle3 = null;
		this.imageTitle4 = null;
		this.imageTitle5 = null;
		this.imageTitle6 = null;
		this.imageTitle7 = null;
		this.imageTitle8 = null;
		this.unloadTitle();
		LocType.unload();
		NpcType.unload();
		ObjType.unload();
		FloType.instances = null;
		IdkType.instances = null;
		ComType.instances = null;
		SeqType.instances = null;
		SpotAnimType.instances = null;
		SpotAnimType.modelCache = null;
		VarpType.instances = null;
		super.drawArea = null;
		PlayerEntity.modelCache = null;
		Draw3D.unload();
		World3D.unload();
		Model.unload();
		SeqBase.instances = null;
		SeqFrame.instances = null;
		System.gc();
	}

	private Socket openSocket( int port) throws IOException {
		if (signlink.mainapp != null) {
			return signlink.opensocket(port);
		}

		return new Socket(InetAddress.getByName(this.getCodeBase().getHost()), port);
	}

	private void addPlayerOptions( PlayerEntity player, int a, int b, int c) {
		if (player == this.localPlayer || this.menuSize >= 400) {
			return;
		}

		String tooltip = player.name + getCombatLevelColorTag(this.localPlayer.combatLevel, player.combatLevel) + " (level-" + player.combatLevel + ")";
		if (this.objSelected == 1) {
			this.menuOption[this.menuSize] = "Use " + this.objSelectedName + " with @whi@" + tooltip;
			this.menuAction[this.menuSize] = 367;
			this.menuParamA[this.menuSize] = a;
			this.menuParamB[this.menuSize] = b;
			this.menuParamC[this.menuSize] = c;
			this.menuSize++;
		} else if (this.spellSelected != 1) {
			this.menuOption[this.menuSize] = "Follow @whi@" + tooltip;
			this.menuAction[this.menuSize] = 1544;
			this.menuParamA[this.menuSize] = a;
			this.menuParamB[this.menuSize] = b;
			this.menuParamC[this.menuSize] = c;
			this.menuSize++;

			if (this.overrideChat == 0) {
				this.menuOption[this.menuSize] = "Trade with @whi@" + tooltip;
				this.menuAction[this.menuSize] = 1373;
				this.menuParamA[this.menuSize] = a;
				this.menuParamB[this.menuSize] = b;
				this.menuParamC[this.menuSize] = c;
				this.menuSize++;
			}

			if (this.wildernessLevel > 0) {
				this.menuOption[this.menuSize] = "Attack @whi@" + tooltip;
				if (this.localPlayer.combatLevel >= player.combatLevel) {
					this.menuAction[this.menuSize] = 151;
				} else {
					this.menuAction[this.menuSize] = 2151;
				}
				this.menuParamA[this.menuSize] = a;
				this.menuParamB[this.menuSize] = b;
				this.menuParamC[this.menuSize] = c;
				this.menuSize++;
			}

			if (this.worldLocationState == 1) {
				this.menuOption[this.menuSize] = "Fight @whi@" + tooltip;
				this.menuAction[this.menuSize] = 151;
				this.menuParamA[this.menuSize] = a;
				this.menuParamB[this.menuSize] = b;
				this.menuParamC[this.menuSize] = c;
				this.menuSize++;
			}

			if (this.worldLocationState == 2) {
				this.menuOption[this.menuSize] = "Duel-with @whi@" + tooltip;
				this.menuAction[this.menuSize] = 1101;
				this.menuParamA[this.menuSize] = a;
				this.menuParamB[this.menuSize] = b;
				this.menuParamC[this.menuSize] = c;
				this.menuSize++;
			}
		} else if ((this.activeSpellFlags & 0x8) == 8) {
			this.menuOption[this.menuSize] = this.spellCaption + " @whi@" + tooltip;
			this.menuAction[this.menuSize] = 651;
			this.menuParamA[this.menuSize] = a;
			this.menuParamB[this.menuSize] = b;
			this.menuParamC[this.menuSize] = c;
			this.menuSize++;
		}

		for ( int i = 0; i < this.menuSize; i++) {
			if (this.menuAction[i] == 660) {
				this.menuOption[i] = "Walk here @whi@" + tooltip;
				return;
			}
		}
	}

	private void updateGame() {
		if (this.systemUpdateTimer > 1) {
			this.systemUpdateTimer--;
		}

		if (this.idleTimeout > 0) {
			this.idleTimeout--;
		}

		for ( int i = 0; i < 5 && this.read(); i++) {
		}

		if (this.ingame) {
			for ( int wave = 0; wave < this.waveCount; wave++) {
				if (this.waveDelay[wave] <= 0) {
					boolean failed = false;
					try {
						if (this.waveIds[wave] != this.lastWaveId || this.waveLoops[wave] != this.lastWaveLoops) {
							Packet buf = Wave.generate(this.waveIds[wave], this.waveLoops[wave]);
							//TODO: Offer toggle to play all sounds instead of vanilla last sound
							SoundPlayer.sounds.put(this.waveIds[wave], AudioSystem.getAudioInputStream(new ByteArrayInputStream(buf.data, 0, buf.pos)));

							if (PLAY_ALL_SOUNDS)
								new SoundPlayer(SoundPlayer.sounds.get(this.waveIds[wave]), 100, 0);

							if (System.currentTimeMillis() + (long) (buf.pos / 22) > this.lastWaveStartTime + (long) (this.lastWaveLength / 22)) {
								this.lastWaveLength = buf.pos;
								this.lastWaveStartTime = System.currentTimeMillis();
								if (this.saveWave(buf.data, buf.pos)) {
									this.lastWaveId = this.waveIds[wave];
									this.lastWaveLoops = this.waveLoops[wave];
								} else {
									failed = true;
								}
							}
						} else {
							if (PLAY_ALL_SOUNDS) {
								new SoundPlayer(SoundPlayer.sounds.get(this.waveIds[wave]), 100, 0);
							}
							if (!this.replayWave()) {
								failed = true;
							}
						}
					} catch ( Exception ignored) {
					}

					if (failed && this.waveDelay[wave] != -5) {
						this.waveDelay[wave] = -5;
					} else {
						this.waveCount--;
						for (int i = wave; i < this.waveCount; i++) {
							this.waveIds[i] = this.waveIds[i + 1];
							this.waveLoops[i] = this.waveLoops[i + 1];
							this.waveDelay[i] = this.waveDelay[i + 1];
						}
						wave--;
					}
				} else {
					this.waveDelay[wave]--;
				}
			}

			if (this.nextMusicDelay > 0) {
				this.nextMusicDelay -= 20;
				if (this.nextMusicDelay < 0) {
					this.nextMusicDelay = 0;
				}
				if (this.nextMusicDelay == 0 && this.midiActive && !lowMemory) {
					this.setMidi(this.currentMidi, this.midiCrc, this.midiSize);
				}
			}

			Packet tracking = InputTracking.flush();
			if (tracking != null) {
				// EVENT_TRACKING
				this.out.p1isaac(81);
				this.out.p2(tracking.pos);
				this.out.pdata(tracking.data, tracking.pos, 0);
				tracking.release();
			}

			this.idleNetCycles++;
			if (this.idleNetCycles > 750) {
				this.tryReconnect();
			}

			this.updatePlayers();
			this.updateNpcs();
			this.updateEntityChats();
			this.updateTemporaryLocs();

			if ((super.actionKey[1] == 1 || super.actionKey[2] == 1 || super.actionKey[3] == 1 || super.actionKey[4] == 1) && this.cameraMovedWrite++ > 5) {
				this.cameraMovedWrite = 0;
				// EVENT_CAMERA_POSITION
				this.out.p1isaac(189);
				this.out.p2(this.orbitCameraPitch);
				this.out.p2(this.orbitCameraYaw);
				this.out.p1(this.minimapAnticheatAngle);
				this.out.p1(this.minimapZoom);
			}

			this.sceneDelta++;
			if (this.crossMode != 0) {
				this.crossCycle += 20;
				if (this.crossCycle >= 400) {
					this.crossMode = 0;
				}
			}

			if (this.selectedArea != 0) {
				this.selectedCycle++;
				if (this.selectedCycle >= 15) {
					if (this.selectedArea == 2) {
						this.redrawSidebar = true;
					}
					if (this.selectedArea == 3) {
						this.redrawChatback = true;
					}
					this.selectedArea = 0;
				}
			}

			if (this.objDragArea != 0) {
				this.objDragCycles++;
				if (super.mouseX > this.objGrabX + 5 || super.mouseX < this.objGrabX - 5 || super.mouseY > this.objGrabY + 5 || super.mouseY < this.objGrabY - 5) {
					this.objGrabThreshold = true;
				}

				if (super.mouseButton == 0) {
					if (this.objDragArea == 2) {
						this.redrawSidebar = true;
					}
					if (this.objDragArea == 3) {
						this.redrawChatback = true;
					}

					this.objDragArea = 0;
					if (this.objGrabThreshold && this.objDragCycles >= 5) {
						this.hoveredSlotParentId = -1;
						this.handleInput();
						if (this.hoveredSlotParentId == this.objDragInterfaceId && this.hoveredSlot != this.objDragSlot) {
							ComType com = ComType.instances[this.objDragInterfaceId];
							int obj = com.invSlotObjId[this.hoveredSlot];
							com.invSlotObjId[this.hoveredSlot] = com.invSlotObjId[this.objDragSlot];
							com.invSlotObjId[this.objDragSlot] = obj;

							int count = com.invSlotObjCount[this.hoveredSlot];
							com.invSlotObjCount[this.hoveredSlot] = com.invSlotObjCount[this.objDragSlot];
							com.invSlotObjCount[this.objDragSlot] = count;

							// INV_BUTTOND
							this.out.p1isaac(159);
							this.out.p2(this.objDragInterfaceId);
							this.out.p2(this.objDragSlot);
							this.out.p2(this.hoveredSlot);
						}
					} else if ((this.mouseButtonsOption == 1 || this.isAddFriendOption(this.menuSize - 1)) && this.menuSize > 2) {
						this.showContextMenu();
					} else if (this.menuSize > 0) {
						this.useMenuOption(this.menuSize - 1);
					}

					this.selectedCycle = 10;
					super.mouseClickButton = 0;
				}
			}

			updateCounter++;
			if (updateCounter > 127) {
				updateCounter = 0;
				// ANTICHEAT_CYCLELOGIC3
				this.out.p1isaac(215);
				this.out.p3(4991788);
			}

			if (World3D.clickTileX != -1) {
				int x = World3D.clickTileX;
				int z = World3D.clickTileZ;
				boolean success = this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], x, z, 0, 0, 0, 0, 0, 0, true);
				World3D.clickTileX = -1;

				if (success) {
					this.crossX = super.mouseClickX;
					this.crossY = super.mouseClickY;
					this.crossMode = 1;
					this.crossCycle = 0;
				}
			}

			if (super.mouseClickButton == 1 && this.modalMessage != null) {
				this.modalMessage = null;
				this.redrawChatback = true;
				super.mouseClickButton = 0;
			}

			this.handleMouseInput();
			this.handleMinimapInput();
			this.handleTabInput();
			this.handleChatSettingsInput();

			if (super.mouseButton == 1 || super.mouseClickButton == 1) {
				this.dragCycles++;
			}

			if (this.sceneState == 2) {
				this.updateOrbitCamera();
			}
			if (this.sceneState == 2 && this.cutscene) {
				this.applyCutscene();
			}

			for (int i = 0; i < 5; i++) {
				this.cameraModifierCycle[i]++;
			}

			this.handleInputKey();
			super.idleCycles++;
			if (super.idleCycles > 4500) {
				this.idleTimeout = 250;
				super.idleCycles -= 500;
				// IDLE_TIMER
				this.out.p1isaac(70);
			}

			this.cameraOffsetCycle++;
			if (this.cameraOffsetCycle > 500) {
				this.cameraOffsetCycle = 0;
				int rand = (int) (Math.random() * 8.0D);
				if ((rand & 0x1) == 1) {
					this.cameraAnticheatOffsetX += this.cameraOffsetXModifier;
				}
				if ((rand & 0x2) == 2) {
					this.cameraAnticheatOffsetZ += this.cameraOffsetZModifier;
				}
				if ((rand & 0x4) == 4) {
					this.cameraAnticheatAngle += this.cameraOffsetYawModifier;
				}
			}

			if (this.cameraAnticheatOffsetX < -50) {
				this.cameraOffsetXModifier = 2;
			}
			if (this.cameraAnticheatOffsetX > 50) {
				this.cameraOffsetXModifier = -2;
			}
			if (this.cameraAnticheatOffsetZ < -55) {
				this.cameraOffsetZModifier = 2;
			}
			if (this.cameraAnticheatOffsetZ > 55) {
				this.cameraOffsetZModifier = -2;
			}
			if (this.cameraAnticheatAngle < -40) {
				this.cameraOffsetYawModifier = 1;
			}
			if (this.cameraAnticheatAngle > 40) {
				this.cameraOffsetYawModifier = -1;
			}

			this.minimapOffsetCycle++;
			if (this.minimapOffsetCycle > 500) {
				this.minimapOffsetCycle = 0;
				int rand = (int) (Math.random() * 8.0D);
				if ((rand & 0x1) == 1) {
					this.minimapAnticheatAngle += this.minimapAngleModifier;
				}
				if ((rand & 0x2) == 2) {
					this.minimapZoom += this.minimapZoomModifier;
				}
			}

			if (this.minimapAnticheatAngle < -60) {
				this.minimapAngleModifier = 2;
			}
			if (this.minimapAnticheatAngle > 60) {
				this.minimapAngleModifier = -2;
			}

			if (this.minimapZoom < -20) {
				this.minimapZoomModifier = 1;
			}
			if (this.minimapZoom > 10) {
				this.minimapZoomModifier = -1;
			}

			update2Counter++;
			if (update2Counter > 110) {
				update2Counter = 0;
				// ANTICHEAT_CYCLELOGIC4
				this.out.p1isaac(236);
				this.out.p4(0);
			}

			this.heartbeatTimer++;
			if (this.heartbeatTimer > 50) {
				// NO_TIMEOUT
				this.out.p1isaac(108);
			}

			try {
				if (this.stream != null && this.out.pos > 0) {
					this.stream.write(this.out.data, this.out.pos, 0);
					this.out.pos = 0;
					this.heartbeatTimer = 0;
				}
			} catch ( IOException ignored) {
				this.tryReconnect();
			} catch ( Exception ignored) {
				this.logout();
			}
		}
	}

	private void drawTooltip() {
		if (this.menuSize < 2 && this.objSelected == 0 && this.spellSelected == 0) {
			return;
		}

		String tooltip;
		if (this.objSelected == 1 && this.menuSize < 2) {
			tooltip = "Use " + this.objSelectedName + " with...";
		} else if (this.spellSelected == 1 && this.menuSize < 2) {
			tooltip = this.spellCaption + "...";
		} else {
			tooltip = this.menuOption[this.menuSize - 1];
		}

		if (this.menuSize > 2) {
			tooltip = tooltip + "@whi@ / " + (this.menuSize - 2) + " more options";
		}

		this.fontBold12.drawStringTooltip(4, 15, tooltip, 16777215, true, loopCycle / 1000);
	}

	private void pushSpotanims() {
		for ( SpotAnimEntity entity = (SpotAnimEntity) this.spotanims.peekFront(); entity != null; entity = (SpotAnimEntity) this.spotanims.prev()) {
			if (entity.level != this.currentLevel || entity.seqComplete) {
				entity.unlink();
			} else if (loopCycle >= entity.startCycle) {
				entity.update(this.sceneDelta);
				if (entity.seqComplete) {
					entity.unlink();
				} else {
					this.scene.addTemporary(entity.level, entity.x, entity.y, entity.z, null, entity, -1, 0, 60, false);
				}
			}
		}
	}

	@Override
	public URL getCodeBase() {
		if (signlink.mainapp != null) {
			return signlink.mainapp.getCodeBase();
		}

		try {
			if (super.frame != null) {
				return new URL(Configuration.URL + ":" + ( Configuration.PORT + Configuration.PORT_OFFSET));
			}
		} catch ( Exception ex) {
		}

		return super.getCodeBase();
	}

	private boolean tryMove( int srcX, int srcZ, int dx, int dz, int type, int locWidth, int locLength, int locRotation, int locShape, int forceapproach, boolean tryNearest) {
		byte sceneWidth = 104;
		byte sceneLength = 104;
		for ( int x = 0; x < sceneWidth; x++) {
			for (int z = 0; z < sceneLength; z++) {
				this.bfsDirection[x][z] = 0;
				this.bfsCost[x][z] = 99999999;
			}
		}

		int x = srcX;
		int z = srcZ;

		this.bfsDirection[srcX][srcZ] = 99;
		this.bfsCost[srcX][srcZ] = 0;

		int steps = 0;
		int length = 0;

		this.bfsStepX[steps] = srcX;
		this.bfsStepZ[steps++] = srcZ;

		boolean arrived = false;
		int bufferSize = this.bfsStepX.length;
		int[][] flags = this.levelCollisionMap[this.currentLevel].flags;

		while (length != steps) {
			x = this.bfsStepX[length];
			z = this.bfsStepZ[length];
			length = (length + 1) % bufferSize;

			if (x == dx && z == dz) {
				arrived = true;
				break;
			}

			if (locShape != 0) {
				int shape = locShape - 1;

				if ((shape <= LocType.WALL_SQUARECORNER || shape == LocType.WALL_DIAGONAL) && this.levelCollisionMap[this.currentLevel].reachedWall(x, z, dx, dz, shape, locRotation)) {
					arrived = true;
					break;
				}

				if (shape <= LocType.WALLDECOR_DIAGONAL_BOTH && this.levelCollisionMap[this.currentLevel].reachedWallDecoration(x, z, dx, dz, shape, locRotation)) {
					arrived = true;
					break;
				}
			}

			if (locWidth != 0 && locLength != 0 && this.levelCollisionMap[this.currentLevel].reachedLoc(x, z, dx, dz, locWidth, locLength, forceapproach)) {
				arrived = true;
				break;
			}

			int nextCost = this.bfsCost[x][z] + 1;
			if (x > 0 && this.bfsDirection[x - 1][z] == 0 && (flags[x - 1][z] & 0x280108) == 0) {
				this.bfsStepX[steps] = x - 1;
				this.bfsStepZ[steps] = z;
				steps = (steps + 1) % bufferSize;
				this.bfsDirection[x - 1][z] = 2;
				this.bfsCost[x - 1][z] = nextCost;
			}

			if (x < sceneWidth - 1 && this.bfsDirection[x + 1][z] == 0 && (flags[x + 1][z] & 0x280180) == 0) {
				this.bfsStepX[steps] = x + 1;
				this.bfsStepZ[steps] = z;
				steps = (steps + 1) % bufferSize;
				this.bfsDirection[x + 1][z] = 8;
				this.bfsCost[x + 1][z] = nextCost;
			}

			if (z > 0 && this.bfsDirection[x][z - 1] == 0 && (flags[x][z - 1] & 0x280102) == 0) {
				this.bfsStepX[steps] = x;
				this.bfsStepZ[steps] = z - 1;
				steps = (steps + 1) % bufferSize;
				this.bfsDirection[x][z - 1] = 1;
				this.bfsCost[x][z - 1] = nextCost;
			}

			if (z < sceneLength - 1 && this.bfsDirection[x][z + 1] == 0 && (flags[x][z + 1] & 0x280120) == 0) {
				this.bfsStepX[steps] = x;
				this.bfsStepZ[steps] = z + 1;
				steps = (steps + 1) % bufferSize;
				this.bfsDirection[x][z + 1] = 4;
				this.bfsCost[x][z + 1] = nextCost;
			}

			if (x > 0 && z > 0 && this.bfsDirection[x - 1][z - 1] == 0 && (flags[x - 1][z - 1] & 0x28010E) == 0 && (flags[x - 1][z] & 0x280108) == 0 && (flags[x][z - 1] & 0x280102) == 0) {
				this.bfsStepX[steps] = x - 1;
				this.bfsStepZ[steps] = z - 1;
				steps = (steps + 1) % bufferSize;
				this.bfsDirection[x - 1][z - 1] = 3;
				this.bfsCost[x - 1][z - 1] = nextCost;
			}

			if (x < sceneWidth - 1 && z > 0 && this.bfsDirection[x + 1][z - 1] == 0 && (flags[x + 1][z - 1] & 0x280183) == 0 && (flags[x + 1][z] & 0x280180) == 0 && (flags[x][z - 1] & 0x280102) == 0) {
				this.bfsStepX[steps] = x + 1;
				this.bfsStepZ[steps] = z - 1;
				steps = (steps + 1) % bufferSize;
				this.bfsDirection[x + 1][z - 1] = 9;
				this.bfsCost[x + 1][z - 1] = nextCost;
			}

			if (x > 0 && z < sceneLength - 1 && this.bfsDirection[x - 1][z + 1] == 0 && (flags[x - 1][z + 1] & 0x280138) == 0 && (flags[x - 1][z] & 0x280108) == 0 && (flags[x][z + 1] & 0x280120) == 0) {
				this.bfsStepX[steps] = x - 1;
				this.bfsStepZ[steps] = z + 1;
				steps = (steps + 1) % bufferSize;
				this.bfsDirection[x - 1][z + 1] = 6;
				this.bfsCost[x - 1][z + 1] = nextCost;
			}

			if (x < sceneWidth - 1 && z < sceneLength - 1 && this.bfsDirection[x + 1][z + 1] == 0 && (flags[x + 1][z + 1] & 0x2801E0) == 0 && (flags[x + 1][z] & 0x280180) == 0 && (flags[x][z + 1] & 0x280120) == 0) {
				this.bfsStepX[steps] = x + 1;
				this.bfsStepZ[steps] = z + 1;
				steps = (steps + 1) % bufferSize;
				this.bfsDirection[x + 1][z + 1] = 12;
				this.bfsCost[x + 1][z + 1] = nextCost;
			}
		}

		this.tryMoveNearest = 0;

		if (!arrived) {
			if (tryNearest) {
				int min = 100;
				for (int padding = 1; padding < 2; padding++) {
					for (int px = dx - padding; px <= dx + padding; px++) {
						for (int pz = dz - padding; pz <= dz + padding; pz++) {
							if (px >= 0 && pz >= 0 && px < 104 && pz < 104 && this.bfsCost[px][pz] < min) {
								min = this.bfsCost[px][pz];
								x = px;
								z = pz;
								this.tryMoveNearest = 1;
								arrived = true;
							}
						}
					}

					if (arrived) {
						break;
					}
				}
			}

			if (!arrived) {
				return false;
			}
		}

		length = 0;
		this.bfsStepX[length] = x;
		this.bfsStepZ[length++] = z;

		int dir = this.bfsDirection[x][z];
		int next = dir;
		while (x != srcX || z != srcZ) {
			if (next != dir) {
				dir = next;
				this.bfsStepX[length] = x;
				this.bfsStepZ[length++] = z;
			}

			if ((next & 0x2) != 0) {
				x++;
			} else if ((next & 0x8) != 0) {
				x--;
			}

			if ((next & 0x1) != 0) {
				z++;
			} else if ((next & 0x4) != 0) {
				z--;
			}

			next = this.bfsDirection[x][z];
		}

		if (length > 0) {
			bufferSize = length;

			if (length > 25) {
				bufferSize = 25;
			}

			length--;

			int startX = this.bfsStepX[length];
			int startZ = this.bfsStepZ[length];

			if (this.showDebug && super.actionKey[6] == 1 && super.actionKey[7] == 1) {
				// check if tile is already added, if so remove it
				for (int i = 0; i < this.userTileMarkers.length; i++) {
					if (this.userTileMarkers[i] != null && this.userTileMarkers[i].x == World3D.clickTileX && this.userTileMarkers[i].z == World3D.clickTileZ) {
						this.userTileMarkers[i] = null;
						return false;
					}
				}

				// add new
				this.userTileMarkers[this.userTileMarkerIndex] = new Tile(this.currentLevel, World3D.clickTileX, World3D.clickTileZ);
				this.userTileMarkerIndex = this.userTileMarkerIndex + 1 & (this.userTileMarkers.length - 1);
				return false;
			}

			if (type == 0) {
				// MOVE_GAMECLICK
				this.out.p1isaac(181);
				this.out.p1(bufferSize + bufferSize + 3);
			} else if (type == 1) {
				// MOVE_MINIMAPCLICK
				this.out.p1isaac(165);
				this.out.p1(bufferSize + bufferSize + 3 + 14);
			} else if (type == 2) {
				// MOVE_OPCLICK
				this.out.p1isaac(93);
				this.out.p1(bufferSize + bufferSize + 3);
			}

			if (super.actionKey[5] == 1) {
				this.out.p1(1);
			} else {
				this.out.p1(0);
			}

			this.out.p2(startX + this.sceneBaseTileX);
			this.out.p2(startZ + this.sceneBaseTileZ);
			this.flagSceneTileX = this.bfsStepX[0];
			this.flagSceneTileZ = this.bfsStepZ[0];

			for ( int i = 1; i < bufferSize; i++) {
				length--;
				this.out.p1(this.bfsStepX[length] - startX);
				this.out.p1(this.bfsStepZ[length] - startZ);
			}

			return true;
		}

		return type != 1;
	}

	private void readPlayerInfo( Packet buf, int size) {
		this.entityRemovalCount = 0;
		this.entityUpdateCount = 0;

		this.readLocalPlayer(buf, size);
		this.readPlayers(buf, size);
		this.readNewPlayers(size, buf);
		this.readPlayerUpdates(buf, size);

		for ( int i = 0; i < this.entityRemovalCount; i++) {
			int index = this.entityRemovalIds[i];
			if (this.players[index].cycle != loopCycle) {
				this.players[index] = null;
			}
		}

		if (buf.pos != size) {
			signlink.reporterror("Error packet size mismatch in getplayer pos:" + buf.pos + " psize:" + size);
			throw new RuntimeException("eek");
		}

		for (int index = 0; index < this.playerCount; index++) {
			if (this.players[this.playerIds[index]] == null) {
				signlink.reporterror(this.username + " null entry in pl list - pos:" + index + " size:" + this.playerCount);
				throw new RuntimeException("eek");
			}
		}
	}

	private boolean updateInterfaceAnimation( int id, int delta) {
		boolean updated = false;
		ComType parent = ComType.instances[id];
		for ( int i = 0; i < parent.childId.length && parent.childId[i] != -1; i++) {
			ComType child = ComType.instances[parent.childId[i]];
			if (child.type == 1) {
				updated |= this.updateInterfaceAnimation(child.id, delta);
			}
			if (child.type == 6 && (child.anim != -1 || child.activeAnim != -1)) {
				boolean active = this.executeInterfaceScript(child);
				int seqId;
				if (active) {
					seqId = child.activeAnim;
				} else {
					seqId = child.anim;
				}
				if (seqId != -1) {
					SeqType type = SeqType.instances[seqId];
					child.seqCycle += delta;
					while (child.seqCycle > type.delay[child.seqFrame]) {
						child.seqCycle -= type.delay[child.seqFrame] + 1;
						child.seqFrame++;
						if (child.seqFrame >= type.frameCount) {
							child.seqFrame -= type.replayoff;
							if (child.seqFrame < 0 || child.seqFrame >= type.frameCount) {
								child.seqFrame = 0;
							}
						}
						updated = true;
					}
				}
			}
		}
		return updated;
	}

	private void addMessage( int type, String text, String sender) {
		if (type == 0 && this.stickyChatInterfaceId != -1) {
			this.modalMessage = text;
			super.mouseClickButton = 0;
		}

		if (this.chatInterfaceId == -1) {
			this.redrawChatback = true;
		}

		for ( int i = 99; i > 0; i--) {
			this.messageType[i] = this.messageType[i - 1];
			this.messageSender[i] = this.messageSender[i - 1];
			this.messageText[i] = this.messageText[i - 1];
		}

		if (this.showDebug && type == 0) {
			text = "[" + (loopCycle / 30) + "]: " + text;
		}

		this.messageType[0] = type;
		this.messageSender[0] = sender;
		this.messageText[0] = text;
	}

	private void resetInterfaceAnimation( int id) {
		ComType parent = ComType.instances[id];
		for ( int i = 0; i < parent.childId.length && parent.childId[i] != -1; i++) {
			ComType child = ComType.instances[parent.childId[i]];
			if (child.type == 1) {
				this.resetInterfaceAnimation(child.id);
			}
			child.seqFrame = 0;
			child.seqCycle = 0;
		}
	}

	private void removeFriend( long username) {
		if (username == 0L) {
			return;
		}

		for (int i = 0; i < this.friendCount; i++) {
			if (this.friendName37[i] == username) {
				this.friendCount--;
				this.redrawSidebar = true;
				for ( int j = i; j < this.friendCount; j++) {
					this.friendName[j] = this.friendName[j + 1];
					this.friendWorld[j] = this.friendWorld[j + 1];
					this.friendName37[j] = this.friendName37[j + 1];
				}
				// FRIENDLIST_DEL
				this.out.p1isaac(11);
				this.out.p8(username);
				return;
			}
		}
	}

	private boolean executeInterfaceScript( ComType com) {
		if (com.scriptComparator == null) {
			return false;
		}

		for ( int i = 0; i < com.scriptComparator.length; i++) {
			int value = this.executeClientscript1(com, i);
			int operand = com.scriptOperand[i];

			if (com.scriptComparator[i] == 2) {
				if (value >= operand) {
					return false;
				}
			} else if (com.scriptComparator[i] == 3) {
				if (value <= operand) {
					return false;
				}
			} else if (com.scriptComparator[i] == 4) {
				if (value == operand) {
					return false;
				}
			} else if (value != operand) {
				return false;
			}
		}

		return true;
	}

	private void handleMinimapInput() {
		if (super.mouseClickButton == 1) {
			int x = super.mouseClickX - 21 - 561;
			int y = super.mouseClickY - 9 - 5;

			if (x >= 0 && y >= 0 && x < 146 && y < 151) {
				x -= 73;
				y -= 75;

				int yaw = this.orbitCameraYaw + this.minimapAnticheatAngle & 0x7FF;
				int sinYaw = Draw3D.sin[yaw];
				int cosYaw = Draw3D.cos[yaw];

				sinYaw = (sinYaw * (this.minimapZoom + 256)) >> 8;
				cosYaw = (cosYaw * (this.minimapZoom + 256)) >> 8;

				int relX = y * sinYaw + x * cosYaw >> 11;
				int relY = y * cosYaw - x * sinYaw >> 11;

				int tileX = this.localPlayer.x + relX >> 7;
				int tileZ = this.localPlayer.z - relY >> 7;

				boolean success = this.tryMove(this.localPlayer.pathTileX[0], this.localPlayer.pathTileZ[0], tileX, tileZ, 1, 0, 0, 0, 0, 0, true);
				if (success) {
					// the additional 14-bytes in MOVE_MINIMAPCLICK
					this.out.p1(x);
					this.out.p1(y);
					this.out.p2(this.orbitCameraYaw);
					this.out.p1(57);
					this.out.p1(this.minimapAnticheatAngle);
					this.out.p1(this.minimapZoom);
					this.out.p1(89);
					this.out.p2(this.localPlayer.x);
					this.out.p2(this.localPlayer.z);
					this.out.p1(this.tryMoveNearest);
					this.out.p1(63);
				}
			}
		}
	}

	private void handleMouseInput() {
		if (this.objDragArea != 0) {
			return;
		}

		int button = super.mouseClickButton;
		if (this.spellSelected == 1 && super.mouseClickX >= 520 && super.mouseClickY >= 165 && super.mouseClickX <= 788 && super.mouseClickY <= 230) {
			button = 0;
		}

		if (this.menuVisible) {
			if (button != 1) {
				int x = super.mouseX;
				int y = super.mouseY;

				if (this.menuArea == 0) {
					x -= 8;
					y -= 11;
				} else if (this.menuArea == 1) {
					x -= 562;
					y -= 231;
				} else if (this.menuArea == 2) {
					x -= 22;
					y -= 375;
				}

				if (x < this.menuX - 10 || x > this.menuX + this.menuWidth + 10 || y < this.menuY - 10 || y > this.menuY + this.menuHeight + 10) {
					this.menuVisible = false;
					if (this.menuArea == 1) {
						this.redrawSidebar = true;
					}
					if (this.menuArea == 2) {
						this.redrawChatback = true;
					}
				}
			}

			if (button == 1) {
				int menuX = this.menuX;
				int menuY = this.menuY;
				int menuWidth = this.menuWidth;

				int clickX = super.mouseClickX;
				int clickY = super.mouseClickY;

				if (this.menuArea == 0) {
					clickX -= 8;
					clickY -= 11;
				} else if (this.menuArea == 1) {
					clickX -= 562;
					clickY -= 231;
				} else if (this.menuArea == 2) {
					clickX -= 22;
					clickY -= 375;
				}

				int option = -1;
				for ( int i = 0; i < this.menuSize; i++) {
					int optionY = menuY + (this.menuSize - 1 - i) * 15 + 31;
					if (clickX > menuX && clickX < menuX + menuWidth && clickY > optionY - 13 && clickY < optionY + 3) {
						option = i;
					}
				}

				if (option != -1) {
					this.useMenuOption(option);
				}

				this.menuVisible = false;
				if (this.menuArea == 1) {
					this.redrawSidebar = true;
				} else if (this.menuArea == 2) {
					this.redrawChatback = true;
				}
			}
		} else {
			if (button == 1 && this.menuSize > 0) {
				int action = this.menuAction[this.menuSize - 1];

				if (action == 602 || action == 596 || action == 22 || action == 892 || action == 415 || action == 405 || action == 38 || action == 422 || action == 478 || action == 347 || action == 188) {
					int slot = this.menuParamB[this.menuSize - 1];
					int comId = this.menuParamC[this.menuSize - 1];
					ComType com = ComType.instances[comId];

					if (com.draggable) {
						this.objGrabThreshold = false;
						this.objDragCycles = 0;
						this.objDragInterfaceId = comId;
						this.objDragSlot = slot;
						this.objDragArea = 2;
						this.objGrabX = super.mouseClickX;
						this.objGrabY = super.mouseClickY;

						if (ComType.instances[comId].layer == this.viewportInterfaceId) {
							this.objDragArea = 1;
						}

						if (ComType.instances[comId].layer == this.chatInterfaceId) {
							this.objDragArea = 3;
						}

						return;
					}
				}
			}

			if (button == 1 && (this.mouseButtonsOption == 1 || this.isAddFriendOption(this.menuSize - 1)) && this.menuSize > 2) {
				button = 2;
			}

			if (button == 1 && this.menuSize > 0) {
				this.useMenuOption(this.menuSize - 1);
			}

			if (button != 2 || this.menuSize <= 0) {
				return;
			}

			this.showContextMenu();
		}
	}

	private void applyCutscene() {
		int x = this.cutsceneSrcLocalTileX * 128 + 64;
		int z = this.cutsceneSrcLocalTileZ * 128 + 64;
		int y = this.getHeightmapY(this.currentLevel, this.cutsceneSrcLocalTileX, this.cutsceneSrcLocalTileZ) - this.cutsceneSrcHeight;

		if (this.cameraX < x) {
			this.cameraX += this.cutsceneMoveSpeed + (x - this.cameraX) * this.cutsceneMoveAcceleration / 1000;
			if (this.cameraX > x) {
				this.cameraX = x;
			}
		}

		if (this.cameraX > x) {
			this.cameraX -= this.cutsceneMoveSpeed + (this.cameraX - x) * this.cutsceneMoveAcceleration / 1000;
			if (this.cameraX < x) {
				this.cameraX = x;
			}
		}

		if (this.cameraY < y) {
			this.cameraY += this.cutsceneMoveSpeed + (y - this.cameraY) * this.cutsceneMoveAcceleration / 1000;
			if (this.cameraY > y) {
				this.cameraY = y;
			}
		}

		if (this.cameraY > y) {
			this.cameraY -= this.cutsceneMoveSpeed + (this.cameraY - y) * this.cutsceneMoveAcceleration / 1000;
			if (this.cameraY < y) {
				this.cameraY = y;
			}
		}

		if (this.cameraZ < z) {
			this.cameraZ += this.cutsceneMoveSpeed + (z - this.cameraZ) * this.cutsceneMoveAcceleration / 1000;
			if (this.cameraZ > z) {
				this.cameraZ = z;
			}
		}

		if (this.cameraZ > z) {
			this.cameraZ -= this.cutsceneMoveSpeed + (this.cameraZ - z) * this.cutsceneMoveAcceleration / 1000;
			if (this.cameraZ < z) {
				this.cameraZ = z;
			}
		}

		x = this.cutsceneDstLocalTileX * 128 + 64;
		z = this.cutsceneDstLocalTileZ * 128 + 64;
		y = this.getHeightmapY(this.currentLevel, this.cutsceneDstLocalTileX, this.cutsceneDstLocalTileZ) - this.cutsceneDstHeight;

		int deltaX = x - this.cameraX;
		int deltaY = y - this.cameraY;
		int deltaZ = z - this.cameraZ;

		int distance = (int) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
		int pitch = (int) (Math.atan2(deltaY, distance) * 325.949D) & 0x7FF;
		int yaw = (int) (Math.atan2(deltaX, deltaZ) * -325.949D) & 0x7FF;

		if (pitch < 128) {
			pitch = 128;
		}

		if (pitch > 383) {
			pitch = 383;
		}

		if (this.cameraPitch < pitch) {
			this.cameraPitch += this.cutsceneRotateSpeed + (pitch - this.cameraPitch) * this.cutsceneRotateAcceleration / 1000;
			if (this.cameraPitch > pitch) {
				this.cameraPitch = pitch;
			}
		}

		if (this.cameraPitch > pitch) {
			this.cameraPitch -= this.cutsceneRotateSpeed + (this.cameraPitch - pitch) * this.cutsceneRotateAcceleration / 1000;
			if (this.cameraPitch < pitch) {
				this.cameraPitch = pitch;
			}
		}

		int deltaYaw = yaw - this.cameraYaw;
		if (deltaYaw > 1024) {
			deltaYaw -= 2048;
		}

		if (deltaYaw < -1024) {
			deltaYaw += 2048;
		}

		if (deltaYaw > 0) {
			this.cameraYaw += this.cutsceneRotateSpeed + deltaYaw * this.cutsceneRotateAcceleration / 1000;
			this.cameraYaw &= 0x7FF;
		}

		if (deltaYaw < 0) {
			this.cameraYaw -= this.cutsceneRotateSpeed + -deltaYaw * this.cutsceneRotateAcceleration / 1000;
			this.cameraYaw &= 0x7FF;
		}

		int tmp = yaw - this.cameraYaw;
		if (tmp > 1024) {
			tmp -= 2048;
		}

		if (tmp < -1024) {
			tmp += 2048;
		}

		if (tmp < 0 && deltaYaw > 0 || tmp > 0 && deltaYaw < 0) {
			this.cameraYaw = yaw;
		}
	}

	private void handleTabInput() {
		if (super.mouseClickButton != 1) {
			return;
		}

		if (super.mouseClickX >= 549 && super.mouseClickX <= 583 && super.mouseClickY >= 195 && super.mouseClickY < 231 && this.tabInterfaceId[0] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 0;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 579 && super.mouseClickX <= 609 && super.mouseClickY >= 194 && super.mouseClickY < 231 && this.tabInterfaceId[1] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 1;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 607 && super.mouseClickX <= 637 && super.mouseClickY >= 194 && super.mouseClickY < 231 && this.tabInterfaceId[2] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 2;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 635 && super.mouseClickX <= 679 && super.mouseClickY >= 194 && super.mouseClickY < 229 && this.tabInterfaceId[3] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 3;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 676 && super.mouseClickX <= 706 && super.mouseClickY >= 194 && super.mouseClickY < 231 && this.tabInterfaceId[4] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 4;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 704 && super.mouseClickX <= 734 && super.mouseClickY >= 194 && super.mouseClickY < 231 && this.tabInterfaceId[5] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 5;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 732 && super.mouseClickX <= 766 && super.mouseClickY >= 195 && super.mouseClickY < 231 && this.tabInterfaceId[6] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 6;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 550 && super.mouseClickX <= 584 && super.mouseClickY >= 492 && super.mouseClickY < 528 && this.tabInterfaceId[7] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 7;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 582 && super.mouseClickX <= 612 && super.mouseClickY >= 492 && super.mouseClickY < 529 && this.tabInterfaceId[8] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 8;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 609 && super.mouseClickX <= 639 && super.mouseClickY >= 492 && super.mouseClickY < 529 && this.tabInterfaceId[9] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 9;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 637 && super.mouseClickX <= 681 && super.mouseClickY >= 493 && super.mouseClickY < 528 && this.tabInterfaceId[10] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 10;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 679 && super.mouseClickX <= 709 && super.mouseClickY >= 492 && super.mouseClickY < 529 && this.tabInterfaceId[11] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 11;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 706 && super.mouseClickX <= 736 && super.mouseClickY >= 492 && super.mouseClickY < 529 && this.tabInterfaceId[12] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 12;
			this.redrawSideicons = true;
		} else if (super.mouseClickX >= 734 && super.mouseClickX <= 768 && super.mouseClickY >= 492 && super.mouseClickY < 528 && this.tabInterfaceId[13] != -1) {
			this.redrawSidebar = true;
			this.selectedTab = 13;
			this.redrawSideicons = true;
		}

		sidebarInputCounter++;
		if (sidebarInputCounter > 150) {
			sidebarInputCounter = 0;
			// ANTICHEAT_CYCLELOGIC1
			this.out.p1isaac(233);
			this.out.p1(43);
		}
	}

	private boolean handleSocialMenuOption( ComType component) {
		int type = component.clientCode;
		if (type >= 1 && type <= 200) {
			if (type >= 101) {
				type -= 101;
			} else {
				type--;
			}

			this.menuOption[this.menuSize] = "Remove @whi@" + this.friendName[type];
			this.menuAction[this.menuSize] = 557;
			this.menuSize++;

			this.menuOption[this.menuSize] = "Message @whi@" + this.friendName[type];
			this.menuAction[this.menuSize] = 679;
			this.menuSize++;
			return true;
		} else if (type >= 401 && type <= 500) {
			this.menuOption[this.menuSize] = "Remove @whi@" + component.text;
			this.menuAction[this.menuSize] = 556;
			this.menuSize++;
			return true;
		} else {
			return false;
		}
	}

	private void readNpcs( Packet buf, int size) {
		buf.accessBits();

		int count = buf.gBit(8);
		if (count < this.npcCount) {
			for (int i = count; i < this.npcCount; i++) {
				this.entityRemovalIds[this.entityRemovalCount++] = this.npcIds[i];
			}
		}

		if (count > this.npcCount) {
			signlink.reporterror(this.username + " Too many npcs");
			throw new RuntimeException("eek");
		}

		this.npcCount = 0;
		for (int i = 0; i < count; i++) {
			int index = this.npcIds[i];
			NpcEntity npc = this.npcs[index];

			int hasUpdate = buf.gBit(1);
			if (hasUpdate == 0) {
				this.npcIds[this.npcCount++] = index;
				npc.cycle = loopCycle;
			} else {
				int updateType = buf.gBit(2);

				if (updateType == 0) {
					this.npcIds[this.npcCount++] = index;
					npc.cycle = loopCycle;
					this.entityUpdateIds[this.entityUpdateCount++] = index;
				} else if (updateType == 1) {
					this.npcIds[this.npcCount++] = index;
					npc.cycle = loopCycle;

					int walkDir = buf.gBit(3);
					npc.step(false, walkDir);

					int hasMaskUpdate = buf.gBit(1);
					if (hasMaskUpdate == 1) {
						this.entityUpdateIds[this.entityUpdateCount++] = index;
					}
				} else if (updateType == 2) {
					this.npcIds[this.npcCount++] = index;
					npc.cycle = loopCycle;

					int walkDir = buf.gBit(3);
					npc.step(true, walkDir);
					int runDir = buf.gBit(3);
					npc.step(true, runDir);

					int hasMaskUpdate = buf.gBit(1);
					if (hasMaskUpdate == 1) {
						this.entityUpdateIds[this.entityUpdateCount++] = index;
					}
				} else if (updateType == 3) {
					this.entityRemovalIds[this.entityRemovalCount++] = index;
				}
			}
		}
	}

	@Override
	public String getParameter( String name) {
		if (signlink.mainapp != null) {
			return signlink.mainapp.getParameter(name);
		}

		return super.getParameter(name);
	}

	private void tryReconnect() {
		if (this.idleTimeout > 0) {
			this.logout();
		} else {
			this.areaViewport.bind();
			this.fontPlain12.drawStringCenter(257, 144, "Connection lost", 0);
			this.fontPlain12.drawStringCenter(256, 143, "Connection lost", 16777215);
			this.fontPlain12.drawStringCenter(257, 159, "Please wait - attempting to reestablish", 0);
			this.fontPlain12.drawStringCenter(256, 158, "Please wait - attempting to reestablish", 16777215);
			this.areaViewport.draw(super.graphics, 8, 11);
			this.flagSceneTileX = 0;
			ClientStream stream = this.stream;
			this.ingame = false;

			this.login(this.username, this.password, true);
			if (!this.ingame) {
				this.logout();
			}

			try {
				stream.close();
			} catch ( Exception ex) {
			}
		}
	}

	private void updateFlameBuffer( Pix8 image) {
		short flameHeight = 256;
		for ( int i = 0; i < this.flameBuffer0.length; i++) {
			this.flameBuffer0[i] = 0;
		}

		for ( int i = 0; i < 5000; i++) {
			int rand = (int) (Math.random() * 128.0D * (double) flameHeight);
			this.flameBuffer0[rand] = (int) (Math.random() * 256.0D);
		}

		for (int i = 0; i < 20; i++) {
			for (int y = 1; y < flameHeight - 1; y++) {
				for (int x = 1; x < 127; x++) {
					int index = x + (y << 7);
					this.flameBuffer1[index] = (this.flameBuffer0[index - 1] + this.flameBuffer0[index + 1] + this.flameBuffer0[index - 128] + this.flameBuffer0[index + 128]) / 4;
				}
			}

			int[] last = this.flameBuffer0;
			this.flameBuffer0 = this.flameBuffer1;
			this.flameBuffer1 = last;
		}

		if (image != null) {
			int off = 0;

			for (int y = 0; y < image.height; y++) {
				for (int x = 0; x < image.width; x++) {
					if (image.pixels[off++] != 0) {
						int x0 = x + image.cropX + 16;
						int y0 = y + image.cropY + 16;
						int index = x0 + (y0 << 7);
						this.flameBuffer0[index] = 0;
					}
				}
			}
		}
	}

	private void sortObjStacks( int x, int z) {
		LinkList objStacks = this.levelObjStacks[this.currentLevel][x][z];
		if (objStacks == null) {
			this.scene.removeObjStack(this.currentLevel, x, z);
			return;
		}

		int topCost = -99999999;
		ObjStackEntity topObj = null;

		for (ObjStackEntity obj = (ObjStackEntity) objStacks.peekFront(); obj != null; obj = (ObjStackEntity) objStacks.prev()) {
			ObjType type = ObjType.get(obj.index);
			int cost = type.cost;

			if (type.stackable) {
				cost *= obj.count + 1;
			}

			if (cost > topCost) {
				topCost = cost;
				topObj = obj;
			}
		}

		objStacks.pushFront(topObj);

		int bottomObjId = -1;
		int middleObjId = -1;
		int bottomObjCount = 0;
		int middleObjCount = 0;
		for (ObjStackEntity obj = (ObjStackEntity) objStacks.peekFront(); obj != null; obj = (ObjStackEntity) objStacks.prev()) {
			if (obj.index != topObj.index && bottomObjId == -1) {
				bottomObjId = obj.index;
				bottomObjCount = obj.count;
			}

			if (obj.index != topObj.index && obj.index != bottomObjId && middleObjId == -1) {
				middleObjId = obj.index;
				middleObjCount = obj.count;
			}
		}

		Model bottomObj = null;
		if (bottomObjId != -1) {
			bottomObj = ObjType.get(bottomObjId).getInterfaceModel(bottomObjCount);
		}

		Model middleObj = null;
		if (middleObjId != -1) {
			middleObj = ObjType.get(middleObjId).getInterfaceModel(middleObjCount);
		}

		int bitset = x + (z << 7) + 1610612736;
		ObjType type = ObjType.get(topObj.index);
		this.scene.addObjStack(x, z, this.getHeightmapY(this.currentLevel, x * 128 + 64, z * 128 + 64), this.currentLevel, bitset, type.getInterfaceModel(topObj.count), middleObj, bottomObj);
	}

	private void buildScene() {
		try {
			this.minimapLevel = -1;
			this.temporaryLocs.clear();
			this.locList.clear();
			this.spotanims.clear();
			this.projectiles.clear();
			Draw3D.clearTexels();
			this.clearCaches();
			this.scene.reset();
			for ( int level = 0; level < 4; level++) {
				this.levelCollisionMap[level].reset();
			}
			System.gc();

			World world = new World(104, 104, this.levelHeightmap, this.levelTileFlags);
			World.lowMemory = World3D.lowMemory;

			int maps = this.sceneMapLandData.length;

			for ( int index = 0; index < maps; index++) {
				int mapsquareX = this.sceneMapIndex[index] >> 8;
				int mapsquareZ = this.sceneMapIndex[index] & 0xFF;

				// underground pass check
				if (mapsquareX == 33 && mapsquareZ >= 71 && mapsquareZ <= 73) {
					World.lowMemory = false;
					break;
				}
			}

			if (World.lowMemory) {
				this.scene.setMinLevel(this.currentLevel);
			} else {
				this.scene.setMinLevel(0);
			}

			byte[] data = new byte[100000];

			// NO_TIMEOUT
			this.out.p1isaac(108);
			for (int i = 0; i < maps; i++) {
				int x = (this.sceneMapIndex[i] >> 8) * 64 - this.sceneBaseTileX;
				int z = (this.sceneMapIndex[i] & 0xFF) * 64 - this.sceneBaseTileZ;
				byte[] src = this.sceneMapLandData[i];

				if (src != null) {
					int length = (new Packet(src)).g4();
					BZip2.read(data, length, src, src.length - 4, 4);
					world.readLandscape((this.sceneCenterZoneX - 6) * 8, (this.sceneCenterZoneZ - 6) * 8, x, z, data);
				} else if (this.sceneCenterZoneZ < 800) {
					world.clearLandscape(z, x, 64, 64);
				}
			}

			// NO_TIMEOUT
			this.out.p1isaac(108);
			for (int i = 0; i < maps; i++) {
				byte[] src = this.sceneMapLocData[i];
				if (src != null) {
					int length = (new Packet(src)).g4();
					BZip2.read(data, length, src, src.length - 4, 4);
					int x = (this.sceneMapIndex[i] >> 8) * 64 - this.sceneBaseTileX;
					int z = (this.sceneMapIndex[i] & 0xFF) * 64 - this.sceneBaseTileZ;
					world.readLocs(this.scene, this.locList, this.levelCollisionMap, data, x, z);
				}
			}

			// NO_TIMEOUT
			this.out.p1isaac(108);
			world.build(this.scene, this.levelCollisionMap);
			this.areaViewport.bind();

			// NO_TIMEOUT
			this.out.p1isaac(108);
			for ( LocEntity loc = (LocEntity) this.locList.peekFront(); loc != null; loc = (LocEntity) this.locList.prev()) {
				if ((this.levelTileFlags[1][loc.heightmapNE][loc.heightmapNW] & 0x2) == 2) {
					loc.heightmapSW--;
					if (loc.heightmapSW < 0) {
						loc.unlink();
					}
				}
			}

			for (int x = 0; x < 104; x++) {
				for (int z = 0; z < 104; z++) {
					this.sortObjStacks(x, z);
				}
			}

			for ( LocTemporary loc = (LocTemporary) this.spawnedLocations.peekFront(); loc != null; loc = (LocTemporary) this.spawnedLocations.prev()) {
				this.addLoc(loc.plane, loc.x, loc.z, loc.locIndex, loc.angle, loc.shape, loc.layer);
			}
		} catch ( Exception ignored) {
		}

		LocType.modelCacheStatic.clear();
		System.gc();
		Draw3D.initPool(20);
	}

	@Override
	protected void update() {
		if (this.errorStarted || this.errorLoading || this.errorHost) {
			return;
		}

		loopCycle++;
		if (this.ingame) {
			this.updateGame();
		} else {
			this.updateTitle();
		}
	}

	private void updateEntityChats() {
		for ( int i = -1; i < this.playerCount; i++) {
			int index;
			if (i == -1) {
				index = this.LOCAL_PLAYER_INDEX;
			} else {
				index = this.playerIds[i];
			}

			PlayerEntity player = this.players[index];
			if (player != null && player.chatTimer > 0) {
				player.chatTimer--;

				if (player.chatTimer == 0) {
					player.chat = null;
				}
			}
		}

		for (int i = 0; i < this.npcCount; i++) {
			int index = this.npcIds[i];
			NpcEntity npc = this.npcs[index];

			if (npc != null && npc.chatTimer > 0) {
				npc.chatTimer--;

				if (npc.chatTimer == 0) {
					npc.chat = null;
				}
			}
		}
	}

	private int executeClientscript1( ComType component, int scriptId) {
		if (component.scripts == null || scriptId >= component.scripts.length) {
			return -2;
		}

		try {
			int[] script = component.scripts[scriptId];
			int register = 0;
			int pc = 0;

			while (true) {
				int opcode = script[pc++];
				if (opcode == 0) {
					return register;
				}

				if (opcode == 1) { // load_skill_level {skill}
					register += this.skillLevel[script[pc++]];
				} else if (opcode == 2) { // load_skill_base_level {skill}
					register += this.skillBaseLevel[script[pc++]];
				} else if (opcode == 3) { // load_skill_exp {skill}
					register += this.skillExperience[script[pc++]];
				} else if (opcode == 4) { // load_inv_count {interface id} {obj id}
					ComType com = ComType.instances[script[pc++]];
					int obj = script[pc++] + 1;

					for (int i = 0; i < com.invSlotObjId.length; i++) {
						if (com.invSlotObjId[i] == obj) {
							register += com.invSlotObjCount[i];
						}
					}
				} else if (opcode == 5) { // load_var {id}
					register += this.varps[script[pc++]];
				} else if (opcode == 6) { // load_next_level_xp {skill}
					register += levelExperience[this.skillBaseLevel[script[pc++]] - 1];
				} else if (opcode == 7) {
					register += this.varps[script[pc++]] * 100 / 46875;
				} else if (opcode == 8) { // load_combat_level
					register += this.localPlayer.combatLevel;
				} else if (opcode == 9) { // load_total_level
					for (int i = 0; i < 19; i++) {
						if (i == 18) {
							// runecrafting
							i = 20;
						}

						register += this.skillBaseLevel[i];
					}
				} else if (opcode == 10) { // load_inv_contains {interface id} {obj id}
					ComType com = ComType.instances[script[pc++]];
					int obj = script[pc++] + 1;

					for (int i = 0; i < com.invSlotObjId.length; i++) {
						if (com.invSlotObjId[i] == obj) {
							register += 999999999;
							break;
						}
					}
				} else if (opcode == 11) { // load_energy
					register += this.energy;
				} else if (opcode == 12) { // load_weight
					register += this.weightCarried;
				} else if (opcode == 13) { // load_bool {varp} {bit: 0..31}
					int varp = this.varps[script[pc++]];
					int lsb = script[pc++];

					register += (varp & 0x1 << lsb) == 0 ? 0 : 1;
				}
			}
		} catch ( Exception ex) {
			return -1;
		}
	}

	private void drawError() {
		Graphics g = this.getBaseComponent().getGraphics();
		g.setColor(Color.black);
		g.fillRect(0, 0, 789, 532);
		this.setFramerate(1);

		if (this.errorLoading) {
			this.flameActive = false;
			int y = 35;

			g.setFont(new Font("Helvetica", Font.BOLD, 16));
			g.setColor(Color.yellow);
			g.drawString("Sorry, an error has occured whilst loading RuneScape", 30, y);
			y += 50;

			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, y);
			y += 50;

			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", Font.BOLD, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, y);
			y += 30;

			g.drawString("2: Try clearing your web-browsers cache from tools->internet options", 30, y);
			y += 30;

			g.drawString("3: Try using a different game-world", 30, y);
			y += 30;

			g.drawString("4: Try rebooting your computer", 30, y);
			y += 30;

			g.drawString("5: Try selecting a different version of Java from the play-game menu", 30, y);
		}

		if (this.errorHost) {
			this.flameActive = false;
			g.setFont(new Font("Helvetica", Font.BOLD, 20));
			g.setColor(Color.white);
			g.drawString("Error - unable to load game!", 50, 50);
			g.drawString("To play RuneScape make sure you play from", 50, 100);
			g.drawString("http://2004scape.org", 50, 150);
		}

		if (this.errorStarted) {
			this.flameActive = false;
			int y = 35;

			g.setColor(Color.yellow);
			g.drawString("Error a copy of RuneScape already appears to be loaded", 30, y);
			y += 50;

			g.setColor(Color.white);
			g.drawString("To fix this try the following (in order):", 30, y);
			y += 50;

			g.setColor(Color.white);
			g.setFont(new Font("Helvetica", Font.BOLD, 12));
			g.drawString("1: Try closing ALL open web-browser windows, and reloading", 30, y);
			y += 30;

			g.drawString("2: Try rebooting your computer, and reloading", 30, y);
			y += 30;
		}
	}

	private void loadTitleBackground() {
		byte[] data = this.archiveTitle.read("title.dat", null);
		Pix24 title = new Pix24(data, this);
		this.imageTitle0.bind();
		title.blitOpaque(0, 0);

		this.imageTitle1.bind();
		title.blitOpaque(-661, 0);

		this.imageTitle2.bind();
		title.blitOpaque(-128, 0);

		this.imageTitle3.bind();
		title.blitOpaque(-214, -386);

		this.imageTitle4.bind();
		title.blitOpaque(-214, -186);

		this.imageTitle5.bind();
		title.blitOpaque(0, -265);

		this.imageTitle6.bind();
		title.blitOpaque(-574, -265);

		this.imageTitle7.bind();
		title.blitOpaque(-128, -186);

		this.imageTitle8.bind();
		title.blitOpaque(-574, -186);

		int[] mirror = new int[title.width];
		for ( int y = 0; y < title.height; y++) {
			for ( int x = 0; x < title.width; x++) {
				mirror[x] = title.pixels[title.width + title.width * y - x - 1];
			}

			if (title.width >= 0) {
				System.arraycopy(mirror, 0, title.pixels, title.width * y, title.width);
			}
		}

		this.imageTitle0.bind();
		title.blitOpaque(394, 0);

		this.imageTitle1.bind();
		title.blitOpaque(-267, 0);

		this.imageTitle2.bind();
		title.blitOpaque(266, 0);

		this.imageTitle3.bind();
		title.blitOpaque(180, -386);

		this.imageTitle4.bind();
		title.blitOpaque(180, -186);

		this.imageTitle5.bind();
		title.blitOpaque(394, -265);

		this.imageTitle6.bind();
		title.blitOpaque(-180, -265);

		this.imageTitle7.bind();
		title.blitOpaque(212, -186);

		this.imageTitle8.bind();
		title.blitOpaque(-180, -186);

		title = new Pix24(this.archiveTitle, "logo", 0);
		this.imageTitle2.bind();
		title.draw(super.screenWidth / 2 - title.width / 2 - 128, 18);
		title = null;

		// some null objects may force garbage collection threshold
		Object dummy1 = null;
		Object dummy2 = null;
		System.gc();
	}

	private void pushLocs() {
		for ( LocEntity loc = (LocEntity) this.locList.peekFront(); loc != null; loc = (LocEntity) this.locList.prev()) {
			boolean append = false;
			loc.seqCycle += this.sceneDelta;
			if (loc.seqFrame == -1) {
				loc.seqFrame = 0;
				append = true;
			}

			while (loc.seqCycle > loc.seq.delay[loc.seqFrame]) {
				loc.seqCycle -= loc.seq.delay[loc.seqFrame] + 1;
				loc.seqFrame++;

				append = true;

				if (loc.seqFrame >= loc.seq.frameCount) {
					loc.seqFrame -= loc.seq.replayoff;

					if (loc.seqFrame < 0 || loc.seqFrame >= loc.seq.frameCount) {
						loc.unlink();
						append = false;
						break;
					}
				}
			}

			if (append) {
				int level = loc.heightmapSW;
				int x = loc.heightmapNE;
				int z = loc.heightmapNW;

				int bitset = 0;
				if (loc.heightmapSE == 0) {
					bitset = this.scene.getWallBitset(level, x, z);
				}

				if (loc.heightmapSE == 1) {
					bitset = this.scene.getWallDecorationBitset(level, z, x);
				}

				if (loc.heightmapSE == 2) {
					bitset = this.scene.getLocBitset(level, x, z);
				}

				if (loc.heightmapSE == 3) {
					bitset = this.scene.getGroundDecorationBitset(level, x, z);
				}

				if (bitset != 0 && (bitset >> 14 & 0x7FFF) == loc.index) {
					int heightmapSW = this.levelHeightmap[level][x][z];
					int heightmapSE = this.levelHeightmap[level][x + 1][z];
					int heightmapNE = this.levelHeightmap[level][x + 1][z + 1];
					int heightmapNW = this.levelHeightmap[level][x][z + 1];

					LocType type = LocType.get(loc.index);
					int seqId = -1;
					if (loc.seqFrame != -1) {
						seqId = loc.seq.frames[loc.seqFrame];
					}

					if (loc.heightmapSE == 2) {
						int info = this.scene.getInfo(level, x, z, bitset);
						int shape = info & 0x1F;
						int rotation = info >> 6;

						if (shape == LocType.CENTREPIECE_DIAGONAL) {
							shape = LocType.CENTREPIECE_STRAIGHT;
						}

						Model model = type.getModel(shape, rotation, heightmapSW, heightmapSE, heightmapNE, heightmapNW, seqId);
						this.scene.setLocModel(level, x, z, model);
					} else if (loc.heightmapSE == 1) {
						Model model = type.getModel(LocType.WALLDECOR_STRAIGHT_NOOFFSET, 0, heightmapSW, heightmapSE, heightmapNE, heightmapNW, seqId);
						this.scene.setWallDecorationModel(level, x, z, model);
					} else if (loc.heightmapSE == 0) {
						int info = this.scene.getInfo(level, x, z, bitset);
						int shape = info & 0x1F;
						int rotation = info >> 6;

						if (shape == LocType.WALL_L) {
							int nextRotation = rotation + 1 & 0x3;
							Model model1 = type.getModel(LocType.WALL_L, rotation + 4, heightmapSW, heightmapSE, heightmapNE, heightmapNW, seqId);
							Model model2 = type.getModel(LocType.WALL_L, nextRotation, heightmapSW, heightmapSE, heightmapNE, heightmapNW, seqId);
							this.scene.setWallModels(x, z, level, model1, model2);
						} else {
							Model model = type.getModel(shape, rotation, heightmapSW, heightmapSE, heightmapNE, heightmapNW, seqId);
							this.scene.setWallModel(level, x, z, model);
						}
					} else if (loc.heightmapSE == 3) {
						int info = this.scene.getInfo(level, x, z, bitset);
						int rotation = info >> 6;
						Model model = type.getModel(LocType.GROUNDDECOR, rotation, heightmapSW, heightmapSE, heightmapNE, heightmapNW, seqId);
						this.scene.setGroundDecorationModel(level, x, z, model);
					}
				} else {
					loc.unlink();
				}
			}
		}
	}

	private void removeIgnore( long username) {
		if (username == 0L) {
			return;
		}

		for ( int i = 0; i < this.ignoreCount; i++) {
			if (this.ignoreName37[i] == username) {
				this.ignoreCount--;
				this.redrawSidebar = true;
				for ( int j = i; j < this.ignoreCount; j++) {
					this.ignoreName37[j] = this.ignoreName37[j + 1];
				}
				// IGNORELIST_DEL
				this.out.p1isaac(171);
				this.out.p8(username);
				return;
			}
		}
	}

	private void handleViewportOptions() {
		if (this.objSelected == 0 && this.spellSelected == 0) {
			this.menuOption[this.menuSize] = "Walk here";
			this.menuAction[this.menuSize] = 660;
			this.menuParamB[this.menuSize] = super.mouseX;
			this.menuParamC[this.menuSize] = super.mouseY;
			this.menuSize++;
		}

		int lastBitset = -1;
		for ( int picked = 0; picked < Model.pickedCount; picked++) {
			int bitset = Model.pickedBitsets[picked];
			int x = bitset & 0x7F;
			int z = bitset >> 7 & 0x7F;
			int entityType = bitset >> 29 & 0x3;
			int typeId = bitset >> 14 & 0x7FFF;

			if (bitset == lastBitset) {
				continue;
			}

			lastBitset = bitset;

			if (entityType == 2 && this.scene.getInfo(this.currentLevel, x, z, bitset) >= 0) {
				LocType loc = LocType.get(typeId);
				if (this.objSelected == 1) {
					this.menuOption[this.menuSize] = "Use " + this.objSelectedName + " with @cya@" + loc.name;
					this.menuAction[this.menuSize] = 450;
					this.menuParamA[this.menuSize] = bitset;
					this.menuParamB[this.menuSize] = x;
					this.menuParamC[this.menuSize] = z;
					this.menuSize++;
				} else if (this.spellSelected != 1) {
					if (loc.ops != null) {
						for (int op = 4; op >= 0; op--) {
							if (loc.ops[op] != null) {
								this.menuOption[this.menuSize] = loc.ops[op] + " @cya@" + loc.name;
								if (op == 0) {
									this.menuAction[this.menuSize] = 285;
								}

								if (op == 1) {
									this.menuAction[this.menuSize] = 504;
								}

								if (op == 2) {
									this.menuAction[this.menuSize] = 364;
								}

								if (op == 3) {
									this.menuAction[this.menuSize] = 581;
								}

								if (op == 4) {
									this.menuAction[this.menuSize] = 1501;
								}

								this.menuParamA[this.menuSize] = bitset;
								this.menuParamB[this.menuSize] = x;
								this.menuParamC[this.menuSize] = z;
								this.menuSize++;
							}
						}
					}

					this.menuOption[this.menuSize] = "Examine @cya@" + loc.name;
					if (this.showDebug) {
						this.menuOption[this.menuSize] += "@whi@ (" + (loc.index) + ")";
					}
					this.menuAction[this.menuSize] = 1175;
					this.menuParamA[this.menuSize] = bitset;
					this.menuParamB[this.menuSize] = x;
					this.menuParamC[this.menuSize] = z;
					this.menuSize++;
				} else if ((this.activeSpellFlags & 0x4) == 4) {
					this.menuOption[this.menuSize] = this.spellCaption + " @cya@" + loc.name;
					this.menuAction[this.menuSize] = 55;
					this.menuParamA[this.menuSize] = bitset;
					this.menuParamB[this.menuSize] = x;
					this.menuParamC[this.menuSize] = z;
					this.menuSize++;
				}
			}

			if (entityType == 1) {
				NpcEntity npc = this.npcs[typeId];
				if (npc.type.size == 1 && (npc.x & 0x7F) == 64 && (npc.z & 0x7F) == 64) {
					for (int i = 0; i < this.npcCount; i++) {
						NpcEntity other = this.npcs[this.npcIds[i]];

						if (other != null && other != npc && other.type.size == 1 && other.x == npc.x && other.z == npc.z) {
							this.addNpcOptions(other.type, this.npcIds[i], x, z);
						}
					}
				}

				this.addNpcOptions(npc.type, typeId, x, z);
			}

			if (entityType == 0) {
				PlayerEntity player = this.players[typeId];
				if ((player.x & 0x7F) == 64 && (player.z & 0x7F) == 64) {
					for (int i = 0; i < this.npcCount; i++) {
						NpcEntity other = this.npcs[this.npcIds[i]];

						if (other != null && other.type.size == 1 && other.x == player.x && other.z == player.z) {
							this.addNpcOptions(other.type, this.npcIds[i], x, z);
						}
					}

					for ( int i = 0; i < this.playerCount; i++) {
						PlayerEntity other = this.players[this.playerIds[i]];

						if (other != null && other != player && other.x == player.x && other.z == player.z) {
							this.addPlayerOptions(other, this.playerIds[i], x, z);
						}
					}
				}

				this.addPlayerOptions(player, typeId, x, z);
			}

			if (entityType == 3) {
				LinkList objs = this.levelObjStacks[this.currentLevel][x][z];
				if (objs == null) {
					continue;
				}

				for ( ObjStackEntity obj = (ObjStackEntity) objs.peekBack(); obj != null; obj = (ObjStackEntity) objs.next()) {
					ObjType type = ObjType.get(obj.index);
					if (this.objSelected == 1) {
						this.menuOption[this.menuSize] = "Use " + this.objSelectedName + " with @lre@" + type.name;
						this.menuAction[this.menuSize] = 217;
						this.menuParamA[this.menuSize] = obj.index;
						this.menuParamB[this.menuSize] = x;
						this.menuParamC[this.menuSize] = z;
						this.menuSize++;
					} else if (this.spellSelected != 1) {
						for ( int op = 4; op >= 0; op--) {
							if (type.ops != null && type.ops[op] != null) {
								this.menuOption[this.menuSize] = type.ops[op] + " @lre@" + type.name;
								if (op == 0) {
									this.menuAction[this.menuSize] = 224;
								}

								if (op == 1) {
									this.menuAction[this.menuSize] = 993;
								}

								if (op == 2) {
									this.menuAction[this.menuSize] = 99;
								}

								if (op == 3) {
									this.menuAction[this.menuSize] = 746;
								}

								if (op == 4) {
									this.menuAction[this.menuSize] = 877;
								}

								this.menuParamA[this.menuSize] = obj.index;
								this.menuParamB[this.menuSize] = x;
								this.menuParamC[this.menuSize] = z;
								this.menuSize++;
							} else if (op == 2) {
								this.menuOption[this.menuSize] = "Take @lre@" + type.name;
								this.menuAction[this.menuSize] = 99;
								this.menuParamA[this.menuSize] = obj.index;
								this.menuParamB[this.menuSize] = x;
								this.menuParamC[this.menuSize] = z;
								this.menuSize++;
							}
						}

						this.menuOption[this.menuSize] = "Examine @lre@" + type.name;
						if (this.showDebug) {
							this.menuOption[this.menuSize] += "@whi@ (" + (obj.index) + ")";
						}
						this.menuAction[this.menuSize] = 1102;
						this.menuParamA[this.menuSize] = obj.index;
						this.menuParamB[this.menuSize] = x;
						this.menuParamC[this.menuSize] = z;
						this.menuSize++;
					} else if ((this.activeSpellFlags & 0x1) == 1) {
						this.menuOption[this.menuSize] = this.spellCaption + " @lre@" + type.name;
						this.menuAction[this.menuSize] = 965;
						this.menuParamA[this.menuSize] = obj.index;
						this.menuParamB[this.menuSize] = x;
						this.menuParamC[this.menuSize] = z;
						this.menuSize++;
					}
				}
			}
		}
	}

	private void updatePlayers() {
		for ( int i = -1; i < this.playerCount; i++) {
			int index;
			if (i == -1) {
				index = this.LOCAL_PLAYER_INDEX;
			} else {
				index = this.playerIds[i];
			}

			PlayerEntity player = this.players[index];
			if (player != null) {
				this.updateEntity(player, 1);
			}
		}

		updatePlayersCounter++;
		if (updatePlayersCounter > 1406) {
			updatePlayersCounter = 0;
			// ANTICHEAT_CYCLELOGIC6
			this.out.p1isaac(219);
			this.out.p1(0);
			int start = this.out.pos;
			this.out.p1(162);
			this.out.p1(22);
			if ((int) (Math.random() * 2.0D) == 0) {
				this.out.p1(84);
			}
			this.out.p2(31824);
			this.out.p2(13490);
			if ((int) (Math.random() * 2.0D) == 0) {
				this.out.p1(123);
			}
			if ((int) (Math.random() * 2.0D) == 0) {
				this.out.p1(134);
			}
			this.out.p1(100);
			this.out.p1(94);
			this.out.p2(35521);
			this.out.psize1(this.out.pos - start);
		}
	}

	private void drawTileHint() {
		if (this.hintType != 2) {
			return;
		}

		this.projectFromGround((this.hintTileX - this.sceneBaseTileX << 7) + this.hintOffsetX, this.hintHeight * 2, (this.hintTileZ - this.sceneBaseTileZ << 7) + this.hintOffsetZ);

		if (this.projectX > -1 && loopCycle % 20 < 10) {
			this.imageHeadicons[2].draw(this.projectX - 12, this.projectY - 28);
		}
	}

	private void readLocalPlayer( Packet buf, int size) {
		buf.accessBits();

		int hasUpdate = buf.gBit(1);
		if (hasUpdate != 0) {
			int updateType = buf.gBit(2);

			if (updateType == 0) {
				this.entityUpdateIds[this.entityUpdateCount++] = this.LOCAL_PLAYER_INDEX;
			} else if (updateType == 1) {
				int walkDir = buf.gBit(3);
				this.localPlayer.step(false, walkDir);

				int hasMaskUpdate = buf.gBit(1);
				if (hasMaskUpdate == 1) {
					this.entityUpdateIds[this.entityUpdateCount++] = this.LOCAL_PLAYER_INDEX;
				}
			} else if (updateType == 2) {
				int walkDir = buf.gBit(3);
				this.localPlayer.step(true, walkDir);
				int runDir = buf.gBit(3);
				this.localPlayer.step(true, runDir);

				int hasMaskUpdate = buf.gBit(1);
				if (hasMaskUpdate == 1) {
					this.entityUpdateIds[this.entityUpdateCount++] = this.LOCAL_PLAYER_INDEX;
				}
			} else if (updateType == 3) {
				this.currentLevel = buf.gBit(2);
				if (this.showDebug) {
					this.userTileMarkers = new Tile[4];
					this.userTileMarkerIndex = 0;
				}
				int localX = buf.gBit(7);
				int localZ = buf.gBit(7);
				int jump = buf.gBit(1);
				this.localPlayer.move(jump == 1, localX, localZ);

				int hasMaskUpdate = buf.gBit(1);
				if (hasMaskUpdate == 1) {
					this.entityUpdateIds[this.entityUpdateCount++] = this.LOCAL_PLAYER_INDEX;
				}
			}
		}
	}

	private void drawChatback() {
		this.areaChatback.bind();
		Draw3D.lineOffset = this.areaChatbackOffsets;
		this.imageChatback.draw(0, 0);
		if (this.showSocialInput) {
			this.fontBold12.drawStringCenter(239, 40, this.socialMessage, 0);
			this.fontBold12.drawStringCenter(239, 60, this.socialInput + "*", 128);
		} else if (this.chatbackInputOpen) {
			this.fontBold12.drawStringCenter(239, 40, "Enter amount:", 0);
			this.fontBold12.drawStringCenter(239, 60, this.chatbackInput + "*", 128);
		} else if (this.modalMessage != null) {
			this.fontBold12.drawStringCenter(239, 40, this.modalMessage, 0);
			this.fontBold12.drawStringCenter(239, 60, "Click to continue", 128);
		} else if (this.chatInterfaceId != -1) {
			this.drawInterface(ComType.instances[this.chatInterfaceId], 0, 0, 0);
		} else if (this.stickyChatInterfaceId == -1) {
			PixFont font = this.fontPlain12;
			int line = 0;
			Draw2D.setBounds(77, 463, 0, 0);
			for ( int i = 0; i < 100; i++) {
				if (this.messageText[i] != null) {
					int type = this.messageType[i];
					int offset = this.chatScrollOffset + 70 - line * 14;
					if (type == 0) {
						if (offset > 0 && offset < 110) {
							font.drawString(4, offset, this.messageText[i], 0);
						}
						line++;
					}
					if (type == 1) {
						if (offset > 0 && offset < 110) {
							font.drawString(4, offset, this.messageSender[i] + ":", 16777215);
							font.drawString(font.stringWidth(this.messageSender[i]) + 12, offset, this.messageText[i], 255);
						}
						line++;
					}
					if (type == 2 && (this.publicChatSetting == 0 || this.publicChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
						if (offset > 0 && offset < 110) {
							font.drawString(4, offset, this.messageSender[i] + ":", 0);
							font.drawString(font.stringWidth(this.messageSender[i]) + 12, offset, this.messageText[i], 255);
						}
						line++;
					}
					if ((type == 3 || type == 7) && this.splitPrivateChat == 0 && (type == 7 || this.privateChatSetting == 0 || this.privateChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
						if (offset > 0 && offset < 110) {
							font.drawString(4, offset, "From " + this.messageSender[i] + ":", 0);
							font.drawString(font.stringWidth("From " + this.messageSender[i]) + 12, offset, this.messageText[i], 8388608);
						}
						line++;
					}
					if (type == 4 && (this.tradeChatSetting == 0 || this.tradeChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
						if (offset > 0 && offset < 110) {
							font.drawString(4, offset, this.messageSender[i] + " " + this.messageText[i], 8388736);
						}
						line++;
					}
					if (type == 5 && this.splitPrivateChat == 0 && this.privateChatSetting < 2) {
						if (offset > 0 && offset < 110) {
							font.drawString(4, offset, this.messageText[i], 8388608);
						}
						line++;
					}
					if (type == 6 && this.splitPrivateChat == 0 && this.privateChatSetting < 2) {
						if (offset > 0 && offset < 110) {
							font.drawString(4, offset, "To " + this.messageSender[i] + ":", 0);
							font.drawString(font.stringWidth("To " + this.messageSender[i]) + 12, offset, this.messageText[i], 8388608);
						}
						line++;
					}
					if (type == 8 && (this.tradeChatSetting == 0 || this.tradeChatSetting == 1 && this.isFriend(this.messageSender[i]))) {
						if (offset > 0 && offset < 110) {
							font.drawString(4, offset, this.messageSender[i] + " " + this.messageText[i], 13350793);
						}
						line++;
					}
				}
			}
			Draw2D.resetBounds();
			this.chatScrollHeight = line * 14 + 7;
			if (this.chatScrollHeight < 78) {
				this.chatScrollHeight = 78;
			}
			this.drawScrollbar(463, 0, this.chatScrollHeight - this.chatScrollOffset - 77, this.chatScrollHeight, 77);
			font.drawString(4, 90, JString.formatName(this.username) + ":", 0);
			font.drawString(font.stringWidth(this.username + ": ") + 6, 90, this.chatTyped + "*", 255);
			Draw2D.drawHorizontalLine(0, 77, 0, 479);
		} else {
			this.drawInterface(ComType.instances[this.stickyChatInterfaceId], 0, 0, 0);
		}
		if (this.menuVisible && this.menuArea == 2) {
			this.drawMenu();
		}
		this.areaChatback.draw(super.graphics, 22, 375);
		this.areaViewport.bind();
		Draw3D.lineOffset = this.areaViewportOffsets;
	}

	private boolean read() {
		if (this.stream == null) {
			return false;
		}

		try {
			int available = this.stream.available();
			if (available == 0) {
				return false;
			}

			if (this.packetType == -1) {
				this.stream.read(this.in.data, 0, 1);
				this.packetType = this.in.data[0] & 0xFF;
				if (this.randomIn != null) {
					this.packetType = (this.packetType - this.randomIn.nextInt()) & 0xFF;
				}
				this.packetSize = Protocol.SERVERPROT_SIZES[this.packetType];
				available--;
			}

			if (this.packetSize == -1) {
				if (available <= 0) {
					return false;
				}

				this.stream.read(this.in.data, 0, 1);
				this.packetSize = this.in.data[0] & 0xFF;
				available--;
			}

			if (this.packetSize == -2) {
				if (available <= 1) {
					return false;
				}

				this.stream.read(this.in.data, 0, 2);
				this.in.pos = 0;
				this.packetSize = this.in.g2();
				available -= 2;
			}

			if (available < this.packetSize) {
				return false;
			}

			this.in.pos = 0;
			this.stream.read(this.in.data, 0, this.packetSize);
			this.idleNetCycles = 0;
			this.lastPacketType2 = this.lastPacketType1;
			this.lastPacketType1 = this.lastPacketType0;
			this.lastPacketType0 = this.packetType;

			if (this.packetType == 150) {
				// VARP_SMALL
				int varp = this.in.g2();
				byte value = this.in.g1b();
				this.varCache[varp] = value;
				if (this.varps[varp] != value) {
					this.varps[varp] = value;
					this.updateVarp(varp);
					this.redrawSidebar = true;
					if (this.stickyChatInterfaceId != -1) {
						this.redrawChatback = true;
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 152) {
				// UPDATE_FRIENDLIST
				long username = this.in.g8();
				int world = this.in.g1();
				String displayName = JString.formatName(JString.fromBase37(username));
				for (int i = 0; i < this.friendCount; i++) {
					if (username == this.friendName37[i]) {
						if (this.friendWorld[i] != world) {
							this.friendWorld[i] = world;
							this.redrawSidebar = true;
							if (world > 0) {
								this.addMessage(5, displayName + " has logged in.", "");
							}
							if (world == 0) {
								this.addMessage(5, displayName + " has logged out.", "");
							}
						}
						displayName = null;
						break;
					}
				}
				if (displayName != null && this.friendCount < 100) {
					this.friendName37[this.friendCount] = username;
					this.friendName[this.friendCount] = displayName;
					this.friendWorld[this.friendCount] = world;
					this.friendCount++;
					this.redrawSidebar = true;
				}
				boolean sorted = false;
				while (!sorted) {
					sorted = true;
					for (int i = 0; i < this.friendCount - 1; i++) {
						if (this.friendWorld[i] != nodeId && this.friendWorld[i + 1] == nodeId || this.friendWorld[i] == 0 && this.friendWorld[i + 1] != 0) {
							int oldWorld = this.friendWorld[i];
							this.friendWorld[i] = this.friendWorld[i + 1];
							this.friendWorld[i + 1] = oldWorld;

							String oldName = this.friendName[i];
							this.friendName[i] = this.friendName[i + 1];
							this.friendName[i + 1] = oldName;

							long oldName37 = this.friendName37[i];
							this.friendName37[i] = this.friendName37[i + 1];
							this.friendName37[i + 1] = oldName37;
							this.redrawSidebar = true;
							sorted = false;
						}
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 43) {
				// UPDATE_REBOOT_TIMER
				this.systemUpdateTimer = this.in.g2() * 30;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 80) {
				// DATA_LAND_DONE
				int x = this.in.g1();
				int z = this.in.g1();
				int index = -1;
				for (int i = 0; i < this.sceneMapIndex.length; i++) {
					if (this.sceneMapIndex[i] == (x << 8) + z) {
						index = i;
					}
				}
				if (index != -1) {
					signlink.cachesave("m" + x + "_" + z, this.sceneMapLandData[index]);
					this.sceneState = 1;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 1) {
				// NPC_INFO (Server Cycle)
				this.readNpcInfo(this.in, this.packetSize);
				try {
					if (!PLAY_ALL_SOUNDS)
						SoundPlayer.playLastSound();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 237) {
				// REBUILD_NORMAL
				int zoneX = this.in.g2();
				int zoneZ = this.in.g2();
				if (this.sceneCenterZoneX == zoneX && this.sceneCenterZoneZ == zoneZ && this.sceneState != 0) {
					this.packetType = -1;
					return true;
				}
				this.sceneCenterZoneX = zoneX;
				this.sceneCenterZoneZ = zoneZ;
				this.sceneBaseTileX = (this.sceneCenterZoneX - 6) * 8;
				this.sceneBaseTileZ = (this.sceneCenterZoneZ - 6) * 8;
				this.sceneState = 1;
				this.areaViewport.bind();
				this.fontPlain12.drawStringCenter(257, 151, "Loading - please wait.", 0);
				this.fontPlain12.drawStringCenter(256, 150, "Loading - please wait.", 16777215);
				this.areaViewport.draw(super.graphics, 8, 11);
				signlink.looprate(5);
				int regions = (this.packetSize - 2) / 10;
				this.sceneMapLandData = new byte[regions][];
				this.sceneMapLocData = new byte[regions][];
				this.sceneMapIndex = new int[regions];
				// REBUILD_GETMAPS
				this.out.p1isaac(150);
				this.out.p1(0);
				int mapCount = 0;
				for (int i = 0; i < regions; i++) {
					int mapsquareX = this.in.g1();
					int mapsquareZ = this.in.g1();
					int landCrc = this.in.g4();
					int locCrc = this.in.g4();
					this.sceneMapIndex[i] = (mapsquareX << 8) + mapsquareZ;
					byte[] data;
					if (landCrc != 0) {
						data = signlink.cacheload("m" + mapsquareX + "_" + mapsquareZ);
						if (data != null) {
							this.crc32.reset();
							this.crc32.update(data);
							if ((int) this.crc32.getValue() != landCrc) {
								data = null;
							}
						}
						if (data == null) {
							this.sceneState = 0;
							this.out.p1(0);
							this.out.p1(mapsquareX);
							this.out.p1(mapsquareZ);
							mapCount += 3;
						} else {
							this.sceneMapLandData[i] = data;
						}
					}
					if (locCrc != 0) {
						data = signlink.cacheload("l" + mapsquareX + "_" + mapsquareZ);
						if (data != null) {
							this.crc32.reset();
							this.crc32.update(data);
							if ((int) this.crc32.getValue() != locCrc) {
								data = null;
							}
						}
						if (data == null) {
							this.sceneState = 0;
							this.out.p1(1);
							this.out.p1(mapsquareX);
							this.out.p1(mapsquareZ);
							mapCount += 3;
						} else {
							this.sceneMapLocData[i] = data;
						}
					}
				}
				this.out.psize1(mapCount);
				signlink.looprate(50);
				this.areaViewport.bind();
				if (this.sceneState == 0) {
					this.fontPlain12.drawStringCenter(257, 166, "Map area updated since last visit, so load will take longer this time only", 0);
					this.fontPlain12.drawStringCenter(256, 165, "Map area updated since last visit, so load will take longer this time only", 16777215);
				}
				this.areaViewport.draw(super.graphics, 8, 11);
				int dx = this.sceneBaseTileX - this.mapLastBaseX;
				int dz = this.sceneBaseTileZ - this.mapLastBaseZ;
				this.mapLastBaseX = this.sceneBaseTileX;
				this.mapLastBaseZ = this.sceneBaseTileZ;
				for (int i = 0; i < 8192; i++) {
					NpcEntity npc = this.npcs[i];
					if (npc != null) {
						for ( int j = 0; j < 10; j++) {
							npc.pathTileX[j] -= dx;
							npc.pathTileZ[j] -= dz;
						}
						npc.x -= dx * 128;
						npc.z -= dz * 128;
					}
				}
				for (int i = 0; i < this.MAX_PLAYER_COUNT; i++) {
					PlayerEntity player = this.players[i];
					if (player != null) {
						for ( int j = 0; j < 10; j++) {
							player.pathTileX[j] -= dx;
							player.pathTileZ[j] -= dz;
						}
						player.x -= dx * 128;
						player.z -= dz * 128;
					}
				}
				byte startTileX = 0;
				byte endTileX = 104;
				byte dirX = 1;
				if (dx < 0) {
					startTileX = 103;
					endTileX = -1;
					dirX = -1;
				}
				byte startTileZ = 0;
				byte endTileZ = 104;
				byte dirZ = 1;
				if (dz < 0) {
					startTileZ = 103;
					endTileZ = -1;
					dirZ = -1;
				}
				for ( int x = startTileX; x != endTileX; x += dirX) {
					for ( int z = startTileZ; z != endTileZ; z += dirZ) {
						int lastX = x + dx;
						int lastZ = z + dz;
						for ( int level = 0; level < 4; level++) {
							if (lastX >= 0 && lastZ >= 0 && lastX < 104 && lastZ < 104) {
								this.levelObjStacks[level][x][z] = this.levelObjStacks[level][lastX][lastZ];
							} else {
								this.levelObjStacks[level][x][z] = null;
							}
						}
					}
				}
				for ( LocTemporary loc = (LocTemporary) this.spawnedLocations.peekFront(); loc != null; loc = (LocTemporary) this.spawnedLocations.prev()) {
					loc.x -= dx;
					loc.z -= dz;
					if (loc.x < 0 || loc.z < 0 || loc.x >= 104 || loc.z >= 104) {
						loc.unlink();
					}
				}
				if (this.flagSceneTileX != 0) {
					this.flagSceneTileX -= dx;
					this.flagSceneTileZ -= dz;
				}
				this.cutscene = false;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 197) {
				// IF_SETPLAYERHEAD
				int com = this.in.g2();
				ComType.instances[com].model = this.localPlayer.getHeadModel();
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 25) {
				// HINT_ARROW
				this.hintType = this.in.g1();
				if (this.hintType == 1) {
					this.hintNpc = this.in.g2();
				}
				if (this.hintType >= 2 && this.hintType <= 6) {
					if (this.hintType == 2) {
						this.hintOffsetX = 64;
						this.hintOffsetZ = 64;
					}
					if (this.hintType == 3) {
						this.hintOffsetX = 0;
						this.hintOffsetZ = 64;
					}
					if (this.hintType == 4) {
						this.hintOffsetX = 128;
						this.hintOffsetZ = 64;
					}
					if (this.hintType == 5) {
						this.hintOffsetX = 64;
						this.hintOffsetZ = 0;
					}
					if (this.hintType == 6) {
						this.hintOffsetX = 64;
						this.hintOffsetZ = 128;
					}
					this.hintType = 2;
					this.hintTileX = this.in.g2();
					this.hintTileZ = this.in.g2();
					this.hintHeight = this.in.g1();
				}
				if (this.hintType == 10) {
					this.hintPlayer = this.in.g2();
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 54) {
				// MIDI_SONG
				String name = this.in.gjstr();
				int crc = this.in.g4();
				int length = this.in.g4();
				if (!name.equals(this.currentMidi) && this.midiActive && !lowMemory) {
					this.setMidi(name, crc, length);
				}
				this.currentMidi = name;
				this.midiCrc = crc;
				this.midiSize = length;
				this.nextMusicDelay = 0;
				this.packetType = -1;
				isJingle = false;
				return true;
			}
			if (this.packetType == 142) {
				// LOGOUT
				this.logout();
				this.packetType = -1;
				return false;
			}
			if (this.packetType == 20) {
				// DATA_LOC_DONE
				int x = this.in.g1();
				int z = this.in.g1();
				int index = -1;
				for (int i = 0; i < this.sceneMapIndex.length; i++) {
					if (this.sceneMapIndex[i] == (x << 8) + z) {
						index = i;
					}
				}
				if (index != -1) {
					signlink.cachesave("l" + x + "_" + z, this.sceneMapLocData[index]);
					this.sceneState = 1;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 19) {
				// UNSET_MAP_FLAG
				this.flagSceneTileX = 0;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 139) {
				// UPDATE_UID192
				this.localPid = this.in.g2();
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 151 || this.packetType == 23 || this.packetType == 50 || this.packetType == 191 || this.packetType == 69 || this.packetType == 49 || this.packetType == 223 || this.packetType == 42 || this.packetType == 76 || this.packetType == 59) {
				// Zone Protocol
				this.readZonePacket(this.in, this.packetType);
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 28) {
				// IF_OPENMAINSIDEMODAL
				int main = this.in.g2();
				int side = this.in.g2();
				if (this.chatInterfaceId != -1) {
					this.chatInterfaceId = -1;
					this.redrawChatback = true;
				}
				if (this.chatbackInputOpen) {
					this.chatbackInputOpen = false;
					this.redrawChatback = true;
				}
				this.viewportInterfaceId = main;
				this.sidebarInterfaceId = side;
				this.redrawSidebar = true;
				this.redrawSideicons = true;
				this.pressedContinueOption = false;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 175) {
				// VARP_LARGE
				int varp = this.in.g2();
				int value = this.in.g4();
				this.varCache[varp] = value;
				if (this.varps[varp] != value) {
					this.varps[varp] = value;
					this.updateVarp(varp);
					this.redrawSidebar = true;
					if (this.stickyChatInterfaceId != -1) {
						this.redrawChatback = true;
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 146) {
				// IF_SETANIM
				int com = this.in.g2();
				int seqId = this.in.g2();
				ComType.instances[com].anim = seqId;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 167) {
				// IF_OPENSIDEOVERLAY
				int com = this.in.g2();
				int tab = this.in.g1();
				if (com == 65535) {
					com = -1;
				}
				this.tabInterfaceId[tab] = com;
				this.redrawSidebar = true;
				this.redrawSideicons = true;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 220) {
				// DATA_LOC
				int x = this.in.g1();
				int z = this.in.g1();
				int off = this.in.g2();
				int length = this.in.g2();
				int index = -1;
				for (int i = 0; i < this.sceneMapIndex.length; i++) {
					if (this.sceneMapIndex[i] == (x << 8) + z) {
						index = i;
					}
				}
				if (index != -1) {
					if (this.sceneMapLocData[index] == null || this.sceneMapLocData[index].length != length) {
						this.sceneMapLocData[index] = new byte[length];
					}
					this.in.gdata(this.packetSize - 6, off, this.sceneMapLocData[index]);
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 133) {
				// FINISH_TRACKING
				Packet tracking = InputTracking.stop();
				if (tracking != null) {
					this.out.p1isaac(81);
					this.out.p2(tracking.pos);
					this.out.pdata(tracking.data, tracking.pos, 0);
					tracking.release();
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 98) {
				// UPDATE_INV_FULL
				this.redrawSidebar = true;
				int com = this.in.g2();
				ComType inv = ComType.instances[com];
				int size = this.in.g1();
				for (int i = 0; i < size; i++) {
					inv.invSlotObjId[i] = this.in.g2();
					int count = this.in.g1();
					if (count == 255) {
						count = this.in.g4();
					}
					inv.invSlotObjCount[i] = count;
				}
				for (int i = size; i < inv.invSlotObjId.length; i++) {
					inv.invSlotObjId[i] = 0;
					inv.invSlotObjCount[i] = 0;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 226) {
				// ENABLE_TRACKING
				InputTracking.setEnabled();
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 243) {
				// P_COUNTDIALOG
				this.showSocialInput = false;
				this.chatbackInputOpen = true;
				this.chatbackInput = "";
				this.redrawChatback = true;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 15) {
				// UPDATE_INV_STOP_TRANSMIT
				int com = this.in.g2();
				ComType inv = ComType.instances[com];
				for (int i = 0; i < inv.invSlotObjId.length; i++) {
					inv.invSlotObjId[i] = -1;
					inv.invSlotObjId[i] = 0;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 140) {
				// LAST_LOGIN_INFO
				this.lastAddress = this.in.g4();
				this.daysSinceLastLogin = this.in.g2();
				this.daysSinceRecoveriesChanged = this.in.g1();
				this.unreadMessages = this.in.g2();
				if (this.lastAddress != 0 && this.viewportInterfaceId == -1) {
					signlink.dnslookup(JString.formatIPv4(this.lastAddress));
					this.closeInterfaces();
					short clientCode = 650;
					if (this.daysSinceRecoveriesChanged != 201) {
						clientCode = 655;
					}
					this.reportAbuseInput = "";
					this.reportAbuseMuteOption = false;
					for (int i = 0; i < ComType.instances.length; i++) {
						if (ComType.instances[i] != null && ComType.instances[i].clientCode == clientCode) {
							this.viewportInterfaceId = ComType.instances[i].layer;
							break;
						}
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 126) {
				// TUTORIAL_FLASHSIDE
				this.flashingTab = this.in.g1();
				if (this.flashingTab == this.selectedTab) {
					if (this.flashingTab == 3) {
						this.selectedTab = 1;
					} else {
						this.selectedTab = 3;
					}
					this.redrawSidebar = true;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 212) {
				// MIDI_JINGLE
				if (this.midiActive && !lowMemory) {
					int delay = this.in.g2();
					int length = this.in.g4();
					int remaining = this.packetSize - 6;
					byte[] src = new byte[length];
					BZip2.read(src, length, this.in.data, remaining, this.in.pos);
					this.saveMidi(src, length, 0);
					this.nextMusicDelay = delay;
					isJingle = true;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 254) {
				// SET_MULTIWAY
				this.inMultizone = this.in.g1();
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 12) {
				// SYNTH_SOUND
				int id = this.in.g2();
				int loop = this.in.g1();
				int delay = this.in.g2();
				if (this.waveEnabled && !lowMemory && this.waveCount < 50) {
					this.waveIds[this.waveCount] = id;
					this.waveLoops[this.waveCount] = loop;
					this.waveDelay[this.waveCount] = delay + Wave.delays[id];
					this.waveCount++;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 204) {
				// IF_SETNPCHEAD
				int com = this.in.g2();
				int npcId = this.in.g2();
				NpcType npc = NpcType.get(npcId);
				ComType.instances[com].model = npc.getHeadModel();
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 7) {
				// UPDATE_ZONE_PARTIAL_FOLLOWS
				this.baseX = this.in.g1();
				this.baseZ = this.in.g1();
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 103) {
				// IF_SETRECOL
				int com = this.in.g2();
				int src = this.in.g2();
				int dst = this.in.g2();
				ComType inter = ComType.instances[com];
				Model model = inter.model;
				if (model != null) {
					model.recolor(src, dst);
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 32) {
				// CHAT_FILTER_SETTINGS
				this.publicChatSetting = this.in.g1();
				this.privateChatSetting = this.in.g1();
				this.tradeChatSetting = this.in.g1();
				this.redrawPrivacySettings = true;
				this.redrawChatback = true;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 195) {
				// IF_OPENSIDEMODAL
				int com = this.in.g2();
				this.resetInterfaceAnimation(com);
				if (this.chatInterfaceId != -1) {
					this.chatInterfaceId = -1;
					this.redrawChatback = true;
				}
				if (this.chatbackInputOpen) {
					this.chatbackInputOpen = false;
					this.redrawChatback = true;
				}
				this.sidebarInterfaceId = com;
				this.redrawSidebar = true;
				this.redrawSideicons = true;
				this.viewportInterfaceId = -1;
				this.pressedContinueOption = false;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 14) {
				// IF_OPENCHATMODAL
				int com = this.in.g2();
				this.resetInterfaceAnimation(com);
				if (this.sidebarInterfaceId != -1) {
					this.sidebarInterfaceId = -1;
					this.redrawSidebar = true;
					this.redrawSideicons = true;
				}
				this.chatInterfaceId = com;
				this.redrawChatback = true;
				this.viewportInterfaceId = -1;
				this.pressedContinueOption = false;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 209) {
				// IF_SETPOSITION
				int com = this.in.g2();
				int x = this.in.g2b();
				int z = this.in.g2b();
				ComType inter = ComType.instances[com];
				inter.x = x;
				inter.y = z;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 3) {
				// CAM_LOOKAT
				this.cutscene = true;
				this.cutsceneSrcLocalTileX = this.in.g1();
				this.cutsceneSrcLocalTileZ = this.in.g1();
				this.cutsceneSrcHeight = this.in.g2();
				this.cutsceneMoveSpeed = this.in.g1();
				this.cutsceneMoveAcceleration = this.in.g1();
				if (this.cutsceneMoveAcceleration >= 100) {
					this.cameraX = this.cutsceneSrcLocalTileX * 128 + 64;
					this.cameraZ = this.cutsceneSrcLocalTileZ * 128 + 64;
					this.cameraY = this.getHeightmapY(this.currentLevel, this.cutsceneSrcLocalTileX, this.cutsceneSrcLocalTileZ) - this.cutsceneSrcHeight;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 135) {
				// UPDATE_ZONE_FULL_FOLLOWS
				this.baseX = this.in.g1();
				this.baseZ = this.in.g1();
				for (int x = this.baseX; x < this.baseX + 8; x++) {
					for (int z = this.baseZ; z < this.baseZ + 8; z++) {
						if (this.levelObjStacks[this.currentLevel][x][z] != null) {
							this.levelObjStacks[this.currentLevel][x][z] = null;
							this.sortObjStacks(x, z);
						}
					}
				}
				for ( LocTemporary loc = (LocTemporary) this.spawnedLocations.peekFront(); loc != null; loc = (LocTemporary) this.spawnedLocations.prev()) {
					if (loc.x >= this.baseX && loc.x < this.baseX + 8 && loc.z >= this.baseZ && loc.z < this.baseZ + 8 && loc.plane == this.currentLevel) {
						this.addLoc(loc.plane, loc.x, loc.z, loc.lastLocIndex, loc.lastAngle, loc.lastShape, loc.layer);
						loc.unlink();
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 132) {
				// DATA_LAND
				int x = this.in.g1();
				int z = this.in.g1();
				int offset = this.in.g2();
				int length = this.in.g2();
				int index = -1;
				for (int i = 0; i < this.sceneMapIndex.length; i++) {
					if (this.sceneMapIndex[i] == (x << 8) + z) {
						index = i;
					}
				}
				if (index != -1) {
					if (this.sceneMapLandData[index] == null || this.sceneMapLandData[index].length != length) {
						this.sceneMapLandData[index] = new byte[length];
					}
					this.in.gdata(this.packetSize - 6, offset, this.sceneMapLandData[index]);
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 41) {
				// MESSAGE_PRIVATE
				long from = this.in.g8();
				int messageId = this.in.g4();
				int staffModLevel = this.in.g1();
				boolean ignored = false;
				for (int i = 0; i < 100; i++) {
					if (this.messageIds[i] == messageId) {
						ignored = true;
						break;
					}
				}
				if (staffModLevel <= 1) {
					for (int i = 0; i < this.ignoreCount; i++) {
						if (this.ignoreName37[i] == from) {
							ignored = true;
							break;
						}
					}
				}
				if (!ignored && this.overrideChat == 0) {
					try {
						this.messageIds[this.privateMessageCount] = messageId;
						this.privateMessageCount = (this.privateMessageCount + 1) % 100;
						String uncompressed = WordPack.unpack(this.in, this.packetSize - 13);
						String filtered = WordFilter.filter(uncompressed);
						if (staffModLevel > 1) {
							this.addMessage(7, filtered, JString.formatName(JString.fromBase37(from)));
						} else {
							this.addMessage(3, filtered, JString.formatName(JString.fromBase37(from)));
						}
					} catch ( Exception ex) {
						signlink.reporterror("cde1");
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 193) {
				// RESET_CLIENT_VARCACHE
				for (int i = 0; i < this.varps.length; i++) {
					if (this.varps[i] != this.varCache[i]) {
						this.varps[i] = this.varCache[i];
						this.updateVarp(i);
						this.redrawSidebar = true;
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 87) {
				// IF_SETMODEL
				int com = this.in.g2();
				int model = this.in.g2();
				ComType.instances[com].model = new Model(model);
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 185) {
				// TUTORIAL_OPENCHAT
				int com = this.in.g2b();
				this.stickyChatInterfaceId = com;
				this.redrawChatback = true;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 68) {
				// UPDATE_RUNENERGY
				if (this.selectedTab == 12) {
					this.redrawSidebar = true;
				}
				this.energy = this.in.g1();
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 74) {
				// CAM_MOVETO
				this.cutscene = true;
				this.cutsceneDstLocalTileX = this.in.g1();
				this.cutsceneDstLocalTileZ = this.in.g1();
				this.cutsceneDstHeight = this.in.g2();
				this.cutsceneRotateSpeed = this.in.g1();
				this.cutsceneRotateAcceleration = this.in.g1();
				if (this.cutsceneRotateAcceleration >= 100) {
					int sceneX = this.cutsceneDstLocalTileX * 128 + 64;
					int sceneZ = this.cutsceneDstLocalTileZ * 128 + 64;
					int sceneY = this.getHeightmapY(this.currentLevel, this.cutsceneDstLocalTileX, this.cutsceneDstLocalTileZ) - this.cutsceneDstHeight;
					int deltaX = sceneX - this.cameraX;
					int deltaY = sceneY - this.cameraY;
					int deltaZ = sceneZ - this.cameraZ;
					int distance = (int) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
					this.cameraPitch = (int) (Math.atan2(deltaY, distance) * 325.949D) & 0x7FF;
					this.cameraYaw = (int) (Math.atan2(deltaX, deltaZ) * -325.949D) & 0x7FF;
					if (this.cameraPitch < 128) {
						this.cameraPitch = 128;
					}
					if (this.cameraPitch > 383) {
						this.cameraPitch = 383;
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 84) {
				// IF_SHOWSIDE
				this.selectedTab = this.in.g1();
				this.redrawSidebar = true;
				this.redrawSideicons = true;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 4) {
				// MESSAGE_GAME
				String message = this.in.gjstr();
				long username;
				if (message.endsWith(":tradereq:")) {
					String player = message.substring(0, message.indexOf(":"));
					username = JString.toBase37(player);
					boolean ignored = false;
					for (int i = 0; i < this.ignoreCount; i++) {
						if (this.ignoreName37[i] == username) {
							ignored = true;
							break;
						}
					}
					if (!ignored && this.overrideChat == 0) {
						this.addMessage(4, "wishes to trade with you.", player);
					}
				} else if (message.endsWith(":duelreq:")) {
					String player = message.substring(0, message.indexOf(":"));
					username = JString.toBase37(player);
					boolean ignored = false;
					for (int i = 0; i < this.ignoreCount; i++) {
						if (this.ignoreName37[i] == username) {
							ignored = true;
							break;
						}
					}
					if (!ignored && this.overrideChat == 0) {
						this.addMessage(8, "wishes to duel with you.", player);
					}
				} else {
					this.addMessage(0, message, "");
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 46) {
				// IF_SETOBJECT
				int com = this.in.g2();
				int objId = this.in.g2();
				int zoom = this.in.g2();
				ObjType obj = ObjType.get(objId);
				ComType.instances[com].model = obj.getInterfaceModel(50);
				ComType.instances[com].xan = obj.xan2d;
				ComType.instances[com].yan = obj.yan2d;
				ComType.instances[com].zoom = obj.zoom2d * 100 / zoom;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 168) {
				// IF_OPENMAINMODAL
				int com = this.in.g2();
				this.resetInterfaceAnimation(com);
				if (this.sidebarInterfaceId != -1) {
					this.sidebarInterfaceId = -1;
					this.redrawSidebar = true;
					this.redrawSideicons = true;
				}
				if (this.chatInterfaceId != -1) {
					this.chatInterfaceId = -1;
					this.redrawChatback = true;
				}
				if (this.chatbackInputOpen) {
					this.chatbackInputOpen = false;
					this.redrawChatback = true;
				}
				this.viewportInterfaceId = com;
				this.pressedContinueOption = false;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 2) {
				// IF_SETCOLOUR
				int com = this.in.g2();
				int color = this.in.g2();
				int r = color >> 10 & 0x1F;
				int g = color >> 5 & 0x1F;
				int b = color & 0x1F;
				ComType.instances[com].colour = (r << 19) + (g << 11) + (b << 3);
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 136) {
				// RESET_ANIMS
				for (int i = 0; i < this.players.length; i++) {
					if (this.players[i] != null) {
						this.players[i].primarySeqId = -1;
					}
				}
				for (int i = 0; i < this.npcs.length; i++) {
					if (this.npcs[i] != null) {
						this.npcs[i].primarySeqId = -1;
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 26) {
				// IF_SETHIDE
				int com = this.in.g2();
				boolean hide = this.in.g1() == 1;
				ComType.instances[com].hide = hide;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 21) {
				// UPDATE_IGNORELIST
				this.ignoreCount = this.packetSize / 8;
				for (int i = 0; i < this.ignoreCount; i++) {
					this.ignoreName37[i] = this.in.g8();
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 239) {
				// CAM_RESET
				this.cutscene = false;
				for (int i = 0; i < 5; i++) {
					this.cameraModifierEnabled[i] = false;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 129) {
				// IF_CLOSE
				if (this.sidebarInterfaceId != -1) {
					this.sidebarInterfaceId = -1;
					this.redrawSidebar = true;
					this.redrawSideicons = true;
				}
				if (this.chatInterfaceId != -1) {
					this.chatInterfaceId = -1;
					this.redrawChatback = true;
				}
				if (this.chatbackInputOpen) {
					this.chatbackInputOpen = false;
					this.redrawChatback = true;
				}
				this.viewportInterfaceId = -1;
				this.pressedContinueOption = false;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 201) {
				// IF_SETTEXT
				int com = this.in.g2();
				String text = this.in.gjstr();
				ComType.instances[com].text = text;
				if (ComType.instances[com].layer == this.tabInterfaceId[this.selectedTab]) {
					this.redrawSidebar = true;
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 44) {
				// UPDATE_STAT
				this.redrawSidebar = true;
				int stat = this.in.g1();
				int xp = this.in.g4();
				int level = this.in.g1();
				this.skillExperience[stat] = xp;
				this.skillLevel[stat] = level;
				this.skillBaseLevel[stat] = 1;
				for (int i = 0; i < 98; i++) {
					if (xp >= levelExperience[i]) {
						this.skillBaseLevel[stat] = i + 2;
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 162) {
				// UPDATE_ZONE_PARTIAL_ENCLOSED
				this.baseX = this.in.g1();
				this.baseZ = this.in.g1();
				while (this.in.pos < this.packetSize) {
					int opcode = this.in.g1();
					this.readZonePacket(this.in, opcode);
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 22) {
				// UPDATE_RUNWEIGHT
				if (this.selectedTab == 12) {
					this.redrawSidebar = true;
				}
				this.weightCarried = this.in.g2b();
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 13) {
				// CAM_SHAKE
				int type = this.in.g1();
				int jitter = this.in.g1();
				int wobbleScale = this.in.g1();
				int wobbleSpeed = this.in.g1();
				this.cameraModifierEnabled[type] = true;
				this.cameraModifierJitter[type] = jitter;
				this.cameraModifierWobbleScale[type] = wobbleScale;
				this.cameraModifierWobbleSpeed[type] = wobbleSpeed;
				this.cameraModifierCycle[type] = 0;
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 213) {
				// UPDATE_INV_PARTIAL
				this.redrawSidebar = true;
				int com = this.in.g2();
				ComType inv = ComType.instances[com];
				while (this.in.pos < this.packetSize) {
					int slot = this.in.g1();
					int id = this.in.g2();
					int count = this.in.g1();
					if (count == 255) {
						count = this.in.g4();
					}
					if (slot >= 0 && slot < inv.invSlotObjId.length) {
						inv.invSlotObjId[slot] = id;
						inv.invSlotObjCount[slot] = count;
					}
				}
				this.packetType = -1;
				return true;
			}
			if (this.packetType == 184) {
				// PLAYER_INFO
				this.readPlayerInfo(this.in, this.packetSize);
				if (this.sceneState == 1) {
					this.sceneState = 2;
					World.levelBuilt = this.currentLevel;
					this.buildScene();
				}
				if (lowMemory && this.sceneState == 2 && World.levelBuilt != this.currentLevel) {
					this.areaViewport.bind();
					this.fontPlain12.drawStringCenter(257, 151, "Loading - please wait.", 0);
					this.fontPlain12.drawStringCenter(256, 150, "Loading - please wait.", 16777215);
					this.areaViewport.draw(super.graphics, 8, 11);
					World.levelBuilt = this.currentLevel;
					this.buildScene();
				}
				if (this.currentLevel != this.minimapLevel && this.sceneState == 2) {
					this.minimapLevel = this.currentLevel;
					this.createMinimap(this.currentLevel);
				}
				this.packetType = -1;
				return true;
			}
			signlink.reporterror("T1 - " + this.packetType + "," + this.packetSize + " - " + this.lastPacketType1 + "," + this.lastPacketType2);
			this.logout();
		} catch ( IOException ex) {
			this.tryReconnect();
		} catch ( Exception ex) {
			String error = "T2 - " + this.packetType + "," + this.lastPacketType1 + "," + this.lastPacketType2 + " - " + this.packetSize + "," + (this.sceneBaseTileX + this.localPlayer.pathTileX[0]) + "," + (this.sceneBaseTileZ + this.localPlayer.pathTileZ[0]) + " - ";
			for (int i = 0; i < this.packetSize && i < 50; i++) {
				error = error + this.in.data[i] + ",";
			}
			signlink.reporterror(error);
			this.logout();
		}
		return true;
	}

	private void drawSidebar() {
		this.areaSidebar.bind();
		Draw3D.lineOffset = this.areaSidebarOffsets;
		this.imageInvback.draw(0, 0);
		if (this.sidebarInterfaceId != -1) {
			this.drawInterface(ComType.instances[this.sidebarInterfaceId], 0, 0, 0);
		} else if (this.tabInterfaceId[this.selectedTab] != -1) {
			this.drawInterface(ComType.instances[this.tabInterfaceId[this.selectedTab]], 0, 0, 0);
		}
		if (this.menuVisible && this.menuArea == 1) {
			this.drawMenu();
		}
		this.areaSidebar.draw(super.graphics, 562, 231);
		this.areaViewport.bind();
		Draw3D.lineOffset = this.areaViewportOffsets;
	}

	private boolean isFriend( String username) {
		if (username == null) {
			return false;
		}

		for ( int i = 0; i < this.friendCount; i++) {
			if (username.equalsIgnoreCase(this.friendName[i])) {
				return true;
			}
		}

		return username.equalsIgnoreCase(this.localPlayer.name);
	}

	@Override
	public void init() {
		nodeId = Integer.parseInt(this.getParameter("nodeid"));
		portOffset = Integer.parseInt(this.getParameter("portoff"));

		String lowmem = this.getParameter("lowmem");
		if (lowmem != null && lowmem.equals("1")) {
			setLowMemory();
		} else {
			setHighMemory();
		}

		String free = this.getParameter("free");
		members = free == null || !free.equals("1");

		this.initApplet(789, 532);
	}

	private void readPlayerUpdates( PlayerEntity player, int index, int mask, Packet buf) {
		player.lastMask = mask;
		player.lastMaskCycle = loopCycle;

		if ((mask & 0x1) == 1) {
			int length = buf.g1();
			byte[] data = new byte[length];
			Packet appearance = new Packet(data);
			buf.gdata(length, 0, data);
			this.playerAppearanceBuffer[index] = appearance;
			player.read(appearance);
		}
		if ((mask & 0x2) == 2) {
			int seqId = buf.g2();
			if (seqId == 65535) {
				seqId = -1;
			}
			if (seqId == player.primarySeqId) {
				player.primarySeqLoop = 0;
			}
			int delay = buf.g1();
			if (seqId == -1 || player.primarySeqId == -1 || SeqType.instances[seqId].priority > SeqType.instances[player.primarySeqId].priority || SeqType.instances[player.primarySeqId].priority == 0) {
				player.primarySeqId = seqId;
				player.primarySeqFrame = 0;
				player.primarySeqCycle = 0;
				player.primarySeqDelay = delay;
				player.primarySeqLoop = 0;
			}
		}
		if ((mask & 0x4) == 4) {
			player.targetId = buf.g2();
			if (player.targetId == 65535) {
				player.targetId = -1;
			}
		}
		if ((mask & 0x8) == 8) {
			player.chat = buf.gjstr();
			player.chatColor = 0;
			player.chatStyle = 0;
			player.chatTimer = 150;
			this.addMessage(2, player.chat, player.name);
		}
		if ((mask & 0x10) == 16) {
			player.damage = buf.g1();
			player.damageType = buf.g1();
			player.combatCycle = loopCycle + 400;
			player.health = buf.g1();
			player.totalHealth = buf.g1();
		}
		if ((mask & 0x20) == 32) {
			player.targetTileX = buf.g2();
			player.targetTileZ = buf.g2();
			player.lastFaceX = player.targetTileX;
			player.lastFaceZ = player.targetTileZ;
		}
		if ((mask & 0x40) == 64) {
			int colorEffect = buf.g2();
			int type = buf.g1();
			int length = buf.g1();
			int start = buf.pos;
			if (player.name != null) {
				long username = JString.toBase37(player.name);
				boolean ignored = false;
				if (type <= 1) {
					for ( int i = 0; i < this.ignoreCount; i++) {
						if (this.ignoreName37[i] == username) {
							ignored = true;
							break;
						}
					}
				}
				if (!ignored && this.overrideChat == 0) {
					try {
						String uncompressed = WordPack.unpack(buf, length);
						String filtered = WordFilter.filter(uncompressed);
						player.chat = filtered;
						player.chatColor = colorEffect >> 8;
						player.chatStyle = colorEffect & 0xFF;
						player.chatTimer = 150;
						if (type > 1) {
							this.addMessage(1, filtered, player.name);
						} else {
							this.addMessage(2, filtered, player.name);
						}
					} catch ( Exception ex) {
						signlink.reporterror("cde2");
					}
				}
			}
			buf.pos = start + length;
		}
		if ((mask & 0x100) == 256) {
			player.spotanimId = buf.g2();
			int heightDelay = buf.g4();
			player.spotanimOffset = heightDelay >> 16;
			player.spotanimLastCycle = loopCycle + (heightDelay & 0xFFFF);
			player.spotanimFrame = 0;
			player.spotanimCycle = 0;
			if (player.spotanimLastCycle > loopCycle) {
				player.spotanimFrame = -1;
			}
			if (player.spotanimId == 65535) {
				player.spotanimId = -1;
			}
		}
		if ((mask & 0x200) == 512) {
			player.forceMoveStartSceneTileX = buf.g1();
			player.forceMoveStartSceneTileZ = buf.g1();
			player.forceMoveEndSceneTileX = buf.g1();
			player.forceMoveEndSceneTileZ = buf.g1();
			player.forceMoveEndCycle = buf.g2() + loopCycle;
			player.forceMoveStartCycle = buf.g2() + loopCycle;
			player.forceMoveFaceDirection = buf.g1();
			player.pathLength = 0;
			player.pathTileX[0] = player.forceMoveEndSceneTileX;
			player.pathTileZ[0] = player.forceMoveEndSceneTileZ;
		}
	}

	@Override
	protected void drawProgress( String message, int progress) {
		this.loadTitle();
		if (this.archiveTitle == null) {
			super.drawProgress(message, progress);
		} else {
			this.imageTitle4.bind();
			short x = 360;
			short y = 200;
			byte offsetY = 20;
			this.fontBold12.drawStringCenter(x / 2, y / 2 - offsetY - 26, "RuneScape is loading - please wait...", 16777215);
			int midY = y / 2 - offsetY - 18;
			Draw2D.drawRect(x / 2 - 152, midY, 9179409, 304, 34);
			Draw2D.drawRect(x / 2 - 151, midY + 1, 0, 302, 32);
			Draw2D.fillRect(x / 2 - 150, midY + 2, 9179409, progress * 3, 30);
			Draw2D.fillRect(x / 2 - 150 + progress * 3, midY + 2, 0, 300 - progress * 3, 30);
			this.fontBold12.drawStringCenter(x / 2, y / 2 + 5 - offsetY, message, 16777215);
			this.imageTitle4.draw(super.graphics, 214, 186);
			if (this.redrawTitleBackground) {
				this.redrawTitleBackground = false;
				if (!this.flameActive) {
					this.imageTitle0.draw(super.graphics, 0, 0);
					this.imageTitle1.draw(super.graphics, 661, 0);
				}
				this.imageTitle2.draw(super.graphics, 128, 0);
				this.imageTitle3.draw(super.graphics, 214, 386);
				this.imageTitle5.draw(super.graphics, 0, 265);
				this.imageTitle6.draw(super.graphics, 574, 265);
				this.imageTitle7.draw(super.graphics, 128, 186);
				this.imageTitle8.draw(super.graphics, 574, 186);
			}
		}
	}
}
