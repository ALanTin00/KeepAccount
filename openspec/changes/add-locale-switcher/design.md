## Context

KeepAccount is a Kotlin + Jetpack Compose Android app with many user-facing strings currently embedded directly in composable functions and view-model messages. The requested language switcher affects most screens, settings navigation, persistence, and runtime UI refresh behavior.

## Goals / Non-Goals

**Goals:**
- Centralize user-facing copy so app text is maintainable for Simplified Chinese, Traditional Chinese, English, Japanese, and Korean.
- Add a settings entry labeled `佬味口音`.
- Add a dedicated language selection Activity with back navigation, title, and five selectable language options.
- Persist the selected language and apply it consistently after selection.
- Prefer immediate refresh after switching language; if any surface cannot update safely without restart, inform the user clearly.

**Non-Goals:**
- Translating user-entered data such as notes, category records, backup file contents, or seeded bill notes.
- Changing currency, date calculation, number formatting, or calendar system behavior.
- Adding remote translation, server-managed copy, or automatic device-language detection beyond the chosen app language.

## Decisions

1. **Use Android string resources as the source of truth.**
   - Move UI strings into `res/values/strings.xml` and locale-specific resource folders.
   - Use Android resource qualifiers for Simplified Chinese, Traditional Chinese, English, Japanese, and Korean.
   - Treat branded "佬味" copy as explicit product wording: Simplified and Traditional Chinese keep the provided branded labels; English, Japanese, and Korean use the normal semantic labels requested by the user.
   - Rationale: Android resources are the platform-native way to manage localized copy, work with Activities and Compose, and keep translations reviewable.
   - Alternative considered: a Kotlin-only text provider. This is easier to retrofit selectively but less idiomatic and harder to use for Activity labels and future Android surfaces.

2. **Persist language selection in app preferences.**
   - Store a stable language code such as `zh-Hans`, `zh-Hant`, `en`, `ja`, or `ko`.
   - Apply the stored value at app startup and when opening the language screen.
   - Rationale: keeps the selected language independent from system language and stable across app launches.

3. **Apply language changes with Activity recreation when needed.**
   - After a user selects a language, update the locale configuration and recreate the current Activity/task surface if needed.
   - The app should not require a full manual restart when Android APIs allow the UI to refresh by recreation.
   - If a full restart is still required on an unsupported path, display a clear message.

4. **Add a dedicated `佬味口音` Activity.**
   - Settings opens this Activity from a button.
   - The Activity contains a back button, title, and five selectable rows or buttons.
   - Rationale: keeps Settings simple and gives language selection enough room for future copy such as restart hints.

## Risks / Trade-offs

- Hardcoded strings can be missed during migration -> Use search for Chinese, English labels, and hardcoded `Text(...)` values; verify common screens manually.
- Immediate locale refresh can vary by Android version -> Prefer AppCompat/application locale or explicit configuration update plus Activity recreation; document fallback behavior.
- Seed data/category names may be domain data rather than UI copy -> Keep user data stable unless the category display names are app-defined UI strings that can be safely localized.
