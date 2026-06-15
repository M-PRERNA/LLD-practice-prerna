package collections;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class DiaryStorageTest {
	public static void main(String[] args) throws Exception {
		Path dataDirectory = Files.createTempDirectory("voice-diary-storage-test-");
		DiaryStorage storage = new DiaryStorage(dataDirectory);

		DiaryEntry first = storage.addEntry("Morning idea about the garden", Optional.empty());
		Path audioPath = dataDirectory.resolve("audio").resolve("thought.wav");
		DiaryEntry second = storage.addEntry("Evening reflection\nFelt calm after walking", Optional.of(audioPath));

		assertEquals(2, storage.listEntries().size(), "stores two entries");
		assertEquals(second.getId(), storage.listEntries().get(0).getId(), "lists newest entries first");
		assertEquals(first.getTranscript(), storage.findById(first.getId()).orElseThrow().getTranscript(), "finds an entry by id");
		assertEquals(1, storage.search("garden").size(), "searches transcripts case-insensitively");
		assertEquals(audioPath.toString(), storage.findById(second.getId()).orElseThrow().getAudioPath().orElseThrow(), "stores audio paths");

		DiaryStorage reloadedStorage = new DiaryStorage(dataDirectory);
		List<DiaryEntry> reloadedEntries = reloadedStorage.listEntries();
		assertEquals(2, reloadedEntries.size(), "reloads entries from disk");

		System.out.println("DiaryStorageTest passed");
	}

	private static void assertEquals(Object expected, Object actual, String message) {
		if (!expected.equals(actual)) {
			throw new AssertionError(message + ". Expected <" + expected + "> but was <" + actual + ">.");
		}
	}
}
