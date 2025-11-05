package io.yanxxcloud.editorswitcher.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import io.yanxxcloud.editorswitcher.model.CustomEditorConfig
import io.yanxxcloud.editorswitcher.services.EditorSwitcherService
import io.yanxxcloud.editorswitcher.utils.EditorUtils

/**
 * Generic action for switching to a custom editor.
 * Instances are created dynamically for each enabled custom editor.
 */
class SwitchToCustomEditorAction(
    private val editorId: String,
    displayName: String
) : AnAction(displayName) {

    override fun actionPerformed(e: AnActionEvent) {
        val context = EditorUtils.getEditorContext(e)
        val service = EditorSwitcherService.getInstance()
        
        val config = service.getCustomEditor(editorId)
        if (config == null) {
            Messages.showErrorDialog(
                context.project,
                "Custom editor configuration not found: $editorId",
                "Smart Editor Switcher"
            )
            return
        }
        
        if (!config.enabled) {
            Messages.showWarningDialog(
                context.project,
                "Editor \"${config.displayName}\" is currently disabled. Please enable it in Settings.",
                "Smart Editor Switcher"
            )
            return
        }
        
        // Check if executable exists
        if (config.executablePath.isEmpty()) {
            Messages.showErrorDialog(
                context.project,
                "Executable path not configured for ${config.displayName}. Please configure it in Settings > Tools > Smart Editor Switcher.",
                "Smart Editor Switcher"
            )
            return
        }
        
        service.switchToCustomEditor(
            editorId,
            context.filePath,
            context.projectPath,
            context.line,
            context.column
        )
        
        Messages.showInfoMessage(
            context.project,
            "Switching to ${config.displayName}... (Line: ${context.line}, Column: ${context.column})",
            "Smart Editor Switcher"
        )
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
        
        // Update presentation with icon if available
        val service = EditorSwitcherService.getInstance()
        val config = service.getCustomEditor(editorId)
        config?.let {
            e.presentation.text = it.getDisplayNameWithIcon()
        }
    }
}
