package io.yanxxcloud.editorswitcher.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

object EditorUtils {
    
    data class EditorContext(
        val project: Project?,
        val file: VirtualFile?,
        val filePath: String?,
        val projectPath: String?,
        val line: Int,
        val column: Int
    )
    
    fun getEditorContext(e: AnActionEvent): EditorContext {
        val project = e.project
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val editor = e.getData(CommonDataKeys.EDITOR)
        
        val filePath = file?.path
        val projectPath = project?.basePath
        
        // 获取光标位置
        val (line, column) = if (editor != null) {
            val caretModel = editor.caretModel
            val logicalPosition = caretModel.logicalPosition
            Pair(logicalPosition.line + 1, logicalPosition.column + 1) // 转换为 1-based
        } else {
            Pair(1, 1)
        }
        
        return EditorContext(
            project = project,
            file = file,
            filePath = filePath,
            projectPath = projectPath,
            line = line,
            column = column
        )
    }
}