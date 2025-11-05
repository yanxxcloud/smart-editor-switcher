# 功能设计：灵活自定义 AI IDE 跳转配置

## 概述

增强 Smart Editor Switcher 插件，支持灵活配置自定义 AI IDE 和编辑器，允许用户无需修改代码即可添加任何编辑器。当前系统硬编码了特定编辑器（Cursor、Kiro、VS Code 等），添加新编辑器需要修改代码。本设计使用户能够通过设置界面配置自定义编辑器，完全控制命令行参数和启动行为。

## 目标

- 使用户能够在不修改插件代码的情况下添加自定义 AI IDE 和编辑器
- 保持与现有硬编码编辑器配置的向后兼容性
- 支持灵活的命令行参数模板，适应不同编辑器的启动模式
- 提供直观的 UI 来管理自定义编辑器配置
- 保留所有现有功能：光标定位、项目上下文、键盘快捷键、状态栏和右键菜单集成

## 非目标

- 移除现有的硬编码编辑器支持（保留以提供默认便利性）
- 自动发现系统上所有可能的编辑器
- 为所有存在的 AI IDE 提供预配置模板
- 实现超出基本文件打开和光标定位的编辑器特定功能

## 当前系统分析

### 现有架构

**EditorType 枚举**: 固定的支持编辑器集合（KIRO、VSCODE、SUBLIME、CURSOR、ZED 等）

**EditorSwitcherService**: 
- 以独立属性存储编辑器路径（vsCodePath、cursorPath 等）
- buildCommand() 方法中硬编码的命令行参数模式
- detectEditorPaths() 方法中每个编辑器的固定检测逻辑

**Action 类**: 
- 每个编辑器一个 Action 类（SwitchToVSCodeAction、SwitchToCursorAction 等）
- 每个 Action 使用不同的 EditorType 枚举值触发相同的服务方法

**设置界面**: 
- 每个支持的编辑器的手动配置字段
- 硬编码编辑器标签的固定布局

**插件注册**: 
- 在 plugin.xml 中注册 Action，使用固定的 ID 和键盘快捷键

### 限制分析

当前架构需要：
- 修改代码才能添加新编辑器（枚举、服务属性、Action 类、UI 字段）
- 每次添加新编辑器都需要重新构建和重新安装插件
- 开发者无法轻松尝试小众或自定义构建的编辑器

## 设计方案

### 高层方法

引入**双模式系统**：
1. **内置编辑器**：保留现有的硬编码编辑器以提供便利（无需更改）
2. **自定义编辑器**：用户自定义编辑器的新灵活配置系统

### 数据模型

#### 自定义编辑器配置实体

每个自定义编辑器配置包含：

| 字段 | 类型 | 描述 | 是否必需 |
|-------|------|-------------|----------|
| 编辑器 ID | String | 唯一标识符（字母数字 + 下划线） | 是 |
| 显示名称 | String | UI 中显示的可读名称 | 是 |
| 可执行文件路径 | String | 编辑器可执行文件或命令的完整路径 | 是 |
| 图标 Emoji | String | 用于视觉识别的可选 emoji | 否 |
| 命令模板 | String | 构建启动命令的模板 | 是 |
| 是否启用 | Boolean | 此编辑器是否激活 | 是 |
| 键盘快捷键 | String | 可选的自定义键盘快捷键 | 否 |
| 在状态栏显示 | Boolean | 在状态栏组件菜单中显示 | 是 |
| 在右键菜单显示 | Boolean | 在右键上下文菜单中显示 | 是 |
| 在工具菜单显示 | Boolean | 在 Tools > Switch Editor 菜单中显示 | 是 |

#### 命令模板系统

命令模板使用占位符变量：

| 占位符 | 描述 | 示例值 |
|-------------|-------------|---------------|
| `{EXECUTABLE}` | 可执行文件路径 | `/usr/local/bin/code` |
| `{PROJECT}` | 当前项目根路径 | `/Users/user/myproject` |
| `{FILE}` | 当前文件绝对路径 | `/Users/user/myproject/src/Main.kt` |
| `{LINE}` | 当前光标行号 | `42` |
| `{COLUMN}` | 当前光标列号 | `15` |

**模板示例**：

```
VS Code 风格: {EXECUTABLE} {PROJECT} --goto {FILE}:{LINE}:{COLUMN}
Sublime 风格: {EXECUTABLE} {PROJECT} {FILE}:{LINE}:{COLUMN}
Zed 风格: {EXECUTABLE} {PROJECT} {FILE}:{LINE}:{COLUMN}
自定义风格: {EXECUTABLE} -p {PROJECT} -f {FILE} -l {LINE} -c {COLUMN}
```

