package dev.slne.surf.ide.surfideplugin.creator.buildsystem

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardBaseData
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.externalSystem.service.ui.completion.TextCompletionComboBox
import com.intellij.openapi.externalSystem.service.ui.completion.TextCompletionComboBoxConverter
import com.intellij.openapi.observable.properties.GraphProperty
import com.intellij.openapi.observable.util.not
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.validation.*
import com.intellij.openapi.util.NlsContexts
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ValidationInfoBuilder
import com.intellij.ui.util.minimumWidth
import com.intellij.util.lang.JavaVersion
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import dev.slne.surf.ide.surfideplugin.SurfModuleBuilder
import org.gradle.util.GradleVersion
import org.jetbrains.plugins.gradle.frameworkSupport.buildscript.isGradleOlderThan
import org.jetbrains.plugins.gradle.jvmcompat.GradleJvmSupportMatrix
import org.jetbrains.plugins.gradle.settings.GradleDefaultProjectSettings
import org.jetbrains.plugins.gradle.util.GradleBundle

val JAVA_17 = JavaVersion.tryParse("17")

class BuildSystemPropertiesStep<ParentStep>(
    private val parent: ParentStep,
    private val moduleBuilder: SurfModuleBuilder
) :
    AbstractNewProjectWizardStep(parent) where ParentStep : NewProjectWizardStep, ParentStep : NewProjectWizardBaseData {

    private val gradleVersionsProperty = propertyGraph.lazyProperty { suggestGradleVersions() }
    private val autoSelectGradleVersionProperty =
        propertyGraph.lazyProperty { suggestAutoSelectGradleVersion() }

    private var autoSelectGradleVersion by autoSelectGradleVersionProperty

    init {
        moduleBuilder.groupId = propertyGraph.property("dev.slne.surf")
        moduleBuilder.artifactId = propertyGraph.lazyProperty(::suggestArtifactId)
        moduleBuilder.baseModuleName = propertyGraph.property("surf-plugin-name")
        moduleBuilder.fileProjectName = propertyGraph.property("SurfPlugin")
        moduleBuilder.gradleVersion = propertyGraph.lazyProperty { suggestGradleVersion() }

        moduleBuilder.artifactId.dependsOn(parent.nameProperty, ::suggestArtifactId)
    }

    private fun suggestGradleVersion(): String {
        val gradleVersion = org.jetbrains.plugins.gradle.util.suggestGradleVersion {
            withProject(context.project)
            withJavaVersionFilter(JAVA_17)
            withFilter { validateJdkCompatibility(it) }
            if (autoSelectGradleVersion) {
                dontCheckDefaultProjectSettingsVersion()
            }
        } ?: GradleVersion.current()
        return gradleVersion.version
    }

    private fun suggestGradleVersions(): List<String> {
        return GradleJvmSupportMatrix.getAllSupportedGradleVersionsByIdea()
            .filter { validateJdkCompatibility(it) }.map { it.version }
    }

    private fun suggestAutoSelectGradleVersion(): Boolean {
        return GradleDefaultProjectSettings.getInstance().gradleVersion == null
    }

    private fun validateJdkCompatibility(gradleVersion: GradleVersion): Boolean {
        return JAVA_17 == null || GradleJvmSupportMatrix.isSupported(gradleVersion, JAVA_17)
    }

    private fun suggestArtifactId() = parent.name

    override fun setupUI(builder: Panel) {
        builder.group("Module Properties") {
            row("Base Module Name:") {
                contextHelp(BASE_MODULE_NAME_HELP)
                textField()
                    .bindText(moduleBuilder.baseModuleName)
                    .columns(COLUMNS_MEDIUM)
                    .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                    .textValidation(CHECK_NON_EMPTY)
            }
            row("File Project Name:") {
                contextHelp(FILE_PROJECT_NAME_HELP)
                textField()
                    .bindText(moduleBuilder.fileProjectName)
                    .columns(COLUMNS_MEDIUM)
                    .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                    .textValidation(CHECK_NON_EMPTY, CHECK_FILE_PROJECT_NAME, checkFileProjectNameWithData(moduleBuilder.surfDataDepend))
            }
        }
        builder.group("Build System Properties") {
            row("Group ID:") {
                textField()
                    .bindText(moduleBuilder.groupId)
                    .columns(COLUMNS_MEDIUM)
                    .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                    .textValidation(CHECK_NON_EMPTY, CHECK_GROUP_ID)
            }
            row("Artifact ID:") {
                textField()
                    .bindText(moduleBuilder.artifactId)
                    .columns(COLUMNS_MEDIUM)
                    .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                    .textValidation(CHECK_NON_EMPTY, CHECK_ARTIFACT_ID)
            }
        }

        builder.panel {
            row {
                label(GradleBundle.message("gradle.project.settings.distribution.wrapper.version.npw"))
                    .applyToComponent { minimumWidth = MINIMUM_LABEL_WIDTH }
                cell(
                    TextCompletionComboBox(
                        context.project,
                        TextCompletionComboBoxConverter.Default()
                    )
                )
                    .columns(8)
                    .applyToComponent { bindSelectedItem(moduleBuilder.gradleVersion) }
                    .applyToComponent { bindCompletionVariants(gradleVersionsProperty) }
                    .trimmedTextValidation(CHECK_NON_EMPTY)
                    .validationOnInput {
                        validateGradleVersion(
                            moduleBuilder.gradleVersion.get(),
                            withDialog = false
                        )
                    }
                    .validationOnApply {
                        validateGradleVersion(
                            moduleBuilder.gradleVersion.get(),
                            withDialog = true
                        )
                    }
                    .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                    .enabledIf(autoSelectGradleVersionProperty.not())
//                    .onApply { logGradleVersionFinished(gradleVersion) }
                checkBox(GradleBundle.message("gradle.project.settings.distribution.wrapper.version.auto.select"))
                    .bindSelected(autoSelectGradleVersionProperty)
            }
        }
    }

    private fun ValidationInfoBuilder.validateGradleVersion(
        rawGradleVersion: String,
        withDialog: Boolean
    ): ValidationInfo? {
        val gradleVersion = try {
            GradleVersion.version(rawGradleVersion)
        } catch (ex: IllegalArgumentException) {
            return error(ex.localizedMessage)
        }
        return validateIdeaGradleCompatibility(withDialog, gradleVersion)
            ?: validateJdkCompatibility(gradleVersion, withDialog)
            ?: validateGradleDslCompatibility(gradleVersion, withDialog)
    }

    private fun ValidationInfoBuilder.validateIdeaGradleCompatibility(
        withDialog: Boolean,
        gradleVersion: GradleVersion
    ): ValidationInfo? {
        if (GradleJvmSupportMatrix.isGradleSupportedByIdea(gradleVersion)) {
            return null
        }
        val oldestSupportedGradleVersion =
            GradleJvmSupportMatrix.getOldestSupportedGradleVersionByIdea()
        return errorWithDialog(
            withDialog = withDialog,
            message = GradleBundle.message(
                "gradle.settings.wizard.gradle.unsupported.message",
                ApplicationNamesInfo.getInstance().fullProductName,
                oldestSupportedGradleVersion.version
            ),
            dialogTitle = GradleBundle.message(
                "gradle.settings.wizard.gradle.unsupported.title"
            ),
            dialogMessage = GradleBundle.message(
                "gradle.settings.wizard.gradle.unsupported.message",
                ApplicationNamesInfo.getInstance().fullProductName,
                oldestSupportedGradleVersion.version,
            )
        )
    }

    private fun ValidationInfoBuilder.errorWithDialog(
        withDialog: Boolean, // dialog shouldn't be shown on text input
        message: @NlsContexts.DialogMessage String,
        dialogTitle: @NlsContexts.DialogTitle String,
        dialogMessage: @NlsContexts.DialogMessage String
    ): ValidationInfo {
        if (withDialog) {
            MessageDialogBuilder.okCancel(dialogTitle, dialogMessage)
                .icon(UIUtil.getErrorIcon())
                .ask(component)
        }
        return error(message)
    }

    private fun ValidationInfoBuilder.validateGradleDslCompatibility(
        gradleVersion: GradleVersion,
        withDialog: Boolean
    ): ValidationInfo? {
        val oldestCompatibleGradle = "4.0"
        if (gradleVersion.isGradleOlderThan(oldestCompatibleGradle)) {
            return validationWithDialog(
                withDialog = withDialog,
                message = GradleBundle.message(
                    "gradle.project.settings.kotlin.dsl.unsupported",
                    gradleVersion.version
                ),
                dialogTitle = GradleBundle.message(
                    "gradle.project.settings.kotlin.dsl.unsupported.title",
                    context.isCreatingNewProjectInt
                ),
                dialogMessage = GradleBundle.message(
                    "gradle.project.settings.kotlin.dsl.unsupported.message",
                    oldestCompatibleGradle,
                    gradleVersion.version
                )
            )
        }
        return null
    }

    protected fun ValidationInfoBuilder.validationWithDialog(
        withDialog: Boolean, // dialog shouldn't be shown on text input
        message: @NlsContexts.DialogMessage String,
        dialogTitle: @NlsContexts.DialogTitle String,
        dialogMessage: @NlsContexts.DialogMessage String
    ): ValidationInfo? {
        if (withDialog) {
            val isContinue = MessageDialogBuilder.yesNo(dialogTitle, dialogMessage)
                .icon(UIUtil.getWarningIcon())
                .ask(component)
            if (isContinue) {
                return null
            }
        }
        return error(message)
    }

    private fun ValidationInfoBuilder.validateJdkCompatibility(
        gradleVersion: GradleVersion,
        withDialog: Boolean
    ): ValidationInfo? {
        if (JAVA_17 == null || GradleJvmSupportMatrix.isSupported(
                gradleVersion,
                JAVA_17
            )
        ) return null
        return validationWithDialog(
            withDialog = withDialog,
            message = GradleBundle.message(
                "gradle.project.settings.distribution.version.unsupported",
                JAVA_17.toFeatureString(),
                gradleVersion.version
            ),
            dialogTitle = GradleBundle.message(
                "gradle.settings.wizard.unsupported.jdk.title",
                context.isCreatingNewProjectInt
            ),
            dialogMessage = GradleBundle.message(
                "gradle.settings.wizard.unsupported.jdk.message",
                GradleJvmSupportMatrix.suggestOldestSupportedJavaVersion(gradleVersion),
                GradleJvmSupportMatrix.suggestLatestSupportedJavaVersion(gradleVersion),
                JAVA_17.toFeatureString(),
                gradleVersion.version
            )
        )
    }
}

val CHECK_FILE_PROJECT_NAME: DialogValidation.WithParameter<() -> String> =
    validationErrorIf<String>(
        "File project name must be a valid Java identifier"
    ) {
        it.isEmpty() || !it.first().isUpperCase()
                || !it.all { char -> char.isLetterOrDigit() }
    }

fun checkFileProjectNameWithData(withData: GraphProperty<Boolean>): DialogValidation.WithParameter<() -> String> =
    validationErrorIf<String>(
        "File project name cannot be 'Surf' because it clashes with the '@SurfSpringApplication' annotation"
    ) { withData.get() && it == "Surf" }


const val BASE_MODULE_NAME_HELP = """
    The base module name is basically the prefix of each module name.
    <br>
    <br>
    For example, if the base module name is <code>surf-plugin</code>, the module names will be 
    <code>surf-plugin-api</code> and <code>surf-plugin-core</code etc.
"""

const val FILE_PROJECT_NAME_HELP = """
    The file project name is the name used for file names.
    <br>
    <br>
    For example, if the file project name is <code>Surf</code>, the instance will be named <code><b>Surf</b>Instance</code>
"""

private val MINIMUM_LABEL_WIDTH = JBUI.scale(120)