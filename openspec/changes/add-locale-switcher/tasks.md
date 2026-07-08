## 1. Localized Copy Foundation

- [x] 1.1 Inventory user-facing hardcoded strings across Compose screens, Activities, dialogs, buttons, validation messages, and settings text.
- [x] 1.2 Move app-facing strings into centralized Android string resources.
- [x] 1.3 Add localized resource files for Simplified Chinese, Traditional Chinese, English, Japanese, and Korean.
- [x] 1.4 Keep user-entered data, backup contents, and bill record notes out of translation.
- [x] 1.5 Apply the special branded-copy mapping for `记你佬味`, `佬味账本`, `楞手佬味`, `佬味计数`, `设你佬味`, `佬凤日记`, `佬凤爱美丽`, and `佬味口音`.

## 2. Language Selection UI

- [x] 2.1 Add a Settings button labeled `佬味口音`.
- [x] 2.2 Create a `佬味口音` Activity with a back button and title.
- [x] 2.3 Add five selectable language options: Simplified Chinese, Traditional Chinese, English, Japanese, and Korean.
- [x] 2.4 Visually indicate the currently selected language.

## 3. Language Persistence And Runtime Application

- [x] 3.1 Persist the selected language using a stable language code.
- [x] 3.2 Apply the persisted language during app startup and when opening the language Activity.
- [x] 3.3 Refresh the visible UI after language selection using Activity recreation or equivalent runtime locale update.
- [x] 3.4 If full immediate refresh is not reliable, show a clear restart-required message after selection.
- [x] 3.5 On first launch without a saved app language, detect the system language and fall back to English when unsupported.

## 4. Verification

- [x] 4.1 Verify Settings opens the language Activity and back navigation returns to Settings.
- [x] 4.2 Verify each of the five language options changes centralized UI copy.
- [x] 4.3 Verify the selected language persists after app relaunch.
- [x] 4.4 Verify Simplified and Traditional Chinese keep branded labels while English, Japanese, and Korean use the requested semantic labels.
- [x] 4.5 Run `./gradlew :app:compileDebugKotlin` and fix any compile errors.
