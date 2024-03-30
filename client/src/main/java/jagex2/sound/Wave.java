package jagex2.sound;

import jagex2.io.Packet;

public class Wave {

    private static final Wave[] tracks = new Wave[1000];

    public static final int[] delays = new int[1000];

    public static byte[] waveBytes;

    public static Packet waveBuffer;

    private final SoundTone[] tones = new SoundTone[10];

    private int loopBegin;

    private int loopEnd;

    public static void unpack( Packet dat) {
		waveBytes = new byte[441000];
		waveBuffer = new Packet(waveBytes);
		SoundTone.init();

		while (true) {
			int id = dat.g2();
			if (id == 65535) {
				return;
			}

			tracks[id] = new Wave();
			tracks[id].read(dat);
			delays[id] = tracks[id].trim();
		}
	}

    public static Packet generate( int id, int loopCount) {
		if (tracks[id] == null) {
			return null;
		}

		Wave track = tracks[id];
		return track.getWave(loopCount);
	}

    public void read( Packet dat) {
		for ( int tone = 0; tone < 10; tone++) {
			if (dat.g1() != 0) {
				dat.pos--;
				this.tones[tone] = new SoundTone();
				this.tones[tone].read(dat);
			}
		}

		this.loopBegin = dat.g2();
		this.loopEnd = dat.g2();
	}

    public int trim() {
		int start = 9999999;
		for ( int tone = 0; tone < 10; tone++) {
			if (this.tones[tone] != null && this.tones[tone].start / 20 < start) {
				start = this.tones[tone].start / 20;
			}
		}

		if (this.loopBegin < this.loopEnd && this.loopBegin / 20 < start) {
			start = this.loopBegin / 20;
		}

		if (start == 9999999 || start == 0) {
			return 0;
		}

		for ( int tone = 0; tone < 10; tone++) {
			if (this.tones[tone] != null) {
				this.tones[tone].start -= start * 20;
			}
		}

		if (this.loopBegin < this.loopEnd) {
			this.loopBegin -= start * 20;
			this.loopEnd -= start * 20;
		}

		return start;
	}

    public Packet getWave( int loopCount) {
		int length = this.generate(loopCount);
		waveBuffer.pos = 0;
		waveBuffer.p4(0x52494646); // "RIFF" ChunkID
		waveBuffer.ip4(length + 36); // ChunkSize
		waveBuffer.p4(0x57415645); // "WAVE" format
		waveBuffer.p4(0x666d7420); // "fmt " chunk id
		waveBuffer.ip4(16); // chunk size
		waveBuffer.ip2(1); // audio format
		waveBuffer.ip2(1); // num channels
		waveBuffer.ip4(22050); // sample rate
		waveBuffer.ip4(22050); // byte rate
		waveBuffer.ip2(1); // block align
		waveBuffer.ip2(8); // bits per sample
		waveBuffer.p4(0x64617461); // "data"
		waveBuffer.ip4(length);
		waveBuffer.pos += length;
		return waveBuffer;
	}

    private int generate( int loopCount) {
		int duration = 0;
		for ( int tone = 0; tone < 10; tone++) {
			if (this.tones[tone] != null && this.tones[tone].length + this.tones[tone].start > duration) {
				duration = this.tones[tone].length + this.tones[tone].start;
			}
		}

		if (duration == 0) {
			return 0;
		}

		int sampleCount = duration * 22050 / 1000;
		int loopStart = this.loopBegin * 22050 / 1000;
		int loopStop = this.loopEnd * 22050 / 1000;
		if (loopStart < 0 || loopStop < 0 || loopStop > sampleCount || loopStart >= loopStop) {
			loopCount = 0;
		}

		int totalSampleCount = sampleCount + (loopStop - loopStart) * (loopCount - 1);
		for ( int sample = 44; sample < totalSampleCount + 44; sample++) {
			waveBytes[sample] = -128;
		}

		for ( int tone = 0; tone < 10; tone++) {
			if (this.tones[tone] != null) {
				int toneSampleCount = this.tones[tone].length * 22050 / 1000;
				int start = this.tones[tone].start * 22050 / 1000;
				int[] samples = this.tones[tone].generate(toneSampleCount, this.tones[tone].length);

				for (int sample = 0; sample < toneSampleCount; sample++) {
					waveBytes[sample + start + 44] += (byte) (samples[sample] >> 8);
				}
			}
		}

		if (loopCount > 1) {
			loopStart += 44;
			loopStop += 44;
			sampleCount += 44;
			totalSampleCount += 44;

			int endOffset = totalSampleCount - sampleCount;
			for (int sample = sampleCount - 1; sample >= loopStop; sample--) {
				waveBytes[sample + endOffset] = waveBytes[sample];
			}

			for ( int loop = 1; loop < loopCount; loop++) {
				int offset = (loopStop - loopStart) * loop;

				for (int sample = loopStart; sample < loopStop; sample++) {
					waveBytes[sample + offset] = waveBytes[sample];
				}
			}

			totalSampleCount -= 44;
		}

		return totalSampleCount;
	}

