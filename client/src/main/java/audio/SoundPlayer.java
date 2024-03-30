package audio;

import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

import static sign.signlink.wavevol;


public class SoundPlayer {

	private AudioInputStream stream;
	private DataLine.Info info;
	private Clip sound;

	private InputStream soundStream;
	private Thread player;
	private int delay;
	private int soundLevel;
	private InputStream arg0;
	public static int volume;

	public SoundPlayer(InputStream stream, int level, int delay) {
		if (level == 0 || volume == 4 || level - volume <= 0) {
			return;
		}
		this.soundStream = stream;
		this.soundLevel = level;
		this.delay = delay;
		run();
	}

	public void run() {
		try {
			stream = AudioSystem.getAudioInputStream(soundStream);
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
			stream.close();
			player.interrupt();
		} catch (Exception e) {
			player.interrupt();
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