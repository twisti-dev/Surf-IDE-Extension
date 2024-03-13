package dev.slne.surf.ide.surfideplugin.util

fun <T> Array<T?>.castNotNull(): Array<T> {
    @Suppress("UNCHECKED_CAST")
    return this as Array<T>
}