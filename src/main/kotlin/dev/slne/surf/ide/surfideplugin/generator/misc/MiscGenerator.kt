package dev.slne.surf.ide.surfideplugin.generator.misc

import dev.slne.surf.ide.surfideplugin.MISC_CODE_STYLES_CONFIG_TEMPLATE
import dev.slne.surf.ide.surfideplugin.MISC_CODE_STYLES_PROJECT_TEMPLATE
import dev.slne.surf.ide.surfideplugin.MISC_INSPECTION_PROFILES_TEMPLATE
import dev.slne.surf.ide.surfideplugin.MISC_SPACE_TEMPLATE
import dev.slne.surf.ide.surfideplugin.util.dir
import dev.slne.surf.ide.surfideplugin.util.file
import java.io.File

internal fun File.buildMisc() {
    file(MISC_SPACE_TEMPLATE)

    dir(".idea") {
        dir("codeStyles") {
            file(MISC_CODE_STYLES_CONFIG_TEMPLATE)
            file(MISC_CODE_STYLES_PROJECT_TEMPLATE)
        }

        dir("inspectionProfiles") {
            file(MISC_INSPECTION_PROFILES_TEMPLATE)
        }
    }
}

internal fun generateMiscAttributes() = mapOf<String, Any>()
