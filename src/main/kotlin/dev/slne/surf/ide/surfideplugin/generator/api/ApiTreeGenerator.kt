package dev.slne.surf.ide.surfideplugin.generator.api

import dev.slne.surf.ide.surfideplugin.API_INSTANCE_TEMPLATE
import dev.slne.surf.ide.surfideplugin.API_TEMPLATE
import dev.slne.surf.ide.surfideplugin.FileName
import dev.slne.surf.ide.surfideplugin.generator.generateCommonAttributes
import dev.slne.surf.ide.surfideplugin.util.TemplateAttributes
import dev.slne.surf.ide.surfideplugin.util.dir
import dev.slne.surf.ide.surfideplugin.util.file
import dev.slne.surf.ide.surfideplugin.util.packages
import java.io.File

internal fun File.buildApi(
    basePackages: List<String>,
    withData: Boolean,
    filePrefix: String
) {
    packages(basePackages.toFullBasePackages()) {
        dir("api") {
            file(
                name = API_TEMPLATE.fullFileName(filePrefix),
                templateName = API_TEMPLATE.getTemplateName(withData),
                attributes = generateApiAttributes(basePackages, "api", API_TEMPLATE, filePrefix)
            )

            dir("instance") {
                file(
                    name = API_INSTANCE_TEMPLATE.fullFileName(filePrefix),
                    templateName = API_INSTANCE_TEMPLATE.getTemplateName(withData),
                    attributes = generateApiAttributes(
                        basePackages,
                        "api.instance",
                        API_INSTANCE_TEMPLATE,
                        filePrefix
                    )
                )
            }
        }
    }
}

internal fun generateApiAttributes(
    basePackages: List<String>,
    currentPackage: String,
    currentClassName: FileName,
    filePrefix: String
) = mapOf(
    *generateCommonAttributes(
        basePackages,
        currentPackage,
        currentClassName,
        filePrefix
    ).map { (k, v) -> k to v }.toTypedArray(),
    TemplateAttributes.API_INSTANCE_NAME to API_INSTANCE_TEMPLATE.displayFileName(filePrefix),
    TemplateAttributes.API_PACKAGE to "${basePackages.joinToString(".")}.api",
    TemplateAttributes.API_NAME to API_TEMPLATE.displayFileName(filePrefix)
)

fun List<String>.toFullBasePackages() = listOf("src", "main", "java", *this.toTypedArray())