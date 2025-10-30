package io.yanxxcloud.editorswitcher.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedWidget
import com.intellij.util.Consumer
import java.awt.event.MouseEvent

class EditorSwitcherStatusBarWidget(project: Project) : EditorBasedWidget(project), StatusBarWidget.MultipleTextValuesPresentation {
    
    companion object {
        const val ID = "SmartEditorSwitcher"
    }

    override fun ID(): String = ID

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun getTooltipText(): String = "点击切换编辑器"

    override fun getSelectedValue(): String = "编辑器切换"

    @Deprecated("This method is deprecated and will be removed in future releases")
    override fun getPopupStep(): ListPopup? {
        return createPopup()
    }

    private fun createPopup(): ListPopup? {
        val group = EditorSwitcherActionGroup()
        return JBPopupFactory.getInstance().createActionGroupPopup(
            "切换到编辑器",
            group,
            com.intellij.openapi.actionSystem.impl.SimpleDataContext.getProjectContext(project),
            JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
            false
        )
    }

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return Consumer { _ ->
            val popup = createPopup()
            popup?.showInBestPositionFor(com.intellij.openapi.actionSystem.impl.SimpleDataContext.getProjectContext(project))
        }
    }

    override fun install(statusBar: StatusBar) {
        super.install(statusBar)
    }

    override fun dispose() {
        super.dispose()
    }
}