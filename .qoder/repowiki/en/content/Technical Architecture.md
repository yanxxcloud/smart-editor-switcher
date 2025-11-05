# Smart Editor Switcher Plugin - Technical Architecture

<cite>
**Referenced Files in This Document**
- [plugin.xml](file://src/main/resources/META-INF/plugin.xml)
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt)
- [SmartEditorSwitcherConfigurable.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherConfigurable.kt)
- [EditorSwitcherStatusBarWidget.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidget.kt)
- [EditorSwitcherStatusBarWidgetFactory.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidgetFactory.kt)
- [EditorSwitcherActionGroup.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherActionGroup.kt)
- [SwitchToVSCodeAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToVSCodeAction.kt)
- [SwitchToCursorAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToCursorAction.kt)
- [EditorUtils.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/utils/EditorUtils.kt)
- [SmartEditorSwitcherSettingsComponent.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherSettingsComponent.kt)
- [build.gradle.kts](file://build.gradle.kts)
- [gradle.properties](file://gradle.properties)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Architecture Overview](#architecture-overview)
3. [Core Components](#core-components)
4. [Design Patterns Implementation](#design-patterns-implementation)
5. [Component Interactions](#component-interactions)
6. [Integration Points](#integration-points)
7. [Technical Decisions](#technical-decisions)
8. [Cross-Cutting Concerns](#cross-cutting-concerns)
9. [Infrastructure Requirements](#infrastructure-requirements)
10. [Deployment Topology](#deployment-topology)
11. [Conclusion](#conclusion)

## Introduction

The Smart Editor Switcher plugin is a sophisticated IntelliJ Platform plugin that enables seamless transitions between JetBrains IDEs and external code editors. Built using Kotlin and targeting Java 17, the plugin demonstrates advanced architectural patterns while maintaining compatibility across IntelliJ Platform builds 232-252.*.

The plugin architecture follows IntelliJ Platform conventions, implementing a layered approach with clear separation of concerns across service, action, UI, and configuration layers. The design emphasizes extensibility, maintainability, and user experience through intelligent cursor positioning and project context preservation.

## Architecture Overview

The Smart Editor Switcher plugin follows a modular architecture pattern that aligns with IntelliJ Platform best practices. The system is organized into distinct layers that handle different aspects of editor switching functionality.

```mermaid
graph TB
subgraph "IntelliJ Platform Layer"
PluginXML["plugin.xml<br/>Extension Points"]
Actions["Action System"]
Services["Application Services"]
UI["Status Bar & Widgets"]
end
subgraph "Business Logic Layer"
ServiceLayer["EditorSwitcherService<br/>(Singleton Pattern)"]
Utils["EditorUtils<br/>(Utility Functions)"]
end
subgraph "Presentation Layer"
Settings["SmartEditorSwitcherConfigurable<br/>(Settings UI)"]
StatusBar["EditorSwitcherStatusBarWidget<br/>(Status Bar Integration)"]
ActionGroup["EditorSwitcherActionGroup<br/>(Action Organization)"]
end
subgraph "External Editors"
VSCode["VS Code"]
Cursor["Cursor"]
Zed["Zed"]
Kiro["Kiro"]
Sublime["Sublime Text"]
Others["Other Editors"]
end
PluginXML --> Actions
PluginXML --> Services
PluginXML --> UI
Actions --> ServiceLayer
Services --> ServiceLayer
UI --> ServiceLayer
ServiceLayer --> Utils
Settings --> ServiceLayer
StatusBar --> ActionGroup
ActionGroup --> Actions
ServiceLayer --> VSCode
ServiceLayer --> Cursor
ServiceLayer --> Zed
ServiceLayer --> Kiro
ServiceLayer --> Sublime
ServiceLayer --> Others
```

**Diagram sources**
- [plugin.xml](file://src/main/resources/META-INF/plugin.xml#L30-L60)
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L10-L30)
- [EditorSwitcherStatusBarWidget.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidget.kt#L10-L25)

**Section sources**
- [plugin.xml](file://src/main/resources/META-INF/plugin.xml#L1-L117)
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L1-L268)

## Core Components

### Service Layer - EditorSwitcherService

The service layer represents the core business logic of the plugin, implementing the Singleton pattern through IntelliJ's application service mechanism. This central service manages editor configurations, executes switching operations, and maintains persistent state.

```mermaid
classDiagram
class EditorSwitcherService {
+String kiroPath
+String vsCodePath
+String sublimePath
+String atomPath
+String notepadPlusPlusPath
+String vimPath
+String emacsPath
+String cursorPath
+String zedPath
+getInstance() EditorSwitcherService
+switchToEditor(type, filePath, projectPath, line, column) void
+detectEditorPaths() void
-buildCommand(type, path, filePath, projectPath, line, column) String[]
-detectKiroPath() String
-detectVSCodePath() String
-detectSublimePath() String
-detectAtomPath() String
-detectNotepadPlusPlusPath() String
-detectVimPath() String
-detectEmacsPath() String
-detectCursorPath() String
-detectZedPath() String
}
class EditorType {
<<enumeration>>
KIRO
VSCODE
SUBLIME
ATOM
NOTEPADPP
VIM
EMACS
CURSOR
ZED
}
EditorSwitcherService --> EditorType : uses
```

**Diagram sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L10-L50)
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L260-L268)

The service implements several key responsibilities:
- **Configuration Management**: Maintains editor executable paths with automatic detection capabilities
- **Command Construction**: Builds platform-specific command-line arguments for each editor type
- **Process Execution**: Launches external editors with proper context preservation
- **State Persistence**: Implements IntelliJ's PersistentStateComponent interface for configuration storage

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L1-L268)

### Action System - SwitchTo*Actions

The action system provides multiple entry points for editor switching, each implementing the Command pattern to encapsulate switching logic. These actions serve as discrete commands that can be triggered through various UI mechanisms.

```mermaid
classDiagram
class AnAction {
<<abstract>>
+actionPerformed(event) void
+update(event) void
}
class SwitchToVSCodeAction {
+actionPerformed(event) void
+update(event) void
}
class SwitchToCursorAction {
+actionPerformed(event) void
+update(event) void
}
class SwitchToZedAction {
+actionPerformed(event) void
+update(event) void
}
class SwitchToKiroAction {
+actionPerformed(event) void
+update(event) void
}
class SwitchToSublimeAction {
+actionPerformed(event) void
+update(event) void
}
AnAction <|-- SwitchToVSCodeAction
AnAction <|-- SwitchToCursorAction
AnAction <|-- SwitchToZedAction
AnAction <|-- SwitchToKiroAction
AnAction <|-- SwitchToSublimeAction
```

**Diagram sources**
- [SwitchToVSCodeAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToVSCodeAction.kt#L10-L20)
- [SwitchToCursorAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToCursorAction.kt#L10-L20)

Each action follows a consistent pattern:
1. **Context Extraction**: Uses EditorUtils to gather current file and cursor position
2. **Validation**: Checks if the target editor is configured
3. **Execution**: Delegates to the service layer for actual switching
4. **Feedback**: Provides user notifications about the operation status

**Section sources**
- [SwitchToVSCodeAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToVSCodeAction.kt#L1-L46)
- [SwitchToCursorAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToCursorAction.kt#L1-L46)

### Settings Integration - SmartEditorSwitcherConfigurable

The settings component provides a comprehensive configuration interface for managing editor paths and plugin preferences. It implements IntelliJ's Configurable interface to integrate seamlessly with the IDE's settings system.

```mermaid
classDiagram
class Configurable {
<<interface>>
+getDisplayName() String
+createComponent() JComponent
+isModified() Boolean
+apply() void
+reset() void
+disposeUIResources() void
}
class SmartEditorSwitcherConfigurable {
-settingsComponent SmartEditorSwitcherSettingsComponent
+getDisplayName() String
+getPreferredFocusedComponent() JComponent
+createComponent() JComponent
+isModified() Boolean
+apply() void
+reset() void
+disposeUIResources() void
}
class SmartEditorSwitcherSettingsComponent {
+panel JPanel
+vsCodePathText String
+cursorPathText String
+zedPathText String
+kiroPathText String
+sublimePathText String
+preferredFocusedComponent JComponent
}
Configurable <|-- SmartEditorSwitcherConfigurable
SmartEditorSwitcherConfigurable --> SmartEditorSwitcherSettingsComponent : creates
```

**Diagram sources**
- [SmartEditorSwitcherConfigurable.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherConfigurable.kt#L7-L20)
- [SmartEditorSwitcherSettingsComponent.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherSettingsComponent.kt#L10-L30)

**Section sources**
- [SmartEditorSwitcherConfigurable.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherConfigurable.kt#L1-L56)
- [SmartEditorSwitcherSettingsComponent.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherSettingsComponent.kt#L1-L165)

### UI Integration - Status Bar Widget

The status bar widget provides immediate access to editor switching functionality through the IntelliJ IDE's status bar. It implements a factory pattern for widget creation and integrates with IntelliJ's status bar system.

```mermaid
classDiagram
class StatusBarWidgetFactory {
<<interface>>
+getId() String
+getDisplayName() String
+isAvailable(project) Boolean
+createWidget(project) StatusBarWidget
+disposeWidget(widget) void
+canBeEnabledOn(statusBar) Boolean
}
class EditorSwitcherStatusBarWidgetFactory {
+getId() String
+getDisplayName() String
+isAvailable(project) Boolean
+createWidget(project) StatusBarWidget
+disposeWidget(widget) void
+canBeEnabledOn(statusBar) Boolean
}
class EditorSwitcherStatusBarWidget {
+ID() String
+getPresentation() WidgetPresentation
+getTooltipText() String
+getSelectedValue() String
+getPopupStep() ListPopup
+getClickConsumer() Consumer
+install(statusBar) void
+dispose() void
-createPopup() ListPopup
}
class EditorBasedWidget {
<<abstract>>
+getProject() Project
}
StatusBarWidgetFactory <|-- EditorSwitcherStatusBarWidgetFactory
EditorBasedWidget <|-- EditorSwitcherStatusBarWidget
EditorSwitcherStatusBarWidgetFactory --> EditorSwitcherStatusBarWidget : creates
```

**Diagram sources**
- [EditorSwitcherStatusBarWidgetFactory.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidgetFactory.kt#L8-L20)
- [EditorSwitcherStatusBarWidget.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidget.kt#L10-L30)

**Section sources**
- [EditorSwitcherStatusBarWidgetFactory.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidgetFactory.kt#L1-L25)
- [EditorSwitcherStatusBarWidget.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidget.kt#L1-L56)

## Design Patterns Implementation

### Singleton Pattern via IntelliJ Application Service

The EditorSwitcherService implements the Singleton pattern through IntelliJ's application service mechanism, ensuring a single instance across the IDE session while leveraging IntelliJ's lifecycle management.

```mermaid
sequenceDiagram
participant Client as "Action/Widget"
participant Service as "EditorSwitcherService"
participant App as "ApplicationManager"
participant Storage as "PersistentStateComponent"
Client->>App : getService(EditorSwitcherService : : class.java)
App->>Service : getInstance()
Service->>Storage : getState()
Storage-->>Service : restored state
Service-->>Client : singleton instance
Note over Client,Storage : Service instance reused for subsequent requests
```

**Diagram sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L25-L30)

### Command Pattern for Actions

Each SwitchTo*Action implements the Command pattern, encapsulating the switching operation as a discrete command that can be executed through various triggers.

```mermaid
sequenceDiagram
participant User as "User"
participant Action as "SwitchToVSCodeAction"
participant Utils as "EditorUtils"
participant Service as "EditorSwitcherService"
participant OS as "Operating System"
User->>Action : actionPerformed()
Action->>Utils : getEditorContext(event)
Utils-->>Action : EditorContext
Action->>Service : switchToEditor(VSCODE, ...)
Service->>Service : buildCommand(VSCODE, ...)
Service->>OS : ProcessBuilder.start()
OS-->>Service : Process
Service-->>Action : success
Action->>User : showInfoMessage()
```

**Diagram sources**
- [SwitchToVSCodeAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToVSCodeAction.kt#L10-L40)
- [EditorUtils.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/utils/EditorUtils.kt#L15-L45)

### Factory Pattern for UI Components

The status bar widget factory implements the Factory pattern, creating appropriate widget instances based on the project context and availability requirements.

```mermaid
flowchart TD
Start([Factory Creation]) --> CheckAvailability["Check isAvailable(project)"]
CheckAvailability --> Available{"Available?"}
Available --> |Yes| CreateWidget["createWidget(project)"]
Available --> |No| ReturnNull["Return null"]
CreateWidget --> NewInstance["New EditorSwitcherStatusBarWidget(project)"]
NewInstance --> SetupWidget["Setup widget presentation"]
SetupWidget --> ReturnWidget["Return widget instance"]
ReturnNull --> End([End])
ReturnWidget --> End
```

**Diagram sources**
- [EditorSwitcherStatusBarWidgetFactory.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidgetFactory.kt#L15-L25)

### Dependency Injection through getInstance()

The plugin extensively uses IntelliJ's dependency injection mechanism through the getInstance() pattern, allowing loose coupling between components while maintaining access to shared services.

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L25-L30)
- [SwitchToVSCodeAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToVSCodeAction.kt#L15-L20)

## Component Interactions

### User Input Propagation Flow

The system handles user input through multiple pathways, each following a consistent data flow pattern that ensures proper context preservation and error handling.

```mermaid
sequenceDiagram
participant StatusBar as "Status Bar Widget"
participant ActionGroup as "EditorSwitcherActionGroup"
participant Action as "Specific Action"
participant Utils as "EditorUtils"
participant Service as "EditorSwitcherService"
participant External as "External Editor"
Note over StatusBar,External : Status Bar Click Flow
StatusBar->>ActionGroup : createPopup()
ActionGroup->>ActionGroup : getChildren()
ActionGroup-->>StatusBar : Action array
StatusBar->>Action : actionPerformed()
Note over Utils,External : Action Execution Flow
Action->>Utils : getEditorContext(event)
Utils->>Utils : extract project, file, cursor position
Utils-->>Action : EditorContext
Action->>Service : switchToEditor(type, ...)
Service->>Service : validate editor path
Service->>Service : buildCommand(type, ...)
Service->>External : launch process
External-->>Service : process started
Service-->>Action : success
Action->>Action : show notification
```

**Diagram sources**
- [EditorSwitcherStatusBarWidget.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidget.kt#L30-L50)
- [EditorSwitcherActionGroup.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherActionGroup.kt#L10-L35)
- [SwitchToVSCodeAction.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/actions/SwitchToVSCodeAction.kt#L10-L45)

### Configuration Synchronization

The settings system maintains synchronization between the UI component and the service layer, ensuring configuration changes are immediately reflected in switching operations.

```mermaid
sequenceDiagram
participant SettingsUI as "Settings Component"
participant Configurable as "Configurable"
participant Service as "EditorSwitcherService"
SettingsUI->>Configurable : isModified()
Configurable->>Service : compare current vs saved
Service-->>Configurable : modification status
Configurable-->>SettingsUI : modified state
SettingsUI->>Configurable : apply()
Configurable->>Service : update paths
Service->>Service : persist state
Service-->>Configurable : apply successful
Configurable-->>SettingsUI : apply complete
SettingsUI->>Configurable : reset()
Configurable->>Service : get current state
Service-->>Configurable : current values
Configurable-->>SettingsUI : restore values
```

**Diagram sources**
- [SmartEditorSwitcherConfigurable.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherConfigurable.kt#L20-L50)

**Section sources**
- [EditorSwitcherStatusBarWidget.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherStatusBarWidget.kt#L30-L56)
- [EditorSwitcherActionGroup.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/ui/EditorSwitcherActionGroup.kt#L1-L36)
- [SmartEditorSwitcherConfigurable.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/settings/SmartEditorSwitcherConfigurable.kt#L1-L56)

## Integration Points

### plugin.xml Extension Points

The plugin.xml defines all integration points with the IntelliJ Platform, establishing the plugin's presence and capabilities within the IDE ecosystem.

| Extension Point | Implementation | Purpose |
|----------------|---------------|---------|
| `applicationConfigurable` | `SmartEditorSwitcherConfigurable` | Settings UI integration |
| `applicationService` | `EditorSwitcherService` | Core service registration |
| `statusBarWidgetFactory` | `EditorSwitcherStatusBarWidgetFactory` | Status bar integration |
| `action` | Various SwitchTo*Action classes | Keyboard shortcut and menu integration |

### Action Group Organization

The plugin organizes actions into logical groups for menu presentation and keyboard shortcut assignment:

```mermaid
graph LR
subgraph "Main Menu Group"
VSCode["Switch to VS Code<br/>Ctrl+Alt+V"]
Cursor["Switch to Cursor<br/>Ctrl+Alt+C"]
Zed["Switch to Zed<br/>Ctrl+Alt+Z"]
Kiro["Switch to Kiro<br/>Ctrl+Alt+K"]
Sublime["Switch to Sublime Text<br/>Ctrl+Alt+S"]
end
subgraph "Context Menu Group"
ContextVSCode["VS Code"]
ContextCursor["Cursor"]
ContextZed["Zed"]
ContextKiro["Kiro"]
ContextSublime["Sublime Text"]
end
VSCode --> ContextVSCode
Cursor --> ContextCursor
Zed --> ContextZed
Kiro --> ContextKiro
Sublime --> ContextSublime
```

**Diagram sources**
- [plugin.xml](file://src/main/resources/META-INF/plugin.xml#L60-L100)

**Section sources**
- [plugin.xml](file://src/main/resources/META-INF/plugin.xml#L30-L117)

## Technical Decisions

### Kotlin Development Choice

The plugin is developed entirely in Kotlin, leveraging modern language features and seamless interoperability with Java-based IntelliJ Platform APIs. This decision provides:

- **Type Safety**: Compile-time null safety and type inference
- **Concise Syntax**: Reduced boilerplate compared to Java
- **Functional Programming**: First-class support for functional constructs
- **IDE Integration**: Excellent support in IntelliJ IDEA itself

### Java 17 Targeting

The plugin targets Java 17 (JDK 17) for several strategic reasons:

- **Long-term Support**: Java 17 is an LTS version with extended support
- **Performance**: Modern JVM optimizations and garbage collection improvements
- **Security**: Active security updates and bug fixes
- **Compatibility**: Broad platform support across operating systems

### IntelliJ Platform Compatibility

The plugin supports IntelliJ Platform builds 232-252.*, representing a balanced approach:

- **Modern Features**: Access to recent IntelliJ Platform enhancements
- **Broad Compatibility**: Coverage of multiple major IDE releases
- **Future-proofing**: Preparation for upcoming platform versions
- **Stability**: Well-tested compatibility range

**Section sources**
- [build.gradle.kts](file://build.gradle.kts#L1-L48)
- [gradle.properties](file://gradle.properties#L1-L34)

## Cross-Cutting Concerns

### Configuration Persistence

The plugin implements robust configuration persistence through IntelliJ's PersistentStateComponent interface, ensuring settings survive IDE restarts and provide reliable user experience.

```mermaid
flowchart TD
Start([Configuration Change]) --> Modify["Modify Service State"]
Modify --> Save["saveState()"]
Save --> Serialize["XmlSerializerUtil.copyBean()"]
Serialize --> FileSystem["Write to SmartEditorSwitcher.xml"]
FileSystem --> Reload["Next Session Load"]
Reload --> Restore["loadState()"]
Restore --> Deserialize["XmlSerializerUtil.copyBean()"]
Deserialize --> Ready["Service Ready"]
```

**Diagram sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L30-L40)

### Error Logging and Diagnostics

The plugin implements comprehensive error logging using IntelliJ's diagnostic framework, providing visibility into switching operations and troubleshooting capabilities.

### Cross-Platform Compatibility

The plugin handles cross-platform differences through:

- **Path Normalization**: Platform-appropriate path handling for Windows, macOS, and Linux
- **Executable Detection**: Automatic detection of editor installations across platforms
- **Command Line Arguments**: Platform-specific argument construction for each editor

**Section sources**
- [EditorSwitcherService.kt](file://src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt#L45-L50)

## Infrastructure Requirements

### Development Environment

The plugin development requires:

- **IntelliJ IDEA**: Ultimate or Community edition with Kotlin plugin
- **JDK 17**: Java Development Kit 17 or later
- **Gradle**: Version 8.14 or compatible
- **Kotlin**: Version 2.0.21 or compatible

### Build System Configuration

The Gradle build system provides:

- **Multi-platform Support**: Automatic handling of platform-specific compilation
- **Plugin Signing**: Automated signing for distribution
- **Version Management**: Semantic versioning with build number compatibility
- **Testing Integration**: Built-in testing framework support

### Runtime Requirements

The plugin operates within IntelliJ Platform runtime with:

- **Memory**: Minimal memory footprint, typical for IntelliJ plugins
- **Disk Space**: Small installation footprint with configuration persistence
- **Network**: No network requirements for basic functionality
- **Permissions**: Standard file system access for editor detection

**Section sources**
- [build.gradle.kts](file://build.gradle.kts#L10-L30)
- [gradle.properties](file://gradle.properties#L10-L20)

## Deployment Topology

### JetBrains Marketplace Distribution

The plugin is distributed through JetBrains Marketplace with automated deployment pipeline:

```mermaid
graph TB
subgraph "Development"
DevRepo["GitHub Repository"]
LocalBuild["Local Development"]
end
subgraph "CI/CD Pipeline"
AutoBuild["Automated Build"]
SignPlugin["Plugin Signing"]
TestPlugin["Plugin Testing"]
end
subgraph "Distribution"
Marketplace["JetBrains Marketplace"]
DirectDownload["Direct Download"]
end
subgraph "User Installation"
IDEInstall["IDE Installation"]
AutoUpdate["Automatic Updates"]
end
DevRepo --> AutoBuild
LocalBuild --> AutoBuild
AutoBuild --> SignPlugin
SignPlugin --> TestPlugin
TestPlugin --> Marketplace
TestPlugin --> DirectDownload
Marketplace --> IDEInstall
DirectDownload --> IDEInstall
IDEInstall --> AutoUpdate
```

### Release Management

The plugin follows semantic versioning with automated release management:

- **Version Control**: Git-based version tracking with annotated tags
- **Release Notes**: Comprehensive changelog generation
- **Automated Publishing**: CI/CD pipeline for marketplace submission
- **Backward Compatibility**: Careful version compatibility matrix maintenance

**Section sources**
- [build.gradle.kts](file://build.gradle.kts#L35-L45)
- [gradle.properties](file://gradle.properties#L3-L5)

## Conclusion

The Smart Editor Switcher plugin demonstrates sophisticated architectural design principles applied to IntelliJ Platform plugin development. Through careful implementation of design patterns, adherence to platform conventions, and consideration of cross-cutting concerns, the plugin provides a robust and extensible solution for editor switching functionality.

Key architectural strengths include:

- **Modular Design**: Clear separation of concerns across service, action, UI, and configuration layers
- **Pattern Implementation**: Effective use of Singleton, Command, Factory, and Dependency Injection patterns
- **Platform Integration**: Seamless integration with IntelliJ Platform extension points and APIs
- **User Experience**: Consistent and intuitive interaction patterns across multiple access methods
- **Maintainability**: Clean code structure and comprehensive error handling

The plugin serves as an excellent example of how to leverage IntelliJ Platform capabilities while maintaining code quality, performance, and user satisfaction. Its architecture supports future enhancements and adaptations to evolving platform requirements while preserving backward compatibility and user experience.