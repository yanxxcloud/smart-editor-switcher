package io.yanxxcloud.editorswitcher.ui

import io.yanxxcloud.editorswitcher.actions.*
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Separator

class EditorSwitcherActionGroup : ActionGroup() {

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return arrayOf(
            SwitchToVSCodeAction().apply { 
                templatePresentation.text = "📘 VS Code"
                templatePresentation.description = "切换到 VS Code"
            },
            SwitchToCursorAction().apply { 
                templatePresentation.text = "🎯 Cursor"
                templatePresentation.description = "切换到 Cursor"
            },
            SwitchToZedAction().apply { 
                templatePresentation.text = "⚡ Zed"
                templatePresentation.description = "切换到 Zed"
            },
            Separator.getInstance(),
            SwitchToKiroAction().apply { 
                templatePresentation.text = "🚀 Kiro"
                templatePresentation.description = "切换到 Kiro 编辑器"
            },
            SwitchToSublimeAction().apply { 
                templatePresentation.text = "🎨 Sublime Text"
                templatePresentation.description = "切换到 Sublime Text"
            }
        )
    }
}