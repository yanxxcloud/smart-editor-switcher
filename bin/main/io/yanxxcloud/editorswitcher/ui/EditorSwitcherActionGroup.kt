package io.yanxxcloud.editorswitcher.ui

import io.yanxxcloud.editorswitcher.actions.*
import io.yanxxcloud.editorswitcher.services.EditorSwitcherService
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Separator

class EditorSwitcherActionGroup : ActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        val builtInActions = arrayOf(
            SwitchToVSCodeAction().apply { 
                templatePresentation.text = "📘 VS Code"
                templatePresentation.description = "Switch to VS Code"
            },
            SwitchToCursorAction().apply { 
                templatePresentation.text = "🎯 Cursor"
                templatePresentation.description = "Switch to Cursor"
            },
            SwitchToZedAction().apply { 
                templatePresentation.text = "⚡ Zed"
                templatePresentation.description = "Switch to Zed"
            },
            Separator.getInstance(),
            SwitchToKiroAction().apply { 
                templatePresentation.text = "🚀 Kiro"
                templatePresentation.description = "Switch to Kiro Editor"
            },
            SwitchToSublimeAction().apply { 
                templatePresentation.text = "🎨 Sublime Text"
                templatePresentation.description = "Switch to Sublime Text"
            }
        )
        
        // Add custom editors if any are enabled
        val service = EditorSwitcherService.getInstance()
        val customEditors = service.getEnabledCustomEditors()
            .filter { it.showInStatusBar }
        
        if (customEditors.isEmpty()) {
            return builtInActions
        }
        
        // Add separator and custom editors
        val customActions = customEditors.map { config ->
            SwitchToCustomEditorAction(config.editorId, config.displayName).apply {
                templatePresentation.text = config.getDisplayNameWithIcon()
                templatePresentation.description = "Switch to ${config.displayName}"
            }
        }
        
        return builtInActions + Separator.getInstance() + customActions
    }
}