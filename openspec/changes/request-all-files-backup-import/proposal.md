## Why

公共 Download 目录在 Android 10+ 会受到分区存储影响，使用 MediaStore 写入同名文件时可能生成 `keep_account_backup (1).json`、`keep_account_backup (2).json` 这类副本，不能稳定覆盖固定文件。纯内部私有目录虽然不需要权限，但用户在电脑上很难找到文件。为了降低操作门槛，备份文件改为放在 App 专属外部目录中，由 App 自己读写和覆盖，同时电脑连接手机后更容易在 `Android/data/com.example.keepaccount/files/backup` 找到。

## What Changes

- “生成数据库数据”改为写入 App 专属外部备份目录，并覆盖旧文件。
- “读取数据库数据”改为从同一个 App 专属外部备份文件读取，不再读取公共 Download 目录，也不再读取内部私有目录。
- 不申请存储权限，不引导用户去系统设置授权，不使用系统文件选择器。
- 设置页面展示的路径和操作指引同步改成 `Android/data/com.example.keepaccount/files/backup/keep_account_backup.json`。
- 保留现有 JSON 格式、版本校验、重复账单跳过和导入结果提示。

## Capabilities

### New Capabilities

- `app-specific-external-backup-storage`: 使用 App 专属外部目录保存和读取本地数据库备份文件，避免公共目录权限和重复文件问题，同时让电脑连接手机后更容易找到备份文件。

### Modified Capabilities

## Impact

- Affected app area: 设置页、备份导出/导入流程、本地文件读写逻辑、多语言设置文案。
- Android permissions: 不需要 `MANAGE_EXTERNAL_STORAGE` 或公共目录写入权限来生成/读取备份文件。
- Storage behavior: 备份文件位于 App 专属外部目录，例如 `Android/data/com.example.keepaccount/files/backup/keep_account_backup.json`。
- Lifecycle: App 卸载时，该目录内的备份文件会随 App 数据一起删除。