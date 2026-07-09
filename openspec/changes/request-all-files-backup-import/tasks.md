## 1. Storage Simplification

- [x] 1.1 Remove all-files access permission and helper code from the backup flow.
- [x] 1.2 Change backup generation to write `keep_account_backup.json` under app-specific external files storage.
- [x] 1.3 Ensure generation overwrites the same app-specific external backup file instead of creating duplicate filenames.

## 2. Import Flow

- [x] 2.1 Change settings import to read from the app-specific external backup file.
- [x] 2.2 Remove all-files permission dialog and system file picker from the settings backup flow.
- [x] 2.3 Preserve existing JSON validation, duplicate skipping, and import result messaging.
- [x] 2.4 Show a clear missing-file message when the app-specific external backup file does not exist.

## 3. Settings Guidance

- [x] 3.1 Update settings page path labels and operation guidance to describe app-specific external storage under Android/data.
- [x] 3.2 Update localized strings for Simplified Chinese, Traditional Chinese, English, Japanese, and Korean.

## 4. Verification

- [x] 4.1 Build the debug app successfully.
- [x] 4.2 Test on a real Android device that generate database data writes one app-specific external backup file and overwrites it on repeated taps.
- [x] 4.3 Test on a real Android device that read database data imports from the app-specific external backup file without storage permission.
- [x] 4.4 Test on a real Android device that missing app-specific external backup file shows a clear failure message and does not modify the database.