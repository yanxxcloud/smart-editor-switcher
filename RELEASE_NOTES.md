# Smart Editor Switcher v1.0.0 发布说明

## 🚀 首次正式发布

Smart Editor Switcher 是一个专业的 JetBrains IDE 插件，让您在 IDE 和其他主流编辑器之间无缝切换。支持智能光标定位和项目上下文保持。

**作者**: yanxxcloud

## ✨ 核心功能

### 🎯 智能光标定位
- 切换编辑器时保持精确的光标位置（行号和列号）
- 支持所有主流编辑器的光标定位语法

### 📁 项目上下文保持
- 自动传递当前项目路径
- 保持文件和项目的完整上下文

### ⚡ 多种访问方式
- **快捷键**: 一键快速切换
- **状态栏**: 点击底部状态栏快速访问
- **右键菜单**: 在文件和项目视图中快速切换
- **工具菜单**: 通过 Tools 菜单访问

### 🔍 智能路径检测
- 自动检测已安装编辑器的路径
- 支持 Windows、macOS、Linux 多平台
- 一键自动配置所有编辑器路径

## 📝 支持的编辑器

| 编辑器 | 快捷键 | 光标定位 | 项目支持 |
|--------|--------|----------|----------|
| 📘 **VS Code** | `Ctrl+Alt+V` | ✅ | ✅ |
| 🎯 **Cursor** | `Ctrl+Alt+C` | ✅ | ✅ |
| ⚡ **Zed** | `Ctrl+Alt+Z` | ✅ | ✅ |
| 🚀 **Kiro** | `Ctrl+Alt+K` | ✅ | ✅ |
| 🎨 **Sublime Text** | `Ctrl+Alt+S` | ✅ | ✅ |

## 🛠️ 安装和配置

### 安装方式
1. **JetBrains Marketplace**: 在 IDE 中搜索 "Smart Editor Switcher"
2. **手动安装**: 下载 `editor-switcher-1.0.0.zip` 并在 IDE 中安装

### 配置步骤
1. 打开 `Settings/Preferences` → `Tools` → `Smart Editor Switcher`
2. 点击"自动检测路径"按钮，或手动配置编辑器路径
3. 开始使用快捷键或状态栏切换编辑器

## 🎯 使用场景

- **AI 辅助开发**: 在 JetBrains IDE 和 Kiro/Cursor 之间快速切换
- **代码审查**: 使用不同编辑器的特色功能
- **团队协作**: 适应不同团队成员的编辑器偏好
- **学习新工具**: 轻松尝试新的编辑器

## 🔧 技术特性

- **跨平台支持**: Windows、macOS、Linux
- **多 IDE 兼容**: 支持所有 JetBrains IDE
- **轻量级**: 最小化资源占用
- **可扩展**: 易于添加新编辑器支持

## 📋 系统要求

- **IDE 版本**: IntelliJ Platform 232+ (2023.2+)
- **Java 版本**: JDK 17+
- **支持的 IDE**: IntelliJ IDEA, PyCharm, WebStorm, PhpStorm, CLion, GoLand 等

## 🐛 已知问题

- 某些编辑器可能需要手动配置路径
- Windows 用户可能需要管理员权限启动某些编辑器

## 🔮 未来计划

- 添加更多编辑器支持
- 自定义快捷键配置
- 编辑器启动参数配置
- 主题和图标定制

## 📞 支持和反馈

- **GitHub**: https://github.com/kiro-ide/editor-switcher
- **邮箱**: support@kiro.io
- **官网**: https://kiro.io

---

感谢使用 Kiro Editor Switcher！🎉