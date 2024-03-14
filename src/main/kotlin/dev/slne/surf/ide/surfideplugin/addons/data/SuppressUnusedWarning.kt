package dev.slne.surf.ide.surfideplugin.addons.data

import com.intellij.codeInspection.reference.EntryPoint
import com.intellij.codeInspection.reference.RefElement
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import com.intellij.psi.PsiElement
import org.jdom.Element

class SuppressUnusedWarning : EntryPoint() {
    override fun getDisplayName() = "Data entry point"
    override fun isEntryPoint(refElement: RefElement, psiElement: PsiElement) = false
    override fun isEntryPoint(psiElement: PsiElement) = false
    override fun isSelected() = false
    override fun setSelected(selected: Boolean) {}
    override fun getIgnoreAnnotations() =
        arrayOf(DataConstants.DATA_LISTENER_ANNOTATION, DataConstants.DATA_LISTENERS_ANNOTATION)

    @Throws(InvalidDataException::class)
    override fun readExternal(element: Element) {
    }

    @Throws(WriteExternalException::class)
    override fun writeExternal(element: Element) {
    }
}