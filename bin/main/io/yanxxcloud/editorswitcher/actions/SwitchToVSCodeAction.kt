package io.yanxxcloud.editorswitcher.actions

import io.yanxxcloud.editorswitcher.services.EditorSwitcherService
import io.yanxxcloud.editorswitcher.services.EditorType
import io.yanxxcloud.editorswitcher.utils.EditorUtils
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages

class SwitchToVSCodeAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val context = EditorUtils.getEditorContext(e)
        val service = EditorSwitcherService.getInstance()
        
        if (service.vsCodePath.isEmpty()) {
            service.detectEditorPaths()
            if (service.vsCodePath.isEmpty()) {
                Messages.showErrorDialog(
                    context.project,
                    "VS Code 路径未配置。请在 Settings > Tools > Smart Editor Switcher 中配置路径。",
                    "Smart Editor Switcher"
                )
                return
            }
        }

        service.switchToEditor(
            EditorType.VSCODE, 
            context.filePath, 
            context.projectPath, 
            context.line, 
            context.column
        )
        
        Messages.showInfoMessage(
            context.project,
            "正在切换到 VS Code... (行: ${context.line}, 列: ${context.column})",
            "Smart Editor Switcher"
        )
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}