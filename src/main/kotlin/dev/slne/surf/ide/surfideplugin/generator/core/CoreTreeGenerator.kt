package dev.slne.surf.ide.surfideplugin.generator.core

import dev.slne.surf.ide.surfideplugin.CORE_API_ENDPOINT_TEMPLATE
import dev.slne.surf.ide.surfideplugin.CORE_INSTANCE_TEMPLATE
import dev.slne.surf.ide.surfideplugin.CORE_SPRING_APPLICATION_TEMPLATE
import dev.slne.surf.ide.surfideplugin.FileName
import dev.slne.surf.ide.surfideplugin.generator.api.generateApiAttributes
import dev.slne.surf.ide.surfideplugin.generator.api.toFullBasePackages
import dev.slne.surf.ide.surfideplugin.util.TemplateAttributes
import dev.slne.surf.ide.surfideplugin.util.dir
import dev.slne.surf.ide.surfideplugin.util.file
import dev.slne.surf.ide.surfideplugin.util.packages
import java.io.File

internal fun File.buildCore(
    basePackages: List<String>,
    withData: Boolean,
    filePrefix: String
) {
    packages(basePackages.toFullBasePackages()) {
        dir("core") {
            dir("instance") {
                file(
                    CORE_INSTANCE_TEMPLATE,
                    filePrefix,
                    withData,
                    generateCoreAttributes(
                        basePackages,
                        "core.instance",
                        CORE_INSTANCE_TEMPLATE,
                        filePrefix
                    )
                )
            }

            if (withData) {
                dir("spring") {
                    file(
                        CORE_SPRING_APPLICATION_TEMPLATE,
                        filePrefix,
                        true,
                        generateCoreAttributes(
                            basePackages,
                            "core.spring",
                            CORE_SPRING_APPLICATION_TEMPLATE,
                            filePrefix
                        )
                    )

                    dir("feign") {
                        file(
                            CORE_API_ENDPOINT_TEMPLATE,
                            filePrefix,
                            false,
                            generateCoreAttributes(
                                basePackages,
                                "core.spring.feign",
                                CORE_API_ENDPOINT_TEMPLATE,
                                filePrefix
                            )
                        )
                    }
                }
            }
        }
    }
}

internal fun generateCoreAttributes(
    basePackages: List<String>,
    currentPackage: String,
    currentClassName: FileName,
    filePrefix: String
) = mapOf(
    *generateApiAttributes(
        basePackages,
        currentPackage,
        currentClassName,
        filePrefix
    ).map { (k, v) -> k to v }.toTypedArray(),
    TemplateAttributes.CORE_INSTANCE_NAME to CORE_INSTANCE_TEMPLATE.displayFileName(filePrefix),
    TemplateAttributes.CORE_SPRING_APPLICATION_NAME to CORE_SPRING_APPLICATION_TEMPLATE.displayFileName(
        filePrefix
    ),
    TemplateAttributes.SPRING_BASE_PACKAGE to basePackages.joinToString(".") + ".core.spring",
    TemplateAttributes.CORE_INSTANCE_PACKAGE to "${basePackages.joinToString(".")}.core.instance"
)