plugins {
    application
    idea

    kotlin("jvm") version "1.6.0"
    kotlin("plugin.lombok") version "1.6.0"

    id("io.freefair.lombok") version "6.3.0"
}

allprojects {
    group = rootProject.group
    version = rootProject.version

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.lombok")
    apply(plugin = "io.freefair.lombok")

    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        implementation(fileTree("${rootDir}\\libs"))

        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))

        // https://mvnrepository.com/artifact/io.github.classgraph/classgraph
        implementation("io.github.classgraph:classgraph:4.8.137")
        // https://mvnrepository.com/artifact/it.unimi.dsi/fastutil
        implementation("it.unimi.dsi:fastutil:8.5.6")
        // https://mvnrepository.com/artifact/com.google.guava/guava
        implementation("com.google.guava:guava:31.0.1-jre")
        // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
        implementation("com.fasterxml.jackson.core:jackson-databind:2.0.1")
        // https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jdk8
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.13.0")
        // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-paranamer
        implementation("com.fasterxml.jackson.module:jackson-module-paranamer:2.13.0")
        // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-parameter-names
        implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.13.0")
        // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
        // https://mvnrepository.com/artifact/io.insert-koin/koin-core-jvm
        implementation("io.insert-koin:koin-core-jvm:3.2.0-beta-1")

        implementation(group = "org.pf4j", name = "pf4j", version = "3.6.0") {
        }
    }

    tasks {
        val javaVersion = JavaVersion.VERSION_17.toString()
        val kotlinVersion = JavaVersion.VERSION_11.toString()

        compileJava {
            targetCompatibility = javaVersion
            sourceCompatibility = javaVersion
            options.encoding = "UTF-8"
        }
        compileKotlin {
            kotlinOptions {
                jvmTarget = kotlinVersion
                targetCompatibility = kotlinVersion
                sourceCompatibility = kotlinVersion
                freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
            }
        }
        jar {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}
