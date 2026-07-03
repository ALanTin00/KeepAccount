## ADDED Requirements

### Requirement: 本地离线 App

The app SHALL 作为纯本地记账 App 运行，不要求网络连接。

#### Scenario: 无网络时打开 App

- **GIVEN** 设备没有网络连接
- **WHEN** 用户打开 App
- **THEN** 用户 SHALL 可以查看、新增、筛选和统计本地账单记录

#### Scenario: 不依赖远程数据

- **GIVEN** App 需要读取或写入账单数据
- **WHEN** App 执行账单相关操作
- **THEN** App SHALL 使用本地 Room 数据库
- **AND** App SHALL NOT 依赖服务端 API

### Requirement: 本地 Room 存储

The app SHALL 使用本地 Room 数据库存储账单记录。

#### Scenario: 用户保存账单

- **GIVEN** 用户填写了一条有效账单记录
- **WHEN** 用户点击确定保存
- **THEN** 账单 SHALL 保存到 Room 数据库
- **AND** 账单 SHALL 出现在对应日期和月份的明细列表中

#### Scenario: 用户重启 App

- **GIVEN** 用户已经保存过账单记录
- **WHEN** 用户关闭并重新打开 App
- **THEN** 已保存的账单记录 SHALL 仍然可以从 Room 数据库读取

#### Scenario: 数据库结构升级

- **GIVEN** App 已经安装并存在本地账单数据
- **WHEN** 新版本 App 增加或修改 Room 数据库字段
- **THEN** App SHALL 通过 Room Migration 升级数据库结构
- **AND** 已有账单数据 SHALL 保留
- **AND** App SHALL NOT 通过清空数据库完成升级

### Requirement: 默认图标使用

The app SHALL 在本次变更中使用 Android 默认启动图标作为分类图标和占位图片。

#### Scenario: 展示分类图标

- **GIVEN** 账单分类出现在列表、选择器、图表图例或详情页中
- **WHEN** UI 需要显示该分类的图标
- **THEN** 代码实现 SHALL 引用 `R.mipmap.ic_launcher`

### Requirement: 默认页面状态和底部导航

The app SHALL 提供明细、统计、设置三个底部导航入口，并在首次打开时进入明细页。

#### Scenario: 用户首次打开 App

- **GIVEN** 用户首次打开 App
- **WHEN** 页面加载完成
- **THEN** App SHALL 默认显示“明细”页
- **AND** 明细页 SHALL 默认使用当前年月
- **AND** 明细页 SHALL 默认使用“全部类型”筛选

#### Scenario: 用户切换底部导航

- **GIVEN** 用户正在使用 App
- **WHEN** 用户点击底部“明细”或“统计”
- **THEN** App SHALL 切换到对应页面

#### Scenario: 用户打开设置页

- **GIVEN** 用户正在使用 App
- **WHEN** 用户点击底部“设置”
- **THEN** App SHALL 显示设置页或设置占位页

### Requirement: 设置页

The app SHALL 提供基础设置页，用于管理本地账单数据。

#### Scenario: 用户重新生成测试数据

- **GIVEN** 用户打开设置页
- **WHEN** 用户点击重新生成测试数据
- **THEN** App SHALL 请求用户确认
- **AND** 用户确认后 App SHALL 清空当前账单记录
- **AND** App SHALL 写入 2024 和 2025 两年的本地测试账单数据

### Requirement: 账单分类

The app SHALL 提供默认支出分类和收入分类。

#### Scenario: 展示支出分类

- **GIVEN** 用户需要选择支出分类
- **WHEN** 分类列表打开
- **THEN** App SHALL 展示餐饮、交通、购物、生活缴费、医疗、服饰、娱乐、服务、教育、运动、旅行、宠物、保险、公益

#### Scenario: 展示收入分类

- **GIVEN** 用户需要选择收入分类
- **WHEN** 分类列表打开
- **THEN** App SHALL 展示工资、奖金、退款、投资、其他

#### Scenario: 用户选择全部类型

- **GIVEN** 明细页存在类型筛选
- **WHEN** 用户选择“全部类型”
- **THEN** 明细页 SHALL 清除类型筛选
- **AND** 明细页 SHALL 展示当前年月下的全部账单记录

### Requirement: 明细页

The app SHALL 提供按月份查看账单记录的明细页。

#### Scenario: 用户查看明细页

