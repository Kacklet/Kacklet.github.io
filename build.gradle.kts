import org.gradle.kotlin.dsl.main
import org.gradle.kotlin.dsl.sourceSets

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
    dependencies {}
}

plugins {
    idea
    java
    application
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.kyori.blossom") version "1.3.1"
    id("me.chrommob.skidfuscatorgradle") version "master-SNAPSHOT"
}



val mod_name: String by project
val mod_id: String by project
val version: String by project
val licensed: String by project
val version_number: String by project
val archiveBaseName: String by project

// Toolchains:
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

blossom {
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
    replaceToken("@VER@", version)
    replaceToken("@VER_NUM@", version_number)
    replaceToken("@LICENSED@", licensed)
}


// Minecraft configuration:
loom {
    log4jConfigs.from(file(".gradle/loom-cache/log4j.xml"))
    launchConfigs {
        "client" {
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
            //arg("--mixin", "mixins.${mod_id}.json")
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        //mixinConfig("mixins.${mod_id}.json")
    }
    mixin {
        //defaultRefmapName.set("mixins.${mod_id}.refmap.json")
    }
}

sourceSets.main {
    output.setResourcesDir(file("$buildDir/classes/java/main"))
}

tasks.withType(JavaCompile::class) {
    options.encoding = "windows-1252"
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.sk1er.club/repository/maven-public/")
    maven("https://repo.sk1er.club/repository/maven-releases/")
    maven("https://jitpack.io")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://repo.nea.moe/releases")
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val exportLib: Configuration by configurations.creating {
    configurations.compileOnly.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-14.25.1.1349-1.8.9")
    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.8.9")
    shadowImpl("com.github.jagrosh:DiscordIPC:a8d6631")
    shadowImpl("moe.nea:libautoupdate:1.2.0")
    shadowImpl("org.reflections:reflections:0.10.2")
    shadowImpl("gg.essential:loader-launchwrapper:1.2.1")
    implementation(files("assets/libs/essential.jar"))
    compileOnly(libs.mixin)
}

tasks.withType(JavaCompile::class) {
    options.encoding = "windows-1252"
}

tasks.withType(Jar::class) {
    manifest {
        attributes(mapOf(
                "FMLCorePluginContainsFMLMod" to "true",
                "ForceLoadAsMod" to "true",
                "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
                "MixinConfigs" to "mixins.${mod_id}.json"
        ))
    }
}


tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("id", mod_id)
    inputs.property("name", mod_name)

    filesMatching(listOf("mcmod.info", "mixins.${mod_id}.json")) {
        expand(inputs.properties)
    }
}

val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.jar {
    archiveClassifier.set("without-deps")
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.shadowJar {
    destinationDirectory.set(layout.buildDirectory.dir("badjars"))
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Copying jars into mod: ${it.files}")
        }
    }
}

tasks.assemble.get().dependsOn(tasks.remapJar)
project.setProperty("mainClassName", "lol.magmaclient.Magma")

tasks.wrapper {
    gradleVersion ="7.3"
}


tasks.register("printExportLibFiles") {
    doLast {
        exportLib.files.forEach { file ->
            println("File in exportLib: ${file.absolutePath}")
        }
    }
}