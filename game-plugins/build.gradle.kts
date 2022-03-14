import org.jetbrains.kotlin.lombok.utils.capitalize
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

val rootPluginDir = projectDir
val rootPluginBuildDir = buildDir

val sharedProject = project(":game-plugins")
val sharedDir = sharedProject.projectDir
val generateDir =
    sharedDir.resolve("src").resolve("main").resolve("kotlin").resolve("org").resolve("apollo").resolve("utility")
        .resolve("constants")
        .resolve("generated")

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.tomlj:tomlj:1.0.0")
        classpath(fileTree("${rootDir}\\libs"))
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.6.0")
}

subprojects {
    group = "org.apollo.plugins"

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
        implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.6.0")
        implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.6.0")

        implementation(project(":game-plugins"))
    }

    tasks {
        jar {
            manifest {
                attributes(
                    "Plugin-Version" to "1.0.0",
                    "Plugin-Id" to nameToId(project.name)
                )
            }
        }
    }
}

tasks.register("generate_toml_constants") {
    dependsOn("list_jars")
    val resourceFiles = mutableMapOf<String, MutableSet<File>>()
    val dirtySuffix = mutableSetOf<String>()
    val projects: Set<Project> = allprojects
    projects.forEach { project ->
        populateResourceFiles(dirtySuffix, resourceFiles, project)
    }

    resourceFiles.keys.forEach { prefix ->
        if (!dirtySuffix.contains(prefix)) {
            return@forEach
        }
        generateToml(prefix, resourceFiles[prefix]!!)
    }
}

tasks {
    build {
        dependsOn("generate_toml_constants")
    }
}

tasks.register("list_jars") {
    var string = ""
    val paths = mutableSetOf<String>()
    subprojects.forEach { project ->
        printPaths(project, paths)
    }

    string += "paths = [\n"
    for ((index, path) in paths.withIndex()) {
        string += "\"${path.replace("\\", "/")}\""
        if (index != paths.size - 1) {
            string += ","
        }
        string += "\n"
    }
    string += "]"

    val pathsFile = sharedProject.sourceSets.main.get().resources.srcDirs.elementAt(0).resolve("paths.toml")
    pathsFile.writeText(string)

    println()
}

fun populateResourceFiles(
    dirtyPrefixes: MutableSet<String>,
    resourceFiles: MutableMap<String, MutableSet<File>>,
    project: Project
) {
    if (!generateDir.exists()) {
        generateDir.mkdir()
    }

    val pluginResourceFiles = project.sourceSets.main.get().resources.asFileTree
    if (pluginResourceFiles.isEmpty) return

    for (resourceFile in pluginResourceFiles) {
        if (resourceFile.extension != "toml" || resourceFile.name == "paths.toml") {
            continue
        }

        val suffix = findSuffix(resourceFile.nameWithoutExtension)
        val resourceFileGenerated =
            File(generateDir.path + File.separator + suffix.toUpperCaseAsciiOnly() + "Constants.kt")
        if (!resourceFileGenerated.exists() || resourceFile.lastModified() > resourceFileGenerated.lastModified()) {
            dirtyPrefixes += suffix
        }

        resourceFiles.getOrPut(suffix) { mutableSetOf() }.add(resourceFile)
    }
}

fun generateToml(suffix: String, resources: Set<File>) {
    if (resources.isEmpty()) return

    val suffixClassName = suffix.replace(".", "_").snakeToUpperCamelCase()
    val className = suffixClassName + "Constants"
    val resourceFile = File(generateDir.path + File.separator + className + ".kt")
    if (resourceFile.exists())
        resourceFile.delete()
    resourceFile.createNewFile()

    resourceFile.appendImports()
    resourceFile.startCompanion(className)

    val tomls = mutableSetOf<org.tomlj.TomlParseResult>()
    resources.forEach {
        val toml = org.tomlj.Toml.parse(it.reader())
        tomls.add(toml)

        val nameWithoutSuffix = it.nameWithoutExtension.removeExtensions().camelToSnakeCase().toUpperCaseAsciiOnly()
        if (toml.contains("name"))
            resourceFile.appendText(
                "\tval ${nameWithoutSuffix}_PROPERTY = ${suffixClassName}String(ApiGroupString(\"" + toml.get(
                    "name"
                ) + "\"))\n"
            )
        if (toml.contains("id"))
            resourceFile.appendText("\tval ${nameWithoutSuffix}_ID = ${suffixClassName}Int(ApiGroupInt(" + toml.get("id") + "))\n\n")
    }

    resourceFile.endCompanion()

    if (suffix == "if") {
        generateIf(resourceFile, tomls)
    }
}

fun generateIf(resourceFile: File, tomls: MutableSet<org.tomlj.TomlParseResult>) {
    val classes = mutableMapOf<String, MutableList<String>>()
    tomls.forEach {
        val className = (it["name"] as String).classCase() + "Components"
        val string = StringBuilder()

        val components = it["components"] as org.tomlj.TomlTable
        components.dottedKeySet().forEach { name ->
            val value = components[name]

            val constantName = name.toUpperCaseAsciiOnly()
            string.append("\tval ${constantName}_COMPONENT_KEY = NamedComponent(\"" + name + "\")\n")
            string.append("\tval ${constantName}_COMPONENT_ID = " + value + "\n\n")
        }

        classes.getOrPut(className) { mutableListOf() }.add(string.toString())
    }

    classes.forEach { (name, values) ->
        resourceFile.startCompanion(name)
        values.forEach {
            resourceFile.appendText(it)
        }
        resourceFile.endCompanion()
    }
}

fun File.appendImports() {
    appendText("package org.apollo.utility.constants.generated\n\n")
    appendText("import org.apollo.utility.constants.*\n")
}

fun File.startCompanion(className: String) {
    appendText("\n\nobject $className {\n\n")
}

fun File.endCompanion() {
    appendText("}")
}

fun findSuffix(name: String): String {
    val suffix = name.indexOf(".")
    return name.substring(suffix + 1)
}

fun String.removeExtensions(): String {
    val suffix = indexOf(".")
    return substring(0, suffix)
}

fun String.classCase(): String {
    return trim().capitalize().replace(" ", "_")
}

fun printPaths(project: Project, paths: MutableSet<String>) {
    if (project.buildDir.path.isEmpty()) return

    paths.add(project.buildDir.resolve("libs").path)
}

val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
val snakeRegex = "_[a-zA-Z]".toRegex()

// String extensions
fun String.camelToSnakeCase(): String {
    return camelRegex.replace(this) {
        "_${it.value}"
    }.toLowerCase()
}

fun String.snakeToLowerCamelCase(): String {
    return snakeRegex.replace(this) {
        it.value.replace("_", "")
            .toUpperCase()
    }
}

fun String.snakeToUpperCamelCase(): String {
    return this.snakeToLowerCamelCase().capitalize()
}

fun nameToId(name: String): Any {
    return name.replace("[^A-Za-z]".toRegex(), "").toLowerCase() + "-plugin"
}
