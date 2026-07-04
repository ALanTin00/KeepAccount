# 提交日志

## 2026-07-04

### Add ledger validation tests

- 新增账本计算单元测试，覆盖支出/入账汇总、不计入收支、日期分组、分类排行和金额正负显示。
- 新增 Room Repository 真机测试，覆盖账单保存、月份筛选、分类筛选和 20 条分页加载。
- 新增 Room 数据库 Migration 测试，验证 1 到 2 版本升级时保留旧账单并回填 `updatedAt`。
- 新增离线能力测试，验证 App 未申请 `INTERNET` 权限。
- OpenSpec `add-ledger-home` 任务更新为 41/41 全部完成。
- 已通过 `testDebugUnitTest`、`assembleDebug`、`connectedDebugAndroidTest` 和 OpenSpec strict 校验。

## 2026-07-03

### Initial KeepAccount Android app

- 初始化 KeepAccount Android 项目并配置 Git 仓库。
- 实现本地 Room 数据库账单存储，支持支出、入账、不计入收支三类账单。
- 实现明细页、统计页、设置页、新增/编辑/删除账单、分类筛选、年月筛选和分页加载。
- 加入 2024 和 2025 两年本地测试数据生成逻辑。
- 移除设置页独立清空全部账单入口，仅保留重新生成测试数据的二次确认流程。
- 补充 Android/Gradle 常用 `.gitignore`，避免提交本地配置和构建产物。
