package dev.slne.surf.ide.surfideplugin.creator.depend

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardBaseData
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.properties.ObservableProperty
import com.intellij.openapi.observable.util.or
import com.intellij.openapi.ui.validation.CHECK_NO_WHITESPACES
import com.intellij.openapi.ui.validation.DialogValidation
import com.intellij.openapi.ui.validation.WHEN_GRAPH_PROPAGATION_FINISHED
import com.intellij.openapi.ui.validation.validationErrorIf
import com.intellij.ui.UIBundle
import com.intellij.ui.dsl.builder.*
import dev.slne.surf.ide.surfideplugin.SurfModuleBuilder

class SurfDependStep(parent: NewProjectWizardStep, private val moduleBuilder: SurfModuleBuilder) :
    AbstractNewProjectWizardStep(parent),
    NewProjectWizardBaseData by parent as NewProjectWizardBaseData {

    init {
        moduleBuilder.surfDataDepend = propertyGraph.property(true)
        moduleBuilder.surfDataVersion = propertyGraph.property("5.4.3-SNAPSHOT")

        moduleBuilder.surfApiDepend = propertyGraph.property(true)
        moduleBuilder.surfApiVersion = propertyGraph.property("1.0-SNAPSHOT")
    }

    override fun setupUI(builder: Panel) {
        builder.group("Depends on: ") {
            row {
                checkBox("SurfData")
                    .bindSelected(moduleBuilder.surfDataDepend)
                checkBox("SurfAPI")
                    .bindSelected(moduleBuilder.surfApiDepend)
            }
            group("Versions: ") {
                visibleIf(moduleBuilder.surfDataDepend or moduleBuilder.surfApiDepend)
                row("Surf Data: ") {
                    visibleIf(moduleBuilder.surfDataDepend)
                    textField()
                        .bindText(moduleBuilder.surfDataVersion)
                        .columns(COLUMNS_SHORT)
                        .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                        .textValidation(
                            CHECK_NO_WHITESPACES,
                            checkNonEmptyIfEnabled(moduleBuilder.surfDataDepend)
                        )
                }

                row("Surf API: ") {
                    visibleIf(moduleBuilder.surfApiDepend)
                    textField()
                        .bindText(moduleBuilder.surfApiVersion)
                        .columns(COLUMNS_SHORT)
                        .validationRequestor(WHEN_GRAPH_PROPAGATION_FINISHED(propertyGraph))
                        .textValidation(
                            CHECK_NO_WHITESPACES,
                            checkNonEmptyIfEnabled(moduleBuilder.surfApiDepend)
                        )
                }
            }
        }
    }
}

fun checkNonEmptyIfEnabled(property: ObservableProperty<Boolean>): DialogValidation.WithParameter<() -> String> =
    validationErrorIf<String>(
        UIBundle.message("kotlin.dsl.validation.missing.value")
    ) {
        it.isEmpty() && property.get()
    }