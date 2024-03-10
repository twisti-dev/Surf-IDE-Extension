package dev.slne.surf.ide.surfideplugin.generator.bukkit

import dev.slne.surf.ide.surfideplugin.*
import dev.slne.surf.ide.surfideplugin.generator.api.toFullBasePackages
import dev.slne.surf.ide.surfideplugin.generator.core.generateCoreAttributes
import dev.slne.surf.ide.surfideplugin.util.*
import java.io.File

internal fun File.buildBukkit(
    basePackages: List<String>,
    withData: Boolean,
    filePrefix: String
) {
    packages(basePackages.toFullBasePackages()) {
        dir("bukkit") {
            file(
                BUKKIT_MAIN_TEMPLATE,
                filePrefix,
                false,
                generateBukkitAttributes(
                    basePackages,
                    "bukkit",
                    BUKKIT_MAIN_TEMPLATE,
                    filePrefix
                )
            )

            file(
                BUKKIT_LOADER_TEMPLATE,
                filePrefix,
                false,
                generateBukkitAttributes(
                    basePackages,
                    "bukkit",
                    BUKKIT_LOADER_TEMPLATE,
                    filePrefix
                )
            )

            file(
                BUKKIT_BOOTSTRAPPER_TEMPLATE,
                filePrefix,
                false,
                generateBukkitAttributes(
                    basePackages,
                    "bukkit",
                    BUKKIT_BOOTSTRAPPER_TEMPLATE,
                    filePrefix
                )
            )

            dir("instance") {
                file(
                    BUKKIT_INSTANCE_TEMPLATE,
                    filePrefix,
                    withData,
                    generateBukkitAttributes(
                        basePackages,
                        "bukkit.instance",
                        BUKKIT_INSTANCE_TEMPLATE,
                        filePrefix
                    )
                )
            }
        }
    }
}

internal fun generateBukkitAttributes(
    basePackages: List<String>,
    currentPackage: String,
    currentClassName: FileName,
    filePrefix: String
) = mapOf(
    *generateCoreAttributes(
        basePackages,
        currentPackage,
        currentClassName,
        filePrefix
    ).map { (k, v) -> k to v }.toTypedArray(),
    TemplateAttributes.BUKKIT_INSTANCE_NAME to BUKKIT_INSTANCE_TEMPLATE.displayFileName(filePrefix),
    TemplateAttributes.BUKKIT_MAIN_PACKAGE to basePackages.toBukkitBasePackages(),
    TemplateAttributes.BUKKIT_INSTANCE_PACKAGE to "${basePackages.toBukkitBasePackages()}.instance"
)