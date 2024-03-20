package dev.slne.surf.ide.surfideplugin.addons.data.inspection

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.intellij.psi.util.PsiUtil
import com.intellij.psi.util.createSmartPointer
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor
import com.siyeh.ig.InspectionGadgetsFix
import dev.slne.surf.ide.surfideplugin.addons.data.DataConstants
import dev.slne.surf.ide.surfideplugin.util.addAnnotation
import org.jetbrains.annotations.Nls

class DataListenerMethodInspection : BaseInspection() {

    override fun getDisplayName(): String {
        return "Data @DataListener method with incorrect parameters"
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : BaseInspectionVisitor() {
            override fun visitMethod(method: PsiMethod) {
                method.getAnnotation(DataConstants.DATA_LISTENER_ANNOTATION) ?: return

                val parameters = method.parameterList.parameters
                if (parameters.isEmpty() || parameters.size != 1) {
                    registerMethodError(method)
                }

                val parameter = parameters[0]
                if (!parameter.isExtendingRedisEvent()) {
                    registerError(parameter)
                }
            }
        }
    }

    override fun buildErrorString(vararg infos: Any?): String {
        return "The method must contain exactly one parameter of type RedisEvent"
    }
}

class DataListenerClassAnnotatedInspection : BaseInspection() {
    override fun getDisplayName() =
        "Class with @DataListener methods not annotated with @DataListeners"

    override fun buildErrorString(vararg infos: Any?) =
        "Class contains @DataListener methods but is not annotated with @DataListeners"

    override fun getStaticDescription() =
        "All classes containing @DataListener methods should be annotated with @DataListeners"

    override fun buildFix(vararg infos: Any?): LocalQuickFix {
        val classPointer = (infos[0] as PsiClass).createSmartPointer()
        return object : InspectionGadgetsFix() {
            override fun doFix(project: Project, descriptor: ProblemDescriptor) {
                classPointer.element?.addAnnotation(DataConstants.DATA_LISTENERS_ANNOTATION) ?: println("Class not found")
            }

            @Nls
            override fun getName() = "Add @DataListeners annotation"

            @Nls
            override fun getFamilyName() = name
        }
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : BaseInspectionVisitor() {
            override fun visitClass(aClass: PsiClass) {
                if (aClass.methods.any { it.hasAnnotation(DataConstants.DATA_LISTENER_ANNOTATION) }) {
                    if (!aClass.hasAnnotation(DataConstants.DATA_LISTENERS_ANNOTATION)) {
                        registerClassError(aClass, aClass)
                    }
                }
            }
        }
    }
}

fun PsiParameter.isExtendingRedisEvent(): Boolean {
    val redisClass =
        JavaPsiFacade.getInstance(project).findClass(DataConstants.DATA_REDIS_EVENT, resolveScope)
            ?: return false

    return PsiUtil.resolveClassInClassTypeOnly(type)?.let { typeElement ->
        return typeElement.isInheritor(redisClass, true)
    } ?: false
}