package io.yanxxcloud.editorswitcher.model

/**
 * Result of template validation
 */
data class ValidationResult(
    /**
     * Whether the validation passed
     */
    val isValid: Boolean,
    
    /**
     * Error message if validation failed, empty if valid
     */
    val errorMessage: String = ""
) {
    companion object {
        fun success() = ValidationResult(true, "")
        fun error(message: String) = ValidationResult(false, message)
    }
}
