package io.yanxxcloud.editorswitcher.settings

import io.yanxxcloud.editorswitcher.services.EditorSwitcherService
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent
import javax.swing.JPanel

class SmartEditorSwitcherSettingsComponent {
    
    val panel: JPanel
    private val vsCodePathField: TextFieldWithBrowseButton
    private val cursorPathField: TextFieldWithBrowseButton
    private val zedPathField: TextFieldWithBrowseButton
    private val kiroPathField: TextFieldWithBrowseButton
    private val sublimePathField: TextFieldWithBrowseButton

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

    init {
        vsCodePathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "选择 VS Code 可执行文件",
                "请选择 VS Code 的可执行文件路径",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }

        cursorPathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "选择 Cursor 可执行文件",
                "请选择 Cursor 的可执行文件路径",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }

        zedPathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "选择 Zed 可执行文件",
                "请选择 Zed 的可执行文件路径",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }

        kiroPathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "选择 Kiro 可执行文件",
                "请选择 Kiro 编辑器的可执行文件路径",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }

        sublimePathField = TextFieldWithBrowseButton().apply {
            addBrowseFolderListener(
                "选择 Sublime Text 可执行文件",
                "请选择 Sublime Text 的可执行文件路径",
                null,
                FileChooserDescriptorFactory.createSingleFileDescriptor()
            )
        }

        panel = panel {
            group("主流编辑器") {
                row("📘 VS Code 路径:") {
                    cell(vsCodePathField)
                        .align(AlignX.FILL)
                        .comment("Visual Studio Code 的可执行文件路径")
                }
                row("🎯 Cursor 路径:") {
                    cell(cursorPathField)
                        .align(AlignX.FILL)
                        .comment("Cursor AI 编辑器的可执行文件路径")
                }
                row("⚡ Zed 路径:") {
                    cell(zedPathField)
                        .align(AlignX.FILL)
                        .comment("Zed 编辑器的可执行文件路径")
                }
            }
            
            group("其他编辑器") {
                row("🚀 Kiro 路径:") {
                    cell(kiroPathField)
                        .align(AlignX.FILL)
                        .comment("Kiro AI 编辑器的可执行文件路径")
                }
                row("🎨 Sublime Text 路径:") {
                    cell(sublimePathField)
                        .align(AlignX.FILL)
                        .comment("Sublime Text 的可执行文件路径")
                }
            }
            
            group("使用说明") {
                row {
                    text("""
                        <p><b>快捷键:</b></p>
                        <ul>
                        <li>Ctrl+Alt+V: 切换到 VS Code</li>
                        <li>Ctrl+Alt+C: 切换到 Cursor</li>
                        <li>Ctrl+Alt+Z: 切换到 Zed</li>
                        <li>Ctrl+Alt+K: 切换到 Kiro</li>
                        <li>Ctrl+Alt+S: 切换到 Sublime Text</li>
                        </ul>
                        <p><b>功能特性:</b></p>
                        <ul>
                        <li>智能光标定位 - 保持精确的行列位置</li>
                        <li>项目上下文保持 - 自动传递项目路径</li>
                        <li>状态栏快速访问 - 点击底部状态栏切换</li>
                        <li>右键菜单集成 - 在文件上右键快速切换</li>
                        </ul>
                    """.trimIndent())
                }
            }
            
            row {
                button("自动检测路径") {
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