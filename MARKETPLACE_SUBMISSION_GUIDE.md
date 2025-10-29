# JetBrains Plugin Marketplace 提交指南

## 📝 提交步骤

### 1. 创建 JetBrains 账户
1. 访问 [JetBrains Plugin Marketplace](https://plugins.jetbrains.com/)
2. 点击右上角 "Sign In" 
3. 如果没有账户，点击 "Create Account" 创建新账户
4. 使用你的邮箱注册（建议使用 yanxxcloud@gmail.com）

### 2. 申请开发者权限
1. 登录后，访问 [Plugin Developer Portal](https://plugins.jetbrains.com/author/me)
2. 点击 "Become a Plugin Developer"
3. 填写开发者信息：
   - **Name**: yanxxcloud
   - **Email**: yanxxcloud@gmail.com
   - **Company** (可选): 个人开发者
   - **Website**: https://github.com/yanxxcloud

### 3. 上传插件

#### 3.1 进入上传页面
1. 访问 [Upload Plugin](https://plugins.jetbrains.com/plugin/add)
2. 或在开发者面板点击 "Upload Plugin"

#### 3.2 填写基本信息
- **Plugin Name**: `Smart Editor Switcher`
- **Plugin ID**: `io.yanxxcloud.editorswitcher`
- **Category**: `Tools Integration`
- **Tags**: `editor`, `switcher`, `productivity`, `tools`, `external`

#### 3.3 上传插件文件
- 选择文件: `build/distributions/editor-switcher-1.0.0.zip`
- 系统会自动解析 plugin.xml 中的信息

#### 3.4 填写详细描述

**Short Description** (简短描述):
```
A professional editor switcher plugin for seamless switching between JetBrains IDE and popular external editors with smart cursor positioning.
```

**Full Description** (完整描述):
```html
<h3>Smart Editor Switcher - Intelligent Editor Switching Tool</h3>

<p>A professional editor switching plugin that enables seamless transitions between JetBrains IDE and other mainstream editors. Features smart cursor positioning and project context preservation.</p>

<h4>🚀 Core Features</h4>
<ul>
    <li><strong>Smart Cursor Positioning</strong> - Maintains precise cursor position and line/column information when switching</li>
    <li><strong>Project Context Preservation</strong> - Automatically passes current project and file paths</li>
    <li><strong>One-Click Quick Switching</strong> - Supports keyboard shortcuts and status bar quick access</li>
    <li><strong>Auto Path Detection</strong> - Intelligently recognizes installed editor paths</li>
    <li><strong>Right-Click Menu Integration</strong> - Quick switching in file and project views</li>
</ul>

<h4>📝 Supported Editors</h4>
<ul>
    <li>📘 <strong>VS Code</strong> - Microsoft Visual Studio Code</li>
    <li>🎯 <strong>Cursor</strong> - AI Code Editor</li>
    <li>⚡ <strong>Zed</strong> - High-performance Code Editor</li>
    <li>🚀 <strong>Kiro</strong> - AI-driven Editor</li>
    <li>🎨 <strong>Sublime Text</strong> - Lightweight Text Editor</li>
    <li>⚛️ <strong>Atom</strong> - GitHub Open Source Editor</li>
    <li>📝 <strong>Notepad++</strong> - Windows Text Editor</li>
    <li>🖥️ <strong>Vim/Emacs</strong> - Classic Editors</li>
</ul>

<h4>⌨️ Keyboard Shortcuts</h4>
<ul>
    <li><kbd>Ctrl+Alt+V</kbd> - Switch to VS Code</li>
    <li><kbd>Ctrl+Alt+C</kbd> - Switch to Cursor</li>
    <li><kbd>Ctrl+Alt+Z</kbd> - Switch to Zed</li>
    <li><kbd>Ctrl+Alt+K</kbd> - Switch to Kiro</li>
    <li><kbd>Ctrl+Alt+S</kbd> - Switch to Sublime Text</li>
</ul>

<h4>🛠️ Configuration</h4>
<p>Configure editor paths in <strong>Settings → Tools → Smart Editor Switcher</strong>, or use the auto-detection feature.</p>

<h4>🎯 Use Cases</h4>
<ul>
    <li><strong>AI-Assisted Development</strong> - Quick switching between JetBrains IDE and Kiro/Cursor</li>
    <li><strong>Code Review</strong> - Utilize different editors' unique features</li>
    <li><strong>Team Collaboration</strong> - Adapt to different team members' editor preferences</li>
    <li><strong>Learning New Tools</strong> - Easily try new editors</li>
</ul>

<h4>🔧 Technical Features</h4>
<ul>
    <li><strong>Cross-Platform Support</strong> - Windows, macOS, Linux</li>
    <li><strong>Multi-IDE Compatible</strong> - Supports all JetBrains IDEs</li>
    <li><strong>Lightweight</strong> - Minimal resource usage</li>
    <li><strong>Extensible</strong> - Easy to add new editor support</li>
</ul>

<h4>📋 System Requirements</h4>
<ul>
    <li><strong>IDE Version</strong> - IntelliJ Platform 232+ (2023.2+)</li>
    <li><strong>Java Version</strong> - JDK 17+</li>
    <li><strong>Supported IDEs</strong> - IntelliJ IDEA, PyCharm, WebStorm, PhpStorm, CLion, GoLand, etc.</li>
</ul>

<p><strong>Author</strong>: yanxxcloud<br>
<strong>Source Code</strong>: <a href="https://github.com/yanxxcloud/smart-editor-switcher">GitHub Repository</a></p>
```

#### 3.5 设置兼容性
- **Since Build**: `232` (IntelliJ Platform 2023.2)
- **Until Build**: `252.*` (IntelliJ Platform 2025.2)
- **Supported Products**: 选择所有 JetBrains IDE

#### 3.6 添加截图和图标
建议添加：
1. **Plugin Icon** (40x40 px): 插件图标
2. **Screenshots**: 
   - 设置界面截图
   - 状态栏截图
   - 右键菜单截图
   - 使用演示截图

#### 3.7 设置许可证
- **License**: MIT License
- **License URL**: https://github.com/yanxxcloud/smart-editor-switcher/blob/main/LICENSE

### 4. 提交审核

#### 4.1 检查清单
- [ ] 插件名称和描述准确
- [ ] 版本号正确 (1.0.0)
- [ ] 兼容性设置正确
- [ ] 所有必填字段已填写
- [ ] 插件文件上传成功

#### 4.2 提交
1. 点击 "Submit for Review"
2. 插件将进入审核队列
3. 通常审核时间为 1-3 个工作日

### 5. 审核过程

#### 5.1 自动检查
- 插件结构验证
- 兼容性检查
- 安全扫描

#### 5.2 人工审核
- 功能测试
- 描述准确性
- 用户体验

#### 5.3 可能的结果
- **Approved**: 插件通过审核，发布到市场
- **Rejected**: 需要修改后重新提交
- **Pending**: 需要更多信息或修改

### 6. 发布后管理

#### 6.1 监控
- 查看下载统计
- 监控用户评价
- 处理用户反馈

#### 6.2 更新
- 修复 bug
- 添加新功能
- 更新兼容性

## 📞 联系信息

- **GitHub**: https://github.com/yanxxcloud/smart-editor-switcher
- **Email**: yanxxcloud@gmail.com

## 🔗 有用链接

- [Plugin Development Guidelines](https://plugins.jetbrains.com/docs/intellij/plugin-development-guidelines.html)
- [Plugin Marketplace Guidelines](https://plugins.jetbrains.com/legal/approval-guidelines)
- [Plugin Developer Agreement](https://plugins.jetbrains.com/legal/developer-agreement)

---

**注意**: 首次提交可能需要更长的审核时间。确保遵循所有指南以提高通过率。