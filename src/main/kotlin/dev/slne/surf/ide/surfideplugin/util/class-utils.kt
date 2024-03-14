package dev.slne.surf.ide.surfideplugin.util

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass

fun PsiClass.addAnnotation(annotation: String) {
    if (annotations.any { it.hasQualifiedName(annotation) }) {
        return
    }

    JavaPsiFacade.getInstance(project).findClass(annotation, resolveScope) ?: return
    modifierList?.addAnnotation(annotation)
}

fun PsiClass.getDefaultListenerName() = "on" + name?.replace("Event", "")