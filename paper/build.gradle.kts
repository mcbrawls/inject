plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
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
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val version = prop("minecraft_version") + "-R0.1-SNAPSHOT"

    implementation(project(":api"))
    implementation(project(":http"))

    paperweight.paperDevBundle(version)
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
                name = "Inject (Paper)"
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
