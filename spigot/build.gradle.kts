import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

fun prop(name: String) = project.rootProject.property(name) as String

val isPublishing = gradle.startParameter.taskNames.any { it.contains("publish") }

group = prop("group")
version = prop("version")

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

sourceSets {
    create("example") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val exampleImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
    isCanBeResolved = true
}

val exampleCompileOnly by configurations.getting {
    extendsFrom(configurations.compileOnly.get())
}

dependencies {
    implementation(project(":api"))

    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("io.netty:netty-all:4.0.23.Final")

    exampleImplementation(project(":api"))
}

tasks {
    val shadowJarExample by creating(ShadowJar::class) {
        archiveClassifier.set("example")
        from(sourceSets["main"].output)
        from(sourceSets["example"].output) // Include the example source set
        from(project(":api").sourceSets.main.get().output) // Cursed. But it works (I spent 2 hours on this)
        from(project(":http").sourceSets.main.get().output)

        configurations = listOf(exampleImplementation) // Include example dependencies
        mergeServiceFiles() // Optional: If you need to merge service files
        group = "build"
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("plugin.yml") {
        if (isPublishing) {
            exclude()
        } else {
            expand("version" to project.version)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])

            pom {
                name = "Inject (Spigot)"
                description = "A library for making injecting into Netty easier."
                url = "https://mcbrawls.net"

                licenses {
                    license {
                        name = "MIT"
                        url = "https://opensource.org/licenses/MIT"
                        distribution = "repo"
                    }
                }
            }
        }
    }

    repositories {
        runCatching { // getenv throws if variable doesn't exist
            val mavenUser = System.getenv("MAVEN_USERNAME_ANDANTE")
            val mavenPass = System.getenv("MAVEN_PASSWORD_ANDANTE")

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
}
