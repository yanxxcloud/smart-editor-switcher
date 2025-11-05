# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Commands

- Build the plugin (zip under `build/distributions/`):
  ```bash
  ./gradlew buildPlugin
  ```
- Run the IDE sandbox with the plugin installed:
  ```bash
  ./gradlew runIde
  ```
- Compile and run tests (JUnit; none present by default):
  ```bash
  ./gradlew test
  ```
- Run a single test:
  ```bash
  ./gradlew test --tests "fully.qualified.ClassNameTest"
  ```
- Verify plugin against IDEs (IntelliJ Gradle plugin):
  ```bash
  ./gradlew verifyPlugin
  ```
- Sign and publish (requires environment variables):
  ```bash
  CERTIFICATE_CHAIN=... PRIVATE_KEY=... PRIVATE_KEY_PASSWORD=... ./gradlew signPlugin
  PUBLISH_TOKEN=... ./gradlew publishPlugin
  ```
- General Gradle hygiene:
  ```bash
  ./gradlew clean build
  ```
- Linting: No dedicated linter configured (ktlint/detekt not present); rely on compiler checks via `./gradlew build`.

Notes:
- Uses Java/Kotlin on JVM 17 (Gradle targets Java 17). Ensure JDK 17 is available.
- IntelliJ Platform version: 2024.2.4 (type IC). Compatible build range: 232 – 252.*

## High-level architecture

This is a JetBrains IntelliJ Platform plugin: “Smart Editor Switcher”. It switches the current file/project from a JetBrains IDE to external editors (VS Code, Cursor, Zed, Kiro, Sublime), preserving cursor line/column and context.

Key components and flow:

- Manifest and wiring (`src/main/resources/META-INF/plugin.xml`)
  - Registers:
    - `applicationService`: `io.yanxxcloud.editorswitcher.services.EditorSwitcherService`
    - Settings page (`applicationConfigurable`): `...settings.SmartEditorSwitcherConfigurable`
    - Status bar widget factory: `...ui.EditorSwitcherStatusBarWidgetFactory`
    - Action group in Tools menu and context menus with actions per editor and keyboard shortcuts (e.g., Ctrl+Alt+V/C/Z/K/S)
  - Declares IDE compatibility (`since-build`, `until-build`).

- Core service (`src/main/kotlin/io/yanxxcloud/editorswitcher/services/EditorSwitcherService.kt`)
  - PersistentStateComponent storing executable paths for supported editors; exposes `detectEditorPaths()` to auto-fill common locations per OS.
  - `switchToEditor(editorType, filePath, projectPath, line, column)`: builds an editor-specific command (e.g., VS Code/Cursor `--goto file:line:column`, Sublime `file:line:column`, Vim via Terminal `vim +line file`) and launches via `ProcessBuilder`.
  - `EditorType` enum enumerates supported editors.

- Actions (`src/main/kotlin/io/yanxxcloud/editorswitcher/actions/*`)
  - One action per editor (e.g., `SwitchToVSCodeAction`, `SwitchToCursorAction`, ...):
    - Collects context via `EditorUtils.getEditorContext(e)` (project base path, file path, caret line/column, 1-based).
    - Ensures the target editor path is configured (tries auto-detect), then delegates to `EditorSwitcherService.switchToEditor(...)`.
    - Provides simple user feedback via `Messages` dialogs.
  - `com.example.editorswitcher.actions.EditorSwitcherConfigAction`: opens the plugin’s settings page.

- Utilities (`src/main/kotlin/io/yanxxcloud/editorswitcher/utils/EditorUtils.kt`)
  - `getEditorContext(AnActionEvent)`: extracts `projectPath`, `filePath`, and caret `line`/`column` (1-based) for downstream use.

- UI integrations
  - Settings UI (`...settings.SmartEditorSwitcherSettingsComponent` + `SmartEditorSwitcherConfigurable`):
    - Built with IntelliJ UI DSL; provides file-chooser fields for editor executables and an “Auto-detect” button that populates from `EditorSwitcherService.detectEditorPaths()`.
  - Status bar (`...ui.EditorSwitcherStatusBarWidget` + factory):
    - Shows a clickable “Editor Switch” entry that opens a popup menu (`EditorSwitcherActionGroup`) listing target editors.

- Gradle/Build (`build.gradle.kts`, `gradle.properties`)
  - Kotlin `2.0.21`, Gradle IntelliJ plugin `1.17.4`, platform `2024.2.4` (IC), Java 17 toolchain.
  - Optional signing/publishing tasks are wired to read credentials from environment variables.

Artifacts and paths:
- Built plugin ZIP: `build/distributions/*.zip`
- IDE sandbox runs via `runIde` (no separate config required).
