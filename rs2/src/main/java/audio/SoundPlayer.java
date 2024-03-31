package audio;

import jagex2.client.Client;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import static sign.signlink.wavevol;


public class SoundPlayer {

	public static HashMap<Integer, AudioInputStream> sounds = new HashMap<>();

	private AudioInputStream stream;
	private DataLine.Info info;
	private Clip sound;

	private InputStream soundStream;
	private int delay;
	private int soundLevel;
	private InputStream arg0;
	public static int volume;

	public SoundPlayer(AudioInputStream stream, int level, int delay) {
		if (level == 0 || volume == 4 || level - volume <= 0) {
			return;
		}
		this.soundStream = stream;
		this.soundLevel = level;
		this.delay = delay;
		run();
	}

	public static void playLastSound() throws IOException {
		if (Client.lastWaveId != -1) {
			new SoundPlayer(sounds.get(Client.lastWaveId), 100, 0);
			Client.lastWaveId = -1;
		}
	}

	public void run() {
		try {
			stream = (AudioInputStream) soundStream;
			info = new DataLine.Info(Clip.class, stream.getFormat());
			sound = (Clip) AudioSystem.getLine(info);
			sound.open(stream);
			FloatControl gain = (FloatControl) sound.getControl(FloatControl.Type.MASTER_GAIN);
			gain.setValue(getDecibels(gain, wavevol));
			if (delay > 0) {
				Thread.sleep(delay);
			}
			sound.start();
			sound.drain();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public float getDecibels(FloatControl volumeControl, int level) {
		float range = volumeControl.getMaximum() - volumeControl.getMinimum();
		float min = volumeControl.getMinimum();
		switch (level) {
			case 0: // 4 in player options
				return min + (range * .90f);
			case -400: // 3
				return min + (range * .74f);
			case -800: // 2
				return min + (range * .62f);
			case -1200: // 1
				return min + (range * .50f);
			default:
				return min;
		}
	}
}