package dev.slne.surf.ide.surfideplugin.addons.data.inspection

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiParameter
import com.intellij.psi.PsiTypeParameter
import com.intellij.psi.util.PsiUtil
import com.siyeh.ig.BaseInspection
import com.siyeh.ig.BaseInspectionVisitor
import dev.slne.surf.ide.surfideplugin.addons.data.DataConstants

class DataListenerInspection : BaseInspection() {

    override fun getDisplayName(): String {
        return "Data @DataListener method with incorrect parameters"
    }

    override fun buildVisitor(): BaseInspectionVisitor {
        return object : BaseInspectionVisitor() {
            override fun visitMethod(method: PsiMethod) {
                if (method.parameterList.parameters.size != 1) {
                    registerMethodError(method)
                }

                val parameter = method.parameterList.parameters[0]
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

fun PsiParameter.isExtendingRedisEvent(): Boolean {
    val redisClass =
        JavaPsiFacade.getInstance(project).findClass(DataConstants.DATA_REDIS_EVENT, resolveScope)
            ?: return false

    return PsiUtil.resolveClassInClassTypeOnly(type)?.let { typeElement ->
        return typeElement.isInheritor(redisClass, true)
    } ?: false
}