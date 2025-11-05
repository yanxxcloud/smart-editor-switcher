## 🔧 v1.0.1 (2024-10-30)

### 修复和改进
- **API 兼容性**: 修复了已弃用 API 的使用，提升与新版本 IntelliJ Platform 的兼容性
- **代码优化**: 
  - 替换 `FileChooserDescriptorFactory.createSingleFileDescriptor()` 为现代 API
  - 修复 `StatusBarWidget.MultipleTextValuesPresentation.getPopupStep()` 弃用警告
  - 优化文件选择器实现，提升用户体验
- **构建改进**: 解决编译错误，确保插件能够正常构建和发布

### 技术改进
- 使用 `FileChooserDescriptor` 替代已弃用的工厂方法
- 添加适当的 `@Deprecated` 注解以保持向后兼容
- 优化状态栏组件的事件处理

### 📦 安装方式

1. **JetBrains Marketplace**: 在 IDE 中搜索 "Smart Editor Switcher"
2. **手动安装**: 下载下方的 `editor-switcher-1.0.1.zip` 文件并在 IDE 中安装

### 🎯 支持的编辑器

- 📘 **VS Code** (`Ctrl+Alt+V`)
- 🎯 **Cursor** (`Ctrl+Alt+C`) 
- ⚡ **Zed** (`Ctrl+Alt+Z`)
- 🚀 **Kiro** (`Ctrl+Alt+K`)
- 🎨 **Sublime Text** (`Ctrl+Alt+S`)

### 📋 系统要求

- **IDE 版本**: IntelliJ Platform 232+ (2023.2+)
- **Java 版本**: JDK 17+
- **支持的 IDE**: IntelliJ IDEA, PyCharm, WebStorm, PhpStorm, CLion, GoLand 等

---

**完整更新日志**: https://github.com/yanxxcloud/smart-editor-switcher/blob/main/RELEASE_NOTES.md