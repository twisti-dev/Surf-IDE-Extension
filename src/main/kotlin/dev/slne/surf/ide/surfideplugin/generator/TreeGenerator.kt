package dev.slne.surf.ide.surfideplugin.generator

import com.intellij.openapi.vfs.VirtualFile
import dev.slne.surf.ide.surfideplugin.FileName
import dev.slne.surf.ide.surfideplugin.generator.api.buildApi
import dev.slne.surf.ide.surfideplugin.generator.bukkit.buildBukkit
import dev.slne.surf.ide.surfideplugin.generator.core.buildCore
import dev.slne.surf.ide.surfideplugin.generator.gradle.buildBaseGradle
import dev.slne.surf.ide.surfideplugin.generator.velocity.buildVelocity
import dev.slne.surf.ide.surfideplugin.util.TemplateAttributes
import dev.slne.surf.ide.surfideplugin.util.build
import dev.slne.surf.ide.surfideplugin.util.dir

/**
 * Base class for building KVision project.
 * File name of template should look like: <project_type>_<directory>_<source_type>_<filename>.ft
 *  project_type - used only in backend depending files (that means: backend source dir, build.gradle)
 *  directory - options: js, jvm - if destination dir is root, leave blank
 *  source_type - options: source, resources, test - if none of this, leave blank
 *  filename - destined file name, for example for source code it would be "MainApp.kt"
 * Examples:
 *  - ktor_jvm_source_Main.kt.ft
 *  - ktor_jvm_resources_application.conf.ft
 *  - js_test_AppSpec.kt.ft
 * @constructor accepts arrays of file names to be generated - those are not template file names,
 * standard names like = Main.kt or application.conf. Based on them template file names are constructed
 */
