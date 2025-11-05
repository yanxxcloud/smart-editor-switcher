package io.yanxxcloud.editorswitcher.ui

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import io.yanxxcloud.editorswitcher.model.CustomEditorConfig
import io.yanxxcloud.editorswitcher.services.EditorSwitcherService
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea

/**
 * Dialog for adding or editing custom editor configuration
 */
class CustomEditorDialog(
    private val existingConfig: CustomEditorConfig? = null,
    private val isEditMode: Boolean = false
) : DialogWrapper(true) {

    private val editorIdField = JBTextField()
    private val displayNameField = JBTextField()
    private val executablePathField = TextFieldWithBrowseButton()
    private val iconEmojiField = JBTextField()
    private val commandTemplateArea = JTextArea(5, 50)
    private val keyboardShortcutField = JBTextField()
    private val showInStatusBarCheckbox = JCheckBox("Show in Status Bar", true)
    private val showInContextMenuCheckbox = JCheckBox("Show in Context Menu", true)
    private val showInToolsMenuCheckbox = JCheckBox("Show in Tools Menu", true)

    init {
        title = if (isEditMode) "Edit Custom Editor" else "Add Custom Editor"
        
        // Load existing config if editing
        existingConfig?.let {
            editorIdField.text = it.editorId
            editorIdField.isEditable = false // ID is read-only in edit mode
            displayNameField.text = it.displayName
            executablePathField.text = it.executablePath
            iconEmojiField.text = it.iconEmoji
            commandTemplateArea.text = it.commandTemplate
            keyboardShortcutField.text = it.keyboardShortcut
            showInStatusBarCheckbox.isSelected = it.showInStatusBar
            showInContextMenuCheckbox.isSelected = it.showInContextMenu
            showInToolsMenuCheckbox.isSelected = it.showInToolsMenu
        }
        
        // Configure executable path browser
        executablePathField.addActionListener {
            val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
                .withTitle("Select Editor Executable")
                .withDescription("Select the executable file for the editor")
            com.intellij.openapi.fileChooser.FileChooser.chooseFile(descriptor, null, null) { file ->
                executablePathField.text = file.path
            }
        }
        
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            group("Basic Information") {
                row("Editor ID:") {
                    cell(editorIdField)
                        .align(AlignX.FILL)
                        .comment("Unique identifier (alphanumeric + underscore, max 50 chars)")
                }
                
                row("Display Name:") {
                    cell(displayNameField)
                        .align(AlignX.FILL)
                        .comment("Name shown in menus and UI")
                }
                
                row("Executable Path:") {
                    cell(executablePathField)
                        .align(AlignX.FILL)
                        .comment("Full path to editor executable or command")
                }
                
                row("Icon Emoji:") {
                    cell(iconEmojiField)
                        .align(AlignX.FILL)
                        .comment("Optional emoji for visual identification (e.g., 🌊)")
                }
            }
            
            group("Command Template") {
                row {
                    label("Template:")
                        .align(AlignY.TOP)
                    scrollCell(commandTemplateArea)
                        .align(AlignX.FILL)
                        .comment("""
                            Use placeholders: {EXECUTABLE}, {PROJECT}, {FILE}, {LINE}, {COLUMN}
                            Example: {EXECUTABLE} {PROJECT} --goto {FILE}:{LINE}:{COLUMN}
                        """.trimIndent())
                }
                
                row {
                    comment("""
                        <b>Common Templates:</b><br>
                        VS Code style: {EXECUTABLE} {PROJECT} --goto {FILE}:{LINE}:{COLUMN}<br>
                        Sublime style: {EXECUTABLE} {PROJECT} {FILE}:{LINE}:{COLUMN}<br>
                        Custom: {EXECUTABLE} -p {PROJECT} -f {FILE} -l {LINE} -c {COLUMN}
                    """.trimIndent())
                }
            }
            
            group("Keyboard Shortcut") {
                row("Shortcut:") {
                    cell(keyboardShortcutField)
                        .align(AlignX.FILL)
                        .comment("Optional (e.g., ctrl alt W)")
                }
            }
            
            group("Visibility") {
                row {
                    cell(showInToolsMenuCheckbox)
                }
                row {
                    cell(showInStatusBarCheckbox)
                }
                row {
                    cell(showInContextMenuCheckbox)
                }
            }
        }
    }

    override fun doValidate(): ValidationInfo? {
        val service = EditorSwitcherService.getInstance()
        
        // Validate Editor ID
        val editorId = editorIdField.text.trim()
        val idValidation = service.validateEditorId(editorId, !isEditMode)
        if (!idValidation.isValid) {
            return ValidationInfo(idValidation.errorMessage, editorIdField)
        }
        
        // Validate Display Name
        val displayName = displayNameField.text.trim()
        if (displayName.isEmpty()) {
            return ValidationInfo("Display name is required", displayNameField)
        }
        if (displayName.length > 100) {
            return ValidationInfo("Display name must be at most 100 characters", displayNameField)
        }
        
        // Validate Executable Path
        val executablePath = executablePathField.text.trim()
        if (executablePath.isEmpty()) {
            return ValidationInfo("Executable path is required", executablePathField)
        }
        
        // Validate Command Template
        val template = commandTemplateArea.text.trim()
        val templateValidation = service.validateTemplate(template)
        if (!templateValidation.isValid) {
            return ValidationInfo(templateValidation.errorMessage, commandTemplateArea)
        }
        
        return null
    }

    /**
     * Get the configured editor from dialog fields
     */
    fun getEditorConfig(): CustomEditorConfig {
        return CustomEditorConfig(
            editorId = editorIdField.text.trim(),
            displayName = displayNameField.text.trim(),
            executablePath = executablePathField.text.trim(),
            iconEmoji = iconEmojiField.text.trim(),
            commandTemplate = commandTemplateArea.text.trim(),
            enabled = true,
            keyboardShortcut = keyboardShortcutField.text.trim(),
            showInStatusBar = showInStatusBarCheckbox.isSelected,
            showInContextMenu = showInContextMenuCheckbox.isSelected,
            showInToolsMenu = showInToolsMenuCheckbox.isSelected
        )
    }
}
