package dev.slne.surf.ide.surfideplugin.addons.data.generation.redisevent.wizard

import com.intellij.ide.highlighter.JavaHighlightingColors
import com.intellij.openapi.observable.properties.AtomicProperty
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.validation.CHECK_NON_EMPTY
import com.intellij.openapi.ui.validation.DialogValidation
import com.intellij.openapi.ui.validation.WHEN_TEXT_CHANGED
import com.intellij.openapi.ui.validation.validationErrorIf
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.textValidation
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue
import java.util.*

class RedisEventListenerWizardModern(private val className: String, defaultListenerName: String) {

    val chosenListenerName = property(defaultListenerName)
    val chosenChannels = property("")

    fun build(): DialogPanel {
        return panel {
            row {
                @Suppress("DialogTitleCapitalization")
                label("public void").applyToComponent {
                    foreground = JBColor.isBright()
                        .ifTrue { JavaHighlightingColors.KEYWORD.fallbackAttributeKey!!.defaultAttributes.foregroundColor }
                        ?: JavaHighlightingColors.KEYWORD.defaultAttributes.foregroundColor
                }

                textField()
                    .bindText(chosenListenerName)
                    .validationRequestor(WHEN_TEXT_CHANGED)
                    .textValidation(CHECK_NON_EMPTY, CHECK_METHOD_NAME)
            }

            row("Channels") {
                contextHelp("Comma separated list of channels. E.g. 'channel1, channel2'")
                textField()
                    .bindText(chosenChannels)
                    .validationRequestor(WHEN_TEXT_CHANGED)
                    .textValidation(CHECK_CHANNELS_SPLITTED_CORRECTLY)
            }
        }
    }
}

fun <T> property(initialValue: T): ObservableMutableProperty<T> {
    return AtomicProperty(initialValue)
}

val CHECK_METHOD_NAME: DialogValidation.WithParameter<() -> String> =
    validationErrorIf<String>("Invalid method name") {
        it.isEmpty() || it.contains(" ") || JavaKeyword.values()
            .map { it.toString() }.contains(it)
    }

val CHECK_CHANNELS_SPLITTED_CORRECTLY: DialogValidation.WithParameter<() -> String> =
    validationErrorIf<String>("Invalid channels list") { input ->
        input.endsWith(",")
                || input.endsWith(", ")
                || input.startsWith(",")
                || input.startsWith(", ")
                || input.split(",").map { it.trim() }.any { it.isEmpty() }
    }

enum class JavaKeyword {
    ABSTRACT,
    BOOLEAN,
    BREAK,
    CASE,
    CATCH,
    CHAR,
    CLASS,
    CONST,
    CONTINUE,
    DOUBLE,
    ELSE,
    EXTENDS,
    FINAL,
    FINALLY,
    FLOAT,
    FOR,
    IF,
    IMPLEMENTS,
    IMPORT,
    INSTANCEOF,
    INT,
    INTERFACE,
    LONG,
    NEW,
    PRIVATE,
    PROTECTED,
    PUBLIC,
    RETURN,
    STATIC,
    SUPER,
    SWITCH,
    THIS,
    THROW,
    THROWS,
    TRY,
    VOID,
    WHILE,
    TRUE,
    FALSE,
    NULL;

    override fun toString() = name.lowercase(Locale.getDefault())
}
