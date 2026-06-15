package collections;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class DiaryStorage {
	private final Path dataDirectory;
	private final Path entriesFile;

	public DiaryStorage(Path dataDirectory) {
		this.dataDirectory = dataDirectory;
		this.entriesFile = dataDirectory.resolve("entries.tsv");
	}

	public Path getDataDirectory() {
		return dataDirectory;
	}

	public Path getAudioDirectory() {
		return dataDirectory.resolve("audio");
	}

	public DiaryEntry addEntry(String transcript, Optional<Path> audioPath) throws IOException {
		if (transcript == null || transcript.isBlank()) {
			throw new IllegalArgumentException("Diary entry text cannot be blank.");
		}

		Files.createDirectories(dataDirectory);
		DiaryEntry entry = new DiaryEntry(
			UUID.randomUUID().toString(),
			Instant.now(),
			transcript.trim(),
			audioPath.map(Path::toString).orElse("")
		);
		Files.writeString(
			entriesFile,
			serialize(entry) + System.lineSeparator(),
			StandardCharsets.UTF_8,
			StandardOpenOption.CREATE,
			StandardOpenOption.APPEND
		);
		return entry;
	}

	public List<DiaryEntry> listEntries() throws IOException {
		if (!Files.exists(entriesFile)) {
			return new ArrayList<>();
		}

		return Files.readAllLines(entriesFile, StandardCharsets.UTF_8)
			.stream()
			.filter(line -> !line.isBlank())
			.map(this::deserialize)
			.sorted(Comparator.comparing(DiaryEntry::getCreatedAt).reversed())
			.collect(Collectors.toList());
	}

	public Optional<DiaryEntry> findById(String id) throws IOException {
		return listEntries()
			.stream()
			.filter(entry -> entry.getId().equals(id))
			.findFirst();
	}

	public List<DiaryEntry> search(String query) throws IOException {
		String normalizedQuery = query.toLowerCase();
		return listEntries()
			.stream()
			.filter(entry -> entry.getTranscript().toLowerCase().contains(normalizedQuery))
			.collect(Collectors.toList());
	}

	private String serialize(DiaryEntry entry) {
		return String.join(
			"\t",
			entry.getId(),
			entry.getCreatedAt().toString(),
			encode(entry.getTranscript()),
			encode(entry.getAudioPath().orElse(""))
		);
	}

	private DiaryEntry deserialize(String line) {
		String[] parts = line.split("\t", -1);
		if (parts.length != 4) {
			throw new IllegalStateException("Invalid diary storage row: " + line);
		}
		return new DiaryEntry(
			parts[0],
			Instant.parse(parts[1]),
			decode(parts[2]),
			decode(parts[3])
		);
	}

	private String encode(String value) {
		return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}

	private String decode(String value) {
		return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
	}
}
