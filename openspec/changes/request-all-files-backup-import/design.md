## Context

The previous public Download approach can create duplicate files on modern Android because MediaStore may rename colliding file names instead of overwriting them. App internal storage avoids permissions but is hard for the user to find from a computer. The updated requirement is to keep backup read/write in app-specific external storage so no broad storage permission is needed, the app can reliably overwrite the backup file, and the user can more easily find the file from a computer under Android/data.

## Goals / Non-Goals

**Goals:**
- Store generated backup files in app-specific external storage, under a stable app-owned path such as `Android/data/com.example.keepaccount/files/backup/keep_account_backup.json`.
- Overwrite the same backup file every time the user generates database data.
- Read database data from the same app-specific external backup file.
- Remove all-files permission UI, manifest permission, and file picker fallback from this flow.
- Update settings page path labels and guidance to match the app-specific external storage behavior.

**Non-Goals:**
- Sharing the backup file through Android's public Downloads folder.
- Requesting broad storage permission.
- Changing the backup JSON schema or duplicate handling rules.

## Decisions

1. Use `Context.getExternalFilesDir(null)` for backup files.
   - Rationale: App-specific external files are app-owned, require no runtime storage permission, can be overwritten with normal file APIs, and are easier to locate from a computer than internal private storage.
   - Alternative considered: Keep app internal files storage. Rejected because the user needs a lower-friction computer-visible location.
   - Alternative considered: Keep using Download with all-files access. Rejected because the requirement removes permission prompts and avoids public-folder duplicates.

2. Keep a dedicated `backup` subdirectory inside app-specific external files.
   - Rationale: Keeps the backup file organized while staying inside the app-owned external folder.

3. Remove system picker and all-files permission from settings backup actions.
   - Rationale: The read/write source is now the app's app-specific external backup file, so external shared-file access is no longer part of this flow.

## Risks / Trade-offs

- App-specific external storage is deleted on uninstall -> settings guidance must make clear that this backup follows app data lifecycle.
- Some Android versions or device file managers may restrict browsing `Android/data`; connecting to a computer is still the intended lower-friction path for locating the file.
- If external storage is unavailable, generation/read should fail with a clear message instead of requesting storage permission.