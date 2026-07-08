## ADDED Requirements

### Requirement: Centralized localized UI copy
The app SHALL manage user-facing application copy through centralized localized resources for Simplified Chinese, Traditional Chinese, English, Japanese, and Korean.

#### Scenario: UI copy is available in supported languages
- **WHEN** the app renders built-in navigation labels, page titles, buttons, dialogs, validation messages, and settings text
- **THEN** the displayed copy SHALL come from centralized localized resources for the active app language

#### Scenario: User content is not translated
- **WHEN** the app displays user-entered notes, imported records, backup data, or seeded bill record notes
- **THEN** the app SHALL preserve and display that content as stored

### Requirement: Special branded copy translation rules
The app SHALL use special branded copy translations for selected labels while translating all other UI copy normally.

#### Scenario: Simplified and Traditional Chinese branded labels
- **WHEN** the active language is Simplified Chinese or Traditional Chinese
- **THEN** the app SHALL display these labels as `记你佬味`, `佬味账本`, `楞手佬味`, `佬味计数`, `设你佬味`, `佬凤日记`, `佬凤爱美丽`, and `佬味口音`

#### Scenario: Non-Chinese semantic labels
- **WHEN** the active language is English, Japanese, or Korean
- **THEN** the app SHALL translate `记你佬味` semantically as `记一笔`, `佬味账本` as `记账本`, `楞手佬味` as `明细`, `佬味计数` as `统计`, `设你佬味` as `设置`, `佬凤日记` as `备注`, `佬凤爱美丽` as `换肤`, and `佬味口音` as `切换语音`

### Requirement: Settings language entry
The settings page SHALL include a button labeled `佬味口音` that opens the language selection screen.

#### Scenario: User opens language settings
- **WHEN** the user taps the `佬味口音` button in Settings
- **THEN** the app SHALL open a dedicated language selection Activity

### Requirement: Language selection Activity
The language selection Activity SHALL include a back button, a title labeled `佬味口音`, and five selectable language options: Simplified Chinese, Traditional Chinese, English, Japanese, and Korean.

#### Scenario: Language options are visible
- **WHEN** the language selection Activity is displayed
- **THEN** the user SHALL see exactly five selectable language options for Simplified Chinese, Traditional Chinese, English, Japanese, and Korean

#### Scenario: User returns from language screen
- **WHEN** the user taps the back button
- **THEN** the app SHALL close the language selection Activity and return to Settings

### Requirement: Persisted app language
The app SHALL persist the selected language and apply it across app launches until the user selects a different language.

#### Scenario: First launch uses matching system language
- **WHEN** the app launches for the first time and no app language has been saved
- **THEN** the app SHALL detect the system language and select Simplified Chinese, Traditional Chinese, English, Japanese, or Korean when matched

#### Scenario: First launch falls back to English
- **WHEN** the app launches for the first time and the system language does not match Simplified Chinese, Traditional Chinese, English, Japanese, or Korean
- **THEN** the app SHALL select English as the active app language

#### Scenario: User selects a language
- **WHEN** the user selects one of the five language options
- **THEN** the app SHALL save that language as the active app language

#### Scenario: App relaunches after language selection
- **WHEN** the user has previously selected a language and launches the app again
- **THEN** the app SHALL display centralized UI copy in the previously selected language

### Requirement: Language change refresh behavior
The app SHALL refresh visible UI after a language change without requiring a manual app restart when supported by the Android runtime.

#### Scenario: Runtime refresh is supported
- **WHEN** the user selects a different language and the runtime supports applying it immediately
- **THEN** the app SHALL refresh the current UI so centralized copy appears in the selected language

#### Scenario: Manual restart is required
- **WHEN** the user selects a different language and the app cannot safely refresh all visible UI immediately
- **THEN** the app SHALL show a clear message that the app needs to be restarted for the language change to fully apply
