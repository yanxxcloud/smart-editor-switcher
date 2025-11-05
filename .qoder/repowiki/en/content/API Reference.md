# API Reference

<cite>
**Referenced Files in This Document**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt)
- [EditorUtils.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/utils/EditorUtils.kt)
- [plugin.xml](file://src/main/resources/META-INF/plugin.xml)
- [SwitchToVSCodeAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToVSCodeAction.kt)
- [EditorSwitcherStatusBarWidget.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidget.kt)
- [SmartEditorSwitcherConfigurable.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherConfigurable.kt)
- [SmartEditorSwitcherSettingsComponent.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherSettingsComponent.kt)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [EditorSwitcherService API](#editorswitcherservice-api)
3. [EditorUtils API](#editorutils-api)
4. [Service Lifecycle and Initialization](#service-lifecycle-and-initialization)
5. [Usage Patterns and Examples](#usage-patterns-and-examples)
6. [Thread Safety Considerations](#thread-safety-considerations)
7. [Version Compatibility](#version-compatibility)
8. [Common Usage Patterns](#common-usage-patterns)
9. [Error Handling Guidelines](#error-handling-guidelines)
10. [Migration Guidance](#migration-guidance)

## Introduction

The Smart Editor Switcher plugin provides two primary public APIs for integrating editor switching functionality into JetBrains IDEs:

- **EditorSwitcherService**: The main service responsible for managing editor configurations and executing switches
- **EditorUtils**: Utility class for extracting editor context information from IntelliJ Platform APIs

These APIs enable seamless transitions between JetBrains IDEs and external editors while maintaining project context, cursor positions, and file information.

## EditorSwitcherService API

The EditorSwitcherService is a singleton service that manages editor configurations and handles the actual switching process.

### Public Methods

#### getInstance()

**Signature**: `fun getInstance(): EditorSwitcherService`

**Purpose**: Retrieves the singleton instance of the EditorSwitcherService.

**Parameters**: None

**Return Type**: `EditorSwitcherService`

**Thread Safety**: Thread-safe. Uses IntelliJ Platform's ApplicationManager.getService() mechanism.

**Usage Example**:
```kotlin
val service = EditorSwitcherService.getInstance()
// Use the service instance for editor switching operations
```

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L28-L30)

#### getEditorPath()

**Signature**: `fun getEditorPath(editorType: EditorType): String`

**Purpose**: Retrieves the configured executable path for a specific editor type.

**Parameters**:
- `editorType`: The type of editor (KIRO, VSCODE, SUBLIME, ATOM, NOTEPADPP, VIM, EMACS, CURSOR, ZED)

**Return Type**: `String` - The configured path or empty string if not configured

**Thread Safety**: Thread-safe. Reads from immutable state properties.

**Usage Example**:
```kotlin
val service = EditorSwitcherService.getInstance()
val vscodePath = service.getEditorPath(EditorType.VSCODE)
if (vscodePath.isNotEmpty()) {
    // Editor is configured
}
```

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L32-L42)

#### setEditorPath()

**Signature**: `fun setEditorPath(editorType: EditorType, path: String)`

**Purpose**: Sets the executable path for a specific editor type.

**Parameters**:
- `editorType`: The type of editor to configure
- `path`: The filesystem path to the editor's executable

**Return Type**: `Unit`

**Thread Safety**: Thread-safe. Updates internal mutable state properties.

**Usage Example**:
```kotlin
val service = EditorSwitcherService.getInstance()
service.setEditorPath(EditorType.VSCODE, "/Applications/Visual Studio Code.app/Contents/Resources/app/bin/code")
```

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L15-L26)

#### executeSwitch()

**Signature**: `fun switchToEditor(editorType: EditorType, filePath: String?, projectPath: String?, line: Int, column: Int)`

**Purpose**: Executes the editor switching operation with full context preservation.

**Parameters**:
- `editorType`: The target editor type to switch to
- `filePath`: Optional path to the file to open (null for project-only switches)
- `projectPath`: Optional path to the project root (null if not available)
- `line`: Line number for cursor positioning (default: 1)
- `column`: Column number for cursor positioning (default: 1)

**Return Type**: `Unit`

**Exceptions**: May throw `Exception` during process execution, caught internally with logging.

**Thread Safety**: Thread-safe. Creates new ProcessBuilder instances for each execution.

**Usage Example**:
```kotlin
val service = EditorSwitcherService.getInstance()
val context = EditorUtils.getEditorContext(actionEvent)
service.switchToEditor(
    EditorType.VSCODE, 
    context.filePath, 
    context.projectPath, 
    context.line, 
    context.column
)
```

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L44-L62)

#### detectEditorPaths()

**Signature**: `fun detectEditorPaths()`

**Purpose**: Automatically detects and configures paths for all supported editors.

**Parameters**: None

**Return Type**: `Unit`

**Thread Safety**: Thread-safe. Updates internal mutable state properties.

**Usage Example**:
```kotlin
val service = EditorSwitcherService.getInstance()
service.detectEditorPaths()
// Paths will be automatically configured for all detected editors
```

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L144-L162)

### Configuration Properties

The service maintains configuration for nine editor types:

| Property | Type | Description |
|----------|------|-------------|
| `kiroPath` | `String` | Path to Kiro editor executable |
| `vsCodePath` | `String` | Path to VS Code executable |
| `sublimePath` | `String` | Path to Sublime Text executable |
| `atomPath` | `String` | Path to Atom editor executable |
| `notepadPlusPlusPath` | `String` | Path to Notepad++ executable |
| `vimPath` | `String` | Path to Vim executable |
| `emacsPath` | `String` | Path to Emacs executable |
| `cursorPath` | `String` | Path to Cursor editor executable |
| `zedPath` | `String` | Path to Zed editor executable |

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L15-L26)

## EditorUtils API

The EditorUtils class provides context extraction functionality for determining the current editor state within IntelliJ Platform actions.

### Public Methods

#### getEditorContext()

**Signature**: `fun getEditorContext(e: AnActionEvent): EditorContext`

**Purpose**: Extracts comprehensive editor context information from an action event.

**Parameters**:
- `e`: The AnActionEvent containing the current context

**Return Type**: `EditorContext` data class with the following properties:
- `project`: Current project (nullable)
- `file`: Current virtual file (nullable)
- `filePath`: String representation of file path (nullable)
- `projectPath`: String representation of project base path (nullable)
- `line`: Current line number (1-based)
- `column`: Current column number (1-based)

**Thread Safety**: Thread-safe. Uses IntelliJ Platform API safely within action context.

**Usage Example**:
```kotlin
class MyAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val context = EditorUtils.getEditorContext(e)
        // Access context properties
        println("Current file: ${context.filePath}")
        println("Current position: ${context.line}:${context.column}")
        
        // Use context for editor switching
        val service = EditorSwitcherService.getInstance()
        service.switchToEditor(EditorType.VSCODE, context.filePath, context.projectPath, context.line, context.column)
    }
}
```

**Section sources**
- [EditorUtils.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/utils/EditorUtils.kt#L15-L44)

### EditorContext Data Class

The EditorContext data class encapsulates all relevant editor state information:

```kotlin
data class EditorContext(
    val project: Project?,
    val file: VirtualFile?,
    val filePath: String?,
    val projectPath: String?,
    val line: Int,
    val column: Int
)
```

**Properties**:
- `project`: The current IntelliJ project (null if no project is open)
- `file`: The current virtual file being edited (null if no file is open)
- `filePath`: String representation of the file path (null if no file)
- `projectPath`: String representation of the project base path (null if no project)
- `line`: Current line number (1-based, starts at 1)
- `column`: Current column number (1-based, starts at 1)

**Section sources**
- [EditorUtils.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/utils/EditorUtils.kt#L10-L14)

## Service Lifecycle and Initialization

### Plugin Registration

The EditorSwitcherService is registered as an application service in the plugin.xml configuration:

```xml
<extensions defaultExtensionNs="com.intellij">
    <applicationService 
        serviceImplementation="io.yanxxcloud.editorswitcher.services.EditorSwitcherService"/>
</extensions>
```

**Section sources**
- [plugin.xml](file://src/main/resources/META-INF/plugin.xml#L48-L50)

### Service Initialization

The service follows IntelliJ Platform's service lifecycle:

1. **Registration**: Service is declared in plugin.xml
2. **Lazy Initialization**: Created on first access via ApplicationManager
3. **Persistence**: State is automatically persisted to SmartEditorSwitcher.xml
4. **Configuration**: Loaded from persistent storage on startup

### State Management

The service implements `PersistentStateComponent` for automatic persistence:

```kotlin
@State(
    name = "io.yanxxcloud.editorswitcher.services.EditorSwitcherService",
    storages = [Storage("SmartEditorSwitcher.xml")]
)
```

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L10-L13)

## Usage Patterns and Examples

### Basic Editor Switching Pattern

```kotlin
class SwitchToVSCodeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // Get editor context
        val context = EditorUtils.getEditorContext(e)
        
        // Get service instance
        val service = EditorSwitcherService.getInstance()
        
        // Check if VS Code is configured
        if (service.vsCodePath.isEmpty()) {
            service.detectEditorPaths()
            if (service.vsCodePath.isEmpty()) {
                Messages.showErrorDialog(
                    context.project,
                    "VS Code 路径未配置，请在设置中配置。",
                    "Smart Editor Switcher"
                )
                return
            }
        }
        
        // Execute switch
        service.switchToEditor(
            EditorType.VSCODE, 
            context.filePath, 
            context.projectPath, 
            context.line, 
            context.column
        )
    }
}
```

**Section sources**
- [SwitchToVSCodeAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToVSCodeAction.kt#L10-L45)

### Configuration Management Pattern

```kotlin
class SmartEditorSwitcherConfigurable : Configurable {
    override fun apply() {
        val service = EditorSwitcherService.getInstance()
        val component = settingsComponent ?: return
        
        // Apply configuration changes
        service.vsCodePath = component.vsCodePathText
        service.cursorPath = component.cursorPathText
        service.zedPath = component.zedPathText
        service.kiroPath = component.kiroPathText
        service.sublimePath = component.sublimePathText
    }
    
    override fun reset() {
        val service = EditorSwitcherService.getInstance()
        val component = settingsComponent ?: return
        
        // Reset UI to current service state
        component.vsCodePathText = service.vsCodePath
        component.cursorPathText = service.cursorPath
        component.zedPathText = service.zedPath
        component.kiroPathText = service.kiroPath
        component.sublimePathText = service.sublimePath
    }
}
```

**Section sources**
- [SmartEditorSwitcherConfigurable.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherConfigurable.kt#L30-L55)

### Status Bar Widget Integration

```kotlin
class EditorSwitcherStatusBarWidget(project: Project) : EditorBasedWidget(project) {
    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return Consumer { _ ->
            val popup = createPopup()
            popup?.showInBestPositionFor(
                com.intellij.openapi.actionSystem.impl.SimpleDataContext.getProjectContext(project)
            )
        }
    }
    
    private fun createPopup(): ListPopup? {
        val group = EditorSwitcherActionGroup()
        return JBPopupFactory.getInstance().createActionGroupPopup(
            "切换到编辑器",
            group,
            com.intellij.openapi.actionSystem.impl.SimpleDataContext.getProjectContext(project),
            JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
            false
        )
    }
}
```

**Section sources**
- [EditorSwitcherStatusBarWidget.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidget.kt#L25-L55)

## Thread Safety Considerations

### Service Thread Safety

The EditorSwitcherService is designed with thread safety in mind:

- **Singleton Access**: Uses `ApplicationManager.getApplication().getService()` for thread-safe singleton access
- **Immutable State**: Configuration properties are accessed safely from multiple threads
- **Process Execution**: Each `switchToEditor` call creates isolated ProcessBuilder instances
- **Logging**: Uses thread-safe logging mechanisms

### EditorUtils Thread Safety

The EditorUtils class is stateless and thread-safe:

- **Pure Functions**: All methods operate on input parameters without maintaining state
- **IntelliJ APIs**: Uses thread-safe IntelliJ Platform APIs for context extraction
- **Data Classes**: Immutable data structures prevent concurrent modification issues

### Best Practices

1. **Always use `getInstance()`** for accessing the service
2. **Check editor paths** before attempting to switch
3. **Handle exceptions** gracefully in production code
4. **Use appropriate logging** for debugging and monitoring

## Version Compatibility

### IntelliJ Platform Compatibility

The plugin targets IntelliJ Platform versions 2023.2 through 2025.2:

```xml
<idea-version since-build="232" until-build="252.*"/>
```

**Section sources**
- [plugin.xml](file://src/main/resources/META-INF/plugin.xml#L44-L45)

### API Stability Guarantees

- **Public Methods**: All documented public methods maintain backward compatibility
- **Data Classes**: EditorContext structure remains stable across versions
- **Enum Types**: EditorType enumeration will not remove existing values
- **Exception Handling**: Error handling patterns remain consistent

### Migration Considerations

When upgrading to newer IntelliJ Platform versions:

1. **Review API Changes**: Check for any deprecations in IntelliJ Platform APIs
2. **Test Editor Paths**: Verify path detection works with new platform versions
3. **Validate Command Formats**: Ensure editor command formats remain compatible
4. **Update Dependencies**: Review Gradle dependencies for compatibility

## Common Usage Patterns

### Pattern 1: Action-Based Switching

```kotlin
class CustomEditorAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val context = EditorUtils.getEditorContext(e)
        val service = EditorSwitcherService.getInstance()
        
        // Safe path checking
        if (service.customEditorPath.isEmpty()) {
            service.detectEditorPaths()
            if (service.customEditorPath.isEmpty()) {
                // Handle missing configuration
                return
            }
        }
        
        // Execute switch with context preservation
        service.switchToEditor(
            EditorType.CUSTOM_EDITOR,
            context.filePath,
            context.projectPath,
            context.line,
            context.column
        )
    }
}
```

### Pattern 2: Configuration-Driven Setup

```kotlin
class ConfigurationSetup {
    fun setupEditors() {
        val service = EditorSwitcherService.getInstance()
        
        // Auto-detect paths
        service.detectEditorPaths()
        
        // Manual configuration fallback
        if (service.vsCodePath.isEmpty()) {
            service.vsCodePath = "/custom/path/to/code"
        }
        
        // Validate configurations
        validateEditorPaths(service)
    }
    
    private fun validateEditorPaths(service: EditorSwitcherService) {
        val requiredEditors = listOf(
            EditorType.VSCODE,
            EditorType.SUBLINE
        )
        
        for (editorType in requiredEditors) {
            val path = when (editorType) {
                EditorType.VSCODE -> service.vsCodePath
                EditorType.SUBLINE -> service.sublimePath
                // ... other editors
            }
            
            if (path.isEmpty()) {
                // Log warning or show notification
            }
        }
    }
}
```

### Pattern 3: Context-Aware UI Integration

```kotlin
class ContextAwareComponent {
    fun updateUI(e: AnActionEvent) {
        val context = EditorUtils.getEditorContext(e)
        
        // Enable/disable UI elements based on context
        val hasFile = context.file != null
        val hasProject = context.project != null
        
        // Update UI state
        updateFileActions(hasFile)
        updateProjectActions(hasProject)
    }
}
```

## Error Handling Guidelines

### Common Error Scenarios

1. **Missing Editor Paths**: When `switchToEditor` is called with unconfigured editor paths
2. **Process Execution Failures**: When external editor processes fail to start
3. **Invalid Context**: When `getEditorContext` is called outside valid action context
4. **File Access Issues**: When trying to access files that don't exist or aren't accessible

### Recommended Error Handling

```kotlin
class RobustEditorSwitcher {
    fun safeSwitchToEditor(e: AnActionEvent, editorType: EditorType) {
        try {
            val context = EditorUtils.getEditorContext(e)
            val service = EditorSwitcherService.getInstance()
            
            // Check if editor is configured
            val editorPath = when (editorType) {
                EditorType.VSCODE -> service.vsCodePath
                EditorType.VSCODE -> service.vsCodePath
                // ... other editors
            }
            
            if (editorPath.isEmpty()) {
                // Show user-friendly error message
                Messages.showErrorDialog(
                    context.project,
                    "编辑器路径未配置，请先在设置中配置。",
                    "Smart Editor Switcher"
                )
                return
            }
            
            // Attempt switch with graceful error handling
            service.switchToEditor(
                editorType,
                context.filePath,
                context.projectPath,
                context.line,
                context.column
            )
            
        } catch (ex: Exception) {
            // Log error with context
            Logger.getInstance(this::class.java).error(
                "Failed to switch to $editorType", ex
            )
            
            // Show user-friendly error message
            Messages.showErrorDialog(
                e.project,
                "切换编辑器失败：${ex.message}",
                "Smart Editor Switcher"
            )
        }
    }
}
```

### Performance Considerations

#### Frequent Calls Impact

- **Context Extraction**: `getEditorContext()` is lightweight but involves multiple API calls
- **Path Detection**: `detectEditorPaths()` performs filesystem operations and can be expensive
- **Process Creation**: `switchToEditor()` creates new processes which have overhead

#### Optimization Strategies

1. **Cache Context**: Store context information when possible to avoid repeated extraction
2. **Batch Operations**: Group multiple configuration updates together
3. **Async Detection**: Perform path detection asynchronously if possible
4. **Lazy Loading**: Initialize expensive operations only when needed

## Migration Guidance

### Breaking Changes to Watch For

When upgrading the plugin or IntelliJ Platform:

1. **API Deprecations**: Monitor IntelliJ Platform API deprecation warnings
2. **Path Format Changes**: Check for changes in editor executable path formats
3. **Command Line Arguments**: Verify that editor command line argument formats haven't changed
4. **Platform Integration**: Ensure continued compatibility with new platform features

### Version-Specific Considerations

#### IntelliJ Platform 2023.x

- Stable API surface
- No breaking changes expected
- Continue using current patterns

#### IntelliJ Platform 2024.x+

- Monitor for new editor support requirements
- Check for improved context extraction APIs
- Review logging framework updates

### Migration Checklist

1. **Test All Editor Types**: Verify all nine editor types work correctly
2. **Validate Context Extraction**: Ensure `getEditorContext()` returns expected values
3. **Check Path Detection**: Verify auto-detection works for all platforms
4. **Review Error Handling**: Ensure error messages are user-friendly
5. **Performance Testing**: Validate performance hasn't regressed

### Future-Proofing

To prepare for future changes:

1. **Use Interfaces**: Rely on documented interfaces rather than implementation details
2. **Graceful Degradation**: Handle missing functionality gracefully
3. **Configuration Flexibility**: Allow users to override default behaviors
4. **Extensible Design**: Design for adding new editor types easily