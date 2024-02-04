import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    id("java")
    id("idea")
    id("xyz.jpenilla.run-paper") version "2.2.0"
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("io.papermc.paperweight.userdev") version "1.5.11"
    id("org.ajoberstar.grgit") version "5.2.1"
    id("net.kyori.blossom") version "2.1.0"
}

group = "me.denarydev"
version = "1.20.2-v1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://the-planet.fun/repo/public/")
}

dependencies {
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT")

    val crystalVersion = "2.1.0"
    library("me.denarydev.crystal.paper:utils:$crystalVersion")
    library("me.denarydev.crystal.paper:serializers:$crystalVersion")
    library("me.denarydev.crystal.shared:config:$crystalVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

paper {
    main = "me.denarydev.regionmobs.RegionMobsPlugin"
    loader = "me.denarydev.regionmobs.loader.PluginLibrariesLoader"

    generateLibrariesJson = true

    apiVersion = "1.20"

    authors = listOf("DenaryDev")

    defaultPermission = BukkitPluginDescription.Permission.Default.OP

    permissions {
        register("regionmobs.admin") {
            description = "Admin permission"
        }
    }
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", rootProject.version.toString())
                property("build_time", System.currentTimeMillis().toString())
                property("git_branch", grgit.branch.current().name)
                property("git_commit", shortCommit())
            }
        }
    }
}

fun shortCommit(): String {
    val clean = grgit.status().isClean
    val commit = grgit.head().abbreviatedId
    return commit + (if (clean) "" else "-dirty")
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

    assemble {
        dependsOn(reobfJar)
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
            url("https://download.luckperms.net/1529/bukkit/loader/LuckPerms-Bukkit-5.4.116.jar")
            url("https://ci.athion.net/job/FastAsyncWorldEdit/637/artifact/artifacts/FastAsyncWorldEdit-Bukkit-2.8.4.jar")
            url("https://ci.lucko.me/job/spark/400/artifact/spark-bukkit/build/libs/spark-1.10.59-bukkit.jar")
        }
    }
}
