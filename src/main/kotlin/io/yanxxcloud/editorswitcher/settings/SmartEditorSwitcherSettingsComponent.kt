package io.yanxxcloud.editorswitcher.settings

import io.yanxxcloud.editorswitcher.services.EditorSwitcherService
import io.yanxxcloud.editorswitcher.model.CustomEditorConfig
import io.yanxxcloud.editorswitcher.ui.CustomEditorDialog
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class SmartEditorSwitcherSettingsComponent {
    
    val panel: JPanel
    private val vsCodePathField: TextFieldWithBrowseButton
    private val cursorPathField: TextFieldWithBrowseButton
    private val zedPathField: TextFieldWithBrowseButton
    private val kiroPathField: TextFieldWithBrowseButton
    private val sublimePathField: TextFieldWithBrowseButton
    
    // Custom editors table
    private val customEditorsTableModel = DefaultTableModel(
        arrayOf("Emoji", "Name", "Enabled", "Editor ID"),
        0
    )
    private val customEditorsTable = JBTable(customEditorsTableModel)
    private var customEditorsData = mutableListOf<CustomEditorConfig>()

    var vsCodePathText: String
        get() = vsCodePathField.text
        set(value) { vsCodePathField.text = value }

    var cursorPathText: String
        get() = cursorPathField.text
        set(value) { cursorPathField.text = value }

    var zedPathText: String
        get() = zedPathField.text
        set(value) { zedPathField.text = value }

    var kiroPathText: String
        get() = kiroPathField.text
        set(value) { kiroPathField.text = value }

    var sublimePathText: String
        get() = sublimePathField.text
        set(value) { sublimePathField.text = value }

    val preferredFocusedComponent: JComponent
        get() = vsCodePathField
    
    /**
     * Get custom editors configurations
     */
    fun getCustomEditors(): List<CustomEditorConfig> {
        return customEditorsData.toList()
    }
    
    /**
     * Set custom editors configurations
     */
    fun setCustomEditors(editors: List<CustomEditorConfig>) {
        customEditorsData.clear()
        customEditorsData.addAll(editors)
        refreshCustomEditorsTable()
    }
    
    /**
     * Refresh custom editors table
     */
    private fun refreshCustomEditorsTable() {
        customEditorsTableModel.setRowCount(0)
        customEditorsData.forEach { config ->
            customEditorsTableModel.addRow(
                arrayOf(
                    config.iconEmoji.ifEmpty { "-" },
                    config.displayName,
                    if (config.enabled) "✓" else "✗",
                    config.editorId
                )
            )
        }
    }

    init {
        vsCodePathField = TextFieldWithBrowseButton().apply {
            addActionListener {
                val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
                    .withTitle("选择 VS Code 可执行文件")
                    .withDescription("请选择 VS Code 的可执行文件路径")
                com.intellij.openapi.fileChooser.FileChooser.chooseFile(descriptor, null, null) { file ->
                    text = file.path
                }
            }
        }

        cursorPathField = TextFieldWithBrowseButton().apply {
            addActionListener {
                val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
                    .withTitle("选择 Cursor 可执行文件")
                    .withDescription("请选择 Cursor 的可执行文件路径")
                com.intellij.openapi.fileChooser.FileChooser.chooseFile(descriptor, null, null) { file ->
                    text = file.path
                }
            }
        }

        zedPathField = TextFieldWithBrowseButton().apply {
            addActionListener {
                val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
                    .withTitle("选择 Zed 可执行文件")
                    .withDescription("请选择 Zed 的可执行文件路径")
                com.intellij.openapi.fileChooser.FileChooser.chooseFile(descriptor, null, null) { file ->
                    text = file.path
                }
            }
        }

        kiroPathField = TextFieldWithBrowseButton().apply {
            addActionListener {
                val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
                    .withTitle("选择 Kiro 可执行文件")
                    .withDescription("请选择 Kiro 编辑器的可执行文件路径")
                com.intellij.openapi.fileChooser.FileChooser.chooseFile(descriptor, null, null) { file ->
                    text = file.path
                }
            }
        }

        sublimePathField = TextFieldWithBrowseButton().apply {
            addActionListener {
                val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
                    .withTitle("选择 Sublime Text 可执行文件")
                    .withDescription("请选择 Sublime Text 的可执行文件路径")
                com.intellij.openapi.fileChooser.FileChooser.chooseFile(descriptor, null, null) { file ->
                    text = file.path
                }
            }
        }
    
    panel = panel {
            group("Built-in Editors") {
                row("📘 VS Code Path:") {
                    cell(vsCodePathField)
                        .align(AlignX.FILL)
                        .comment("Visual Studio Code executable path")
                }
                row("🎯 Cursor Path:") {
                    cell(cursorPathField)
                        .align(AlignX.FILL)
                        .comment("Cursor AI editor executable path")
                }
                row("⚡ Zed Path:") {
                    cell(zedPathField)
                        .align(AlignX.FILL)
                        .comment("Zed editor executable path")
                }
            }
            
            group("Other Editors") {
                row("🚀 Kiro Path:") {
                    cell(kiroPathField)
                        .align(AlignX.FILL)
                        .comment("Kiro AI editor executable path")
                }
                row("🎨 Sublime Text Path:") {
                    cell(sublimePathField)
                        .align(AlignX.FILL)
                        .comment("Sublime Text executable path")
                }
            }
            
            group("Custom Editors") {
                row {
                    comment("Add your own AI IDEs and editors without code modification")
                }
                
                row {
                    val tablePanel = ToolbarDecorator.createDecorator(customEditorsTable)
                        .setAddAction {
                            val dialog = CustomEditorDialog()
                            if (dialog.showAndGet()) {
                                val config = dialog.getEditorConfig()
                                customEditorsData.add(config)
                                refreshCustomEditorsTable()
                            }
                        }
                        .setEditAction {
                            val selectedRow = customEditorsTable.selectedRow
                            if (selectedRow >= 0 && selectedRow < customEditorsData.size) {
                                val config = customEditorsData[selectedRow]
                                val dialog = CustomEditorDialog(config, isEditMode = true)
                                if (dialog.showAndGet()) {
                                    customEditorsData[selectedRow] = dialog.getEditorConfig()
                                    refreshCustomEditorsTable()
                                }
                            }
                        }
                        .setRemoveAction {
                            val selectedRow = customEditorsTable.selectedRow
                            if (selectedRow >= 0 && selectedRow < customEditorsData.size) {
                                customEditorsData.removeAt(selectedRow)
                                refreshCustomEditorsTable()
                            }
                        }
                        .createPanel()
                    
                    cell(tablePanel)
                        .align(Align.FILL)
                }.resizableRow()
                
                row {
                    comment("""
                        <b>Template Placeholders:</b><br>
                        {EXECUTABLE} - Editor path | {PROJECT} - Project path | {FILE} - File path<br>
                        {LINE} - Line number | {COLUMN} - Column number
                    """.trimIndent())
                }
            }
            
            group("Usage Instructions") {
                row {
                    text("""
                        <p><b>Shortcuts:</b></p>
                        <ul>
                        <li>Ctrl+Alt+V: Switch to VS Code</li>
                        <li>Ctrl+Alt+C: Switch to Cursor</li>
                        <li>Ctrl+Alt+Z: Switch to Zed</li>
                        <li>Ctrl+Alt+K: Switch to Kiro</li>
                        <li>Ctrl+Alt+S: Switch to Sublime Text</li>
                        </ul>
                        <p><b>Features:</b></p>
                        <ul>
                        <li>Smart cursor positioning - maintains exact line/column</li>
                        <li>Project context preservation - auto-passes project path</li>
                        <li>Status bar quick access - click bottom status bar</li>
                        <li>Context menu integration - right-click on files</li>
                        </ul>
                    """.trimIndent())
                }
            }
            
            row {
                button("Auto-detect Paths") {
                    val service = EditorSwitcherService.getInstance()
                    service.detectEditorPaths()
                    vsCodePathText = service.vsCodePath
                    cursorPathText = service.cursorPath
                    zedPathText = service.zedPath
                    kiroPathText = service.kiroPath
                    sublimePathText = service.sublimePath
                }
            }
        }
    }
}