package io.yanxxcloud.editorswitcher.model

/**
 * Configuration for a custom editor.
 * This allows users to add any editor without code modification.
 */
data class CustomEditorConfig(
    /**
     * Unique identifier for the editor (alphanumeric + underscore)
     */
    var editorId: String = "",
    
    /**
     * Human-readable display name shown in UI
     */
    var displayName: String = "",
    
    /**
     * Full path to editor executable or command
     */
    var executablePath: String = "",
    
    /**
     * Optional emoji for visual identification
     */
    var iconEmoji: String = "",
    
    /**
     * Command template with placeholders:
     * {EXECUTABLE}, {PROJECT}, {FILE}, {LINE}, {COLUMN}
     */
    var commandTemplate: String = "",
    
    /**
     * Whether this editor is active
     */
    var enabled: Boolean = true,
    
    /**
     * Optional custom keyboard shortcut (e.g., "ctrl alt W")
     */
    var keyboardShortcut: String = "",
    
    /**
     * Display in status bar widget menu
     */
    var showInStatusBar: Boolean = true,
    
    /**
     * Display in right-click context menu
     */
    var showInContextMenu: Boolean = true,
    
    /**
     * Display in Tools > Switch Editor menu
     */
    var showInToolsMenu: Boolean = true
) {
    /**
     * Returns display name with emoji prefix if configured
     */
    fun getDisplayNameWithIcon(): String {
        return if (iconEmoji.isNotEmpty()) {
            "$iconEmoji $displayName"
        } else {
            displayName
        }
    }
    
    /**
     * Validates if this configuration is complete
     */
    fun isValid(): Boolean {
        return editorId.isNotEmpty() && 
               displayName.isNotEmpty() && 
               executablePath.isNotEmpty() && 
               commandTemplate.isNotEmpty() &&
               commandTemplate.contains("{EXECUTABLE}")
    }
}
