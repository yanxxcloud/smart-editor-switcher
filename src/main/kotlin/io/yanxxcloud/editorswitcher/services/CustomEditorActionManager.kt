package io.yanxxcloud.editorswitcher.services

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import io.yanxxcloud.editorswitcher.actions.SwitchToCustomEditorAction
import io.yanxxcloud.editorswitcher.model.CustomEditorConfig

/**
 * Manages dynamic registration and unregistration of custom editor actions
 */
@Service
class CustomEditorActionManager {
    
    companion object {
        private const val ACTION_ID_PREFIX = "SmartEditorSwitcher.Custom."
        private const val TOOLS_GROUP_ID = "SmartEditorSwitcher.MainGroup"
        private const val CONTEXT_GROUP_ID = "SmartEditorSwitcher.ContextMenu"
    }
    
    private val registeredActionIds = mutableSetOf<String>()
    
    /**
     * Register actions for all enabled custom editors
     */
    fun registerCustomEditorActions(customEditors: List<CustomEditorConfig>) {
        thisLogger().info("Registering custom editor actions")
        
        // Unregister all existing custom actions first
        unregisterAllCustomActions()
        
        val actionManager = ActionManager.getInstance()
        
        // Register actions for each enabled custom editor
        customEditors.filter { it.enabled }.forEach { config ->
            val actionId = ACTION_ID_PREFIX + config.editorId
            val action = SwitchToCustomEditorAction(config.editorId, config.displayName)
            
            try {
                // Register the action
                actionManager.registerAction(actionId, action)
                registeredActionIds.add(actionId)
                
                // Add to Tools menu if configured
                if (config.showInToolsMenu) {
                    val toolsGroup = actionManager.getAction(TOOLS_GROUP_ID) as? DefaultActionGroup
                    toolsGroup?.add(action)
                }
                
                // Add to context menu if configured
                if (config.showInContextMenu) {
                    val contextGroup = actionManager.getAction(CONTEXT_GROUP_ID) as? DefaultActionGroup
                    contextGroup?.add(action)
                }
                
                thisLogger().info("Registered custom editor action: $actionId")
            } catch (e: Exception) {
                thisLogger().error("Failed to register custom editor action: $actionId", e)
            }
        }
    }
    
    /**
     * Unregister all custom editor actions
     */
    fun unregisterAllCustomActions() {
        val actionManager = ActionManager.getInstance()
        
        registeredActionIds.forEach { actionId ->
            try {
                // Remove from action groups
                val toolsGroup = actionManager.getAction(TOOLS_GROUP_ID) as? DefaultActionGroup
                val contextGroup = actionManager.getAction(CONTEXT_GROUP_ID) as? DefaultActionGroup
                val action = actionManager.getAction(actionId)
                
                action?.let {
                    toolsGroup?.remove(it)
                    contextGroup?.remove(it)
                }
                
                // Unregister the action
                actionManager.unregisterAction(actionId)
                thisLogger().info("Unregistered custom editor action: $actionId")
            } catch (e: Exception) {
                thisLogger().error("Failed to unregister custom editor action: $actionId", e)
            }
        }
        
        registeredActionIds.clear()
    }
    
    /**
     * Refresh all custom editor actions based on current configuration
     */
    fun refreshActions() {
        val service = EditorSwitcherService.getInstance()
        val customEditors = service.getAllCustomEditors()
        registerCustomEditorActions(customEditors)
    }
}
