package dev.slne.surf.ide.surfideplugin

import com.intellij.ide.projectWizard.ProjectSettingsStep
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.*
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import dev.slne.surf.ide.surfideplugin.creator.buildsystem.BuildSystemPropertiesStep
import dev.slne.surf.ide.surfideplugin.creator.depend.SurfDependStep
import dev.slne.surf.ide.surfideplugin.creator.modules.SelectModulesStep
import dev.slne.surf.ide.surfideplugin.generator.TreeGenerator
import dev.slne.surf.ide.surfideplugin.old.creator.backgroundTask
import java.io.File

const val ID = "TestModuleBuilder"

class SurfModuleBuilder : ModuleBuilder() {
    lateinit var surfDataDepend: GraphProperty<Boolean>
    lateinit var surfDataVersion: GraphProperty<String>

    lateinit var surfApiDepend: GraphProperty<Boolean>
    lateinit var surfApiVersion: GraphProperty<String>

    lateinit var withApi: GraphProperty<Boolean>
    lateinit var withCore: GraphProperty<Boolean>
    lateinit var withBukkit: GraphProperty<Boolean>
    lateinit var withVelocity: GraphProperty<Boolean>

    lateinit var groupId: GraphProperty<String>
    lateinit var artifactId: GraphProperty<String>
    lateinit var fileProjectName: GraphProperty<String>
    lateinit var baseModuleName: GraphProperty<String>

    lateinit var gradleVersion: GraphProperty<String>

    override fun getModuleType() = SurfModuleType()

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        if (moduleJdk != null) {
            modifiableRootModel.sdk = moduleJdk
        } else {
            modifiableRootModel.inheritSdk()
        }

        val root = createAndGetRoot() ?: return
        val generator = TreeGenerator(surfDataDepend.get(), surfApiDepend.get())

        modifiableRootModel.addContentEntry(root)
        modifiableRootModel.project.backgroundTask("Creating Plugin") {
            it.isIndeterminate = true
            generator.generate(
                modifiableRootModel.project,
                root,
                groupId.get(),
                artifactId.get(),
                "1.0.0-SNAPSHOT",
                surfDataVersion.get(),
                surfApiVersion.get(),
                withApi.get(),
                withCore.get(),
                withBukkit.get(),
                withVelocity.get(),
                baseModuleName.get(),
                fileProjectName.get(),
                gradleVersion.get()
            )
        }
    }

    private fun createAndGetRoot(): VirtualFile? {
        val path = contentEntryPath?.let { FileUtil.toSystemIndependentName(it) } ?: return null
        return LocalFileSystem.getInstance()
            .refreshAndFindFileByPath(File(path).apply { mkdirs() }.absolutePath)
    }

    override fun getCustomOptionsStep(
        context: WizardContext,
        parentDisposable: Disposable?
    ): ModuleWizardStep {
        return BridgeStep(
            NewProjectWizardStepPanel(
                RootNewProjectWizardStep(context)
                    .nextStep(::NewProjectWizardBaseStep)
                    .nextStep(::GitNewProjectWizardStep)
                    .nextStep { SurfDependStep(it, this) }
                    .nextStep { SelectModulesStep(it, this) }
                    .nextStep { BuildSystemPropertiesStep(it, this) }
            )
        )
    }

    override fun getIgnoredSteps() = listOf(ProjectSettingsStep::class.java)
}

private class BridgeStep(private val panel: NewProjectWizardStepPanel) :
    ModuleWizardStep(),
    NewProjectWizardStep by panel.step {

    override fun validate() = panel.validate()

    override fun updateDataModel() = panel.apply()

    override fun getPreferredFocusedComponent() = panel.getPreferredFocusedComponent()

    override fun getComponent() = panel.component
}