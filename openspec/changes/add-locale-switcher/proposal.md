## Why

The app currently hardcodes most display text in Compose code, making future language support and wording changes costly. Adding a dedicated language entry and centralized localized text management prepares the app for iterative releases across Simplified Chinese, Traditional Chinese, English, Japanese, and Korean.

## What Changes

- Add a settings-page button labeled `佬味口音`.
- Add a new `佬味口音` Activity with a back button, title, and five selectable language options.
- Centralize app-facing copy so UI text can be provided for Simplified Chinese, Traditional Chinese, English, Japanese, and Korean.
- Persist the selected language and apply it consistently across the app.
- Determine whether the app needs a restart after language changes; prefer immediate UI refresh when practical, otherwise show a clear restart prompt.

## Capabilities

### New Capabilities
- `localized-ui-language-switcher`: Covers centralized UI copy, supported languages, settings entry point, language selection screen, persistence, and language-change refresh behavior.

### Modified Capabilities
- None.

## Impact

- Affects Compose UI screens with hardcoded user-facing text.
- Adds a new Activity and navigation entry from Settings.
- Adds localized string resources or an equivalent centralized text provider for five languages.
- Adds persisted language preference and runtime locale application behavior.
