## Context

The statistics page already computes `statisticsRecords` for the selected month and renders a daily comparison chart from those records. The chart has tap handling for selecting a daily bar and showing a summary tooltip, but it does not expose the underlying records for the selected day.

The desired interaction is local to the statistics UI: after tapping a daily expense entry, users should see the existing black summary tooltip; tapping that tooltip opens a modal dialog showing the selected day's expense records without navigating away from the page.

## Goals / Non-Goals

**Goals:**
- Reuse the existing statistics month records as the data source for the dialog.
- Show a modal overlay inspired by the provided reference: dimmed background, rounded white panel, date/total title, ranked records, and a dismiss button.
- Keep chart selection behavior intact while adding a tooltip-activated second-level detail view.
- Restrict the dialog records to the selected day and current statistics mode, with the requested expense use case fully supported.

**Non-Goals:**
- No database schema changes.
- No new navigation destination.
- No change to category detail ranking or monthly comparison behavior.
- No new third-party UI dependency.

## Decisions

- Store dialog selection as local Compose state in `StatisticsPage` rather than adding ViewModel state.
  - Rationale: the selection is transient UI state and can be derived from the existing `statisticsRecords` list.
  - Alternative considered: ViewModel-backed state. This adds lifecycle persistence, but is unnecessary for a dismissible modal.

- Build the dialog from existing `BillRecordEntity` values filtered by `record.localDate() == selectedDate && record.type == statisticsMode`.
  - Rationale: avoids additional repository queries and keeps the dialog synchronized with the already loaded statistics month.
  - Alternative considered: querying records on click. This would introduce async loading states and extra repository surface without clear benefit.

- Use a Material modal dialog or bottom-sheet style component implemented in Compose.
  - Rationale: the app already uses Compose-only UI and existing formatting helpers for categories, times, and amounts.
  - Alternative considered: launching a detail activity. This is heavier and breaks the reference's modal interaction.

## Risks / Trade-offs

- Large daily record counts could make the dialog too tall -> Use a bounded scrollable list inside the dialog.
- Dialog copy and Chinese text rendering must remain consistent with existing encoding handling -> Prefer existing string patterns or Unicode-safe literals where needed.
- Users might tap days with no records -> Do not open the dialog for empty-value days, or show an empty state if invoked defensively.
