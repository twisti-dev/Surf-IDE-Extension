package dev.slne.surf.ide.surfideplugin.addons.data.generation.redisevent

import com.intellij.codeInsight.generation.ClassMember
import com.intellij.codeInsight.generation.GenerateMembersHandlerBase
import com.intellij.codeInsight.generation.GenerationInfo
import com.intellij.codeInsight.generation.PsiGenerationInfo
import com.intellij.ide.util.TreeClassChooserFactory
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.refactoring.RefactoringBundle
import dev.slne.surf.ide.surfideplugin.addons.data.DataConstants
import dev.slne.surf.ide.surfideplugin.util.addAnnotation
import dev.slne.surf.ide.surfideplugin.util.castNotNull
import dev.slne.surf.ide.surfideplugin.util.getDefaultListenerName

class GenerateRedisEventListenerHandler :
    GenerateMembersHandlerBase("Generate Redis Event Listener") {


    private var data: GenerateData? = null

    override fun getAllOriginalMembers(aClass: PsiClass?) = null

    override fun chooseOriginalMembers(
        aClass: PsiClass,
        project: Project,
        editor: Editor
    ): Array<ClassMember>? {
        val moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(aClass) ?: return null

        val chooser = TreeClassChooserFactory.getInstance(project)
            .createWithInnerClassesScopeChooser(
                RefactoringBundle.message("choose.destination.class"),
                GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(
                    moduleForPsiElement,
                    false
                ),
                { aClass1 -> isSuperRedisEventAllowed(aClass1) },
                null,
            )


        chooser.showDialog()
        val chosenClass = chooser.selected ?: return null
        val chosenClassName = chosenClass.nameIdentifier?.text ?: return null

        val generationDialog = GenerateRedisEventListenerDialog(
            editor,
            chosenClassName,
            chosenClass.getDefaultListenerName()
        )

        val okay = generationDialog.showAndGet()

        if (!okay) {
            return null
        }

        val chosenName = generationDialog.chosenListenerName
        val chosenChannels = generationDialog.chosenChannels.split(", ")
        val position = editor.caretModel.logicalPosition

        val method = PsiTreeUtil.getParentOfType(
            aClass.containingFile.findElementAt(editor.caretModel.offset),
            PsiMethod::class.java,
        )

        this.data = GenerateData(
            editor,
            position,
            method,
            editor.caretModel,
            chosenClass,
            chosenName,
            chosenChannels
        )

        return DUMMY_RESULT
    }

    override fun generateMemberPrototypes(
        psiClass: PsiClass,
        originalMember: ClassMember?
    ): Array<GenerationInfo>? {
        if (data == null) {
            return null
        }

        data?.let { data ->
            if (!psiClass.hasAnnotation(DataConstants.DATA_LISTENERS_ANNOTATION)) {
                psiClass.addAnnotation(DataConstants.DATA_LISTENERS_ANNOTATION)
            }

            data.model.moveToLogicalPosition(data.position)

            val newMethod =
                generateEventListenerMethod(
                    data.editor.project!!,
                    data.chosenClass,
                    data.chosenName,
                    data.chosenChannels
                )

            if (newMethod != null) {
                val info = PsiGenerationInfo(newMethod)
                info.positionCaret(data.editor, true)
                if (data.method != null) {
                    info.insert(psiClass, data.method, false)
                }

                return arrayOf(info)
            }
        }

        return null
    }

    private fun generateEventListenerMethod(
        project: Project,
        chosenClass: PsiClass,
        chosenName: String,
        chosenChannels: List<String>,
    ): PsiMethod? {
        val method = generateRedisStyleEventListenerMethod(
            chosenClass,
            chosenName,
            project,
            DataConstants.DATA_LISTENER_ANNOTATION,
            chosenChannels,
        ) ?: return null

        return method
    }

    private fun isSuperRedisEventAllowed(eventClass: PsiClass): Boolean {
        val redisClass = JavaPsiFacade.getInstance(eventClass.project)
            .findClass(DataConstants.DATA_REDIS_EVENT, eventClass.resolveScope) ?: return false

        return eventClass.isInheritor(redisClass, true)
    }

    companion object {
        private val DUMMY_RESULT =
            // cannot return empty array, but this result won't be used anyway
            arrayOfNulls<ClassMember>(1).castNotNull()

        fun generateRedisStyleEventListenerMethod(
            chosenClass: PsiClass,
            chosenName: String,
            project: Project,
            annotationName: String,
            channels: List<String>,
        ): PsiMethod? {
            val newMethod =
                createVoidMethodWithParameterType(project, chosenName, chosenClass) ?: return null
            val modifierList = newMethod.modifierList
            val annotation = modifierList.addAnnotation(annotationName)

            if (channels.isNotEmpty()) {
                val channelBuilder = StringBuilder()
                channelBuilder.append("{")
                for ((index, s) in channels.withIndex()) {
                    channelBuilder.append("\"$s\"")
                    if (index != channels.size - 1) {
                        channelBuilder.append(", ")
                    }
                }
                channelBuilder.append("}")

                val channelsParam = JavaPsiFacade.getElementFactory(project)
                    .createExpressionFromText(channelBuilder.toString(), annotation)
                annotation.setDeclaredAttributeValue("channels", channelsParam)
            }

            return newMethod
        }

        private fun createVoidMethodWithParameterType(
            project: Project,
            name: String,
            paramType: PsiClass
        ): PsiMethod? {
            val newMethod =
                JavaPsiFacade.getElementFactory(project).createMethod(name, PsiTypes.voidType())

            val list = newMethod.parameterList
            val qName = paramType.qualifiedName ?: return null
            val parameter = JavaPsiFacade.getElementFactory(project)
                .createParameter(
                    "event",
                    PsiClassType.getTypeByName(qName, project, GlobalSearchScope.allScope(project)),
                )
            list.add(parameter)

            return newMethod
        }

    }

}

private data class GenerateData(
    var editor: Editor,
    var position: LogicalPosition,
    var method: PsiMethod?,
    var model: CaretModel,
    var chosenClass: PsiClass,
    var chosenName: String,
    var chosenChannels: List<String>,
)