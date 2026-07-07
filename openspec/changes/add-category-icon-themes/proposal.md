## Why

The app currently has one fixed category icon mapping, while the provided assets contain three complete 19-icon sets for the existing 19 bill categories. Users need a settings entry that opens a dedicated appearance page to switch the visual style of category icons without rebuilding or restarting the app.

## What Changes

- Add three complete category icon themes sourced from the provided folders.
- Rename the Chinese asset filenames to Android-safe English resource names before adding them to the project.
- Add a settings-page button labeled `佬凤爱美丽` that opens a dedicated Activity with a back button and title.
- Add three icon-switch buttons to the `佬凤爱美丽` page, each using an icon from one provided set as the preview and the button text `切换`.
- Persist the selected icon theme locally and refresh category icons immediately after switching; app restart is not required.
- Keep all Chinese UI text and OpenSpec artifacts encoded as UTF-8.

## Capabilities

### New Capabilities
- `category-icon-themes`: Category icon theme assets, dedicated switching page behavior, persistence, and immediate UI refresh requirements.

### Modified Capabilities

## Impact

- Adds new PNG resources under `app/src/main/res/drawable` with English resource names.
- Updates shared category icon resolution in the Compose UI.
- Updates settings UI, the new appearance Activity, and ViewModel state/preferences for theme selection.
- Affects category icon rendering across ledger, add bill, statistics, category detail, and monthly ranking screens.
