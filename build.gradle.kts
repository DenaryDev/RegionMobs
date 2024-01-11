import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    // Uncomment if you need Kotlin
    //kotlin("jvm") version "1.9.22"
    id("xyz.jpenilla.run-paper") version "2.2.0"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    // Uncomment if you need NMS
    //id("io.papermc.paperweight.userdev") version "1.5.11"
}

group = "me.example"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://the-planet.fun/repo/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    // Uncomment if you need NMS
    //paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")

    // Uncomment if you need Kotlin
    //library(kotlin("stdlib"))

    val crystalVersion = "2.1.0"
    library("me.denarydev.crystal.paper:utils:$crystalVersion")
    library("me.denarydev.crystal.shared:config:$crystalVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Uncomment if you need Kotlin
//kotlin {
//    jvmToolchain(17)
//}

paper {
    // Default values can be overridden if needed
    // name = "TestPlugin"
    // version = "1.0"
    // description = "This is a test plugin"
    // website = "https://example.com"
    // author = "Notch"

    // Plugin main class (required)
    main = "me.example.testplugin.TestPlugin"

    // Plugin bootstrapper/loader (optional)
    bootstrapper = "me.example.testplugin.bootstrap.TestPluginBootstrap"
    loader = "me.example.testplugin.loader.PluginLibrariesLoader" // Libraries loader SHOULD be written in Java, not Kotlin
    hasOpenClassloader = false

    // Generate paper-libraries.json from `library` and `paperLibrary` in `dependencies`
    generateLibrariesJson = true

    // Mark plugin for supporting Folia
    foliaSupported = true

    // API version (Needs to be 1.19 or higher)
    apiVersion = "1.20"

    // Other possible properties from plugin.yml (optional)
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP // or POSTWORLD
    authors = listOf("Notch", "Notch2")

    prefix = "TEST"
    defaultPermission = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
    provides = listOf("TestPluginOldName", "TestPlug")

    bootstrapDependencies {
        // Required dependency during bootstrap
        register("WorldEdit")

        // During bootstrap, load BeforePlugin's bootstrap code before ours
        register("BeforePlugin") {
            required = false
            load = net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        // During bootstrap, load AfterPlugin's bootstrap code after ours
        register("AfterPlugin") {
            required = false
            load = net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder.AFTER
        }
    }

    serverDependencies {
        // During server run time, require LuckPerms, add it to the classpath, and load it before us
        register("LuckPerms") {
            load = net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        // During server run time, require WorldEdit, add it to the classpath, and load it before us
        register("WorldEdit") {
            load = net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        // Optional dependency, add it to classpath if it is available
        register("ProtocolLib") {
            required = false
        }

        // During server run time, optionally depend on Essentials but do not add it to the classpath
        register("Essentials") {
            required = false
            joinClasspath = false
        }
    }

    permissions {
        register("testplugin.*") {
            children = listOf("testplugin.test") // Defaults permissions to true
            // You can also specify the values of the permissions
            childrenMap = mapOf("testplugin.test" to true)
        }
        register("testplugin.test") {
            description = "Allows you to run the test command"
            default = BukkitPluginDescription.Permission.Default.OP // TRUE, FALSE, OP or NOT_OP
        }
    }
}

runPaper {
    folia {
        registerTask()
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
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

    // Uncomment if you need Kotlin
    //compileKotlin {
    //    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    //}

    // Uncomment if you need NMS
    //build {
    //    dependsOn(reobfJar)
    //}

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    withType<RunServer>().configureEach {
        minecraftVersion("1.20.2")
        val file = projectDir.resolve("run/server.jar") // Check for a custom server.jar file
        if (file.exists()) serverJar(file)

        // See https://github.com/jpenilla/run-task/wiki/Basic-Usage#downloading-plugins for more info
        downloadPlugins {
            // Don't download these plugins on Folia because they don't support Folia.
            if (this@configureEach.name == "runServer") {
                url("https://download.luckperms.net/1529/bukkit/loader/LuckPerms-Bukkit-5.4.116.jar")
                url("https://repo.extendedclip.com/content/repositories/placeholderapi/me/clip/placeholderapi/2.11.5/placeholderapi-2.11.5.jar")
            }
        }
    }
}
