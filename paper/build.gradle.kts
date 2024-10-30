plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.7.4"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

fun prop(name: String) = project.rootProject.property(name) as String

group = prop("group")
version = prop("version")

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
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
    val version = prop("minecraft_version") + "-R0.1-SNAPSHOT"

    implementation(project(":api"))
    paperweight.paperDevBundle(version)

    exampleCompileOnly("io.papermc.paper:paper-api:$version")
    exampleImplementation(project(":api"))
}

tasks {
    val jarExample by creating(Jar::class) {
        archiveClassifier.set("example")
        from(sourceSets["main"].output)
        from(sourceSets["example"].output)
        from(project(":api").sourceSets.main.get().output) // Cursed. But it works (I spent 2 hours on this)

        group = "build"
    }

    task("runExample") {
        dependsOn(runDevBundleServer)
        dependsOn(jarExample)

        group = "run paper"
    }

    runDevBundleServer {
        pluginJars(jarExample.archiveFile)
    }
}

runPaper {
    disablePluginJarDetection()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.name
            from(components["java"])
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