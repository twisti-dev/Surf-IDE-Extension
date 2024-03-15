package dev.slne.surf.ide.surfideplugin.addons.data.generation.redisevent.wizard

import com.intellij.ide.highlighter.JavaHighlightingColors
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy
import com.intellij.ui.JBColor
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.util.ui.UIUtil
import javax.swing.*

class RedisEventListenerWizard(panel: JPanel?, className: String, defaultListenerName: String) {
    lateinit var panel: JPanel
    private lateinit var classNameTextField: JTextField
    private lateinit var listenerNameTextField: JTextField
    private lateinit var publicVoidLabel: JLabel
    private lateinit var contentPanel: JPanel
    private lateinit var separator: JSeparator
    private lateinit var choosenChannels: JFormattedTextField
    private lateinit var channelsLabel: JLabel

    init {
        classNameTextField.font = EditorUtil.getEditorFont()
        listenerNameTextField.font = EditorUtil.getEditorFont()
        publicVoidLabel.font = EditorUtil.getEditorFont()

        if (!JBColor.isBright()) {
            val foregroundColor = JavaHighlightingColors.KEYWORD.defaultAttributes.foregroundColor

            publicVoidLabel.foreground = foregroundColor
//            channelsLabel.foreground = foregroundColor
        } else {
            val foregroundColor =
                JavaHighlightingColors.KEYWORD.fallbackAttributeKey!!.defaultAttributes.foregroundColor

            publicVoidLabel.foreground = foregroundColor
//            channelsLabel.foreground = foregroundColor
        }

        if (panel != null) {
            separator.isVisible = true
            contentPanel.add(panel, innerContentPanelConstraints)
        }

        classNameTextField.text = className
        listenerNameTextField.text = defaultListenerName

        IdeFocusTraversalPolicy.getPreferredFocusedComponent(listenerNameTextField).requestFocus()
        listenerNameTextField.requestFocus()

        choosenChannels.font = EditorUtil.getEditorFont()
        choosenChannels.toolTipText = "Comma separated list of channels"
    }

    val chosenClassName: String
        get() = listenerNameTextField.text

    val chosenChannels: String
        get() = choosenChannels.text

    companion object {
        private val innerContentPanelConstraints = GridConstraints()

        init {
            innerContentPanelConstraints.row = 0
            innerContentPanelConstraints.column = 0
            innerContentPanelConstraints.rowSpan = 1
            innerContentPanelConstraints.colSpan = 1
            innerContentPanelConstraints.anchor = GridConstraints.ANCHOR_CENTER
            innerContentPanelConstraints.fill = GridConstraints.FILL_BOTH
            innerContentPanelConstraints.hSizePolicy = GridConstraints.SIZEPOLICY_FIXED
            innerContentPanelConstraints.vSizePolicy = GridConstraints.SIZEPOLICY_FIXED
        }
    }
}