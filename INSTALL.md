# 安装说明

## 插件文件位置
构建完成的插件文件位于：
```
build/distributions/editor-switcher-1.0-SNAPSHOT.zip
```

## 兼容性
- **支持的 IDE 版本**: 232 - 252.* (IntelliJ IDEA 2023.2+ 到 2025.2+)
- **支持的 IDE**: IntelliJ IDEA, PyCharm, WebStorm, PhpStorm, CLion, GoLand 等所有 JetBrains IDE

## 安装步骤

### 方法一：通过 JetBrains IDE 安装
1. 打开你的 JetBrains IDE（IntelliJ IDEA、PyCharm、WebStorm 等）
2. 进入 `Settings/Preferences` > `Plugins`
3. 点击齿轮图标 ⚙️ > `Install Plugin from Disk...`
4. 选择 `build/distributions/editor-switcher-1.0-SNAPSHOT.zip` 文件
5. 重启 IDE

### 方法二：手动安装
1. 解压 `editor-switcher-1.0-SNAPSHOT.zip`
2. 将解压后的文件夹复制到 IDE 插件目录：
   - **macOS**: `~/Library/Application Support/JetBrains/[IDE]/plugins/`
   - **Windows**: `%APPDATA%\JetBrains\[IDE]\plugins\`
   - **Linux**: `~/.local/share/JetBrains/[IDE]/plugins/`
3. 重启 IDE

## 配置

安装后，进入 `Settings/Preferences` > `Tools` > `Editor Switcher` 配置编辑器路径：

### Kiro 路径示例
- **macOS**: `/Applications/Kiro.app/Contents/MacOS/Kiro`
- **Windows**: `C:\Program Files\Kiro\Kiro.exe`
- **Linux**: `/usr/local/bin/kiro`

### VS Code 路径示例
- **macOS**: `/Applications/Visual Studio Code.app/Contents/Resources/app/bin/code`
- **Windows**: `C:\Program Files\Microsoft VS Code\bin\code.cmd`
- **Linux**: `/usr/bin/code`

### Sublime Text 路径示例
- **macOS**: `/Applications/Sublime Text.app/Contents/SharedSupport/bin/subl`
- **Windows**: `C:\Program Files\Sublime Text\subl.exe`
- **Linux**: `/usr/bin/subl`

## 使用方法

### 快捷键
- `Ctrl+Alt+K`: 切换到 Kiro
- `Ctrl+Alt+V`: 切换到 VS Code
- `Ctrl+Alt+S`: 切换到 Sublime Text

### 菜单
`Tools` > `Switch Editor` > 选择目标编辑器

## 故障排除

1. **编辑器未启动**：检查编辑器路径是否正确配置
2. **权限问题**：确保 IDE 有权限执行外部程序
3. **路径包含空格**：某些系统可能需要用引号包围路径

## 开发

如需修改插件：
1. 修改源代码
2. 运行 `./gradlew buildPlugin`
3. 重新安装生成的插件文件