- **GIVEN** 用户打开“明细”页
- **WHEN** 页面加载完成
- **THEN** 页面 SHALL 显示当前选中的年月、总支出、总入账和按日期分组的账单列表
- **AND** 日期分组 SHALL 按日期从新到旧排序
- **AND** 同一天内的账单 SHALL 按记录时间从新到旧排序
- **AND** 明细列表 SHALL 首次只加载 20 条账单记录

#### Scenario: 用户上拉加载更多明细

- **GIVEN** 明细页还有更多账单记录
- **WHEN** 用户滚动到列表底部
- **THEN** 明细列表 SHALL 加载下一页账单记录
- **AND** 每次 SHALL 最多追加 20 条账单记录

#### Scenario: 明细页分页不影响汇总

- **GIVEN** 明细页只加载了部分账单记录
- **WHEN** 页面显示总支出和总入账
- **THEN** 汇总金额 SHALL 基于当前年月和筛选条件下的全部匹配账单计算

#### Scenario: 用户按分类筛选明细

- **GIVEN** 用户打开类型筛选弹窗
- **WHEN** 用户选择一个分类
- **THEN** 类型筛选弹窗 SHALL 关闭
- **AND** 选中的分类文案 SHALL 替换默认的“全部类型”文案
- **AND** 明细列表 SHALL 只显示匹配该分类的账单记录

#### Scenario: 用户关闭类型筛选弹窗

- **GIVEN** 用户打开类型筛选弹窗
- **WHEN** 用户点击关闭按钮
- **THEN** 类型筛选弹窗 SHALL 关闭
- **AND** 当前类型筛选条件 SHALL 保持不变

#### Scenario: 用户按月份筛选明细

- **GIVEN** 用户打开年月选择弹窗
- **WHEN** 用户选择年月并点击确定
- **THEN** 年月选择弹窗 SHALL 关闭
- **AND** 明细页 SHALL 更新为该年月下的账单列表和汇总金额

#### Scenario: 用户取消月份筛选

- **GIVEN** 用户打开年月选择弹窗
- **WHEN** 用户点击取消
- **THEN** 年月选择弹窗 SHALL 关闭
- **AND** 当前年月筛选条件 SHALL 保持不变

#### Scenario: 明细页没有账单

- **GIVEN** 当前年月和类型筛选下没有账单记录
- **WHEN** 明细页加载完成
- **THEN** 明细页 SHALL 显示空状态
- **AND** 明细页 SHALL 保留“记一笔”入口

#### Scenario: 明细页显示金额

- **GIVEN** 明细页展示账单记录
- **WHEN** 账单类型为支出
- **THEN** 金额 SHALL 显示为负数
- **AND** 当账单类型为入账时，金额 SHALL 显示为正数

#### Scenario: 用户打开账单详情

- **GIVEN** 明细页展示账单记录
- **WHEN** 用户点击一条账单
- **THEN** App SHALL 打开该账单的详情视图
- **AND** 详情视图 SHALL 显示类型、分类、金额、日期和备注

#### Scenario: 用户删除账单

- **GIVEN** 用户正在查看账单详情
- **WHEN** 用户确认删除该账单
- **THEN** App SHALL 从 Room 数据库删除该账单
- **AND** 明细页 SHALL 不再显示该账单
- **AND** 统计页 SHALL 使用删除后的数据重新计算

#### Scenario: 用户编辑账单

- **GIVEN** 用户正在查看账单详情
- **WHEN** 用户点击编辑
- **THEN** App SHALL 打开预填该账单数据的编辑表单
- **AND** 用户保存后 SHALL 更新原账单记录
- **AND** App SHALL NOT 新增重复账单记录

### Requirement: 新增记账

The app SHALL 允许用户新增本地账单记录。

#### Scenario: 用户新增一笔账单

- **GIVEN** 用户打开新增记账页面或弹窗
- **WHEN** 用户填写金额、类型、分类、日期和可选备注
- **AND** 用户点击确定
- **THEN** 账单 SHALL 保存到本地 Room 数据库
- **AND** 明细页和统计页 SHALL 使用这条新账单更新显示结果
- **AND** 新增记账页面或弹窗 SHALL 关闭

#### Scenario: 用户选择具体日期

- **GIVEN** 用户正在新增记账
- **WHEN** 用户打开日期选择弹窗并选择日期
- **THEN** 新增记账页面 SHALL 显示用户选择的日期

