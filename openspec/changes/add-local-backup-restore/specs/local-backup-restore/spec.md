## ADDED Requirements

### Requirement: 本地账单数据导出

The app SHALL 允许用户在设置页将当前本地账单数据导出为备份文件。

#### Scenario: 用户生成数据库数据

- **GIVEN** 用户打开设置页
- **WHEN** 用户点击“生成数据库数据”
- **THEN** App SHALL 从 Room 数据库读取全部账单记录
- **AND** App SHALL 生成一个本地备份文件
- **AND** App SHALL 显示备份文件生成结果和文件所在目录

#### Scenario: 导出文件包含备份元数据

- **GIVEN** App 正在生成备份文件
- **WHEN** 备份文件写入完成
- **THEN** 备份文件 SHALL 包含备份版本号
- **AND** 备份文件 SHALL 包含导出时间
- **AND** 备份文件 SHALL 包含账单记录列表

#### Scenario: 没有账单时生成备份

- **GIVEN** 本地 Room 数据库中没有账单记录
- **WHEN** 用户点击“生成数据库数据”
- **THEN** App SHALL 仍然生成合法备份文件
- **AND** 备份文件中的账单记录列表 SHALL 为空

### Requirement: 本地账单数据导入

The app SHALL 允许用户在设置页从指定目录读取备份文件，并将账单数据恢复到本地 Room 数据库。

#### Scenario: 用户读取数据库数据

- **GIVEN** 用户已经把备份文件放到 App 指定目录
- **WHEN** 用户在设置页点击“读取数据库数据”
- **THEN** App SHALL 读取该备份文件
- **AND** App SHALL 校验备份文件格式和版本
- **AND** App SHALL 将有效账单记录写入 Room 数据库
- **AND** App SHALL 显示导入结果

#### Scenario: 备份文件不存在

- **GIVEN** 指定目录中不存在备份文件
- **WHEN** 用户点击“读取数据库数据”
- **THEN** App SHALL NOT 修改 Room 数据库
- **AND** App SHALL 显示文件不存在或目录错误的提示

#### Scenario: 备份文件格式错误

- **GIVEN** 指定目录中存在备份文件
- **WHEN** 备份文件不是合法 JSON 或缺少必要字段
- **THEN** App SHALL NOT 修改 Room 数据库
- **AND** App SHALL 显示备份文件无效的提示

#### Scenario: 导入重复账单

- **GIVEN** 本地数据库中已经存在与备份文件相同的账单记录
- **WHEN** App 导入备份文件
- **THEN** App SHALL 跳过重复账单
- **AND** App SHALL NOT 新增重复记录

#### Scenario: 导入后刷新页面

- **GIVEN** App 成功导入备份文件
- **WHEN** 导入流程结束
- **THEN** App SHALL 刷新明细页和统计页使用的数据
- **AND** App SHALL 显示导入成功、导入数量和跳过重复数量

### Requirement: 本地迁移流程提示

The app SHALL 在设置页清晰提示备份文件的导出和导入目录，帮助用户完成换机迁移。

#### Scenario: 设置页显示备份目录

- **GIVEN** 用户打开设置页
- **WHEN** 设置页展示备份和恢复入口
- **THEN** App SHALL 显示或可查看备份文件目录
- **AND** App SHALL 显示备份文件名

#### Scenario: 导入需要重启时提示用户

- **GIVEN** App 成功导入备份文件
- **WHEN** 当前实现无法立即刷新全部页面数据
- **THEN** App SHALL 明确提示用户需要重启 App 后生效

### Requirement: 离线备份恢复

The app SHALL 在无网络环境下完成备份和恢复。

#### Scenario: 无网络时导出和导入

- **GIVEN** 设备没有网络连接
- **WHEN** 用户生成数据库数据或读取数据库数据
- **THEN** App SHALL 仅使用本地 Room 数据库和本地文件完成操作
- **AND** App SHALL NOT 请求服务端接口
