package dev.slne.surf.ide.surfideplugin.addons.data.generation.redisevent

import com.intellij.codeInsight.generation.actions.BaseGenerateAction
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import dev.slne.surf.ide.surfideplugin.addons.data.DataConstants

class GenerateRedisEventListenerAction : BaseGenerateAction(GenerateRedisEventListenerHandler()) {

    override fun isValidForClass(targetClass: PsiClass): Boolean {
        if (targetClass.isEnum || targetClass.isInterface || targetClass.isAnnotationType) {
            return false
        }

        return JavaPsiFacade.getInstance(targetClass.project)
            .findClass(DataConstants.DATA_REDIS_EVENT, targetClass.resolveScope) != null
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.text = "Generate Redis Event Listener"
    }
}