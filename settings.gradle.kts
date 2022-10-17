import java.nio.file.Files
import java.nio.file.Path

rootProject.name = "Bitrune Scripts"

include("game-plugins")
includePlugins(project(":game-plugins").projectDir.toPath())

pluginManagement {
    plugins {
        kotlin("jvm") version "1.6.0"
    }
}

fun includePlugins(pluginPath: Path) {
    Files.walk(pluginPath).forEach {
        if (!Files.isDirectory(it)) {
            return@forEach
        }
        searchPlugin(pluginPath, it)
    }
}

fun searchPlugin(parent: Path, path: Path) {
    val hasBuildFile = Files.exists(path.resolve("build.gradle.kts"))
    if (!hasBuildFile) {
        return
    }
    val relativePath = parent.relativize(path)
    val pluginName = relativePath.toString().replace(File.separator, ":")

    include("game-plugins:$pluginName")
}
