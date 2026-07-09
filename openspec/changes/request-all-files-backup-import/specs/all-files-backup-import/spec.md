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

#### Scenario: 设置页展示换手机操作步骤
- **GIVEN** the user opens the settings page
- **WHEN** backup generate and read actions are visible
- **THEN** the app SHALL describe the backup location as an app-specific external folder under `Android/data/com.example.keepaccount/files/backup`
- **AND** the app SHALL remove guidance that tells the user to use Download
- **AND** the app SHALL mention that the user can use the phone file manager or connect the phone to a computer to copy the backup file
- **AND** the app SHALL instruct the user to open the app once on the new phone so the folder is created before placing the backup file there\n- **AND** the app SHALL instruct the user to tap the read database data action on the new phone after placing the backup file\n- **AND** the app SHALL mention that the file is deleted when the app is uninstalled

### Requirement: 无权限备份恢复
The app SHALL complete backup generation and reading without requesting external storage permissions.

#### Scenario: 无存储权限时生成和读取
- **GIVEN** the app has not been granted external storage permissions
- **WHEN** the user generates or reads database data in settings
- **THEN** the app SHALL complete the operation using only app-specific external storage
- **AND** the app SHALL NOT open Android all-files access settings
- **AND** the app SHALL NOT open the system file picker
### Requirement: 独立换机页面
The app SHALL provide a dedicated change-phone page for database backup generation and reading.

#### Scenario: 从设置页进入换机页面
- **GIVEN** the user opens the settings page
- **WHEN** the page is displayed
- **THEN** the app SHALL show a centered button labeled `佬凤爱换机` in Simplified Chinese
- **AND** the app SHALL navigate to a dedicated activity when the button is tapped
- **AND** the settings page SHALL no longer show backup path guidance or database generate/read buttons directly

#### Scenario: 换机页面承载备份操作
- **GIVEN** the user opens the change-phone page
- **WHEN** the page is displayed
- **THEN** the app SHALL show the backup directory, backup file name, five-step change-phone operation guidance, generate database data action, and read database data action
- **AND** each guidance step SHALL end with a newline
- **AND** the guidance SHALL explain old-phone export, copying the file by computer, placing it on the new phone, and importing on the new phone

#### Scenario: 换机页面多语言标题
- **GIVEN** the user switches app language
- **WHEN** the change-phone entry or page title is displayed
- **THEN** Simplified Chinese SHALL use `佬凤爱换机`
- **AND** Traditional Chinese SHALL use the Traditional Chinese translation
- **AND** English, Japanese, and Korean SHALL translate the title as change phone