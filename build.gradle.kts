import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.2.0"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
}

group = "me.rafaelka"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://the-planet.fun/repo/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

    val crystalVersion = "2.1.0"
    library("me.denarydev.crystal.paper:utils:$crystalVersion")
    library("me.denarydev.crystal.shared:config:$crystalVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

paper {
    main = "me.rafaelka.regionmobs.RegionMobsPlugin"
    loader = "me.rafaelka.regionmobs.loader.PluginLibrariesLoader"

    generateLibrariesJson = true

    foliaSupported = true

    apiVersion = "1.20"

    authors = listOf("RafaelkaUwU")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP

    serverDependencies {
        register("WorldGuard") {
            load = net.minecrell.pluginyml.paper.PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

    permissions {
        register("regionmobs.admin") {
            children = listOf(
                "regionmobs.about",
                "regionmobs.reload",
            )
        }
        register("regionmobs.about") {
            description = "Allows you to reload plugin"
        }
        register("regionmobs.reload") {
            description = "Allows you to reload plugin"
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
