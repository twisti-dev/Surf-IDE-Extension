package dev.slne.surf.ide.surfideplugin.generator.velocity

import dev.slne.surf.ide.surfideplugin.FileName
import dev.slne.surf.ide.surfideplugin.VELOCITY_INSTANCE_TEMPLATE
import dev.slne.surf.ide.surfideplugin.VELOCITY_MAIN_TEMPLATE
import dev.slne.surf.ide.surfideplugin.generator.api.toFullBasePackages
import dev.slne.surf.ide.surfideplugin.generator.core.generateCoreAttributes
import dev.slne.surf.ide.surfideplugin.util.TemplateAttributes
import dev.slne.surf.ide.surfideplugin.util.dir
import dev.slne.surf.ide.surfideplugin.util.file
import dev.slne.surf.ide.surfideplugin.util.packages
import java.io.File

internal fun File.buildVelocity(
    basePackages: List<String>,
    withData: Boolean,
    filePrefix: String,
    moduleName: String
) {
    packages(basePackages.toFullBasePackages()) {
        dir("velocity") {
            file(
                VELOCITY_MAIN_TEMPLATE,
                filePrefix,
                false,
                generateVelocityAttributes(
                    basePackages,
                    "velocity",
                    VELOCITY_MAIN_TEMPLATE,
                    filePrefix,
                    moduleName
                )
            )

            dir("instance") {
                file(
                    VELOCITY_INSTANCE_TEMPLATE,
                    filePrefix,
                    withData,
                    generateVelocityAttributes(
                        basePackages,
                        "velocity.instance",
                        VELOCITY_INSTANCE_TEMPLATE,
                        filePrefix,
                        moduleName
                    )
                )
            }
        }
    }
}

internal fun generateVelocityAttributes(
    basePackages: List<String>,
    currentPackage: String,
    currentClassName: FileName,
    filePrefix: String,
    moduleName: String
) = mapOf(
    *generateCoreAttributes(
        basePackages,
        currentPackage,
        currentClassName,
        filePrefix
    ).map { (k, v) -> k to v }.toTypedArray(),
    TemplateAttributes.VELOCITY_MODULE_NAME to moduleName,
    TemplateAttributes.VELOCITY_INSTANCE_NAME to VELOCITY_INSTANCE_TEMPLATE.displayFileName(filePrefix),
    TemplateAttributes.VELOCITY_MAIN_PACKAGE to "${basePackages.joinToString(".")}.velocity",
    TemplateAttributes.VELOCITY_INSTANCE_PACKAGE to "${basePackages.joinToString(".")}.velocity.instance"
)