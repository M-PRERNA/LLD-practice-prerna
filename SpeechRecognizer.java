package collections;

import java.io.IOException;
import java.nio.file.Path;

public interface SpeechRecognizer {
	boolean isAvailable();

	String transcribe(Path audioFile) throws IOException, InterruptedException;
}
