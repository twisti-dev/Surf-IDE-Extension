package dev.slne.surf.ide.surfideplugin.util

import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import dev.slne.surf.ide.surfideplugin.FileName
import dev.slne.surf.ide.surfideplugin.SurfModuleBuilder
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction
import java.io.File

fun File.dir(name: String, body: File.() -> Unit = {}) {
    val file = File(this, name)
    if (!file.exists() || !file.isDirectory) {
        file.mkdirs()
    }
    file.body()
}

fun File.packages(packages: List<String>, body: File.() -> Unit = {}) {
    val file = File(this, packages.joinToString(File.separator))
    if (!file.exists() || !file.isDirectory) {
        file.mkdirs()
    }
    file.body()
}

fun VirtualFile.build(body: File.() -> Unit = {}) {
    File(this.path).body()
}


fun File.file(
    fileName: FileName,
    filePrefix: String = "",
    withData: Boolean = false,
    attributes: Map<String, Any> = emptyMap(),
    binary: Boolean = false,
    executable: Boolean = false
) {
    file(
        name = fileName.fullFileName(filePrefix),
        templateName = fileName.getTemplateName(withData),
        attributes = attributes,
        binary = binary,
        executable = executable
    )
}

fun File.file(
    name: String,
    templateName: String,
    attributes: Map<String, Any> = emptyMap(),
    binary: Boolean = false,
    executable: Boolean = false
) {
    val file = File(this, name)
    if (!file.exists()) {
        file.createNewFile()
    }
    if (!binary) {
        val data = getTemplateData(templateName, attributes)
        file.writeText(data)
    } else {
        val data = getBinaryData(templateName)
        file.writeBytes(data)
    }
    if (executable) {
        file.setExecutable(true)
    }
}

private fun getTemplateData(templateName: String, attributes: Map<String, Any> = emptyMap()): String {
    val template = FileTemplateManager
        .getDefaultInstance()
        .getInternalTemplate(templateName)
    return if (attributes.isEmpty()) {
        template.text
    } else {
        template.getText(attributes)
    }
}

private fun getBinaryData(templateName: String): ByteArray {
    SurfModuleBuilder::class.java.getResourceAsStream("/fileTemplates/internal/$templateName")
        .use { stream ->
            return stream!!.readBytes()
        }
}

fun Project.runGradle(command: String) {
    GradleExecuteTaskAction.runGradle(this, DefaultRunExecutor.getRunExecutorInstance(), this.basePath!!, command)
}

fun Project.getRootFile(): VirtualFile? {
    return projectFile?.parent?.parent
}

fun String.insertAfter(after: Regex, insert: String): String {
    val last = after.find(this)?.range?.last
    return if (last != null) {
        buildString {
            append(this.substring(0, last + 1))
            appendLine(insert)
            appendLine(this.substring(last + 1))
        }
    } else {
        this
    }

}

fun List<String>.toBukkitBasePackages(): String {
    return this.joinToString(".") + ".bukkit"
}

object TemplateAttributes {
    const val PACKAGE = "PACKAGE"
    const val CURRENT_CLASS_NAME = "CLASS_NAME"

    const val API_NAME = "API_NAME"
    const val API_INSTANCE_NAME = "API_INSTANCE_NAME"
    const val API_PACKAGE = "API_PACKAGE"

    const val CORE_INSTANCE_NAME = "CORE_INSTANCE_NAME"
    const val CORE_INSTANCE_PACKAGE = "CORE_INSTANCE_PACKAGE"
    const val CORE_SPRING_APPLICATION_NAME = "CORE_SPRING_APPLICATION_NAME"
    const val SPRING_BASE_PACKAGE = "SPRING_BASE_PACKAGE"

    const val BUKKIT_INSTANCE_NAME = "BUKKIT_INSTANCE_NAME"
    const val BUKKIT_INSTANCE_PACKAGE = "BUKKIT_INSTANCE_PACKAGE"
    const val BUKKIT_MAIN_PACKAGE = "BUKKIT_MAIN_PACKAGE"

    const val VELOCITY_MODULE_NAME = "VELOCITY_MODULE_NAME"
    const val VELOCITY_INSTANCE_NAME = "VELOCITY_INSTANCE_NAME"
    const val VELOCITY_INSTANCE_PACKAGE = "VELOCITY_INSTANCE_PACKAGE"
    const val VELOCITY_MAIN_PACKAGE = "VELOCITY_MAIN_PACKAGE"

    const val GRADLE_BASE_MODULE_NAME = "GRADLE_BASE_MODULE_NAME"
    const val GRADLE_INCLUDE_API = "GRADLE_INCLUDE_API"
    const val GRADLE_INCLUDE_CORE = "GRADLE_INCLUDE_CORE"
    const val GRADLE_INCLUDE_BUKKIT = "GRADLE_INCLUDE_BUKKIT"
    const val GRADLE_INCLUDE_VELOCITY = "GRADLE_INCLUDE_VELOCITY"
    const val GRADLE_PROJECT_GROUP = "GRADLE_PROJECT_GROUP"
    const val GRADLE_PROJECT_VERSION = "GRADLE_PROJECT_VERSION"
    const val GRADLE_API_MODULE_NAME = "GRADLE_API_MODULE_NAME"
    const val GRADLE_CORE_MODULE_NAME = "GRADLE_CORE_MODULE_NAME"
    const val GRADLE_BUKKIT_MODULE_NAME = "GRADLE_BUKKIT_MODULE_NAME"
    const val GRADLE_VELOCITY_MODULE_NAME = "GRADLE_VELOCITY_MODULE_NAME"
    const val GRADLE_BUKKIT_MAIN_CLASS = "GRADLE_BUKKIT_MAIN_CLASS"
    const val GRADLE_BUKKIT_LOADER_CLASS = "GRADLE_BUKKIT_LOADER_CLASS"
    const val GRADLE_BUKKIT_BOOTSTRAPPER_CLASS = "GRADLE_BUKKIT_BOOTSTRAPPER_CLASS"

    const val WITH_DATA = "WITH_DATA"
    const val WITH_API = "WITH_API"
    const val DATA_VERSION = "DATA_VERSION"
    const val API_VERSION = "API_VERSION"

    // Optionals
    const val PLUGIN_DESCRIPTION = "PLUGIN_DESCRIPTION"
    const val PLUGIN_WEBSITE = "PLUGIN_WEBSITE"
    const val PLUGIN_AUTHORS = "PLUGIN_AUTHORS"
}