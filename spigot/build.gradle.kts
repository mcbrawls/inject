plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
}

java {
    withSourcesJar()
    withJavadocJar()
}

base {
    archivesName.set("${rootProject.name}-${project.name}")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.REOBF_PRODUCTION

fun prop(name: String) = project.rootProject.property(name) as String

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

tasks.assemble {
    dependsOn(tasks.reobfJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])

            pom {
                name = "Inject (Spigot/Paper)"
                description = "A library for making injecting into Netty easier."
                url = "https://mcbrawls.net"

                licenses {
                    license {
                        name = "GPL-3.0"
                        url = "https://opensource.org/license/gpl-3-0"
                        distribution = "repo"
                    }

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
