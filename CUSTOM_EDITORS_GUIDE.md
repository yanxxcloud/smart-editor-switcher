# Custom AI IDE Configuration Feature

## Overview

This feature allows users to add custom AI IDEs and editors to Smart Editor Switcher without modifying the plugin code. Users can configure any editor through the settings UI with full control over command-line arguments.

## Features

- **Flexible Editor Configuration**: Add any editor/IDE with custom command templates
- **Template-based Commands**: Use placeholders like `{EXECUTABLE}`, `{PROJECT}`, `{FILE}`, `{LINE}`, `{COLUMN}`
- **Full UI Integration**: Custom editors appear in Tools menu, status bar, and context menus
- **Backward Compatible**: Existing built-in editors continue to work as before
- **Easy Management**: Add, edit, delete, and enable/disable custom editors from settings

## How to Add a Custom AI IDE

1. **Open Settings**
   - Go to `Settings > Tools > Smart Editor Switcher`

2. **Add Custom Editor**
   - Navigate to the "Custom Editors" section
   - Click the "+" (Add) button in the table toolbar

3. **Configure Editor**
   - **Editor ID**: Unique identifier (e.g., `windsurf`)
   - **Display Name**: Name shown in menus (e.g., `Windsurf AI`)
   - **Executable Path**: Browse to the editor executable
   - **Icon Emoji**: Optional emoji for visual identification (e.g., `🌊`)
   - **Command Template**: Define how to launch the editor
   - **Keyboard Shortcut**: Optional custom shortcut (e.g., `ctrl alt W`)
   - **Visibility**: Choose where the editor appears (Tools menu, Status bar, Context menu)

4. **Save and Apply**
   - Click "OK" to save the configuration
   - Click "Apply" in the settings dialog
   - The custom editor is now ready to use!

## Command Template Examples

### Popular AI IDEs

**Windsurf** (VS Code-style):
```
{EXECUTABLE} {PROJECT} --goto {FILE}:{LINE}:{COLUMN}
```

**Warp Terminal**:
```
{EXECUTABLE} --working-directory {PROJECT}
```

**Custom Editor** (with separate flags):
```
{EXECUTABLE} -p {PROJECT} -f {FILE} -l {LINE} -c {COLUMN}
```

### Template Placeholders

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `{EXECUTABLE}` | Editor executable path | `/usr/local/bin/code` |
| `{PROJECT}` | Current project root path | `/Users/user/myproject` |
| `{FILE}` | Current file absolute path | `/Users/user/myproject/src/Main.kt` |
| `{LINE}` | Current cursor line number | `42` |
| `{COLUMN}` | Current cursor column number | `15` |

## Example: Adding Windsurf AI

1. **Editor ID**: `windsurf`
2. **Display Name**: `Windsurf AI`
3. **Executable Path**: `/Applications/Windsurf.app/Contents/MacOS/windsurf` (macOS)
4. **Icon Emoji**: `🌊`
5. **Command Template**: `{EXECUTABLE} {PROJECT} --goto {FILE}:{LINE}:{COLUMN}`
6. **Keyboard Shortcut**: `ctrl alt W`
7. **Visibility**: All checkboxes selected

After saving and applying, you can:
- Press `Ctrl+Alt+W` to switch to Windsurf
- Select "🌊 Windsurf AI" from the status bar menu
- Right-click on a file and choose "Open with External Editor > 🌊 Windsurf AI"
- Use "Tools > Switch Editor > 🌊 Windsurf AI" menu

## Managing Custom Editors

### Edit an Editor
1. Select the editor row in the table
2. Click the "Edit" (pencil) button
3. Modify fields and save

### Delete an Editor
1. Select the editor row in the table
2. Click the "Delete" (trash) button
3. Confirm deletion

### Enable/Disable an Editor
- Edit the editor and toggle the "enabled" status
- Or simply delete and re-add when needed

## Technical Details

### Command Parsing
- Templates are parsed at runtime
- Empty placeholders (e.g., no file open) are omitted
- Spaces in paths are handled automatically
- Commands are executed via `ProcessBuilder`

### Platform Compatibility
- Works on macOS, Linux, and Windows
- Use platform-specific paths for executables
- Test your command template before relying on it

### Validation
- Editor ID must be unique and alphanumeric + underscore
- Display name is required (max 100 chars)
- Executable path is required
- Template must contain `{EXECUTABLE}` placeholder
- Real-time validation in the dialog

## Troubleshooting

**Editor doesn't launch**:
- Verify the executable path exists and is correct
- Check if the command template has proper syntax
- Look at IDE logs for error messages

**Command template errors**:
- Ensure `{EXECUTABLE}` is present
- Use correct placeholder names (case-sensitive)
- Test the command manually in terminal first

**Keyboard shortcut conflicts**:
- Choose a unique shortcut
- Check existing shortcuts in `Settings > Keymap`

## Future Enhancements

- Pre-configured templates library for popular AI IDEs
- Import/Export custom editor configurations
- Command testing feature
- Environment variable support in templates
- Cloud sync across machines

## Feedback

This is a new feature. Please report any issues or suggestions for improvement!
