# Voice Diary

A private browser diary that uses microphone speech recognition when supported, converts speech into editable text, and stores saved entries on the current device with `localStorage`.

## Run locally

```sh
python3 -m http.server 8000
```

Open `http://localhost:8000` in Chrome or another browser with Web Speech API support.

## Install as a mobile app

Voice Diary is a Progressive Web App (PWA). After opening it in a supported mobile browser, choose the browser's install option:

- Android Chrome: tap the menu, then tap **Install app** or **Add to Home screen**.
- iPhone Safari: tap Share, then tap **Add to Home Screen**.

The app shell is cached for offline use after the first visit. Speech transcription still needs browser speech recognition support, and some browsers may require a network connection for transcription.

## Deploy to Netlify

The repository includes `netlify.toml`, which builds the static PWA into `dist` and publishes that folder.

```sh
npx netlify-cli@latest deploy --prod --build
```

Set `NETLIFY_AUTH_TOKEN` and `NETLIFY_SITE_ID` first, or run `npx netlify-cli@latest login` and link a site before deploying.

## Notes

- Microphone transcription depends on the browser's Speech Recognition implementation.
- If speech recognition is unavailable or microphone permission is denied, the diary still works with typed entries.
- Saved entries stay in the browser on the current device until deleted or browser storage is cleared.
