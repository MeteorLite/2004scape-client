package audio;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static sign.signlink.midi;

public class MidiPlayer {

  static Soundbank soundfont;
  static Sequencer sequencer;
  public static Synthesizer synthesizer;

  private static boolean isRunning = false;

  public static int volume = 20;

  private static int fadeTime = 3000;

  private static Thread fadeThread;

  public static HashMap<Integer, byte[]> songs = new HashMap<>();

  private static String currentSong;

  private static long length = -1;

  static boolean firstReset = true;

  public static boolean isJingle = false;

  public static void playSong(String name, boolean forced) {

    if (firstReset) {
      volume = 40;
      firstReset = false;
    }

    if (midi == null || volume == 0) {
      sequencer.stop();
    } else {
      if (sequencer != null)
        if (sequencer.getSequence() != null)
          sequencer.start();
    }

    if (midi == null || (midi.equals(currentSong)  && !forced)) {
      return;
    }

    File songFile = new File(midi);
    if (!songFile.exists())
      return;

    currentSong = midi;

    try {
      if (soundfont == null) {
        soundfont = MidiSystem.getSoundbank(
            Objects.requireNonNull(
                ClassLoader.getSystemClassLoader().getResourceAsStream("SCC1_Florestan.sf2")));
      }
      if (sequencer == null) {
        sequencer = MidiSystem.getSequencer(false);
      }

      if (synthesizer == null) {
        synthesizer = MidiSystem.getSynthesizer();
      }

      if (isRunning && !isJingle) {
        fadeOut();
      } else {
        if (sequencer.isRunning())
          sequencer.stop();
        sequencer = MidiSystem.getSequencer(false);
        synthesizer = MidiSystem.getSynthesizer();

        sequencer.open();
        synthesizer.open();
        synthesizer.loadAllInstruments(soundfont);

        sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
        Sequence sequence = null;
        try {
          sequence = MidiSystem.getSequence(songFile);
          sequencer.setSequence(removeVolumeControlEvents(sequence));
        } catch (InvalidMidiDataException | IOException e) {
          e.printStackTrace();
        }

        length = sequencer.getSequence().getTickLength();

        sequencer.start();
        isRunning = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void fadeOut() {

    final long fadeStart = System.currentTimeMillis();
    fadeThread = new Thread(new Runnable() {
      @Override
      public void run() {
        int oldVolume = volume;
        while (System.currentTimeMillis() < fadeStart + fadeTime) {
          if (volume >= 10) {
            volume -= 10;
            MidiChannel[] channels = synthesizer.getChannels();
            for (MidiChannel channel : channels) {
              channel.controlChange(7, volume);
            }
          }

          try {
            Thread.sleep(fadeTime / 10);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }
        isRunning = false;
        playSong("", true);
        volume = oldVolume;
      }
    });

    fadeThread.start();
  }

  public static void stop() {
    if (synthesizer != null) {
      synthesizer.close();
      soundfont = null;
      sequencer = null;
      synthesizer = null;
    }

    currentSong = null;
  }

  private static Sequence removeVolumeControlEvents(Sequence sequence) throws InvalidMidiDataException {
    Sequence newSequence = new Sequence(sequence.getDivisionType(), sequence.getResolution());
    Track[] tracks = sequence.getTracks();

    for (Track track : tracks) {
      Track newTrack = newSequence.createTrack();
      for (int i = 0; i < track.size(); i++) {
        MidiEvent event = track.get(i);
        MidiMessage message = event.getMessage();
        if (!(message instanceof ShortMessage)) {
          // Not a ShortMessage, add it directly to the new track
          newTrack.add(event);
          continue;
        }
        ShortMessage shortMessage = (ShortMessage) message;
        if (shortMessage.getCommand() != ShortMessage.CONTROL_CHANGE || shortMessage.getData1() != 7) {
          // Not a volume control event, add it to the new track
          newTrack.add(event);
        }
      }
    }

    return newSequence;
  }
}