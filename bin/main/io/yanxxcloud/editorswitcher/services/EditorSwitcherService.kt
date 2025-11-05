package io.yanxxcloud.editorswitcher.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.util.xmlb.XmlSerializerUtil
import java.io.File

@State(
    name = "io.yanxxcloud.editorswitcher.services.EditorSwitcherService",
    storages = [Storage("SmartEditorSwitcher.xml")]
)
class EditorSwitcherService : PersistentStateComponent<EditorSwitcherService> {
    
    var kiroPath: String = ""
    var vsCodePath: String = ""
    var sublimePath: String = ""
    var atomPath: String = ""
    var notepadPlusPlusPath: String = ""
    var vimPath: String = ""
    var emacsPath: String = ""
    var cursorPath: String = ""
    var zedPath: String = ""
    
    companion object {
        fun getInstance(): EditorSwitcherService = ApplicationManager.getApplication().getService(EditorSwitcherService::class.java)
    }

    override fun getState(): EditorSwitcherService = this

    override fun loadState(state: EditorSwitcherService) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun switchToEditor(editorType: EditorType, filePath: String?, projectPath: String?, line: Int = 1, column: Int = 1) {
        val editorPath = when (editorType) {
            EditorType.KIRO -> kiroPath
            EditorType.VSCODE -> vsCodePath
            EditorType.SUBLIME -> sublimePath
            EditorType.ATOM -> atomPath
            EditorType.NOTEPADPP -> notepadPlusPlusPath
            EditorType.VIM -> vimPath
            EditorType.EMACS -> emacsPath
            EditorType.CURSOR -> cursorPath
            EditorType.ZED -> zedPath
        }

        if (editorPath.isEmpty()) {
            thisLogger().warn("Editor path not configured for $editorType")
            return
        }

        try {
            val command = buildCommand(editorType, editorPath, filePath, projectPath, line, column)
            val processBuilder = ProcessBuilder(command)
            processBuilder.start()
            thisLogger().info("Switched to $editorType at line $line, column $column with command: ${command.joinToString(" ")}")
        } catch (e: Exception) {
            thisLogger().error("Failed to switch to $editorType", e)
        }
    }

    private fun buildCommand(editorType: EditorType, editorPath: String, filePath: String?, projectPath: String?, line: Int = 1, column: Int = 1): List<String> {
        return when (editorType) {
            EditorType.KIRO -> {
                val command = mutableListOf(editorPath)
                projectPath?.let { command.add(it) }
                filePath?.let { 
                    command.add(it)
                    // Kiro 支持 --line 和 --column 参数
                    command.addAll(listOf("--line", line.toString(), "--column", column.toString()))
                }
                command
            }
            EditorType.VSCODE -> {
                val command = mutableListOf(editorPath)
                if (projectPath != null) {
                    command.add(projectPath)
                    filePath?.let { command.addAll(listOf("--goto", "$it:$line:$column")) }
                } else {
                    filePath?.let { command.addAll(listOf("--goto", "$it:$line:$column")) }
                }
                command
            }
            EditorType.SUBLIME -> {
                val command = mutableListOf(editorPath)
                projectPath?.let { command.add(it) }
                filePath?.let { command.add("$it:$line:$column") }
                command
            }
            EditorType.ATOM -> {
                val command = mutableListOf(editorPath)
                projectPath?.let { command.add(it) }
                filePath?.let { command.add("$it:$line:$column") }
                command
            }
            EditorType.NOTEPADPP -> {
                val command = mutableListOf(editorPath)
                filePath?.let { command.addAll(listOf("-n$line", "-c$column", it)) }
                command
            }
            EditorType.VIM -> {
                val command = mutableListOf("open", "-a", "Terminal")
                filePath?.let { 
                    command.addAll(listOf("--args", "vim", "+$line", it))
                } ?: run {
                    projectPath?.let { command.addAll(listOf("--args", "vim", it)) }
                }
                command
            }
            EditorType.EMACS -> {
                val command = mutableListOf(editorPath)
                filePath?.let { 
                    command.add("+$line:$column")
                    command.add(it)
                }
                command
            }
            EditorType.CURSOR -> {
                val command = mutableListOf(editorPath)
                if (projectPath != null) {
                    command.add(projectPath)
                    filePath?.let { command.addAll(listOf("--goto", "$it:$line:$column")) }
                } else {
                    filePath?.let { command.addAll(listOf("--goto", "$it:$line:$column")) }
                }
                command
            }
            EditorType.ZED -> {
                val command = mutableListOf(editorPath)
                projectPath?.let { command.add(it) }
                filePath?.let { command.add("$it:$line:$column") }
                command
            }
        }
    }

