# Voice Diary

A private browser diary that uses microphone speech recognition when supported, converts speech into editable text, and stores saved entries on the current device with `localStorage`.

## Run locally

```sh
python3 -m http.server 8000
```

Open `http://localhost:8000` in Chrome or another browser with Web Speech API support.

## Notes

- Microphone transcription depends on the browser's Speech Recognition implementation.
- If speech recognition is unavailable or microphone permission is denied, the diary still works with typed entries.
- Saved entries stay in the browser on the current device until deleted or browser storage is cleared.
