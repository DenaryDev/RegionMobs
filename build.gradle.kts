import net.minecrell.pluginyml.paper.PaperPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    id("idea")
    id("com.diffplug.spotless") version "8.7.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("de.eldoria.plugin-yml.paper") version "0.9.0"
    id("com.github.gmazzo.buildconfig") version "6.0.10"
    id("org.ajoberstar.grgit") version "5.3.2"
}

group = "me.denarydev"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "PaperMC" }
    maven("https://repo.prostocraft.ru/private/") {
        name = "prostocraft"
        credentials(PasswordCredentials::class)
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")

    val crystalVersion = "3.0.build.+"
    library("me.denarydev.crystal:crystal-paper:$crystalVersion")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

paper {
    author = "DenaryDev"

    main = "me.denarydev.regionmobs.RegionMobsPlugin"
    bootstrapper = "me.denarydev.regionmobs.RegionMobsBootstrap"

    apiVersion = "26.1"

    serverDependencies {
        register("Crystal") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

spotless {
    java {
        target("**/me/denarydev/regionmobs/**")

        licenseHeaderFile(rootProject.file("HEADER"))

        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
        endWithNewline()
    }
}

buildConfig {
    packageName("me.denarydev.regionmobs")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
    buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")
    buildConfigField("String", "GIT_BRANCH", "\"${grgit.branch.current().name}\"")
    buildConfigField("String", "GIT_COMMIT", "\"${shortCommit()}\"")
}

fun shortCommit(): String {
    val clean = grgit.status().isClean
    val commit = grgit.head().abbreviatedId
    return commit + (if (clean) "" else "-dirty")
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.release = 25
    }

    withType<ProcessResources>().configureEach {
        filteringCharset = Charsets.UTF_8.name()
    }

    compileJava {
        dependsOn("spotlessApply")

        options.compilerArgs.addAll(
            listOf(
                "-parameters",
                "-nowarn",
                "-Xlint:-unchecked",
                "-Xlint:-deprecation",
                "-Xlint:-processing"
            )
        )
        options.isFork = true
    }

    withType<RunServer>().configureEach {
        minecraftVersion("26.1.2")
        runDirectory(rootProject.projectDir.resolve("run"))

        val file = rootProject.projectDir.resolve("run/server.jar")
        if (file.exists()) serverJar(file)
    }
}
