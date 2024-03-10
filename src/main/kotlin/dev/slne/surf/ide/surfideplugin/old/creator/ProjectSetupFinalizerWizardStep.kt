package dev.slne.surf.ide.surfideplugin.old.creator

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.validation.DialogValidation
import com.intellij.openapi.ui.validation.WHEN_GRAPH_PROPAGATION_FINISHED
import com.intellij.ui.dsl.builder.Panel
import dev.slne.surf.ide.surfideplugin.old.util.mapFirstNotNull
import javax.swing.JPanel

class ProjectSetupFinalizerWizardStep(parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    private val finalizers: List<ProjectSetupFinalizer> by lazy {
        val factories = ProjectSetupFinalizer.EP_NAME.extensionList
        val result = mutableListOf<ProjectSetupFinalizer>()
        if (factories.isNotEmpty()) {
            var par: NewProjectWizardStep = this
            for (factory in factories) {
                val finalizer = factory.create(par)
                result += finalizer
                par = finalizer
            }
        }
        result
    }
    private val step by lazy {
        when (finalizers.size) {
            0 -> null
            1 -> finalizers[0]
            else -> {
                var step = finalizers[0].nextStep { finalizers[1] }
                for (i in 2 until finalizers.size) {
                    step = step.nextStep { finalizers[i] }
                }
                step
            }
        }
    }

    override fun setupUI(builder: Panel) {
        for (step in finalizers) {
            step.setupUI(builder)
        }
        if (finalizers.isNotEmpty()) {
            builder.row {
                cell(JPanel())
                    .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                    .validation(
                        DialogValidation {
                            finalizers.mapFirstNotNull(ProjectSetupFinalizer::validate)?.let(::ValidationInfo)
                        }
                    )
            }
        }
    }

    override fun setupProject(project: Project) {
        for (step in finalizers) {
            step.setupProject(project)
        }
    }
}

/**
 * A step applied after all other steps for all Minecraft project creators. These steps can also block project creation
 * by providing extra validations.
 *
 * To add custom project setup finalizers, register a [Factory] to the
 * `dev.slne.surf.ide.surf-ide-plugin.projectSetupFinalizer` extension point.
 */
interface ProjectSetupFinalizer : NewProjectWizardStep {
    companion object {
        val EP_NAME = ExtensionPointName<Factory>("dev.slne.surf.ide.surf-ide-plugin.projectSetupFinalizer")
    }

    /**
     * Validates the existing settings of this wizard.
     *
     * @return `null` if the settings are valid, or an error message if they are invalid.
     */
    fun validate(): String? = null

    interface Factory {
        fun create(parent: NewProjectWizardStep): ProjectSetupFinalizer
    }
}