**模板解析规则**：
- 占位符将被实际运行时值替换
- 空占位符（例如，没有打开文件）将导致省略该参数
- 额外的空格会被规范化
- 包含特殊字符的参数会自动添加引号

### 服务层设计

#### 增强的 EditorSwitcherService

**新状态属性**：

| 属性 | 类型 | 描述 |
|----------|------|-------------|
| customEditors | List<CustomEditorConfig> | 自定义编辑器配置列表 |

**新方法**：

| 方法 | 目的 | 参数 | 返回类型 |
|--------|---------|------------|-------------|
| addCustomEditor | 添加新的自定义编辑器 | config: CustomEditorConfig | Boolean (成功) |
| updateCustomEditor | 修改现有自定义编辑器 | editorId: String, config: CustomEditorConfig | Boolean (成功) |
| deleteCustomEditor | 删除自定义编辑器 | editorId: String | Boolean (成功) |
| getCustomEditor | 获取自定义编辑器配置 | editorId: String | CustomEditorConfig? |
| getAllCustomEditors | 获取所有自定义编辑器 | - | List<CustomEditorConfig> |
| switchToCustomEditor | 启动自定义编辑器 | editorId: String, filePath: String?, projectPath: String?, line: Int, column: Int | Unit |
| buildCustomCommand | 从模板构建命令 | config: CustomEditorConfig, filePath: String?, projectPath: String?, line: Int, column: Int | List<String> |
| validateTemplate | 检查模板语法有效性 | template: String | ValidationResult |

**模板解析逻辑**：
- 解析模板字符串并识别所有占位符
- 用相应的运行时值替换每个占位符
- 通过省略参数来处理缺失值（例如，未选择文件）
- 将最终字符串拆分为 ProcessBuilder 的命令数组

**验证要求**：
- 编辑器 ID 必须唯一且为字母数字
- 可执行文件路径必须存在或为有效命令
- 模板必须包含 `{EXECUTABLE}` 占位符
- 模板语法必须可解析

### UI 设计

#### 设置组件增强

**新部分：自定义编辑器**

视觉结构：
- 带有"添加自定义编辑器"按钮的工具栏
- 显示所有自定义编辑器的表格/列表，列包括：图标、名称、已启用、操作
- 每行的内联编辑/删除/启用/禁用操作
- "添加/编辑自定义编辑器"对话框表单

**添加/编辑自定义编辑器对话框**：

| 字段 | 输入类型 | 验证 |
|-------|------------|------------|
| 编辑器 ID | 文本字段（编辑时只读） | 唯一，字母数字 + 下划线，最大 50 个字符 |
| 显示名称 | 文本字段 | 必需，最大 100 个字符 |
| 可执行文件路径 | 文件浏览器字段 | 必需，文件必须存在或为有效命令 |
| 图标 Emoji | 文本字段 | 可选，单个 emoji 字符 |
| 命令模板 | 带语法帮助的文本区域 | 必需，必须包含 {EXECUTABLE} |
| 键盘快捷键 | 键盘快捷键记录器 | 可选，不得与现有快捷键冲突 |
| 在状态栏显示 | 复选框 | 默认：true |
| 在右键菜单显示 | 复选框 | 默认：true |
| 在工具菜单显示 | 复选框 | 默认：true |

**模板构建器助手**：
- 下拉菜单插入常用占位符
- 预览区域显示带示例值的示例命令
- 实时验证反馈

**自定义编辑器表格**：
- 按名称排序
- 过滤/搜索功能
- 批量启用/禁用
- 导出/导入配置（未来考虑）

### Action 注册设计

#### 动态 Action 创建

**挑战**：IntelliJ 平台的 Action 通常在 plugin.xml 中静态注册，但自定义编辑器需要动态注册。

**解决方法**：

**通用自定义编辑器 Action**：
- 单个 Action 类：`SwitchToCustomEditorAction`
- 为每个启用的自定义编辑器动态创建 Action 实例
- 生成 Action ID：`SmartEditorSwitcher.Custom.{EDITOR_ID}`

**Action 注册机制**：
- 使用 ActionManager 动态注册/取消注册 Action
- 在插件启动期间注册自定义 Action
- 当自定义编辑器配置更改时重新注册
- 保存设置时处理 Action 更新

**Action 组集成**：
- Tools 菜单中现有的 `SmartEditorSwitcher.MainGroup` 包含自定义编辑器
- 状态栏小部件菜单动态包含自定义编辑器
- 右键菜单根据配置标志动态包含自定义编辑器

