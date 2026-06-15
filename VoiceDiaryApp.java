package collections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VoiceDiaryApp {
	private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter
		.ofPattern("yyyy-MM-dd HH:mm")
		.withZone(ZoneId.systemDefault());

	private final DiaryStorage storage;
	private final MicrophoneRecorder recorder;
	private final SpeechRecognizer speechRecognizer;
	private final BufferedReader input;

	public VoiceDiaryApp(DiaryStorage storage, MicrophoneRecorder recorder, SpeechRecognizer speechRecognizer, BufferedReader input) {
		this.storage = storage;
		this.recorder = recorder;
		this.speechRecognizer = speechRecognizer;
		this.input = input;
	}

	public static void main(String[] args) {
		try {
			new VoiceDiaryApp(
				new DiaryStorage(resolveDataDirectory(args)),
				new MicrophoneRecorder(),
				new WhisperSpeechRecognizer(),
				new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
			).run(args);
		} catch (Exception e) {
			System.err.println("Voice diary error: " + e.getMessage());
			System.exit(1);
		}
	}

	public void run(String[] args) throws IOException, InterruptedException {
		List<String> commandArgs = stripDataDirectoryArgs(args);
		if (commandArgs.isEmpty()) {
			runInteractiveMenu();
			return;
		}

		String command = commandArgs.get(0);
		if ("add-text".equals(command)) {
			String transcript = String.join(" ", commandArgs.subList(1, commandArgs.size()));
			printCreatedEntry(storage.addEntry(transcript, Optional.empty()));
		} else if ("record".equals(command)) {
			recordVoiceEntry();
		} else if ("list".equals(command)) {
			printEntries(storage.listEntries());
		} else if ("search".equals(command)) {
			printEntries(storage.search(String.join(" ", commandArgs.subList(1, commandArgs.size()))));
		} else if ("show".equals(command)) {
			showEntry(commandArgs);
		} else {
			printUsage();
		}
	}

	private void runInteractiveMenu() throws IOException, InterruptedException {
		while (true) {
			System.out.println();
			System.out.println("Voice Diary");
			System.out.println("1. Record voice note");
			System.out.println("2. Add typed note");
			System.out.println("3. List entries");
			System.out.println("4. Search entries");
			System.out.println("5. View entry");
			System.out.println("0. Exit");
			System.out.print("Choose an option: ");

			String choice = input.readLine();
			if ("1".equals(choice)) {
				recordVoiceEntry();
			} else if ("2".equals(choice)) {
				addTypedEntry();
			} else if ("3".equals(choice)) {
				printEntries(storage.listEntries());
			} else if ("4".equals(choice)) {
				System.out.print("Search text: ");
				printEntries(storage.search(input.readLine()));
			} else if ("5".equals(choice)) {
				System.out.print("Entry id: ");
				printEntry(storage.findById(input.readLine()));
			} else if ("0".equals(choice)) {
				return;
			} else {
				System.out.println("Unknown option.");
			}
		}
	}

	private void recordVoiceEntry() throws IOException, InterruptedException {
		Path audioFile = storage.getAudioDirectory().resolve(System.currentTimeMillis() + ".wav");
		Path recordedAudio = recorder.recordUntilEnter(audioFile, input);
		String transcript;
		if (speechRecognizer.isAvailable()) {
			System.out.println("Transcribing with local Whisper...");
			transcript = speechRecognizer.transcribe(recordedAudio);
		} else {
			System.out.println("Whisper CLI was not found. Type the transcript for this recording.");
			transcript = readMultilineTranscript();
		}
		printCreatedEntry(storage.addEntry(transcript, Optional.of(recordedAudio)));
	}

	private void addTypedEntry() throws IOException {
		System.out.println("Write your diary note. Finish with a single '.' on its own line.");
		printCreatedEntry(storage.addEntry(readMultilineTranscript(), Optional.empty()));
	}

	private String readMultilineTranscript() throws IOException {
		StringBuilder transcript = new StringBuilder();
		while (true) {
			String line = input.readLine();
			if (line == null || ".".equals(line)) {
				break;
			}
			if (transcript.length() > 0) {
				transcript.append(System.lineSeparator());
			}
			transcript.append(line);
		}
		return transcript.toString();
	}

	private void showEntry(List<String> commandArgs) throws IOException {
		if (commandArgs.size() < 2) {
			System.out.println("Missing entry id.");
			return;
		}
		printEntry(storage.findById(commandArgs.get(1)));
	}

	private void printCreatedEntry(DiaryEntry entry) {
		System.out.println("Saved diary entry " + entry.getId());
	}

	private void printEntries(List<DiaryEntry> entries) {
		if (entries.isEmpty()) {
			System.out.println("No diary entries found.");
			return;
		}

		for (DiaryEntry entry : entries) {
			System.out.println(entry.getId() + " | " + DISPLAY_TIME.format(entry.getCreatedAt()) + " | " + firstLine(entry.getTranscript()));
		}
	}

	private void printEntry(Optional<DiaryEntry> entry) {
		if (entry.isEmpty()) {
			System.out.println("Diary entry not found.");
			return;
		}

		DiaryEntry diaryEntry = entry.get();
		System.out.println("Id: " + diaryEntry.getId());
		System.out.println("Created: " + DISPLAY_TIME.format(diaryEntry.getCreatedAt()));
		diaryEntry.getAudioPath().ifPresent(path -> System.out.println("Audio: " + path));
		System.out.println();
		System.out.println(diaryEntry.getTranscript());
	}

	private String firstLine(String text) {
		return Arrays.stream(text.split("\\R", 2)).findFirst().orElse("");
	}

	private void printUsage() {
		System.out.println("Usage:");
		System.out.println("  java collections.VoiceDiaryApp [--data-dir PATH]");
		System.out.println("  java collections.VoiceDiaryApp [--data-dir PATH] record");
		System.out.println("  java collections.VoiceDiaryApp [--data-dir PATH] add-text \"Today I felt...\"");
		System.out.println("  java collections.VoiceDiaryApp [--data-dir PATH] list");
		System.out.println("  java collections.VoiceDiaryApp [--data-dir PATH] search \"keyword\"");
		System.out.println("  java collections.VoiceDiaryApp [--data-dir PATH] show ENTRY_ID");
	}

	private static Path resolveDataDirectory(String[] args) {
		for (int index = 0; index < args.length - 1; index++) {
			if ("--data-dir".equals(args[index])) {
				return Path.of(args[index + 1]);
			}
		}
		return Path.of(System.getProperty("user.home"), ".voice-diary");
	}

	private static List<String> stripDataDirectoryArgs(String[] args) {
		List<String> values = new java.util.ArrayList<>(Arrays.asList(args));
		for (int index = 0; index < values.size() - 1; index++) {
			if ("--data-dir".equals(values.get(index))) {
				values.remove(index + 1);
				values.remove(index);
				break;
			}
		}
		return values;
	}
}