#### Scenario: 新增记账默认值

- **GIVEN** 用户打开新增记账页面或弹窗
- **WHEN** 页面初始化完成
- **THEN** 账单类型 SHALL 默认为支出
- **AND** 分类 SHALL 默认为餐饮
- **AND** 日期 SHALL 默认为当天

#### Scenario: 金额为空或为零

- **GIVEN** 用户正在新增记账
- **WHEN** 用户未输入金额或输入金额为 0 并点击确定
- **THEN** App SHALL NOT 保存账单
- **AND** App SHALL 提示用户输入有效金额

#### Scenario: 用户添加备注

- **GIVEN** 用户正在新增记账
- **WHEN** 用户输入备注并确认
- **THEN** 备注 SHALL 返回新增记账页面
- **AND** 备注 SHALL 随账单一起保存

#### Scenario: 备注为空

- **GIVEN** 用户正在新增记账
- **WHEN** 用户不填写备注并保存账单
- **THEN** App SHALL 允许保存账单

#### Scenario: 备注超过长度限制

- **GIVEN** 用户正在输入备注
- **WHEN** 备注内容超过 30 个字符
- **THEN** App SHALL 限制继续输入或提示用户备注最多 30 个字符

#### Scenario: 不计入收支记录

- **GIVEN** 用户新增一条“不计入收支”账单
- **WHEN** 账单保存成功
- **THEN** 账单 SHALL 显示在明细列表中
- **AND** 账单 SHALL NOT 计入总支出
- **AND** 账单 SHALL NOT 计入总入账
- **AND** 账单 SHALL NOT 参与统计页图表和排行

### Requirement: 统计页

The app SHALL 基于本地账单记录提供按月份统计功能。

#### Scenario: 用户查看月度统计

- **GIVEN** 用户打开“统计”页
- **WHEN** 用户选择一个年月
- **THEN** 页面 SHALL 显示当前模式下的总金额、分类占比、分类排行、每日对比和月度对比

#### Scenario: 统计页计算总金额

- **GIVEN** 用户正在查看统计页
- **WHEN** 当前模式为支出
- **THEN** 总金额 SHALL 等于选中年月内所有支出账单金额合计
- **AND** 当当前模式为入账时，总金额 SHALL 等于选中年月内所有入账账单金额合计

#### Scenario: 统计页计算分类占比

- **GIVEN** 用户正在查看统计页
- **WHEN** 当前模式下存在账单记录
- **THEN** 分类占比 SHALL 按分类金额除以当前模式总金额计算
- **AND** 分类排行 SHALL 按分类金额从高到低排序

#### Scenario: 统计页计算对比图

- **GIVEN** 用户正在查看统计页
- **WHEN** 页面展示每日对比和月度对比
- **THEN** 每日对比 SHALL 按自然日汇总当前模式金额
- **AND** 月度对比 SHALL 按月份汇总当前模式金额

#### Scenario: 用户切换支出和入账

- **GIVEN** 用户正在查看统计页
- **WHEN** 用户切换“支出”或“入账”
- **THEN** 统计页 SHALL 按当前模式重新计算并展示统计数据

#### Scenario: 统计页没有账单

- **GIVEN** 当前年月和模式下没有账单记录
- **WHEN** 统计页加载完成
- **THEN** 统计页 SHALL 显示空状态
- **AND** 统计页 SHALL NOT 显示空图表

#### Scenario: 用户打开分类详情

- **GIVEN** 用户正在查看分类排行
- **WHEN** 用户点击一个分类
- **THEN** App SHALL 打开该分类的统计详情页
- **AND** 详情页 SHALL 支持按金额或按时间排序

#### Scenario: 分类详情按金额排序

- **GIVEN** 用户打开分类详情页
- **WHEN** 用户选择按金额排序
- **THEN** 账单列表 SHALL 按金额绝对值从大到小排序

#### Scenario: 分类详情按时间排序

- **GIVEN** 用户打开分类详情页
- **WHEN** 用户选择按时间排序
- **THEN** 账单列表 SHALL 按记录时间从新到旧排序

#### Scenario: 分类详情没有账单

- **GIVEN** 当前分类在选中年月下没有账单记录
- **WHEN** 分类详情页加载完成
- **THEN** 分类详情页 SHALL 显示空状态
