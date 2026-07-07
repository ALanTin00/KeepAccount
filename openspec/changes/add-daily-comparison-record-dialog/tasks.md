## 1. Chart Interaction

- [x] 1.1 Extend the daily comparison chart tooltip click handling to surface the selected `DailyChartPoint` to `StatisticsPage`.
- [x] 1.2 Keep existing daily bar selection behavior while opening the dialog only from the selected black tooltip for days with matching records.

## 2. Dialog Data

- [x] 2.1 Add transient Compose state for the selected daily dialog date.
- [x] 2.2 Derive dialog records from `statisticsRecords` by selected date and active statistics mode.
- [x] 2.3 Compute the dialog title total from the filtered records.

## 3. Dialog UI

- [x] 3.1 Implement a modal dialog matching the reference layout: dim overlay, rounded white panel, date/total title, ranked records, divider decoration, and dismiss button.
- [x] 3.2 Render each record row with rank, category icon/name, time, optional note, and signed amount.
- [x] 3.3 Add empty-state protection for a selected date with no matching records.

## 4. Verification

- [x] 4.1 Run `./gradlew.bat :app:compileDebugKotlin`.
- [x] 4.2 Manually verify tapping a daily expense bar only shows the black tooltip, then tapping the tooltip opens and dismisses the record dialog without changing month or mode.
