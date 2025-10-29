# Smart Editor Switcher

一个专业的 JetBrains IDE 插件，让您在 IDE 和其他主流编辑器之间无缝切换。支持智能光标定位和项目上下文保持。

**作者**: yanxxcloud

## ✨ 功能特性

- 🚀 **智能光标定位** - 切换时保持精确的光标位置和行列信息
- 📁 **项目上下文保持** - 自动传递当前项目和文件路径
- ⚡ **一键快速切换** - 支持快捷键和状态栏快速访问
- 🔍 **自动路径检测** - 智能识别已安装的编辑器路径
- 🎯 **多编辑器支持** - 支持 VS Code、Cursor、Zed、Kiro、Sublime Text 等主流编辑器
- 🖱️ **右键菜单集成** - 在文件和项目视图中快速切换

## 快捷键

- `Ctrl+Alt+K`: 切换到 Kiro
- `Ctrl+Alt+V`: 切换到 VS Code  
- `Ctrl+Alt+S`: 切换到 Sublime Text

## 安装

1. 克隆此仓库
2. 在项目根目录运行 `./gradlew buildPlugin`
3. 在 JetBrains IDE 中安装生成的插件文件

## 配置

1. 打开 Settings/Preferences
2. 导航到 Tools > Editor Switcher
3. 配置各个编辑器的可执行文件路径
4. 或点击"自动检测路径"按钮

## 使用方法

### 通过菜单
Tools > Switch Editor > 选择目标编辑器

### 通过快捷键
使用上述快捷键直接切换

### 通过右键菜单
在文件上右键，选择相应的切换选项

## 支持的编辑器

- **Kiro**: 新一代 AI 驱动的编辑器
- **VS Code**: Microsoft Visual Studio Code
- **Sublime Text**: 轻量级文本编辑器

## 开发

### 构建插件
```bash
./gradlew buildPlugin
```

### 运行测试
```bash
./gradlew test
```

### 在 IDE 中运行
```bash
./gradlew runIde
```

## 贡献

欢迎提交 Issue 和 Pull Request！

## 许可证

MIT License