	public static final class SoundTone {

        private SoundEnvelope frequencyBase;

        private SoundEnvelope amplitudeBase;

        private SoundEnvelope frequencyModRate;

        private SoundEnvelope frequencyModRange;

        private SoundEnvelope amplitudeModRate;

        private SoundEnvelope amplitudeModRange;

        private SoundEnvelope release;

        private SoundEnvelope attack;

        private final int[] harmonicVolume = new int[5];

        private final int[] harmonicSemitone = new int[5];

        private final int[] harmonicDelay = new int[5];

        private int reverbDelay;

        private int reverbVolume = 100;

        public int length = 500;

        public int start;

        public static int[] buffer;

        public static int[] noise;

        public static int[] sin;

        public static final int[] tmpPhases = new int[5];

        public static final int[] tmpDelays = new int[5];

        public static final int[] tmpVolumes = new int[5];

        public static final int[] tmpSemitones = new int[5];

        public static final int[] tmpStarts = new int[5];

        public static void init() {
			noise = new int[32768];
			for ( int i = 0; i < 32768; i++) {
				if (Math.random() > 0.5D) {
					noise[i] = 1;
				} else {
					noise[i] = -1;
				}
			}

			sin = new int[32768];
			for ( int i = 0; i < 32768; i++) {
				sin[i] = (int) (Math.sin((double) i / 5215.1903D) * 16384.0D);
			}

			buffer = new int[220500]; // 10s * 22050 KHz
		}

        public int[] generate( int sampleCount, int length) {
			for ( int sample = 0; sample < sampleCount; sample++) {
				buffer[sample] = 0;
			}

			if (length < 10) {
				return buffer;
			}

			double samplesPerStep = (double) sampleCount / ((double) length + 0.0D);

			this.frequencyBase.reset();
			this.amplitudeBase.reset();

			int frequencyStart = 0;
			int frequencyDuration = 0;
			int frequencyPhase = 0;

			if (this.frequencyModRate != null) {
				this.frequencyModRate.reset();
				this.frequencyModRange.reset();
				frequencyStart = (int) ((double) (this.frequencyModRate.end - this.frequencyModRate.start) * 32.768D / samplesPerStep);
				frequencyDuration = (int) ((double) this.frequencyModRate.start * 32.768D / samplesPerStep);
			}

			int amplitudeStart = 0;
			int amplitudeDuration = 0;
			int amplitudePhase = 0;
			if (this.amplitudeModRate != null) {
				this.amplitudeModRate.reset();
				this.amplitudeModRange.reset();
				amplitudeStart = (int) ((double) (this.amplitudeModRate.end - this.amplitudeModRate.start) * 32.768D / samplesPerStep);
				amplitudeDuration = (int) ((double) this.amplitudeModRate.start * 32.768D / samplesPerStep);
			}

			for ( int harmonic = 0; harmonic < 5; harmonic++) {
				if (this.harmonicVolume[harmonic] != 0) {
					tmpPhases[harmonic] = 0;
					tmpDelays[harmonic] = (int) ((double) this.harmonicDelay[harmonic] * samplesPerStep);
					tmpVolumes[harmonic] = (this.harmonicVolume[harmonic] << 14) / 100;
					tmpSemitones[harmonic] = (int) ((double) (this.frequencyBase.end - this.frequencyBase.start) * 32.768D * Math.pow(1.0057929410678534D, this.harmonicSemitone[harmonic]) / samplesPerStep);
					tmpStarts[harmonic] = (int) ((double) this.frequencyBase.start * 32.768D / samplesPerStep);
				}
			}

			for ( int sample = 0; sample < sampleCount; sample++) {
				int frequency = this.frequencyBase.evaluate(sampleCount);
				int amplitude = this.amplitudeBase.evaluate(sampleCount);

				if (this.frequencyModRate != null) {
					int rate = this.frequencyModRate.evaluate(sampleCount);
					int range = this.frequencyModRange.evaluate(sampleCount);
					frequency += this.generate(range, frequencyPhase, this.frequencyModRate.form) >> 1;
					frequencyPhase += (rate * frequencyStart >> 16) + frequencyDuration;
				}

				if (this.amplitudeModRate != null) {
					int rate = this.amplitudeModRate.evaluate(sampleCount);
					int range = this.amplitudeModRange.evaluate(sampleCount);
					amplitude = amplitude * ((this.generate(range, amplitudePhase, this.amplitudeModRate.form) >> 1) + 32768) >> 15;
					amplitudePhase += (rate * amplitudeStart >> 16) + amplitudeDuration;
				}

				for (int harmonic = 0; harmonic < 5; harmonic++) {
					if (this.harmonicVolume[harmonic] != 0) {
						int position = sample + tmpDelays[harmonic];

						if (position < sampleCount) {
							buffer[position] += this.generate(amplitude * tmpVolumes[harmonic] >> 15, tmpPhases[harmonic], this.frequencyBase.form);
							tmpPhases[harmonic] += (frequency * tmpSemitones[harmonic] >> 16) + tmpStarts[harmonic];
						}
					}
				}
			}

			if (this.release != null) {
				this.release.reset();
				this.attack.reset();

				int counter = 0;
				boolean muted = true;

				for (int sample = 0; sample < sampleCount; sample++) {
					int releaseValue = this.release.evaluate(sampleCount);
					int attackValue = this.attack.evaluate(sampleCount);

					int threshold;
					if (muted) {
						threshold = this.release.start + ((this.release.end - this.release.start) * releaseValue >> 8);
					} else {
						threshold = this.release.start + ((this.release.end - this.release.start) * attackValue >> 8);
					}

					counter += 256;
					if (counter >= threshold) {
						counter = 0;
						muted = !muted;
					}

					if (muted) {
						buffer[sample] = 0;
					}
				}
			}

			if (this.reverbDelay > 0 && this.reverbVolume > 0) {
				int start = (int) ((double) this.reverbDelay * samplesPerStep);

				for (int sample = start; sample < sampleCount; sample++) {
					buffer[sample] += buffer[sample - start] * this.reverbVolume / 100;
				}
			}

			for (int sample = 0; sample < sampleCount; sample++) {
				if (buffer[sample] < -32768) {
					buffer[sample] = -32768;
				}

				if (buffer[sample] > 32767) {
					buffer[sample] = 32767;
				}
			}

			return buffer;
		}

