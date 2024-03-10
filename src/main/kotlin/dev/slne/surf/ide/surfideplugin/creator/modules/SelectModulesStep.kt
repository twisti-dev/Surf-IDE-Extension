package dev.slne.surf.ide.surfideplugin.creator.modules

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardBaseData
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindSelected
import dev.slne.surf.ide.surfideplugin.SurfModuleBuilder

class SelectModulesStep(
    parent: NewProjectWizardStep,
    private val moduleBuilder: SurfModuleBuilder
) :
    AbstractNewProjectWizardStep(parent),
    NewProjectWizardBaseData by parent as NewProjectWizardBaseData {

    init {
        moduleBuilder.withApi = propertyGraph.property(true)
        moduleBuilder.withCore = propertyGraph.property(true)
        moduleBuilder.withBukkit = propertyGraph.property(true)
        moduleBuilder.withVelocity = propertyGraph.property(true)

        moduleBuilder.withApi.afterChange {
            if (!it) {
                moduleBuilder.withCore.set(false)
                moduleBuilder.withBukkit.set(false)
                moduleBuilder.withVelocity.set(false)
            }
        }

        moduleBuilder.withCore.afterChange {
            if (!it) {
                moduleBuilder.withBukkit.set(false)
                moduleBuilder.withVelocity.set(false)
            }
        }
    }

    override fun setupUI(builder: Panel) {
        builder.group("Select modules: ") {
            row {
                checkBox("API")
                    .bindSelected(moduleBuilder.withApi)
                checkBox("Core")
                    .bindSelected(moduleBuilder.withCore)
                    .enabledIf(moduleBuilder.withApi)
                checkBox("Bukkit")
                    .bindSelected(moduleBuilder.withBukkit)
                    .enabledIf(moduleBuilder.withCore)
                checkBox("Velocity")
                    .bindSelected(moduleBuilder.withVelocity)
                    .enabledIf(moduleBuilder.withCore)
            }
        }
    }
}