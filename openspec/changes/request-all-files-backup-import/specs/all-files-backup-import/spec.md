## ADDED Requirements

### Requirement: App 专属外部备份路径
The app SHALL use app-specific external storage as the fixed backup file location for both generating and reading database backup files.

#### Scenario: 生成数据库文件到 App 专属外部目录
- **GIVEN** the user is on the settings page
- **WHEN** the user taps the generate database data action
- **THEN** the app SHALL create or replace `keep_account_backup.json` under the app-owned external backup directory
- **AND** the app SHALL use a path equivalent to `Android/data/com.example.keepaccount/files/backup/keep_account_backup.json`
- **AND** the app SHALL NOT create `keep_account_backup (1).json` or any other duplicate backup filename
- **AND** the app SHALL NOT require Android storage permission for this operation

#### Scenario: 从 App 专属外部目录读取数据库文件
- **GIVEN** an app-specific external backup file exists
- **WHEN** the user taps the read database data action
- **THEN** the app SHALL read from the app-owned external backup file
- **AND** the app SHALL import valid non-duplicate bill records into the local database
- **AND** the app SHALL show the import count and duplicate-skip count
- **AND** the app SHALL NOT require Android storage permission for this operation

#### Scenario: App 专属外部备份文件不存在
- **GIVEN** the app-owned external backup file does not exist
- **WHEN** the user taps the read database data action
- **THEN** the app SHALL NOT modify the local database
- **AND** the app SHALL show a message that the backup file does not exist

### Requirement: 设置页 App 专属外部备份指引
The settings page SHALL display backup operation guidance that matches app-specific external storage behavior.

#### Scenario: 设置页展示电脑可找的 App 专属目录路径
- **GIVEN** the user opens the settings page
- **WHEN** backup generate and read actions are visible
- **THEN** the app SHALL describe the backup location as an app-specific external folder under `Android/data/com.example.keepaccount/files/backup`
- **AND** the app SHALL remove guidance that tells the user to use Download
- **AND** the app SHALL mention that connecting the phone to a computer can locate the file in that folder
- **AND** the app SHALL mention that the file is deleted when the app is uninstalled

### Requirement: 无权限备份恢复
The app SHALL complete backup generation and reading without requesting external storage permissions.

#### Scenario: 无存储权限时生成和读取
- **GIVEN** the app has not been granted external storage permissions
- **WHEN** the user generates or reads database data in settings
- **THEN** the app SHALL complete the operation using only app-specific external storage
- **AND** the app SHALL NOT open Android all-files access settings
- **AND** the app SHALL NOT open the system file picker