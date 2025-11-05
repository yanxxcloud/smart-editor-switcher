package io.yanxxcloud.editorswitcher.settings

import io.yanxxcloud.editorswitcher.services.EditorSwitcherService
import io.yanxxcloud.editorswitcher.services.CustomEditorActionManager
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class SmartEditorSwitcherConfigurable : Configurable {
    
    private var settingsComponent: SmartEditorSwitcherSettingsComponent? = null

    override fun getDisplayName(): String = "Smart Editor Switcher"

    override fun getPreferredFocusedComponent(): JComponent? = settingsComponent?.preferredFocusedComponent

    override fun createComponent(): JComponent? {
        settingsComponent = SmartEditorSwitcherSettingsComponent()
        return settingsComponent?.panel
    }

    override fun isModified(): Boolean {
        val service = EditorSwitcherService.getInstance()
        val component = settingsComponent ?: return false
        
        val builtInModified = service.vsCodePath != component.vsCodePathText ||
               service.cursorPath != component.cursorPathText ||
               service.zedPath != component.zedPathText ||
               service.kiroPath != component.kiroPathText ||
               service.sublimePath != component.sublimePathText
        
        // Check if custom editors are modified
        val currentCustomEditors = service.getAllCustomEditors()
        val componentCustomEditors = component.getCustomEditors()
        val customEditorsModified = currentCustomEditors != componentCustomEditors
        
        return builtInModified || customEditorsModified
    }

    override fun apply() {
        val service = EditorSwitcherService.getInstance()
        val component = settingsComponent ?: return
        
        service.vsCodePath = component.vsCodePathText
        service.cursorPath = component.cursorPathText
        service.zedPath = component.zedPathText
        service.kiroPath = component.kiroPathText
        service.sublimePath = component.sublimePathText
        
        // Apply custom editors
        service.customEditors.clear()
        service.customEditors.addAll(component.getCustomEditors())
        
        // Refresh custom editor actions
        val actionManager = CustomEditorActionManager()
        actionManager.refreshActions()
    }

    override fun reset() {
        val service = EditorSwitcherService.getInstance()
        val component = settingsComponent ?: return
        
        component.vsCodePathText = service.vsCodePath
        component.cursorPathText = service.cursorPath
        component.zedPathText = service.zedPath
        component.kiroPathText = service.kiroPath
        component.sublimePathText = service.sublimePath
        
        // Reset custom editors
        component.setCustomEditors(service.getAllCustomEditors())
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}