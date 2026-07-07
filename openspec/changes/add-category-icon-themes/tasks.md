## 1. Asset Import

- [x] 1.1 Copy all three provided 19-icon PNG sets into `app/src/main/res/drawable` using English lowercase resource names.
- [x] 1.2 Verify each theme has one mapped drawable for all 19 built-in categories.

## 2. Icon Theme Model

- [x] 2.1 Add a category icon theme model and Compose-local selected theme.
- [x] 2.2 Update shared category icon resolution to return resources by category ID and selected theme.

## 3. Settings Switching

- [x] 3.1 Persist the selected category icon theme in local preferences and expose it through `LedgerUiState`.
- [x] 3.2 Add a settings entry button labeled `佬凤爱美丽`.
- [x] 3.3 Add three `佬凤爱美丽` page icon theme controls with one preview icon from each theme and `切换` text.
- [x] 3.4 Wire icon theme controls so tapping them immediately updates category icons without requiring app restart.
- [x] 3.5 Move the three icon theme switching controls to a dedicated `佬凤爱美丽` Activity with a title and back button.

## 4. Verification

- [x] 4.1 Run `./gradlew.bat :app:compileDebugKotlin`.
- [x] 4.2 Run `openspec.cmd validate add-category-icon-themes --strict`.
- [x] 4.3 Verify copied resource filenames are English Android-safe names and Chinese UI text remains UTF-8.
