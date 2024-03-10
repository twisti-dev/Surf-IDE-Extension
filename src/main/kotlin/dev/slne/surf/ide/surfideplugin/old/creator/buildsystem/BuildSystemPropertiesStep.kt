package dev.slne.surf.ide.surfideplugin.old.creator.buildsystem

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardBaseData
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.util.bindStorage
import com.intellij.openapi.ui.validation.*
import com.intellij.ui.dsl.builder.*
import dev.slne.surf.ide.surfideplugin.old.creator.storeToData
import dev.slne.surf.ide.surfideplugin.util.SemanticVersion


private val versionValidation = validationErrorIf<String>("Version must be a valid semantic version") {
    SemanticVersion.tryParse(it) == null
}

class BuildSystemPropertiesStep<ParentStep>(private val parent: ParentStep) :
    AbstractNewProjectWizardStep(parent) where ParentStep : NewProjectWizardStep, ParentStep : NewProjectWizardBaseData {

    val groupIdProperty = propertyGraph.property("dev.slne.surf")
        .bindStorage("${javaClass.name}.groupId")
    val artifactIdProperty = propertyGraph.lazyProperty(::suggestArtifactId)
    private val versionProperty = propertyGraph.property("1.0.0-SNAPSHOT")
        .bindStorage("${javaClass.name}.version")

    var groupId by groupIdProperty
    var artifactId by artifactIdProperty
    var version by versionProperty

    init {
        artifactIdProperty.dependsOn(parent.nameProperty, ::suggestArtifactId)
        storeToData()
    }

    private fun suggestArtifactId() = parent.name

    override fun setupUI(builder: Panel) {
        builder.group("Build System Properties") {
            row("Group ID:") {
                textField()
                    .bindText(groupIdProperty)
                    .columns(COLUMNS_MEDIUM)
                    .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                    .textValidation(CHECK_NON_EMPTY, CHECK_GROUP_ID)
            }
            row("Artifact ID:") {
                textField()
                    .bindText(artifactIdProperty)
                    .columns(COLUMNS_MEDIUM)
                    .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                    .textValidation(CHECK_NON_EMPTY, CHECK_ARTIFACT_ID)
            }
            row("Version:") {
                textField()
                    .bindText(versionProperty)
                    .columns(COLUMNS_MEDIUM)
                    .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                    .textValidation(versionValidation)
            }
        }
    }
}