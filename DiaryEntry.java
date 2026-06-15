package collections;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class DiaryEntry {
	private final String id;
	private final Instant createdAt;
	private final String transcript;
	private final String audioPath;

	public DiaryEntry(String id, Instant createdAt, String transcript, String audioPath) {
		this.id = Objects.requireNonNull(id, "id");
		this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
		this.transcript = Objects.requireNonNull(transcript, "transcript");
		this.audioPath = audioPath;
	}

	public String getId() {
		return id;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public String getTranscript() {
		return transcript;
	}

	public Optional<String> getAudioPath() {
		return Optional.ofNullable(audioPath).filter(path -> !path.isBlank());
	}
}
