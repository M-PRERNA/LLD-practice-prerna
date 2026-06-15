package collections;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WhisperSpeechRecognizer implements SpeechRecognizer {
	private final String executable;
	private final String model;

	public WhisperSpeechRecognizer() {
		this.executable = System.getenv().getOrDefault("VOICE_DIARY_WHISPER_BIN", "whisper");
		this.model = System.getenv().getOrDefault("VOICE_DIARY_WHISPER_MODEL", "base");
	}

	@Override
	public boolean isAvailable() {
		String pathValue = System.getenv("PATH");
		if (pathValue == null || pathValue.isBlank()) {
			return false;
		}

		for (String directory : pathValue.split(System.getProperty("path.separator"))) {
			Path candidate = Path.of(directory, executable);
			if (Files.isRegularFile(candidate) && Files.isExecutable(candidate)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String transcribe(Path audioFile) throws IOException, InterruptedException {
		Path outputDirectory = Files.createTempDirectory("voice-diary-whisper-");
		List<String> command = new ArrayList<>();
		command.add(executable);
		command.add(audioFile.toString());
		command.add("--model");
		command.add(model);
		command.add("--output_format");
		command.add("txt");
		command.add("--output_dir");
		command.add(outputDirectory.toString());

		Process process = new ProcessBuilder(command)
			.redirectErrorStream(true)
			.start();
		String processOutput = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			throw new IOException("Whisper failed with exit code " + exitCode + ": " + processOutput.trim());
		}

		String transcriptFileName = stripExtension(audioFile.getFileName().toString()) + ".txt";
		Path transcriptFile = outputDirectory.resolve(transcriptFileName);
		if (!Files.exists(transcriptFile)) {
			throw new IOException("Whisper completed but did not create " + transcriptFile);
		}
		return Files.readString(transcriptFile, StandardCharsets.UTF_8).trim();
	}

	private String stripExtension(String fileName) {
		int lastDot = fileName.lastIndexOf('.');
		if (lastDot <= 0) {
			return fileName;
		}
		return fileName.substring(0, lastDot);
	}
}
