package dev.slne.surf.ide.surfideplugin.addons.data.generation.redisevent

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.DialogWrapper
import dev.slne.surf.ide.surfideplugin.addons.data.generation.redisevent.wizard.RedisEventListenerWizard

class GenerateRedisEventListenerDialog(
    editor: Editor,
    className: String,
    defaultListenerName: String
) :
    DialogWrapper(editor.project, editor.component, false, IdeModalityType.IDE) {

    private val wizard: RedisEventListenerWizard =
        RedisEventListenerWizard(null, className, defaultListenerName)

    init {
        title = "Generate Redis Event Listener"
        isOKActionEnabled = true
        setValidationDelay(0)

        init()
    }


    override fun createCenterPanel() = wizard.panel

    val chosenName: String
        get() = wizard.chosenClassName
}

