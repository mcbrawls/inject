import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.21"
    id("fabric-loom") version "1.7.1"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

val modId = project.property("mod_id") as String

base {
    archivesName.set(modId)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

repositories {
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    testImplementation(sourceSets.main.get().output)
}

sourceSets {
    val main = main.get()

    test {
        compileClasspath += main.compileClasspath
        runtimeClasspath += main.runtimeClasspath
    }
}

loom {
    splitEnvironmentSourceSets()

    runs {
        create("test") {
            inherit(runs["server"])
            name("Minecraft Server (Test)")
            source(sourceSets.test.get())
            ideConfigGenerated(true)
        }
    }

    mods {
        register(modId) {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = modId
            from(components["java"])
        }
    }

    repositories {
        val mavenUser = runCatching { System.getenv("MAVEN_USERNAME_ANDANTE") }.getOrNull()
        val mavenPass = runCatching { System.getenv("MAVEN_PASSWORD_ANDANTE") }.getOrNull()

        if (mavenUser == null || mavenPass == null) return@repositories

        maven {
            name = "Andante"
            url = uri("https://maven.andante.dev/releases/")

            credentials {
                username = mavenUser
                password = mavenPass
            }
        }
    }
}
