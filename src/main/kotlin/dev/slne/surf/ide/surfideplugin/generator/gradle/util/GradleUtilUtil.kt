package dev.slne.surf.ide.surfideplugin.generator.gradle.util

import com.intellij.openapi.externalSystem.model.ExternalSystemException
import org.gradle.util.GradleVersion
import org.gradle.wrapper.WrapperConfiguration
import org.gradle.wrapper.WrapperExecutor
import org.jetbrains.annotations.NotNull
import org.jetbrains.plugins.gradle.util.GradleLog
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


fun writeWrapperConfiguration(
    @NotNull targetPath: Path,
    @NotNull wrapperConfiguration: WrapperConfiguration
): Boolean {
    val wrapperProperties = Properties()
    setFromWrapperConfiguration(wrapperConfiguration, wrapperProperties)
    try {
        Files.newBufferedWriter(targetPath, StandardCharsets.ISO_8859_1).use { writer ->
            wrapperProperties.store(writer, null)
        }
    } catch (e: IOException) {
        GradleLog.LOG.warn(
            String.format(
                "I/O exception on writing Gradle wrapper properties into '%s'",
                targetPath.toAbsolutePath()
            ), e
        )
        return false
    }
    return true
}

fun setFromWrapperConfiguration(source: WrapperConfiguration, target: Properties) {
    source.apply {
        target[WrapperExecutor.DISTRIBUTION_URL_PROPERTY] = distribution.toString()
        target[WrapperExecutor.DISTRIBUTION_BASE_PROPERTY] = distributionBase
        target[WrapperExecutor.DISTRIBUTION_PATH_PROPERTY] = distributionPath
        target[WrapperExecutor.ZIP_STORE_BASE_PROPERTY] = zipBase
        target[WrapperExecutor.ZIP_STORE_PATH_PROPERTY] = zipPath
    }
}

fun getWrapperDistributionUri(gradleVersion: GradleVersion): URI {
    val distributionSource =
        if (gradleVersion.isSnapshot) "https://services.gradle.org/distributions-snapshots"
        else "https://services.gradle.org/distributions"

    try {
        return URI(String.format("%s/gradle-%s-bin.zip", distributionSource, gradleVersion.version))
    } catch (e: URISyntaxException) {
        throw ExternalSystemException(e)
    }
}