class TreeGenerator(private val withData: Boolean, val withSurfApi: Boolean) {
    fun generate(
        root: VirtualFile,
//        artifactId: String,
//        groupId: String,
//        modules: List<String>,
//        initializers: List<String>,
        groupId: String,
        artifactId: String,
        version: String,
        dataVersion: String,
        apiVersion: String,
        withApi: Boolean = true,
        withCore: Boolean = true,
        withBukkit: Boolean = true,
        withVelocity: Boolean = true,
        baseModuleName: String,
        filePrefix: String
    ) {
        try {
//            GitIgnoreGenerator().generate(InitSettings())
            root.build {
                val basePackages = listOf(*groupId.split('.').toTypedArray(), artifactId)
                val apiModuleName = baseModuleName.toModuleName("api")
                val coreModuleName = baseModuleName.toModuleName("core")
                val bukkitModuleName = baseModuleName.toModuleName("bukkit")
                val velocityModuleName = baseModuleName.toModuleName("velocity")

                if (withApi) {
                    dir(apiModuleName) {
                        buildApi(basePackages, withData, filePrefix)
                    }
                }

                if (withCore) {
                    dir(coreModuleName) {
                        buildCore(basePackages, withData, filePrefix)
                    }
                }

                if (withBukkit) {
                    dir(bukkitModuleName) {
                        buildBukkit(basePackages, withData, filePrefix)
                    }
                }

                if (withVelocity) {
                    dir(velocityModuleName) {
                        buildVelocity(basePackages, withData, filePrefix, velocityModuleName)
                    }
                }

                buildBaseGradle(
                    basePackages,
                    withData,
                    withSurfApi,
                    dataVersion,
                    apiVersion,
                    groupId,
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


//                dir("src") {
//
//                    dir("jvmMain") {
//                        dir("kotlin") {
//                            packages(packageSegments) {
//                                jvmFiles.forEach { fileName ->
//                                    file(
//                                        fileName,
//                                        "${templateName}_jvm_source_$fileName",
//                                        attrs
//                                    )
//                                }
//                            }
//                        }
//                        dir("resources") {
//                            jvmResourcesFiles.forEach { fileName ->
//                                file(
//                                    fileName,
//                                    "${templateName}_jvm_resources_$fileName",
//                                    attrs
//                                )
//                            }
//                            if (jvmResourcesAssetsFiles.isNotEmpty()) {
//                                dir("assets") {
//                                    jvmResourcesAssetsFiles.forEach { fileName ->
//                                        file(
//                                            fileName,
//                                            "${templateName}_jvm_resources_assets_$fileName",
//                                            attrs
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    dir("commonMain") {
//                        dir("kotlin") {
//                            packages(packageSegments) {
//                                commonFiles.forEach { fileName ->
//                                    file(
//                                        fileName,
//                                        "common_$fileName",
//                                        attrs
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                    dir("jsMain") {
//                        dir("kotlin") {
//                            packages(packageSegments) {
//                                if (isFrontendOnly) {
//                                    jsSourceFrontendFiles.forEach { fileName ->
//                                        file(
//                                            fileName,
//                                            "js_source_frontend_$fileName",
//                                            attrs
//                                        )
//                                    }
//                                } else {
//                                    jsSourceFullstackFiles.forEach { fileName ->
//                                        file(
//                                            fileName,
//                                            "js_source_fullstack_$fileName",
//                                            attrs
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                        dir("web") {
//                            jsWebFiles.forEach { fileName ->
//                                file(
//                                    fileName,
//                                    "js_web_$fileName",
//                                    attrs
//                                )
//                            }
//
//                        }
//                        if (modules.contains("kvision-i18n")) {
//                            dir("resources") {
//                                dir("i18n") {
//                                    jsResourcesFiles.forEach { fileName ->
//                                        file(
//                                            fileName,
//                                            "js_resources_$fileName",
//                                            attrs
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    dir("jsTest") {
//                        dir("kotlin") {
//                            dir("test") {
//                                packages(packageSegments) {
//                                    jsTestFiles.forEach { fileName ->
//                                        file(
//                                            fileName,
//                                            "js_test_$fileName",
//                                            attrs
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                dir("gradle") {
//                    dir("wrapper") {
//                        gradleWrapperFiles.forEach { fileName ->
//                            file(
//                                fileName,
//                                "wrapper_$fileName",
//                                attrs,
//                                binary = true
//                            )
//                        }
//                    }
//                }
//                dir(".idea") {
//                    ideaFiles.forEach { fileName -> file(fileName, "idea_${fileName}", attrs) }
//                }
//                dir("webpack.config.d") {
//                    webpackFiles.forEach { fileName ->
//                        file(
//                            fileName,
//                            "webpack_${fileName}",
//                            attrs
//                        )
//                    }
//                }
//                gradleFile.forEach { fileName ->
//                    file(
//                        fileName,
//                        "${templateName}_${fileName}",
//                        attrs
//                    )
//                }
//                rootFiles.forEach { fileName ->
//                    file(
//                        fileName,
//                        fileName,
//                        attrs,
//                        binary = (fileName == "gradlew" || fileName == "gradlew.bat"),
//                        executable = (fileName == "gradlew")
//                    )
//                }
            }
            root.refresh(false, true)
        } catch (ex: Exception) {
            ex.printStackTrace()
            println(ex)
        }
    }

    private fun generateAttributes(
        currentPackageName: String,
        currentClassName: String,
        apiInstanceName: String,
    ): Map<String, Any> {
        return mapOf(
            TemplateAttributes.PACKAGE to currentPackageName,
            TemplateAttributes.CURRENT_CLASS_NAME to currentClassName,
            TemplateAttributes.API_INSTANCE_NAME to apiInstanceName
        )
    }
}

fun String.toModuleName(moduleName: String) =
    if (endsWith("-")) "$this$moduleName" else "$this-$moduleName"

internal fun generateCommonAttributes(
    basePackages: List<String>,
    currentPackage: String,
    currentClassName: FileName,
    filePrefix: String
) = mapOf(
    TemplateAttributes.PACKAGE to "${basePackages.joinToString(".")}.$currentPackage",
    TemplateAttributes.CURRENT_CLASS_NAME to currentClassName.displayFileName(filePrefix)
)
