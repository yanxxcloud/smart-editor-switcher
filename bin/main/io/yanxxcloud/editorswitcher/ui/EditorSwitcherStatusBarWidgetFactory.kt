package io.yanxxcloud.editorswitcher.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class EditorSwitcherStatusBarWidgetFactory : StatusBarWidgetFactory {
    
    override fun getId(): String = "SmartEditorSwitcher"
    
    override fun getDisplayName(): String = "Smart Editor Switcher"
    
    override fun isAvailable(project: Project): Boolean = true
    
    override fun createWidget(project: Project): StatusBarWidget {
        return EditorSwitcherStatusBarWidget(project)
    }
    
    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }
    
    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}