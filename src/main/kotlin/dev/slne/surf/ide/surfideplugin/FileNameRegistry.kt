package dev.slne.surf.ide.surfideplugin

// @formatter:off
val API_TEMPLATE = FileName({ "${it}Api" })
val API_INSTANCE_TEMPLATE = FileName({ "${it}Instance" }, fileName = "ApiInstance")

val CORE_INSTANCE_TEMPLATE = FileName({ "Core${it}Instance" }, fileName = "CoreInstance")
val CORE_SPRING_APPLICATION_TEMPLATE = FileName({ "${it}SpringApplication" }, fileName = "CoreSpringApplication")
val CORE_API_ENDPOINT_TEMPLATE = FileName({ "${it}ApiEndpoint" }, fileName = "CoreApiEndpoint")

val BUKKIT_INSTANCE_TEMPLATE = FileName({ "Bukkit${it}Instance" }, fileName = "BukkitInstance")
val BUKKIT_MAIN_TEMPLATE = FileName({ "BukkitMain" })
val BUKKIT_LOADER_TEMPLATE = FileName({ "BukkitLoader" })
val BUKKIT_BOOTSTRAPPER_TEMPLATE = FileName({ "BukkitBootstrapper" })

val VELOCITY_INSTANCE_TEMPLATE = FileName({ "Velocity${it}Instance" }, fileName = "VelocityInstance")
val VELOCITY_MAIN_TEMPLATE = FileName({ "VelocityMain" })

val GRADLE_SETTINGS_TEMPLATE = gradleTemplate("Gradle Root ", "settings")
val GRADLE_BUILD_LOGIC_BUILD_TEMPLATE = gradleTemplate("Gradle Build Logic ", "build")
val GRADLE_BUILD_LOGIC_COMMON_CONVENTIONS_TEMPLATE = gradleTemplate("Gradle Build Logic ", "dev.slne.java-common-conventions")
val GRADLE_BUILD_LOGIC_LIBRARY_CONVENTIONS_TEMPLATE = gradleTemplate("Gradle Build Logic ", "dev.slne.java-library-conventions")
val GRADLE_BUILD_LOGIC_SHADOW_CONVENTIONS_TEMPLATE = gradleTemplate("Gradle Build Logic ", "dev.slne.java-shadow-conventions")
val GRADLE_LIBS_TEMPLATE = gradleTemplate("Gradle ", "libs", "versions.toml")
val GRADLE_API_BUILD_TEMPLATE = gradleTemplate("Gradle Api ", "build")
val GRADLE_CORE_BUILD_TEMPLATE = gradleTemplate("Gradle Core ", "build")
val GRADLE_BUKKIT_BUILD_TEMPLATE = gradleTemplate("Gradle Bukkit ", "build")
val GRADLE_VELOCITY_BUILD_TEMPLATE = gradleTemplate("Gradle Velocity ", "build")
// @formatter:on

data class FileName(
    val displayFileName: (prefix: String) -> String,
    val fileName: String = displayFileName(""),
    val fileEnding: String = "java"
) {
    val fullFileName = "$fileName.$fileEnding"
    fun fullFileName(prefix: String) = "${displayFileName(prefix)}.$fileEnding"
    fun getTemplateName(withData: Boolean) =
        fileName + (if (withData) "_data" else "") + ".$fileEnding"
}

private fun gradleTemplate(prefix: String, fileName: String, fileEnding: String = "gradle.kts") = FileName(
    displayFileName = { fileName },
    fileName = prefix + fileName,
    fileEnding = fileEnding
)