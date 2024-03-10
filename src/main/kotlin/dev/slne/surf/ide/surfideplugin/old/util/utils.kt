package dev.slne.surf.ide.surfideplugin.old.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState

/**
 * Splits a string into the longest prefix matching a predicate and the corresponding suffix *not* matching.
 *
 * Note: Name inspired by Scala.
 */
inline fun String.span(predicate: (Char) -> Boolean): Pair<String, String> {
    val prefix = takeWhile(predicate)
    return prefix to drop(prefix.length)
}

inline fun <T, R> Iterable<T>.mapFirstNotNull(transform: (T) -> R?): R? {
    forEach { element -> transform(element)?.let { return it } }
    return null
}

fun invokeLater(func: () -> Unit) {
    ApplicationManager.getApplication().invokeLater(func, ModalityState.defaultModalityState())
}