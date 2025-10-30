package com.example.editorswitcher.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project

class EditorSwitcherConfigAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        ShowSettingsUtil.getInstance().showSettingsDialog(project, "Smart Editor Switcher")
    }}