    fun detectEditorPaths() {
        // 自动检测编辑器路径
        if (kiroPath.isEmpty()) {
            kiroPath = detectKiroPath()
        }
        if (vsCodePath.isEmpty()) {
            vsCodePath = detectVSCodePath()
        }
        if (sublimePath.isEmpty()) {
            sublimePath = detectSublimePath()
        }
        if (atomPath.isEmpty()) {
            atomPath = detectAtomPath()
        }
        if (notepadPlusPlusPath.isEmpty()) {
            notepadPlusPlusPath = detectNotepadPlusPlusPath()
        }
        if (vimPath.isEmpty()) {
            vimPath = detectVimPath()
        }
        if (emacsPath.isEmpty()) {
            emacsPath = detectEmacsPath()
        }
        if (cursorPath.isEmpty()) {
            cursorPath = detectCursorPath()
        }
        if (zedPath.isEmpty()) {
            zedPath = detectZedPath()
        }
    }

    private fun detectKiroPath(): String {
        val possiblePaths = listOf(
            "/Applications/Kiro.app/Contents/MacOS/Kiro",
            "/usr/local/bin/kiro",
            "/opt/kiro/bin/kiro",
            "C:\\Program Files\\Kiro\\Kiro.exe",
            "C:\\Program Files (x86)\\Kiro\\Kiro.exe"
        )
        return possiblePaths.firstOrNull { File(it).exists() } ?: ""
    }

    private fun detectVSCodePath(): String {
        val possiblePaths = listOf(
            "/Applications/Visual Studio Code.app/Contents/Resources/app/bin/code",
            "/usr/local/bin/code",
            "/opt/visual-studio-code/bin/code",
            "C:\\Program Files\\Microsoft VS Code\\bin\\code.cmd",
            "C:\\Program Files (x86)\\Microsoft VS Code\\bin\\code.cmd"
        )
        return possiblePaths.firstOrNull { File(it).exists() } ?: ""
    }

    private fun detectSublimePath(): String {
        val possiblePaths = listOf(
            "/Applications/Sublime Text.app/Contents/SharedSupport/bin/subl",
            "/usr/local/bin/subl",
            "/opt/sublime_text/sublime_text",
            "C:\\Program Files\\Sublime Text\\subl.exe",
            "C:\\Program Files (x86)\\Sublime Text\\subl.exe"
        )
        return possiblePaths.firstOrNull { File(it).exists() } ?: ""
    }

    private fun detectAtomPath(): String {
        val possiblePaths = listOf(
            "/Applications/Atom.app/Contents/Resources/app/atom.sh",
            "/usr/local/bin/atom",
            "/opt/atom/atom",
            "C:\\Program Files\\Atom\\atom.exe",
            "C:\\Program Files (x86)\\Atom\\atom.exe"
        )
        return possiblePaths.firstOrNull { File(it).exists() } ?: ""
    }

    private fun detectNotepadPlusPlusPath(): String {
        val possiblePaths = listOf(
            "C:\\Program Files\\Notepad++\\notepad++.exe",
            "C:\\Program Files (x86)\\Notepad++\\notepad++.exe"
        )
        return possiblePaths.firstOrNull { File(it).exists() } ?: ""
    }

    private fun detectVimPath(): String {
        val possiblePaths = listOf(
            "/usr/bin/vim",
            "/usr/local/bin/vim",
            "/opt/homebrew/bin/vim",
            "C:\\Program Files\\Vim\\vim90\\vim.exe"
        )
        return possiblePaths.firstOrNull { File(it).exists() } ?: ""
    }

    private fun detectEmacsPath(): String {
        val possiblePaths = listOf(
            "/Applications/Emacs.app/Contents/MacOS/Emacs",
            "/usr/local/bin/emacs",
            "/opt/homebrew/bin/emacs",
            "C:\\Program Files\\Emacs\\bin\\emacs.exe"
        )
        return possiblePaths.firstOrNull { File(it).exists() } ?: ""
    }

    private fun detectCursorPath(): String {
        val possiblePaths = listOf(
            "/Applications/Cursor.app/Contents/Resources/app/bin/cursor",
            "/usr/local/bin/cursor",
            "/opt/cursor/cursor",
            "C:\\Program Files\\Cursor\\Cursor.exe",
            "C:\\Users\\%USERNAME%\\AppData\\Local\\Programs\\cursor\\Cursor.exe"
        )
        return possiblePaths.firstOrNull { File(it).exists() } ?: ""
    }

    private fun detectZedPath(): String {
        val possiblePaths = listOf(
            "/Applications/Zed.app/Contents/MacOS/zed",
            "/usr/local/bin/zed",
            "/opt/zed/zed",
            "C:\\Program Files\\Zed\\zed.exe",
            "C:\\Users\\%USERNAME%\\AppData\\Local\\Programs\\Zed\\zed.exe"
        )
        return possiblePaths.firstOrNull { File(it).exists() } ?: ""
    }
}

enum class EditorType {
    KIRO, VSCODE, SUBLIME, ATOM, NOTEPADPP, VIM, EMACS, CURSOR, ZED
}