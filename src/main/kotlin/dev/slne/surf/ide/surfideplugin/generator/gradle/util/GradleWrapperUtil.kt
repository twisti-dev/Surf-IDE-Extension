@file:JvmName("GradleWrapperUtil")

package dev.slne.surf.ide.surfideplugin.generator.gradle.util

import com.intellij.ide.starters.local.StandardAssetsProvider
import com.intellij.ide.starters.local.generator.AssetsProcessor
import com.intellij.openapi.util.io.findOrCreateFile
import org.gradle.util.GradleVersion
import org.gradle.wrapper.WrapperConfiguration
import org.jetbrains.annotations.ApiStatus
import java.nio.file.Path

@ApiStatus.Internal
fun generateGradleWrapper(root: Path, gradleVersion: GradleVersion) {
    generateGradleWrapper(root, generateGradleWrapperConfiguration(gradleVersion))
}

private fun generateGradleWrapper(root: Path, configuration: WrapperConfiguration) {
    val propertiesLocation = StandardAssetsProvider().gradleWrapperPropertiesLocation
    val propertiesFile = root.findOrCreateFile(propertiesLocation)
    writeWrapperConfiguration(propertiesFile, configuration)
    val assets = StandardAssetsProvider().getGradlewAssets()
    AssetsProcessor.getInstance().generateSources(root, assets, emptyMap())
}

private fun generateGradleWrapperConfiguration(gradleVersion: GradleVersion) =
    WrapperConfiguration().apply {
        distribution = getWrapperDistributionUri(gradleVersion)
    }
