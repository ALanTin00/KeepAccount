## ADDED Requirements

### Requirement: Imported category icon themes
The system SHALL include three complete category icon themes, and each theme SHALL provide one icon for every built-in category.

#### Scenario: Assets are available for all categories
- **WHEN** the app resolves a category icon for any of the 19 built-in categories
- **THEN** each available icon theme SHALL provide a non-null drawable resource for that category

#### Scenario: Asset names are Android-safe
- **WHEN** provided PNG files are added to the Android project
- **THEN** the files SHALL use lowercase English resource names with underscores instead of Chinese filenames

### Requirement: Settings page opens icon theme page
The settings page SHALL show one entry button for category icon appearance, and tapping it SHALL open a dedicated Activity page.

#### Scenario: User views icon theme entry
- **WHEN** the user opens the settings page
- **THEN** the system SHALL show a button labeled `佬凤爱美丽`

#### Scenario: User opens icon theme page
- **WHEN** the user taps the `佬凤爱美丽` button
- **THEN** the system SHALL open a new Activity page titled `佬凤爱美丽`
- **AND** the page SHALL provide a back control

### Requirement: Icon theme page switches category icon theme
The dedicated `佬凤爱美丽` page SHALL show three controls for switching between the provided category icon themes.

#### Scenario: User views icon theme controls
- **WHEN** the user opens the `佬凤爱美丽` page
- **THEN** the system SHALL show three icon theme controls, each with an icon preview from a different provided theme and action text `切换`

#### Scenario: User switches icon theme
- **WHEN** the user taps one of the icon theme `切换` controls
- **THEN** the system SHALL set the selected category icon theme to the corresponding provided theme

### Requirement: Category icon theme persists and refreshes immediately
The selected category icon theme SHALL persist locally and SHALL update visible category icons without requiring an app restart.

#### Scenario: Immediate refresh
- **WHEN** the user switches category icon theme from the `佬凤爱美丽` page
- **THEN** visible category icons SHALL use the newly selected theme after Compose recomposition without restarting the app

#### Scenario: Persisted selection
- **WHEN** the app is opened after a previous category icon theme selection
- **THEN** the app SHALL load the previously selected icon theme from local preferences
