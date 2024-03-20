package dev.slne.surf.ide.surfideplugin.generator.gradle

import dev.slne.surf.ide.surfideplugin.*
import dev.slne.surf.ide.surfideplugin.util.*
import java.io.File


internal fun File.buildBaseGradle(
    basePackages: List<String>,
    dataDepend: Boolean,
    apiDepend: Boolean,
    dataVersion: String,
    apiVersion: String,
    groupID: String,
    version: String,
    baseModuleName: String,
    withApi: Boolean,
    apiModuleName: String,
    withCore: Boolean,
    coreModuleName: String,
    withBukkit: Boolean,
    bukkitModuleName: String,
    withVelocity: Boolean,
    velocityModuleName: String
) {
    fun generateBaseGradleAttributes() = generateBaseGradleAttributes(
        basePackages,
        apiDepend,
        dataDepend,
        dataVersion,
        apiVersion,
        groupID,
        version,
        baseModuleName,
        withApi,
        apiModuleName,
        withCore,
        coreModuleName,
        withBukkit,
        bukkitModuleName,
        withVelocity,
        velocityModuleName
    )

    file(
        fileName = GRADLE_SETTINGS_TEMPLATE,
        withData = false,
        attributes = generateBaseGradleAttributes()
    )

    dir("build-logic") {
        file(
            fileName = GRADLE_BUILD_LOGIC_BUILD_TEMPLATE,
            withData = false,
            attributes = generateBaseGradleAttributes()
        )

        packages(listOf("src", "main", "kotlin")) {
            file(
                fileName = GRADLE_BUILD_LOGIC_COMMON_CONVENTIONS_TEMPLATE,
                withData = false,
                attributes = generateBaseGradleAttributes()
            )

            file(
                fileName = GRADLE_BUILD_LOGIC_LIBRARY_CONVENTIONS_TEMPLATE,
                withData = false,
                attributes = generateBaseGradleAttributes()
            )

            file(
                fileName = GRADLE_BUILD_LOGIC_SHADOW_CONVENTIONS_TEMPLATE,
                withData = false,
                attributes = generateBaseGradleAttributes()
            )
        }
    }

    dir("gradle") {
        file(
            fileName = GRADLE_LIBS_TEMPLATE,
            withData = false,
            attributes = generateBaseGradleAttributes()
        )
    }

    if(withApi) {
        dir(apiModuleName) {
            file(
                fileName = GRADLE_API_BUILD_TEMPLATE,
                withData = false,
                attributes = generateBaseGradleAttributes()
            )
        }
    }

    if (withCore) {
        dir(coreModuleName) {
            file(
                fileName = GRADLE_CORE_BUILD_TEMPLATE,
                withData = false,
                attributes = generateBaseGradleAttributes()
            )
        }
    }

    if (withBukkit) {
        dir(bukkitModuleName) {
            file(
                fileName = GRADLE_BUKKIT_BUILD_TEMPLATE,
                withData = false,
                attributes = generateBaseGradleAttributes()
            )
        }
    }

    if (withVelocity) {
        dir(velocityModuleName) {
            file(
                fileName = GRADLE_VELOCITY_BUILD_TEMPLATE,
                withData = false,
                attributes = generateBaseGradleAttributes()
            )
        }
    }
}

internal fun generateBaseGradleAttributes(
    basePackages: List<String>,
    apiDepend: Boolean,
    dataDepend: Boolean,
    dataVersion: String,
    apiVersion: String,
    groupID: String,
    version: String,
    baseModuleName: String,
    withApi: Boolean,
    apiModuleName: String,
    withCore: Boolean,
    coreModuleName: String,
    withBukkit: Boolean,
    bukkitModuleName: String,
    withVelocity: Boolean,
    velocityModuleName: String
) = mapOf(
    TemplateAttributes.GRADLE_BASE_MODULE_NAME to if (baseModuleName.endsWith("-")) baseModuleName.dropLast(
        1
    ) else baseModuleName,
    TemplateAttributes.GRADLE_INCLUDE_API to if (!withApi) "" else "include(\":$apiModuleName\")",
    TemplateAttributes.GRADLE_INCLUDE_CORE to if (!withCore) "" else "include(\":$coreModuleName\")",
    TemplateAttributes.GRADLE_INCLUDE_BUKKIT to if (!withBukkit) "" else "include(\":$bukkitModuleName\")",
    TemplateAttributes.GRADLE_INCLUDE_VELOCITY to if (!withVelocity) "" else "include(\":$velocityModuleName\")",
    TemplateAttributes.GRADLE_PROJECT_GROUP to groupID,
    TemplateAttributes.GRADLE_PROJECT_VERSION to version,
    TemplateAttributes.WITH_API to apiDepend,
    TemplateAttributes.WITH_DATA to dataDepend,
    TemplateAttributes.DATA_VERSION to dataVersion,
    TemplateAttributes.API_VERSION to apiVersion,
    TemplateAttributes.GRADLE_API_MODULE_NAME to apiModuleName,
    TemplateAttributes.GRADLE_CORE_MODULE_NAME to coreModuleName,
    TemplateAttributes.GRADLE_BUKKIT_MODULE_NAME to bukkitModuleName,
    TemplateAttributes.GRADLE_VELOCITY_MODULE_NAME to velocityModuleName,
    TemplateAttributes.GRADLE_BUKKIT_MAIN_CLASS to basePackages.toBukkitBasePackages() + BUKKIT_MAIN_TEMPLATE.fileName,
    TemplateAttributes.GRADLE_BUKKIT_LOADER_CLASS to basePackages.toBukkitBasePackages() + BUKKIT_LOADER_TEMPLATE.fileName,
    TemplateAttributes.GRADLE_BUKKIT_BOOTSTRAPPER_CLASS to basePackages.toBukkitBasePackages() + BUKKIT_BOOTSTRAPPER_TEMPLATE.fileName
)