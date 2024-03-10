package dev.slne.surf.ide.surfideplugin.old.creator

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.projectWizard.ProjectSettingsStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.AbstractNewProjectWizardBuilder
import com.intellij.ide.wizard.GitNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardBaseStep
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.RootNewProjectWizardStep
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import dev.slne.surf.ide.surfideplugin.asset.SurfAssets
import dev.slne.surf.ide.surfideplugin.old.creator.buildsystem.BuildSystemPropertiesStep
import dev.slne.surf.ide.surfideplugin.old.creator.step.DependStep
import java.io.File

const val SURF_BUILDER_ID = "SURF_MODULE_BUILDER"
class SurfModuleBuilder : AbstractNewProjectWizardBuilder() {
    override fun getPresentableName() = "Surf Plugin"
    override fun getNodeIcon() = SurfAssets.SURF_LOGO
    override fun getGroupName() = "Surf"
    override fun getParentGroup() = "Surf"
    override fun getBuilderId() = SURF_BUILDER_ID
    override fun getDescription() = "Create a new Surf plugin project"

    override fun createStep(context: WizardContext) = RootNewProjectWizardStep(context)
        .nextStep(::NewProjectWizardBaseStep)
        .nextStep(::GitNewProjectWizardStep) // TODO: 05.03.2024 15:12 - change to space
        // TODO: 05.03.2024 15:14 - select dependencies & optional core, bukkit and velocity part
        .nextStep(::BuildSystemPropertiesStep)
        .nextStep(::DependStep)

    override fun getIgnoredSteps() = listOf(ProjectSettingsStep::class.java)

    fun createAndGetRoot(): VirtualFile? {
        val path = contentEntryPath?.let { FileUtil.toSystemIndependentName(it) } ?: return null
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(File(path).apply { mkdirs() }.absolutePath)
    }
    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {

        if (moduleJdk != null) {
            modifiableRootModel.sdk = moduleJdk
        } else {
            modifiableRootModel.inheritSdk()
        }

        val root = createAndGetRoot() ?: return
        modifiableRootModel.addContentEntry(root)
        try {

            ApplicationManager.getApplication().runWriteAction {
                val manager = PsiManager.getInstance(modifiableRootModel.project)
                manager.findFile(root)?.add(
                    PsiDirectoryFactory.getInstance(manager.project)
                        .createDirectory(root.createChildDirectory(null, "webpack"))
                )
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }

        modifiableRootModel.project.backgroundTask("Creating project") {
            root.build {
                dir("src") {
                    dir("main") {
                        dir("kotlin") {
                            packages(listOf("dev", "slne", "surf", "plugin")) {
                                file("Main.kt", "Main.kt")
                            }
                        }
                        dir("resources")
                    }
                    dir("test") {
                        dir("kotlin")
                        dir("resources")
                    }
                }
            }

            root.refresh(false, true)
        }
    }
}

fun Project.backgroundTask(
    name: String,
    indeterminate: Boolean = true,
    cancellable: Boolean = false,
    background: Boolean = false,
    callback: (indicator: ProgressIndicator) -> Unit
) {
    ProgressManager.getInstance().run(object : Task.Backgroundable(this, name, cancellable, { background }) {
        override fun shouldStartInBackground() = background

        override fun run(indicator: ProgressIndicator) {
            try {
                if (indeterminate) indicator.isIndeterminate = true
                callback(indicator)
            } catch (e: Throwable) {
                e.printStackTrace()
                throw e
            }
        }
    })
}
fun VirtualFile.build(body: File.() -> Unit = {}) {
    File(this.path).body()
}

fun File.dir(name: String, body: File.() -> Unit = {}) {
    val file = File(this, name)
    if (!file.exists() || !file.isDirectory) {
        file.mkdirs()
    }
    file.body()
}

fun File.packages(packages: List<String>, body: File.() -> Unit = {}) {
    val file = File(this, packages.joinToString("/"))
    if (!file.exists() || !file.isDirectory) {
        file.mkdirs()
    }
    file.body()
}

fun File.file(
    name: String,
    templateName: String,
    attributes: Map<String, Any> = emptyMap(),
    binary: Boolean = false,
    executable: Boolean = false
) {
    val file = File(this, name)
    if (!file.exists()) {
        file.createNewFile()
    }
    if (!binary) {
        val data = getTemplateData(templateName, attributes)
        file.writeText(data)
    } else {
        val data = getBinaryData(templateName)
        file.writeBytes(data)
    }
    if (executable) {
        file.setExecutable(true)
    }
}

private fun getTemplateData(templateName: String, attributes: Map<String, Any> = emptyMap()): String {
    val template = FileTemplateManager
        .getDefaultInstance()
        .getInternalTemplate(templateName)
    return if (attributes.isEmpty()) {
        template.text
    } else {
        template.getText(attributes)
    }
}

private fun getBinaryData(templateName: String): ByteArray {
    SurfModuleBuilder::class.java.getResourceAsStream("/fileTemplates/internal/$templateName")
        .use { stream ->
            return stream!!.readBytes()
        }
}