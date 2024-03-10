package dev.slne.surf.ide.surfideplugin

import com.intellij.openapi.module.ModuleType
import dev.slne.surf.ide.surfideplugin.asset.SurfAssets
import javax.swing.Icon

class SurfModuleType : ModuleType<SurfModuleBuilder>(ID) {
    override fun createModuleBuilder() = SurfModuleBuilder()
    override fun getName() = "Surf Module"
    override fun getDescription() = "Create a new Surf plugin project"
    override fun getNodeIcon(isOpened: Boolean) = SurfAssets.SURF_LOGO
}