        private int generate( int amplitude, int phase, int form) {
			if (form == 1) {
				return (phase & 0x7FFF) < 16384 ? amplitude : -amplitude;
			} else if (form == 2) {
				return sin[phase & 0x7FFF] * amplitude >> 14;
			} else if (form == 3) {
				return ((phase & 0x7FFF) * amplitude >> 14) - amplitude;
			} else if (form == 4) {
				return noise[phase / 2607 & 0x7FFF] * amplitude;
			} else {
				return 0;
			}
		}

        public void read( Packet dat) {
			this.frequencyBase = new SoundEnvelope();
			this.frequencyBase.read(dat);

			this.amplitudeBase = new SoundEnvelope();
			this.amplitudeBase.read(dat);

			if (dat.g1() != 0) {
				dat.pos--;

				this.frequencyModRate = new SoundEnvelope();
				this.frequencyModRate.read(dat);

				this.frequencyModRange = new SoundEnvelope();
				this.frequencyModRange.read(dat);
			}

			if (dat.g1() != 0) {
				dat.pos--;

				this.amplitudeModRate = new SoundEnvelope();
				this.amplitudeModRate.read(dat);

				this.amplitudeModRange = new SoundEnvelope();
				this.amplitudeModRange.read(dat);
			}

			if (dat.g1() != 0) {
				dat.pos--;

				this.release = new SoundEnvelope();
				this.release.read(dat);

				this.attack = new SoundEnvelope();
				this.attack.read(dat);
			}

			for ( int harmonic = 0; harmonic < 10; harmonic++) {
				int volume = dat.gsmarts();
				if (volume == 0) {
					break;
				}

				this.harmonicVolume[harmonic] = volume;
				this.harmonicSemitone[harmonic] = dat.gsmart();
				this.harmonicDelay[harmonic] = dat.gsmarts();
			}

			this.reverbDelay = dat.gsmarts();
			this.reverbVolume = dat.gsmarts();
			this.length = dat.g2();
			this.start = dat.g2();
		}
	}

	public static final class SoundEnvelope {

        private int length;

        private int[] shapeDelta;

        private int[] shapePeak;

        public int start;

        public int end;

        public int form;

        private int threshold;

        private int position;

        private int delta;

        private int amplitude;

        private int ticks;

        public void read( Packet dat) {
			this.form = dat.g1();
			this.start = dat.g4();
			this.end = dat.g4();
			this.length = dat.g1();
			this.shapeDelta = new int[this.length];
			this.shapePeak = new int[this.length];

			for ( int i = 0; i < this.length; i++) {
				this.shapeDelta[i] = dat.g2();
				this.shapePeak[i] = dat.g2();
			}
		}

        public void reset() {
			this.threshold = 0;
			this.position = 0;
			this.delta = 0;
			this.amplitude = 0;
			this.ticks = 0;
		}

        public int evaluate( int delta) {
			if (this.ticks >= this.threshold) {
				this.amplitude = this.shapePeak[this.position++] << 15;

				if (this.position >= this.length) {
					this.position = this.length - 1;
				}

				this.threshold = (int) ((double) this.shapeDelta[this.position] / 65536.0D * (double) delta);
				if (this.threshold > this.ticks) {
					this.delta = ((this.shapePeak[this.position] << 15) - this.amplitude) / (this.threshold - this.ticks);
				}
			}

			this.amplitude += this.delta;
			this.ticks++;
			return this.amplitude - this.delta >> 15;
		}
	}
}
