package sign;

import audio.MidiPlayer;
import org.openrs2.deob.annotation.OriginalArg;
import org.openrs2.deob.annotation.OriginalClass;
import org.openrs2.deob.annotation.OriginalMember;
import org.openrs2.deob.annotation.Pc;

import java.applet.Applet;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public class signlink implements Runnable {

	public static final int clientversion = 225;

	public static int uid;

	public static Applet mainapp;

	public static boolean sunjava;

	private static boolean active;

	private static int threadliveid;

	private static InetAddress socketip;

	private static int socketreq;

	private static int savelen;

	private static boolean midiplay;

	private static int midipos;

	public static int midivol;

	public static int midifade;

	private static boolean waveplay;

	private static int wavepos;

	public static int wavevol;

	private static Socket socket = null;

	private static int threadreqpri = 1;

	private static Runnable threadreq = null;

	private static String dnsreq = null;

	public static String dns = null;

	private static String loadreq = null;

	private static byte[] loadbuf = null;

	private static String savereq = null;

	private static byte[] savebuf = null;

	private static String urlreq = null;

	private static DataInputStream urlstream = null;

	private static int looprate = 50;

	public static String midi = null;

	public static String wave = null;

	public static boolean reporterror = true;

	public static String errorname = "";

	public static void startpriv( InetAddress address) {
		threadliveid = (int) (Math.random() * 9.9999999E7D);

		if (active) {
			try {
				Thread.sleep(500L);
			} catch ( Exception ignored) {
			}
			active = false;
		}

		socketreq = 0;
		threadreq = null;
		dnsreq = null;
		loadreq = null;
		savereq = null;
		urlreq = null;
		socketip = address;

		Thread thread = new Thread(new signlink());
		thread.setDaemon(true);
		thread.start();

		while (!active) {
			try {
				Thread.sleep(50L);
			} catch ( Exception ignored) {
			}
		}
	}

	public static String findcachedir() {
		String[] paths = new String[] {
			"c:/windows/", "c:/winnt/", "d:/windows/", "d:/winnt/", "e:/windows/", "e:/winnt/", "f:/windows/", "f:/winnt/", "c:/",
			"~/", "/tmp/", ""
		};
		String store = ".file_store_32";

		for ( int i = 0; i < paths.length; i++) {
			try {
				String dir = paths[i];
				File cache;

				if (dir.length() > 0) {
					cache = new File(dir);

					if (!cache.exists() || !cache.canWrite()) {
						System.out.println("Unable to find or write to cache directory: " + dir);
						continue;
					}
				}

				cache = new File(dir + store);
				if ((!cache.exists() && !cache.mkdir()) || !cache.canWrite()) {
					System.out.println("Unable to find or write to cache directory: " + dir + store + "/");
					continue;
				}

				return dir + store + "/";
			} catch ( Exception _ex) {
			}
		}

		return null;
	}

	public static int getuid( String cacheDir) {
		if (cacheDir == null) {
			return 0;
		}

		try {
			File uid = new File(cacheDir + "uid.dat");
			if (!uid.exists() || uid.length() < 4L) {
				DataOutputStream stream = new DataOutputStream(new FileOutputStream(cacheDir + "uid.dat"));
				stream.writeInt((int) (Math.random() * 9.9999999E7D));
				stream.close();
			}
		} catch ( Exception ignored) {
		}

		try {
			DataInputStream stream = new DataInputStream(new FileInputStream(cacheDir + "uid.dat"));
			int uid = stream.readInt();
			stream.close();
			return uid + 1;
		} catch ( Exception ignored) {
			return 0;
		}
	}

	public static long gethash( String str) {
		String trimmed = str.trim();
		long hash = 0L;

		for ( int i = 0; i < trimmed.length() && i < 12; i++) {
			char c = trimmed.charAt(i);
			hash *= 37L;

			if (c >= 'A' && c <= 'Z') {
				hash += c + 1 - 65;
			} else if (c >= 'a' && c <= 'z') {
				hash += c + 1 - 97;
			} else if (c >= '0' && c <= '9') {
				hash += c + 27 - 48;
			}
		}

		return hash;
	}

	public static void looprate( int rate) {
		looprate = rate;
	}

	public static synchronized byte[] cacheload( String name) {
		if (!active) {
			return null;
		}

		loadreq = String.valueOf(gethash(name));
		while (loadreq != null) {
			try {
				Thread.sleep(1L);
			} catch ( Exception ignored) {
			}
		}

		return loadbuf;
	}

	public static synchronized void cachesave( String name, byte[] src) {
		if (!active || src.length > 2000000) {
			return;
		}

		while (savereq != null) {
			try {
				Thread.sleep(1L);
			} catch ( Exception ignored) {
			}
		}

		savelen = src.length;
		savebuf = src;
		savereq = String.valueOf(gethash(name));

		while (savereq != null) {
			try {
				Thread.sleep(1L);
			} catch ( Exception ignored) {
			}
		}
	}

	public static synchronized Socket opensocket( int port) throws IOException {
		socketreq = port;

		while (socketreq != 0) {
			try {
				Thread.sleep(50L);
			} catch ( Exception ignored) {
			}
		}

		if (socket == null) {
			throw new IOException("could not open socket");
		}

		return socket;
	}

	public static synchronized DataInputStream openurl( String url) throws IOException {
		urlreq = url;

		while (urlreq != null) {
			try {
				Thread.sleep(50L);
			} catch ( Exception ignored) {
			}
		}

		if (urlstream == null) {
			throw new IOException("could not open: " + url);
		}

		return urlstream;
	}

	public static synchronized void dnslookup( String hostname) {
		dns = hostname;
		dnsreq = hostname;
	}

	public static synchronized void startthread( Runnable runnable, int priority) {
		threadreqpri = priority;
		threadreq = runnable;
	}

	public static synchronized boolean wavesave( byte[] src, int length) {
		if (length > 2000000 || savereq != null) {
			return false;
		}

		wavepos = (wavepos + 1) % 5;
		savelen = length;
		savebuf = src;
		waveplay = true;
		savereq = "sound" + wavepos + ".wav";
		return true;
	}

	public static synchronized boolean wavereplay() {
		if (savereq != null) {
			return false;
		}

		savebuf = null;
		waveplay = true;
		savereq = "sound" + wavepos + ".wav";
		return true;
	}

	public static synchronized void midisave( byte[] src, int length) {
		if (length > 2000000 || savereq != null) {
			return;
		}

		midipos = (midipos + 1) % 5;
		savelen = length;
		savebuf = src;
		midiplay = true;
		savereq = "jingle" + midipos + ".mid";
	}

	public static void reporterror( String message) {
		if (!reporterror || !active) {
			return;
		}

		System.out.println("Error: " + message);

		try {
			message = message.replace('@', '_');
			message = message.replace('&', '_');
			message = message.replace('#', '_');

			DataInputStream stream = openurl("reporterror" + signlink.clientversion + ".cgi?error=" + errorname + " " + message);
			stream.readLine();
			stream.close();
		} catch ( IOException ignored) {
		}
	}

	public void run() {
		active = true;

		String cacheDir = findcachedir();
		uid = getuid(cacheDir);
		if (cacheDir == null) {
			cacheDir = "";
		}

		int threadId = threadliveid;
		while (threadliveid == threadId) {
			if (socketreq != 0) {
				try {
					socket = new Socket(socketip, socketreq);
				} catch ( Exception ignored) {
					socket = null;
				}

				socketreq = 0;
			} else if (threadreq != null) {
				Thread thread = new Thread(threadreq);
				thread.setDaemon(true);
				thread.start();
				thread.setPriority(threadreqpri);
				threadreq = null;
			} else if (dnsreq != null) {
				try {
					dns = InetAddress.getByName(dnsreq).getHostName();
				} catch ( Exception ignored) {
					dns = "unknown";
				}

				dnsreq = null;
			} else if (loadreq != null) {
				loadbuf = null;
				try {
					File file = new File(cacheDir + loadreq);
					if (file.exists()) {
						int length = (int) file.length();
						loadbuf = new byte[length];

						DataInputStream stream = new DataInputStream(new FileInputStream(cacheDir + loadreq));
						stream.readFully(loadbuf, 0, length);
						stream.close();
					}
				} catch ( Exception ignored) {
				}
				loadreq = null;
			} else if (savereq != null) {
				if (savebuf != null) {
					try {
						FileOutputStream stream = new FileOutputStream(cacheDir + savereq);
						stream.write(savebuf, 0, savelen);
						stream.close();
					} catch ( Exception ignored) {
					}
				}

				if (waveplay) {
					wave = cacheDir + savereq;
					waveplay = false;
				}

				if (midiplay) {
					midi = cacheDir + savereq;
					MidiPlayer.playSong(savereq, false);
					midiplay = false;
				}

				savereq = null;
			} else if (urlreq != null) {
				try {
					urlstream = new DataInputStream((new URL(mainapp.getCodeBase(), urlreq)).openStream());
				} catch ( Exception ignored) {
					urlstream = null;
				}
				urlreq = null;
			}

			try {
				Thread.sleep(looprate);
			} catch ( Exception ignored) {
			}
		}
	}
}
