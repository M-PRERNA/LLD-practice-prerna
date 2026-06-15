package collections;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WhisperSpeechRecognizerTest {
	public static void main(String[] args) throws Exception {
		Path audioFile = Files.createTempFile("thought", ".wav");
		Files.writeString(audioFile, "fake wav bytes", StandardCharsets.UTF_8);

		WhisperSpeechRecognizer recognizer = new WhisperSpeechRecognizer();
		if (!recognizer.isAvailable()) {
			throw new AssertionError("Expected fake whisper executable to be available on PATH.");
		}

		String transcript = recognizer.transcribe(audioFile);
		if (!"mock transcript from microphone".equals(transcript)) {
			throw new AssertionError("Unexpected transcript: " + transcript);
		}

		System.out.println("WhisperSpeechRecognizerTest passed");
	}
}
