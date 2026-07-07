## Context

The project defines 19 built-in categories: 14 expense categories and 5 income categories. Category icons are currently resolved by `categoryIconResId(category)` in the UI layer, and several income categories reuse existing role icons. The user provided three folders, each containing 19 PNG files with Chinese filenames.

Android resource names must be lowercase ASCII with underscores, so the assets must be copied into the project using English names. Category icons are used by multiple Compose screens, so theme selection needs a shared state path instead of one-off screen parameters.

## Goals / Non-Goals

**Goals:**
- Import all three provided 19-icon sets with English Android resource names.
- Map every built-in category to one icon in each set.
- Add one settings-page entry button labeled `佬凤爱美丽`.
- Add a dedicated `佬凤爱美丽` Activity with a title, back button, and three buttons that preview one icon from each set and use `切换` as the action text.
- Switch category icons immediately after tapping a `佬凤爱美丽` page button and persist the selection for future app launches.
- Keep Chinese text encoded as UTF-8.

**Non-Goals:**
- No database schema changes.
- No custom per-category picker.
- No remote asset loading or new dependencies.
- No requirement to rename existing category names.

## Decisions

- Store the selected icon theme in `SharedPreferences` and mirror it in `LedgerUiState`.
  - Rationale: the setting is local, small, and should survive app restart without adding database migration work.
  - Alternative considered: database-backed settings. This adds schema and migration cost for a purely local UI preference.

- Use a Compose `CompositionLocal` for the selected icon theme.
  - Rationale: `CategoryIcon` is shared across many screens; a composition-local value lets existing call sites refresh without threading a theme parameter through every screen.
  - Alternative considered: passing the theme through every screen and row. This is more invasive and easy to miss.

- Keep the current app behavior restart-free.
  - Rationale: switching only changes Compose state and resource IDs, so recomposition can update visible icons immediately. Persisted preferences cover future Activity/ViewModel instances, and a preference listener keeps the main app state in sync when another Activity changes the selected theme.

- Rename copied PNG assets to English category-based resource names.
  - Rationale: Android resources require ASCII-safe names, and category-based names make the mapping clear in code.

## Risks / Trade-offs

- The provided image filenames do not directly match the accounting categories -> Use a deterministic one-to-one mapping documented in code by category ID and resource name.
- Different Activities have separate ViewModel instances -> Persist the selected theme and listen for preference changes so newly opened screens and the existing main screen use the chosen theme.
- Adding 57 PNG assets increases APK size -> The files are small PNGs and require no runtime download or decoding logic beyond normal resources.