**键盘快捷键处理**：
- 通过 Keymap 设置注册自定义键盘快捷键
- 快捷键冲突检测和警告
- 用户可以通过标准 IntelliJ keymap 设置修改快捷键

### 状态栏组件增强

**当前行为**：硬编码编辑器的静态列表

**增强行为**：
- 动态构建菜单，包括内置和启用的自定义编辑器
- 分离的视觉部分："内置编辑器"和"自定义编辑器"
- 如果配置了图标 emoji，则显示
- 遵守"在状态栏显示"配置标志

**菜单项格式**：`[图标] 显示名称`

### 右键菜单增强

**当前行为**：对内置编辑器 Action 的固定引用

**增强行为**：
- 动态添加自定义编辑器 Action 到右键菜单
- 遵守"在右键菜单显示"配置标志
- 使用分隔符保持内置和自定义编辑器之间的分离

### 数据持久化

**存储策略**：
- 现有：`SmartEditorSwitcher.xml` 存储内置编辑器路径
- 新增：相同的 XML 文件将自定义编辑器配置存储为嵌套元素
- 使用 IntelliJ 的 PersistentStateComponent 序列化

**XML 结构示例**：

```
状态结构：
- 内置编辑器路径（现有字段）
- customEditors（集合）
  - customEditor（重复元素）
    - editorId
    - displayName
    - executablePath
    - iconEmoji
    - commandTemplate
    - enabled
    - keyboardShortcut
    - showInStatusBar
    - showInContextMenu
    - showInToolsMenu
```

## 用户工作流程

### 添加自定义 AI IDE

1. 用户打开 Settings > Tools > Smart Editor Switcher
2. 导航到"自定义编辑器"部分
3. 点击"添加自定义编辑器"按钮
4. 出现带有空表单的对话框
5. 用户填写：
   - 编辑器 ID：`windsurf`
   - 显示名称：`Windsurf AI`
   - 可执行文件路径：浏览到 `/Applications/Windsurf.app/Contents/MacOS/windsurf`
   - 图标 Emoji：`🌊`
   - 命令模板：`{EXECUTABLE} {PROJECT} --goto {FILE}:{LINE}:{COLUMN}`
   - 复选框：全部选中（在所有位置显示）
   - 键盘快捷键：录制 `Ctrl+Alt+W`
6. 点击"确定"保存
7. 系统验证配置并注册 Action
8. 新编辑器出现在 Tools 菜单、状态栏和右键菜单中
9. 用户可以立即使用 `Ctrl+Alt+W` 切换到 Windsurf

### 编辑自定义编辑器

1. 用户打开 Settings > Tools > Smart Editor Switcher
2. 在表格中找到自定义编辑器
3. 点击"编辑"操作图标
4. 修改字段（编辑器 ID 除外）
5. 保存更改
6. 系统动态更新 Action 注册

### 禁用自定义编辑器

1. 用户打开 Settings > Tools > Smart Editor Switcher
2. 在表格中找到自定义编辑器
3. 切换"已启用"复选框或点击"禁用"操作
4. 编辑器从菜单中移除，快捷键停用
5. 配置保留以便将来重新启用

## 建议的 AI IDE 示例

用户可以配置这些流行的 AI IDE：

| AI IDE | 典型可执行文件路径 (macOS) | 典型命令模板 |
|--------|----------------------------------|--------------------------|
| Windsurf | `/Applications/Windsurf.app/Contents/MacOS/windsurf` | `{EXECUTABLE} {PROJECT} --goto {FILE}:{LINE}:{COLUMN}` |
| GitHub Copilot Workspace | 基于浏览器（需要特殊处理） | N/A - 超出范围 |
| Replit | 基于浏览器（需要特殊处理） | N/A - 超出范围 |
| Tabnine | 基于插件（非独立编辑器） | N/A - 超出范围 |
| Warp AI Terminal | `/Applications/Warp.app/Contents/MacOS/Warp` | `{EXECUTABLE} --working-directory {PROJECT}` |
| Pieces for Developers | `/Applications/Pieces.app/Contents/MacOS/Pieces` | `{EXECUTABLE} open {FILE}` |

**注意**：基于浏览器和基于插件的工具需要不同的集成方法，超出了本设计范围。

## 技术考虑

### 命令模板解析

**解析策略**：
- 使用正则表达式识别占位符模式：`\{[A-Z_]+\}`
- 将命令构建为字符串标记列表
- 用运行时值替换占位符
- 过滤掉空/null 参数

