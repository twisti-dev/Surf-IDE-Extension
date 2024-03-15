package dev.slne.surf.ide.surfideplugin.addons.data.generation.redisevent

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.DialogWrapper
import dev.slne.surf.ide.surfideplugin.addons.data.generation.redisevent.wizard.RedisEventListenerWizardModern

class GenerateRedisEventListenerDialog(
    editor: Editor,
    className: String,
    defaultListenerName: String
) :
    DialogWrapper(editor.project, editor.component, false, IdeModalityType.IDE) {

    private val wizard: RedisEventListenerWizardModern =
        RedisEventListenerWizardModern(className, defaultListenerName)

    init {
        title = "Redis Event Listener Settings"
        isOKActionEnabled = true
        setValidationDelay(0)

        init()
    }


    override fun createCenterPanel() = wizard.build()

    val chosenListenerName by wizard.chosenListenerName
    val chosenChannels by wizard.chosenChannels
}

