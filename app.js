(function () {
  "use strict";

  const STORAGE_KEY = "voice-diary.entries.v1";
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;

  const elements = {
    title: document.getElementById("entry-title"),
    text: document.getElementById("entry-text"),
    liveTranscript: document.getElementById("live-transcript"),
    statusPill: document.getElementById("status-pill"),
    start: document.getElementById("start-listening"),
    stop: document.getElementById("stop-listening"),
    save: document.getElementById("save-entry"),
    clear: document.getElementById("clear-draft"),
    supportMessage: document.getElementById("support-message"),
    entriesList: document.getElementById("entries-list"),
    emptyState: document.getElementById("empty-state"),
    search: document.getElementById("entry-search"),
    export: document.getElementById("export-entries")
  };

  let recognition = null;
  let isRecording = false;
  let finalTranscript = "";

  function loadEntries() {
    try {
      const parsedEntries = JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");
      return Array.isArray(parsedEntries) ? parsedEntries : [];
    } catch (error) {
      console.warn("Diary entries could not be loaded.", error);
      return [];
    }
  }

  function saveEntries(entries) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(entries));
  }

  function formatDate(value) {
    return new Intl.DateTimeFormat(undefined, {
      dateStyle: "medium",
      timeStyle: "short"
    }).format(new Date(value));
  }

  function setStatus(message, recording) {
    elements.statusPill.textContent = message;
    elements.statusPill.classList.toggle("recording", Boolean(recording));
    elements.start.classList.toggle("recording", Boolean(recording));
  }

  function setSupportMessage(message, isError) {
    elements.supportMessage.textContent = message;
    elements.supportMessage.classList.toggle("error", Boolean(isError));
  }

  function normalizeText(text) {
    return text.replace(/\s+/g, " ").trim();
  }

  function appendTranscript(text) {
    const currentText = elements.text.value.trim();
    const normalizedTranscript = normalizeText(text);

    if (!normalizedTranscript) {
      return;
    }

    elements.text.value = currentText ? `${currentText} ${normalizedTranscript}` : normalizedTranscript;
  }

  function createEntry(title, text) {
    const now = new Date().toISOString();
    return {
      id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
      title: title || "Untitled thought",
      text,
      createdAt: now
    };
  }

  function matchesSearch(entry, query) {
    const haystack = `${entry.title} ${entry.text} ${formatDate(entry.createdAt)}`.toLowerCase();
    return haystack.includes(query.toLowerCase());
  }

  function renderEntries() {
    const query = elements.search.value.trim();
    const entries = loadEntries();
    const filteredEntries = query ? entries.filter((entry) => matchesSearch(entry, query)) : entries;

    elements.entriesList.innerHTML = "";
    elements.emptyState.classList.toggle("hidden", filteredEntries.length > 0);
    elements.emptyState.textContent = query
      ? "No saved entries match that search."
      : "No diary entries yet. Capture your first thought above.";

    filteredEntries.forEach((entry) => {
      const item = document.createElement("li");
      item.className = "entry-card";

      const header = document.createElement("header");
      const title = document.createElement("h3");
      const date = document.createElement("span");
      const body = document.createElement("p");
      const actions = document.createElement("div");
      const deleteButton = document.createElement("button");

      title.textContent = entry.title;
      date.className = "entry-date";
      date.textContent = formatDate(entry.createdAt);
      body.className = "entry-text";
      body.textContent = entry.text;
      actions.className = "entry-actions";
      deleteButton.type = "button";
      deleteButton.className = "ghost";
      deleteButton.textContent = "Delete";
      deleteButton.addEventListener("click", () => deleteEntry(entry.id));

      header.append(title, date);
      actions.append(deleteButton);
      item.append(header, body, actions);
      elements.entriesList.append(item);
    });
  }

  function deleteEntry(id) {
    const entries = loadEntries().filter((entry) => entry.id !== id);
    saveEntries(entries);
    renderEntries();
  }

  function saveCurrentEntry() {
    const text = elements.text.value.trim();

    if (!text) {
      setSupportMessage("Write or dictate something before saving.", true);
      elements.text.focus();
      return;
    }

    const entries = loadEntries();
    entries.unshift(createEntry(elements.title.value.trim(), text));
    saveEntries(entries);
    elements.title.value = "";
    elements.text.value = "";
    elements.liveTranscript.textContent = "Saved. Capture another thought when you are ready.";
    setSupportMessage("Entry saved on this device.", false);
    renderEntries();
  }

  function clearDraft() {
    finalTranscript = "";
    elements.title.value = "";
    elements.text.value = "";
    elements.liveTranscript.textContent = "Your spoken words will appear here while recording.";
    setSupportMessage("", false);
  }

  function stopListening() {
    if (recognition && isRecording) {
      recognition.stop();
    }
  }

  function startListening() {
    if (!recognition) {
      setSupportMessage("Speech recognition is unavailable in this browser. You can still type diary entries.", true);
      return;
    }

    finalTranscript = "";
    elements.liveTranscript.textContent = "Listening...";
    setSupportMessage("Allow microphone access when your browser asks.", false);

    try {
      recognition.start();
    } catch (error) {
      setSupportMessage("Listening is already starting. Please wait a moment.", true);
    }
  }

  function configureSpeechRecognition() {
    if (!SpeechRecognition) {
      elements.start.disabled = true;
      setStatus("Typing mode", false);
      setSupportMessage("Speech recognition is not supported in this browser. Typed diary entries still work.", true);
      return;
    }

    recognition = new SpeechRecognition();
    recognition.continuous = true;
    recognition.interimResults = true;
    recognition.lang = navigator.language || "en-US";

    recognition.onstart = () => {
      isRecording = true;
      elements.start.disabled = true;
      elements.stop.disabled = false;
      setStatus("Listening", true);
    };

    recognition.onresult = (event) => {
      let interimTranscript = "";

      for (let index = event.resultIndex; index < event.results.length; index += 1) {
        const transcript = event.results[index][0].transcript;

        if (event.results[index].isFinal) {
          finalTranscript += `${transcript} `;
        } else {
          interimTranscript += transcript;
        }
      }

      elements.liveTranscript.textContent = normalizeText(`${finalTranscript} ${interimTranscript}`) || "Listening...";
    };

    recognition.onerror = (event) => {
      const messages = {
        "not-allowed": "Microphone permission was blocked. Allow access or type the entry manually.",
        "no-speech": "No speech was detected. Try again when you are ready.",
        "audio-capture": "No microphone was found. Connect a microphone or type the entry manually."
      };

      setSupportMessage(messages[event.error] || `Speech recognition stopped: ${event.error}.`, true);
    };

    recognition.onend = () => {
      isRecording = false;
      elements.start.disabled = false;
      elements.stop.disabled = true;
      setStatus("Ready", false);
      appendTranscript(finalTranscript);
      finalTranscript = "";
    };
  }

  function exportEntries() {
    const entries = loadEntries();

    if (entries.length === 0) {
      setSupportMessage("There are no saved entries to export yet.", true);
      return;
    }

    const blob = new Blob([JSON.stringify(entries, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = "voice-diary-entries.json";
    link.click();
    URL.revokeObjectURL(url);
    setSupportMessage("Diary export downloaded as JSON.", false);
  }

  elements.start.addEventListener("click", startListening);
  elements.stop.addEventListener("click", stopListening);
  elements.save.addEventListener("click", saveCurrentEntry);
  elements.clear.addEventListener("click", clearDraft);
  elements.search.addEventListener("input", renderEntries);
  elements.export.addEventListener("click", exportEntries);

  configureSpeechRecognition();
  renderEntries();
}());
