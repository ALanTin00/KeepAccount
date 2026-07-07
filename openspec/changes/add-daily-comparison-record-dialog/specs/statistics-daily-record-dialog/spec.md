## ADDED Requirements

### Requirement: Daily comparison opens record dialog
The statistics page SHALL allow users to open a record dialog from the daily comparison chart for a day with matching records.

#### Scenario: User taps a daily expense bar
- **WHEN** the user taps a daily comparison bar for a date with expense records
- **THEN** the system SHALL select that bar and show the existing black summary tooltip

#### Scenario: User taps a selected daily tooltip
- **WHEN** the user taps the black summary tooltip for a selected date with expense records
- **THEN** the system SHALL show a modal dialog for that date's expense records

#### Scenario: User taps an empty daily bar
- **WHEN** the user taps a daily comparison bar for a date with no matching records
- **THEN** the system SHALL NOT show a populated record dialog

### Requirement: Dialog summarizes selected day
The daily record dialog SHALL show the selected date, current statistics mode, and total amount for the records displayed.

#### Scenario: Dialog title for expense records
- **WHEN** the dialog opens for an expense day
- **THEN** the title SHALL include the selected date and the day's total expense amount

### Requirement: Dialog lists matching records
The daily record dialog SHALL list only records whose date matches the selected daily comparison point and whose type matches the active statistics mode.

#### Scenario: Records are scoped to selected date and mode
- **WHEN** the dialog opens for a selected day
- **THEN** every listed record SHALL have the selected date and active statistics mode

#### Scenario: Record row details
- **WHEN** a record appears in the dialog
- **THEN** the row SHALL show rank, category, time, optional note, and signed amount

### Requirement: Dialog can be dismissed
The daily record dialog SHALL provide a clear dismiss action and close without changing the selected statistics month or mode.

#### Scenario: User dismisses dialog
- **WHEN** the user activates the dialog dismiss action
- **THEN** the dialog SHALL close and the statistics page SHALL remain on the same month and mode
