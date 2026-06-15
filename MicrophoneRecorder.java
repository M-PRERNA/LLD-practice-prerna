package collections;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneRecorder {
	private static final AudioFormat AUDIO_FORMAT = new AudioFormat(16000.0F, 16, 1, true, false);

	public Path recordUntilEnter(Path audioFile, BufferedReader input) throws IOException {
		Files.createDirectories(audioFile.getParent());

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
		if (!AudioSystem.isLineSupported(info)) {
			throw new IOException("No microphone input line supports 16 kHz mono WAV recording.");
		}

		try {
			TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(AUDIO_FORMAT);
			microphone.start();

			Thread writer = new Thread(() -> writeAudio(audioFile, microphone), "voice-diary-recorder");
			writer.start();

			System.out.println("Recording. Press Enter to stop.");
			input.readLine();
			microphone.stop();
			microphone.close();
			writer.join();
			return audioFile;
		} catch (LineUnavailableException e) {
			throw new IOException("Microphone is unavailable: " + e.getMessage(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Recording interrupted.", e);
		}
	}

	private void writeAudio(Path audioFile, TargetDataLine microphone) {
		try (AudioInputStream stream = new AudioInputStream(microphone)) {
			AudioSystem.write(stream, AudioFileFormat.Type.WAVE, audioFile.toFile());
		} catch (IOException e) {
			throw new IllegalStateException("Unable to write recording to " + audioFile, e);
		}
	}
}