**边缘情况**：
- 缺少文件上下文：省略 {FILE}、{LINE}、{COLUMN} 参数
- 缺少项目上下文：省略 {PROJECT} 参数
- 路径中的空格：对包含空格的参数添加引号
- 特殊字符：根据平台需要转义或添加引号

### 平台兼容性

**命令执行**：
- macOS/Linux：直接可执行文件调用
- Windows：处理 `.exe` 扩展名和路径差异
- 进程执行：使用带有适当工作目录的 ProcessBuilder

**路径检测**：
- 支持绝对路径和 PATH 中的命令
- 保存前验证可执行文件可访问性
- 在 UI 中提供特定于平台的提示

### 性能考虑

**Action 注册**：
- 动态 Action 注册发生在设置保存期间（不频繁操作）
- 对 IDE 启动的性能影响最小
- Action 实例是轻量级的

**菜单填充**：
- 菜单打开时按需重建
- 带有脏标志的缓存自定义编辑器列表
- 没有可测量的性能影响

### 错误处理

**配置错误**：
- 无效的模板语法：在对话框中显示验证错误，阻止保存
- 重复的编辑器 ID：显示错误消息，阻止保存
- 缺少可执行文件：警告消息，允许保存以便未来修正
- 快捷键冲突：警告消息，建议替代方案

**运行时错误**：
- 找不到可执行文件：显示带有设置链接的错误通知
- 命令执行失败：记录错误并显示用户友好的通知
- 进程启动超时：通知用户潜在问题

**验证反馈**：
- 在添加/编辑对话框中实时验证
- 无效字段的视觉指示器（红色边框、错误图标）
- 带有修正建议的有用错误消息

## 迁移策略

### 向后兼容性

**现有用户**：
- 现有编辑器配置不变
- 内置编辑器继续像以前一样工作
- 自定义编辑器是附加功能，选择加入

**配置迁移**：
- 现有 XML 设置不需要迁移
- 新的 `customEditors` 集合添加到状态模式中
- 现有编辑器路径字段保持不变

### 插件更新过程

1. 用户将插件更新到新版本
2. 现有设置加载无修改
3. 新的"自定义编辑器"部分出现在设置 UI 中
4. 用户可以立即开始添加自定义编辑器
5. 现有工作流程没有破坏性更改

## 未来增强

超出本设计的潜在扩展：

- **预配置模板**：流行 AI IDE 配置库
- **导入/导出**：在团队成员之间共享自定义编辑器配置
- **URL 协议处理程序**：支持带有自定义 URL 方案的基于浏览器的 IDE
- **环境变量**：允许在模板中插入环境变量
- **条件参数**：特定于平台或上下文的参数模板逻辑
- **命令测试**：测试按钮验证命令执行而不切换
- **编辑器检测向导**：引导用户查找和配置新编辑器
- **云同步**：跨多台机器同步自定义编辑器配置

## 成功标准

**功能要求**：
- 用户可以无问题地添加至少 5 个自定义编辑器
- 自定义编辑器出现在所有配置的位置（菜单、状态栏）
- 自定义编辑器的键盘快捷键正常工作
- 自定义编辑器切换保持光标位置准确性
- 配置在 IDE 重启后保持

**可用性要求**：
- 添加自定义编辑器耗时不到 2 分钟
- 模板语法无需文档即可理解
- 验证错误清晰且可操作
- 用户不会对内置与自定义编辑器感到困惑

**技术要求**：
- 20+ 个自定义编辑器不会导致性能下降
- 动态 Action 注册不会内存泄漏
- 与 IntelliJ 平台 232-252.* 兼容
- 所有现有测试继续通过

## 风险评估

### 技术风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------------|--------|------------|
| 动态 Action 注册不稳定 | 低 | 高 | 通过 Action 管理器生命周期进行彻底测试 |
| 命令模板解析边缘情况 | 中 | 中 | 使用各种模板模式的综合测试套件 |
| 特定于平台的命令执行问题 | 中 | 中 | 特定于平台的测试和回退机制 |
| 多个自定义编辑器的性能影响 | 低 | 低 | 延迟初始化和缓存策略 |

### 用户体验风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------------|--------|------------|
| 模板语法对用户来说太复杂 | 中 | 中 | 模板构建器助手、示例和文档 |
| 内置和自定义编辑器之间的混淆 | 低 | 低 | UI 中清晰的视觉分离 |
| 错误配置导致失败 | 高 | 低 | 验证、测试工具和有用的错误消息 |

## 未解决问题

无 - 设计完整，可以进入实施。
