## Why

The daily comparison chart currently shows a spending summary, but users cannot inspect which records make up a tapped day. Adding a detail dialog turns the chart interaction into a useful drill-down path without leaving the statistics page.

## What Changes

- Add a tooltip-driven dialog for the daily comparison chart.
- When the user taps a daily expense bar, keep the existing black tooltip selection behavior.
- When the user taps the selected bar's black tooltip, show that day's matching bill records.
- The dialog should summarize the selected date and total amount, list records with category, time, note, and signed amount, and provide a clear dismiss action.
- The dialog visual direction should follow the provided reference: dimmed background, rounded white panel, ranked list, and a confirm button.

## Capabilities

### New Capabilities
- `statistics-daily-record-dialog`: Daily comparison chart drill-down behavior and record detail dialog requirements.

### Modified Capabilities

## Impact

- Statistics UI state and chart interaction in `StatisticsScreen.kt`.
- Potential supporting state/callbacks in `LedgerViewModel.kt` or existing statistics state if needed.
- Reuses existing bill record/category formatting and bottom-sheet/dialog UI patterns; no new external dependencies expected.
