/*
 * Minecraft Development for IntelliJ
 *
 * https://mcdev.io/
 *
 * Copyright (C) 2024 minecraft-dev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, version 3.0 only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.slne.surf.ide.surfideplugin.old.creator.step


import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardBaseData.Companion.baseData
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.openapi.observable.util.bindBooleanStorage
import com.intellij.openapi.observable.util.bindStorage
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.ui.dsl.builder.*
import dev.slne.surf.ide.surfideplugin.old.creator.updateWhenChanged

abstract class AbstractOptionalStringStep(parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    protected abstract val label: String
    protected open val bindToStorage = false

    val valueProperty = propertyGraph.property("").apply {
        if (bindToStorage) {
            bindStorage("${this@AbstractOptionalStringStep.javaClass.name}.value")
        }
    }
    var value by valueProperty

    override fun setupUI(builder: Panel) {
        with(builder) {
            row(label) {
                textField()
                    .bindText(valueProperty)
                    .columns(COLUMNS_LARGE)
            }
        }
    }
}

abstract class AbstractOptionalStringBasedOnProjectNameStep(
    parent: NewProjectWizardStep,
) : AbstractOptionalStringStep(parent) {
    private val formatProperty = propertyGraph.property("").bindStorage("${javaClass.name}.format")
    var format by formatProperty

    init {
        if (format.isNotEmpty()) {
            value = suggestValue()
        }
        valueProperty.updateWhenChanged(formatProperty, ::suggestValue)
        valueProperty.updateWhenChanged(baseData!!.nameProperty, ::suggestValue)
        formatProperty.updateWhenChanged(valueProperty, ::suggestFormat)
    }

    private fun suggestValue() = format.replace(PROJECT_NAME_PLACEHOLDER, baseData!!.name)

    private fun suggestFormat(): String {
        val index = value.indexOf(baseData!!.name)
        if (index == -1) {
            return value
        }
        if (value.indexOf(baseData!!.name, startIndex = index + baseData!!.name.length) != -1) {
            // don't change format if there are multiple instances of the project name
            return format
        }
        return value.replace(baseData!!.name, PROJECT_NAME_PLACEHOLDER)
    }

    companion object {
        const val PROJECT_NAME_PLACEHOLDER = "{PROJECT_NAME}"
    }
}

class DescriptionStep(parent: NewProjectWizardStep) : AbstractOptionalStringStep(parent) {
    override val label = "Description:"

    override fun setupProject(project: Project) {
        data.putUserData(KEY, value)
    }

    companion object {
        val KEY = Key.create<String>("${DescriptionStep::class.java.name}.description")
    }
}

class AuthorsStep(parent: NewProjectWizardStep) : AbstractOptionalStringStep(parent) {
    override val label = "Authors:"
    override val bindToStorage = true

    override fun setupProject(project: Project) {
        data.putUserData(KEY, parseAuthors(value))
    }

    companion object {
        val KEY = Key.create<List<String>>("${AuthorsStep::class.java.name}.authors")

        private val bracketRegex = Regex("[\\[\\]]")
        private val commaRegex = Regex("\\s*,\\s*")

        fun parseAuthors(string: String): List<String> {
            return if (string.isNotBlank()) {
                string.trim().replace(bracketRegex, "").split(commaRegex).toList()
            } else {
                emptyList()
            }
        }
    }
}

class WebsiteStep(parent: NewProjectWizardStep) : AbstractOptionalStringStep(parent) {
    override val label = "Website:"
    override val bindToStorage = true

    override fun setupProject(project: Project) {
        data.putUserData(KEY, value)
    }

    companion object {
        val KEY = Key.create<String>("${WebsiteStep::class.java.name}.website")
    }
}


const val DEPENDS_ON_SURF_DATA = "surf-data"
const val DEPENDS_ON_SURF_API = "surf-api"
class DependStep(parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    private val surfDataDepend = propertyGraph.property(true)
        .bindBooleanStorage(DEPENDS_ON_SURF_DATA)
    private val surfApiDepend = propertyGraph.property(true)
        .bindBooleanStorage(DEPENDS_ON_SURF_API)

    override fun setupUI(builder: Panel) {
        builder.group("Depends on: ") {
            row {
                checkBox("surf-data")
                    .bindSelected(surfDataDepend)
                checkBox("surf-api")
                    .bindSelected(surfApiDepend)
            }
        }
    }

    override fun setupProject(project: Project) {
        data.putUserData(DATA_KEY, if (surfDataDepend.get()) listOf(DEPENDS_ON_SURF_DATA) else emptyList())
        data.putUserData(API_KEY, if (surfApiDepend.get()) listOf(DEPENDS_ON_SURF_API) else emptyList())
    }

    companion object {
        val KEY = Key.create<List<String>>("${DependStep::class.java.name}.depend")
        val DATA_KEY = Key.create<List<String>>("${DependStep::class.java.name}.depend.data")
        val API_KEY = Key.create<List<String>>("${DependStep::class.java.name}.depend.api")
    }
}
