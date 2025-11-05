package io.yanxxcloud.editorswitcher.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.util.xmlb.XmlSerializerUtil
import io.yanxxcloud.editorswitcher.model.CustomEditorConfig
import io.yanxxcloud.editorswitcher.model.ValidationResult
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
    
    // Custom editors configuration
    var customEditors: MutableList<CustomEditorConfig> = mutableListOf()
    
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
    
    // ========== Custom Editor Management ==========
    
    /**
     * Add a new custom editor configuration
     */
    fun addCustomEditor(config: CustomEditorConfig): Boolean {
        if (customEditors.any { it.editorId == config.editorId }) {
            thisLogger().warn("Custom editor with ID ${config.editorId} already exists")
            return false
        }
        if (!config.isValid()) {
            thisLogger().warn("Invalid custom editor configuration")
            return false
        }
        customEditors.add(config)
        thisLogger().info("Added custom editor: ${config.displayName}")
        return true
    }
    
    /**
     * Update existing custom editor configuration
     */
    fun updateCustomEditor(editorId: String, config: CustomEditorConfig): Boolean {
        val index = customEditors.indexOfFirst { it.editorId == editorId }
        if (index == -1) {
            thisLogger().warn("Custom editor with ID $editorId not found")
            return false
        }
        if (!config.isValid()) {
            thisLogger().warn("Invalid custom editor configuration")
            return false
        }
        customEditors[index] = config
        thisLogger().info("Updated custom editor: ${config.displayName}")
        return true
    }
    
    /**
     * Delete custom editor
     */
    fun deleteCustomEditor(editorId: String): Boolean {
        val removed = customEditors.removeIf { it.editorId == editorId }
        if (removed) {
            thisLogger().info("Deleted custom editor: $editorId")
        }
        return removed
    }
    
    /**
     * Get custom editor configuration by ID
     */
    fun getCustomEditor(editorId: String): CustomEditorConfig? {
        return customEditors.firstOrNull { it.editorId == editorId }
    }
    
    /**
     * Get all custom editors
     */
    fun getAllCustomEditors(): List<CustomEditorConfig> {
        return customEditors.toList()
    }
    
    /**
     * Get all enabled custom editors
     */
    fun getEnabledCustomEditors(): List<CustomEditorConfig> {
        return customEditors.filter { it.enabled }
    }
    
    /**
     * Switch to a custom editor
     */
    fun switchToCustomEditor(
        editorId: String, 
        filePath: String?, 
        projectPath: String?, 
        line: Int = 1, 
        column: Int = 1
    ) {
        val config = getCustomEditor(editorId)
        if (config == null) {
            thisLogger().warn("Custom editor with ID $editorId not found")
            return
        }
        
        if (!config.enabled) {
            thisLogger().warn("Custom editor $editorId is disabled")
            return
        }
        
        try {
            val command = buildCustomCommand(config, filePath, projectPath, line, column)
            val processBuilder = ProcessBuilder(command)
            processBuilder.start()
            thisLogger().info("Switched to custom editor ${config.displayName} at line $line, column $column")
        } catch (e: Exception) {
            thisLogger().error("Failed to switch to custom editor ${config.displayName}", e)
        }
    }
    
    /**
     * Build command from template
     */
    fun buildCustomCommand(
        config: CustomEditorConfig,
        filePath: String?,
        projectPath: String?,
        line: Int = 1,
        column: Int = 1
    ): List<String> {
        var template = config.commandTemplate
        
        // Replace placeholders
        template = template.replace("{EXECUTABLE}", config.executablePath)
        
        if (projectPath != null) {
            template = template.replace("{PROJECT}", projectPath)
        } else {
            // Remove {PROJECT} placeholder if no project path
            template = template.replace("{PROJECT}", "")
        }
        
        if (filePath != null) {
            template = template.replace("{FILE}", filePath)
            template = template.replace("{LINE}", line.toString())
            template = template.replace("{COLUMN}", column.toString())
        } else {
            // Remove file-related placeholders if no file
            template = template.replace("{FILE}", "")
            template = template.replace("{LINE}", "")
            template = template.replace("{COLUMN}", "")
        }
        
        // Split into command tokens and filter out empty strings
        val command = template.split(Regex("\\s+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        
        return command
    }
    
    /**
     * Validate command template
     */
    fun validateTemplate(template: String): ValidationResult {
        if (template.isEmpty()) {
            return ValidationResult.error("Template cannot be empty")
        }
        
        if (!template.contains("{EXECUTABLE}")) {
            return ValidationResult.error("Template must contain {EXECUTABLE} placeholder")
        }
        
        // Check for valid placeholder syntax
        val placeholderPattern = Regex("\\{[A-Z_]+\\}")
        val validPlaceholders = setOf("{EXECUTABLE}", "{PROJECT}", "{FILE}", "{LINE}", "{COLUMN}")
        
        placeholderPattern.findAll(template).forEach { match ->
            if (match.value !in validPlaceholders) {
                return ValidationResult.error("Unknown placeholder: ${match.value}")
            }
        }
        
        return ValidationResult.success()
    }
    
    /**
     * Validate custom editor ID
     */
    fun validateEditorId(editorId: String, isNew: Boolean = true): ValidationResult {
        if (editorId.isEmpty()) {
            return ValidationResult.error("Editor ID cannot be empty")
        }
        
        if (!editorId.matches(Regex("^[a-zA-Z0-9_]+$"))) {
            return ValidationResult.error("Editor ID must contain only letters, numbers, and underscores")
        }
        
        if (editorId.length > 50) {
            return ValidationResult.error("Editor ID must be at most 50 characters")
        }
        
        if (isNew && customEditors.any { it.editorId == editorId }) {
            return ValidationResult.error("Editor ID already exists")
        }
        
        return ValidationResult.success()
    }
}

enum class EditorType {
    KIRO, VSCODE, SUBLIME, ATOM, NOTEPADPP, VIM, EMACS, CURSOR, ZED
}